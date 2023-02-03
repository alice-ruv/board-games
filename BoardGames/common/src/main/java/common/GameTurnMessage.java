package common;

import common.gameboard.GameBoardCell;
import common.gameboard.IGameBoard;

public class GameTurnMessage extends GameMessage
{
    private static final long serialVersionUID = 5521110517891977088L;

    private final GameBoardCell gameBoardCell;

    public GameTurnMessage(GameBoardCell gameBoardCell, IGameBoard.GameBoardStatus gameBoardStatus, Integer winnerId)
    {
        super(gameBoardStatus, winnerId);
        this.gameBoardCell = gameBoardCell;
    }

    public GameBoardCell getGameBoardCell() {
        return gameBoardCell;
    }


}
