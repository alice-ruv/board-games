package client.controllers;

import common.GameTypeDetails;
import common.exceptions.GeneralErrorException;
import client.ClientContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import java.net.URL;
import java.util.ResourceBundle;
import static common.GameTypes.BATTLESHIP;
import static common.GameTypes.CONNECT_4;
import static client.Scenes.*;

public class LoggedInController extends BaseController {

    @FXML
    public ToggleGroup gameTypeToggleGroup;
    
    @FXML
    private Label helloDisplayNameLabel;
    
    @FXML
    private RadioButton battleshipButton;

    @FXML
    private RadioButton connect4Button;

    @FXML
    private Label userChoiceLabel;

    @FXML
    private Button instructionsButton;

    @FXML
    private Button resultsButton;

    @FXML
    private Button joinGameButton;


    @FXML
    public void battleshipButtonPressed(ActionEvent ignoredEvent)
    {
        setSelectedGame(BATTLESHIP);
    }

    @FXML
    public void connect4ButtonPressed(ActionEvent ignoredEvent)
    {
        setSelectedGame(CONNECT_4);
    }

    @FXML
    public void gameInstructionsButtonPressed(ActionEvent ignoredEvent)
    {
        clientContext.changeScene(GAME_INSTRUCTIONS);
    }

    @FXML
    public void gameResultsButtonPressed(ActionEvent ignoredEvent)
    {
        clientContext.changeScene(GAME_RESULTS);
    }

    @FXML
    public void joinGameButtonPressed(ActionEvent ignoredEvent)
    {
        clientContext.changeScene(JOIN_GAME);
    }

    @FXML
    public void logOutButtonPressed(ActionEvent ignoredEvent)
    {
        clientContext.getClientUserManager().cleanUp();
        clientContext.getClientGameManager().cleanUp();
        clientContext.changeScene(LOG_IN);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        instructionsButton.setVisible(false);
        resultsButton.setVisible(false);
        joinGameButton.setVisible(false);
    }

    private void setSelectedGame(String gameName)
    {
        try
        {
            clientContext.getClientGameManager().setGameType(gameName);
        }
        catch (GeneralErrorException e)
        {
            displayError(true);
            return;
        }
        setSelectedGameOnScreen(gameName);
    }

    private void setSelectedGameOnScreen(String gameName)
    {
        userChoiceLabel.setText(gameName + " is a good choice! " + "\nPlease select one of the following options:");
        instructionsButton.setVisible(true);
        resultsButton.setVisible(true);
        joinGameButton.setVisible(true);
    }

    public void postInit(ClientContext clientContext)
    {
        try
        {
            super.postInit(clientContext);
            System.out.println(clientContext.getClientUserManager().getUser().getDisplayName() + " logged in");
            helloDisplayNameLabel.setText("Hello " + clientContext.getClientUserManager().getUser().getDisplayName() +"! Please select a game:");
            GameTypeDetails selectedGameType = clientContext.getClientGameManager().getSelectedGameTypeDetails();
            if (selectedGameType == null)
            {
                return;
            }
            setSelectedGameOnScreen(selectedGameType.getGameName());
            switch (selectedGameType.getGameName())
            {
                case BATTLESHIP:
                    battleshipButton.setSelected(true);
                    break;

                case CONNECT_4:
                    connect4Button.setSelected(true);
                    break;

                default:
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

}
