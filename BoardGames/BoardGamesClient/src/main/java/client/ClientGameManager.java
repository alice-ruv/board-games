package client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;
import common.*;
import common.exceptions.GeneralErrorException;
import common.gameboard.BattleshipUserBoard;
import common.gameboard.GameBoardCell;
import common.gameboard.IGameBoard;
import static common.Paths.*;
import javax.jms.*;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.*;

public class ClientGameManager implements IClientGameManager, MessageListener
{
    private Map<String, GameTypeDetails> gameTypeDetailsMap;

    private GameTypeDetails selectedGameTypeDetails;

    private int gameId = -1;

    private ConnectionFactory connectionFactory;
    private JMSContext context;
    @SuppressWarnings("FieldCanBeLocal")
    private JMSConsumer gameConsumer;

    private static final String JMS_PORT = "7676";

    private final Object startGameMessageLock = new Object();
    private StartGameMessage startGameMessage;

    private final Object lastOpponentTurnLock = new Object();
    private GameTurnMessage opponentTurnMessage;

    private final Object gameSetupMessageLock = new Object();
    private GameSetupMessage gameSetupMessage;

    private String serverName;

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    @Override
    public void setGameType(String gameTypeName) throws GeneralErrorException
    {
        Map<String /*gameTypeName*/, GameTypeDetails> map = getGameTypeDetailsMap();
        selectedGameTypeDetails = map.get(gameTypeName);
        if (selectedGameTypeDetails == null)
        {
            throw new GeneralErrorException();
        }
    }

    @Override
    public GameTypeDetails getSelectedGameTypeDetails()
    {
        return selectedGameTypeDetails;
    }

    @Override
    public GameSetupMessage getGameSetupMessage()
    {
        synchronized (gameSetupMessageLock)
        {
            if (gameSetupMessage == null)
            {
                try
                {
                    gameSetupMessageLock.wait();
                }
                catch (InterruptedException ignored) {}
            }
            return gameSetupMessage;
        }
    }

    public void setGameSetupMessage(GameSetupMessage gameSetupMessage)
    {
        synchronized (gameSetupMessageLock)
        {
            this.gameSetupMessage = gameSetupMessage;
            gameSetupMessageLock.notify();
        }
    }

    @Override
    public StartGameMessage getStartGameMessage()
    {
        synchronized (startGameMessageLock)
        {
            if (startGameMessage == null)
            {
                try
                {
                    startGameMessageLock.wait();
                }
                catch (InterruptedException ignored) {}
            }
            return startGameMessage;
        }
    }

    public void setStartGameMessage(StartGameMessage startGameMessage)
    {
        synchronized (startGameMessageLock)
        {
            this.startGameMessage = startGameMessage;
            startGameMessageLock.notify();
        }
    }

    @Override
    public GameTurnMessage getOpponentTurnMessage()
    {
        synchronized (lastOpponentTurnLock)
        {
            if (opponentTurnMessage == null)
            {
                try
                {
                    lastOpponentTurnLock.wait();
                }
                catch (InterruptedException ignored) {}
            }
            return opponentTurnMessage;
        }
    }

    public void setOpponentTurnMessage(GameTurnMessage opponentTurnMessage, boolean forCleanUp)
    {
        synchronized (lastOpponentTurnLock)
        {
            if (!forCleanUp && this.opponentTurnMessage != null &&
                    this.opponentTurnMessage.getGameBoardStatus() != IGameBoard.GameBoardStatus.RUNNING)
            {
                //we might get a win message while it was this player turn, e.g. if the opponent player quited the game
                return;
            }
            this.opponentTurnMessage = opponentTurnMessage;
            lastOpponentTurnLock.notify();
        }
    }

    public void setOpponentTurnMessage(GameTurnMessage opponentTurnMessage)
    {
        setOpponentTurnMessage(opponentTurnMessage, false);
    }

