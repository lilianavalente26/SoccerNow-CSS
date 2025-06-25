package pt.ul.fc.css.soccernow.presentation.control.menu;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.StackPane;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

/**
 * Controller for the menu view.
 * Handles navigation to different areas of the application.
 */
public class MenuController {

    private final String PATH = "/pt/ul/fc/css/soccernow/presentation/view/";

    @FXML
    private MenuBar menuBar;

    @FXML
    private StackPane menuPane;

    @FXML
    public void save() {

        // similar to load...

    }

    @FXML
    public void playerAreaOpen() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/player/playerArea.fxml"
        );
    }

    @FXML
    public void refereeAreaOpen() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/referee/refereeArea.fxml"
        );
    }

    @FXML
    public void exit() {
        menuBar.getScene().getWindow().hide();
    }
    
    @FXML
    public void clubAreaOpen() {
        MainController.getInstance().loadClubArea();
    }

    public void teamAreaOpen() {
    	MainController.getInstance().loadTeamArea();
    }

    public void matchAreaOpen() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/match/matchArea.fxml"
        );
    }

    public void tournamentAreaOpen() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/tournament/tournamentArea.fxml"
        );
    }

    public void logout() {
        MainController.getInstance().loadView(
                "/pt/ul/fc/css/soccernow/presentation/view/login/login.fxml"
        );
    }
}
