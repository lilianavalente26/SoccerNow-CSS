package pt.ul.fc.css.soccernow.presentation.control.tournament;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.TournamentDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TournamentRegistrationController {

    @FXML private TextField nameField;
    @FXML private TextField clubsField;
    @FXML private Label nameError;
    @FXML private Label clubsError;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/tournament";

    @FXML
    public void registerTournament() {
        nameError.setVisible(false);
        clubsError.setVisible(false);

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            nameError.setText("Tournament name is required");
            nameError.setVisible(true);
            return;
        }

        try {
            TournamentDto tournament = new TournamentDto();
            tournament.setTournamentName(nameField.getText());
            
            if (!clubsField.getText().isEmpty()) {
                try {
                    List<Long> clubs = Arrays.stream(clubsField.getText().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                    tournament.setClubs(clubs);
                } catch (NumberFormatException e) {
                    clubsError.setText("Club IDs must be numbers separated by commas");
                    clubsError.setVisible(true);
                    return;
                }
            }

            ResponseEntity<Long> response = restTemplate.postForEntity(
                BASE_URL + "/register/tournament",
                tournament,
                Long.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                TournamentAreaController.setLastOperationMessage("Tournament created successfully with ID: " + response.getBody());
                returnToTournamentArea();
            }
            
        } catch (HttpClientErrorException e) {
            clubsError.setText("Creation failed: " + e.getResponseBodyAsString());
            clubsError.setVisible(true);
        } catch (Exception e) {
            clubsError.setText("Creation failed: " + e.getMessage());
            clubsError.setVisible(true);
        }
    }

    @FXML
    public void cancelRegistration() {
        returnToTournamentArea();
    }
    
    private void returnToTournamentArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/tournament/tournamentArea.fxml"
        );
    }
}