    @Override
    public void updatePlayerTurn(int userId, GameBoardCell cell) throws GeneralErrorException
    {
        //reset to null previous opponent turn
        setOpponentTurnMessage(null);
        UpdatePlayerTurnRequest input = new UpdatePlayerTurnRequest(gameId, userId, cell);
        try
        {
            Jsonb jsonb = JsonbBuilder.create();
            HttpResponse<String> apiResponse = Unirest.post(getUrl(UPDATE_PLAYER_TURN))
                    .header("Content-Type", "application/json")
                    .body(jsonb.toJson(input))
                    .asString();

            switch (apiResponse.getStatus())
            {
                case 200:
                case 204://on void function the response is No Content success status(204)
                {
                    return;
                }
                default:
                {
                    throw new GeneralErrorException();
                }
            }
        }
        catch (UnirestException e)
        {
            throw new GeneralErrorException(e.getMessage());
        }
    }

    @Override
    public  List<GameResult> getGameResults(int userId) throws GeneralErrorException {
        try
        {
            GameResultsRequest input = new GameResultsRequest(userId, selectedGameTypeDetails.getGameTypeId());
            Jsonb jsonb = JsonbBuilder.create();
            HttpResponse<String> apiResponse = Unirest.post(getUrl(GAME_RESULTS))
                    .header("Content-Type", "application/json")
                    .body(jsonb.toJson(input))
                    .asString();

            if (apiResponse.getStatus() == 200) {
                GameResultsResponse gameResultsResponse = jsonb.fromJson(apiResponse.getRawBody(), GameResultsResponse.class);
                return gameResultsResponse.getResults();
            }

        }
        catch (UnirestException e)
        {
            e.printStackTrace();
        }
        throw new GeneralErrorException();
    }

    private Map<String, GameTypeDetails> getGameTypeDetailsMap() throws GeneralErrorException
    {
        if (gameTypeDetailsMap == null)
        {
            gameTypeDetailsMap = new HashMap<>();
            List<GameTypeDetails> gameTypeDetailsList = getGameTypesDetails();
           for(GameTypeDetails gameTypeDetails : gameTypeDetailsList)
           {
               gameTypeDetailsMap.put(gameTypeDetails.getGameName(), gameTypeDetails);
           }
        }
        return gameTypeDetailsMap;
    }

    private List<GameTypeDetails> getGameTypesDetails() throws GeneralErrorException
    {
        try
        {
            Jsonb jsonb = JsonbBuilder.create();
            HttpResponse<String> apiResponse = Unirest.get(getUrl(GAME_TYPES_DETAILS))
                    .header("Content-Type", "application/json")
                    .asString();

            if (apiResponse.getStatus() == 200) {
                GameTypesDetailsResponse gameTypesDetailsResponse = jsonb.fromJson(apiResponse.getRawBody(), GameTypesDetailsResponse.class);
                return gameTypesDetailsResponse.getGameTypeDetailsList();
            }

        }
        catch (UnirestException e)
        {
            e.printStackTrace();
        }
        throw new GeneralErrorException();
    }

    private void  initConsumer(int userId) throws GeneralErrorException
    {
        try
        {
            if (this.connectionFactory == null)
            {
                this.connectionFactory = new ConnectionFactory();
                this.connectionFactory.setProperty(ConnectionConfiguration.imqAddressList, serverName + ":" + JMS_PORT);
            }

            this.context = connectionFactory.createContext();
            this.context.setClientID("client_" + userId + java.util.UUID.randomUUID());
            String topicName = "topic" + this.gameId + "_" + userId;
            Topic topic = this.context.createTopic(topicName);

            this.gameConsumer = context.createConsumer(topic);
            this.gameConsumer.setMessageListener(this);
        }
        catch (JMSException e) {
            throw new GeneralErrorException();
        }
    }

    @Override
    public void joinGame(JoinGameRequest input) throws GeneralErrorException
    {
        try
        {
            Jsonb jsonb = JsonbBuilder.create();
            HttpResponse<String> apiResponse = Unirest.post(getUrl(JOIN_GAME))
                    .header("Content-Type", "application/json")
                    .body(jsonb.toJson(input))
                    .asString();
            if (apiResponse.getStatus() == 200) {
                JoinGameResponse output = jsonb.fromJson(apiResponse.getRawBody(), JoinGameResponse.class);
                this.gameId = output.getGameId();
                initConsumer(input.getUserId());
                playerReady(new PlayerReadyRequest(input.getUserId(), this.gameId));
                return;
            }
        }
        catch (UnirestException e)
        {
            e.printStackTrace();
        }
        throw new GeneralErrorException();
    }

