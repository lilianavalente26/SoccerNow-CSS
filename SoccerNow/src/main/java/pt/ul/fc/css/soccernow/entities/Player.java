package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;
import pt.ul.fc.css.soccernow.enums.Position;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OptimisticLock;

@Entity
public class Player extends User {

    @ManyToMany
    private List<Team> teams;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Position preferredPosition;

    /**
     * Constructor for the Player class.
     */
    public Player() {
        super();
    }

    /**
     * Set the teams the player is part of
     * @param teams The teams the player is part of.
     */
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    /**
     * Set the preferred position of the player
     * @param i The preferred position of the player.
     */
    public void setPreferredPosition(Position preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    /**
     * Get the teams the player is part of
     * @return The teams the player is part of.
     */
    public List<Team> getTeams() {
        return teams;
    }
    
    /**
     * Returns the ids of all 
     * @return a list with the id of all teams
     */
    public List<Long> getTeamsIds() {
    	List<Long> returnTeamsIds = new ArrayList<>();
    	if (this.teams != null) {
    		for (Team team: this.teams) {
            	returnTeamsIds.add(team.getTeamId());
            }
    	}
        return returnTeamsIds;
    }
    
    /**
     * Get the preferred position of the player
     * @return The preferred position of the player.
     */
    public Position getPreferredPosition() {
        return preferredPosition;
    }

    /**
     * Set the teams the player is part of
     * @param team The teams the player is part of.
     */
    public void addTeam(Team team) {
        if (this.teams == null) {
            this.teams = new ArrayList<>();
        }
        this.teams.add(team);
    }
}
