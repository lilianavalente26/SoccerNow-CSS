package pt.ul.fc.css.soccernow.controllers;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pt.ul.fc.css.soccernow.dto.PlayerDto;
import pt.ul.fc.css.soccernow.dto.RefereeDto;
import pt.ul.fc.css.soccernow.exceptions.InvalidUserDataException;
import pt.ul.fc.css.soccernow.handlers.UserHandler;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelPlayer;
import pt.ul.fc.css.soccernow.viewmodels.ViewModelReferee;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserHandler userHandler;

    @Autowired
    public UserController(UserHandler userHandler) {
        this.userHandler = userHandler;
    }

    // -------------------- PLAYER ENDPOINTS --------------------

    @PostMapping("/players/register")
    @ApiOperation("Register a new player")
    public ResponseEntity<Long> registerPlayer(@RequestBody PlayerDto userData) {
        long userId = userHandler.handleRegisterPlayer(userData);
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/players/{id}")
    @ApiOperation("Get player data")
    public ResponseEntity<PlayerDto> getPlayer(@PathVariable Long id) {
        return ResponseEntity.ok(userHandler.handleGetPlayer(id));
    }

    @PutMapping("/players/{id}/update/all")
    @ApiOperation("Update all player information")
    public ResponseEntity<String> updatePlayer(@PathVariable Long id, @RequestBody PlayerDto userData) {
        userHandler.handleUpdatePlayer(id, userData);
        return ResponseEntity.ok("Player with ID " + id + " updated!");
    }

    @DeleteMapping("/players/{id}")
    @ApiOperation("Delete a player")
    public ResponseEntity<String> deletePlayer(@PathVariable Long id) {
        userHandler.handleDeletePlayer(id);
        return ResponseEntity.ok("Player with ID " + id + " removed!");
    }

    @PutMapping("/players/{id}/update/name/{newName}")
    @ApiOperation("Update player's name")
    public ResponseEntity<String> updatePlayerName(@PathVariable Long id, @PathVariable String newName) {
        userHandler.handleUpdatePlayerName(id, newName);
        return ResponseEntity.ok("Player with ID " + id + " updated!");
    }

    @PutMapping("/players/{id}/update/position/{newPosition}")
    @ApiOperation("Update player's position")
    public ResponseEntity<String> updatePlayerPosition(@PathVariable Long id, @PathVariable String newPosition) {
        userHandler.handleUpdatePlayerPosition(id, newPosition);
        return ResponseEntity.ok("Player with ID " + id + " updated!");
    }

    @PutMapping("/players/{id}/update/add-team/{teamId}")
    @ApiOperation("Add team to player")
    public ResponseEntity<String> addPlayerTeam(@PathVariable Long id, @PathVariable Long teamId) {
        userHandler.handleUpdateAddPlayerTeam(id, teamId);
        return ResponseEntity.ok("Player with ID " + id + " updated!");
    }

    @PutMapping("/players/{id}/update/remove-team/{teamId}")
    @ApiOperation("Remove team from player")
    public ResponseEntity<String> removePlayerTeam(@PathVariable Long id, @PathVariable Long teamId) {
        userHandler.handleUpdateRemovePlayerTeam(id, teamId);
        return ResponseEntity.ok("Player with ID " + id + " updated!");
    }

    // -------------------- REFEREE ENDPOINTS --------------------

    @PostMapping("/referees/register")
    @ApiOperation("Register a new referee")
    public ResponseEntity<Long> registerReferee(@RequestBody RefereeDto userData) {
        long userId = userHandler.handleRegisterReferee(userData);
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/referees/{id}")
    @ApiOperation("Get referee data")
    public ResponseEntity<RefereeDto> getReferee(@PathVariable Long id) {
        return ResponseEntity.ok(userHandler.handleGetReferee(id));
    }

    @PutMapping("/referees/{id}/update/all")
    @ApiOperation("Update all referee information")
    public ResponseEntity<String> updateReferee(@PathVariable Long id, @RequestBody RefereeDto userData) {
        userHandler.handleUpdateReferee(id, userData);
        return ResponseEntity.ok("Referee with ID " + id + " updated!");
    }

    @DeleteMapping("/referees/{id}")
    @ApiOperation("Delete a referee")
    public ResponseEntity<String> deleteReferee(@PathVariable Long id) {
        userHandler.handleDeleteReferee(id);
        return ResponseEntity.ok("Referee with ID " + id + " removed!");
    }

    @PutMapping("/referees/{id}/update/name/{newName}")
    @ApiOperation("Update referee's name")
    public ResponseEntity<String> updateRefereeName(@PathVariable Long id, @PathVariable String newName) {
        userHandler.handleUpdateRefereeName(id, newName);
        return ResponseEntity.ok("Referee with ID " + id + " updated!");
    }

    @PutMapping("/referees/{id}/update/certificate/{newCertificate}")
    @ApiOperation("Update referee's certificate status")
    public ResponseEntity<String> updateRefereeCertificate(@PathVariable Long id, @PathVariable boolean newCertificate) {
        userHandler.handleUpdateRefereeCertificate(id, newCertificate);
        return ResponseEntity.ok("Referee with ID " + id + " updated!");
    }

    @PutMapping("/referees/{id}/update/add-match/{matchId}")
    @ApiOperation("Add match to referee")
    public ResponseEntity<String> addRefereeMatch(@PathVariable Long id, @PathVariable Long matchId) {
        userHandler.handleUpdateAddRefereeMatch(id, matchId);
        return ResponseEntity.ok("Referee with ID " + id + " updated!");
    }

    @PutMapping("/referees/{id}/update/remove-match/{matchId}")
    @ApiOperation("Remove match from referee")
    public ResponseEntity<String> removeRefereeMatch(@PathVariable Long id, @PathVariable Long matchId) {
        userHandler.handleUpdateRemoveRefereeMatch(id, matchId);
        return ResponseEntity.ok("Referee with ID " + id + " updated!");
    }


// -------------------- PLAYER FILTERS --------------------

    @GetMapping("/player/filter-by/name/{name}")
    @ApiOperation(value = "Filter players by name", response = String.class)
    public ResponseEntity<List<ViewModelPlayer>> filterPlayersByName(@RequestParam String name) {
        List<ViewModelPlayer> players = userHandler.handleFilterPlayersByName(name);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/player/filter-by/position/{position}")
    @ApiOperation(value = "Filter players by position", response = String.class)
    public ResponseEntity<List<ViewModelPlayer>> filterPlayersByPosition(@RequestParam String position) {
        List<ViewModelPlayer> players = userHandler.handleFilterPlayersByPosition(position);
        return ResponseEntity.ok(players);
    }


    @GetMapping("/player/filter-by/min-max-goals-scored/{maxGoals}/{minGoals}")
    @ApiOperation(value = "Filter players by the number of goals they scored", response = String.class)
    public ResponseEntity<List<ViewModelPlayer>> filterPlayersByGoalsScored(@RequestParam(required = false) Integer maxGoals,
                                                                        @RequestParam(required = false) Integer minGoals) {
        List<ViewModelPlayer> players = userHandler.handleFilterPlayersByGoalsScored(maxGoals, minGoals);
        return ResponseEntity.ok(players);
    }
    @GetMapping("/player/filter-by/min-max-cards-received/{maxCards}/{minCards}")
    @ApiOperation(value = "Filter players by the number of card they received", response = String.class)
    public ResponseEntity<List<ViewModelPlayer>> filterPlayersByCardsReceived(@RequestParam(required = false) Integer maxCards,
                                                                        @RequestParam(required = false) Integer minCards) {
        List<ViewModelPlayer> players = userHandler.handleFilterPlayersByCardsReceived(maxCards, minCards);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/player/filter-by/min-max-games-played/{maxGames}/{minGames}")
    @ApiOperation(value = "Filter players by the number of games they played", response = String.class)
    public ResponseEntity<List<ViewModelPlayer>> filterPlayersByGamesPlayed(@RequestParam(required = false) Integer maxGames,
                                                                        @RequestParam(required = false) Integer minGames) {
        List<ViewModelPlayer> players = userHandler.handleFilterPlayersByGamesPlayed(maxGames, minGames);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/player/find-all")
    @ApiOperation(value = "Find all players", response = List.class)
    public ResponseEntity<List<ViewModelPlayer>> findAllPlayers() {
        List<ViewModelPlayer> players = userHandler.handleFindAllPlayers();
        return ResponseEntity.ok(players);
    }

    // -------------------- REFEREE FILTERS --------------------

    @GetMapping("/referee/filter-by/name/{name}")
    @ApiOperation(value = "Filter referees by name", response = String.class)
    public ResponseEntity<List<ViewModelReferee>> filterRefereesByName(@RequestParam String name) {
        List<ViewModelReferee> referees = userHandler.handleFilterRefereesByName(name);
        return ResponseEntity.ok(referees);
    }

    @GetMapping("/referee/filter-by/min-max-matches-oficialized/{maxGames}/{minGames}")
    @ApiOperation(value = "Filter players by the number of card they received", response = String.class)
    public ResponseEntity<List<ViewModelReferee>> filterRefereesByMatchesOficialized(@RequestParam(required = false) Integer maxGames,
                                                                        @RequestParam(required = false) Integer minGames) {
        List<ViewModelReferee> referees = userHandler.handleFilterRefereesByMatchesOficialized(maxGames, minGames);
        return ResponseEntity.ok(referees);
    }

    @GetMapping("/referee/filter-by/min-max-cards-shown/{maxCards}/{minCards}")
    @ApiOperation(value = "Filter players by the number of card they received", response = String.class)
    public ResponseEntity<List<ViewModelReferee>> filterRefereesByCardsShown(@RequestParam(required = false) Integer maxCards,
                                                                               @RequestParam(required = false) Integer minCards) {
        List<ViewModelReferee> referees = userHandler.handleFilterRefereesByCardsShown(maxCards, minCards);
        return ResponseEntity.ok(referees);
    }

    @GetMapping("/referee/find-all")
    @ApiOperation(value = "Find all referees", response = List.class)
    public ResponseEntity<List<ViewModelReferee>> findAllReferees() {
        List<ViewModelReferee> referees = userHandler.handleFindAllReferees();
        return ResponseEntity.ok(referees);
    }


    // -------------------- EXCEPTION HANDLER --------------------

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<String> handleInvalidUserData(InvalidUserDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
