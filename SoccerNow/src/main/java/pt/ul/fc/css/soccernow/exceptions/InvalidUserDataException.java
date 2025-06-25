package pt.ul.fc.css.soccernow.exceptions;

@SuppressWarnings("serial")
public class InvalidUserDataException extends RuntimeException {
	
	public InvalidUserDataException(String message) {
        super(message);
    }
    
    public InvalidUserDataException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
