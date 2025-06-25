package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long teamId;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToMany(mappedBy = "teams")
    private List<Player> players;

    @Column(name = "goalkeeper_id")
    private Long goalkeeper;

	@OneToMany(mappedBy = "team1")
    private List<Match> matchesAsTeam1;

    @OneToMany(mappedBy = "team2")
    private List<Match> matchesAsTeam2;

    /**
     * Constructor for Team.
     */
    public Team() {
    }

    /**
     * Retrieves all matches this team participated in, regardless of being team1 or team2.
     * This is a transient property not persisted by Hibernate.
     * @return Combined list of all matches, empty list if none
     */
    @Transient
    public List<Match> getAllMatches() {
        List<Match> all = new ArrayList<>();
        if (matchesAsTeam1 != null) all.addAll(matchesAsTeam1);
        if (matchesAsTeam2 != null) all.addAll(matchesAsTeam2);
        return all;
    }

    /**
     * Set the team id.
     * @param teamId The team id
     */
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    /**
     * Set the club.
     * @param club The club
     */
    public void setClub(Club club) {
        this.club = club;
    }

    /**
     * Set the players.
     * @param players The players
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }


    /**
     * Get the team id.
     * @return The team id
     */
    public Long getTeamId() {
        return teamId;
    }

    /**
     * Get the club.
     * @return The club
     */
    public Club getClub() {
        return club;
    }

    /**
     * Get the players.
     * @return The players
     */
    public List<Player> getPlayers() {
        return players;
    }
    
    /**
     * Retrieves the IDs of all players in this team.
     * @return List of player IDs, empty list if no players
     */
    public List<Long> getPlayersIds() {
		List<Long> rtnList = new ArrayList<>();
		if (this.players != null) {
			for (Player player: this.players) {
				rtnList.add(player.getUserId());
			}
		}
		return rtnList;
	}

    /**
     * Retrieves the list of matches where this team was team1.
     * @return List of Match entities, may be null if not initialized
     */
    public List<Match> getMatchesAsTeam1() {
        return matchesAsTeam1;
    }
    
    /**
     * Retrieves the IDs of all matches where this team was team1.
     * @return List of match IDs, empty list if none
     */
    public List<Long> getMatchesAsTeam1Ids() {
    	List<Long> rtnList = new ArrayList<>();
		if (this.matchesAsTeam1 != null) {
			for (Match match: this.matchesAsTeam1) {
				rtnList.add(match.getMatchId());
			}
		}
		return rtnList;
    }

    /**
     * Sets the list of matches where this team was team1.
     * @param matchesAsTeam1 List of Match entities to set
     */
    public void setMatchesAsTeam1(List<Match> matchesAsTeam1) {
        this.matchesAsTeam1 = matchesAsTeam1;
    }

    /**
     * Retrieves the list of matches where this team was team2.
     * @return List of Match entities, may be null if not initialized
     */
    public List<Match> getMatchesAsTeam2() {
        return matchesAsTeam2;
    }
    
    /**
     * Retrieves the IDs of all matches where this team was team2.
     * @return List of match IDs, empty list if none
     */
    public List<Long> getMatchesAsTeam2Ids() {
    	List<Long> rtnList = new ArrayList<>();
		if (this.matchesAsTeam2 != null) {
			for (Match match: this.matchesAsTeam2) {
				rtnList.add(match.getMatchId());
			}
		}
		return rtnList;
    }

    /**
     * Sets the list of matches where this team was team2.
     * @param matchesAsTeam2 List of Match entities to set
     */
    public void setMatchesAsTeam2(List<Match> matchesAsTeam2) {
        this.matchesAsTeam2 = matchesAsTeam2;
    }
    
    /**
     * Retrieves the goalkeeper's player ID.
     * @return Long representing the goalkeeper's player ID
     */
    public Long getGoalkeeper() {
		return goalkeeper;
	}

    /**
     * Sets the goalkeeper's player ID.
     * @param goalkeeper The player ID to set as goalkeeper
     */
	public void setGoalkeeper(Long goalkeeper) {
		this.goalkeeper = goalkeeper;
	}
}
