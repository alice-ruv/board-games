package common;

import javax.json.bind.annotation.JsonbProperty;

public class PlayerReadyRequest
{
    @JsonbProperty(value = "userId")
    private int userId;

    @JsonbProperty(value = "gameId")
    private int gameId;

    public PlayerReadyRequest(int userId, int gameId)
    {
        this.userId = userId;
        this.gameId = gameId;
    }
    @SuppressWarnings("unused") //used by json convertor
    public PlayerReadyRequest() {}

    public int getUserId() {
        return userId;
    }
    public int getGameId() {
        return gameId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

}