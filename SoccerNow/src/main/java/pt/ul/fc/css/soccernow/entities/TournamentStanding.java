package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tournament_standing")
public class TournamentStanding {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "tournament_id", nullable = false)
	private Tournament tournament;
	
	@ManyToOne
	@JoinColumn(name = "club_id", nullable = false)
	private Club club;
	
	@Column(name = "points",  nullable = false)
	private int points = 0;
	
	@Column(name = "goals_scored")
    private int goalsScored = 0;

    @Column(name = "goals_conceded")
    private int goalsConceded = 0;
    
    public TournamentStanding() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

	public Club getClub() {
		return club;
	}

	public void setClub(Club club) {
		this.club = club;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getGoalsScored() {
		return goalsScored;
	}

	public void setGoalsScored(int goalsScored) {
		this.goalsScored = goalsScored;
	}

	public int getGoalsConceded() {
		return goalsConceded;
	}

	public void setGoalsConceded(int goalsConceded) {
		this.goalsConceded = goalsConceded;
	}
    
	public void addPoints(int pointsToAdd) {
		this.points += pointsToAdd;
	}
	
	public void addScoredGoals(int g) {
		this.goalsScored += g;
	}
    
	public void addConcededGoals(int g) {
		this.goalsConceded += g;
	}
}
