package common;
public enum GameStatus
{
    WAIT_FOR_ALL_PLAYERS,
    READY_TO_START,   //two players performed join to game
    RUNNING,
    FINISHING, //when player is quiting (disconnected) the game in the middle or canceling before the other player joined
    FINISHED
};
