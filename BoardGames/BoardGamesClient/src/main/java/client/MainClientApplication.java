package client;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainClientApplication extends Application 
{
    @Override
    public void start(Stage stage)
    {
        ClientContext clientContext = new ClientContext();
        clientContext.setStage(stage);
        clientContext.changeScene(Scenes.LOG_IN);
    }

    public static void main(String[] args) {
        launch();
    }
}
