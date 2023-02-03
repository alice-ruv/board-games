package client.controllers;

import client.ClientContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;
import static client.Scenes.LOG_IN;

public class SettingsController extends BaseController
{
    @FXML
    private TextField serverUrlTextField;

    @FXML
    private Label messageLabel;

    @FXML
    public void saveButtonPressed(ActionEvent ignoredEvent)
    {
        //eliminate leading and trailing spaces from serverName when passing it
        clientContext.setServerName(serverUrlTextField.getText().trim());
        messageLabel.setVisible(true);
    }

    @FXML
    public void backButtonPressed(ActionEvent ignoredEvent){
        clientContext.changeScene(LOG_IN);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageLabel.setVisible(false);
    }

    @Override
    public void postInit(ClientContext clientContext)
    {
        super.postInit(clientContext);
        serverUrlTextField.setText(clientContext.getServerName());
    }
}
