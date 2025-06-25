package pt.ul.fc.css.soccernow.presentation.control.match;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.MatchDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MatchRegistrationController {

    @FXML private TextField team1Field;
    @FXML private TextField team2Field;
    @FXML private TextField refereeField;
    @FXML private TextField refereeListField;
    @FXML private TextField dateField;
    @FXML private TextField timeField;
    @FXML private TextField stadiumField;
    @FXML private TextField tournamentField;
    
    @FXML private Label team1Error;
    @FXML private Label team2Error;
    @FXML private Label refereeError;
    @FXML private Label refereeListError;
    @FXML private Label dateError;
    @FXML private Label timeError;
    @FXML private Label stadiumError;
    @FXML private Label tournamentError;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";

    @FXML
    public void registerMatch() {
        clearErrors();
        
        try {
            MatchDto match = new MatchDto();

            Long team1Id = parseLongField(team1Field.getText(), "Team 1 ID", team1Error);
            if (team1Id == null) return;
            match.setTeam1(team1Id);

            Long team2Id = parseLongField(team2Field.getText(), "Team 2 ID", team2Error);
            if (team2Id == null) return;
            match.setTeam2(team2Id);

            Long refereeId = parseLongField(refereeField.getText(), "Principal Referee ID", refereeError);
            if (refereeId == null) return;
            match.setPrincipalReferee(refereeId);

            if (!refereeListField.getText().isEmpty()) {
                List<Long> refereeIds = parseRefereeList(refereeListField.getText(), "Referee List", refereeListError);
                if (refereeIds == null) return;
                match.setReferees(refereeIds);
            }

            LocalDate date = parseDateField(dateField.getText(), "Date (YYYY-MM-DD)", dateError);
            if (date == null) return;
            match.setDate(date);

            LocalTime time = parseTimeField(timeField.getText(), "Time (HH:MM)", timeError);
            if (time == null) return;
            match.setTime(time);

            Long stadiumId = parseLongField(stadiumField.getText(), "Stadium ID", stadiumError);
            if (stadiumId == null) return;
            match.setStadium(stadiumId);

            if (!tournamentField.getText().isEmpty()) {
                Long tournamentId = parseLongField(tournamentField.getText(), "Tournament ID", tournamentError);
                if (tournamentId == null) return;
                match.setTournament(tournamentId);
            }

            ResponseEntity<Long> response = restTemplate.postForEntity(
                BASE_URL + "/register/match",
                match,
                Long.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                MatchAreaController.setLastOperationMessage("Match registered successfully with ID: " + response.getBody());
                returnToMatchArea();
            }
            
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            if (errorMessage.contains("Team")) {
                if (errorMessage.contains("Team 1")) {
                    showFieldError(team1Error, errorMessage);
                } else if (errorMessage.contains("Team 2")) {
                    showFieldError(team2Error, errorMessage);
                }
            } else if (errorMessage.contains("Referee")) {
                showFieldError(refereeError, errorMessage);
            } else if (errorMessage.contains("Stadium")) {
                showFieldError(stadiumError, errorMessage);
            } else if (errorMessage.contains("Tournament")) {
                showFieldError(tournamentError, errorMessage);
            } else {
                showFieldError(team1Error, errorMessage);
            }
        } catch (Exception e) {
            showFieldError(team1Error, "Registration failed: " + e.getMessage());
        }
    }
    
    private Long parseLongField(String text, String fieldName, Label errorLabel) {
        if (text == null || text.trim().isEmpty()) {
            showFieldError(errorLabel, fieldName + " is required");
            return null;
        }
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            showFieldError(errorLabel, fieldName + " must be a valid number");
            return null;
        }
    }
    
    private List<Long> parseRefereeList(String text, String fieldName, Label errorLabel) {
        try {
            return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            showFieldError(errorLabel, fieldName + " must be comma-separated referee IDs (ID,ID,ID)");
            return null;
        }
    }
    
    private LocalDate parseDateField(String text, String fieldName, Label errorLabel) {
        if (text == null || text.trim().isEmpty()) {
            showFieldError(errorLabel, fieldName + " is required");
            return null;
        }
        try {
            return LocalDate.parse(text.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            showFieldError(errorLabel, fieldName + " must be in YYYY-MM-DD format");
            return null;
        }
    }
    
    private LocalTime parseTimeField(String text, String fieldName, Label errorLabel) {
        if (text == null || text.trim().isEmpty()) {
            showFieldError(errorLabel, fieldName + " is required");
            return null;
        }
        try {
            return LocalTime.parse(text.trim(), DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException e) {
            showFieldError(errorLabel, fieldName + " must be in HH:MM format");
            return null;
        }
    }
    
    private void showFieldError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void clearErrors() {
        team1Error.setVisible(false);
        team2Error.setVisible(false);
        refereeError.setVisible(false);
        refereeListError.setVisible(false);
        dateError.setVisible(false);
        timeError.setVisible(false);
        stadiumError.setVisible(false);
        tournamentError.setVisible(false);
    }

    @FXML
    public void cancelRegistration() {
        returnToMatchArea();
    }
    
    private void returnToMatchArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/match/matchArea.fxml"
        );
    }
}