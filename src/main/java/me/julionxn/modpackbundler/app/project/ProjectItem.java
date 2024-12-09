package me.julionxn.modpackbundler.app.project;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import me.julionxn.modpackbundler.app.ProjectsController;
import me.julionxn.modpackbundler.models.Project;

public class ProjectItem {

    private final ProjectsController controller;
    private final Project project;
    private final StackPane stackPane;

    public ProjectItem(ProjectsController controller, Project project) {
        this.controller = controller;
        this.project = project;
        this.stackPane = getStackPane(this.project.name);
    }

    private StackPane getStackPane(String name) {
        Rectangle rect = new Rectangle();
        rect.setWidth(60);
        rect.setHeight(90);
        rect.setFill(Color.SALMON);
        Label label = new Label(name);
        StackPane stackPane = new StackPane(rect, label);
        stackPane.setOnMouseClicked(event -> {
            controller.setCurrentProject(this);
        });
        stackPane.setOnMouseEntered(event -> rect.setStroke(Color.BLUE));
        stackPane.setOnMouseExited(event -> rect.setStroke(Color.TRANSPARENT));
        return stackPane;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public Project getProject() {
        return project;
    }
}
