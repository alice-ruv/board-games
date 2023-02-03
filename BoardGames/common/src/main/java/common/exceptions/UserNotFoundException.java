package common.exceptions;

public class UserNotFoundException extends Exception
{

    private static final long serialVersionUID = 3887161388011051534L;

    public UserNotFoundException(String message)
    {
        super(message);
    }

    public UserNotFoundException() {
        super();
    }
}
