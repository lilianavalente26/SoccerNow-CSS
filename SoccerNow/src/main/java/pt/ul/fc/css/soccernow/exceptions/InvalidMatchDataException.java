package pt.ul.fc.css.soccernow.exceptions;

@SuppressWarnings("serial")
public class InvalidMatchDataException extends RuntimeException {
    public InvalidMatchDataException(String message) {
        super(message);
    }

    public InvalidMatchDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
