package client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import common.LogInRequest;
import common.Paths;
import common.SignUpRequest;
import common.User;
import common.exceptions.UserAlreadyExistException;
import common.exceptions.GeneralErrorException;
import common.exceptions.UserNotFoundException;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import static common.Paths.*;

public class ClientUserManager implements IClientUserManager
{
    private User user;
    private String serverName;

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public User getUser()
    {
        return user;
    }


    public void signUp(SignUpRequest input) throws UserAlreadyExistException, GeneralErrorException
    {
        try
        {
            Jsonb jsonb = JsonbBuilder.create();
            HttpResponse<String> apiResponse = Unirest.post(getUrl(SIGN_UP))
                    .header("Content-Type", "application/json")
                    .body(jsonb.toJson(input))
                    .asString();

            switch (apiResponse.getStatus())
            {
                case 200:
                {
                    this.user = jsonb.fromJson(apiResponse.getRawBody(), User.class);
                    return;
                }
                case 409:   //Conflict
                {
                    throw new UserAlreadyExistException();
                }
                default:
                {
                    throw new GeneralErrorException();
                }
            }
        }
        catch (UnirestException e)
        {
            throw new GeneralErrorException(e.getMessage());
        }
    }

    public void logIn(LogInRequest input) throws UserNotFoundException, GeneralErrorException
    {
        try
        {
            Jsonb jsonb = JsonbBuilder.create();
            HttpResponse<String> apiResponse = Unirest.post(getUrl(LOG_IN))
                    .header("Content-Type", "application/json")
                    .body(jsonb.toJson(input))
                    .asString();

            switch ( apiResponse.getStatus())
            {
                case 200:
                {
                    this.user = jsonb.fromJson(apiResponse.getRawBody(), User.class);
                    return;
                }
                case 401: //user not exist
                {
                    throw new UserNotFoundException();
                }
                default:
                {
                    throw new GeneralErrorException();
                }
            }
        }
        catch (UnirestException e)
        {
            throw new GeneralErrorException(e.getMessage());
        }
    }

    private String getUrl(String functionName)
    {
        return PROTOCOL + serverName + REST_SERVER_PORT + APP_NAME + API + USER + functionName;
    }

    @Override
    public void cleanUp()
    {
        this.user = null;
    }

}
