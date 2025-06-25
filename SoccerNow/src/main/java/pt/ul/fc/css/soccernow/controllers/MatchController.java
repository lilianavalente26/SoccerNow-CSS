package pt.ul.fc.css.soccernow.controllers;

import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pt.ul.fc.css.soccernow.dto.MatchDto;
import pt.ul.fc.css.soccernow.dto.MatchStatisticsDto;
import pt.ul.fc.css.soccernow.exceptions.InvalidMatchDataException;
import pt.ul.fc.css.soccernow.handlers.MatchHandler;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelMatch;

@RestController
@RequestMapping("")
public class MatchController {

    private final MatchHandler matchHandler;

    @Autowired
    public MatchController(MatchHandler matchService) {
        this.matchHandler = matchService;
    }

    @PostMapping("/register/match")
    @ApiOperation(value = "Register a new match", response = Long.class)
    public ResponseEntity<Long> registerMatch(@RequestBody MatchDto matchData) {
        long matchId = matchHandler.handleRegisterMatch(matchData);
        return ResponseEntity.ok(matchId);
    }

    @GetMapping("/match/{id}")
    @ApiOperation(value = "Get match data", response = MatchDto.class)
    public ResponseEntity<MatchDto> getMatch(@PathVariable Long id) {
        MatchDto matchData = matchHandler.handleGetMatch(id);
        return ResponseEntity.ok(matchData);
    }

    @DeleteMapping("/match/{id}")
    @ApiOperation(value = "Delete a match", response = String.class)
    public ResponseEntity<String> deleteMatch(@PathVariable Long id) {
        matchHandler.handleDeleteMatch(id);
        return ResponseEntity.ok("Match with ID " + id + " removed!");
    }

    @ExceptionHandler(InvalidMatchDataException.class)
    public ResponseEntity<String> handleInvalidUserData(InvalidMatchDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @PutMapping("/match/{matchId}/register/goal/player/{playerId}")
    @ApiOperation(value = "Register a goal for a player", response = String.class)
    public ResponseEntity<String> registerGoal(@PathVariable Long matchId, @PathVariable Long playerId) {
        matchHandler.handleRegisterGoal(matchId, playerId);
        return ResponseEntity.ok("Goal registered successfully!");
    }

    @PutMapping("/match/{matchId}/setStateAsFinished")
    @ApiOperation(value = "Set match state as finished", response = String.class)
    public ResponseEntity<String> setMatchStateAsFinished(@PathVariable Long matchId) {
        matchHandler.handleSetMatchStateAsFinished(matchId);
        return ResponseEntity.ok("Match state is set to finished!");
    }

    @PutMapping("/match/{matchId}/register/player/{playerId}/card/yellowCard")
    @ApiOperation(value = "Register a yellow card for a player in a match", response = String.class)
    public ResponseEntity<String> registerYellowCard(@PathVariable Long matchId, @PathVariable Long playerId) {
        matchHandler.handleRegisterYellowCard(matchId, playerId);
        return ResponseEntity.ok("Yellow Card registered successfully to player "+ playerId + " !");
    }

    @PutMapping("/match/{matchId}/register/player/{playerId}/card/redCard")
    @ApiOperation(value = "Register a red card for a player in a match", response = String.class)
    public ResponseEntity<String> registerRedCard(@PathVariable Long matchId, @PathVariable Long playerId) {
        matchHandler.handleRegisterRedCard(matchId, playerId);
        return ResponseEntity.ok("Red Card registered successfully to player "+ playerId + " !");
    }

    @GetMapping("/match/{matchId}/getAllStatisticsFormated")
    @ApiOperation(value = "Get all match statistics", response = String.class)
    public ResponseEntity<String> getAllMatchStatistics(@PathVariable Long matchId) {
        String allMatchStatistics = matchHandler.handleGetAllMatchStatistics(matchId);
        return ResponseEntity.ok(allMatchStatistics);
    }

    @GetMapping("/match/{matchId}/getStatistics")
    @ApiOperation(value = "Get match statistics", response = MatchStatisticsDto.class)
    public ResponseEntity<MatchStatisticsDto> getMatchStatisticsFormated(@PathVariable Long matchId) {
        MatchStatisticsDto matchStatistics = matchHandler.handleGetMatchStatistics(matchId);
        return ResponseEntity.ok(matchStatistics);
    }
    
    @GetMapping("/match/filter/{isOver}/{minGoals}/{maxGoals}/{stadiumName}/{matchPeriod}")
    @ApiOperation(value = "Get filteres amtches", response = String.class)
    public ResponseEntity<List<ViewModelMatch>> getFilteredMatches(
    		@RequestParam(required = false) Boolean isOver, 
    		@RequestParam(required = false) Integer minGoals,
    		@RequestParam(required = false) Integer maxGoals,
    		@RequestParam(required = false) String stadiumName, 
    		@RequestParam(required = false) String matchPeriod)  {
    	List<ViewModelMatch> matches = matchHandler.handleSearchFilterMatch(isOver, minGoals, maxGoals, stadiumName, matchPeriod);
    	return ResponseEntity.ok(matches);
    }
}
