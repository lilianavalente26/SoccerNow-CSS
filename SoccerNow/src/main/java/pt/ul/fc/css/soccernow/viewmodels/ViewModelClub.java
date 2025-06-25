package pt.ul.fc.css.soccernow.viewmodels;

import java.util.List;

public class ViewModelClub {

	private Long clubId;
	
	private String name;

	private int nTeams;
		
	private List<String> tournaments;
	
	private List<String> achievements;
	
    /**
     * Constructs a new empty ClubDto.
     */
	public ViewModelClub() {}

	
    public Long getClubId() {
		return clubId;
	}

	public void setClubId(Long clubId) {
		this.clubId = clubId;
	}

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
	public List<String> getTournaments() {
		return tournaments;
	}

    /**
     * Sets the list of tournament identifiers this club participates in.
     * @param tournament list of tournament IDs to set
     */
	public void setTournaments(List<String> tournament) {
		this.tournaments = tournament;
	}

	/**
	 * Gets the list of team identifiers this club has.
	 * @return list of team IDs, or null if not set
	 */
	public int getTeams() {
		return nTeams;
	}

	/**
	 * Sets the list of team identifiers this club has.
	 * @param teams list of team IDs to set
	 */
	public void setTeams(int teams) {
		this.nTeams = teams;
	}

	/**
	 * Gets the list of achievement identifiers this club has.
	 * @return list of achievement IDs, or null if not set
	 */
	public List<String> getAchievements() {
		return achievements;
	}

	/**
	 * Sets the list of achievement identifiers this club has.
	 * @param achievements list of achievement IDs to set
	 */
	public void setAchievements(List<String> achievements) {
		this.achievements = achievements;
	}
}
