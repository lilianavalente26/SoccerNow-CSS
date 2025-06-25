package pt.ul.fc.css.soccernow.matches;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.soccernow.controllers.*;
import pt.ul.fc.css.soccernow.dto.*;
import pt.ul.fc.css.soccernow.exceptions.InvalidMatchDataException;
import pt.ul.fc.css.soccernow.repositories.MatchRepository;
import pt.ul.fc.css.soccernow.repositories.MatchStatisticsRepository;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelMatch;

@SpringBootTest
@Transactional
public class MatchTests {

    @Autowired private MatchController mc;
    @Autowired private UserController uc;
    @Autowired private TeamController tc;
    @Autowired private ClubController cc;
    @Autowired private StadiumController sc;
    
    @Autowired private MatchRepository mr;
    @Autowired private MatchStatisticsRepository msr;
    
    private Long club1Id;
    private Long club2Id;
    
    private Long stadium1Id;
    private Long stadium2Id;
    
    private List<Long> validReferees;
    private List<Long> validPlayers;
    
    private Long validTeam1;
    private Long validTeam2;
    
    private MatchDto validMatchDto;
    private MatchDto morningMatchDto;
    private MatchDto afternoonMatchDto;
    private MatchDto nightMatchDto;
    
    @BeforeEach
    public void setup() {
        club1Id = cc.registerClub("Club 1").getBody();
        club2Id = cc.registerClub("Club 2").getBody();
        
        stadium1Id = sc.registerStadium("Alvalade").getBody();
        stadium2Id = sc.registerStadium("Estadio da Luz").getBody();
        
        Long r1 = uc.registerReferee(createRefereeDto("r1", true, null)).getBody();
        Long r2 = uc.registerReferee(createRefereeDto("r2", true, null)).getBody();
        Long r3 = uc.registerReferee(createRefereeDto("r3", false, null)).getBody();
        
        validReferees = new ArrayList<>();
        validReferees.add(r1);
        validReferees.add(r2);
        validReferees.add(r3);
        
        Long p1 = uc.registerPlayer(createPlayerDto("player1", "GOALKEEPER", null)).getBody();
        Long p2 = uc.registerPlayer(createPlayerDto("player2", "DEFENDER", null)).getBody();
        Long p3 = uc.registerPlayer(createPlayerDto("player3", "MIDFIELDER", null)).getBody();
        Long p4 = uc.registerPlayer(createPlayerDto("player4", "MIDFIELDER", null)).getBody();
        Long p5 = uc.registerPlayer(createPlayerDto("player5", "FORWARD", null)).getBody();
        
        Long p6 = uc.registerPlayer(createPlayerDto("player6", "GOALKEEPER", null)).getBody();
        Long p7 = uc.registerPlayer(createPlayerDto("player7", "DEFENDER", null)).getBody();
        Long p8 = uc.registerPlayer(createPlayerDto("player8", "MIDFIELDER", null)).getBody();
        Long p9 = uc.registerPlayer(createPlayerDto("player9", "MIDFIELDER", null)).getBody();
        Long p10 = uc.registerPlayer(createPlayerDto("player10", "FORWARD", null)).getBody();
        
        validPlayers = new ArrayList<>();
        validPlayers.add(p1);
        validPlayers.add(p2);
        validPlayers.add(p3);
        validPlayers.add(p4);
        validPlayers.add(p5);
        validPlayers.add(p6);
        validPlayers.add(p7);
        validPlayers.add(p8);
        validPlayers.add(p9);
        validPlayers.add(p10);
        
        validTeam1 = tc.registerTeam(createTeamDto(club1Id, validPlayers.subList(0, 5), validPlayers.get(0), null, null)).getBody();
        
        validTeam2 = tc.registerTeam(createTeamDto(club2Id, validPlayers.subList(5, 10), validPlayers.get(5), null, null)).getBody();
        
        // Create match DTOs for different times
        validMatchDto = createMatchDto(validReferees.get(0), validReferees.subList(0, 1), validTeam1, validTeam2, 
            LocalDate.of(2026, 5, 5), LocalTime.of(12, 0, 0), stadium1Id);
            
        morningMatchDto = createMatchDto(validReferees.get(0), validReferees.subList(0, 1), validTeam1, validTeam2, 
            LocalDate.of(2026, 5, 5), LocalTime.of(9, 0, 0), stadium1Id);
            
        afternoonMatchDto = createMatchDto(validReferees.get(0), validReferees.subList(0, 1), validTeam1, validTeam2, 
            LocalDate.of(2026, 5, 5), LocalTime.of(15, 0, 0), stadium2Id);
            
        nightMatchDto = createMatchDto(validReferees.get(0), validReferees.subList(0, 1), validTeam1, validTeam2, 
            LocalDate.of(2026, 5, 5), LocalTime.of(20, 0, 0), stadium1Id);
    }
    
