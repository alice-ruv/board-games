package com.example.boardgamesserver.managers;

import com.example.boardgamesserver.db.DatabaseManager;
import com.example.boardgamesserver.db.IDatabaseManager;
import common.*;
import common.exceptions.GeneralErrorException;
import common.gameboard.BattleshipBoard;
import common.gameboard.GameBoardFactory;
import common.gameboard.IGameBoard;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerGameManager implements IServerGameManager {

    private static final long TIMEOUT =  3 * 60 * 1000; //timeout measured in milliseconds
    private static final int NUM_OF_PLAYERS = 2;

    //scheduler of a single thread pool that can schedule commands to run after a given delay or to execute periodically.
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ServerGameManager()
    {
        scheduler.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkGamesTimeout();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private static final class InstanceHolder {
        static final ServerGameManager instance = new ServerGameManager();
    }

    //get/create single instance of ServerGameManager
    public static ServerGameManager getInstance()
    {
        return InstanceHolder.instance;
    }

    private void checkGamesTimeout()
    {
        try {
            IDatabaseManager dbManager = DatabaseManager.getInstance();
            List<Integer> gameIds = dbManager.getAllRunningGames();

            for (Integer gameId : gameIds)
            {
                GameFullData gameFullData = dbManager.getGameFullData(gameId);
                if (gameFullData == null)
                {   //no game data to check
                    continue;
                }

                if (gameFullData.getGame().getGameStatus() != GameStatus.RUNNING)
                {   //games timeout will be checked only for RUNNING games
                    return;
                }

                Timestamp currTime = new Timestamp(System.currentTimeMillis());
                Timestamp lastTime = (gameFullData.getGame().getLastTurnTime() != null) ?
                        gameFullData.getGame().getLastTurnTime() : gameFullData.getGame().getStartTime();

                long timeDiff = currTime.getTime() - lastTime.getTime();

                if (timeDiff > TIMEOUT)
                {   //need to finish the game
                    //if current player turn was too long, the other player wins
                    Integer winnerId = Objects.requireNonNull(gameFullData.getUserInGameList().stream().
                            filter(user -> user.getUserId() != gameFullData.getGame().getCurrUserTurn()).findFirst().orElse(null)).getUserId();

                    GameUpdateData gameUpdateData = GameUpdateData.createInstanceEnd(gameId, currTime,
                            null, null, GameStatus.FINISHED, winnerId);

                    if (!dbManager.updateGame(gameUpdateData))
                    {   //game probably already finished
                        return;
                    }

                    IGameNetworkManager gameNetworkManager = GameNetworkManager.getInstance();

                    for (UserInGame user : gameFullData.getUserInGameList())
                    {
                        GameTurnMessage currPlayerMessage = new GameTurnMessage(
                                null, IGameBoard.GameBoardStatus.WIN, winnerId);
                        gameNetworkManager.sendMessage(user.getUserId(), gameId, currPlayerMessage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } //end of checkGamesTimeout method

    public List<GameTypeDetails> getGameTypesDetails() throws GeneralErrorException
    {
        IDatabaseManager dbManager = DatabaseManager.getInstance();
        return dbManager.getGameTypesDetails();
    }

    @Override
    public JoinGameResponse joinGame(JoinGameRequest input) throws GeneralErrorException
    {
        IDatabaseManager dbManager = DatabaseManager.getInstance();
        int gameId = dbManager.joinGame(input.getUserId(), input.getGameTypeId());
        return new JoinGameResponse(gameId);
    }

    @Override
    public void playerReady(PlayerReadyRequest input) throws GeneralErrorException
    {
        //update is_ready boolean in user_game table
        IDatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.updatePlayerReady(input.getUserId(), input.getGameId());

        //check if there are 2 players that are ready on the game_id
        GameFullData gameFullData = dbManager.getGameFullData(input.getGameId());

        if(gameFullData == null)
        {
            System.err.println("Game data is not found.");
            throw new GeneralErrorException();
        }

        if (gameFullData.getUserInGameList().stream().filter(UserInGame::isReady).count() < NUM_OF_PLAYERS)
        {   //not enough players for current game
            return;
        }

        //if there are 2 players ready on the same game_id, we start the game
        startGame(gameFullData);
    }

    @Override
    public void updatePlayerTurn(UpdatePlayerTurnRequest input) throws GeneralErrorException
    {
        IDatabaseManager dbManager = DatabaseManager.getInstance();
        GameFullData gameFullData = dbManager.getGameFullData(input.getGameId());

        if (gameFullData == null)
        {
            System.err.println("Game data is not found.");
            throw new GeneralErrorException();
        }

        if (gameFullData.getGame().getGameStatus() != GameStatus.RUNNING)
        {
            System.out.println("Game is not running.");
            return;
        }

        if (gameFullData.getGame().getCurrUserTurn() != input.getUserId())
        {   //not the same player's turn
            throw new GeneralErrorException();
        }

        IGameBoard gameBoard = gameFullData.getGame().getTrackBoard();
        gameBoard.updateGameBoard(input.getUserId(), input.getGameBoardCell());
        IGameBoard.GameBoardStatus gameBoardStatus = gameBoard.getGameBoardStatus();

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        IGameNetworkManager gameNetworkManager = GameNetworkManager.getInstance();

        //update next turn for other user
        Optional<UserInGame> user = gameFullData.getUserInGameList().stream().filter(u -> u.getUserId() != input.getUserId()).findFirst();
        int nextUserTurnId = Objects.requireNonNull(user.orElse(null)).getUserId();

        if (gameBoardStatus == IGameBoard.GameBoardStatus.RUNNING)
        {
            GameUpdateData gameUpdateData = GameUpdateData.createInstanceTurn(input.getGameId(), currentTimestamp, gameBoard, nextUserTurnId);
            if (!dbManager.updateGame(gameUpdateData))
            {   //game probably already finished
                return;
            }
            GameTurnMessage gameTurnMessage = new GameTurnMessage(input.getGameBoardCell(), gameBoardStatus, null);
            gameNetworkManager.sendMessage(nextUserTurnId, input.getGameId(), gameTurnMessage);
        }
        else
        {   //game is over
            Integer winnerId = (gameBoardStatus == IGameBoard.GameBoardStatus.WIN ? input.getUserId() : null);
            GameUpdateData gameUpdateData = GameUpdateData.createInstanceEnd(
                    input.getGameId(), currentTimestamp, currentTimestamp, gameBoard,GameStatus.FINISHED, winnerId);

            if (!dbManager.updateGame(gameUpdateData))
            {   //game probably already finished in db
                return;
            }

            //message to the player that did the final turn
            //gameBoardCell is null because this player already performed the final turn
            GameTurnMessage currentPlayerMessage = new GameTurnMessage(null, gameBoardStatus, winnerId);
            gameNetworkManager.sendMessage(input.getUserId(), input.getGameId(), currentPlayerMessage);

            //message for second player
            //gameBoardCell is not null because we inform the other player about the last move
            GameTurnMessage nextPlayerMessage = new GameTurnMessage(input.getGameBoardCell(), gameBoardStatus, winnerId);
            gameNetworkManager.sendMessage(nextUserTurnId, input.getGameId(), nextPlayerMessage);
        }
    } //end of updatePlayerTurn method

    @Override
    public void updatePlayerSetup(UpdatePlayerSetupRequest input) throws GeneralErrorException //for Battleship only
    {
        IDatabaseManager dbManager = DatabaseManager.getInstance();
        GameFullData gameFullData = dbManager.getGameFullData(input.getGameId());

        if (gameFullData == null)
        {
            System.err.println("Game data is not found.");
            throw new GeneralErrorException();
        }

        //update current player's board setup
        BattleshipBoard battleshipBoard = (BattleshipBoard)gameFullData.getGame().getTrackBoard();
        battleshipBoard.getBattleshipUserBoardMap().put(String.valueOf(input.getUserId()), input.getUserBoard());

        //update next turn for board setup for other player
        Optional<UserInGame> user = gameFullData.getUserInGameList().stream().filter(u -> u.getUserId() != input.getUserId()).findFirst();
        int nextUserTurnId = Objects.requireNonNull(user.orElse(null)).getUserId();

        GameUpdateData gameUpdateData = GameUpdateData.createInstanceTurn(
                input.getGameId(), new Timestamp(System.currentTimeMillis()), battleshipBoard, nextUserTurnId);

        if (!dbManager.updateGame(gameUpdateData))
        {   //game probably already finished
            return;
        }

        if (battleshipBoard.getBattleshipUserBoardMap().size() == NUM_OF_PLAYERS)
        {   //all the players completed board setup
            sendStartGameMessage(gameFullData);
        }
        else
        {   //send GameSetupMessage to the other player
            IGameNetworkManager gameNetworkManager = GameNetworkManager.getInstance();
            gameNetworkManager.sendMessage(nextUserTurnId, gameFullData.getGame().getGameId(), new GameSetupMessage());
        }
    }

    @Override
    public GameResultsResponse getGameResults(GameResultsRequest input) throws GeneralErrorException
    {
        IDatabaseManager dbManager = DatabaseManager.getInstance();
        List<GameResult> results =  dbManager.getUserGameResults(input.getGameTypeId(), input.getUserId());
        return new GameResultsResponse(results);
    }

    @Override
    public void quitGame(QuitGameRequest input)
    {
        try
        {
            IDatabaseManager dbManager = DatabaseManager.getInstance();

            GameFullData gameFullData = dbManager.getGameFullData(input.getGameId());
            if (gameFullData == null)
            {
                System.err.println("Game data is not found.");
                return;
            }

            if (gameFullData.getUserInGameList().stream().noneMatch(user -> user.getUserId() == input.getUserId()))
            {
                System.err.println("User that sent quit message is not a part of the game.");
                return;
            }

            GameUpdateData data = GameUpdateData.createInstanceFinishing(input.getGameId());
            if (!dbManager.updateGame(data))
            {   //game probably already finished
                return;
            }

            gameFullData = dbManager.getGameFullData(input.getGameId());

            if (gameFullData == null)
            {
                System.err.println("Game data is not found.");
                return;
            }

            if (gameFullData.getGame().getGameStatus() != GameStatus.FINISHING)
            {   //game is already finished while running this method
                return;
            }

            if (gameFullData.getUserInGameList().size() == 1)
            {   //only one user waited for current game which is not started yet
                dbManager.deleteGame(input.getGameId());
            }
            else
            {   //there is already another player connected to the game (while current played quit), so he wins
                int opponentId = Objects.requireNonNull(gameFullData.getUserInGameList().stream().filter(
                        user -> user.getUserId() != input.getUserId()).findFirst().orElse(null)).getUserId();
                Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                GameUpdateData gameUpdateData = GameUpdateData.createInstanceEnd(input.getGameId(), currentTimestamp,
                        currentTimestamp, gameFullData.getGame().getTrackBoard(), GameStatus.FINISHED, opponentId);

                if (!dbManager.updateGame(gameUpdateData, true))
                {   //game probably already finished
                    return;
                }
                //sending win message to opponent
                GameTurnMessage winnerPlayerMessage = new GameTurnMessage(null, IGameBoard.GameBoardStatus.WIN, opponentId);
                IGameNetworkManager gameNetworkManager = GameNetworkManager.getInstance();
                gameNetworkManager.sendMessage(opponentId, input.getGameId(), winnerPlayerMessage);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    } //end of quitGame method

    private void handleSetupNeeded(GameFullData gameFullData)
    {
        //send setup board request to the 1st player
        IGameNetworkManager gameNetworkManager = GameNetworkManager.getInstance();
        gameNetworkManager.sendMessage(gameFullData.getUserInGameList().get(0).getUserId(),
                gameFullData.getGame().getGameId(), new GameSetupMessage());
    }

    private void startGame(GameFullData gameFullData) throws GeneralErrorException
    {
        IDatabaseManager dbManager = DatabaseManager.getInstance();
        IGameBoard gameBoard = GameBoardFactory.createGameBoard(gameFullData);
        //we update 1st turn for the 1st user that connected to current game
        GameUpdateData data = GameUpdateData.createInstanceStart(gameFullData.getGame().getGameId(),
                new Timestamp(System.currentTimeMillis()), gameBoard, GameStatus.RUNNING,
                gameFullData.getUserInGameList().get(0).getUserId());

        if (!dbManager.updateGame(data))
        {   //game probably already finished
            return;
        }

        gameFullData.getGame().setTrackBoard(gameBoard);

        if(gameBoard.isSetupNeeded())
        {
            handleSetupNeeded(gameFullData);
        }
        else
        {   //if no setup needed, we start the game
            sendStartGameMessage(gameFullData);
        }
    }

    private void sendStartGameMessage(GameFullData gameFullData)
    {
        IGameNetworkManager gameNetworkManager = GameNetworkManager.getInstance();
        int userInGameListSize = gameFullData.getUserInGameList().size();

        for(int i = 0; i < userInGameListSize; i++)
        {
            int otherPlayerIndex = (i + 1) % userInGameListSize;
            gameNetworkManager.sendMessage(
                    gameFullData.getUserInGameList().get(i).getUserId(),
                    gameFullData.getGame().getGameId(),
                    new StartGameMessage(gameFullData.getUserInGameList().get(otherPlayerIndex).getDisplayName(),
                            i == 0, gameFullData.getGame().getTrackBoard()));
        }
    }

}