package client.controllers;

import client.Scenes;
import common.*;
import common.exceptions.GeneralErrorException;
import client.ClientContext;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;
import static common.GameTypes.BATTLESHIP;
import static common.GameTypes.CONNECT_4;

public class JoinGameController extends BaseController
{
    @FXML
    private Label gameNameLabel;
    @FXML
    private Label displayNameLabel;
    @FXML
    private ImageView gameImg;

    private void performJoinGame()
    {
        try
        {
            GameTypeDetails gameTypeDetails = clientContext.getClientGameManager().getSelectedGameTypeDetails();
            int userId = clientContext.getClientUserManager().getUser().getUserId();
            int gameTypeId = gameTypeDetails.getGameTypeId();

            clientContext.getClientGameManager().joinGame(new JoinGameRequest(userId, gameTypeId));

            if(gameTypeDetails.getGameName().equals(BATTLESHIP))
            {   //Battleship game requires to set up the board before starting the game
                GameSetupMessage gameSetupMessage = clientContext.getClientGameManager().getGameSetupMessage();
                if (gameSetupMessage == null)
                {   //this player canceled joining game before the game starts
                    return;
                }
            }
            else
            {
                StartGameMessage startGameMessage = clientContext.getClientGameManager().getStartGameMessage();
                if (startGameMessage == null)
                {   //this player canceled joining game before the game starts
                    return;
                }
            }

            //running in javafx thread
            Platform.runLater(()->
            {
                switch (gameTypeDetails.getGameName())
                {
                    case BATTLESHIP:
                        clientContext.changeScene(Scenes.BATTLESHIP);
                        break;

                    case CONNECT_4:
                        clientContext.changeScene(Scenes.CONNECT_4);
                        break;

                    default:
                        break;
                }
            });
        }

        catch (GeneralErrorException e)
        {
            displayError(false);
        }
    }

    @FXML
    public void cancelButtonPressed(ActionEvent ignoredEvent)
    {
        clientContext.getClientGameManager().quitGame(clientContext.getClientUserManager().getUser().getUserId());
        clientContext.getClientGameManager().cleanUp();
        clientContext.changeScene(Scenes.LOGGED_IN);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        //move image
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(gameImg);
        translateTransition.setDuration(Duration.millis(3000));
        translateTransition.setCycleCount(TranslateTransition.INDEFINITE);
        translateTransition.setByX(-350);
        translateTransition.setByY(350);
        translateTransition.setAutoReverse(true);
        translateTransition.play();

        //rotateShip image
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(gameImg);
        rotateTransition.setDuration(Duration.millis(3000));
        rotateTransition.setCycleCount(TranslateTransition.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR); //make a constant rotation rate
        rotateTransition.setByAngle(360);
        rotateTransition.play();
    }

    public void postInit(ClientContext clientContext)
    {
        super.postInit(clientContext);
        displayNameLabel.setText(clientContext.getClientUserManager().getUser().getDisplayName());
        String gameTypeName = clientContext.getClientGameManager().getSelectedGameTypeDetails().getGameName();
        gameNameLabel.setText(gameTypeName);
        Image image = new Image(gameTypeName + ".png");
        gameImg.setImage(image);
        //running in new thread to not stuck the UI
        new Thread(this::performJoinGame).start();
    }
}
