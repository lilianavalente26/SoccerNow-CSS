package pt.ul.fc.css.soccernow.presentation.control.referee;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import pt.ul.fc.css.soccernow.presentation.model.RefereeDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

/**
 * RefereeAreaController is responsible for managing the referee area in the application.
 * It handles referee registration and listing of referees.
 */
public class RefereeAreaController {
    
    @FXML
    private StackPane refereeAreaPane;

    @FXML private TextField refereeIdField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private static String lastSearchedId;
    private static Long lastRegisteredRefereeId;
    private static String lastOperationMessage;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/users";

    @FXML
    public void initialize() {
        if (lastRegisteredRefereeId != null) {
            showSuccess("Referee registered successfully with id " + lastRegisteredRefereeId);
            lastRegisteredRefereeId = null;
        }
        if (lastOperationMessage != null) {
            showSuccess(lastOperationMessage);
            lastOperationMessage = null;
        }
    }

    @FXML
    public void registerReferee() {
        MainController.getInstance().loadView("/pt/ul/fc/css/soccernow/presentation/view/referee/refereeRegistration.fxml");
    }

    public static String getLastSearchedId() {
        return lastSearchedId;
    }

    public static void setLastRegisteredRefereeId(Long id) {
        lastRegisteredRefereeId = id;
    }

    public static void setLastOperationMessage(String message) {
        lastOperationMessage = message;
    }
    
    @FXML
    public void searchReferee() {
        String refereeId = refereeIdField.getText().trim();
        if (refereeId.isEmpty()) {
            showError("Please enter a referee ID");
            return;
        }
        
        lastSearchedId = refereeId;

        try {
            ResponseEntity<RefereeDto> response = restTemplate.getForEntity(
                BASE_URL + "/referees/" + refereeId, 
                RefereeDto.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            	MainController.getInstance().loadRefereeEdit(Long.valueOf(refereeId), response.getBody());
            } else {
                showError("Referee not found");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                showError("Referee not found: " + e.getResponseBodyAsString());
            } else {
                showError("Error searching referee1: " + e.getMessage());
            }
        } catch (Exception e) {
            showError("Error searching referee2: " + e.getMessage());
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