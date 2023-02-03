package common;

import common.gameboard.IGameBoard;

public class StartGameMessage extends GameMessage
{
    private static final long serialVersionUID = -1083850081125121344L;
    private String opponentDisplayName;
    private boolean isYourTurn;
    private IGameBoard gameBoard;

    public StartGameMessage(String opponentDisplayName, boolean isYourTurn, IGameBoard gameBoard,
                            IGameBoard.GameBoardStatus gameBoardStatus, Integer winnerId)
    {
        super(gameBoardStatus, winnerId);
        this.opponentDisplayName = opponentDisplayName;
        this.isYourTurn = isYourTurn;
        this.gameBoard = gameBoard;
    }

    public StartGameMessage(String opponentDisplayName, boolean isYourTurn, IGameBoard gameBoard)
    {
        this(opponentDisplayName, isYourTurn, gameBoard, IGameBoard.GameBoardStatus.RUNNING, null);
    }

    public String getOpponentDisplayName() {
        return opponentDisplayName;
    }
    @SuppressWarnings("unused") //used by serializer
    public void setOpponentDisplayName(String opponentDisplayName) {
        this.opponentDisplayName = opponentDisplayName;
    }

    public boolean isYourTurn() {
        return isYourTurn;
    }
    @SuppressWarnings("unused") //used by serializer
    public void setYourTurn(boolean yourTurn) {
        isYourTurn = yourTurn;
    }

    public IGameBoard getGameBoard() {
        return gameBoard;
    }
    @SuppressWarnings("unused") //used by serializer
    public void setGameBoard(IGameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }
}
