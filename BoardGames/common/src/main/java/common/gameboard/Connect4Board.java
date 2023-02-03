package common.gameboard;

import common.UserInGame;
import common.exceptions.GeneralErrorException;

import javax.json.bind.annotation.JsonbProperty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connect4Board implements IGameBoard
{
    private static final long serialVersionUID = -803617626452853038L;

    public static final int ROWS = 6;
    public static final int COLUMNS = 7;
    public static final int MATCH = 4;

    @JsonbProperty(value = "gameBoardStatus")
    private GameBoardStatus gameBoardStatus;
    @JsonbProperty(value = "playersColorMap")
    private Map<String /*userId*/, Connect4BoardCell.CellColor> playersColorMap;
    @JsonbProperty(value = "connect4BoardCells")
    private Connect4BoardCell[][] connect4BoardCells;

    @JsonbProperty(value = "numOfUsedCells")
    private int numOfUsedCells;
    @SuppressWarnings("unused") //used by json convertor
    public Connect4Board(){}

    public Connect4Board(List<UserInGame> userInGameList)
    {
        this.connect4BoardCells = new Connect4BoardCell[ROWS][COLUMNS];
        for (int r = 0; r < ROWS; r++)
        {
            for (int c = 0; c < COLUMNS; c++)
            {
                this.connect4BoardCells[r][c] = new Connect4BoardCell(r,c);
            }
        }

        this.playersColorMap = new HashMap<>();
        //YELLOW color property belongs to the 1st player that joined to the game, which will make the 1st turn
        this.playersColorMap.put(String.valueOf(userInGameList.get(0).getUserId()), Connect4BoardCell.CellColor.YELLOW);
        //RED color property belongs to the 2nd player that joined to the game, which will make the 2nd turn
        this.playersColorMap.put(String.valueOf(userInGameList.get(1).getUserId()), Connect4BoardCell.CellColor.RED);

        gameBoardStatus = GameBoardStatus.RUNNING;
        numOfUsedCells = 0;
    }


    public Map<String, Connect4BoardCell.CellColor> getPlayersColorMap() {
        return playersColorMap;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setPlayersColorMap(Map<String, Connect4BoardCell.CellColor> playersColorMap) {
        this.playersColorMap = playersColorMap;
    }
    @SuppressWarnings("unused") //used by json convertor
    public Connect4BoardCell[][] getConnect4BoardCells() {
        return connect4BoardCells;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setConnect4BoardCells(Connect4BoardCell[][] connect4BoardCells)
    {
        this.connect4BoardCells = connect4BoardCells;
    }
    @SuppressWarnings("unused") //used by json convertor
    public int getNumOfUsedCells() {
        return numOfUsedCells;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setNumOfUsedCells(int numOfUsedCells) {
        this.numOfUsedCells = numOfUsedCells;
    }

    @Override
    public GameBoardStatus getGameBoardStatus() {
        return gameBoardStatus;
    }

    @Override
    public boolean isSetupNeeded()
    {   //Connect 4 doesn't require setup of the board before starting the game (unlike Battleship).
        return false;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setGameBoardStatus(GameBoardStatus gameBoardStatus) {
        this.gameBoardStatus = gameBoardStatus;
    }

    @Override
    public void updateGameBoard(int userId, GameBoardCell gameBoardCell) throws GeneralErrorException {

        Connect4BoardCell cell = new Connect4BoardCell(gameBoardCell.getRow(), gameBoardCell.getColumn(),
                playersColorMap.get(String.valueOf(userId)));

       if(!isLegalUpdate(userId, cell))
       {
           throw new GeneralErrorException("Not legal move in current game.");
       }

       updateGameBoardInner(cell);
    }

    private boolean isLegalUpdate(int userId, Connect4BoardCell cell)
    {
        if (playersColorMap.get(String.valueOf(userId)) != cell.getColor())
        {
             System.err.println("Unexpected color for cell in the game board.");
            return false;
        }

        int row = cell.getRow();
        int column = cell.getColumn();

        if( (0 > row) ||  (row >= ROWS) || (0 > column) ||  (column >= COLUMNS))
        {
            System.err.println("Cell is out of the game board bounds.");
            return false;
        }
        if (connect4BoardCells[row][column].getColor() != Connect4BoardCell.CellColor.TRANSPARENT)
        {
            System.err.println("Unexpected color for cell in the game board.");
            return false;
        }

        return true;
    }

    private void updateGameBoardInner(Connect4BoardCell cell)
    {
        connect4BoardCells[cell.getRow()][cell.getColumn()]= cell;
        numOfUsedCells++;
        if(checkIfWon(cell))
        {
            gameBoardStatus = GameBoardStatus.WIN;
            return;
        }

        if (numOfUsedCells == ROWS * COLUMNS)
        {
            gameBoardStatus = GameBoardStatus.DRAW;
        }
    }

    private boolean checkIfWon(Connect4BoardCell cell)
    {
        Connect4BoardCell.CellColor color = cell.getColor();
        int row = cell.getRow();
        int column = cell.getColumn();
        int numOfSeq = 1;

        //Check for a match (sequence of 4) in current row

        for(int i = column - 1; i >=0; i--)
        {
            if (connect4BoardCells[row][i].getColor() == color)
            {
                numOfSeq++;
            }
            else
            {
                break;
            }
            if (numOfSeq == MATCH)
            {
                return true;
            }
        }

        for(int i = column + 1; i < COLUMNS; i++)
        {
            if (connect4BoardCells[row][i].getColor() == color)
            {
                numOfSeq++;
            }
            else
            {
                break;
            }
            if (numOfSeq == MATCH)
            {
                return true;
            }
        }

        //Check for a match (sequence of 4) in current column
        numOfSeq = 1;

        for(int i = row + 1; i < ROWS; i++)
        {
            if (connect4BoardCells[i][column].getColor() == color)
            {
                numOfSeq++;
            }
            else
            {
                break;
            }

            if (numOfSeq == MATCH)
            {
                return true;
            }
        }


        //Check for a match (sequence of 4) in diagonal

        numOfSeq = 1;

        for(int i = row + 1, j = column -1; (i < ROWS) && (j >= 0); i++, j--)
        {
            if (connect4BoardCells[i][j].getColor() == color)
            {
                numOfSeq++;
            }
            else
            {
                break;
            }
            if (numOfSeq == MATCH)
            {
                return true;
            }
        }

        for(int i = row - 1, j = column + 1; (i >= 0) && (j < COLUMNS); i--, j++)
        {
            if (connect4BoardCells[i][j].getColor() == color)
            {
                numOfSeq++;
            }
            else
            {
                break;
            }
            if (numOfSeq == MATCH)
            {
                return true;
            }
        }

        numOfSeq = 1;
        for(int i = row + 1, j = column  + 1; (i < ROWS) && (j < COLUMNS); i++, j++)
        {
            if (connect4BoardCells[i][j].getColor() == color)
            {
                numOfSeq++;
            }
            else
            {
                break;
            }
            if (numOfSeq == MATCH)
            {
                return true;
            }
        }

        for(int i = row - 1, j = column - 1; (i >= 0) && (j >= 0); i--, j--)
        {
            if (connect4BoardCells[i][j].getColor() == color)
            {
                numOfSeq++;
            }
            else
            {
                break;
            }
            if (numOfSeq == MATCH)
            {
                return true;
            }
        }

        return false;
    }
}
