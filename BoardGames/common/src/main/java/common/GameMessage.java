package common;

import common.gameboard.IGameBoard;

import java.io.Serializable;

public abstract class GameMessage implements Serializable
{
    private static final long serialVersionUID = 7318240886661070941L;

    private final IGameBoard.GameBoardStatus gameBoardStatus;
    private final Integer winnerId; //relevant only when there is a winner

    protected GameMessage(IGameBoard.GameBoardStatus gameBoardStatus, Integer winnerId)
    {
        this.gameBoardStatus = gameBoardStatus;
        this.winnerId = winnerId;
    }

    public IGameBoard.GameBoardStatus getGameBoardStatus() {
        return gameBoardStatus;
    }

    public Integer getWinnerId() {
        return winnerId;
    }
}
