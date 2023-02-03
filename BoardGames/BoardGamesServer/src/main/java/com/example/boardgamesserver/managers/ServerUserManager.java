package com.example.boardgamesserver.managers;

import common.LogInRequest;
import common.SignUpRequest;
import common.User;
import common.exceptions.UserAlreadyExistException;
import common.exceptions.GeneralErrorException;
import common.exceptions.UserNotFoundException;
import com.example.boardgamesserver.db.DatabaseManager;
import com.example.boardgamesserver.db.IDatabaseManager;

public class ServerUserManager implements IServerUserManager
{

    private static final class InstanceHolder {
        static final ServerUserManager instance = new ServerUserManager();
    }

    public static ServerUserManager getInstance()
    {
        return InstanceHolder.instance;
    }
    private ServerUserManager(){}
    @Override
    public User signUp(SignUpRequest input) throws UserAlreadyExistException, GeneralErrorException
    {
        IDatabaseManager dbManager =  DatabaseManager.getInstance();

        return dbManager.createUser(input.getUsername(),input.getPassword(),input.getDisplayName());

    }

    @Override
    public User logIn(LogInRequest input) throws UserNotFoundException, GeneralErrorException
    {
        IDatabaseManager dbManager = DatabaseManager.getInstance();

        return dbManager.getUser(input.getUsername(),input.getPassword());
    }
}
