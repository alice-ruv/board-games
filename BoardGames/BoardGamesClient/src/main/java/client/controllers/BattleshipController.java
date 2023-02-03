package client.controllers;

import client.BattleshipUserBoardHandler;
import client.BattleshipGameLoop;
import client.ClientContext;
import common.GameSetupMessage;
import common.GameTurnMessage;
import common.StartGameMessage;
import common.exceptions.GeneralErrorException;
import common.gameboard.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ResourceBundle;

public class BattleshipController extends GameBaseController
{
    @FXML
    private Label opponentBoardLabel;

    @FXML
    private Text setupInstructionsText;

    @FXML
    private Text shootInstructionsText;

    @FXML
    private Button doneButton;

    @FXML
    private Canvas canvas;

    @SuppressWarnings("FieldCanBeLocal")
    private BattleshipGameLoop gameLoop;

    private String opponentName;

    enum GameState
    {
        SETUP_BOARD,
        WAITING_FOR_GAME_START,
        RUNNING,
    }

    private GameState gameState;

    private BattleshipUserBoardHandler playerBoardHandler;
    private BattleshipUserBoardHandler opponentBoardHandler;
    private BattleshipShip targetShip = null;   //target ship of current event
    private BattleshipBoardCell startDragCell;
    private BattleshipBoardCell currentDragCell;

    private boolean isDragged = false;

    @FXML
    public void doneButtonPressed(ActionEvent ignoredEvent)
    {   //the player finished to set up his board
        doneButton.setDisable(true);
        setupInstructionsText.setVisible(false);
        shootInstructionsText.setVisible(true);

        gameState = GameState.WAITING_FOR_GAME_START;

        new Thread(() ->{ //non javafx thread
            try
            {
                int userId = clientContext.getClientUserManager().getUser().getUserId();
                clientContext.getClientGameManager().updatePlayerSetup(userId, playerBoardHandler.getUserBoard());
                StartGameMessage startGameMessage = clientContext.getClientGameManager().getStartGameMessage();
                if (startGameMessage == null)
                {   //player quited the game before he got start message
                    return;
                }
                if (startGameMessage.getGameBoardStatus() != IGameBoard.GameBoardStatus.RUNNING)
                {   //other player quit after setup or timeout
                    //running in javafx thread to display game result to the player
                    Platform.runLater(()->handleGameEnd(startGameMessage.getGameBoardStatus(), startGameMessage.getWinnerId()));
                    return;
                }

                BattleshipBoard board = (BattleshipBoard) startGameMessage.getGameBoard();
                this.opponentName = startGameMessage.getOpponentDisplayName();

                BattleshipUserBoard opponentBoard = board.getOpponentUserBoard(userId);

                if (opponentBoard == null)
                {
                    System.err.println("Opponent board wasn't found.");
                    return;
                }

                this.opponentBoardHandler.setUserBoard(opponentBoard);
                isYourTurn = startGameMessage.isYourTurn();
                gameState = GameState.RUNNING;

                //running in javafx thread
                Platform.runLater(()->
                {
                    playerMessageLabel.setText(isYourTurn ? "Your turn!" : "Waiting for " + opponentName + "'s turn...");
                    opponentBoardLabel.setText(opponentName + "'s board");
                });

                if (!isYourTurn)
                {
                    prepareOpponentTurn();
                }

            } catch (GeneralErrorException e) {
                throw new RuntimeException(e);
            }
        }).start();

    } //end of doneButtonPressed method

    @FXML
    public void mousePressedOnCanvas(MouseEvent event)
    {
        if(event.getButton() != MouseButton.PRIMARY)
        {
            return;
        }

        Point2D.Double eventPoint = new Point2D.Double(event.getX(), event.getY());

        //start dragging ship on setup board
        if(gameState == GameState.SETUP_BOARD && playerBoardHandler.isOnBoard(eventPoint))
        {
            BattleshipBoardCell cell = playerBoardHandler.getNearestCell(eventPoint);
            if(cell == null)
            {   //mouse pressed outside the board cell bounds
                return;
            }
            targetShip = playerBoardHandler.getShip(cell);
            if (targetShip != null)
            {
                startDragCell = cell;
                currentDragCell = cell;
            }
        }

        //shoot on enemy board
        else if (gameState == GameState.RUNNING && isYourTurn && opponentBoardHandler.isOnBoard(eventPoint))
        {
            BattleshipBoardCell cell = opponentBoardHandler.getNearestCell(eventPoint);

            if (cell == null || (cell.getState() != BattleshipBoardCell.CellState.BLANK))
            {   //player can shoot only on board BLANK cell
                return;
            }

            opponentBoardHandler.shoot(cell);

            //alternate turns
            isYourTurn = false;
            playerMessageLabel.setText("Waiting for " + opponentName + "'s turn...");

            //running in non javafx thread to send message to server (I/O), to not stuck the UI
            new Thread(() -> updatePlayerTurn(cell)).start();
        }
    }

