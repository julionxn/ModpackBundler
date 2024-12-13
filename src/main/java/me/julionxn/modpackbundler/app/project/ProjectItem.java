package me.julionxn.modpackbundler.app.project;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import me.julionxn.modpackbundler.app.BaseItem;
import me.julionxn.modpackbundler.app.ProjectsController;
import me.julionxn.modpackbundler.models.Project;

public class ProjectItem extends BaseItem {

    private final ProjectsController controller;
    private final Project project;
    private final StackPane stackPane;

    public ProjectItem(ProjectsController controller, Project project) {
        this.controller = controller;
        this.project = project;
        this.stackPane = getStackPane(this.project.name);
    }

    private StackPane getStackPane(String name) {
        Label label = getLabel(name);
        StackPane stackPane = new StackPane(rectangle, label);
        stackPane.setOnMouseClicked(event -> {
            controller.setCurrentProject(this);
        });
        stackPane.setOnMouseEntered(event -> setHovered(true));
        stackPane.setOnMouseExited(event -> setHovered(false));
        return stackPane;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public Project getProject() {
        return project;
    }
}
