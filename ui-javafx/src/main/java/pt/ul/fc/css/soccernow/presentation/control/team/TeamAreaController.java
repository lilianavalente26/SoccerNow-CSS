package pt.ul.fc.css.soccernow.presentation.control.team;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.TeamDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

public class TeamAreaController {
    
    @FXML private TextField teamIdField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    
    private static String lastSearchedId;
    private static Long lastRegisteredTeamId;
    private static String lastOperationMessage;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";

    @FXML
    public void initialize() {
        if (lastRegisteredTeamId != null) {
            showSuccess("Team registered successfully with id " + lastRegisteredTeamId);
            lastRegisteredTeamId = null;
        }
        if (lastOperationMessage != null) {
            showSuccess(lastOperationMessage);
            lastOperationMessage = null;
        }
    }
    
    public static String getLastSearchedId() {
        return lastSearchedId;
    }
    
    public static void setLastRegisteredTeamId(Long id) {
        lastRegisteredTeamId = id;
    }
    
    public static void setLastOperationMessage(String message) {
        lastOperationMessage = message;
    }
    
    @FXML
    public void registerTeam() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/team/teamRegistration.fxml"
        );
    }

    @FXML
    public void searchTeam() {
        String teamId = teamIdField.getText().trim();
        if (teamId.isEmpty()) {
            showError("Please enter a team ID");
            return;
        }
        
        lastSearchedId = teamId;

        try {
            ResponseEntity<TeamDto> response = restTemplate.getForEntity(
                BASE_URL + "/team/" + teamId, 
                TeamDto.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                MainController.getInstance().loadTeamEdit(response.getBody());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                showError("Team not found: " + e.getResponseBodyAsString());
            } else {
                showError("Error searching team: " + e.getMessage());
            }
        } catch (Exception e) {
            showError("Error searching team: " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> successLabel.setVisible(false));
        delay.play();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> errorLabel.setVisible(false));
        delay.play();
    }

    @FXML
    public void backToClubArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/club/clubArea.fxml"
        );
    }
}