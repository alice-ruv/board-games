package common;

import javax.json.bind.annotation.JsonbProperty;

public class GameResultsRequest
{
    @JsonbProperty(value = "userId")
    int userId;

    @JsonbProperty(value = "gameTypeId")
    int gameTypeId;
    @SuppressWarnings("unused") //used by json convertor
    public GameResultsRequest() {}

    public GameResultsRequest(int userId, int gameTypeId) {
        this.userId = userId;
        this.gameTypeId = gameTypeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGameTypeId() {
        return gameTypeId;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setGameTypeId(int gameTypeId) {
        this.gameTypeId = gameTypeId;
    }
}
