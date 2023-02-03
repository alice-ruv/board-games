package common;

import common.gameboard.IGameBoard;

public class GameSetupMessage extends GameMessage
{
    private static final long serialVersionUID = -3015599592139312760L;

    public GameSetupMessage()
    {
        this(IGameBoard.GameBoardStatus.RUNNING, null);
    }

    public GameSetupMessage(IGameBoard.GameBoardStatus gameBoardStatus, Integer winnerId)
    {
        super(gameBoardStatus, winnerId);
    }

}
