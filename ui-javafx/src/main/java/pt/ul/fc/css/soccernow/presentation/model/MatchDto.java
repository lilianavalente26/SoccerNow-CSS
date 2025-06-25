package pt.ul.fc.css.soccernow.presentation.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * MatchDto is a Data Transfer Object that represents a match.
 */
public class MatchDto {

    private Long principalReferee;
    private List<Long> referees;
    private Long team1;
    private Long team2;
    private LocalDate date;
    private LocalTime time;
    private Long stadium;
    private Long stats;
    private Long tournament;

    /**
     * Constructs a new empty MatchDto.
     */
    public MatchDto() {}

    /**
     * Gets the principal referee id.
     * @return id of the principal referee
     */
    public Long getPrincipalReferee() {
        return principalReferee;
    }

    /**
     * Sets the principal referee id.
     * @param principalRefereeId the principal referee id to set
     */
    public void setPrincipalReferee(Long principalReferee) {
        this.principalReferee = principalReferee;
    }

    /**
     * Gets the list of referee id.
     * @return list of referee ids
     */
    public List<Long> getReferees() {
        return referees;
    }

    /**
     * Sets the list of referee id.
     * @param referees list of referee ids to set
     */
    public void setReferees(List<Long> referees) {
        this.referees = referees;
    }

    /**
     * Gets the id of the first team.
     * @return id of team 1
     */
    public Long getTeam1() {
        return team1;
    }

    /**
     * Sets the id of the first team.
     * @param team1Id the team 1 id to set
     */
    public void setTeam1(Long team1) {
        this.team1 = team1;
    }

    /**
     * Gets the id of the second team.
     * @return id of team 2
     */
    public Long getTeam2() {
        return team2;
    }

    /**
     * Sets the id of the second team.
     * @param team2Id the team 2 id to set
     */
    public void setTeam2(Long team2) {
        this.team2 = team2;
    }

    /**
     * Gets the date of the match.
     * @return date of the match
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Sets the date of the match.
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the time of the match.
     * @return time of the match
     */
    public LocalTime getTime() {
        return this.time;
    }

    /**
     * Sets the time of the match.
     * @param time the time to set
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    /**
     * Gets the id of the stadium where the match is played.
     * @return id of the stadium
     */
    public Long getStadium() {
        return stadium;
    }
    /**
     * Sets the id of the stadium where the match is played.
     * @param stadiumId the stadium id to set
     */
    public void setStadium(Long stadium) {
        this.stadium = stadium;
    }

    /**
     * Gets the id of the match statistics.
     * @return id of the match statistics
     */
    public Long getStats() {
        return stats;
    }

    /**
     * Sets the id of the match statistics.
     * @param statsId the match statistics id to set
     */
    public void setStats(Long stats) {
        this.stats = stats;
    }

    /**
     * Gets the id of the tournament this match belongs to.
     * @return id of the tournament
     */
    public Long getTournament() {
        return tournament;
    }

    /**
     * Sets the id of the tournament this match belongs to.
     * @param tournamentId the tournament id to set
     */
    public void setTournament(Long tournament) {
        this.tournament = tournament;
    }
}
