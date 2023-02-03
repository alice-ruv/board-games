package client;

import java.util.*;

public class Scenes
{
    public static final double SCENE_WIDTH = 740;
    public static final double SCENE_HEIGHT = 760;

    public static final String LOG_IN = "log-in.fxml";
    public static final String SIGN_UP = "sign-up.fxml";
    public static final String SETTINGS = "settings.fxml";
    public static final String LOGGED_IN = "logged-in.fxml";
    public static final String GAME_INSTRUCTIONS = "game-instructions.fxml";
    public static final String GAME_RESULTS = "game-results.fxml";
    public static final String JOIN_GAME = "join-game.fxml";
    public static final String BATTLESHIP = "battleship.fxml";
    public static final String CONNECT_4 = "connect4.fxml";

    public Map<String /*fxml file*/, String /*title*/> sceneTitlesMap = new HashMap<>();

    public Scenes()
    {
        sceneTitlesMap.put(LOG_IN, "Log in");
        sceneTitlesMap.put(SIGN_UP, "Sign up");
        sceneTitlesMap.put(SETTINGS, "Settings");
        sceneTitlesMap.put(LOGGED_IN, "Logged in");
        sceneTitlesMap.put(GAME_INSTRUCTIONS, "Game instructions");
        sceneTitlesMap.put(GAME_RESULTS, "Game results");
        sceneTitlesMap.put(JOIN_GAME, "Join game");
        sceneTitlesMap.put(BATTLESHIP, "Battleship");
        sceneTitlesMap.put(CONNECT_4, "Connect 4");
    }

}
