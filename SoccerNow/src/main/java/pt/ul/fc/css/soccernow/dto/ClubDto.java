package pt.ul.fc.css.soccernow.dto;

import java.util.List;

public class ClubDto {

	private String name;

	private List<Long> teams;
		
	private List<Long> tournaments;
	
	private List<Long> achievements;
	
    /**
     * Constructs a new empty ClubDto.
     */
	public ClubDto() {}

    /**
     * Gets the club name.
     * @return the club name, or null if not set
     */
	public String getName() {
		return name;
	}

    /**
     * Sets the club name.
     * @param name the club name to set
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * Gets the list of tournament identifiers this club participates in.
     * @return list of tournament IDs, or null if not set
     */
	public List<Long> getTournaments() {
		return tournaments;
	}

    /**
     * Sets the list of tournament identifiers this club participates in.
     * @param tournament list of tournament IDs to set
     */
	public void setTournaments(List<Long> tournament) {
		this.tournaments = tournament;
	}

	/**
	 * Gets the list of team identifiers this club has.
	 * @return list of team IDs, or null if not set
	 */
	public List<Long> getTeams() {
		return teams;
	}

	/**
	 * Sets the list of team identifiers this club has.
	 * @param teams list of team IDs to set
	 */
	public void setTeams(List<Long> teams) {
		this.teams = teams;
	}

	/**
	 * Gets the list of achievement identifiers this club has.
	 * @return list of achievement IDs, or null if not set
	 */
	public List<Long> getAchievements() {
		return achievements;
	}

	/**
	 * Sets the list of achievement identifiers this club has.
	 * @param achievements list of achievement IDs to set
	 */
	public void setAchievements(List<Long> achievements) {
		this.achievements = achievements;
	}
}
