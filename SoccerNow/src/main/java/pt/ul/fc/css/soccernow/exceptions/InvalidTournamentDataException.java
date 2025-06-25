package pt.ul.fc.css.soccernow.exceptions;

public class InvalidTournamentDataException extends RuntimeException{
    public InvalidTournamentDataException(String message) {
        super(message);
    }

    public InvalidTournamentDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
