package com.example.boardgamesserver;

import common.LogInRequest;
import common.SignUpRequest;
import common.User;
import static common.Paths.*;
import common.exceptions.UserAlreadyExistException;
import common.exceptions.UserNotFoundException;
import common.exceptions.GeneralErrorException;
import com.example.boardgamesserver.managers.IServerUserManager;
import com.example.boardgamesserver.managers.ServerUserManager;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path(USER)
public class UserApi {
    @POST
    @Path(SIGN_UP)
    @Produces("application/json")
    @Consumes("application/json")
    public User signUp(SignUpRequest input) {
        try
        {
            IServerUserManager userManager = ServerUserManager.getInstance();
            return userManager.signUp(input);
        }
        catch (UserAlreadyExistException e)
        {
            throw new WebApplicationException(e.getMessage(), Response.Status.CONFLICT);
        }
        catch (GeneralErrorException e)
        {
           throw new InternalServerErrorException(e);
        }
    }

    @POST
    @Path(LOG_IN)
    @Produces("application/json")
    @Consumes("application/json")
    public User logIn(LogInRequest input) {
        try
        {
            IServerUserManager userManager = ServerUserManager.getInstance();
            return userManager.logIn(input);
        }
        catch (UserNotFoundException e)
        {
            throw new NotAuthorizedException(e.getMessage());
        }
        catch (GeneralErrorException e)
        {
            throw new InternalServerErrorException(e);
        }

    }
}