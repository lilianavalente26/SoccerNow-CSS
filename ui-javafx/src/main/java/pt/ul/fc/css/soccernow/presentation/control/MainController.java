package pt.ul.fc.css.soccernow.presentation.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import pt.ul.fc.css.soccernow.presentation.control.club.ClubEditController;
import pt.ul.fc.css.soccernow.presentation.control.match.MatchResultController;
import pt.ul.fc.css.soccernow.presentation.control.player.PlayerEditController;
import pt.ul.fc.css.soccernow.presentation.control.referee.RefereeEditController;
import pt.ul.fc.css.soccernow.presentation.control.team.TeamEditController;
import pt.ul.fc.css.soccernow.presentation.control.tournament.TournamentEditController;
import pt.ul.fc.css.soccernow.presentation.model.*;

import java.io.IOException;

public class MainController {

    private static final String PLAYER_EDIT_VIEW = "/pt/ul/fc/css/soccernow/presentation/view/player/playerEdit.fxml";
    private static final String REFEREE_EDIT_VIEW = "/pt/ul/fc/css/soccernow/presentation/view/referee/refereeEdit.fxml";
    private static final String CLUB_EDIT_VIEW = "/pt/ul/fc/css/soccernow/presentation/view/club/clubEdit.fxml";
    private static final String CLUB_AREA_VIEW = "/pt/ul/fc/css/soccernow/presentation/view/club/clubArea.fxml";
    private static final String TEAM_EDIT_VIEW = "/pt/ul/fc/css/soccernow/presentation/view/team/teamEdit.fxml";
    private static final String MENU_VIEW = "/pt/ul/fc/css/soccernow/presentation/view/menu/menu.fxml";
    private static final String MATCH_RESULT_VIEW = "/pt/ul/fc/css/soccernow/presentation/view/match/matchResult.fxml";
    private static final String TOURNAMENT_EDIT_VIEW = "/pt/ul/fc/css/soccernow/presentation/view/tournament/tournamentEdit.fxml";

    private static String lastOperationMessage;
    private static Long lastRegisteredId;
    
    @FXML
    private StackPane rootPane;

    private static MainController instance;

    public MainController() {
        if (instance != null) {
            throw new IllegalStateException("MainController is already initialized");
        }
        instance = this;
    }

    public static MainController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MainController not initialized yet");
        }
        return instance;
    }

    /**
     * Loads a view from the specified FXML file path
     * @param fxmlFile Path to the FXML file
     */
    public void loadView(String fxmlFile) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlFile));
            rootPane.getChildren().setAll(node);
        } catch (IOException e) {
            showError("Failed to load view: " + fxmlFile, e);
        } catch (NullPointerException e) {
            showError("View not found: " + fxmlFile, e);
        }
    }
    
    /**
     * Loads the match update view with the specified match data and ID
     * @param match Match data to update
     * @param id Match ID
     */
    public void loadMatchUpdate(MatchDto match, Long id) {
        if (match == null || id == null) {
            showError("Invalid match data", new IllegalArgumentException("Match or match ID is null"));
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MATCH_RESULT_VIEW));
            Node node = loader.load();
            
            MatchResultController controller = loader.getController();
            controller.setMatchData(match, id);
            
            rootPane.getChildren().setAll(node);
        } catch (IOException e) {
            showError("Failed to load match update view", e);
        }
    }

    /**
     * Loads the player edit view with the specified player data
     * @param player Player data to edit
     * @param id Player ID
     */
    public void loadPlayerEdit(PlayerDto player, String id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PLAYER_EDIT_VIEW));
            Node node = loader.load();
            
            PlayerEditController controller = loader.getController();
            controller.setPlayerData(player, id);
            
            rootPane.getChildren().setAll(node);
        } catch (IOException e) {
            showError("Failed to load player edit view", e);
        }
    }

    /**
     * Loads the referee edit view with the specified referee data and ID
     * @param referee Referee data to edit
     * @param id Referee ID
     */
    public void loadRefereeEdit(Long id, RefereeDto referee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(REFEREE_EDIT_VIEW));
            Node node = loader.load();

            RefereeEditController controller = loader.getController();
            controller.setRefereeData(id, referee);

            rootPane.getChildren().setAll(node);
        } catch (IOException e) {
            showError("Failed to load referee edit view", e);
        }
    }

    
    /**
     * Loads the team edit view with the specified team data
     * @param team Team data to edit
     */
    public void loadTeamEdit(TeamDto team) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(TEAM_EDIT_VIEW));
            Node node = loader.load();
            
            TeamEditController controller = loader.getController();
            controller.setTeamData(team);
            
            rootPane.getChildren().setAll(node);
        } catch (IOException e) {
            showError("Failed to load team edit view", e);
        }
    }
    
    /**
     * Loads the club edit view with the specified club data
     * @param id The ID of the club
     * @param club Club data to edit
     */
    public void loadClubEdit(Long id, ClubDto club) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CLUB_EDIT_VIEW));
            Node node = loader.load();

            ClubEditController controller = loader.getController();
            controller.setClubData(id, club);

            rootPane.getChildren().setAll(node);
        } catch (IOException e) {
            showError("Failed to load club edit view", e);
        }
    }

    /**
     * Loads the tournament edit view with the specified tournament data
     * @param id Tournament ID
     * @param tournament Tournament data to edit
     */
    public void loadTournamentEdit(Long id, TournamentDto tournament) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(TOURNAMENT_EDIT_VIEW));
            Node node = loader.load();
            
            TournamentEditController controller = loader.getController();
            controller.setTournamentData(id, tournament);
            
            rootPane.getChildren().setAll(node);
        } catch (IOException e) {
            showError("Failed to load tournament edit view", e);
        }
    }

    /**
     * Loads the team management area
     */
    public void loadTeamArea() {
        loadView("/pt/ul/fc/css/soccernow/presentation/view/team/teamArea.fxml");
    }

    /**
     * Shows an error alert with the specified message
     */
    private void showError(String message, Exception e) {
        System.err.println(message);
        if (e != null) {
            e.printStackTrace();
        }
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message + (e != null ? "\n" + e.getMessage() : ""));
        alert.showAndWait();
    }

    /**
     * Returns to the main menu
     */
    public void backToMenu() {
        loadView(MENU_VIEW);
    }
    
    /**
     * Loads the club management area
     */
    public void loadClubArea() {
        loadView(CLUB_AREA_VIEW);
    }
    
    public static void setLastOperationMessage(String message) {
        lastOperationMessage = message;
    }
    
    public static String getLastOperationMessage() {
        return lastOperationMessage;
    }
    
    public static void clearLastOperationMessage() {
        lastOperationMessage = null;
    }
    
    public static void setLastRegisteredId(Long id) {
        lastRegisteredId = id;
    }
    
    public static Long getLastRegisteredId() {
        return lastRegisteredId;
    }
    
    public static void clearLastRegisteredId() {
        lastRegisteredId = null;
    }
}