package pt.ul.fc.css.soccernow.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pt.ul.fc.css.soccernow.dto.PlayerDto;
import pt.ul.fc.css.soccernow.dto.RefereeDto;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.Referee;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelReferee;

public class RefereeMapper {

	/**
	 * Converts entity Referee to refereeDto
	 * 
	 * @param referee the entity Referee
	 * @return player dto
	 */
	public static RefereeDto toDto(Referee referee) {
		RefereeDto refereeDto = new RefereeDto();
		refereeDto.setName(referee.getName());
		refereeDto.setHasCertificate(referee.isHasCertificate());
		if (referee.getMatches() != null) {
			List<Long> matchesIds = referee.getMatches().stream().map(Match::getMatchId).collect(Collectors.toList());
			refereeDto.setMatches(matchesIds);
		}
		return refereeDto;
	}

	/**
	 * Converts entity Referee to FormatedRefereeDto
	 * 
	 * @param referee the entity Referee
	 * @return player dto
	 */
	public static ViewModelReferee toViewModel(Referee referee) {
		ViewModelReferee refereeDto = new ViewModelReferee();
		refereeDto.setName(referee.getName());
		refereeDto.setHasCertificate(referee.isHasCertificate());
		if (referee.getMatches() != null) {
			List<String> matches = referee.getMatches().stream().map(m -> {
				return m.getTeam1().getClub().getNameClub() + " vs " + m.getTeam2().getClub().getNameClub();
			})
			.collect(Collectors.toList());
			refereeDto.setMatches(matches);
		}
		return refereeDto;
	}
	
	/**
	 * Converts refereeDto to entity referee
	 * 
	 * @param dto the playarDto
	 * @param matches to add to referee
	 * @return referee entity
	 */
	public static Referee toReferee(RefereeDto dto, List<Match> matches) {
		Referee referee = new Referee();
		referee.setName(dto.getName());
		referee.setHasCertificate(dto.getHasCertificate());
		referee.setMatches(matches);
		return referee;
	}
	
}
