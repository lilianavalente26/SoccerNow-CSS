package pt.ul.fc.css.soccernow.presentation.control.tournament;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.TournamentDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

public class TournamentAreaController {
    
    @FXML private TextField tournamentIdField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    
    private static String lastOperationMessage;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/tournament";

    @FXML
    public void initialize() {
        if (lastOperationMessage != null) {
            showSuccess(lastOperationMessage);
            lastOperationMessage = null;
        }
    }
    
    public static void setLastOperationMessage(String message) {
        lastOperationMessage = message;
    }

    @FXML
    public void createTournament() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/tournament/tournamentRegistration.fxml"
        );
    }

    @FXML
    public void searchTournament() {
        String tournamentId = tournamentIdField.getText().trim();
        if (tournamentId.isEmpty()) {
            showError("Please enter a tournament ID");
            return;
        }

        try {
            ResponseEntity<TournamentDto> response = restTemplate.getForEntity(
                    BASE_URL + "/" + tournamentId, 
                    TournamentDto.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    MainController.getInstance().loadTournamentEdit(
                        Long.parseLong(tournamentId), 
                        response.getBody()
                    );
                }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                showError("Tournament not found: " + e.getResponseBodyAsString());
            } else {
                showError("Error searching tournament: " + e.getMessage());
            }
        } catch (Exception e) {
            showError("Error searching tournament: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> errorLabel.setVisible(false));
        delay.play();
    }
    
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> successLabel.setVisible(false));
        delay.play();
    }

    @FXML
    public void backToMenu() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/menu/menu.fxml"
        );
    }
}