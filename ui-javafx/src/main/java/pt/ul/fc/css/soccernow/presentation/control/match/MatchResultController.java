package pt.ul.fc.css.soccernow.presentation.control.match;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.MatchDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

import java.util.Arrays;
import java.util.List;

public class MatchResultController {

    @FXML private TextField matchIdField;
    @FXML private TextField playerGoalsField;
    @FXML private TextField yellowCardsField;
    @FXML private TextField redCardsField;
    
    @FXML private Label playerGoalsError;
    @FXML private Label yellowCardsError;
    @FXML private Label redCardsError;
    @FXML private Label generalError;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";
    private Long currentMatchId;

    public void setMatchData(MatchDto match, Long matchId) {
        if (match == null || matchId == null) {
            System.err.println("Invalid match data received");
            returnToMatchArea();
            return;
        }
        currentMatchId = matchId;
        matchIdField.setText(String.valueOf(currentMatchId));
    }

    @FXML
    public void finishMatch() {
        clearErrors();
        
        try {
            if (currentMatchId == null) {
                showFieldError(playerGoalsError, "No valid match ID available");
                return;
            }

            if (!playerGoalsField.getText().isEmpty()) {
                List<Long> goalScorers = parsePlayerList(playerGoalsField.getText(), "Player Goals", playerGoalsError);
                if (goalScorers == null) return;
                
                for (Long playerId : goalScorers) {
                    restTemplate.put(
                        BASE_URL + "/match/" + currentMatchId + "/register/goal/player/" + playerId, 
                        null
                    );
                }
            }

            if (!yellowCardsField.getText().isEmpty()) {
                List<Long> yellowCards = parsePlayerList(yellowCardsField.getText(), "Yellow Cards", yellowCardsError);
                if (yellowCards == null) return;
                
                for (Long playerId : yellowCards) {
                    restTemplate.put(
                        BASE_URL + "/match/" + currentMatchId + "/register/player/" + playerId + "/card/yellowCard", 
                        null
                    );
                }
            }

            if (!redCardsField.getText().isEmpty()) {
                List<Long> redCards = parsePlayerList(redCardsField.getText(), "Red Cards", redCardsError);
                if (redCards == null) return;
                
                for (Long playerId : redCards) {
                    restTemplate.put(
                        BASE_URL + "/match/" + currentMatchId + "/register/player/" + playerId + "/card/redCard", 
                        null
                    );
                }
            }

            restTemplate.put(
                BASE_URL + "/match/" + currentMatchId + "/setStateAsFinished", 
                null
            );
            
            MatchAreaController.setLastOperationMessage("Match results registered successfully!");
            returnToMatchArea();
            
        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            if (errorMessage.contains("already finished")) {
                showGeneralError(errorMessage);
            } else if (errorMessage.contains("Player")) {
                showFieldError(playerGoalsError, errorMessage);
            } else if (errorMessage.contains("card")) {
                if (errorMessage.contains("yellow")) {
                    showFieldError(yellowCardsError, errorMessage);
                } else {
                    showFieldError(redCardsError, errorMessage);
                }
            } else {
                showGeneralError(errorMessage);
            }
        } catch (Exception e) {
            showFieldError(playerGoalsError, "Failed to register match result: " + e.getMessage());
        }
    }
    
    private List<Long> parsePlayerList(String text, String fieldName, Label errorLabel) {
        try {
            return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(java.util.stream.Collectors.toList());
        } catch (NumberFormatException e) {
            showFieldError(errorLabel, fieldName + " must be player IDs separated by commas (ID,ID,ID)");
            return null;
        }
    }
    
    private void showGeneralError(String message) {
        generalError.setText(message);
        generalError.setVisible(true);
    }

    private void showFieldError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void clearErrors() {
        playerGoalsError.setVisible(false);
        yellowCardsError.setVisible(false);
        redCardsError.setVisible(false);
    }

    @FXML
    public void cancel() {
        returnToMatchArea();
    }
    
    private void returnToMatchArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/match/matchArea.fxml"
        );
    }
}