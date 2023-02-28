package common;

import java.util.List;

public class GameFullData
{
    private final Game game;
    private final GameTypeDetails gameTypeDetails;
    private final List<UserInGame> userInGameList;

    public GameFullData(Game game, GameTypeDetails gameTypeDetails, List<UserInGame> userInGameList)
    {
        this.game = game;
        this.gameTypeDetails = gameTypeDetails;
        this.userInGameList = userInGameList;
    }

    public Game getGame() {
        return game;
    }

    public GameTypeDetails getGameTypeDetails() {
        return gameTypeDetails;
    }

    public List<UserInGame> getUserInGameList() {
        return userInGameList;
    }
}