    private void playerReady(PlayerReadyRequest input) throws GeneralErrorException
    {
        try
        {
            Jsonb jsonb = JsonbBuilder.create();
            HttpResponse<String> apiResponse = Unirest.post(getUrl(PLAYER_READY))
                    .header("Content-Type", "application/json")
                    .body(jsonb.toJson(input))
                    .asString();

            switch (apiResponse.getStatus())
            {
                case 200:
                case 204:   //on void function the response is No Content success status(204)
                {
                    return;
                }
                default:
                {
                    throw new GeneralErrorException();
                }
            }
        }
        catch (UnirestException e)
        {
            throw new GeneralErrorException(e.getMessage());
        }
    }

    @Override
    public void updatePlayerSetup(int userId, BattleshipUserBoard userBoard) throws GeneralErrorException
    {
        try
        {
            UpdatePlayerSetupRequest input = new UpdatePlayerSetupRequest(gameId, userId, userBoard);
            Jsonb jsonb = JsonbBuilder.create();
            HttpResponse<String> apiResponse = Unirest.post(getUrl(UPDATE_PLAYER_SETUP))
                    .header("Content-Type", "application/json")
                    .body(jsonb.toJson(input))
                    .asString();

            switch (apiResponse.getStatus())
            {
                case 200:
                case 204://on void function the response is No Content success status (204)
                {
                    return;
                }
                default:
                {
                    throw new GeneralErrorException();
                }
            }
        }
        catch (UnirestException e)
        {
            throw new GeneralErrorException(e.getMessage());
        }
    }

    @Override
    public void onMessage(Message message)
    {
        try
        {
            GameMessage m = message.getBody(GameMessage.class);
            handleGameMessage(m);
        }
        catch (JMSException e)
        {
            System.err.println("JMSException in onMessage(): " + e);
        }
    }

    private void handleGameMessage(GameMessage gameMessage)
    {
        if (gameMessage instanceof StartGameMessage)
        {
            setStartGameMessage((StartGameMessage)gameMessage);
            return;
        }
        if (gameMessage instanceof GameSetupMessage)
        {
            setGameSetupMessage((GameSetupMessage)gameMessage);
            return;
        }

        if (gameMessage instanceof GameTurnMessage)
        {
            if (gameMessage.getGameBoardStatus() != IGameBoard.GameBoardStatus.RUNNING)
            {
                setStartGameMessage(new StartGameMessage(null, false, null,
                        gameMessage.getGameBoardStatus(), gameMessage.getWinnerId()));
                setGameSetupMessage(new GameSetupMessage(gameMessage.getGameBoardStatus(), gameMessage.getWinnerId()));
            }
            setOpponentTurnMessage((GameTurnMessage)gameMessage);
            return;
        }
        System.err.println("Game message type is incorrect.");
    }

    @Override
    public void quitGame(int userId){
        try
        {
            if (gameId == -1)
            {
                //not connected to a game
                return;
            }
            QuitGameRequest input = new QuitGameRequest(userId, gameId);
            Jsonb jsonb = JsonbBuilder.create();
            Unirest.post(getUrl(QUIT_GAME))
                    .header("Content-Type", "application/json")
                    .body(jsonb.toJson(input))
                    .asString();
        }
        catch (UnirestException ignored) {}
    }

    private String getUrl(String functionName)
    {
        return PROTOCOL + serverName + REST_SERVER_PORT + APP_NAME + API + GAME + functionName;
    }

    @Override
    public void cleanUp()
    {
        if (context != null)
        {
            context.close();
            this.context = null;
        }

        selectedGameTypeDetails = null;
        gameId = -1;
        setOpponentTurnMessage(null, true);
        setStartGameMessage(null);
        setGameSetupMessage(null);
    }
}