    @Test
    public void testRegisterMatch() {
      MatchDto matchDto = validMatchDto;
      Long matchId = mc.registerMatch(matchDto).getBody();
      
      assertNotNull(matchId);
      assertTrue(mr.findById(matchId).isPresent());
    }
	
    @Test
    public void testRegisterMatchWithInvalidTeam1() {
    	MatchDto matchDto = validMatchDto;
    	matchDto.setTeam1(9999L);
    	
    	InvalidMatchDataException exception = assertThrows(
            InvalidMatchDataException.class,
            () -> mc.registerMatch(matchDto)
        );
        assertEquals("Team 1 does not exist in the database", exception.getMessage());
    }
    
    @Test
    public void testRegisterMatchWithInvalidTeam2() {
        MatchDto matchDto = validMatchDto;
        matchDto.setTeam2(9999L);
        
        InvalidMatchDataException exception = assertThrows(
            InvalidMatchDataException.class,
            () -> mc.registerMatch(matchDto)
        );
        assertEquals("Team 2 does not exist in the database", exception.getMessage());
    }
    
    @Test
    public void testRegisterMatchWithInvalidReferee() {
        MatchDto matchDto = validMatchDto;
        matchDto.setPrincipalReferee(9999L);
        
        InvalidMatchDataException exception = assertThrows(
            InvalidMatchDataException.class,
            () -> mc.registerMatch(matchDto)
        );
        assertEquals("Principal referee does not exist in the database", exception.getMessage());
    }
    
    @Test
    public void testRegisterMatchWithInvalidStadium() {
        MatchDto matchDto = validMatchDto;
        matchDto.setStadium(9999L);
        
        InvalidMatchDataException exception = assertThrows(
            InvalidMatchDataException.class,
            () -> mc.registerMatch(matchDto)
        );
        assertEquals("Stadium does not exist in the database", exception.getMessage());
    }
    
    @Test
    public void testRegisterMatchWithSameTeams() {
        MatchDto matchDto = validMatchDto;
        matchDto.setTeam2(validTeam1);
        
        InvalidMatchDataException exception = assertThrows(
            InvalidMatchDataException.class,
            () -> mc.registerMatch(matchDto)
        );
        assertEquals("A match cannot have the same team for both sides!", exception.getMessage());
    }
    
    @Test
    public void testGetMatch() {
    	Long matchId = mc.registerMatch(validMatchDto).getBody();
    	
        ResponseEntity<MatchDto> response = mc.getMatch(matchId);
        MatchDto match = response.getBody();
        
        assertNotNull(match);
        assertEquals(validTeam1, match.getTeam1());
        assertEquals(validTeam2, match.getTeam2());
        assertEquals(validReferees.get(0), match.getPrincipalReferee());
        assertEquals(stadium1Id, match.getStadium());
        assertEquals(LocalDate.of(2026, 5, 5), match.getDate());
        assertEquals(LocalTime.of(12, 0, 0), match.getTime());
    }
    
    @Test
    public void testGetNonExistentMatch() {
        InvalidMatchDataException exception = assertThrows(
            InvalidMatchDataException.class,
            () -> mc.getMatch(9999L)
        );
        assertEquals("Match does not exist in the database", exception.getMessage());
    }
    
    @Test
    public void testDeleteMatch() {
    	Long matchId = mc.registerMatch(validMatchDto).getBody();
    	
        ResponseEntity<String> response = mc.deleteMatch(matchId);
        assertEquals("Match with ID " + matchId + " removed!", response.getBody());
        assertFalse(mr.findById(matchId).isPresent());
    }
    
    @Test
    public void testDeleteNonExistentMatch() {
        InvalidMatchDataException exception = assertThrows(
            InvalidMatchDataException.class,
            () -> mc.deleteMatch(9999L)
        );
        assertEquals("Match does not exist in the database, so it cannot be deleted.", exception.getMessage());
    }
    
