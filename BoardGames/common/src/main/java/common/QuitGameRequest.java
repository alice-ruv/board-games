package common;

import javax.json.bind.annotation.JsonbProperty;

public class QuitGameRequest
{
    @JsonbProperty(value = "userId")
    int userId;

    @JsonbProperty(value = "gameId")
    int gameId;
    
    @SuppressWarnings("unused") //used by json convertor
    public QuitGameRequest() {
    }

    public QuitGameRequest(int userId, int gameId)
    {
        this.userId = userId;
        this.gameId = gameId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
