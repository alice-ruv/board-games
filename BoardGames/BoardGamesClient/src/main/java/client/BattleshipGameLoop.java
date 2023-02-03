package client;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class BattleshipGameLoop extends AnimationTimer
{
    private final Canvas canvas;
    private final BattleshipUserBoardHandler playerBoardHandler;
    private final BattleshipUserBoardHandler opponentBoardHandler;

    public BattleshipGameLoop(Canvas canvas, BattleshipUserBoardHandler playerBoardHandler, BattleshipUserBoardHandler opponentBoardHandler)
    {
        this.canvas = canvas;
        this.playerBoardHandler = playerBoardHandler;
        this.opponentBoardHandler = opponentBoardHandler;
    }

    @Override
    public void handle(long now)
    {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        playerBoardHandler.draw(context);
        opponentBoardHandler.draw(context);
  }
}

