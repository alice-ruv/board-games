package common;

import common.gameboard.IGameBoard;
import java.sql.Timestamp;

public class GameUpdateData
{
    private final Integer gameId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp lastTurnTime;
    private IGameBoard gameBoard;
    private GameStatus gameStatus;
    private Integer currUserTurn;
    private Integer winnerId;

    //we call this constructor from createInstanceBeforeStart method, when second player requests joining a new game
    // we also call this ctor from createInstanceFinishing method
    private GameUpdateData(Integer gameId, GameStatus gameStatus)
    {
        this.gameId = gameId;
        this.gameStatus = gameStatus;
    }

    //we call this constructor from createInstanceStart method, after the second player joins the game
    private GameUpdateData(Integer gameId, Timestamp startTime, IGameBoard gameBoard, GameStatus gameStatus, Integer currUserTurn)
    {
        this.gameId = gameId;
        this.startTime = startTime;
        this.gameBoard = gameBoard;
        this.gameStatus = gameStatus;
        this.currUserTurn = currUserTurn;
    }

    //we call this constructor from createInstanceTurn method, on every turn
    private GameUpdateData(Integer gameId, Timestamp lastTurnTime, IGameBoard gameBoard, Integer currUserTurn)
    {
        this.gameId = gameId;
        this.lastTurnTime = lastTurnTime;
        this.gameBoard = gameBoard;
        this.currUserTurn = currUserTurn;
    }

    //we call this constructor from createInstanceEnd method, in the end of the game
    private GameUpdateData(Integer gameId, Timestamp endTime, Timestamp lastTurnTime, IGameBoard gameBoard, GameStatus gameStatus, Integer winnerId)
    {
        this.gameId = gameId;
        this.endTime = endTime;
        this.lastTurnTime = lastTurnTime;
        this.gameBoard = gameBoard;
        this.gameStatus = gameStatus;
        this.winnerId = winnerId;
    }

    public Integer getGameId() {
        return gameId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public Timestamp getLastTurnTime() {
        return lastTurnTime;
    }

    public IGameBoard getGameBoard() {
        return gameBoard;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public Integer getCurrUserTurn() {
        return currUserTurn;
    }

    public Integer getWinnerId() {
        return winnerId;
    }


    public static GameUpdateData createInstanceBeforeStart(Integer gameId, GameStatus gameStatus)
    {
        return new GameUpdateData(gameId, gameStatus);
    }

    public static GameUpdateData createInstanceStart(Integer gameId, Timestamp startTime, IGameBoard trackBoard, GameStatus gameStatus, Integer currUserTurn)
    {
        return new GameUpdateData(gameId, startTime, trackBoard, gameStatus, currUserTurn);
    }

    public static GameUpdateData createInstanceTurn(Integer gameId, Timestamp lastTurnTime, IGameBoard trackBoard, Integer currUserTurn)
    {
        return new GameUpdateData(gameId, lastTurnTime, trackBoard, currUserTurn);
    }

    public static GameUpdateData createInstanceEnd(Integer gameId, Timestamp endTime, Timestamp lastTurnTime, IGameBoard trackBoard, GameStatus gameStatus, Integer winnerId)
    {
        return new GameUpdateData(gameId, endTime, lastTurnTime, trackBoard, gameStatus, winnerId);
    }

    public static GameUpdateData createInstanceFinishing(Integer gameId)
    {
        return new GameUpdateData(gameId, GameStatus.FINISHING);
    }
}
