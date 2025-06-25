package pt.ul.fc.css.soccernow.presentation.model;

import java.util.List;

public class ClubDto {
    private String name;
    private List<Long> teams;
    private List<Long> tournaments;
    private List<Long> achievements;

    public ClubDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getTeams() {
        return teams;
    }

    public void setTeams(List<Long> teams) {
        this.teams = teams;
    }

    public List<Long> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<Long> tournaments) {
        this.tournaments = tournaments;
    }

    public List<Long> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Long> achievements) {
        this.achievements = achievements;
    }
}