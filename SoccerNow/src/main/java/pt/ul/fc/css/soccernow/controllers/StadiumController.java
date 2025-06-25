package pt.ul.fc.css.soccernow.controllers;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.exceptions.InvalidStadiumDataException;
import pt.ul.fc.css.soccernow.handlers.StadiumHandler;


@RestController
@RequestMapping("")
public class StadiumController {

    private final StadiumHandler stadiumHandler;

    @Autowired
    public StadiumController(StadiumHandler stadiumService) {
        this.stadiumHandler = stadiumService;
    }

    @PostMapping("/register/stadium/{stadiumName}")
    @ApiOperation(value = "Register a new stadium", response = Long.class)
    public ResponseEntity<Long> registerStadium(@PathVariable String stadiumName) {
        long stadiumId = stadiumHandler.handleRegisterStadium(stadiumName);
        return ResponseEntity.ok(stadiumId);
    }

    @GetMapping("/stadium/{stadiumId}")
    @ApiOperation(value = "Get stadium data", response = String.class)
    public ResponseEntity<String> getStadium(@PathVariable Long stadiumId) {
        String stadiumName = stadiumHandler.handleGetStadium(stadiumId);
        return ResponseEntity.ok(stadiumName);
    }

    @DeleteMapping("/stadium/{stadiumId}")
    @ApiOperation(value = "Delete a stadium", response = String.class)
    public ResponseEntity<String> deleteStadium(@PathVariable Long stadiumId) {
        stadiumHandler.handleDeleteStadium(stadiumId);
        return ResponseEntity.ok("Stadium with ID " + stadiumId + " removed!");
    }
    
    @ExceptionHandler(InvalidStadiumDataException.class)
    public ResponseEntity<String> handleInvalidUserData(InvalidStadiumDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
