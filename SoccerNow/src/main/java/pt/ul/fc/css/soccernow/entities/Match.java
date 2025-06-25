package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long matchId;

    @ManyToOne
    @JoinColumn(name = "principal_referee_id", nullable = false)
    private Referee principalReferee;

    @ManyToMany
    @JoinTable(
            name = "users_matches",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<Referee> referees;

    @ManyToOne
    private Team team1;

    @ManyToOne
    private Team team2;

    @Column (name = "date")
    private LocalDate date;

    @Column (name = "time")
    private LocalTime time;

    @ManyToOne
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stats_match_statistics_id")
    private MatchStatistics stats;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = true)
    private Tournament tournament;


    /**
     * Constructor for Match.
     */
    public Match(){}

    /**
     * Retrieves the tournament associated with this match.
     * @return Tournament entity or null if not part of a tournament
     */
    public Tournament getTournament() {
        return tournament;
    }

    /**
     * Sets the tournament for this match.
     * @param tournament The tournament to associate
     */
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    /**
     * Retrieves the match statistics.
     * @return MatchStatistics entity containing match data
     */
    public MatchStatistics getStats() {
        return stats;
    }

    /**
     * Sets the match statistics.
     * @param stats The MatchStatistics to associate
     */
    public void setStats(MatchStatistics stats) {
        this.stats = stats;
    }

    /**
     * Sets the unique match identifier.
     * @param matchId The ID to set
     */
    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    /**
     * Sets the principal referee of the match.
     * @param principalReferee The principal referee to set.
     */
    public void setPrincipalReferee(Referee principalReferee) {
        this.principalReferee = principalReferee;
    }

    /**
     * Sets the referees of the match.
     * @param referees The referees to set.
     */
    public void setReferees(List<Referee> referees) {
        this.referees = referees;
    }

    /**
     * Sets the teams of the match.
     * @param team1 The first team to set.
     */
    public void setTeam1(Team team1) {
        this.team1 = team1;
    }

    /**
     * Sets the teams of the match.
     * @param team2 The second team to set.
     */
    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    /**
     * Sets the date of the match.
     * @param date The date to set.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Sets the time of the match.
     * @param time The time to set.
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * Sets the stadium of the match.
     * @param stadium The stadium to set.
     */
    public void setStadium(Stadium stadium) {
        this.stadium = stadium;
    }

    /**
     * Gets the ID of the match.
     * @return The ID of the match.
     */
    public Long getMatchId() {return matchId;
    }

    /**
     * Gets the date of the match.
     * @return The date of the match.
     */
    public LocalDate getDate() {return date;
    }

    /**
     * Gets the time of the match.
     * @return The time of the match.
     */
    public LocalTime getTime() {return time;
    }

    /**
     * Gets the stadium of the match.
     * @return The stadium of the match.
     */
    public Stadium getStadium() {return stadium;}

    /**
     * Gets the principal referee of the match.
     * @return The principal referee of the match.
     *
     */
    public Referee getPrincipalReferee() {return principalReferee;}

    /**
     * Gets the referees of the match.
     * @return The referees of the match.
     */
    public List<Referee> getReferees() {return referees;}

    /**
     * Gets the team 1 of the match.
     * @return The team1 of the match.
     */
    public Team getTeam1() {return team1;}

    /**
     * Gets the team 2 of the match.
     * @return The team 2 of the match.
     *
     */
    public Team getTeam2() {return team2;}

    /**
     * Retrieves the principal referee's ID.
     * @return referee user ID
     */
    public Long getPrincipalRefereeId() {
        return principalReferee.getUserId();
    }

    /**
     * Retrieves IDs of all referees.
     * @return List of referee user IDs
     */
    public List<Long> getRefereesIds() {
        List<Long> refereeIds = new ArrayList<>();
        for (Referee referee : referees) {
            refereeIds.add(referee.getUserId());
        }
        return refereeIds;
    }

    /**
     * Retrieves home team ID.
     * @return team1 ID
     */
    public Long getTeam1Id() {
        return team1.getTeamId();
    }

    /**
     * Retrieves away team ID.
     * @return team2 ID
     */
    public Long getTeam2Id() {
        return team2.getTeamId();
    }

    /**
     * Retrieves stadium ID.
     * @return stadium ID
     */
    public Long getStadiumId() {
        return stadium.getStadiumId();
    }

    /**
     * Retrieves match statistics ID.
     * @return statistics ID
     */
    public Long getStatsId() {
        return stats.getMatchStatisticsId();
    }

    /**
     * Retrieves tournament ID.
     * @return tournament ID or null if friendly
     */
    public Long getTournamentId() {
        return tournament.getTournamentId();
    }
}
