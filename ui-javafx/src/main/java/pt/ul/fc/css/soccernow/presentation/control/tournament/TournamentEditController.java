package pt.ul.fc.css.soccernow.presentation.control.tournament;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.model.TournamentDto;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TournamentEditController {
    
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField currentClubsField;
    @FXML private TextField clubIdField;
    @FXML private TextField clubsField;
    @FXML private CheckBox isOverCheckBox;
    @FXML private TextField matchIdField;
    
    @FXML private Label nameError;
    @FXML private Label clubsError;
    @FXML private Label matchError;
    @FXML private Label generalError;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080/tournament";
    
    private Long currentTournamentId;
    private TournamentDto currentTournament;
    private final int MIN_CLUBS = 8;

    public void setTournamentData(Long tournamentId, TournamentDto tournament) {
        this.currentTournamentId = tournamentId;
        this.currentTournament = tournament;
        idField.setText(tournamentId != null ? tournamentId.toString() : "");
        nameField.setText(tournament.getTournamentName() != null ? tournament.getTournamentName() : "");
        isOverCheckBox.setSelected(tournament.isOver());
        updateCurrentClubsField();
        clearErrorMessages();
    }
    
    private void updateCurrentClubsField() {
        if (currentTournament.getClubs() != null && !currentTournament.getClubs().isEmpty()) {
            currentClubsField.setText(currentTournament.getClubs().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
        } else {
            currentClubsField.setText("No clubs in tournament");
        }
    }
    
    private void clearErrorMessages() {
        nameError.setVisible(false);
        clubsError.setVisible(false);
        matchError.setVisible(false);
        generalError.setVisible(false);
    }
    
    @FXML
    public void addClub() {
        clearErrorMessages();
        String clubIdText = clubIdField.getText().trim();
        
        if (clubIdText.isEmpty()) {
            clubsError.setText("Please enter a club ID");
            clubsError.setVisible(true);
            return;
        }
        
        try {
            Long clubId = Long.parseLong(clubIdText);
            restTemplate.put(
                BASE_URL + "/" + currentTournamentId + "/update/clubs/" + clubId + "/null",
                null
            );
            refreshTournamentData();
            clubIdField.clear();
            
            generalError.setText("Club added successfully!");
            generalError.setStyle("-fx-text-fill: green;");
            generalError.setVisible(true);
            
        } catch (NumberFormatException e) {
            clubsError.setText("Club ID must be a number");
            clubsError.setVisible(true);
        } catch (HttpClientErrorException e) {
            clubsError.setText("Failed to add club: " + e.getResponseBodyAsString());
            clubsError.setVisible(true);
        } catch (Exception e) {
            clubsError.setText("Error adding club: " + e.getMessage());
            clubsError.setVisible(true);
        }
    }
    
    @FXML
    public void removeClub() {
        clearErrorMessages();
        String clubIdText = clubIdField.getText().trim();
        
        if (clubIdText.isEmpty()) {
            clubsError.setText("Please enter a club ID");
            clubsError.setVisible(true);
            return;
        }
        
        try {
            Long clubId = Long.parseLong(clubIdText);

            if (currentTournament.getClubs() != null && currentTournament.getClubs().size() <= MIN_CLUBS) {
                clubsError.setText("Cannot remove club - tournament must have at least " + MIN_CLUBS + " clubs");
                clubsError.setVisible(true);
                return;
            }
            
            restTemplate.put(
                BASE_URL + "/" + currentTournamentId + "/update/clubs/null/" + clubId,
                null
            );
            refreshTournamentData();
            clubIdField.clear();
            
            generalError.setText("Club removed successfully!");
            generalError.setStyle("-fx-text-fill: green;");
            generalError.setVisible(true);
            
        } catch (NumberFormatException e) {
            clubsError.setText("Club ID must be a number");
            clubsError.setVisible(true);
        } catch (HttpClientErrorException e) {
            clubsError.setText("Failed to remove club: " + e.getResponseBodyAsString());
            clubsError.setVisible(true);
        } catch (Exception e) {
            clubsError.setText("Error removing club: " + e.getMessage());
            clubsError.setVisible(true);
        }
    }

    private void refreshTournamentData() {
        currentTournament = restTemplate.getForObject(BASE_URL + "/" + currentTournamentId, TournamentDto.class);
        updateCurrentClubsField();
    }
    
    @FXML
    public void cancelMatch() {
        clearErrorMessages();
        
        String matchIdText = matchIdField.getText().trim();
        if (matchIdText.isEmpty()) {
            matchError.setText("Please enter a match ID");
            matchError.setVisible(true);
            return;
        }
        
        try {
            Long matchId = Long.parseLong(matchIdText);
            restTemplate.put(
                BASE_URL + "/" + currentTournamentId + "/update/cancel-match/" + matchId,
                null
            );
            
            generalError.setText("Match cancelled successfully!");
            generalError.setStyle("-fx-text-fill: green;");
            generalError.setVisible(true);
            matchIdField.clear();
            
        } catch (NumberFormatException e) {
            matchError.setText("Match ID must be a number");
            matchError.setVisible(true);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                matchError.setText(e.getResponseBodyAsString());
            } else {
                matchError.setText("Failed to cancel match: " + e.getMessage());
            }
            matchError.setVisible(true);
        } catch (Exception e) {
            matchError.setText("Error cancelling match: " + e.getMessage());
            matchError.setVisible(true);
        }
    }
    
    @FXML
    public void saveChanges() {
        clearErrorMessages();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            nameError.setText("Name cannot be empty!");
            nameError.setVisible(true);
            return;
        }
        
        try {
            restTemplate.put(
                    BASE_URL + "/" + currentTournamentId + "/update/name/" + nameField.getText(),
                    null
                );

            if (isOverCheckBox.isSelected()) {
                restTemplate.put(
                    BASE_URL + "/" + currentTournamentId + "/update/setStateAsFinished",
                    null
                );
            }

            if (!clubsField.getText().isEmpty()) {
                try {
                    List<Long> newClubs = Arrays.stream(clubsField.getText().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                    
                    List<Long> currentClubs = currentTournament.getClubs() != null ? 
                            currentTournament.getClubs() : List.of();

                    List<Long> clubsToAdd = newClubs.stream()
                            .filter(clubId -> !currentClubs.contains(clubId))
                            .collect(Collectors.toList());

                    List<Long> clubsToRemove = currentClubs.stream()
                            .filter(clubId -> !newClubs.contains(clubId))
                            .collect(Collectors.toList());

                    for (Long clubId : clubsToAdd) {
                        restTemplate.put(
                            BASE_URL + "/" + currentTournamentId + "/update/clubs/" + clubId + "/null",
                            null
                        );
                    }

                    for (Long clubId : clubsToRemove) {
                        restTemplate.put(
                            BASE_URL + "/" + currentTournamentId + "/update/clubs/null/" + clubId,
                            null
                        );
                    }
                    
                } catch (NumberFormatException e) {
                    clubsError.setText("Club IDs must be numbers separated by commas");
                    clubsError.setVisible(true);
                    return;
                }
            }

            MainController.setLastOperationMessage("Tournament updated successfully!");
            returnToTournamentArea();
            
        } catch (HttpClientErrorException e) {
            generalError.setText("Update failed: " + e.getResponseBodyAsString());
            generalError.setVisible(true);
        } catch (Exception e) {
            generalError.setText("An error occurred: " + e.getMessage());
            generalError.setVisible(true);
        }
    }

    @FXML
    public void deleteTournament() {
        clearErrorMessages();
        try {
            restTemplate.delete(BASE_URL + "/" + currentTournamentId);
            MainController.setLastOperationMessage("Tournament deleted successfully!");
            returnToTournamentArea();
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                generalError.setText(e.getResponseBodyAsString());
            } else {
                generalError.setText("Delete failed: " + e.getMessage());
            }
            generalError.setVisible(true);
        } catch (Exception e) {
            generalError.setText("Error deleting tournament: " + e.getMessage());
            generalError.setVisible(true);
        }
    }

    private void returnToTournamentArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/tournament/tournamentArea.fxml"
        );
    }
    
    @FXML
    public void cancelChanges() {
        returnToTournamentArea();
    }
}