package pt.ul.fc.css.soccernow.presentation.control.club;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.ClubDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

public class ClubAreaController {
    
    @FXML private TextField clubIdField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    
    private static String lastSearchedId;
    private static Long lastRegisteredClubId;
    private static String lastOperationMessage;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";

    @FXML
    public void initialize() {
        if (lastRegisteredClubId != null) {
            showSuccess("Club registered successfully with id " + lastRegisteredClubId);
            lastRegisteredClubId = null;
        }
        if (lastOperationMessage != null) {
            showSuccess(lastOperationMessage);
            lastOperationMessage = null;
        }
    }
    
    public static String getLastSearchedId() {
        return lastSearchedId;
    }
    
    public static void setLastRegisteredClubId(Long id) {
        lastRegisteredClubId = id;
    }
    
    public static void setLastOperationMessage(String message) {
        lastOperationMessage = message;
    }
    
    @FXML
    public void registerClub() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/club/clubRegistration.fxml"
        );
    }

    @FXML
    public void searchClub() {
        String clubId = clubIdField.getText().trim();
        if (clubId.isEmpty()) {
            showError("Please enter a club ID");
            return;
        }
        
        lastSearchedId = clubId;

        try {
            ResponseEntity<ClubDto> response = restTemplate.getForEntity(
                BASE_URL + "/club/" + clubId, 
                ClubDto.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                ClubDto club = response.getBody();
                Long id = Long.parseLong(clubId);
                MainController.getInstance().loadClubEdit(id, club);
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().is4xxClientError()) {
                showError("Club not found: " + e.getResponseBodyAsString());
            } else {
                showError("Error searching club: " + e.getMessage());
            }
        } catch (Exception e) {
            showError("Error searching club: " + e.getMessage());
        }
    }

    @FXML
    public void manageTeams() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/team/teamArea.fxml"
        );
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