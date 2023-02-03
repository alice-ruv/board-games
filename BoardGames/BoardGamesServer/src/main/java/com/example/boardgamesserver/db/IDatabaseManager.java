package com.example.boardgamesserver.db;

import common.*;
import common.exceptions.UserAlreadyExistException;
import common.exceptions.GeneralErrorException;
import common.exceptions.UserNotFoundException;

import java.util.List;

public interface IDatabaseManager {
    User createUser(String username, String password, String displayName) throws UserAlreadyExistException, GeneralErrorException;
    User getUser(String username, String password) throws UserNotFoundException, GeneralErrorException;
    int /*gameId*/ joinGame(int userId, int gameTypeId) throws GeneralErrorException;
    List<GameTypeDetails> getGameTypesDetails() throws GeneralErrorException;
    List<GameResult> getUserGameResults(int gameTypeId, int userId) throws GeneralErrorException;
    boolean updateGame(GameUpdateData data, boolean overwriteFinishingState) throws GeneralErrorException;
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean updateGame(GameUpdateData data) throws GeneralErrorException;
    void updatePlayerReady(int userId, int gameId) throws GeneralErrorException;
    GameFullData getGameFullData(int gameId) throws GeneralErrorException;

    void deleteGame(int gameId) throws GeneralErrorException;

    List<Integer> getAllRunningGames() throws GeneralErrorException;
}

