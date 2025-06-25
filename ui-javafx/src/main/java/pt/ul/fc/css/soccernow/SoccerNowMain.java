package pt.ul.fc.css.soccernow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.ul.fc.css.soccernow.presentation.control.MainController;

public class SoccerNowMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(
            "/pt/ul/fc/css/soccernow/presentation/view/main.fxml"
        ));
        Parent root = mainLoader.load();

        MainController mainController = mainLoader.getController();
        mainController.loadView("/pt/ul/fc/css/soccernow/presentation/view/login/login.fxml");
        
        primaryStage.setTitle("SoccerNow");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) { 
        launch(args); 
    }
}