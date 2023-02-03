package common.exceptions;

public class UserAlreadyExistException extends Exception
{
    private static final long serialVersionUID = -5601363518567618081L;

    public UserAlreadyExistException(String message) {
        super(message);
    }

    public UserAlreadyExistException() {
        super();
    }


}
