package pt.ul.fc.css.soccernow.presentation.model;

import java.util.List;

/**
 * TournamentDto is a Data Transfer Object that represents a tournament.
 */
public class TournamentDto {

    private String tournamentName;
    private List<Long> clubs;
    private List<Long> matches;
    private boolean isOver;

    /**
     * Constructs a new empty TournamentDto.
     */
    public TournamentDto() {}

    /**
     * Gets the tournament name.
     * @return name of the tournament
     */
    public String getTournamentName() {
        return tournamentName;
    }

    /**
     * Sets the tournament name.
     * @param tournamentName the tournament name to set
     */
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    /**
     * Gets the list of club ids.
     * @return list of club ids
     */
    public List<Long> getClubs() {
        return clubs;
    }

    /**
     * Sets the list of club ids.
     * @param clubs list of club ids to set
     */
    public void setClubs(List<Long> clubs) {
        this.clubs = clubs;
    }

    /**
     * Gets the list of match ids.
     * @return list of match ids
     */
    public List<Long> getMatches() {
        return matches;
    }

    /**
     * Sets the list of match ids.
     * @param matches list of match ids to set
     */
    public void setMatches(List<Long> matches) {
        this.matches = matches;
    }

    /**
     * Checks if the tournament is over.
     * @return true if the tournament is over, false otherwise
     */
    public boolean isOver() {
        return isOver;
    }

    /**
     * Sets whether the tournament is over.
     * @param isOver true if the tournament is over, false otherwise
     */
    public void setOver(boolean isOver) {
        this.isOver = isOver;
    }
}