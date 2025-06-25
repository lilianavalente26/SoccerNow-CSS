package pt.ul.fc.css.soccernow.presentation.control.club;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.ClubDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

public class ClubEditController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private Label nameError;

    private ClubDto club;
    private Long clubId;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";

    public void setClubData(Long id, ClubDto club) {
        this.clubId = id;
        this.club = club;

        idField.setText(id.toString());
        nameField.setText(club.getName());
    }

    @FXML
    public void saveChanges() {
        nameError.setVisible(false);

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            nameError.setText("Name cannot be empty.");
            nameError.setVisible(true);
            return;
        }

        club.setName(nameField.getText().trim());

        try {
            restTemplate.put(BASE_URL + "/club/" + clubId, club);
            ClubAreaController.setLastOperationMessage("Club updated successfully");
            MainController.getInstance().loadClubArea();
        } catch (Exception e) {
            nameError.setText("Failed to update club: " + e.getMessage());
            nameError.setVisible(true);
        }
    }

    @FXML
    public void deleteClub() {
        try {
            restTemplate.delete(BASE_URL + "/club/" + clubId);
            ClubAreaController.setLastOperationMessage("Club deleted successfully");
            MainController.getInstance().loadClubArea();
        } catch (Exception e) {
            nameError.setText("Failed to delete club: " + e.getMessage());
            nameError.setVisible(true);
        }
    }

    @FXML
    public void cancelChanges() {
        MainController.getInstance().loadClubArea();
    }
}
