package com.example.boardgamesserver;

import common.*;
import common.exceptions.GeneralErrorException;
import com.example.boardgamesserver.managers.ServerGameManager;
import com.example.boardgamesserver.managers.IServerGameManager;
import javax.ws.rs.*;
import java.util.List;
import static common.Paths.*;

@Path(GAME)
public class GameApi 
{
    @GET
    @Path(GAME_TYPES_DETAILS)
    @Produces("application/json")
    public GameTypesDetailsResponse getGameTypesDetails()
    {
        try
        {
            IServerGameManager gameManager = ServerGameManager.getInstance();
            List<GameTypeDetails> gameTypeDetailsList = gameManager.getGameTypesDetails();
            return new GameTypesDetailsResponse(gameTypeDetailsList);
        }
        catch (GeneralErrorException e)
        {
            throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path(JOIN_GAME)
    @Produces("application/json")
    @Consumes("application/json")
    public JoinGameResponse joinGame(JoinGameRequest input) 
    {
        try
        {
            IServerGameManager gameManager = ServerGameManager.getInstance();
            return gameManager.joinGame(input);
        }
        catch (GeneralErrorException e)
        {
            throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path(PLAYER_READY)
    @Produces("application/json")
    @Consumes("application/json")
    public void playerReady(PlayerReadyRequest input)
    {
        IServerGameManager gameManager = ServerGameManager.getInstance();
        try
        {
            gameManager.playerReady(input);
        }
        catch (GeneralErrorException e)
        {
            throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path(UPDATE_PLAYER_SETUP)
    @Produces("application/json")
    @Consumes("application/json")
    public void updatePlayerSetup(UpdatePlayerSetupRequest input)
    {
        IServerGameManager gameManager = ServerGameManager.getInstance();
        try
        {
            gameManager.updatePlayerSetup(input);
        }
        catch (GeneralErrorException e)
        {
            throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path(UPDATE_PLAYER_TURN)
    @Produces("application/json")
    @Consumes("application/json")
    public void updatePlayerTurn(UpdatePlayerTurnRequest input)
    {
        IServerGameManager gameManager = ServerGameManager.getInstance();
        try
        {
            gameManager.updatePlayerTurn(input);
        }
        catch (GeneralErrorException e)
        {
            throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path(GAME_RESULTS)
    @Produces("application/json")
    @Consumes("application/json")
    public GameResultsResponse getGameResults(GameResultsRequest input)
    {
        IServerGameManager gameManager = ServerGameManager.getInstance();
        try {
            return gameManager.getGameResults(input);
        }
        catch (GeneralErrorException e)
        {
            throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path(QUIT_GAME)
    @Produces("application/json")
    @Consumes("application/json")
    public void quitGame(QuitGameRequest input)
    {
        IServerGameManager gameManager = ServerGameManager.getInstance();
        gameManager.quitGame(input);
    }

}