    @Test
    public void testRegisterGoal() {
    	MatchDto matchDto = validMatchDto;
    	Long matchId = mc.registerMatch(matchDto).getBody();
        
    	
        ResponseEntity<String> response = mc.registerGoal(matchId, validPlayers.get(4));
        assertEquals("Goal registered successfully!", response.getBody());
        
        // Verify statistics were updated
        MatchDto match = mc.getMatch(matchId).getBody();
        MatchStatisticsDto stats = mc.getMatchStatisticsFormated(match.getStats()).getBody();
        assertEquals(1, stats.getTeam1_Score());
        assertTrue(stats.getGoals().size() > 0);
    }
    
    @Test
    public void testRegisterGoalWithInvalidPlayer() {
    	MatchDto matchDto = validMatchDto;
    	Long matchId = mc.registerMatch(matchDto).getBody();
    	
        InvalidMatchDataException exception = assertThrows(
            InvalidMatchDataException.class,
            () -> mc.registerGoal(matchId, 9999L)
        );
        assertEquals("Player does not exist in the database", exception.getMessage());
    }
    
    @Test
    public void testRegisterGoalWithPlayerNotInMatch() {
        Long playerId = uc.registerPlayer(createPlayerDto("p1", "FORWARD", null)).getBody();
    	MatchDto matchDto = validMatchDto;
    	Long matchId = mc.registerMatch(matchDto).getBody();
        
        InvalidMatchDataException exception = assertThrows(
            InvalidMatchDataException.class,
            () -> mc.registerGoal(matchId, playerId)
        );
        assertEquals("Player does not belong to any of the teams in the match", exception.getMessage());
    }
    
    @Test
    public void testSetMatchStateAsFinished() {
    	MatchDto matchDto = validMatchDto;
    	Long matchId = mc.registerMatch(matchDto).getBody();
    	
    	ResponseEntity<String> response1 = mc.registerGoal(matchId, validPlayers.get(4));
        assertEquals("Goal registered successfully!", response1.getBody());
    	
        ResponseEntity<String> response2 = mc.setMatchStateAsFinished(matchId);
        String stats = response2.getBody();
        
        assertNotNull(stats);
        assertTrue(stats.equals("Match state is set to finished!"));
        assertEquals(mc.getMatchStatisticsFormated(matchId).getBody().getWinnerTeam(), 1);
        
        Long matchStatsId = mc.getMatchStatisticsFormated(matchId).getBody().getMatchStatisticsId();
        // Verify in repository
        assertTrue(msr.findById(matchStatsId).get().isOver());
    }
    
    @Test
    public void testRegisterYellowCard() {
    	MatchDto matchDto = validMatchDto;
    	Long matchId = mc.registerMatch(matchDto).getBody();
    	
        ResponseEntity<String> response = mc.registerYellowCard(matchId, validPlayers.get(2));
        assertEquals("Yellow Card registered successfully to player " + validPlayers.get(2) + " !", response.getBody());
        
        // Verify statistics were updated
        MatchDto match = mc.getMatch(matchId).getBody();
        MatchStatisticsDto stats = mc.getMatchStatisticsFormated(match.getStats()).getBody();
        assertTrue(stats.getTeam1_Cards().size() > 0);
    }
    
    @Test
    public void testRegisterRedCard() {
    	MatchDto matchDto = validMatchDto;
    	Long matchId = mc.registerMatch(matchDto).getBody();

        ResponseEntity<String> response = mc.registerRedCard(matchId, validPlayers.get(2));
        assertEquals("Red Card registered successfully to player " + validPlayers.get(2) + " !", response.getBody());
        
        // Verify statistics were updated
        MatchDto match = mc.getMatch(matchId).getBody();
        MatchStatisticsDto stats = mc.getMatchStatisticsFormated(match.getStats()).getBody();
        assertTrue(stats.getTeam1_Cards().size() > 0);
    }
    
