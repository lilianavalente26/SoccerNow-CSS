package pt.ul.fc.css.soccernow.controllers;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.ul.fc.css.soccernow.dto.TeamDto;
import pt.ul.fc.css.soccernow.exceptions.InvalidTeamDataException;
import pt.ul.fc.css.soccernow.handlers.TeamHandler;

@RestController
@RequestMapping("")
public class TeamController {

	private final TeamHandler teamHandler;
	
	@Autowired
	public TeamController(TeamHandler teamService) {
		this.teamHandler = teamService;
	}
	
	@PostMapping("/register/team")
	@ApiOperation(value = "Register a new team", response = Long.class)
	public ResponseEntity<Long> registerTeam(@RequestBody TeamDto teamData) {
		long teamId = teamHandler.handleRegisterTeam(teamData);
		return ResponseEntity.ok(teamId);
	}
	
	@GetMapping("/team/{id}")
	@ApiOperation(value = "Get team data", response = TeamDto.class)
	public ResponseEntity<TeamDto> getTeam(@PathVariable Long id) {
		TeamDto teamData = teamHandler.handleGetTeam(id);
		return ResponseEntity.ok(teamData);
	}
	
	@DeleteMapping("/team/{id}")
	@ApiOperation(value = "Delete a team", response = String.class)
	public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
		teamHandler.handleDeleteTeam(id);
		return ResponseEntity.ok("Team with ID " + id + " removed!");
	}
	
	@PutMapping("/team/{id}")
	@ApiOperation(value = "Update a team", response = String.class)
	public ResponseEntity<String> updateTeam(@PathVariable Long id, @RequestBody TeamDto teamData) {
		teamHandler.handleUpdateTeam(id, teamData);
		return ResponseEntity.ok("Team with ID " + id + " updated!");
	}
	
	@GetMapping("team/search")
	@ApiOperation(value = "Search teams", response = String.class)
	public ResponseEntity<String> searchTeams(TeamDto teamDto) {
		String teams = teamHandler.handleSeatchTeams(teamDto);
		return ResponseEntity.ok("Teams: " + teams);
	}
	
	@ExceptionHandler(InvalidTeamDataException.class)
    public ResponseEntity<String> handleInvalidUserData(InvalidTeamDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
	
}
