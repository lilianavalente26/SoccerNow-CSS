package pt.ul.fc.css.soccernow.viewmodels;


import java.util.List;

public class ViewModelTeam {

    private String club;

    private List<String> players;

    private String goalkeeper;
    
    private List<String> matches;
    
    /**
     * Constructs a new empty TeamDto.
     */
    public ViewModelTeam() {}

	public String getClub() {
		return club;
	}

	public void setClub(String club) {
		this.club = club;
	}

	public List<String> getPlayers() {
		return players;
	}

	public void setPlayers(List<String> players) {
		this.players = players;
	}

	public String getGoalkeeper() {
		return goalkeeper;
	}

	public void setGoalkeeper(String goalkeeper) {
		this.goalkeeper = goalkeeper;
	}

	public List<String> getMatches() {
		return matches;
	}

	public void setMatches(List<String> matches) {
		this.matches = matches;
	}

}
