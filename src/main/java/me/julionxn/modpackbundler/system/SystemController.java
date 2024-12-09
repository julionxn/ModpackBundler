package me.julionxn.modpackbundler.system;

import me.julionxn.modpackbundler.models.Project;
import me.julionxn.modpackbundler.util.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SystemController {

    private final Path rootPath;
    private final List<Project> projects = new ArrayList<>();

    public SystemController(Path rootPath) {
        this.rootPath = rootPath.resolve("ModpackBundler");
    }

    public SystemController(String rootPath) {
        this(Paths.get(rootPath));
    }

    public void load() {
        File file = rootPath.toFile();
        if (!file.exists() && !file.mkdir()) {
            System.out.printf("[ERROR] Cannot create directory: %s\n", file.getAbsolutePath());
        }
        loadProjects();
    }

    public void loadProjects(){
        Optional<List<Path>> projectPathsOpt = FileUtils.getDirectories(rootPath);
        if (projectPathsOpt.isEmpty()) return;
        List<Path> projectPaths = projectPathsOpt.get();
        projects.clear();
        for (Path projectPath : projectPaths) {
            String projectName = projectPath.getFileName().toString();
            Project project = new Project(projectName, projectPath);
            project.loadProfiles();
            projects.add(project);
        }
    }

    public Optional<Project> getProject(String projectName) {
        return projects.stream().filter(project -> project.name.equals(projectName)).findFirst();
    }

    public List<Project> getProjects() {
        return projects;
    }

    public boolean removeProject(String projectName) {
        return projects.removeIf(project -> project.name.equals(projectName));
    }

    public Project addProject(String projectName) {
        Project project = new Project(projectName, rootPath.resolve(projectName));
        addProject(project);
        return project;
    }

    public void addProject(Project project) {
        projects.add(project);
        File file = project.path.toFile();
        if (!file.exists() && !file.mkdir()) {
            System.out.printf("[ERROR] Cannot create directory: %s\n", file.getAbsolutePath());
        }
    }

}
