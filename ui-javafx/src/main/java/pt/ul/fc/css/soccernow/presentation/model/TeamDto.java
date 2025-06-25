package pt.ul.fc.css.soccernow.presentation.model;

import java.util.List;

/**
 * TeamDto is a Data Transfer Object that represents a team.
 */
public class TeamDto {

    private Long club;
    private List<Long> players;
    private Long goalkeeper;
    private List<Long> matchesAsTeam1;
    private List<Long> matchesAsTeam2;

    /**
     * Constructs a new empty TeamDto.
     */
    public TeamDto() {
    }

    public Long getClub() {
        return club;
    }

    public void setClub(Long club) {
        this.club = club;
    }

    public List<Long> getPlayers() {
        return players;
    }

    public void setPlayers(List<Long> players) {
        this.players = players;
    }

    public Long getGoalkeeper() {
        return goalkeeper;
    }

    public void setGoalkeeper(Long goalkeeper) {
        this.goalkeeper = goalkeeper;
    }

    public List<Long> getMatchesAsTeam1() {
        return matchesAsTeam1;
    }

    public void setMatchesAsTeam1(List<Long> matchesAsTeam1) {
        this.matchesAsTeam1 = matchesAsTeam1;
    }

    public List<Long> getMatchesAsTeam2() {
        return matchesAsTeam2;
    }

    public void setMatchesAsTeam2(List<Long> matchesAsTeam2) {
        this.matchesAsTeam2 = matchesAsTeam2;
    }
}
