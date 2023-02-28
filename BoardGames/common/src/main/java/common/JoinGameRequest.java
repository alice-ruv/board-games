package common;

import javax.json.bind.annotation.JsonbProperty;

public class JoinGameRequest
{
    @JsonbProperty(value = "userId")
    private int userId;

    @JsonbProperty(value = "gameTypeId")
    private int gameTypeId;

    public JoinGameRequest(int userId, int gameTypeId)
    {
        this.userId = userId;
        this.gameTypeId = gameTypeId;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public JoinGameRequest() {}

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
