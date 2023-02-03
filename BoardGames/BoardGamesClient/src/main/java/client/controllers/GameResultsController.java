package client.controllers;

import client.ClientContext;

import common.GameResult;
import common.exceptions.GeneralErrorException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import static client.Scenes.LOGGED_IN;


public class GameResultsController extends BaseController
{
    @FXML
    private Label gameNameLabel;

    @FXML
    private Label displayNameLabel;

    @FXML
    TableView<LocalTimeGameResult> gameResultsTable;
    @FXML
    TableColumn<?,?> dateColumn;
    @FXML
    TableColumn<?,?> timeColumn;
    @FXML
    TableColumn<?,?> resultColumn;
    @FXML
    TableColumn<?,?> opponentNameColumn;

    @FXML
    public void backButtonPressed(ActionEvent ignoredEvent){
        clientContext.changeScene(LOGGED_IN);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<>("result"));
        opponentNameColumn.setCellValueFactory(new PropertyValueFactory<>("OpponentDisplayName"));
    }

    @Override
    public void postInit(ClientContext clientContext)
    {
        super.postInit(clientContext);
        gameNameLabel.setText(clientContext.getClientGameManager().getSelectedGameTypeDetails().getGameName());
        displayNameLabel.setText(clientContext.getClientUserManager().getUser().getDisplayName());
        gameResultsTable.setItems(getGameResults());
    }

    //get all game results
    public ObservableList<LocalTimeGameResult> getGameResults()
    {
        ObservableList<LocalTimeGameResult> results = FXCollections.observableArrayList();
        try
        {
            //for every game in which current user take part
            for (GameResult gameResult : this.clientContext.getClientGameManager().getGameResults(clientContext.getClientUserManager().getUser().getUserId()))
            {
                results.add(new LocalTimeGameResult(gameResult.getResult(), gameResult.getGameFinishTime(), gameResult.getOpponentDisplayName()));
            }
        }
        catch (GeneralErrorException e)
        {
            System.err.println("Failed in getting game results.");
            return null;
        }
        return results;
    }

    public static class LocalTimeGameResult    //represents a row in the results table
    {
        private final GameResult.Result result;

        private final LocalDate date;

        private final LocalTime time;

        private final String opponentDisplayName;

        public LocalTimeGameResult(GameResult.Result result, String finishTime, String opponentDisplayName)
        {
            this.result = result;
            Timestamp timestamp = Timestamp.valueOf(finishTime);
            this.date = timestamp.toLocalDateTime().toLocalDate();
            this.time = LocalTime.parse(timestamp.toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            this.opponentDisplayName = opponentDisplayName;
        }
        @SuppressWarnings("unused") //used by resultsTable
        public LocalDate getDate() {
           return date;
        }
        @SuppressWarnings("unused") //used by resultsTable
        public LocalTime getTime() {
           return time;
        }

        @SuppressWarnings("unused") //used by resultsTable
        public GameResult.Result getResult() {
            return result;
        }

        @SuppressWarnings("unused") //used by resultsTable
        public String getOpponentDisplayName() {
            return opponentDisplayName;
        }
    }

}
