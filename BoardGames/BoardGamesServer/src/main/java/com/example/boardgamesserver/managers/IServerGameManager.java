package com.example.boardgamesserver.managers;

import common.*;
import common.exceptions.GeneralErrorException;
import java.util.List;

public interface IServerGameManager {
    List<GameTypeDetails> getGameTypesDetails() throws GeneralErrorException;

    JoinGameResponse joinGame(JoinGameRequest input) throws GeneralErrorException;

    void playerReady(PlayerReadyRequest input) throws GeneralErrorException;

    void updatePlayerTurn(UpdatePlayerTurnRequest input) throws GeneralErrorException;

    void updatePlayerSetup(UpdatePlayerSetupRequest input)throws GeneralErrorException;

    GameResultsResponse getGameResults(GameResultsRequest input)throws GeneralErrorException;

    void quitGame(QuitGameRequest input);
}

