package me.julionxn.modpackbundler.app.project;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import me.julionxn.modpackbundler.app.BaseItem;
import me.julionxn.modpackbundler.app.ProjectsController;
import me.julionxn.modpackbundler.models.Project;

public class ProjectItem extends BaseItem {

    private final ProjectsController controller;
    private final Project project;

    public ProjectItem(ProjectsController controller, Project project) {
        super(project.name);
        this.controller = controller;
        this.project = project;
    }

    @Override
    protected void onClick(MouseEvent event) {
        controller.setCurrentProject(this);
        if (event.getClickCount() == 2){
            controller.openProject();
        }
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public Project getProject() {
        return project;
    }

}
