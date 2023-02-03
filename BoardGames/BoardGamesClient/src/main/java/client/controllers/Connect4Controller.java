package client.controllers;

import client.ClientContext;
import client.Connect4Disk;
import common.*;
import common.gameboard.Connect4Board;
import common.gameboard.Connect4BoardCell;
import common.gameboard.GameBoardCell;
import common.gameboard.IGameBoard;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import java.net.URL;
import java.util.*;

public class Connect4Controller extends GameBaseController
{
    public static Map<Connect4BoardCell.CellColor, Color> colorMap = new HashMap<Connect4BoardCell.CellColor, Color>()
    {
        private static final long serialVersionUID = -8424641334350874367L;
        {
        put(Connect4BoardCell.CellColor.TRANSPARENT, Color.TRANSPARENT);
        put(Connect4BoardCell.CellColor.YELLOW, Color.YELLOW);
        put(Connect4BoardCell.CellColor.RED, Color.RED);
    }};

    private SimpleObjectProperty<Color> playerColorProperty;

    private Connect4Disk[][] disks;

    private static final double CELL_WIDTH = 90, CELL_HEIGHT = 90;
    private static final double CIRC_IN_CELL_SIZE = 40;
    private static final double DISK_SIZE = 35;

    private static final int FALL_TIME = 250;

    @FXML
    private GridPane board;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    //running in javafx thread
    private void prepareGame() 
    {
        board.getChildren().clear();
        disks = new Connect4Disk[board.getRowConstraints().size()][board.getColumnConstraints().size()];

        for(int row = 0; row < board.getRowConstraints().size(); row++)
        {
            for(int col = 0; col < board.getColumnConstraints().size(); col++)
            {
                //for every cell in the board

                Shape cell = createCell();
                Connect4Disk disk = new Connect4Disk(DISK_SIZE, row, col, playerColorProperty);

                //locate the disk to be dropped from above the 1st row
                disk.setTranslateY(-(CELL_HEIGHT * (row + 1)));
                handleOnMouseEntered(disk);
                handleOnMouseExited(disk);
                handleOnMouseClicked(disk);

                //StackPane will lay out the disk preview in front of the board cell, and the disk itself in front of its preview
                StackPane stackPane = new StackPane();
                stackPane.getChildren().addAll(cell, disk.getPreview(), disk);
                board.add(stackPane, col, row);
                disks[row][col] = disk;
            }
        }

        if (!isYourTurn)
        {
            playerMessageLabel.setText("Waiting for " + opponentName + "'s turn...");
            //running in non javafx thread to send message to server (I/O), to not stuck the UI
            new Thread(this::prepareOpponentTurn).start();
        }
        else
        {
            playerMessageLabel.setText("Your turn!");
        }
    }


    private Shape createCell()
    {
                Rectangle rect = new Rectangle(Connect4Controller.CELL_WIDTH, Connect4Controller.CELL_HEIGHT);
                Circle circ = new Circle(Connect4Controller.CIRC_IN_CELL_SIZE);

                //place the circle in the center of every grid
                circ.centerXProperty().set(Connect4Controller.CELL_WIDTH /2.0);
                circ.centerYProperty().set(Connect4Controller.CELL_HEIGHT /2.0);

                //creating the shape of every cell by subtracting a circle from rectangle
                Shape cell = Path.subtract(rect, circ);
                cell.setFill(Color.BLUE);
                cell.setStroke(Color.BLUE);
                cell.setOpacity(.9);

                DropShadow cellShadow = new DropShadow();
                cellShadow.setRadius(15);
                cellShadow.setColor(Color.DARKBLUE);
                cell.setEffect(cellShadow);

                return cell;
    }

    //make a preview before dropping the disk to the selected slot
    public void handleOnMouseEntered(Connect4Disk disk)
    {
        disk.setOnMouseEntered(event -> {
            if(isYourTurn)
            {
                disk.getPreview().setFill(playerColorProperty.get());
            }
        });

        disk.getPreview().setOnMouseEntered(event -> {
            if(isYourTurn)
            {
                disk.getPreview().setFill(playerColorProperty.get());
            }
        });
    }

