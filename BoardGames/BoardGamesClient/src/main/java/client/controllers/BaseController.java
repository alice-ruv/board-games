package client.controllers;

import client.ClientContext;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public abstract class BaseController implements Initializable {

    protected ClientContext clientContext;
    public void postInit(ClientContext clientContext)
    {
        this.clientContext = clientContext;
    }

    //displays general error to user in a separate window
    public void displayError(boolean isInJavaFXThread)
    {
        if (isInJavaFXThread)
        {
            displayErrorAlert();
        }
        else
        {
            Platform.runLater(this::displayErrorAlert);
        }
    }

    public void displayErrorAlert()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING,null, ButtonType.CLOSE);
        alert.setTitle("Error");
        alert.setHeaderText("Unknown error occurred.");
        alert.showAndWait();
    }
}