    @FXML
    public void mouseClickedOnCanvas(MouseEvent event) //rotateShip ship on setup board
    {
        Point2D.Double eventPoint = new Point2D.Double(event.getX(),event.getY());
        if(gameState == GameState.SETUP_BOARD && playerBoardHandler.isOnBoard(eventPoint))
        {
            BattleshipBoardCell cell = playerBoardHandler.getNearestCell(eventPoint);
            if (cell == null)
            {   //mouse pressed outside the board cell bounds
                return;
            }
            targetShip = playerBoardHandler.getShip(cell);

            if(event.getButton() == MouseButton.PRIMARY && targetShip != null && !isDragged)
            {
                if(event.getClickCount() == 2)
                {   //rotateShip ship on double click
                    playerBoardHandler.rotateShip(targetShip);
                    targetShip = null;
                    currentDragCell = null;
                }
            }
        }
    }

    @FXML
    public void mouseMovedOnCanvas(MouseEvent event) //display preview of the shot to the target cell
    {
        if(gameState != GameState.RUNNING || !isYourTurn)
        {   //display shot preview only in player turn when game is running
            return;
        }
        Point2D.Double eventPoint = new Point2D.Double(event.getX(),event.getY());
        if(opponentBoardHandler.isOnBoard(eventPoint))
        {
            BattleshipBoardCell cell = opponentBoardHandler.getNearestCell(eventPoint);
            opponentBoardHandler.setPreviewCell(cell);
        }
        else
        {   //don't show shot preview in case the event point is outside the board bounds
            opponentBoardHandler.setPreviewCell(null);
        }
    }

    @FXML
    public void mouseDraggedOnCanvas(MouseEvent event) //drag the ship on setup board
    {
        if(targetShip != null)
        {
            isDragged = true;
            Point2D.Double eventPoint = new Point2D.Double(event.getX(), event.getY());
            if (playerBoardHandler.moveShip(currentDragCell, eventPoint, targetShip))
            {
                //make the ship dragged within the board cells bounds
                currentDragCell = playerBoardHandler.getNearestCell(eventPoint);
            }
        }
    }

    @FXML
    public void mouseReleasedOnCanvas(MouseEvent event) //release the ship on setup board
    {
        if(event.getButton() == MouseButton.PRIMARY && targetShip != null)
        {
            if(playerBoardHandler.isShipCollides(targetShip))
            {   //if the dragged ship collides with other ships or not placed within the board bounds,
                // we release the ship back to startDragCell (the original position).
                playerBoardHandler.moveShip(currentDragCell, startDragCell, targetShip);
            }
            targetShip = null;
            currentDragCell = null;
            isDragged = false;
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        shootInstructionsText.setVisible(false);
    }

    @Override
    public void postInit(ClientContext clientContext)
    {
        GameSetupMessage gameSetupMessage = clientContext.getClientGameManager().getGameSetupMessage();
        if (gameSetupMessage == null)
        {   //player quit before the game starts
            return;
        }

        super.postInit(clientContext);

        gameState = GameState.SETUP_BOARD;

        if (gameSetupMessage.getGameBoardStatus() != IGameBoard.GameBoardStatus.RUNNING)
        {
            handleGameEnd(gameSetupMessage.getGameBoardStatus(), gameSetupMessage.getWinnerId());
            return;
        }

        BattleshipUserBoard playerBoard = BattleshipUserBoard.createDefaultUserBoard();
        BattleshipUserBoard opponentBoard = BattleshipUserBoard.createDefaultUserBoard();

        double boardSize = canvas.getWidth()/2.5; //board size = board width = board height

        //place the boards
        this.playerBoardHandler = new BattleshipUserBoardHandler(50,canvas.getHeight() - boardSize-110,
                boardSize, boardSize, playerBoard, false);
        this.opponentBoardHandler = new BattleshipUserBoardHandler(115 + boardSize,
                canvas.getHeight() - boardSize-110, boardSize, boardSize, opponentBoard, true);

        gameLoop = new BattleshipGameLoop(canvas, this.playerBoardHandler, this.opponentBoardHandler);
        gameLoop.start();
    }


    @Override
    protected void handleOpponentTurn(GameTurnMessage lastOpponentTurn)
    {
        GameBoardCell gameBoardCell = lastOpponentTurn.getGameBoardCell();
        if(!isYourTurn) //if it's the opponent turn
        {
            if (!playerBoardHandler.getUserBoard().isOnBoard(gameBoardCell.getRow(), gameBoardCell.getColumn()))
            {
                System.err.println("Given cell is out of the board bounds.");
                isYourTurn = true;
                return;
            }
            BattleshipBoardCell cell  = playerBoardHandler.getUserBoard().getCells()[gameBoardCell.getRow()][gameBoardCell.getColumn()];
            playerBoardHandler.getUserBoard().shoot(cell);
            //When we're done handling the opponent turn, we pass the turn to the other player
            playerMessageLabel.setText("Your turn!");
            isYourTurn = true;
        }
    }

}
