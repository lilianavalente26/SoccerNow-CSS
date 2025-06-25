package pt.ul.fc.css.soccernow.exceptions;

@SuppressWarnings("serial")
public class InvalidClubDataException extends RuntimeException {

	public InvalidClubDataException(String message) {
        super(message);
    }
    
    public InvalidClubDataException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
