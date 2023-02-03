package common;

import javax.json.bind.annotation.JsonbProperty;

public class JoinGameResponse
{
    @JsonbProperty(value = "gameId")
    private int gameId;

    public JoinGameResponse(int gameId) {
        this.gameId = gameId;
    }
    @SuppressWarnings("unused") //used by json convertor
    public JoinGameResponse() {}

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }


}
