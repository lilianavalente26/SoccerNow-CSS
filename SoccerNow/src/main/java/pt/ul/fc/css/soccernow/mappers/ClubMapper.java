package pt.ul.fc.css.soccernow.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.ul.fc.css.soccernow.dto.ClubDto;
import pt.ul.fc.css.soccernow.entities.Achievement;
import pt.ul.fc.css.soccernow.entities.Club;
import pt.ul.fc.css.soccernow.entities.Tournament;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelClub;

/**
 * Mapper class for converting between Club and ClubDto objects.
 */
public class ClubMapper {

	public static ClubDto toDto(Club club) {
		ClubDto cDto = new ClubDto();
		cDto.setName(club.getNameClub());
		cDto.setTeams(club.getTeamsIds());
		cDto.setTournaments(club.getTournaments().stream().map(Tournament::getTournamentId).collect(Collectors.toList()));
		cDto.setAchievements(club.getAchievements().stream().map(Achievement::getAchievementId).collect(Collectors.toList()));
		return cDto;
	}
	
	public static ViewModelClub toFormatedDto(Club club) {
		ViewModelClub cDto = new ViewModelClub();
		cDto.setClubId(club.getClubId());
		cDto.setName(club.getNameClub());
		cDto.setTeams(club.getTeams().size());
		cDto.setTournaments(club.getTournaments().stream().map(Tournament::getTournamentName).collect(Collectors.toList()));
		List<String> formatedAchievements = club.getAchievements().stream().map(a -> {
			return "Tournament: " + a.getTournament().getTournamentName() + " | " + "Position: " + a.getPlacement();
		})
		.collect(Collectors.toList());
		cDto.setAchievements(formatedAchievements);
		return cDto;
	}
	
	public static Club toClub(String name) {
		Club club = new Club();
		club.setName(name);
		club.setTeams(new ArrayList<>());
		club.setTournaments(new ArrayList<>());
		club.setAchievements(new ArrayList<>());
		club.setMatchHistory(new ArrayList<>());
		return club;
	}
	
}
