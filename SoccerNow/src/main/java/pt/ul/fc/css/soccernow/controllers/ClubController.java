package pt.ul.fc.css.soccernow.controllers;

import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.ul.fc.css.soccernow.dto.*;
import pt.ul.fc.css.soccernow.exceptions.InvalidClubDataException;
import pt.ul.fc.css.soccernow.handlers.ClubHandler;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelClub;

@RestController
@RequestMapping("")
public class ClubController {
	
	private final ClubHandler clubHandler;
	
	@Autowired
    public ClubController(ClubHandler clubService) {
        this.clubHandler = clubService;
    }
	
	@PostMapping("/register/club/{name}")
	@ApiOperation(value = "Register a new club", response = Long.class)
	public ResponseEntity<Long> registerClub(@PathVariable String name) {
		long userId = clubHandler.handleRegisterClub(name);
		return ResponseEntity.ok(userId);
	}
	
	@GetMapping("/club/{id}")
	@ApiOperation(value = "Get a club", response = ClubDto.class)
	public ResponseEntity<ClubDto> getClub(@PathVariable Long id) {
		ClubDto cDto = clubHandler.handleGetClub(id);
		return ResponseEntity.ok(cDto);
	}
	
	@GetMapping("/club/filter/{name}/{minPlayers}/{maxPlayers}/{nWins}/"
			+ "{nDraws}/{nLosses}/{nAchievements}/{achievementPosition}/{missingPlayerPosition}")
	@ApiOperation(value = "Filter search for clubs", response = String.class)
	public ResponseEntity<List<ViewModelClub>> serchFilterClub(@RequestParam(required = false) String name,
			@RequestParam(required = false) Integer minPlayers, @RequestParam(required = false) Integer maxPlayers,
			@RequestParam(required = false) Integer nWins, @RequestParam(required = false) Integer nDraws,
			@RequestParam(required = false) Integer nLosses, @RequestParam(required = false) Integer nAchievements,
			@RequestParam(required = false) Integer achievementPosition, @RequestParam(required = false) String missingPlayerPosition) {
		List<ViewModelClub> clubs = clubHandler.handleSearchFilterClub(name, minPlayers, maxPlayers,
				nWins, nDraws, nLosses, nAchievements, achievementPosition, missingPlayerPosition);
		return ResponseEntity.ok(clubs);
	}
	
	@DeleteMapping("/club/{id}")
	@ApiOperation(value = "Delete a club", response = String.class)
	public ResponseEntity<String> deletePlayer(@PathVariable Long id) {
		clubHandler.handleDeleteClub(id);
		return ResponseEntity.ok("Club with ID " + id + " removed!");
	}
	
	@GetMapping("/club/matchHistory/{id}")
	@ApiOperation(value = "Get match history of a club", response = String.class)
	public ResponseEntity<String> getMatchHistory(@PathVariable Long id) {
		String matches = clubHandler.handleGetMatchHistory(id);
		return ResponseEntity.ok("MatchHistory: " + matches);
	}
	
	@GetMapping("/club/achievements/{id}")
	@ApiOperation(value = "Get achievements from club", response = String.class)
	public ResponseEntity<String> getAchievements(@PathVariable Long id) {
		String data = clubHandler.handleGetAchievements(id);
		return ResponseEntity.ok(data);
	}
		
	@ExceptionHandler(InvalidClubDataException.class)
    public ResponseEntity<String> handleInvalidUserData(InvalidClubDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
	
}
