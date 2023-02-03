package client;

import client.controllers.BaseController;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.awt.*;
import java.io.IOException;
import static common.Paths.*;
import static client.Scenes.SCENE_HEIGHT;
import static client.Scenes.SCENE_WIDTH;

public class ClientContext
{
    IClientGameManager clientGameManager;
    IClientUserManager clientUserManager;

    private Stage stage;
    private String serverName = LOCALHOST_SERVER;

    public ClientContext()
    {
        clientGameManager = new ClientGameManager();
        clientUserManager = new ClientUserManager();

        clientGameManager.setServerName(serverName);
        clientUserManager.setServerName(serverName);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public IClientGameManager getClientGameManager() {
        return clientGameManager;
    }

    public IClientUserManager getClientUserManager() {
        return clientUserManager;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {

        this.serverName = serverName;
        clientUserManager.setServerName(serverName);
        clientGameManager.setServerName(serverName);
    }

    public boolean test()
    {
        try
        {
            String url = PROTOCOL + serverName + REST_SERVER_PORT + APP_NAME + API + TEST;

            HttpResponse<String> apiResponse = Unirest.get(url)
                    .header("Content-Type", "application/json")
                    .asString();

            if (apiResponse.getStatus() == 200)
            {
                return true;
            }
        }
        catch (UnirestException ignored)
        {}

        return false;
    }

    public  void changeScene(String fxmlFile)
    {
        Parent root;
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            root = loader.load();
            BaseController controller = loader.getController();
            controller.postInit(this);  // pass the context to the next controller
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        Scenes scenes = new Scenes();
        stage.setTitle(scenes.sceneTitlesMap.get(fxmlFile));
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        stage.setScene(new Scene(root, Math.min(SCENE_WIDTH, gd.getDisplayMode().getWidth()),
                Math.min(SCENE_HEIGHT, gd.getDisplayMode().getHeight())));
        stage.show();
    }

}
