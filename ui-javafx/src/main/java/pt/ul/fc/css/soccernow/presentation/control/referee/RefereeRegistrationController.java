package pt.ul.fc.css.soccernow.presentation.control.referee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.RefereeDto;
import pt.ul.fc.css.soccernow.presentation.model.MatchDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RefereeRegistrationController {

    @FXML private TextField nameField;
    @FXML private CheckBox certificateCheckBox;
    @FXML private TextField matchesField;
    @FXML private Label nameError;
    @FXML private Label matchError;

    private final RestTemplate restTemplate = createRestTemplate();
    private final String BASE_URL = "http://localhost:8080/users";
    private static final String MATCH_BASE_URL = "http://localhost:8080/match/";

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        restTemplate.setMessageConverters(Collections.singletonList(converter));
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

    @FXML
    public void registerReferee() {
        nameError.setVisible(false);
        matchError.setVisible(false);

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            nameError.setText("Name is required");
            nameError.setVisible(true);
            return;
        }

        try {
            RefereeDto referee = new RefereeDto();
            referee.setName(nameField.getText());
            referee.setHasCertificate(certificateCheckBox.isSelected());

            if (matchesField.getText() != null && !matchesField.getText().trim().isEmpty()) {
                try {
                    List<Long> matchIds = Arrays.stream(matchesField.getText().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Long::parseLong)
                            .collect(Collectors.toList());

                    for (Long matchId : matchIds) {
                    	try {
                    	    ResponseEntity<MatchDto> response = restTemplate.getForEntity(
                    	        MATCH_BASE_URL + matchId, 
                    	        MatchDto.class
                    	    );
                    	    if (response.getStatusCode() != HttpStatus.OK) {
                    	        matchError.setText("Match with ID " + matchId + " does not exist!");
                    	        matchError.setVisible(true);
                    	        return;
                    	    }
                    	} catch (HttpClientErrorException e) {
                    	    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    	        matchError.setText("Match with ID " + matchId + " does not exist!");
                    	        matchError.setVisible(true);
                    	        return;
                    	    }
                    	    throw e;
                    	}
                    }
                    referee.setMatches(matchIds);
                } catch (NumberFormatException e) {
                    matchError.setText("Invalid match ID format - must be numbers separated by commas");
                    matchError.setVisible(true);
                    return;
                }
            } else {
                referee.setMatches(Collections.emptyList());
            }

            ResponseEntity<Long> response = restTemplate.postForEntity(
                    BASE_URL + "/referees/register",
                    referee,
                    Long.class
            );

            RefereeAreaController.setLastRegisteredRefereeId(response.getBody());
            returnToRefereeArea();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errorMessage = e.getResponseBodyAsString();
                if (errorMessage.contains("Name can not be empty")) {
                    nameError.setText(errorMessage);
                    nameError.setVisible(true);
                } else if (errorMessage.contains("Match with Id")) {
                    matchError.setText(errorMessage);
                    matchError.setVisible(true);
                } else {
                    matchError.setText("Registration failed: " + errorMessage);
                    matchError.setVisible(true);
                }
            } else {
                matchError.setText("Registration failed: " + e.getMessage());
                matchError.setVisible(true);
            }
        } catch (Exception e) {
            matchError.setText("Registration failed: " + e.getMessage());
            matchError.setVisible(true);
        }
    }

    @FXML
    public void cancelRegistration() {
        returnToRefereeArea();
    }

    private void returnToRefereeArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/referee/refereeArea.fxml"
        );
    }
}