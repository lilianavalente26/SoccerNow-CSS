package pt.ul.fc.css.soccernow.users;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.soccernow.controllers.ClubController;
import pt.ul.fc.css.soccernow.controllers.MatchController;
import pt.ul.fc.css.soccernow.controllers.StadiumController;
import pt.ul.fc.css.soccernow.controllers.TeamController;
import pt.ul.fc.css.soccernow.controllers.UserController;
import pt.ul.fc.css.soccernow.dto.ClubDto;
import pt.ul.fc.css.soccernow.dto.MatchDto;
import pt.ul.fc.css.soccernow.dto.PlayerDto;
import pt.ul.fc.css.soccernow.dto.RefereeDto;
import pt.ul.fc.css.soccernow.dto.TeamDto;
import pt.ul.fc.css.soccernow.entities.Referee;
import pt.ul.fc.css.soccernow.entities.Team;
import pt.ul.fc.css.soccernow.exceptions.InvalidUserDataException;
import pt.ul.fc.css.soccernow.repositories.MatchRepository;
import pt.ul.fc.css.soccernow.repositories.RefereeRepository;
import pt.ul.fc.css.soccernow.repositories.TeamRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@Transactional
public class AdvancedRefereeTests {

    @Autowired private UserController uc;
    @Autowired private TeamController tc;
    @Autowired private MatchController mc;
    @Autowired private ClubController cc;
    @Autowired private StadiumController sc;
    
    
    @Autowired private RefereeRepository rr;
    @Autowired private MatchRepository mr;
    @Autowired private TeamRepository tr;
    
    private Long refereeId;
    private Long principalRefereeId;
    private Long goalkeeperId;
    private Long clubId;
    private Long stadiumId;
    private Long teamId;
    private Long matchId;
    
    @BeforeEach
    void setUp() {
    	// Create valid Referee
        RefereeDto refereeDto = new RefereeDto();
        refereeDto.setName("Test Referee");
        refereeDto.setHasCertificate(true);
        refereeId = uc.registerReferee(refereeDto).getBody();
        
        // Create principalRefDto
        RefereeDto principalRefDto = new RefereeDto();
        principalRefDto.setName("Principal Ref");
        principalRefDto.setHasCertificate(true);
        principalRefereeId = uc.registerReferee(principalRefDto).getBody();
        
        // Create goalkeeper
        goalkeeperId = uc.registerPlayer(createPlayerDto("gk1", "GOALKEEPER", null)).getBody();
        
        // Create Club
        clubId = cc.registerClub("Sporting FC").getBody();
        
        // Create Stadium
        stadiumId = sc.registerStadium("Luz").getBody();
        
        
        TeamDto teamDto = createTeamDto(clubId, null, goalkeeperId, null, null);
        teamId = tc.registerTeam(teamDto).getBody();
    }
    
