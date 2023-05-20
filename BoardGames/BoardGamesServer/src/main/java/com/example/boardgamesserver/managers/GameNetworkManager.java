package com.example.boardgamesserver.managers;

import common.GameMessage;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class GameNetworkManager implements IGameNetworkManager
{
    private JMSContext context;

    private GameNetworkManager()
    {
        InitialContext ctx;
        try {
            ctx = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory)ctx.lookup("java:comp/DefaultJMSConnectionFactory");
            this.context = connectionFactory.createContext();
        } 
        catch (NamingException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static final class InstanceHolder {
        static final GameNetworkManager instance = new GameNetworkManager();
    }

    //get/create single instance of GameNetworkManager
    public static GameNetworkManager getInstance()
    {
        return InstanceHolder.instance;
    }

    @Override
    public void sendMessage(int userId, int gameId, GameMessage gameMessage)
    {
        String topicName = "topic" + gameId + "_" + userId;
        Topic topic = this.context.createTopic(topicName);
        System.out.println(topicName + " created.");
        this.context.createProducer().send(topic, gameMessage);
    }
    
    @Override
    protected void finalize()
    {
        try
        {
            if (context != null)
            {
                this.context.close();
                context = null;
            }
        }
        catch (Exception ignored)
        {
        }
    }
}
