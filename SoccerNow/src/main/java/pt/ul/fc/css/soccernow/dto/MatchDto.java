package pt.ul.fc.css.soccernow.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
     * Creates a new empty MatchDto.
     */
	public MatchDto() {}

    /**
     * Gets the tournament identifier.
     * @return the tournament ID, or null if not associated with a tournament
     */
    public Long getTournament() {
        return tournament;
    }

    /**
     * Sets the tournament identifier.
     * @param tournament the tournament ID to set
     */
    public void setTournament(Long tournament) {
        this.tournament = tournament;
    }

    /**
     * Gets the principal referee identifier.
     * @return the referee ID of the main official
     */
    public Long getPrincipalReferee() {
        return principalReferee;
    }

    /**
     * Sets the principal referee identifier.
     * @param principalReferee the referee ID of the main official
     */
    public void setPrincipalReferee(Long principalReferee) {
        this.principalReferee = principalReferee;
    }

    /**
     * Gets the list of all referee identifiers.
     * @return list of referee IDs including assistants
     */
    public List<Long> getReferees() {
        return referees;
    }

    /**
     * Sets the list of all referee identifiers.
     * @param referees list of referee IDs to set
     */
    public void setReferees(List<Long> referees) {
        this.referees = referees;
    }

    /**
     * Gets the first team's identifier.
     * @return ID of team 1 (home team)
     */
    public Long getTeam1() {
        return team1;
    }

    /**
     * Sets the first team's identifier.
     * @param team1 ID of team 1 (home team) to set
     */
    public void setTeam1(Long team1) {
        this.team1 = team1;
    }

    /**
     * Gets the second team's identifier.
     * @return ID of team 2 (away team)
     */
    public Long getTeam2() {
        return team2;
    }

    /**
     * Sets the second team's identifier.
     * @param team2 ID of team 2 (away team) to set
     */
    public void setTeam2(Long team2) {
        this.team2 = team2;
    }

    /**
     * Gets the scheduled match date.
     * @return the match date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the scheduled match date.
     * @param date the match date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the scheduled match time.
     * @return the match time
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Sets the scheduled match time.
     * @param time the match time to set
     */
    public void setTime(LocalTime time) {
        this.time = time;
    }


    /**
     * Gets the stadium identifier.
     * @return ID of the match venue
     */
    public Long getStadium() {
        return stadium;
    }

    /**
     * Sets the stadium identifier.
     * @param stadium ID of the match venue to set
     */
    public void setStadium(Long stadium) {
        this.stadium = stadium;
    }

    /**
     * Gets the match statistics identifier.
     * @return ID of the associated statistics record
     */
    public Long getStats() {
        return stats;
    }

    /**
     * Sets the match statistics identifier.
     * @param stats ID of the statistics record to associate
     */
    public void setStats(Long stats) {
        this.stats = stats;
    }
}
