package common;

import common.gameboard.BattleshipUserBoard;
import javax.json.bind.annotation.JsonbProperty;

//only for battleship
public class UpdatePlayerSetupRequest
{
    @JsonbProperty(value = "gameId")
    private int gameId;

    @JsonbProperty(value = "userId")
    private int userId; //id of current player

    @JsonbProperty(value = "userBoard")
    private BattleshipUserBoard userBoard;

    public UpdatePlayerSetupRequest(int gameId, int userId, BattleshipUserBoard userBoard)
    {
        this.gameId = gameId;
        this.userId = userId;
        this.userBoard = userBoard;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public UpdatePlayerSetupRequest(){}

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BattleshipUserBoard getUserBoard() {
        return userBoard;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public void setUserBoard(BattleshipUserBoard userBoard) {
        this.userBoard = userBoard;
    }
}
