package me.julionxn.modpackbundler;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.julionxn.modpackbundler.app.ProjectsController;
import me.julionxn.modpackbundler.app.profile.UUIDController;
import me.julionxn.modpackbundler.system.SystemController;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        SystemController systemController = new SystemController("C:/");
        systemController.load();
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("projects-view.fxml"));
        Parent root = fxmlLoader.load();
        ProjectsController controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.setSystemController(systemController);
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("ModpackBundler");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}