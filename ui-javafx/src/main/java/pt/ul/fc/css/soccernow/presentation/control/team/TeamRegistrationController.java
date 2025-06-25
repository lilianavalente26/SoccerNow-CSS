package pt.ul.fc.css.soccernow.presentation.control.team;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.TeamDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TeamRegistrationController {

    @FXML private TextField clubIdField;
    @FXML private TextField playersField;
    @FXML private TextField goalkeeperField;
    @FXML private Label clubIdError;
    @FXML private Label playersError;
    @FXML private Label goalkeeperError;
    @FXML private Label generalError;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";

    @FXML
    public void initialize() {
        clubIdError.setVisible(false);
        playersError.setVisible(false);
        goalkeeperError.setVisible(false);
        generalError.setVisible(false);
        
        clubIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) clubIdError.setVisible(false);
        });
        
        playersField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) playersError.setVisible(false);
        });
        
        goalkeeperField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) goalkeeperError.setVisible(false);
        });
    }

    @FXML
    public void registerTeam() {
        clubIdError.setVisible(false);
        playersError.setVisible(false);
        goalkeeperError.setVisible(false);
        generalError.setVisible(false);

        boolean isValid = true;

        if (clubIdField.getText() == null || clubIdField.getText().trim().isEmpty()) {
            clubIdError.setText("Club ID is required!");
            clubIdError.setVisible(true);
            isValid = false;
        }

        if (goalkeeperField.getText() == null || goalkeeperField.getText().trim().isEmpty()) {
            goalkeeperError.setText("Goalkeeper ID is required!");
            goalkeeperError.setVisible(true);
            isValid = false;
        }
        
        if (!isValid) return;

        try {
            TeamDto team = new TeamDto();

            try {
                team.setClub(Long.parseLong(clubIdField.getText().trim()));
            } catch (NumberFormatException e) {
                clubIdError.setText("Club ID must be a valid number");
                clubIdError.setVisible(true);
                return;
            }

            if (!playersField.getText().isEmpty()) {
                try {
                    List<Long> playerIds = Arrays.stream(playersField.getText().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                    team.setPlayers(playerIds);
                } catch (NumberFormatException e) {
                    playersError.setText("Player IDs must be numbers separated by commas");
                    playersError.setVisible(true);
                    return;
                }
            }

            try {
                team.setGoalkeeper(Long.parseLong(goalkeeperField.getText().trim()));
            } catch (NumberFormatException e) {
                goalkeeperError.setText("Goalkeeper ID must be a valid number");
                goalkeeperError.setVisible(true);
                return;
            }

            ResponseEntity<Long> response = restTemplate.postForEntity(
                BASE_URL + "/register/team",
                team,
                Long.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                TeamAreaController.setLastRegisteredTeamId(response.getBody());
                returnToTeamArea();
            }
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errorMessage = e.getResponseBodyAsString();
                
                if (errorMessage.contains("Club")) {
                    clubIdError.setText(errorMessage);
                    clubIdError.setVisible(true);
                } else if (errorMessage.contains("Player")) {
                    playersError.setText(errorMessage);
                    playersError.setVisible(true);
                } else if (errorMessage.contains("Goalkeeper")) {
                    goalkeeperError.setText(errorMessage);
                    goalkeeperError.setVisible(true);
                } else {
                    generalError.setText(errorMessage);
                    generalError.setVisible(true);
                }
            } else {
                generalError.setText("Registration failed: " + e.getStatusText());
                generalError.setVisible(true);
            }
        } catch (Exception e) {
            generalError.setText("Registration failed: " + e.getMessage());
            generalError.setVisible(true);
        }
    }

    @FXML
    public void cancelRegistration() {
        returnToTeamArea();
    }

    private void returnToTeamArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/team/teamArea.fxml"
        );
    }
}