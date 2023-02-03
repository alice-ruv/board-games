package client;

import common.*;
import common.exceptions.GeneralErrorException;

import common.gameboard.BattleshipUserBoard;
import common.gameboard.GameBoardCell;

import java.util.List;

public interface IClientGameManager
{
    void setServerName(String serverName);
    void setGameType(String gameTypeName) throws GeneralErrorException;
    GameTypeDetails getSelectedGameTypeDetails();

    void joinGame(JoinGameRequest input) throws GeneralErrorException;

    StartGameMessage getStartGameMessage();
    GameTurnMessage getOpponentTurnMessage();
    GameSetupMessage getGameSetupMessage();
    void updatePlayerTurn(int userId, GameBoardCell cell) throws GeneralErrorException;
    void updatePlayerSetup(int userId, BattleshipUserBoard userBoard) throws GeneralErrorException;

    List<GameResult> getGameResults(int userId) throws GeneralErrorException;

    void quitGame(int userId);

    void cleanUp();

}
