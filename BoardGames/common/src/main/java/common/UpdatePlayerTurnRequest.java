package common;

import common.gameboard.GameBoardCell;
import javax.json.bind.annotation.JsonbProperty;

public class UpdatePlayerTurnRequest
{
    @JsonbProperty(value = "gameId")
    private int gameId;
    @JsonbProperty(value = "userId")
    private int userId;
    @JsonbProperty(value = "gameBoardCell")
    private GameBoardCell gameBoardCell;

    public UpdatePlayerTurnRequest(int gameId, int userId, GameBoardCell gameBoardCell)
    {
        this.gameId = gameId;
        this.userId = userId;
        this.gameBoardCell = gameBoardCell;
    }
    @SuppressWarnings("unused") //used by json convertor
    public UpdatePlayerTurnRequest() {}

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

    public GameBoardCell getGameBoardCell() {
        return gameBoardCell;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setGameBoardCell(GameBoardCell gameBoardCell) {
        this.gameBoardCell = gameBoardCell;
    }
}
