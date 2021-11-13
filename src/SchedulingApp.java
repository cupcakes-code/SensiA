import java.io.IOException;
import java.sql.SQLException;

import controller.AbstractController;
import controller.ManageCustomerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.ZoneId;

/**
 * The main class. It launches the application and controls which scenes/views
 * are shown.
 *
 *
 */
public class SchedulingApp extends Application {

    @Override
    public void start(Stage startStage) throws IOException {
        AbstractController.start(startStage);
    }

    /**
     * The main method launches the application.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch();
    }

}