    @Test
    public void testGetAllMatchStatisticsFormatedAdvanced() {
    	MatchDto matchDto = validMatchDto;
    	Long matchId = mc.registerMatch(matchDto).getBody();
    	
    	ResponseEntity<String> response1 = mc.registerGoal(matchId, validPlayers.get(4));
        assertEquals("Goal registered successfully!", response1.getBody());
        
        ResponseEntity<String> response2 = mc.registerGoal(matchId, validPlayers.get(3));
        assertEquals("Goal registered successfully!", response2.getBody());
        
        ResponseEntity<String> response3 = mc.registerYellowCard(matchId, validPlayers.get(7));
        assertEquals("Yellow Card registered successfully to player " + validPlayers.get(7) + " !", response3.getBody());
    	
        ResponseEntity<String> response4 = mc.registerRedCard(matchId, validPlayers.get(6));
        assertEquals("Red Card registered successfully to player " + validPlayers.get(6) + " !", response4.getBody());
    	
        String response = mc.getAllMatchStatistics(matchId).getBody();
        Long matchStatsID = msr.findById(matchId).get().getMatchStatisticsId();
        		
        assertNotNull(response);
        String correctResponse = "Match ID: " + matchStatsID + "\nMatch Date: 2026-05-05\nMatch Time: 12:00\n" 	+
        						 "Stadium: Alvalade\nTeam 1: Club 1\nTeam 2: Club 2\n"      	+
        						 "Winner Team: No winner yet\nTeam 1 Score: 2\nTeam 2 Score: 0" +
        						 "\nTeam 1 Red Cards:\nTeam 2 Red Cards:\nplayer7\n"	+
        						 "Team 1 Yellow Cards:\nTeam 2 Yellow Cards:\nplayer8\n"	+
        						 "Goals:\nplayer5\nplayer4\n";
        assertEquals(correctResponse, response);
    }
    
    @Test
    public void testGetMatchStatisticsFormated() {
    	MatchDto matchDto = validMatchDto;
    	Long matchId = mc.registerMatch(matchDto).getBody();
    	
        MatchDto match = mc.getMatch(matchId).getBody();
        ResponseEntity<MatchStatisticsDto> response = mc.getMatchStatisticsFormated(match.getStats());
        MatchStatisticsDto stats = response.getBody();
        
        assertNotNull(stats);
        assertEquals(0, stats.getTeam1_Score());
        assertEquals(0, stats.getTeam2_Score());
        assertFalse(stats.isOver());
    }
    
    private static MatchDto createMatchDto(Long pReferee, List<Long> referees, Long t1, Long t2, LocalDate ld, LocalTime lt, Long stadium) {
    	MatchDto matchDto = new MatchDto();
    	matchDto.setPrincipalReferee(pReferee);
    	matchDto.setReferees(referees);
    	matchDto.setTeam1(t1);
    	matchDto.setTeam2(t2);
    	matchDto.setDate(ld);
    	matchDto.setTime(lt);
    	matchDto.setStadium(stadium);
    	return matchDto;
    }
    
    private static RefereeDto createRefereeDto(String name, boolean c, List<Long> matches) {
    	RefereeDto refereeDto = new RefereeDto();
    	refereeDto.setName(name);
    	refereeDto.setHasCertificate(c);
    	refereeDto.setMatches(matches);
    	return refereeDto;
    }
    
    private static PlayerDto createPlayerDto(String name, String position, List<Long> teams) {
    	PlayerDto playerDto = new PlayerDto();
    	playerDto.setName(name);
    	playerDto.setPreferedPosition(position);
    	playerDto.setTeams(teams);
    	return playerDto;
    }
    
    private static TeamDto createTeamDto(Long c, List<Long> players, Long g, List<Long> m1, List<Long> m2) {
    	TeamDto teamDto = new TeamDto();
    	teamDto.setClub(c);
    	teamDto.setPlayers(new ArrayList<>());
    	for (Long p: players) {
    		teamDto.getPlayers().add(p);
    	}
    	teamDto.setGoalkeeper(g);
    	teamDto.setMatchesAsTeam1(m1);
    	teamDto.setMatchesAsTeam2(m2);
    	return teamDto;
    }
    
    // -------------------- FILTER TESTS --------------------
    
    @Test
    public void testFilterMatchesByIsOver() {
        // Clear existing matches to ensure clean test environment
        mr.deleteAll();

        Long match1Id = mc.registerMatch(validMatchDto).getBody();
        Long match2Id = mc.registerMatch(morningMatchDto).getBody();

        mc.setMatchStateAsFinished(match1Id);

        ResponseEntity<List<ViewModelMatch>> finishedResponse = mc.getFilteredMatches(true, null, null, null, null);
        assertEquals(1, finishedResponse.getBody().size(), "Should only find 1 finished match");
        assertEquals(true, finishedResponse.getBody().get(0).isOver(), "Should return the finished match");

        ResponseEntity<List<ViewModelMatch>> ongoingResponse = mc.getFilteredMatches(false, null, null, null, null);
        assertEquals(1, ongoingResponse.getBody().size(), "Should only find 1 ongoing match");
        assertEquals(false, ongoingResponse.getBody().get(0).isOver(), "Should return the ongoing match");
    }
    
