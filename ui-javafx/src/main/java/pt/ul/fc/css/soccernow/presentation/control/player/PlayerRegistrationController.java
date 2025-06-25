package pt.ul.fc.css.soccernow.presentation.control.player;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.PlayerDto;
import pt.ul.fc.css.soccernow.presentation.model.TeamDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerRegistrationController {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private TextField teamsField;
    @FXML private Label nameError;
    @FXML private Label positionError;
    @FXML private Label teamsError;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/users";

    @FXML
    public void initialize() {
        positionComboBox.getItems().addAll(
            "GOALKEEPER",
            "DEFENDER",
            "MIDFIELDER",
            "FORWARD"
        );

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) nameError.setVisible(false);
        });
        
        positionComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) positionError.setVisible(false);
        });
        
        teamsField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) teamsError.setVisible(false);
        });
    }

    @FXML
    public void registerPlayer() {
        nameError.setVisible(false);
        positionError.setVisible(false);
        teamsError.setVisible(false);

        boolean isValid = true;
        
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            nameError.setText("Name cannot be empty!");
            nameError.setVisible(true);
            isValid = false;
        }
        
        if (positionComboBox.getValue() == null) {
            positionError.setText("Position is required!");
            positionError.setVisible(true);
            isValid = false;
        }
        
        if (!isValid) return;

        try {
            PlayerDto player = new PlayerDto();
            player.setName(nameField.getText());
            player.setPreferedPosition(positionComboBox.getValue());

            if (!teamsField.getText().isEmpty()) {
                try {
                    List<Long> teamIds = Arrays.stream(teamsField.getText().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toList());

                    for (Long teamId : teamIds) {
                        try {
                            restTemplate.getForEntity(
                                "http://localhost:8080/team/" + teamId, 
                                TeamDto.class
                            );
                        } catch (HttpClientErrorException e) {
                            teamsError.setText("Team with ID " + teamId + " does not exist!");
                            teamsError.setVisible(true);
                            return;
                        }
                    }
                    player.setTeams(teamIds);
                } catch (NumberFormatException e) {
                    teamsError.setText("Invalid team ID format - must be numbers separated by commas");
                    teamsError.setVisible(true);
                    return;
                }
            }

            ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL + "/players/register",
                player,
                String.class
            );

            PlayerAreaController.setLastOperationMessage("Player registered successfully with ID " + response.getBody());
            returnToPlayerArea();
            
        } catch (HttpClientErrorException e) {
            // Error handling remains the same
        } catch (Exception e) {
            teamsError.setText("Registration failed: " + e.getMessage());
            teamsError.setVisible(true);
        }
    }

    @FXML
    public void cancelRegistration() {
        returnToPlayerArea();
    }

    private void returnToPlayerArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/player/playerArea.fxml"
        );
    }
}