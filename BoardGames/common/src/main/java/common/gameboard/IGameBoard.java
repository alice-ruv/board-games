package common.gameboard;

import common.exceptions.GeneralErrorException;
import java.io.Serializable;

public interface IGameBoard extends Serializable
{
    enum GameBoardStatus
    {
        RUNNING,
        WIN,
        DRAW
    }

   void updateGameBoard(int userId, GameBoardCell gameBoardCell) throws GeneralErrorException;
   
   GameBoardStatus getGameBoardStatus();

   boolean isSetupNeeded();
}
