package pt.ul.fc.css.soccernow.teams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.soccernow.controllers.ClubController;
import pt.ul.fc.css.soccernow.controllers.TeamController;
import pt.ul.fc.css.soccernow.controllers.UserController;
import pt.ul.fc.css.soccernow.dto.*;
import pt.ul.fc.css.soccernow.exceptions.InvalidTeamDataException;
import pt.ul.fc.css.soccernow.repositories.TeamRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TeamTests {

    @Autowired private TeamController tc;
    @Autowired private ClubController cc;
    @Autowired private UserController uc;
    @Autowired private TeamRepository tr;

    private Long clubId;
    private Long goalkeeperId;
    private Long teamId;

    @BeforeEach
    public void setup() {
        ResponseEntity<Long> clubResponse = cc.registerClub("Club 1");
        clubId = clubResponse.getBody();

        PlayerDto goalkeeperDto = new PlayerDto();
        goalkeeperDto.setName("Test Goalkeeper");
        goalkeeperDto.setPreferedPosition("GOALKEEPER");
        ResponseEntity<Long> playerResponse = uc.registerPlayer(goalkeeperDto);
        goalkeeperId = playerResponse.getBody();

        TeamDto teamDto = createTeamDto();
        ResponseEntity<Long> teamResponse = tc.registerTeam(teamDto);
        teamId = teamResponse.getBody();
    }

    private TeamDto createTeamDto() {
        TeamDto teamDto = new TeamDto();
        teamDto.setClub(clubId);
        teamDto.setGoalkeeper(goalkeeperId);
        return teamDto;
    }

    @Test
    public void testRegisterTeam() {
        TeamDto teamDto = createTeamDto();
        ResponseEntity<Long> response = tc.registerTeam(teamDto);
        
        assertNotNull(response.getBody());
        assertTrue(tr.findById(response.getBody()).isPresent());
    }

    @Test
    public void testRegisterTeamWithInvalidClub() {
        TeamDto teamDto = createTeamDto();
        teamDto.setClub(999L);
        
        InvalidTeamDataException exception = assertThrows(
            InvalidTeamDataException.class,
            () -> tc.registerTeam(teamDto)
        );
        assertEquals("Club with Id 999 does not exist!", exception.getMessage());
    }

    @Test
    public void testRegisterTeamWithInvalidGoalkeeper() {
        TeamDto teamDto = createTeamDto();
        teamDto.setGoalkeeper(999L);
        
        InvalidTeamDataException exception = assertThrows(
            InvalidTeamDataException.class,
            () -> tc.registerTeam(teamDto)
        );
        assertEquals("Goalkeeper with Id 999 does not exist!", exception.getMessage());
    }

    @Test
    public void testRegisterTeamWithNonGoalkeeperPlayer() {
        PlayerDto playerDto = new PlayerDto();
        playerDto.setName("Test Forward");
        playerDto.setPreferedPosition("FORWARD");
        playerDto.setTeams(new ArrayList<>());
        ResponseEntity<Long> playerResponse = uc.registerPlayer(playerDto);
        Long playerId = playerResponse.getBody();

        TeamDto teamDto = createTeamDto();
        teamDto.setGoalkeeper(playerId);
        
        InvalidTeamDataException exception = assertThrows(
            InvalidTeamDataException.class,
            () -> tc.registerTeam(teamDto)
        );
        assertEquals("Goalkeeper with Id " + playerId + " does not have valid position!", exception.getMessage());
    }

    @Test
    public void testGetTeam() {
        ResponseEntity<TeamDto> response = tc.getTeam(teamId);
        TeamDto team = response.getBody();
        
        assertNotNull(team);
        assertEquals(clubId, team.getClub());
        assertEquals(goalkeeperId, team.getGoalkeeper());
        assertTrue(team.getPlayers().isEmpty());
    }

    @Test
    public void testGetNonExistentTeam() {
        InvalidTeamDataException exception = assertThrows(
            InvalidTeamDataException.class,
            () -> tc.getTeam(9999L)
        );
        assertEquals("Team with Id 9999 does not exist!", exception.getMessage());
    }

    @Test
    public void testDeleteTeam() {
        ResponseEntity<String> response = tc.deleteTeam(teamId);
        assertEquals("Team with ID " + teamId + " removed!", response.getBody());
        assertFalse(tr.findById(teamId).isPresent());
    }

    @Test
    public void testDeleteNonExistentTeam() {
        InvalidTeamDataException exception = assertThrows(
            InvalidTeamDataException.class,
            () -> tc.deleteTeam(9999L)
        );
        assertEquals("Team with Id 9999 does not exist!", exception.getMessage());
    }

    @Test
    public void testUpdateTeam() {
        PlayerDto newGoalkeeperDto = new PlayerDto();
        newGoalkeeperDto.setName("New Goalkeeper");
        newGoalkeeperDto.setPreferedPosition("GOALKEEPER");
        newGoalkeeperDto.setTeams(new ArrayList<>());
        ResponseEntity<Long> newGoalieResponse = uc.registerPlayer(newGoalkeeperDto);
        Long newGoalkeeperId = newGoalieResponse.getBody();

        TeamDto updateDto = createTeamDto();
        updateDto.setGoalkeeper(newGoalkeeperId);
        
        ResponseEntity<String> response = tc.updateTeam(teamId, updateDto);
        assertEquals("Team with ID " + teamId + " updated!", response.getBody());
        
        TeamDto updatedTeam = tc.getTeam(teamId).getBody();
        assertEquals(newGoalkeeperId, updatedTeam.getGoalkeeper());
    }

    @Test
    public void testSearchTeams() {
        TeamDto searchCriteria = new TeamDto();
        searchCriteria.setClub(clubId);
        
        ResponseEntity<String> response = tc.searchTeams(searchCriteria);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Teams:"));
    }
}
