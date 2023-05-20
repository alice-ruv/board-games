package client.controllers;

import client.ClientContext;
import common.GameTurnMessage;
import client.Scenes;
import common.User;
import common.exceptions.GeneralErrorException;
import common.gameboard.GameBoardCell;
import common.gameboard.IGameBoard;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Optional;

public abstract class GameBaseController extends BaseController
{
    @FXML
    protected ImageView gameResultImageView;
    @FXML
    protected Label playerMessageLabel;  //indicates the turn of current player and whether the player wins/looses the game
    @FXML
    protected Label gameNameLabel;
    @FXML
    protected Label displayNameLabel; //display name of current user

    protected String opponentName;

    protected boolean isYourTurn;

    boolean isGameFinished = false;

    @FXML
    public void quitButtonPressed(ActionEvent ignoredEvent)
    {
        if(!isGameFinished)
        {
            //If user presses Quit button before the game finished, we will let him confirm his selection before quiting the game.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, null, ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("Quit game confirmation");
            alert.setHeaderText("Are you sure that you want to quit the game?");
            Optional<ButtonType> buttonRes = alert.showAndWait();
            if(!buttonRes.isPresent() || (buttonRes.get() != ButtonType.YES))
            {
                return;
            }
            clientContext.getClientGameManager().quitGame(clientContext.getClientUserManager().getUser().getUserId());
        }

        clientContext.getClientGameManager().cleanUp();
        clientContext.changeScene(Scenes.LOGGED_IN);
    }

    protected void updatePlayerTurn(GameBoardCell cell)
    {
        int userId = clientContext.getClientUserManager().getUser().getUserId();
        try
        {
            clientContext.getClientGameManager().updatePlayerTurn(userId, cell);
            prepareOpponentTurn();
        }
        catch (GeneralErrorException e)
        {
            displayError(false);
        }
    }

    protected void prepareOpponentTurn()
    {
        GameTurnMessage lastOpponentTurn = clientContext.getClientGameManager().getOpponentTurnMessage();
        if (lastOpponentTurn == null)
        {   //player quit the game before making the first turn
            return;
        }

        Platform.runLater(()->
        {
            if (lastOpponentTurn.getGameBoardStatus() == IGameBoard.GameBoardStatus.RUNNING)
            {
                handleOpponentTurn(lastOpponentTurn);
            }
            else
            {
                handleGameEnd(lastOpponentTurn);
            }
        });
    }

    protected void handleGameEnd(IGameBoard.GameBoardStatus gameBoardStatus, Integer winnerId)
    {
        String finalMessage;
        User currentPlayer = clientContext.getClientUserManager().getUser();

        if (gameBoardStatus == IGameBoard.GameBoardStatus.DRAW)
        {
            finalMessage = "It's a draw!";
            Image image = new Image("draw.png");
            gameResultImageView.setImage(image);

        }
        else if (winnerId == currentPlayer.getUserId())
        {
            finalMessage = "You win!";
            Image image = new Image("win.png");
            gameResultImageView.setImage(image);
        }
        else
        {
            finalMessage = "You lose!";
            Image image = new Image("lose.png");
            gameResultImageView.setImage(image);
        }

        playerMessageLabel.setText(finalMessage);
        isGameFinished = true;
        isYourTurn = false;
    }

    private void handleGameEnd(GameTurnMessage finalTurn)
    {
        if (finalTurn.getGameBoardCell() != null)
        {
            //need to update last opponent turn on the board
            handleOpponentTurn(finalTurn);
        }
        handleGameEnd(finalTurn.getGameBoardStatus(), finalTurn.getWinnerId());
    }

    protected abstract void handleOpponentTurn(GameTurnMessage lastOpponentTurn);

    @Override
    public void postInit(ClientContext clientContext)
    {
        super.postInit(clientContext);
        gameNameLabel.setText(clientContext.getClientGameManager().getSelectedGameTypeDetails().getGameName());
        displayNameLabel.setText(clientContext.getClientUserManager().getUser().getDisplayName());
    }
}
