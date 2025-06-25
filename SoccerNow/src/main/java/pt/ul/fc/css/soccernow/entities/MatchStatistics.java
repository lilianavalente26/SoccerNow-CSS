package pt.ul.fc.css.soccernow.entities;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "match_stats")
public class MatchStatistics {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long matchStatisticsId;

	@OneToOne(mappedBy = "stats")
	private Match match;

	@Column (name = "team1_score")
	private int team1_Score;
	
	@Column (name = "team2_score")
	private int team2_Score;
	
	@OneToMany(mappedBy = "matchStatistics", cascade = CascadeType.ALL)
	private List<Card> cards;

	@OneToMany(mappedBy = "matchStatistics", cascade = CascadeType.ALL)
	private List<Goal> goals;

	@Column(name = "winner_team", nullable = true)
	private Integer winnerTeam;
	
	@Column(name="is_over")
	private boolean isOver;
	
    /**
     * Default constructor for MatchStatistics.
     */
	public MatchStatistics() {}

    /**
     * Retrieves the list of goals scored in the match.
     * @return List of Goal entities, may be empty but never null
     */
	public List<Goal> getGoals() {
		return goals;
	}

    /**
     * Sets the list of goals scored in the match.
     * @param goals List of Goal entities to associate
     */
	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}

    /**
     * Retrieves the match these statistics belong to.
     * @return Associated Match entity
     */
	public Match getMatch() {
		return match;
	}

    /**
     * Sets the match these statistics belong to.
     * @param match Match entity to associate
     */
	public void setMatch(Match match) {
		this.match = match;
	}

    /**
     * Retrieves the unique identifier for these statistics.
     * @return Statistics ID, or null if not persisted
     */
	public Long getMatchStatisticsId() {
		return matchStatisticsId;
	}

    /**
     * Sets the unique identifier for these statistics.
     * @param matchStatisticsId ID to set
     */
	public void setMatchStatisticsId(Long matchStatisticsId) {
		this.matchStatisticsId = matchStatisticsId;
	}

    /**
     * Retrieves team 1's score.
     * @return Current score for team 1
     */
	public int getTeam1_Score() {
		return team1_Score;
	}


    /**
     * Sets team 1's score.
     * @param team1_Score Score value to set
     */
	public void setTeam1_Score(int team1_Score) {
		this.team1_Score = team1_Score;
	}

    /**
     * Retrieves team 2's score.
     * @return Current score for team 2
     */
	public int getTeam2_Score() {
		return team2_Score;
	}

    /**
     * Sets team 2's score.
     * @param team2_Score Score value to set
     */
	public void setTeam2_Score(int team2_Score) {
		this.team2_Score = team2_Score;
	}

    /**
     * Retrieves cards.
     * @return List of Card entities for team 1
     */
	public List<Card> getCards() {
		return cards;
	}

    /**
     * Sets cards.
     * @param team1_Cards List of Card entities to associate
     */
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

    /**
     * Retrieves the winning team designation.
     * @return 1 for team1, 2 for team2, or null for draw
     */
	public Integer getWinnerTeam() {
		return winnerTeam;
	}

    /**
     * Sets the winning team designation.
     * @param winnerTeam 1 for team1, 2 for team2, null for draw
     */
	public void setWinnerTeam(Integer winnerTeam) {
		this.winnerTeam = winnerTeam;
	}

    /**
     * Checks if the match is concluded.
     * @return true if match is over, false otherwise
     */
	public boolean isOver() {
		return isOver;
	}

    /**
     * Sets the match conclusion status.
     * @param isOver true to mark as concluded, false as ongoing
     */
	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}
}
