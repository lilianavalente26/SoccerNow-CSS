package pt.ul.fc.css.soccernow.viewmodels;

import java.util.List;

public class ViewModelPlayer {

    private String name;
    private List<String> teams;
    private String preferedPosition;

    /**
     * Constructs a new empty PlayerDto.
     */
    public ViewModelPlayer() {}

    /**
     * Gets the player's name.
     * @return the player name, or null if not set
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the player's name.
     * @param name the player name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of team identifiers the player belongs to.
     * @return list of team IDs, or null if not set
     */
    public List<String> getTeams() {
        return teams;
    }

    /**
     * Sets the list of team identifiers the player belongs to.
     * @param teams list of team IDs to set
     */
    public void setTeams(List<String> teams) {
        this.teams = teams;
    }

    /**
     * Gets the player's preferred position.
     * @return the position as string (e.g., "GOALKEEPER"), or null if not set
     */
    public String getPreferedPosition() {
        return preferedPosition;
    }

    /**
     * Sets the player's preferred position.
     * @param preferedPosition the position to set (e.g., "GOALKEEPER")
     */
    public void setPreferedPosition(String preferedPosition) {
        this.preferedPosition = preferedPosition;
    }
}
