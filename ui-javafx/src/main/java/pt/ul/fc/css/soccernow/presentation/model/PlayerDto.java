package pt.ul.fc.css.soccernow.presentation.model;

import java.util.List;

/**
 * PlayerDto is a Data Transfer Object that represents a player.
 * Matches exactly with the backend PlayerDto structure.
 */
public class PlayerDto {
    private String name;
    private List<Long> teams;
    private String preferedPosition;

    public PlayerDto() {}

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

    public String getPreferedPosition() {
        return preferedPosition;
    }

    public void setPreferedPosition(String preferedPosition) {
        this.preferedPosition = preferedPosition;
    }
}