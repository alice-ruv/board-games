package client;

import common.LogInRequest;
import common.SignUpRequest;
import common.User;
import common.exceptions.UserAlreadyExistException;
import common.exceptions.GeneralErrorException;
import common.exceptions.UserNotFoundException;

public interface IClientUserManager {
   void setServerName(String serverName);

   User getUser();

   void signUp(SignUpRequest input) throws UserAlreadyExistException, GeneralErrorException;

   void logIn(LogInRequest input) throws UserNotFoundException, GeneralErrorException;

   void cleanUp();
}
