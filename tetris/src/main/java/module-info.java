module org.depaul {
    
    requires javafx.controls;
    requires javafx.fxml;
    // if you are using this for your JavaFX application
    requires java.desktop;
    requires javafx.media;

    opens org.depaul.app to javafx.fxml; // Allows FXMLLoader to access the classes and FXML files in this package
    opens org.depaul.gui to javafx.fxml;
 
    exports org.depaul.app;
    exports org.depaul.gui;

    // If other packages (like gui or logic) need to be accessed by JavaFX or other modules, 
    // you can add additional opens or exports statements.
}