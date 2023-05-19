package client.controllers;

import common.LogInRequest;
import common.exceptions.GeneralErrorException;
import common.exceptions.UserNotFoundException;
import client.ClientContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;
import static client.Scenes.*;

public class LogInController extends BaseController
{
    @FXML
    private Label messageForUserLabel; //this label will display an error connecting to server or wrong input for log in

    @FXML
    private Button logInButton;

    @FXML
    private Button signUpButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    public void settingsButtonPressed(ActionEvent ignoredEvent)
    {
        clientContext.changeScene(SETTINGS);
    }

    @FXML
    public void logInButtonPressed(ActionEvent ignoredEvent)
    {
        //clear the label of wrong input indication to user after log in
        messageForUserLabel.setText(null);

        //get user input without leading and trailing spaces
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        //if at least on of the fields is not fulfilled
        if(username.isEmpty() || password.isEmpty())
        {
            messageForUserLabel.setText("Please fulfill all the required fields");
            return;
        }

        setControlsDisable(true);

        try
        {
            clientContext.getClientUserManager().logIn(new LogInRequest(username, password));
        }
        catch (UserNotFoundException e)
        {
            messageForUserLabel.setText("Username or password is incorrect");
            setControlsDisable(false);
            return;
        }
        catch (GeneralErrorException e)
        {
            messageForUserLabel.setText("Unknown error occurred");
            setControlsDisable(false);
            return;
        }
        clientContext.changeScene(LOGGED_IN);
    }

    @FXML
    public void signUpButtonPressed(ActionEvent ignoredEvent)
    {
        clientContext.changeScene(SIGN_UP);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void postInit(ClientContext clientContext)
    {
        super.postInit(clientContext);

        if (!clientContext.test())
        {
            setControlsDisable(true);
            signUpButton.setDisable(true);

            messageForUserLabel.setText("This application is working on remote server. Please update your server URL in Settings.");
            System.out.println("Working on remote server. Server name required.");
        }
    }

    private void setControlsDisable(boolean bool)
    {
        usernameField.setDisable(bool);
        passwordField.setDisable(bool);
        logInButton.setDisable(bool);
    }

}
