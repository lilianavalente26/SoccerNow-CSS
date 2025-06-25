package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OptimisticLock;

@Entity
public class Referee extends User {

    @Column(name = "has_certificate")
    private boolean hasCertificate;

    @ManyToMany(mappedBy = "referees")
    private List<Match> matches;
    
    /**
     * Constructor for the Referee class.
     */
    public Referee() {
        super();
    }

    /**
     * Get the referee's certificate status
     * @return The referee's certificate status.
     */
    public boolean isHasCertificate() {
        return hasCertificate;
    }

    /**
     * Set the referee's certificate status
     * @param hasCertificate The referee's certificate status.
     */
    public void setHasCertificate(boolean hasCertificate) {
        this.hasCertificate = hasCertificate;
    }
    
    /**
     * Get the referee's matches
     * 
     * @return matches
     */
    public List<Match> getMatches() {
        return matches;
    }
    
    /**
     * Returns the ids of all matches
     * @return list with id of all matches
     */
    public List<Long> getMatchesIds() {
    	List<Long> returnMatches = new ArrayList<>();
    	if (this.matches != null) {
    		for (Match m: this.matches) {
        		returnMatches.add(m.getMatchId());
        	}
    	}
     	return returnMatches;
    }
    
    /**
     * Adds a match to the referee list of matches
     * @param match the match to add
     */
    public void addMatch(Match match) {
    	if (this.matches == null) {
    		this.matches = new ArrayList<>();
    	}
    	this.matches.add(match);
    }

    /**
     * Set the referee's matches
     * @param matches the referee's matches
     */
    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

}
