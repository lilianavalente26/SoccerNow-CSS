package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long goalId;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne
    @JoinColumn(name = "match_statistics_id")
    private MatchStatistics matchStatistics;

    /**
     * Default constructor for Goal.
     */
    public Goal() {}

    /**
     * Retrieves the unique identifier for this goal.
     * @return the goal ID, or null if not persisted
     */
    public Long getGoalId() {
        return goalId;
    }

    /**
     * Sets the unique identifier for this goal.
     * @param goalId the ID to set for this goal
     */
    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    /**
     * Retrieves the player who scored this goal.
     * @return the Player entity who scored
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player who scored this goal.
     * @param player the Player entity who scored
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Retrieves the match where this goal was scored.
     * @return the Match entity where goal occurred
     */
    public Match getMatch() {
        return match;
    }


    /**
     * Sets the match where this goal was scored.
     * @param match the Match entity where goal occurred
     */
    public void setMatch(Match match) {
        this.match = match;
    }

    /**
     * Retrieves the match statistics associated with this goal.
     * @return the MatchStatistics entity, or null if not associated
     */
    public MatchStatistics getMatchStatistics() {
        return matchStatistics;
    }

    /**
     * Sets the match statistics associated with this goal.
     * @param matchStatistics the MatchStatistics entity to associate
     */
    public void setMatchStatistics(MatchStatistics matchStatistics) {
        this.matchStatistics = matchStatistics;
    }

}
