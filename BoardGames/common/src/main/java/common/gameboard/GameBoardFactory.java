package common.gameboard;

import common.GameFullData;
import common.exceptions.GeneralErrorException;
import static common.GameTypes.BATTLESHIP;
import static common.GameTypes.CONNECT_4;
public class GameBoardFactory
{
    public static IGameBoard createGameBoard(GameFullData gameFullData) throws GeneralErrorException
    {
        switch (gameFullData.getGameTypeDetails().getGameName())
        {
            case CONNECT_4:
            {
                return new Connect4Board(gameFullData.getUserInGameList());
            }
            case BATTLESHIP:
            {
                return new BattleshipBoard();
            }
            default:
                throw new GeneralErrorException();
        }
    }
}