    //remove the disk preview when we are out of the selected slot
    public void handleOnMouseExited(Connect4Disk disk)
    {
        disk.setOnMouseExited(event -> {
            if(isYourTurn)
            {
                disk.getPreview().setFill(Color.TRANSPARENT);
            }
        });

        disk.getPreview().setOnMouseExited(event -> {
            if(isYourTurn)
            {
                disk.getPreview().setFill(Color.TRANSPARENT);
            }
        });
    }

    //drop the disk to the selected slot
    public void handleOnMouseClicked(Connect4Disk disk)
    {
        disk.setOnMouseClicked(event -> handleOnMouseClickedInner(disk));
        disk.getPreview().setOnMouseClicked(event -> handleOnMouseClickedInner(disk));
    }

    private void handleOnMouseClickedInner(Connect4Disk disk)
    {
        if(isYourTurn && disk.getTranslateY() != 0)
        {
            isYourTurn = false;

            //make the disk fall to the proper row specified in setTranslateY
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(FALL_TIME), disk);
            translateTransition.setToY(0); //drop the disk to the lowest unused row in current column
            translateTransition.play();
            disk.fillProperty().unbind();

            //change the disks color for other player
            playerColorProperty.set((playerColorProperty.get() == Color.RED) ? Color.YELLOW : Color.RED);

            playerMessageLabel.setText("Waiting for " + opponentName + "'s turn...");
            new Thread(() -> updatePlayerTurn(new GameBoardCell(disk.getRow(), disk.getColumn()))).start();
        }
    }

    @Override
    public void postInit(ClientContext clientContext)
    {

        StartGameMessage startGameMessage = clientContext.getClientGameManager().getStartGameMessage();
        if (startGameMessage == null)
        {   //player quit before the game starts
            return;
        }

        super.postInit(clientContext);

         if (startGameMessage.getGameBoardStatus() != IGameBoard.GameBoardStatus.RUNNING)
         {
             handleGameEnd(startGameMessage.getGameBoardStatus(), startGameMessage.getWinnerId());
             return;
         }

        opponentName = startGameMessage.getOpponentDisplayName();

        Connect4Board connect4Board = (Connect4Board) startGameMessage.getGameBoard();

        Connect4BoardCell.CellColor thisPlayerColor = connect4Board.getPlayersColorMap().get(String.valueOf(this.clientContext.getClientUserManager().getUser().getUserId()));
        isYourTurn = startGameMessage.isYourTurn();

        Connect4BoardCell.CellColor firstPlayerColor = (isYourTurn) ? thisPlayerColor :
                (thisPlayerColor == Connect4BoardCell.CellColor.RED ? Connect4BoardCell.CellColor.YELLOW : Connect4BoardCell.CellColor.RED);

        playerColorProperty = new SimpleObjectProperty<>(colorMap.get(firstPlayerColor));
        opponentName = startGameMessage.getOpponentDisplayName();
        prepareGame();

    }

    @Override
    protected void handleOpponentTurn(GameTurnMessage lastOpponentTurn)
    {
        GameBoardCell cell = lastOpponentTurn.getGameBoardCell();
        Connect4Disk disk = disks[cell.getRow()][cell.getColumn()];

        if(!isYourTurn && disk.getTranslateY() != 0)
        {
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(FALL_TIME), disk);
            translateTransition.setToY(0); //drop the disk to the lowest unused row in current column
            translateTransition.play();
            disk.fillProperty().unbind();
            //change the disks color for this player
            playerColorProperty.set((playerColorProperty.get()==Color.RED) ? Color.YELLOW : Color.RED);
        }

        playerMessageLabel.setText("Your turn!");
        isYourTurn = true;
    }

}
