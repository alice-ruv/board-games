package client.controllers;

import common.SignUpRequest;
import common.exceptions.UserAlreadyExistException;
import common.exceptions.GeneralErrorException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

import static client.Scenes.*;

public class SignUpController extends BaseController
{
    private final int USERNAME_MAX_SIZE = 20;
    private final int DISPLAY_NAME_MAX_SIZE = 12;
    private final int PASSWORD_MIN_SIZE = 4;
    private final int PASSWORD_MAX_SIZE = 12;

    @FXML
    private Label usernameConstraintsLabel;

    @FXML
    private Label passwordConstraintsLabel;

    @FXML
    private Label displayNameConstraintsLabel;

    @FXML
    private Label wrongInputLabel;

    @FXML
    private Button submitButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField displayNameField;

    @FXML
    public void submitButtonPressed(ActionEvent ignoredEvent)
    {
        wrongInputLabel.setText(null);

        //get user input without leading and trailing spaces
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String displayName = displayNameField.getText().trim();

        //if at least on of the fields is not fulfilled
        if(username.isEmpty() || password.isEmpty() || displayName.isEmpty())
        {
            wrongInputLabel.setText("Please fulfill all the required fields");
            return;
        }

        //if user input in the given text fields is longer than expected
        if(username.length() > USERNAME_MAX_SIZE || password.length() < PASSWORD_MIN_SIZE ||
                password.length() > PASSWORD_MAX_SIZE || displayName.length() > DISPLAY_NAME_MAX_SIZE)
        {
            wrongInputLabel.setText("Input length is incorrect.");
            return;
        }

        setControlsDisable(true);

        try
        {
            clientContext.getClientUserManager().signUp(new SignUpRequest(username, password, displayName));
        }
        catch (UserAlreadyExistException e)
        {
            wrongInputLabel.setText("Username " + username + " is already exists");
            setControlsDisable(false);
            return;
        }
        catch (GeneralErrorException e)
        {
            wrongInputLabel.setText("Unknown error occurred");
            setControlsDisable(false);
            return;
        }
        catch (Exception e)
        {
            wrongInputLabel.setText(e.getMessage());
            setControlsDisable(false);
            return;
        }

        clientContext.changeScene(LOGGED_IN);
    }

    @FXML
    public void settingsButtonPressed(ActionEvent ignoredEvent){
        clientContext.changeScene(SETTINGS);
    }

    @FXML
    public void logInButtonPressed(ActionEvent ignoredEvent){
        clientContext.changeScene(LOG_IN);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        usernameConstraintsLabel.setText("Please select a username of up to " + USERNAME_MAX_SIZE + " characters:");
        passwordConstraintsLabel.setText("Please select a password of "+ PASSWORD_MIN_SIZE +" to " + PASSWORD_MAX_SIZE + " characters:");
        displayNameConstraintsLabel.setText("Please select a display name of up to " + DISPLAY_NAME_MAX_SIZE + " characters:");
    }

    private void setControlsDisable(boolean bool)
    {
        usernameField.setDisable(bool);
        passwordField.setDisable(bool);
        displayNameField.setDisable(bool);
        submitButton.setDisable(bool);
    }

}
