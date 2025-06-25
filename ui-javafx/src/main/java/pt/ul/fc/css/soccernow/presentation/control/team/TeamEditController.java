package pt.ul.fc.css.soccernow.presentation.control.team;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import pt.ul.fc.css.soccernow.presentation.model.TeamDto;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TeamEditController {

    @FXML private TextField idField;
    @FXML private TextField clubIdField;
    @FXML private TextField playersField;
    @FXML private TextField goalkeeperField;
    @FXML private Label clubIdError;
    @FXML private Label playersError;
    @FXML private Label goalkeeperError;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8080";
    private TeamDto currentTeam;

    @FXML
    public void initialize() {
        clubIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) clubIdError.setVisible(false);
        });

        playersField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) playersError.setVisible(false);
        });

        goalkeeperField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) goalkeeperError.setVisible(false);
        });
    }

    public void setTeamData(TeamDto team) {
        this.currentTeam = team;

        idField.setText(TeamAreaController.getLastSearchedId());

        clubIdField.setText(team.getClub() != null ? team.getClub().toString() : "");

        if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
            playersField.setText(team.getPlayers().stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        } else {
            playersField.setText("");
        }

        goalkeeperField.setText(team.getGoalkeeper() != null ? team.getGoalkeeper().toString() : "");
    }

    @FXML
    public void saveChanges() {
        clubIdError.setVisible(false);
        playersError.setVisible(false);
        goalkeeperError.setVisible(false);

        if (clubIdField.getText() == null || clubIdField.getText().trim().isEmpty()) {
            clubIdError.setText("Club ID cannot be empty!");
            clubIdError.setVisible(true);
            return;
        }

        try {
            TeamDto updatedTeam = new TeamDto();

            try {
                updatedTeam.setClub(Long.parseLong(clubIdField.getText().trim()));
            } catch (NumberFormatException e) {
                clubIdError.setText("Club ID must be a number");
                clubIdError.setVisible(true);
                return;
            }

            if (!playersField.getText().isEmpty()) {
                try {
                    List<Long> players = Arrays.stream(playersField.getText().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                    updatedTeam.setPlayers(players);
                } catch (NumberFormatException e) {
                    playersError.setText("Player IDs must be numbers separated by commas");
                    playersError.setVisible(true);
                    return;
                }
            } else {
                updatedTeam.setPlayers(currentTeam.getPlayers());
            }

            if (!goalkeeperField.getText().isEmpty()) {
                try {
                    updatedTeam.setGoalkeeper(Long.parseLong(goalkeeperField.getText().trim()));
                } catch (NumberFormatException e) {
                    goalkeeperError.setText("Goalkeeper ID must be a number");
                    goalkeeperError.setVisible(true);
                    return;
                }
            } else {
                updatedTeam.setGoalkeeper(currentTeam.getGoalkeeper());
            }

            updatedTeam.setMatchesAsTeam1(currentTeam.getMatchesAsTeam1());
            updatedTeam.setMatchesAsTeam2(currentTeam.getMatchesAsTeam2());

            Long teamId = Long.parseLong(idField.getText());

            restTemplate.put(BASE_URL + "/team/" + teamId, updatedTeam);

            this.currentTeam = updatedTeam;
            TeamAreaController.setLastOperationMessage("Team updated successfully!");
            returnToTeamArea();

        } catch (HttpClientErrorException e) {
            handleHttpError(e);
        } catch (Exception e) {
            playersError.setText("An error occurred: " + e.getMessage());
            playersError.setVisible(true);
        }
    }

    @FXML
    public void deleteTeam() {
        try {
            Long teamId = Long.parseLong(idField.getText());
            restTemplate.delete(BASE_URL + "/team/" + teamId);
            TeamAreaController.setLastOperationMessage("Team deleted successfully!");
            returnToTeamArea();
        } catch (HttpClientErrorException e) {
            handleHttpError(e);
        } catch (Exception e) {
            playersError.setText("Error deleting team: " + e.getMessage());
            playersError.setVisible(true);
        }
    }

    private void handleHttpError(HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            String errorMessage = e.getResponseBodyAsString();
            if (errorMessage.contains("Club")) {
                clubIdError.setText(errorMessage);
                clubIdError.setVisible(true);
            } else if (errorMessage.contains("Player")) {
                playersError.setText(errorMessage);
                playersError.setVisible(true);
            } else if (errorMessage.contains("Goalkeeper")) {
                goalkeeperError.setText(errorMessage);
                goalkeeperError.setVisible(true);
            } else {
                playersError.setText("Update failed: " + errorMessage);
                playersError.setVisible(true);
            }
        } else {
            playersError.setText("Request failed: " + e.getMessage());
            playersError.setVisible(true);
        }
    }

    private void returnToTeamArea() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/team/teamArea.fxml"
        );
    }

    @FXML
    public void cancelChanges() {
        returnToTeamArea();
    }
}
