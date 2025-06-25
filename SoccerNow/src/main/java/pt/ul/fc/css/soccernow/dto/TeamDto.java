package pt.ul.fc.css.soccernow.dto;


import java.util.List;

public class TeamDto {

    private Long club;

    private List<Long> players;

    private Long goalkeeper;
    
    private List<Long> matchesAsTeam1;

    private List<Long> matchesAsTeam2;
    
    /**
     * Constructs a new empty TeamDto.
     */
    public TeamDto() {}

    /**
     * Gets the club identifier this team belongs to.
     * @return the club ID, or null if not set
     */
	public Long getClub() {
		return club;
	}

    /**
     * Sets the club identifier this team belongs to.
     * @param club the club ID to set
     */
	public void setClub(Long club) {
		this.club = club;
	}

    /**
     * Gets the list of player identifiers in this team.
     * @return list of player IDs, or null if not set
     */
	public List<Long> getPlayers() {
		return players;
	}
	
    /**
     * Sets the list of player identifiers in this team.
     * @param players list of player IDs to set
     */
	public void setPlayers(List<Long> players) {
		this.players = players;
	}
	
    /**
     * Gets the goalkeeper's player identifier.
     * @return the goalkeeper's player ID, or null if not set
     */
	public Long getGoalkeeper() {
		return goalkeeper;
	}

    /**
     * Sets the goalkeeper's player identifier.
     * @param goalkeeper the player ID to set as goalkeeper
     */
	public void setGoalkeeper(Long goalkeeper) {
		this.goalkeeper = goalkeeper;
	}

    /**
     * Gets the list of match identifiers where this team was team1 (home).
     * @return list of match IDs, or null if not set
     */
	public List<Long> getMatchesAsTeam1() {
		return matchesAsTeam1;
	}

    /**
     * Sets the list of match identifiers where this team was team1 (home).
     * @param matchesAsTeam1 list of match IDs to set
     */
	public void setMatchesAsTeam1(List<Long> matchesAsTeam1) {
		this.matchesAsTeam1 = matchesAsTeam1;
	}

    /**
     * Gets the list of match identifiers where this team was team2 (away).
     * @return list of match IDs, or null if not set
     */
	public List<Long> getMatchesAsTeam2() {
		return matchesAsTeam2;
	}

    /**
     * Sets the list of match identifiers where this team was team2 (away).
     * @param matchesAsTeam2 list of match IDs to set
     */
	public void setMatchesAsTeam2(List<Long> matchesAsTeam2) {
		this.matchesAsTeam2 = matchesAsTeam2;
	}
}
