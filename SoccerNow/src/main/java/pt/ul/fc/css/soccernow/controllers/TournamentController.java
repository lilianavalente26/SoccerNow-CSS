package pt.ul.fc.css.soccernow.controllers;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pt.ul.fc.css.soccernow.dto.TournamentDto;
import pt.ul.fc.css.soccernow.entities.Tournament;
import pt.ul.fc.css.soccernow.exceptions.InvalidTournamentDataException;
import pt.ul.fc.css.soccernow.handlers.TournamentHandler;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelPointsTournament;

import java.util.List;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

    private final TournamentHandler tournamentHandler;

    @Autowired
    public TournamentController(TournamentHandler tournamentHandler) {
        this.tournamentHandler = tournamentHandler;
    }

    @PostMapping("/register/tournament")
    @ApiOperation(value = "Register a new championship", response = Long.class)
    public ResponseEntity<Long> registerTournament(@RequestBody TournamentDto tournamentDto) {
        long tournamentId = tournamentHandler.handleRegisterTournament(tournamentDto);
        return ResponseEntity.ok(tournamentId);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get championship data", response = TournamentDto.class)
    public ResponseEntity<TournamentDto> getChampionship(@PathVariable Long id) {
        TournamentDto TournamentData = tournamentHandler.handleGetTournament(id);
        return ResponseEntity.ok(TournamentData);
    }
    
    @GetMapping("/{id}/standings")
    @ApiOperation(value = "Get the tournament standings", response = String.class)
    public ResponseEntity<String> getTournamentStandings(@PathVariable Long id) {
    	String data = tournamentHandler.handleGetTournamentStandings(id);
    	return ResponseEntity.ok(data);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a tournament", response = String.class)
    public ResponseEntity<String> deleteTournament(@PathVariable Long id) {
    	tournamentHandler.handleDeleteTournament(id);
    	return ResponseEntity.ok("Tournament with Id " + id + " removed!");
    }
    
    @PutMapping("/{id}/update/name/{newName}")
    @ApiOperation(value = "Updates the name of the tournament", response = String.class)
    public ResponseEntity<String> updateTournamentName(@PathVariable Long id, @PathVariable String newName) {
    	tournamentHandler.handleUpdateTournamentName(id, newName);
    	return ResponseEntity.ok("Tournament with Id " + id + " updated!");
    }
    
    @PutMapping("{id}/update/setStateAsFinished")
    @ApiOperation(value = "Updates if the tournament is over", response = String.class)
    public ResponseEntity<String> updateTournamentIsOver(@PathVariable Long id) {
    	tournamentHandler.handleUpdateTournamentOver(id);
    	return ResponseEntity.ok("Tournament with Id " + id + " updated!");
    }
    
    @PutMapping("{id}/update/clubs/{addClub}/{removeClub}")
    @ApiOperation(value = "Updates the clubs of the tournament", response = String.class)
    public ResponseEntity<String> updateTournamentClubs(@PathVariable Long id, @RequestParam(required = false) Long addClub, @RequestParam(required = false) Long removeClub) {
    	tournamentHandler.handleUpdateTournamentClubs(id, addClub, removeClub);
    	return ResponseEntity.ok("Tournament with Id " + id + " updated!");
    }
    
    @PutMapping("{id}/update/cancel-match/{matchId}")
    @ApiOperation(value = "Cancels a match from a tournament", response = String.class) 
    public ResponseEntity<String> updateTournamentCancelMatch(@PathVariable Long id, @PathVariable Long matchId) {
    	tournamentHandler.handleUpdateTournamentCancelMatch(id, matchId);
    	return ResponseEntity.ok("Tournament with Id " + id + " updated!");
    }

    @GetMapping("/search/by-status/{isOver}")
    @ApiOperation(value = "Search championships by parameters", response = TournamentDto.class)
    public ResponseEntity<List<TournamentDto>> searchChampionshipByStatus(@PathVariable Boolean isOver) {
        List<TournamentDto> tournamentsData = tournamentHandler.handleSearchTournamentByStatus(isOver);
        return ResponseEntity.ok(tournamentsData);
    }

    @GetMapping("/filter-by/name/{name}")
    @ApiOperation(value = "Search up a tournament by it's name", response = TournamentDto.class)
    public ResponseEntity<List<TournamentDto>> searchTournamentByName(@PathVariable String name) {
        List<TournamentDto> tournamentsData = tournamentHandler.handleSearchTournamentByName(name);
        return ResponseEntity.ok(tournamentsData);
    }

    @GetMapping("/filter-by/club/{clubId}")
    @ApiOperation(value = "Search up a tournament by a club's id", response = TournamentDto.class)
    public ResponseEntity<List<TournamentDto>> searchTournamentByClub(@PathVariable Long clubId) {
        List<TournamentDto> tournamentsData = tournamentHandler.handleSearchTournamentByClub(clubId);
        return ResponseEntity.ok(tournamentsData);
    }

//    @GetMapping("/filter-by/number-matches-played/")
//    @ApiOperation(value = "Search up a tournament by a team's id", response = TournamentDto.class)
//    public ResponseEntity<List<PointsTournament>> filterByMatchesPlayed(@RequestParam(required = true) boolean alreadyPlayed,
//                                                                     @RequestParam(required = false) Integer minMatches,
//                                                                     @RequestParam(required = false) Integer maxMatches) {
//        List<Tournament> tournamentsData = tournamentHandler.filterByMatchesPlayed(alreadyPlayed, minMatches, maxMatches);
//        return ResponseEntity.ok(tournamentsData);
//    }
    
    @GetMapping("/filter-by/")
    @ApiOperation(value = "Filter tournaments", response = ViewModelPointsTournament.class)
    public ResponseEntity<List<ViewModelPointsTournament>> filterTournament(
    		@RequestParam(required = false) String name,
    		@RequestParam(required = false) String clubName,
    		@RequestParam(required = false) Integer minRealizedMatches,
    		@RequestParam(required = false) Integer maxRealizedMatches,
    		@RequestParam(required = false) Integer minToDoMatches,
    		@RequestParam(required = false) Integer maxToDoMatches) {
    	
    	List<ViewModelPointsTournament> tournaments = tournamentHandler.filterByTournament(name, clubName, minRealizedMatches, maxRealizedMatches, minToDoMatches, maxToDoMatches);
    	return ResponseEntity.ok(tournaments);
    }


    // ----------------------- EXCEPTION HANDLERS -----------------------

    @ExceptionHandler(InvalidTournamentDataException.class)
    public ResponseEntity<String> handleInvalidTournamentData(InvalidTournamentDataException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}

