package common;
public class UserInGame
{
    private final int userId;
    private final String displayName;
    private final int gameId;
    private final boolean isReady;

    public UserInGame(int userId, String displayName, int gameId, boolean isReady)
    {
        this.userId = userId;
        this.displayName = displayName;
        this.gameId = gameId;
        this.isReady = isReady;
    }

    public int getUserId() {
        return userId;
    }
    public String getDisplayName() {return displayName;}
    public int getGameId() {
        return gameId;
    }
    public boolean isReady() {return isReady;}
}
