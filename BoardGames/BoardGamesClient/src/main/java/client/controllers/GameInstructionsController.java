package client.controllers;

import common.GameTypeDetails;
import client.ClientContext;
import client.Scenes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import java.net.URL;
import java.util.ResourceBundle;

public class GameInstructionsController extends BaseController
{
    @FXML
    private Label titleLabel;

    @FXML
    private Text instructionsText;

    @FXML
    private ImageView gameImg;

    @FXML
    public void backButtonPressed(ActionEvent ignoredEvent){
        clientContext.changeScene(Scenes.LOGGED_IN);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){}
    @Override
    public void postInit(ClientContext clientContext)
    {
        super.postInit(clientContext);
        GameTypeDetails gameTypeDetails = clientContext.getClientGameManager().getSelectedGameTypeDetails();
        titleLabel.setText(gameTypeDetails.getGameName() + " game instructions");

        Image gameImage = new Image(gameTypeDetails.getGameName() + ".png");
        instructionsText.setText(gameTypeDetails.getInstructions());
        gameImg.setImage(gameImage);
    }
}
