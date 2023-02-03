package com.example.boardgamesserver.managers;

import common.LogInRequest;
import common.SignUpRequest;
import common.User;
import common.exceptions.UserAlreadyExistException;
import common.exceptions.UserNotFoundException;
import common.exceptions.GeneralErrorException;

public interface IServerUserManager {
    User signUp(SignUpRequest input) throws UserAlreadyExistException, GeneralErrorException;
    User logIn(LogInRequest input) throws UserNotFoundException, GeneralErrorException;
}
