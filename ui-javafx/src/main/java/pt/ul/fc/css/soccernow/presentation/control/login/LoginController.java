package pt.ul.fc.css.soccernow.presentation.control.login;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginErrorLabel;

    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if(user.isEmpty() || pass.isEmpty()) {
            loginErrorLabel.setVisible(true);
        } else {
            MainController.getInstance().loadView(
                    "/pt/ul/fc/css/soccernow/presentation/view/menu/menu.fxml"
            );
        }
    }
}