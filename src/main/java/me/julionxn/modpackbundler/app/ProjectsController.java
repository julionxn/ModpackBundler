package me.julionxn.modpackbundler.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import me.julionxn.modpackbundler.BaseController;
import me.julionxn.modpackbundler.app.project.ProjectData;
import me.julionxn.modpackbundler.app.project.ProjectDataController;
import me.julionxn.modpackbundler.app.project.ProjectItem;
import me.julionxn.modpackbundler.models.Project;
import me.julionxn.modpackbundler.system.SystemController;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectsController extends BaseController {

    @FXML private AnchorPane mainPane;
    @FXML private AnchorPane projectsContainer;
    private SystemController systemController;
    private final List<ProjectItem> projectItems = new ArrayList<>();
    private ProjectItem currentProject;

    public void setSystemController(SystemController systemController) {
        this.systemController = systemController;
        reloadProjects();
    }

    public void addProject(){
        openProjectDataView(null);
    }

    public void addProject(ProjectData data){
        String name = data.name();
        systemController.addProject(name);
    }

    public void editProject(){
        if (currentProject == null) return;
        Project project = currentProject.getProject();
        openProjectDataView(project);
    }

    public void editProject(Project project, ProjectData data){
        project.rename(data.name());
    }

    private void openProjectDataView(@Nullable Project projectModified){
        String title = projectModified == null ? "Add Project" : "Edit Project";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/julionxn/modpackbundler/data-views/project-data-view.fxml"));
            Parent newView = loader.load();
            ProjectDataController controller = loader.getController();
            controller.init(this, projectModified);
            Stage newWindow = new Stage();
            controller.setStage(newWindow);
            newWindow.setTitle(title);
            newWindow.setScene(new Scene(newView));
            newWindow.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openProject(){
        stage.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/julionxn/modpackbundler/profiles-view.fxml"));
            Parent root = loader.load();

            ProfilesController controller = loader.getController();
            Project project = currentProject.getProject();
            controller.setProject(this.systemController, project);

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setScene(new Scene(root));
            stage.setTitle("ModpackBundler - " + project.name);
            stage.show();

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void removeProject(){
        Project project = currentProject.getProject();
        boolean success1 = systemController.removeProject(project.name);
        boolean success2 = project.remove();
        if (success1 && success2) {
            reloadProjects();
        }
    }

    public void reloadProjects(){
        List<Project> projects = systemController.getProjects();
        projectItems.clear();
        for (Project project : projects) {
            ProjectItem item = new ProjectItem(this, project);
            projectItems.add(item);
        }
        cleanAndShowItems();
    }

    private void cleanAndShowItems(){
        projectsContainer.getChildren().clear();
        int padding = 20;
        int itemWidth = 60;
        int itemHeight = 90;
        int itemsPerRow = (int) ((projectsContainer.getPrefWidth() - padding) / (itemWidth + padding));
        for (int i = 0; i < projectItems.size(); i++) {
            StackPane item = projectItems.get(i).getStackPane();
            int row = i / itemsPerRow;
            int column = i % itemsPerRow;
            double x = column * (itemWidth + padding) + padding;
            double y = row * (itemHeight + padding) + padding;
            item.setLayoutX(x);
            item.setLayoutY(y);
            projectsContainer.getChildren().add(item);
        }
    }

    public void setCurrentProject(ProjectItem projectItem) {
        if (currentProject != null){
            currentProject.setActive(false);
        }
        this.currentProject = projectItem;
        projectItem.setActive(true);
    }

    public SystemController getSystemController() {
        return systemController;
    }

}