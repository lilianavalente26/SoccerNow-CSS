package pt.ul.fc.css.soccernow.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.entities.User;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelTeam;

public class TeamMapper {

	/**
	 * Converts team entity to formatedDto
	 * 
	 * @param team the team
	 * @param goalkeeperName the name of the goalkeeper of the team
	 * @return formatedDto
	 */
	public static ViewModelTeam toViewModel(Team team, String goalkeeperName) {
		ViewModelTeam tDto = new ViewModelTeam();
		tDto.setClub(team.getClub().getNameClub());
		tDto.setGoalkeeper(goalkeeperName);
		
		if (team.getPlayers() != null) {
			tDto.setPlayers(team.getPlayers().stream().map(User::getName).collect(Collectors.toList()));
		} else {
			tDto.setPlayers(new ArrayList<>());
		}
		
		List<String> matchesToAdd = new ArrayList<>();
		if (team.getMatchesAsTeam1() != null) {
			List<String> matches1 = team.getMatchesAsTeam1().stream().map(m -> {
				return m.getTeam1().getClub().getNameClub() + " vs " + m.getTeam2().getClub().getNameClub();
			})
			.collect(Collectors.toList());
			matchesToAdd.addAll(matches1);
		}
		if (team.getMatchesAsTeam2() != null) {
			List<String> matches2 = team.getMatchesAsTeam2().stream().map(m -> {
				return m.getTeam1().getClub().getNameClub() + " vs " + m.getTeam2().getClub().getNameClub();
			})
			.collect(Collectors.toList());
			matchesToAdd.addAll(matches2);
		}
		tDto.setMatches(matchesToAdd);
		return tDto;
	}
	
}
