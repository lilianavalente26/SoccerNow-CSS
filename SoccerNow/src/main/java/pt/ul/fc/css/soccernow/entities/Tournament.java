package pt.ul.fc.css.soccernow.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tournament")
public abstract class Tournament {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tournamentId;
	
	@Column(name = "name")
    private String tournamentName;
	
	@ManyToMany
    @JoinTable(
            name = "tournament_clubs",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "club_id"))
    private List<Club> clubs;

	@OneToMany(mappedBy = "tournament")
    private List<Match> matches;

    @Column(name = "is_over")
	private boolean isOver;
	
	/**
     * Constructor for the Tournament class.
     */
    public Tournament() {
    }

    public void addClub(Club club) {
        if (clubs == null) {
            clubs = new ArrayList<>();
        }
        if (clubs != null && !clubs.contains(club)) {
            clubs.add(club);
        }
    }

    public void addMatch(Match match) {
        if (matches == null) {
            matches = new ArrayList<>();
        }
        if (matches != null && !matches.contains(match)) {
            matches.add(match);
        }
    }

    public List<Club> getClubs() {
        return clubs;
    }

    public void setClubs(List<Club> clubs) {
        this.clubs = clubs;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        isOver = over;
    }

    /**
     * Set the tournament id.
     * @param tournamentId The tournament id
     */
    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    /**
     * Set the tournament name.
     * @param tournamentName The tournament name
     */
    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    /**
     * Get the tournament id.
     * @return The tournament id
     */
    public Long getTournamentId() {
        return tournamentId;
    }

    /**
     * Get the tournament name.
     * @return The tournament name
     */
    public String getTournamentName() {
        return tournamentName;
    }
	
}
