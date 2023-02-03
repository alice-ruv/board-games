package common.exceptions;

public class GeneralErrorException extends Exception
{
    private static final long serialVersionUID = 6964051511839202992L;

    public GeneralErrorException(String message) {
        super(message);
    }
    public GeneralErrorException() {
        super();
    }

}
