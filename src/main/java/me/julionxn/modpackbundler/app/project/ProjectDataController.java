package me.julionxn.modpackbundler.app.project;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import me.julionxn.modpackbundler.BaseController;
import me.julionxn.modpackbundler.app.ProjectsController;
import me.julionxn.modpackbundler.models.Project;
import org.jetbrains.annotations.Nullable;

public class ProjectDataController extends BaseController {

    @FXML private TextField projectName;
    private ProjectsController projectsController;
    private Project projectModified;

    public void init(ProjectsController projectsController, @Nullable Project projectModified) {
        this.projectsController = projectsController;
        this.projectModified = projectModified;
        String name = projectModified != null ? projectModified.name : "";
        projectName.setText(name);
    }

    public void done(){
        ProjectData projectData = new ProjectData(projectName.getText());
        if (projectModified != null){
            projectsController.editProject(projectModified, projectData);
        } else {
            projectsController.addProject(projectData);
        }
        projectsController.getSystemController().loadProjects();
        projectsController.reloadProjects();
        closeWindow();
    }

}
