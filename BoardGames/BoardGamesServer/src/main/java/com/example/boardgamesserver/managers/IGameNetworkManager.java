package com.example.boardgamesserver.managers;

import common.GameMessage;

public interface IGameNetworkManager {
    void sendMessage(int userId, int gameId, GameMessage gameMessage);
}
