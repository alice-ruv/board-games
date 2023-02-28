package common.gameboard;

import javax.json.bind.annotation.JsonbProperty;
import java.util.*;

public class BattleshipBoard implements IGameBoard
{
    private static final long serialVersionUID = 3845845619688598624L;

    @JsonbProperty(value = "gameBoardStatus")
    private IGameBoard.GameBoardStatus gameBoardStatus;
    
    @JsonbProperty(value = "battleshipUserBoardMap")
    private Map<String /*userId*/, BattleshipUserBoard> battleshipUserBoardMap = new HashMap<>();

    public static final int ROWS = 10;  
    public static final int COLUMNS = 10;

    public Map<String/*userId*/, BattleshipUserBoard> getBattleshipUserBoardMap() {
        return battleshipUserBoardMap;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public void setBattleshipUserBoardMap(Map<String, BattleshipUserBoard> battleshipUserBoardMap) {
        this.battleshipUserBoardMap = battleshipUserBoardMap;
    }

    public BattleshipBoard() {
        gameBoardStatus = GameBoardStatus.RUNNING;
    }

    @Override
    public void updateGameBoard(int userId, GameBoardCell gameBoardCell)
    {
        BattleshipUserBoard opponentUserBoard = getOpponentUserBoard(userId);

        if (opponentUserBoard == null)
        {
            System.err.println("Opponent board wasn't found.");
            return;
        }

        if (!opponentUserBoard.isOnBoard(gameBoardCell.getRow(), gameBoardCell.getColumn()))
        {
            System.err.println("Cell is out of board bounds.");
            return;
        }

        BattleshipBoardCell cell = opponentUserBoard.getCells()[gameBoardCell.getRow()][gameBoardCell.getColumn()];
        opponentUserBoard.shoot(cell);

        if (opponentUserBoard.isGameOver())
        {   //it was the last "shot" to the targeted cell that was needed to win
            gameBoardStatus = GameBoardStatus.WIN;
        }
    }

    public BattleshipUserBoard getOpponentUserBoard(int userId)
    {
        //Get opponent board from map by comparing the map key, which represents the user id.
        //If it's an opponent board, the map key should differ from the given user id.
        Optional<BattleshipUserBoard> opponentBoard = battleshipUserBoardMap.entrySet().stream()
                .filter(e -> !String.valueOf(userId).equals(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();

        return opponentBoard.orElse(null);
    }


    @Override
    public GameBoardStatus getGameBoardStatus() {
        return gameBoardStatus;
    }

    @Override
    public boolean isSetupNeeded() {
        //Battleship requires setup of the board before the starting the game (unlike Connect 4).
        return true;
    }
}
