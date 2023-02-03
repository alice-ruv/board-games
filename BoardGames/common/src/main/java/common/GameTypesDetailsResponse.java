package common;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

public class GameTypesDetailsResponse
{
    @JsonbProperty(value = "gameTypeDetailsList")
    private List<GameTypeDetails> gameTypeDetailsList;

    public List<GameTypeDetails> getGameTypeDetailsList() {
        return gameTypeDetailsList;
    }
    @SuppressWarnings("unused") //used by json convertor
    public void setGameTypeDetailsList(List<GameTypeDetails> gameTypeDetailsList)
    {
        this.gameTypeDetailsList = gameTypeDetailsList;
    }
    @SuppressWarnings("unused") //used by json convertor
    public GameTypesDetailsResponse() {}
    public GameTypesDetailsResponse(List<GameTypeDetails> details)
    {
        gameTypeDetailsList = details;
    }
}
