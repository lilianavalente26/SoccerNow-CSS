package pt.ul.fc.css.soccernow.exceptions;

@SuppressWarnings("serial")
public class InvalidTeamDataException extends RuntimeException {

	public InvalidTeamDataException(String message) {
        super(message);
    }
    
    public InvalidTeamDataException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
