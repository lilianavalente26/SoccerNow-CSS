package pt.ul.fc.css.soccernow.presentation.control.player;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.PlayerDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

public class PlayerAreaController {
    
    @FXML private TextField playerIdField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    
    private static String lastSearchedId;
    private static String lastOperationMessage;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/users";

    @FXML
    public void initialize() {
        if (lastOperationMessage != null) {
            showSuccess(lastOperationMessage);
            lastOperationMessage = null;
        }
    }
    
    public static String getLastSearchedId() {
        return lastSearchedId;
    }
    
    public static void setLastOperationMessage(String message) {
        lastOperationMessage = message;
    }
    
    @FXML
    public void registerPlayer() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/player/playerRegistration.fxml"
        );
    }

    @FXML
    public void searchPlayer() {
        String playerId = playerIdField.getText().trim();
        if (playerId.isEmpty()) {
            showError("Please enter a player ID");
            return;
        }
        
        lastSearchedId = playerId;

        try {
            ResponseEntity<PlayerDto> response = restTemplate.getForEntity(
                BASE_URL + "/players/" + playerId, 
                PlayerDto.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                MainController.getInstance().loadPlayerEdit(response.getBody(), playerId);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                showError("Player not found: " + e.getResponseBodyAsString());
            } else {
                showError("Error searching player: " + e.getMessage());
            }
        } catch (Exception e) {
            showError("Error searching player: " + e.getMessage());
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
    public void backToMenu() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/menu/menu.fxml"
        );
    }
}