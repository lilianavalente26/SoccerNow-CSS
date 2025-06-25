package pt.ul.fc.css.soccernow.presentation.control.referee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.RefereeDto;
import pt.ul.fc.css.soccernow.presentation.model.MatchDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RefereeEditController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private CheckBox certificateCheckBox;
    @FXML private TextField matchesField;
    @FXML private Label nameError;
    @FXML private Label matchError;

    private final RestTemplate restTemplate = createRestTemplate();
    private final String BASE_URL = "http://localhost:8080/users";
    private RefereeDto currentReferee;
    private Long refereeId;

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper().registerModule(new JavaTimeModule()));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    @FXML
    public void initialize() {
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) nameError.setVisible(false);
        });

        matchesField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) matchError.setVisible(false);
        });
    }

    /**
     * @param id ID of the referee to be edited
     * @param referee RefereeDto object (excluding ID)
     */
    public void setRefereeData(Long id, RefereeDto referee) {
        this.refereeId = id;
        this.currentReferee = referee != null ? referee : new RefereeDto();

        idField.setText(id != null ? id.toString() : RefereeAreaController.getLastSearchedId());
        nameField.setText(referee.getName() != null ? referee.getName() : "");
        certificateCheckBox.setSelected(referee.getHasCertificate());

        if (referee.getMatches() != null && !referee.getMatches().isEmpty()) {
            matchesField.setText(referee.getMatches().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
        } else {
            matchesField.setText("");
        }
    }

    @FXML
    public void saveChanges() {
        nameError.setVisible(false);
        matchError.setVisible(false);

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            nameError.setText("Name cannot be empty!");
            nameError.setVisible(true);
            return;
        }

        try {
            if (refereeId == null) {
                refereeId = Long.parseLong(idField.getText());
            }

            currentReferee.setName(nameField.getText().trim());
            currentReferee.setHasCertificate(certificateCheckBox.isSelected());

            List<Long> existingMatches = currentReferee.getMatches() != null ?
                    currentReferee.getMatches() :
                    Collections.emptyList();

            List<Long> newMatches = Collections.emptyList();

            if (!matchesField.getText().isEmpty()) {
                try {
                    newMatches = Arrays.stream(matchesField.getText().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Long::parseLong)
                            .collect(Collectors.toList());

                    for (Long matchId : newMatches) {
                        try {
                            restTemplate.getForEntity(
                                    "http://localhost:8080/match/" + matchId,
                                    MatchDto.class
                            );
                        } catch (HttpClientErrorException e) {
                            matchError.setText("Match with ID " + matchId + " does not exist!");
                            matchError.setVisible(true);
                            return;
                        }
                    }
                } catch (NumberFormatException e) {
                    matchError.setText("Invalid match ID format - must be numbers separated by commas");
                    matchError.setVisible(true);
                    return;
                }
            }

            currentReferee.setMatches(newMatches);

            restTemplate.put(
                    BASE_URL + "/referees/" + refereeId + "/update/all",
                    currentReferee
            );

            RefereeAreaController.setLastOperationMessage("Referee updated successfully!");
            returnToRefereeArea();

        } catch (HttpClientErrorException e) {
            handleHttpClientError(e);
        } catch (Exception e) {
            matchError.setText("An error occurred: " + e.getMessage());
            matchError.setVisible(true);
        }
    }

    private void handleHttpClientError(HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            String errorMessage = e.getResponseBodyAsString();
            if (errorMessage.contains("Name can not be empty")) {
                nameError.setText(errorMessage);
                nameError.setVisible(true);
            } else if (errorMessage.contains("Match with Id")) {
                matchError.setText(errorMessage);
                matchError.setVisible(true);
            } else if (errorMessage.contains("Referee has future matches")) {
                matchError.setText(errorMessage);
                matchError.setVisible(true);
            } else {
                matchError.setText("Update failed: " + errorMessage);
                matchError.setVisible(true);
            }
        } else {
            matchError.setText("Update failed: " + e.getMessage());
            matchError.setVisible(true);
        }
    }

    @FXML
    public void deleteReferee() {
        try {
            if (refereeId == null) {
                refereeId = Long.parseLong(idField.getText());
            }

            restTemplate.delete(BASE_URL + "/referees/" + refereeId);
            RefereeAreaController.setLastOperationMessage("Referee deleted successfully!");
            returnToRefereeArea();

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                matchError.setText(e.getResponseBodyAsString());
                matchError.setVisible(true);
            } else {
                matchError.setText("Delete failed: " + e.getMessage());
                matchError.setVisible(true);
            }
        } catch (Exception e) {
            matchError.setText("Error deleting referee: " + e.getMessage());
            matchError.setVisible(true);
        }
    }

    private void returnToRefereeArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/referee/refereeArea.fxml"
        );
    }

    @FXML
    public void cancelChanges() {
        returnToRefereeArea();
    }
}
