package pt.ul.fc.css.soccernow.presentation.control.player;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.PlayerDto;
import pt.ul.fc.css.soccernow.presentation.model.TeamDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayerEditController {
    
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private TextField teamsField;
    @FXML private Label nameError;
    @FXML private Label teamsError;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/users";
    private String playerId;

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
        
        teamsField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) teamsError.setVisible(false);
        });
    }

    public void setPlayerData(PlayerDto player, String id) {
        this.playerId = id;
        idField.setText(id);
        nameField.setText(player.getName() != null ? player.getName() : "");
        positionComboBox.setValue(player.getPreferedPosition());
        
        if (player.getTeams() != null && !player.getTeams().isEmpty()) {
            teamsField.setText(player.getTeams().stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        } else {
            teamsField.setText("");
        }
    }

    @FXML
    public void saveChanges() {
        nameError.setVisible(false);
        teamsError.setVisible(false);

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            nameError.setText("Name cannot be empty!");
            nameError.setVisible(true);
            return;
        }
        
        try {
            PlayerDto player = new PlayerDto();
            player.setName(nameField.getText());
            player.setPreferedPosition(positionComboBox.getValue());

            if (!teamsField.getText().isEmpty()) {
                try {
                    List<Long> teams = Arrays.stream(teamsField.getText().split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toList());

                    for (Long teamId : teams) {
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
                    player.setTeams(teams);
                } catch (NumberFormatException e) {
                    teamsError.setText("Invalid team ID format - must be numbers separated by commas");
                    teamsError.setVisible(true);
                    return;
                }
            }

            restTemplate.put(
                BASE_URL + "/players/" + playerId + "/update/all",
                player
            );
            
            PlayerAreaController.setLastOperationMessage("Player updated successfully!");
            returnToPlayerArea();
            
        } catch (HttpClientErrorException e) {
            // Error handling remains the same
        } catch (Exception e) {
            teamsError.setText("An error occurred: " + e.getMessage());
            teamsError.setVisible(true);
        }
    }

    @FXML
    public void deletePlayer() {
        try {
            restTemplate.delete(BASE_URL + "/players/" + playerId);
            PlayerAreaController.setLastOperationMessage("Player deleted successfully!");
            returnToPlayerArea();
        } catch (Exception e) {
            teamsError.setText("Error deleting player: " + e.getMessage());
            teamsError.setVisible(true);
        }
    }

    private void returnToPlayerArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/player/playerArea.fxml"
        );
    }
    
    @FXML
    public void cancelChanges() {
        returnToPlayerArea();
    }
}