module pt.ul.fc.css.soccernow {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    
    // Spring
    requires spring.web;
    requires spring.core;
    
    // Jackson
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;

    // Open packages
    opens pt.ul.fc.css.soccernow to javafx.fxml;
    opens pt.ul.fc.css.soccernow.presentation.control to javafx.fxml, spring.web;
    opens pt.ul.fc.css.soccernow.presentation.model to com.fasterxml.jackson.databind;
    
    // Exports
    exports pt.ul.fc.css.soccernow;
    exports pt.ul.fc.css.soccernow.presentation.control;
    exports pt.ul.fc.css.soccernow.presentation.model;
    exports pt.ul.fc.css.soccernow.presentation.control.club;
    opens pt.ul.fc.css.soccernow.presentation.control.club to javafx.fxml, spring.web;
    exports pt.ul.fc.css.soccernow.presentation.control.match;
    opens pt.ul.fc.css.soccernow.presentation.control.match to javafx.fxml, spring.web;
    exports pt.ul.fc.css.soccernow.presentation.control.player;
    opens pt.ul.fc.css.soccernow.presentation.control.player to javafx.fxml, spring.web;
    exports pt.ul.fc.css.soccernow.presentation.control.referee;
    opens pt.ul.fc.css.soccernow.presentation.control.referee to javafx.fxml, spring.web;
    exports pt.ul.fc.css.soccernow.presentation.control.team;
    opens pt.ul.fc.css.soccernow.presentation.control.team to javafx.fxml, spring.web;
    exports pt.ul.fc.css.soccernow.presentation.control.tournament;
    opens pt.ul.fc.css.soccernow.presentation.control.tournament to javafx.fxml, spring.web;
    exports pt.ul.fc.css.soccernow.presentation.control.login;
    opens pt.ul.fc.css.soccernow.presentation.control.login to javafx.fxml, spring.web;
    exports pt.ul.fc.css.soccernow.presentation.control.menu;
    opens pt.ul.fc.css.soccernow.presentation.control.menu to javafx.fxml, spring.web;
}