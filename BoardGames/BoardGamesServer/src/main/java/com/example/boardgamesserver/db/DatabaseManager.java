package com.example.boardgamesserver.db;

import common.*;
import common.exceptions.UserAlreadyExistException;
import common.exceptions.GeneralErrorException;
import common.exceptions.UserNotFoundException;
import common.gameboard.BattleshipBoard;
import common.gameboard.Connect4Board;
import common.gameboard.IGameBoard;
import org.postgresql.util.PGobject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static java.sql.Connection.TRANSACTION_SERIALIZABLE;

public class DatabaseManager implements IDatabaseManager 
{
    private static final String dbName = "gameserverdb";
    private static final String baseUrl = "jdbc:postgresql://localhost/";
    private static final String user = "postgres";
    private static final String password = "password";
    private static final String resourceName = "jdbc/gameServerDB";

    //SQLSTATE value which returned when a unique value constraint is violated
    private static final String UNIQUE_VIOLATION = "23505";


    private DatabaseManager()
    {
        init();
    }

    private static final class InstanceHolder {
        static final DatabaseManager instance = new DatabaseManager();
    }

    //get/create single instance of DatabaseManager
    public static DatabaseManager getInstance()
    {
        return InstanceHolder.instance;
    }

    public void init()
    {
        try
        {
            createDatabase();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new RuntimeException();
        }

        try (Connection conn = connect())
        {
            conn.setTransactionIsolation(TRANSACTION_SERIALIZABLE);
            initTables(conn);
            conn.commit();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    private void createDatabase() throws SQLException
    {
        try (Connection conn = DriverManager.getConnection(baseUrl + "postgres", user, password);
             Statement stmt = conn.createStatement())
        {   //the following sql statement will return true if our database has been already created in postgres db, false otherwise
            String sql = "SELECT EXISTS (SELECT 1 FROM pg_database WHERE datname = '"+ dbName + "')";
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            if(rs.getBoolean(1))
            {   //if our database has been already created
                return;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e ;
        }

        try (Connection conn = DriverManager.getConnection(baseUrl, user, password);
             Statement stmt = conn.createStatement())
        {
            String sql = "CREATE DATABASE " + dbName;
            stmt.executeUpdate(sql);
            System.out.println("Database created successfully.");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e ;
        }
    }

    public Connection connect() throws GeneralErrorException, SQLException
    {
        Connection conn = null;
        try
        {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(resourceName);
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            System.out.println("Connected to the PostgresSQL server successfully.");
            return conn;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        }
        catch (NamingException e)
        {
            System.err.println(e.getMessage());
            throw new GeneralErrorException();
        }
    }

    public void initTables(Connection conn) throws Exception
    {
        createUsersTable(conn);
        createGameTypeTable(conn);
        initGameTypes(conn);
        createGameTable(conn);
        createUserGameTable(conn);
    }

    private void createUsersTable(Connection conn) throws SQLException
    {
        try (Statement stmt = conn.createStatement())
        {
            String sql = "CREATE TABLE IF NOT EXISTS users ( " +
                    " user_id serial PRIMARY KEY, " +
                    " username VARCHAR(50) UNIQUE NOT NULL, " +
                    " password VARCHAR(50) NOT NULL, " +
                    " display_name VARCHAR(50) NOT NULL) ";
            stmt.executeUpdate(sql);
            System.out.println("createUsersTable finished successfully.");
        }
        catch (SQLException e)
        {
            conn.rollback();
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private void createGameTable(Connection conn) throws SQLException
    {
        try (Statement stmt = conn.createStatement())
        {   //the following sql statement will return true if game_status type has been already created, false otherwise
            String sql = "SELECT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'game_status')";
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            if(!rs.getBoolean(1))   //if game_status type is not created yet
            {
                String sql1 = "CREATE TYPE game_status AS ENUM (" +
                        "'WAIT_FOR_ALL_PLAYERS', 'READY_TO_START', 'RUNNING', 'FINISHING', 'FINISHED')";
                stmt.executeUpdate(sql1);
            }

            String sql2 = "CREATE TABLE IF NOT EXISTS game ( " +
                    " game_id serial PRIMARY KEY, " +
                    " start_time TIMESTAMP, " +
                    " end_time TIMESTAMP, " +
                    " last_turn_time TIMESTAMP, " +
                    " track_board JSON, " +
                    " status game_status NOT NULL DEFAULT 'WAIT_FOR_ALL_PLAYERS', " +
                    " game_type_id INT NOT NULL, " +
                    " curr_user_turn INT, " +
                    " winner_id INT, " +
                    " FOREIGN KEY (game_type_id) REFERENCES game_type(game_type_id), " +
                    " FOREIGN KEY (curr_user_turn) REFERENCES users(user_id) ," +
                    " FOREIGN KEY (winner_id) REFERENCES users(user_id) ) ";

            stmt.executeUpdate(sql2);
            System.out.println("Created game table in given database.");
        }
        catch (SQLException e)
        {
            conn.rollback();
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private void createUserGameTable(Connection conn) throws SQLException
    {
        try (Statement stmt = conn.createStatement())
        {
            String sql = "CREATE TABLE IF NOT EXISTS user_game ( " +
                    " user_id INT, " +
                    " game_id INT, " +
                    " is_ready BOOLEAN DEFAULT FALSE, " +
                    " PRIMARY KEY (user_id, game_id), " +
                    " FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                    " FOREIGN KEY (game_id) REFERENCES game(game_id) ON DELETE CASCADE) ";

            stmt.executeUpdate(sql);
            System.out.println("Created user_game table in given database.");
        }
        catch (SQLException e)
        {
            conn.rollback();
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private void createGameTypeTable(Connection conn) throws SQLException
    {
        try (Statement stmt = conn.createStatement())
        {
            String sql = "CREATE TABLE IF NOT EXISTS game_type (" +
                    " game_type_id SERIAL PRIMARY KEY, " +
                    " game_name VARCHAR(50) UNIQUE NOT NULL, " +
                    " instructions TEXT NOT NULL) ";
            stmt.executeUpdate(sql);
            System.out.println("Created game_type table in given database.");
        }
        catch (SQLException e)
        {
            conn.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    private void initGameTypes(Connection conn) throws SQLException, GeneralErrorException
    {
        createGameTypeInner(conn, GameTypes.BATTLESHIP, GameTypes.BATTLESHIP_INSTRUCTIONS);
        createGameTypeInner(conn,GameTypes.CONNECT_4, GameTypes.CONNECT_4_INSTRUCTIONS);
    }

    @Override
    public User createUser(String username, String password, String displayName) throws UserAlreadyExistException, GeneralErrorException
    {
        try (Connection conn = connect())
        {
            conn.setTransactionIsolation(TRANSACTION_SERIALIZABLE);
            User user = createUserInner(conn, username, password, displayName);
            conn.commit();
            return user;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            if (e.getSQLState().equals(UNIQUE_VIOLATION))
            {
                throw new UserAlreadyExistException(e.getMessage());
            }
            System.err.println(e.getMessage());
            throw new GeneralErrorException(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new GeneralErrorException(e.getMessage());
        }
    }

    private User createUserInner(Connection conn, String username, String password, String displayName) throws SQLException, GeneralErrorException
    {
        String sql = "INSERT INTO users (username, password, display_name) VALUES (?,?,?) RETURNING user_id;";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY))
        {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, displayName);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.first())
            {
                throw new Exception("User has not been created.");
            }

            int userId = rs.getInt(1);

            System.out.println("Created user with user_id =  " + userId);
            return new User(userId, username, password, displayName);
        }
        catch (SQLException e)
        {
            conn.rollback();
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        }
        catch (Exception e)
        {
            conn.rollback();
            e.printStackTrace();
           throw new GeneralErrorException();
        }
    }

    @Override
    public User getUser(String username, String password) throws UserNotFoundException, GeneralErrorException
    {
        String sql = "SELECT user_id, display_name FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next())
            {
                System.err.println("User does not exist.");
                throw new UserNotFoundException("User " + username + " and/or password ************ is not correct");
            }
            int userId = rs.getInt(1);
            String displayName = rs.getString(2);
            return new User(userId, username, password, displayName);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new GeneralErrorException();
        }
    }

    private void createGameTypeInner(Connection conn, String gameName, String instructions) throws SQLException, GeneralErrorException
    {
        String sql = "INSERT INTO game_type (game_name, instructions) VALUES (?,?) ON CONFLICT DO NOTHING";
        try (PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setString(1, gameName);
            pstmt.setString(2, instructions);
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            conn.rollback();
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        }
        catch (Exception e)
        {
            conn.rollback();
            e.printStackTrace();
            throw new GeneralErrorException();
        }
    }

    //returns gameId for a new game
    public int joinGame(int userId, int gameTypeId) throws GeneralErrorException
    {
        try (Connection conn = connect())
        {
            conn.setTransactionIsolation(TRANSACTION_SERIALIZABLE);
            int gameId = joinGameInner(conn, userId, gameTypeId);
            conn.commit();
            return gameId;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new GeneralErrorException();
        }
    }

    //returns gameId for a new game
    private int joinGameInner(Connection conn, int userId, int gameTypeId) throws Exception
    {
        //We start a new game only if there are two different users waiting for a game with same gameTypeId,
        //and should return to both of them the same gameId (so they can play the same game).
        //Same player can play against multiple players concurrently.
        String sql = "SELECT g.game_id FROM game g JOIN user_game u ON g.game_id = u.game_id " +
                "WHERE game_type_id = ? AND status = 'WAIT_FOR_ALL_PLAYERS' AND u.user_id <> ? LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY))
        {
            pstmt.setInt(1, gameTypeId);
            pstmt.setInt(2, userId);

            ResultSet rs = pstmt.executeQuery();
            int gameId;

            if (rs.first())   //if there is other user waiting for current game type
            {
                gameId = rs.getInt(1);
                System.out.println("Created game with game_id = " + gameId);
                if (!updateGameInner(conn, GameUpdateData.createInstanceBeforeStart(gameId, GameStatus.READY_TO_START), false))
                {
                    throw new Exception("Error in game creation.");
                }
            }
            else  //if there is no other user waiting for current game type
            {
                Game game = createGame(conn, gameTypeId);
                gameId = game.getGameId();
            }
            connectUserToGame(conn, userId, gameId);
            return gameId;
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        }
    }

    @Override
    public List<GameTypeDetails> getGameTypesDetails() throws GeneralErrorException
    {
        List<GameTypeDetails> gameTypeDetailsList = new ArrayList<>();
        try (Connection conn = connect())
        {
            String sql = "SELECT game_type_id, game_name, instructions FROM game_type";
            try (Statement stmt = conn.createStatement())
            {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next())
                {
                    GameTypeDetails gameTypeDetails = new GameTypeDetails(rs.getInt(1),
                            rs.getString(2), rs.getString(3));
                    gameTypeDetailsList.add(gameTypeDetails);
                }
            }
            return gameTypeDetailsList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralErrorException();
        }
    }

    @Override
    public List<GameResult> getUserGameResults(int gameTypeId, int userId) throws GeneralErrorException {

        String sql = "SELECT winner_id, end_time, display_name FROM game g JOIN user_game ug ON g.game_id = ug.game_id  " +
                "JOIN users u ON ug.user_id = u.user_id WHERE u.user_id <> ? AND g.game_id IN " +
                "(SELECT g.game_id FROM game g JOIN user_game u ON  g.game_id = u.game_id  " +
                "WHERE u.user_id = ? AND g.game_type_id = ? AND status = 'FINISHED')";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);    //opponent's userId
            pstmt.setInt(2, userId);    //current userId
            pstmt.setInt(3, gameTypeId);

            ResultSet rs = pstmt.executeQuery();
            List<GameResult> results = new ArrayList<>();

            while (rs.next())
            {
                Integer winnerId = (Integer) rs.getObject(1);
                Timestamp gameFinishTime = rs.getTimestamp(2);
                String opponentDisplayName = rs.getString(3);
                GameResult.Result result = (winnerId == null) ? GameResult.Result.DRAW : ((userId == winnerId) ?
                        GameResult.Result.WIN : GameResult.Result.LOSE);
                GameResult gameResult = new GameResult(result, gameTypeId, gameFinishTime.toString(), opponentDisplayName);
                results.add(gameResult);
            }
            return results;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new GeneralErrorException();
        }
    }

    @Override
    public boolean updateGame(GameUpdateData data, boolean overwriteFinishingState) throws GeneralErrorException {
        try (Connection conn = connect()) {
            if (updateGameInner(conn, data, overwriteFinishingState)) {
                conn.commit();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new GeneralErrorException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralErrorException();
        }
    }

    @Override
    public boolean updateGame(GameUpdateData data) throws GeneralErrorException {
        return updateGame(data, false);
    }

    @Override
    public void updatePlayerReady(int userId, int gameId) throws GeneralErrorException {
        String sql = "UPDATE user_game SET is_ready = TRUE WHERE user_id=? AND game_id=?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, gameId);
            pstmt.executeUpdate();
            conn.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new GeneralErrorException();
        }
    }

    private boolean updateGameInner(Connection conn, GameUpdateData data, boolean overwriteFinishingState) throws Exception
    {   //update game according to the updated game details
        String sql = "UPDATE game SET ";
        boolean needComma = false;
        if (data.getStartTime() != null) {
            sql = sql + "start_time = ? ";
            needComma = true;
        }
        if (data.getEndTime() != null) {
            sql = sql + ((needComma) ? ", end_time = ? " : "end_time = ? ");
            needComma = true;
        }
        if (data.getLastTurnTime() != null) {
            sql = sql + ((needComma) ? ", last_turn_time = ? " : "last_turn_time = ? ");
            needComma = true;
        }
        if (data.getGameBoard() != null) {
            sql = sql + ((needComma) ? ", track_board = ? " : "track_board = ? ");
            needComma = true;
        }
        if (data.getGameStatus() != null) {
            sql = sql + ((needComma) ? ", status = ? " : "status = ?::game_status ");
            needComma = true;
        }
        if (data.getCurrUserTurn() != null) {
            sql = sql + ((needComma) ? ", curr_user_turn = ? " : "curr_user_turn = ? ");
            needComma = true;
        }
        if (data.getWinnerId() != null) {
            sql = sql + ((needComma) ? ", winner_id = ? " : "winner_id = ? ");
        }
        sql = sql + ((overwriteFinishingState) ?
                "WHERE game_id = ? AND status <> 'FINISHED'" : "WHERE game_id = ? AND status <> 'FINISHING' AND status <> 'FINISHED'");

        try (PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            int index = 1;
            if (data.getStartTime() != null) {
                pstmt.setTimestamp(index, data.getStartTime());
                index++;
            }
            if (data.getEndTime() != null) {
                pstmt.setTimestamp(index, data.getEndTime());
                index++;
            }
            if (data.getLastTurnTime() != null) {
                pstmt.setTimestamp(index, data.getLastTurnTime());
                index++;
            }
            if (data.getGameBoard() != null) {
                PGobject jsonObject = new PGobject();
                jsonObject.setType("json");
                Jsonb jsonb = JsonbBuilder.create();
                String jsonString = jsonb.toJson(data.getGameBoard());
                jsonObject.setValue(jsonString);
                pstmt.setObject(index, jsonObject);
                index++;
            }
            if (data.getGameStatus() != null) {
                pstmt.setObject(index, data.getGameStatus(), java.sql.Types.OTHER);
                index++;
            }
            if (data.getCurrUserTurn() != null) {
                pstmt.setInt(index, data.getCurrUserTurn());
                index++;
            }
            if (data.getWinnerId() != null) {
                pstmt.setInt(index, data.getWinnerId());
                index++;
            }
            if (index == 1)
            {   // no game data to update
                return false;
            }
            pstmt.setInt(index, data.getGameId());

            return pstmt.executeUpdate() > 0; //returns true if there's game data that has been updated

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Game createGame(Connection conn, int gameTypeId) throws SQLException {
        String sql = "INSERT INTO game(game_type_id) VALUES (?) RETURNING game_id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY))
        {
            pstmt.setInt(1, gameTypeId);
            ResultSet rs = pstmt.executeQuery();
            rs.first();
            int gameId = rs.getInt(1);
            return new Game(gameId, gameTypeId);
        }
    }

    private void connectUserToGame(Connection conn, int userId, int gameId) throws SQLException {
        String sql = "INSERT INTO user_game(user_id, game_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, gameId);
            pstmt.executeUpdate();
        }
    }

    public GameFullData getGameFullData(int gameId) throws GeneralErrorException {
        String sql = "SELECT  g.start_time, g.end_time, g.last_turn_time, g.track_board, g.status, g.game_type_id, \n" +
                "g.curr_user_turn,g.winner_id ,t.game_name, t.instructions, ug.user_id, ug.is_ready, u.display_name \n" +
                "FROM game g JOIN game_type t ON g.game_type_id = t.game_type_id \n" +
                "JOIN user_game ug ON g.game_id = ug.game_id JOIN users u ON ug.user_id = u.user_id \n" +
                "WHERE g.game_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.first()) {
                return null;
            }

            Timestamp startTime = rs.getTimestamp(1);
            Timestamp endTime = rs.getTimestamp(2);
            Timestamp lastTurnTime = rs.getTimestamp(3);
            String gameBoardString = rs.getString(4);
            IGameBoard gameBoard = null;

            GameStatus gameStatus = GameStatus.valueOf(rs.getString(5));
            int gameType = rs.getInt(6);
            int currUserTurn = rs.getInt(7);
            int winnerId = rs.getInt(8);


            String gameName = rs.getString(9);
            String gameInstructions = rs.getString(10);

            if (gameBoardString != null) {
                Jsonb jsonb = JsonbBuilder.create();
                gameBoard = jsonb.fromJson(gameBoardString,
                        (gameName.equals(GameTypes.CONNECT_4)) ? Connect4Board.class : BattleshipBoard.class);
            }

            Game game = new Game(gameId, startTime, endTime, lastTurnTime, gameBoard, gameStatus, gameType, currUserTurn, winnerId);
            GameTypeDetails gameTypeDetails = new GameTypeDetails(gameType, gameName, gameInstructions);

            List<UserInGame> userInGameList = new ArrayList<>();

            do
            {  //set user data from the first row result
                int userId = rs.getInt(11);
                boolean isReady = rs.getBoolean(12);
                String displayName = rs.getString(13);
                UserInGame userInGame = new UserInGame(userId, displayName, gameId, isReady);
                userInGameList.add(userInGame);
            } while (rs.next());

            return new GameFullData(game, gameTypeDetails, userInGameList);

        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralErrorException();
        }
    }

    @Override
    public void deleteGame(int gameId) throws GeneralErrorException {
        String sql = "DELETE FROM game WHERE game_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, gameId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new GeneralErrorException();
        }
    }

    @Override
    public List<Integer> getAllRunningGames() throws GeneralErrorException
    {
       String sql = "SELECT game_id FROM game WHERE status = 'RUNNING'";
        List<Integer> gameIdsList = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement())
        {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                gameIdsList.add(rs.getInt(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new GeneralErrorException();
        }
        return gameIdsList;
    }

}