    @Test
    public void testFilterMatchesByMinGoals() {
        Long matchId = mc.registerMatch(validMatchDto).getBody();

        mc.registerGoal(matchId, validPlayers.get(4));
        mc.registerGoal(matchId, validPlayers.get(9));

        ResponseEntity<List<ViewModelMatch>> response = mc.getFilteredMatches(null, 2, null, null, null);
        assertEquals(1, response.getBody().size());
        assertEquals(2, response.getBody().get(0).getGoals().size());
        
        // Filter for matches with at least 3 goals (should return empty)
        ResponseEntity<List<ViewModelMatch>> noMatchesResponse = mc.getFilteredMatches(null, 3, null, null, null);
        assertTrue(noMatchesResponse.getBody().isEmpty());
    }
    
    @Test
    public void testFilterMatchesByMaxGoals() {
    	mr.deleteAll();
        Long matchId = mc.registerMatch(validMatchDto).getBody();

        mc.registerGoal(matchId, validPlayers.get(4));

        ResponseEntity<List<ViewModelMatch>> response = mc.getFilteredMatches(null, null, 1, null, null);
        assertEquals(1, response.getBody().size());
        assertEquals(1, response.getBody().get(0).getGoals().size());

        ResponseEntity<List<ViewModelMatch>> noMatchesResponse = mc.getFilteredMatches(null, null, 0, null, null);
        assertTrue(noMatchesResponse.getBody().isEmpty());
    }
    
    @Test
    public void testFilterMatchesByStadium() {
        Long match1Id = mc.registerMatch(validMatchDto).getBody();
        Long match2Id = mc.registerMatch(afternoonMatchDto).getBody();

        ResponseEntity<List<ViewModelMatch>> alvaladeResponse = mc.getFilteredMatches(null, null, null, "Alvalade", null);
        assertEquals(1, alvaladeResponse.getBody().size());
        assertEquals("Alvalade", alvaladeResponse.getBody().get(0).getStadium());

        ResponseEntity<List<ViewModelMatch>> luzResponse = mc.getFilteredMatches(null, null, null, "Estadio da Luz", null);
        assertEquals(1, luzResponse.getBody().size());
        assertEquals("Estadio da Luz", luzResponse.getBody().get(0).getStadium());
    }
    
    @Test
    public void testFilterMatchesByPeriod() {
    	mr.deleteAll();
    	
        Long morningMatchId = mc.registerMatch(morningMatchDto).getBody();
        Long afternoonMatchId = mc.registerMatch(afternoonMatchDto).getBody();
        Long nightMatchId = mc.registerMatch(nightMatchDto).getBody();

        ResponseEntity<List<ViewModelMatch>> morningResponse = mc.getFilteredMatches(null, null, null, null, "MORNING");
        assertEquals(1, morningResponse.getBody().size());

        ResponseEntity<List<ViewModelMatch>> afternoonResponse = mc.getFilteredMatches(null, null, null, null, "AFTERNOON");
        assertEquals(1, afternoonResponse.getBody().size());

        ResponseEntity<List<ViewModelMatch>> nightResponse = mc.getFilteredMatches(null, null, null, null, "NIGHT");
        assertEquals(1, nightResponse.getBody().size());
    }
    
    @Test
    public void testFilterMatchesWithInvalidPeriod() {
        InvalidMatchDataException exception = assertThrows(
            InvalidMatchDataException.class,
            () -> mc.getFilteredMatches(null, null, null, null, "INVALID_PERIOD")
        );
        assertEquals("Only accepts match period: MORNING, AFTERNOON and NIGHT!", exception.getMessage());
    }
    
    @Test
    public void testFilterMatchesWithMultipleCriteria() {
        Long morningMatchId = mc.registerMatch(morningMatchDto).getBody();
        Long afternoonMatchId = mc.registerMatch(afternoonMatchDto).getBody();
        Long nightMatchId = mc.registerMatch(nightMatchDto).getBody();

        mc.registerGoal(nightMatchId, validPlayers.get(4));
        mc.registerGoal(nightMatchId, validPlayers.get(9));
        mc.setMatchStateAsFinished(nightMatchId);

        ResponseEntity<List<ViewModelMatch>> response = mc.getFilteredMatches(true, 2, null, "Alvalade", null);
        assertEquals(1, response.getBody().size());
        assertEquals("Alvalade", response.getBody().get(0).getStadium());
        assertEquals(2, response.getBody().get(0).getGoals().size());
    }
    
}
