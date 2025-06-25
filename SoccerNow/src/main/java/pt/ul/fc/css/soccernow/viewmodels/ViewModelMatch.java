package pt.ul.fc.css.soccernow.viewmodels;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class ViewModelMatch {

	private Long id;
	
    private String principalReferee;

    private List<String> referees;

    private String team1;

    private String team2;

    private LocalDate date;

    private LocalTime time;

    private String stadium;

    private String score;
    
    private List<String> goals;
    
    private List<String> team1_yellowCards;
    
    private List<String> team2_yellowCards;
    
    private List<String> team1_redCards;
    
    private List<String> team2_redCards;
    
    private boolean isOver;

    private String tournament;
    
    private Map<Long, String> players;
    
    /**
     * Creates a new empty MatchDto.
     */
	public ViewModelMatch() {}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPrincipalReferee() {
		return principalReferee;
	}

	public void setPrincipalReferee(String principalReferee) {
		this.principalReferee = principalReferee;
	}

	public List<String> getReferees() {
		return referees;
	}

	public void setReferees(List<String> referees) {
		this.referees = referees;
	}

	public String getTeam1() {
		return team1;
	}

	public void setTeam1(String team1) {
		this.team1 = team1;
	}

	public String getTeam2() {
		return team2;
	}

	public void setTeam2(String team2) {
		this.team2 = team2;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public String getStadium() {
		return stadium;
	}

	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	

	public List<String> getGoals() {
		return goals;
	}

	public void setGoals(List<String> goals) {
		this.goals = goals;
	}

	public List<String> getTeam1_yellowCards() {
		return team1_yellowCards;
	}

	public void setTeam1_yellowCards(List<String> team1_yellowCards) {
		this.team1_yellowCards = team1_yellowCards;
	}

	public List<String> getTeam2_yellowCards() {
		return team2_yellowCards;
	}

	public void setTeam2_yellowCards(List<String> team2_yellowCards) {
		this.team2_yellowCards = team2_yellowCards;
	}

	public List<String> getTeam1_redCards() {
		return team1_redCards;
	}

	public void setTeam1_redCards(List<String> team1_redCards) {
		this.team1_redCards = team1_redCards;
	}

	public List<String> getTeam2_redCards() {
		return team2_redCards;
	}

	public void setTeam2_redCards(List<String> team2_redCards) {
		this.team2_redCards = team2_redCards;
	}

	public boolean isOver() {
		return isOver;
	}

	public void setOver(boolean isOver) {
		this.isOver = isOver;
	}

	public String getTournament() {
		return tournament;
	}

	public void setTournament(String tournament) {
		this.tournament = tournament;
	}

	public Map<Long, String> getPlayers() {
		return players;
	}

	public void setPlayers(Map<Long, String> players) {
		this.players = players;
	}
	
}
