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
import pt.ul.fc.css.soccernow.entities.Player;
import pt.ul.fc.css.soccernow.exceptions.InvalidUserDataException;
import pt.ul.fc.css.soccernow.repositories.PlayerRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@Transactional
public class AdvancedPlayerTests {

    @Autowired private UserController uc;
    @Autowired private TeamController tc;
    @Autowired private ClubController cc;
    @Autowired private MatchController mc;
    @Autowired private StadiumController sc;
    
    @Autowired private PlayerRepository pr;
    
    private Long playerId;
    private Long goalkeeperId;
    private Long validRefereeId;
    private Long clubId;
    private Long stadiumId;
    private Long teamId;
    
    @BeforeEach
    void setUp() {
    	// Create validReferee
    	RefereeDto refereeDto = new RefereeDto();
    	refereeDto.setName("Referee1");
    	refereeDto.setHasCertificate(true);
    	refereeDto.setMatches(null);
    	validRefereeId = uc.registerReferee(refereeDto).getBody();
    	
    	// Create validPlayer
        PlayerDto playerDto = new PlayerDto();
        playerDto.setName("Test Player");
        playerDto.setPreferedPosition("MIDFIELDER");
        playerId = uc.registerPlayer(playerDto).getBody();
        
        // Create validGoalkeeper
        PlayerDto goalkeeperDto = new PlayerDto();
        goalkeeperDto.setName("Test Goalkeeper");
        goalkeeperDto.setPreferedPosition("GOALKEEPER");
        goalkeeperId = uc.registerPlayer(goalkeeperDto).getBody();
        
        // Create validClub
        clubId = cc.registerClub("Test Club").getBody();
        
        // Create validStadium
        stadiumId = sc.registerStadium("Luz").getBody();
                
        // Create validTeam
        TeamDto teamDto = new TeamDto();
        teamDto.setClub(clubId);
        teamDto.setGoalkeeper(goalkeeperId);
        teamId = tc.registerTeam(teamDto).getBody();
    }
    
    @Test
    void updatePlayer_WithFutureMatches_ThrowsException() {
    	
    	Long t1Id = tc.registerTeam(createTeamDto(clubId, Collections.singletonList(playerId), goalkeeperId, null, null)).getBody();
    	Long t2Id = tc.registerTeam(createTeamDto(clubId, null, goalkeeperId, null, null)).getBody();
    	
    	MatchDto matchDto = createMatchDto(validRefereeId, Collections.singletonList(validRefereeId), t1Id, t2Id, LocalDate.now().plusDays(1), LocalTime.now(), stadiumId);
        mc.registerMatch(matchDto);
        
        PlayerDto updateDto = createPlayerDto("UpdateName", "MIDFIELDER", null);
         
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.updatePlayer(playerId, updateDto)
        );
        assertEquals("Player has future matches to play!", exception.getMessage());
    }
    
    @Test
    void deletePlayer_WithFutureMatches_ThrowsException() {
        Long t1Id = tc.registerTeam(createTeamDto(clubId, Collections.singletonList(playerId), goalkeeperId, null, null)).getBody();
        Long t2Id = tc.registerTeam(createTeamDto(clubId, null, goalkeeperId, null, null)).getBody();
        
        MatchDto matchDto = createMatchDto(validRefereeId, Collections.singletonList(validRefereeId), t1Id, t2Id, LocalDate.now().plusDays(1), LocalTime.now(), stadiumId);
        mc.registerMatch(matchDto);
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.deletePlayer(playerId)
        );
        assertEquals("Player has future matches to play!", exception.getMessage());
    }
    
    @Test
    void updatePlayer_WithInvalidTeam_ThrowsException() {
        PlayerDto updateDto = new PlayerDto();
        updateDto.setName("Test Player");
        updateDto.setPreferedPosition("MIDFIELDER");
        List<Long> teams = new ArrayList<>();
        teams.add(999L);
        updateDto.setTeams(teams);
        
        InvalidUserDataException exception = assertThrows(
            InvalidUserDataException.class,
            () -> uc.updatePlayer(playerId, updateDto)
        );
        assertEquals("Team with Id 999 does not exist!", exception.getMessage());
        
        Player player = pr.findById(playerId).get();
        assertTrue(player.getTeamsIds().isEmpty());
        assertEquals("Test Player", player.getName());
    }
    
    @Test
    void updatePlayer_ComplexTeamChanges() {
        PlayerDto addTeamDto = new PlayerDto();
        addTeamDto.setName("Test Player");
        addTeamDto.setPreferedPosition("MIDFIELDER");
        List<Long> teams = new ArrayList<>();
        teams.add(teamId);
        addTeamDto.setTeams(teams);
        uc.updatePlayer(playerId, addTeamDto);
        
        Player player = pr.findById(playerId).get();
        assertEquals(1, player.getTeamsIds().size());
        assertTrue(player.getTeamsIds().contains(teamId));
        
        PlayerDto anotherGoalkeeperDto = new PlayerDto();
        anotherGoalkeeperDto.setName("Another Goalkeeper");
        anotherGoalkeeperDto.setPreferedPosition("GOALKEEPER");
        Long anotherGoalkeeperId = uc.registerPlayer(anotherGoalkeeperDto).getBody();
        
        TeamDto anotherTeamDto = new TeamDto();
        anotherTeamDto.setClub(clubId);
        anotherTeamDto.setGoalkeeper(anotherGoalkeeperId);
        Long anotherTeamId = tc.registerTeam(anotherTeamDto).getBody();
        
        List<Long> twoTeams = new ArrayList<>();
        twoTeams.add(teamId);
        twoTeams.add(anotherTeamId);
        addTeamDto.setTeams(twoTeams);
        uc.updatePlayer(playerId, addTeamDto);
        
        player = pr.findById(playerId).get();
        assertEquals(2, player.getTeamsIds().size());
        assertTrue(player.getTeamsIds().contains(teamId));
        assertTrue(player.getTeamsIds().contains(anotherTeamId));
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
}