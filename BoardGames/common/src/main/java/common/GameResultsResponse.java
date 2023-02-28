package common;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

public class GameResultsResponse 
{
    @JsonbProperty(value = "results")
    private List<GameResult> results;
    
    @SuppressWarnings("unused") //used by json convertor
    public GameResultsResponse() {}

    public GameResultsResponse(List<GameResult> results) {
        this.results = results;
    }

    public List<GameResult> getResults() {
        return results;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public void setResults(List<GameResult> results) {
        this.results = results;
    }
}
