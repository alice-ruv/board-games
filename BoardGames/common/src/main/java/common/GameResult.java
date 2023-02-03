package common;
import javax.json.bind.annotation.JsonbProperty;


public class GameResult
{
    public enum Result {WIN, LOSE, DRAW}

    @JsonbProperty(value = "result")
    private Result result;

    @JsonbProperty(value = "gameTypeId")
    private int gameTypeId;

    @JsonbProperty(value = "gameFinishTime")
    private String gameFinishTime;

    @JsonbProperty(value = "opponentDisplayName")
    private String opponentDisplayName;
    @SuppressWarnings("unused") //used by json convertor
    public GameResult() {}

    public GameResult(Result result, int gameTypeId, String gameFinishTime, String opponentDisplayName)
    {
        this.result = result;
        this.gameTypeId = gameTypeId;
        this.gameFinishTime = gameFinishTime;
        this.opponentDisplayName = opponentDisplayName;
    }

    public Result getResult() {
        return result;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setResult(Result result) {
        this.result = result;
    }
    @SuppressWarnings("unused") //used by json convertor
    public int getGameTypeId() {
        return gameTypeId;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setGameTypeId(int gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getGameFinishTime() {
        return gameFinishTime;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setGameFinishTime(String gameFinishTime) {
        this.gameFinishTime = gameFinishTime;
    }

    public String getOpponentDisplayName() {
        return opponentDisplayName;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setOpponentDisplayName(String opponentDisplayName) {
        this.opponentDisplayName = opponentDisplayName;
    }

}
