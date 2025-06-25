package pt.ul.fc.css.soccernow.mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.ul.fc.css.soccernow.dto.TournamentDto;
import pt.ul.fc.css.soccernow.entities.Club;
import pt.ul.fc.css.soccernow.entities.Match;
import pt.ul.fc.css.soccernow.entities.PointsTournament;
import pt.ul.fc.css.soccernow.entities.TournamentStanding;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelPointsTournament;

/**
 * Mapper class for converting between PointsTournament and TournamentDto objects.
 */
public class PointsTournamentMapper {

    public static TournamentDto toDto(PointsTournament t) {
        TournamentDto tDto = new TournamentDto();
        tDto.setTournamentName(t.getTournamentName());
        tDto.setOver(t.isOver());
        if (t.getClubs() != null) {
            List<Long> clubsIds = t.getClubs().stream().map(Club::getClubId).collect(Collectors.toList());
            tDto.setClubs(clubsIds);
        }
        if (t.getMatches() != null) {
            List<Long> matchesIds = t.getMatches().stream().map(Match::getMatchId).collect(Collectors.toList());
            tDto.setMatches(matchesIds);
        }
        return tDto;
    }
    
    public static ViewModelPointsTournament toViewModel(PointsTournament t, Map<Club, Integer> standings) {
    	ViewModelPointsTournament tDto = new ViewModelPointsTournament();
    	tDto.setTournamentName(t.getTournamentName());
    	tDto.setClubs(t.getClubs().stream().map(Club::getNameClub).collect(Collectors.toList()));
    	tDto.setOver(t.isOver());
    	tDto.setMatches(t.getMatches().stream().map(m -> {
    		return m.getTeam1().getClub().getNameClub() + " vs " + m.getTeam2().getClub().getNameClub();
    	})
        .collect(Collectors.toList()));
    	Map<String, List<Integer>> unsortedStandings = new HashMap<>();
    	for (Club c : standings.keySet()) {
    	    unsortedStandings.put(
    	        c.getNameClub(),
    	        new ArrayList<>(List.of(
    	            t.getClubPoints(c),
    	            t.getClubScoredGoals(c),
    	            t.getClubConcededGoals(c)
    	        ))
    	    );
    	}
    	Map<String, List<Integer>> sortedStandings = unsortedStandings.entrySet().stream()
    	    .sorted((e1, e2) -> Integer.compare(e2.getValue().get(0), e1.getValue().get(0)))
    	    .collect(Collectors.toMap(
    	        Map.Entry::getKey,
    	        Map.Entry::getValue,
    	        (e1, e2) -> e1, 
    	        LinkedHashMap::new 
    	    ));

    	tDto.setStandings(sortedStandings);

    	return tDto;
    }

    public static PointsTournament toPointsTournament(TournamentDto tDto, List<Club> clubs, List<Match> matches, List<TournamentStanding> standings) {
        PointsTournament pt = new PointsTournament();
        pt.setTournamentName(tDto.getTournamentName());
        pt.setOver(tDto.isOver());
        pt.setClubs(clubs);
        pt.setMatches(matches);
        
        if (standings != null) {
        	pt.setStandings(standings);
        }
        return pt;
    }

}
