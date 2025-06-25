package pt.ul.fc.css.soccernow.presentation.control.club;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

public class ClubRegistrationController {

    @FXML private TextField nameField;
    @FXML private Label nameError;
    @FXML private Label generalError;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";

    @FXML
    public void initialize() {
        nameError.setVisible(false);
        generalError.setVisible(false);
        
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) nameError.setVisible(false);
        });
    }

    @FXML
    public void registerClub() {
        nameError.setVisible(false);
        generalError.setVisible(false);

        boolean isValid = true;

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            nameError.setText("Club name is required!");
            nameError.setVisible(true);
            isValid = false;
        }
        
        if (!isValid) return;

        try {
            String clubName = nameField.getText().trim();
            
            ResponseEntity<Long> response = restTemplate.postForEntity(
                BASE_URL + "/register/club/" + clubName,
                null,
                Long.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                ClubAreaController.setLastRegisteredClubId(response.getBody());
                returnToClubArea();
            }
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errorMessage = e.getResponseBodyAsString();
                nameError.setText(errorMessage);
                nameError.setVisible(true);
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
        returnToClubArea();
    }

    private void returnToClubArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/club/clubArea.fxml"
        );
    }
}