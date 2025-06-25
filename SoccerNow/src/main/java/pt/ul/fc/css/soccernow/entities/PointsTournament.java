package pt.ul.fc.css.soccernow.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class PointsTournament extends Tournament {
	
	@OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
	private List<TournamentStanding> standings;
	
	/**
     * Constructor for the PointsTournament class.
     */
    public PointsTournament() {
        super();
    }

	public List<TournamentStanding> getStandings() {
		return standings;
	}

	public void setStandings(List<TournamentStanding> standings) {
		this.standings = standings;
	}
	
	public void updateClubStandings(Club club, Boolean winner, int goalScores, int goalsConceded) {
		for (TournamentStanding ts: this.standings) {
			if (ts.getClub() == club) {
				if (winner != null) {
					if (winner) {
						ts.addPoints(3);
					} else {
						ts.addPoints(0);
					}
				} else {
					ts.addPoints(1);
				}
				ts.addScoredGoals(goalScores);
				ts.addConcededGoals(goalsConceded);
			}
		}
	}
	
	public int getClubPoints(Club c) {
		for (TournamentStanding ts: this.standings) {
			if (ts.getClub().getClubId() == c.getClubId()) {
				return ts.getPoints();
			}
		}
		return -1;
	}
	
	public int getClubScoredGoals(Club c) {
		for (TournamentStanding ts: this.standings) {
			if (ts.getClub().getClubId() == c.getClubId()) {
				return ts.getGoalsScored();
			}
		}
		return -1;
	}
	
	public int getClubConcededGoals(Club c) {
		for (TournamentStanding ts: this.standings) {
			if (ts.getClub().getClubId() == c.getClubId()) {
				return ts.getGoalsConceded();
			}
		}
		return -1;
	}
	
}
