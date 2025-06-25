package pt.ul.fc.css.soccernow.viewmodels;

import java.util.List;
import java.util.Map;

public class ViewModelPointsTournament {

	// Abstract Atributes

    private String tournamentName;

    private List<String> clubs;

    private List<String> matches;

	private boolean isOver;
	
	// <ClubName, List<Points, GoalsScored, GoalsConceded>>
	Map<String, List<Integer>> standings;

	public ViewModelPointsTournament() {}

	public String getTournamentName() {
		return tournamentName;
	}

	public void setTournamentName(String tournamentName) {
		this.tournamentName = tournamentName;
	}

	public List<String> getClubs() {
		return clubs;
	}

	public void setClubs(List<String> clubs) {
		this.clubs = clubs;
	}

	public List<String> getMatches() {
		return matches;
	}

	public void setMatches(List<String> matches) {
		this.matches = matches;
	}

	public boolean isOver() {
		return isOver;
	}

	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}

	public Map<String, List<Integer>> getStandings() {
		return standings;
	}

	public void setStandings(Map<String, List<Integer>> standings) {
		this.standings = standings;
	}
	
}
