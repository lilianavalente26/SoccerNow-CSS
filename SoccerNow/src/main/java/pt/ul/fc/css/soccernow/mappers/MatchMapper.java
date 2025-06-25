package pt.ul.fc.css.soccernow.mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.ul.fc.css.soccernow.dto.MatchDto;
import pt.ul.fc.css.soccernow.entities.Card;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.MatchStatistics;
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.entities.Referee;
import pt.ul.fc.css.soccernow.entities.Stadium;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.entities.Tournament;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelMatch;

/**
 * Mapper class for converting between Match and MatchDto objects.
 */
public class MatchMapper {

	public static MatchDto toDto(Match match) {
		MatchDto mDto = new MatchDto();
		mDto.setPrincipalReferee(match.getPrincipalRefereeId());
		mDto.setReferees(match.getRefereesIds());
		mDto.setTeam1(match.getTeam1Id());
		mDto.setTeam2(match.getTeam2Id());
		mDto.setDate(match.getDate());
		mDto.setTime(match.getTime());
		mDto.setStadium(match.getStadiumId());
		mDto.setStats(match.getStatsId());
		if (match.getTournament() != null) {
			mDto.setTournament(match.getTournamentId());
		} else {
			mDto.setTournament(null);
		}
		return mDto;
	}
	
	public static ViewModelMatch toViewModel(Match match) {
		ViewModelMatch mDto = new ViewModelMatch();
		mDto.setId(match.getMatchId());
		mDto.setPrincipalReferee(match.getPrincipalReferee().getName());
		mDto.setReferees(match.getReferees().stream().map(Referee::getName).collect(Collectors.toList()));
		mDto.setTeam1(match.getTeam1().getClub().getNameClub());
		mDto.setTeam2(match.getTeam2().getClub().getNameClub());
		mDto.setDate(match.getDate());
		mDto.setTime(match.getTime());
		mDto.setStadium(match.getStadium().getStadiumName());
		mDto.setScore(match.getStats().getTeam1_Score() + " vs " + match.getStats().getTeam2_Score());		
		
		if (match.getStats().getGoals() != null) {
			mDto.setGoals(match.getStats().getGoals().stream().map(g -> g.getPlayer().getName()).collect(Collectors.toList()));
		} else {
			mDto.setGoals(new ArrayList<>());
		}
		
		if (match.getStats().getCards() != null) {
			List<Card> yellowTeam1Cards = match.getStats().getCards().stream().filter(c -> c.getCardType().toString().equals("YELLOW_CARD") && c.getTeam() == 1).collect(Collectors.toList());
			List<String> yellowTeam1CardPlayers = yellowTeam1Cards.stream().map(c -> c.getPlayer().getName()).collect(Collectors.toList());
			mDto.setTeam1_yellowCards(yellowTeam1CardPlayers);
			
			List<Card> redTeam1Cards = match.getStats().getCards().stream().filter(c -> c.getCardType().toString().equals("RED_CARD") && c.getTeam() == 1).collect(Collectors.toList());
			List<String> redTeam1CardPlayers = redTeam1Cards.stream().map(c -> c.getPlayer().getName()).collect(Collectors.toList());
			mDto.setTeam1_redCards(redTeam1CardPlayers);
		} else {
			mDto.setTeam1_yellowCards(new ArrayList<>());
			mDto.setTeam1_redCards(new ArrayList<>());
		}
		
		if (match.getStats().getCards() != null) {
			List<Card> yellowTeam2Cards = match.getStats().getCards().stream().filter(c -> c.getCardType().toString().equals("YELLOW_CARD") && c.getTeam() == 2).collect(Collectors.toList());
			List<String> yellowTeam2CardPlayers = yellowTeam2Cards.stream().map(c -> c.getPlayer().getName()).collect(Collectors.toList());
			mDto.setTeam2_yellowCards(yellowTeam2CardPlayers);
			
			List<Card> redTeam2Cards = match.getStats().getCards().stream().filter(c -> c.getCardType().toString().equals("RED_CARD") && c.getTeam() == 2).collect(Collectors.toList());
			List<String> redTeam2CardPlayers = redTeam2Cards.stream().map(c -> c.getPlayer().getName()).collect(Collectors.toList());
			mDto.setTeam2_redCards(redTeam2CardPlayers);
		} else {
			mDto.setTeam2_yellowCards(new ArrayList<>());
			mDto.setTeam2_redCards(new ArrayList<>());
		}
		
		mDto.setOver(match.getStats().isOver());
		
		if (match.getTournament() != null) {
			mDto.setTournament(match.getTournament().getTournamentName());
		} else {
			mDto.setTournament(null);
		}
		
		Map<Long, String> players = new HashMap<>();
		if (match.getTeam1().getPlayers() != null) {
			for (Player p: match.getTeam1().getPlayers()) {
				players.put(p.getUserId(), p.getName());
			}
		}
		if (match.getTeam2().getPlayers() != null) {
			for (Player p: match.getTeam2().getPlayers()) {
				players.put(p.getUserId(), p.getName());
			}
		}
		mDto.setPlayers(players);
		
		return mDto;
	}
	
	public static Match toMatch(MatchDto mDto, Referee pr, List<Referee> referees, Team t1, Team t2, Stadium stm, MatchStatistics stats, Tournament t) {
		Match match = new Match();
		match.setPrincipalReferee(pr);
		match.setReferees(referees);
		match.setTeam1(t1);
		match.setTeam2(t2);
		match.setStadium(stm);
		match.setStats(stats);
		match.setTournament(t);
		match.setDate(mDto.getDate());
		match.setTime(mDto.getTime());
		return match;
	}
	
}