    @Test
    void updateReferee_WithFutureMatches_ThrowsException() {
    	
    	Long t1Id = tc.registerTeam(createTeamDto(clubId, null, goalkeeperId, null, null)).getBody();
    	Long t2Id = tc.registerTeam(createTeamDto(clubId, null, goalkeeperId, null, null)).getBody();   
    	
    	MatchDto matchDto = createMatchDto(refereeId, Collections.singletonList(refereeId), t1Id, t2Id, LocalDate.now().plusDays(1), LocalTime.now(), stadiumId); 
        mc.registerMatch(matchDto).getBody();
    	
        RefereeDto updateDto = createRefereeDto("UpdRef", true, null);
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.updateReferee(refereeId, updateDto)
        );
        assertEquals("Referee has future matches to arbitrate!", exception.getMessage());
    }
    
    @Test
    void deleteReferee_WithFutureMatches_ThrowsException() {
    	Long t1Id = tc.registerTeam(createTeamDto(clubId, null, goalkeeperId, null, null)).getBody();
    	Long t2Id = tc.registerTeam(createTeamDto(clubId, null, goalkeeperId, null, null)).getBody();   
    	
    	MatchDto matchDto = createMatchDto(refereeId, Collections.singletonList(refereeId), t1Id, t2Id, LocalDate.now().plusDays(1), LocalTime.now(), stadiumId); 
        mc.registerMatch(matchDto).getBody();
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.deleteReferee(refereeId)
        );
        assertEquals("Referee has future matches to arbitrate!", exception.getMessage());
    }
    
    @Test
    void updateReferee_WithInvalidMatch_ThrowsException() {
        RefereeDto updateDto = new RefereeDto();
        updateDto.setName("Test Referee");
        updateDto.setHasCertificate(true);
        List<Long> matches = new ArrayList<>();
        matches.add(999L);
        updateDto.setMatches(matches);
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.updateReferee(refereeId, updateDto)
        );
        assertEquals("Match with Id 999 does not exist!", exception.getMessage());
        
        Referee referee = rr.findById(refereeId).get();
        assertTrue(referee.getMatchesIds().isEmpty());
        assertEquals("Test Referee", referee.getName());
    }
    
    @Test
    void updateReferee_ComplexMatchAssignments() {
    	
    	Long t1Id = tc.registerTeam(createTeamDto(clubId, null, goalkeeperId, null, null)).getBody();
    	Long t2Id = tc.registerTeam(createTeamDto(clubId, null, goalkeeperId, null, null)).getBody();   
    	
    	RefereeDto refereeDto = createRefereeDto("DenisReferee", true, new ArrayList<>());
    	Long testRefereeId = uc.registerReferee(refereeDto).getBody();
    	
    	List<Long> refs = new ArrayList<>();
    	refs.add(principalRefereeId);
    	refs.add(testRefereeId);
    	   	
    	
        MatchDto matchDto1 = createMatchDto(principalRefereeId, refs, t1Id, t2Id, LocalDate.now().plusDays(2), LocalTime.now(), stadiumId);
        Long matchId1 = mc.registerMatch(matchDto1).getBody();
        
        MatchDto matchDto2 = createMatchDto(principalRefereeId, Collections.singletonList(principalRefereeId), t1Id, t2Id, LocalDate.now().plusDays(3), LocalTime.now(), stadiumId);
        Long matchId2 = mc.registerMatch(matchDto2).getBody();
        
        RefereeDto updateDto = createRefereeDto("UpdateName", true, Collections.singletonList(matchId1));
        
        uc.updateReferee(testRefereeId, updateDto);
        
        Referee referee = rr.findById(testRefereeId).get();
        assertEquals(1, referee.getMatchesIds().size());
        assertTrue(referee.getMatchesIds().contains(matchId1));
        
        List<Long> matchesUpdate = new ArrayList<>();
        matchesUpdate.add(matchId1);
        matchesUpdate.add(matchId2);
        
        RefereeDto updateDto2 = createRefereeDto("UpdateName2", true, matchesUpdate);
        
        uc.updateReferee(testRefereeId, updateDto2);
        assertEquals(2, referee.getMatchesIds().size());
        assertTrue(referee.getMatchesIds().contains(matchId1));
        assertTrue(referee.getMatchesIds().contains(matchId2));
               
    }
    
    
    @Test
    void deleteReferee_WithPastMatches_Success() {
    	Long t1Id = tc.registerTeam(createTeamDto(clubId, null, goalkeeperId, null, null)).getBody();
    	Long t2Id = tc.registerTeam(createTeamDto(clubId, null, goalkeeperId, null, null)).getBody();   
    	
    	List<Long> refs = new ArrayList<>();
    	refs.add(principalRefereeId);
    	refs.add(refereeId);
    	
    	MatchDto matchDto = createMatchDto(principalRefereeId, refs, t1Id, t2Id, LocalDate.now(), LocalTime.now().plusSeconds(2), stadiumId); 
        mc.registerMatch(matchDto).getBody();
        
        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        
        Long id = refereeId;
        String response = uc.deleteReferee(refereeId).getBody();
        assertEquals("Referee with ID " + id + " removed!", response);
        assertFalse(rr.existsById(id));
    }
    
    private static TeamDto createTeamDto(Long c, List<Long> players, Long g, List<Long> m1, List<Long> m2) {
    	TeamDto teamDto = new TeamDto();
    	teamDto.setClub(c);
    	teamDto.setPlayers(new ArrayList<>());
    	if (players != null) {
    		for (Long p: players) {
        		teamDto.getPlayers().add(p);
        	}
    	}
    	teamDto.setGoalkeeper(g);
    	teamDto.setMatchesAsTeam1(m1);
    	teamDto.setMatchesAsTeam2(m2);
    	return teamDto;
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
}