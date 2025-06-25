package pt.ul.fc.css.soccernow.dto;

import java.util.List;

public class TournamentDto {

	// Abstract Atributes

    private String tournamentName;

    private List<Long> clubs;

    private List<Long> matches;

	private boolean isOver;

    /**
     * Constructs a new empty TournamentDto.
     */
    public String getTournamentName() {
        return tournamentName;
    }

    /**
     * Sets the tournament name.
     * @param tournamentName the name of the tournament to set
     */
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    /**
     * Gets the list of club identifiers participating in the tournament.
     * @return list of club IDs, or null if not set
     */
    public List<Long> getClubs() {
        return clubs;
    }

    /**
     * Sets the list of club identifiers participating in the tournament.
     * @param clubs list of club IDs to set
     */
    public void setClubs(List<Long> clubs) {
        this.clubs = clubs;
    }

    /**
     * Gets the list of match identifiers in the tournament.
     * @return list of match IDs, or null if not set
     */
    public List<Long> getMatches() {
        return matches;
    }

    /**
     * Sets the list of match identifiers in the tournament.
     * @param matches list of match IDs to set
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
     * Sets the tournament status to over or not.
     * @param over true if the tournament is over, false otherwise
     */
    public void setOver(boolean over) {
        isOver = over;
    }

}
