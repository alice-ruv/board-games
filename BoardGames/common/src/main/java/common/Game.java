package common;

import common.gameboard.IGameBoard;
import java.sql.Timestamp;

@SuppressWarnings("unused")
public class Game
{
    private final int gameId;
    private final int gameTypeId;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp lastTurnTime;
    private IGameBoard trackBoard;
    private GameStatus gameStatus;
    private Integer currUserTurn;
    private Integer winnerId;

    public Game (int gameId, int gameType) {
        this.gameId = gameId;
        this.gameTypeId = gameType;
    }
    
    public Game(int gameId, Timestamp startTime, Timestamp endTime, Timestamp lastTurnTime, IGameBoard trackBoard,
                GameStatus gameStatus, int gameType, Integer currUserTurn, Integer winnerId) {
        this.gameId = gameId;
        this.gameTypeId = gameType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastTurnTime = lastTurnTime;
        this.trackBoard = trackBoard;
        this.gameStatus = gameStatus;
        this.currUserTurn = currUserTurn;
        this.winnerId = winnerId;
    }

    public Integer getGameId() {
        return gameId;
    }

    public Integer getGameTypeId(){
        return gameTypeId;
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

    public IGameBoard getTrackBoard() {
        return trackBoard;
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

    public void setTrackBoard(IGameBoard trackBoard) {
        this.trackBoard = trackBoard;
    }
}
