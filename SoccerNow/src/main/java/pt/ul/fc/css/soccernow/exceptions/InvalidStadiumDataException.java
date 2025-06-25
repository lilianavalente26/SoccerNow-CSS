package pt.ul.fc.css.soccernow.exceptions;

@SuppressWarnings("serial")
public class InvalidStadiumDataException extends RuntimeException {
    public InvalidStadiumDataException(String message) {
        super(message);
    }

    public InvalidStadiumDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
