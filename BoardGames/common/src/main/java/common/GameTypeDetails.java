package common;

import javax.json.bind.annotation.JsonbProperty;

public class GameTypeDetails
{
    @JsonbProperty(value = "gameTypeId")
    private int gameTypeId;

    @JsonbProperty(value = "gameName")
    private String gameName;

    @JsonbProperty(value = "instructions")
    private String instructions;

    public GameTypeDetails(){}
    
    public GameTypeDetails(int gameTypeId, String gameName, String instructions)
    {
        this.gameTypeId = gameTypeId;
        this.gameName = gameName;
        this.instructions = instructions;
    }

    public int getGameTypeId() {
        return gameTypeId;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public void setGameTypeId(int gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getGameName() {
        return gameName;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getInstructions() {
        return instructions;
    }
    
    @SuppressWarnings("unused") //used by json convertor
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
