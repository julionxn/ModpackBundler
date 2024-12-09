package me.julionxn.modpackbundler;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.julionxn.modpackbundler.app.ProjectsController;
import me.julionxn.modpackbundler.system.SystemController;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        SystemController systemController = new SystemController("C:/");
        systemController.load();

        // Load the FXML
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("projects-view.fxml"));

        // Load the FXML file and get the root node
        Parent root = fxmlLoader.load();

        // Get the controller and set the SystemController
        ProjectsController controller = fxmlLoader.getController();
        controller.setSystemController(systemController);

        // Create and set the scene
        Scene scene = new Scene(root);

        // Set the stage
        stage.setTitle("ModpackBundler");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}