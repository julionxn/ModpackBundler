package me.julionxn.modpackbundler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

public class BaseController implements Initializable {

    @FXML
    private AnchorPane dragPane;
    private double xOffset = 0;
    private double yOffset = 0;
    protected Stage stage;

    public void setStage(Stage stage){
        this.stage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                Node node = newScene.lookup("#controlPane");
                if (node == null) return;
                node.lookupAll(".controlBtn").forEach(btn -> {
                    if (btn instanceof Button button) {
                        button.setOnMouseEntered(event -> {
                            if (!button.getStyleClass().contains("hovered")) {
                                button.getStyleClass().add("hovered");
                            }
                        });
                        button.setOnMouseExited(event -> {
                            button.getStyleClass().remove("hovered");
                        });
                    }
                });
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dragPane.setOnMousePressed(event1 -> {
            xOffset = event1.getSceneX();
            yOffset = event1.getSceneY();
        });

        dragPane.setOnMouseDragged(event1 -> {
            stage.setX(event1.getScreenX() - xOffset);
            stage.setY(event1.getScreenY() - yOffset);
        });
    }

    public void closeWindow(){
        if (stage != null) {
            stage.close();
        }
    }

    public void minimizeWindow(){
        if (stage != null) {
            stage.setIconified(true);
        }
    }

}
