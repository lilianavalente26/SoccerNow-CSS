package pt.ul.fc.css.soccernow.presentation.control.match;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.MatchDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

public class MatchAreaController {
    
    @FXML private TextField matchIdField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";
    
    @FXML
    public void initialize() {
        String lastMessage = MainController.getLastOperationMessage();
        if (lastMessage != null && !lastMessage.isEmpty()) {
            showSuccess(lastMessage);
            MainController.clearLastOperationMessage();
        }
    }

    @FXML
    public void registerMatch() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/match/matchRegistration.fxml"
        );
    }

    @FXML
    public void updateResults() {
        String matchId = matchIdField.getText().trim();
        if (matchId.isEmpty()) {
            showError("Please enter a match ID");
            return;
        }

        try {
            Long id = Long.parseLong(matchId);
            ResponseEntity<MatchDto> response = restTemplate.getForEntity(
                BASE_URL + "/match/" + id, 
                MatchDto.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                MatchDto matchData = response.getBody();
                if (matchData != null) {
                    MainController.getInstance().loadMatchUpdate(matchData, id);
                } else {
                    showError("Received invalid match data from server");
                }
            }
        } catch (NumberFormatException e) {
            showError("Match ID must be a number");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                showError("Match not found");
            } else {
                showError("Error searching match: " + e.getMessage());
            }
        } catch (Exception e) {
            showError("Error searching match: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }
    
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
    }

    @FXML
    public void backToMenu() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/menu/menu.fxml"
        );
    }

    public static void setLastOperationMessage(String message) {
        MainController.setLastOperationMessage(message);
    }
}