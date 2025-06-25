package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long clubId;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "club")
    private List<Team> teams;
    
	@Transient
	private List<Match> matchHistory;

	@OneToMany(mappedBy = "club")
    private List<Achievement> achievements;
    
    @ManyToMany(mappedBy = "clubs")
    private List<Tournament> tournaments;
    
    @OneToMany(mappedBy = "club")
    private List<TournamentStanding> standings;

    /**
     * Constructor for Club.
     */
    public Club() {
    }

    public void addTournament(Tournament tournament) {
        if (tournaments == null) {
            tournaments = new ArrayList<>();
        }
        if (!tournaments.contains(tournament)) {
            tournaments.add(tournament);
        }
    }

    /**
     * Retrieves the list of teams associated with this club.
     * @return List of Team entities, or null if no teams are associated
     */
    public List<Team> getTeams() {
    	if (this.teams == null) {
    		this.teams = new ArrayList<>();
    	}
        return teams;
    }
    
    /**
     * Retrieves the IDs of all teams associated with this club.
     * @return List of team IDs, empty list if no teams are associated
     */
    public List<Long> getTeamsIds() {
    	List<Long> returnList = new ArrayList<>();
    	if (this.teams != null) {
    		for (Team team: this.teams) {
    			returnList.add(team.getTeamId());
    		}
    	}
    	return returnList;
    }

    /**
     * Sets the teams associated with this club.
     * @param teams List of Team entities to associate with this club
     */
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
    
    /**
     * Adds a team to this club's list of teams.
     * @param team Team entity to add
     */
    public void addTeam(Team team) {
    	if (this.teams == null) {
    		this.teams = new ArrayList<>();
    	}
    	this.teams.add(team);
    }

    /**
     * Sets the achievements earned by this club.
     * @param achievements List of Achievement entities
     */
    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    /**
     * Retrieves the achievements earned by this club.
     * @return List of Achievement entities, or null if no achievements
     */
    public List<Achievement> getAchievements() {
        return achievements;
    }

    /**
     * Sets the ID of the club.
     * @param clubId The ID of the club.
     */
    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    /**
     * Sets the name of the club.
     * @param name The name of the club.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ID of the club.
     * @return The ID of the club.
     */
    public Long getClubId() {
        return clubId;
    }

	/**
     * Gets the name of the club.
     * @return The name of the club.
     */
    public String getNameClub() {
        return this.name;
    }
    
    /**
     * Retrieves the match history of this club.
     * @return List of Match entities, or null if no history
     */
    public List<Match> getMatchHistory() {
		return matchHistory;
	}

    /**
     * Sets the match history for this club.
     * @param matchHistory List of Match entities
     */
	public void setMatchHistory(List<Match> matchHistory) {
		this.matchHistory = matchHistory;
	}
	
    /**
     * Adds a match to this club's history.
     * @param match Match entity to add to history
     */
	public void addMatchHistory(Match match) {
		if (this.matchHistory == null) {
			this.matchHistory = new ArrayList<>();
		}
		this.matchHistory.add(match);
	}
	
    /**
     * Retrieves the IDs of all matches in this club's history.
     * @return List of match IDs, empty list if no history
     */
	public List<Long> getMatchHistoryIds() {
		List<Long> rtnList = new ArrayList<>();
		if (this.matchHistory == null) {
			this.matchHistory = new ArrayList<>();
		}
		return rtnList;
	}
	
    /**
     * Retrieves the tournaments this club participates in.
     * @return List of Tournament entities, or null if none
     */
	public List<Tournament> getTournaments() {
		return tournaments;
	}

    /**
     * Sets the tournaments this club participates in.
     * @param tournament List of Tournament entities
     */
	public void setTournaments(List<Tournament> tournament) {
		this.tournaments = tournament;
	}
}

