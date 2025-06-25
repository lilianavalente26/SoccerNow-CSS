package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long achievementId;

    @Column(name = "placement")
    private int placement;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    /**
     * Constructor for Achievement.
     */
    public Achievement() {
    }

    /**
     * Retrieves the club associated with this achievement.
     * @return the Club entity that earned this achievement, or null if not set
     */
    public Club getClub() {
        return club;
    }

    /**
     * Sets the club associated with this achievement.
     * @param club the Club entity that earned this achievement
     */
    public void setClub(Club club) {
        this.club = club;
    }

    /**
     * Sets achievement id
     * @param achievementId The achievement id.
     */
    public void setAchievementId(Long achievementId) {
        this.achievementId = achievementId;
    }

    /**
     * Sets the placement of the Club in a Tournament.
     * @param placement The placement of the Club in a Tournament.
     */
    public void setPlacement(int placement) {
        this.placement = placement;
    }

    /**
     * Sets the tournament associated with the achievement.
     * @param tournament The tournament associated with the achievement.
     */
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    /**
     * Gets the placement of the Club in a Tournament.
     * @return The placement of the Club in a Tournament.
     */
    public int getPlacement() {
        return placement;
    }

    /**
     * Gets the ID of the achievement.
     * @return The ID of the achievement.
     */
    public Long getAchievementId() {
        return achievementId;
    }

    /**
     * Gets the tournament associated with the achievement.
     * @return The tournament associated with the achievement.
     */
    public Tournament getTournament() {
        return tournament;
    }
}
