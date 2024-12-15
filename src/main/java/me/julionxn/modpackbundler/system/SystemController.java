package me.julionxn.modpackbundler.system;

import com.google.gson.*;
import me.julionxn.modpackbundler.models.Project;
import me.julionxn.modpackbundler.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SystemController {

    private static final String VANILLA_VERSIONS_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private final Path rootPath;
    private final List<Project> projects = new ArrayList<>();
    private final List<String> releases = new ArrayList<>();
    private final List<String> fabricReleases = new ArrayList<>();

    public SystemController(Path rootPath) {
        this.rootPath = rootPath.resolve("ModpackBundler");
    }

    public SystemController(String rootPath) {
        this(Paths.get(rootPath));
    }

    public void load(){
        File file = rootPath.toFile();
        if (!file.exists() && !file.mkdir()) {
            System.out.printf("[ERROR] Cannot create directory: %s\n", file.getAbsolutePath());
        }
        loadProjects();
        loadVanillaVersions();
        loadFabricLoaderVersions();
    }

    private void loadVanillaVersions(){
        JsonObject versionsData;
        try {
            URL url = new URL(VANILLA_VERSIONS_URL);
            Optional<JsonObject> versionsDataOpt = fetchJsonData(url);
            if (versionsDataOpt.isEmpty()) return;
            versionsData = versionsDataOpt.get();
        } catch (IOException e) {
            return;
        }
        JsonArray versions = versionsData.getAsJsonArray("versions");
        for (JsonElement version : versions) {
            JsonObject versionData = version.getAsJsonObject();
            String id = versionData.get("id").getAsString();
            String type = versionData.get("type").getAsString();
            if (!type.equals("release")) continue;
            releases.add(id);
        }
    }

    private void loadFabricLoaderVersions(){
        String url = "https://api.github.com/repos/FabricMC/fabric-loader/releases";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github.v3+json")
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("Failed to fetch releases, status code: " + response.statusCode());
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);

        for (JsonElement element : jsonArray) {
            JsonObject release = element.getAsJsonObject();
            String releaseName = release.get("tag_name").getAsString();
            fabricReleases.add(releaseName);
        }
    }

    public List<String> getReleases(){
        return releases;
    }

    public List<String> getFabricReleases(){
        return fabricReleases;
    }

    private Optional<JsonObject> fetchJsonData(URL requestUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK){
            return Optional.empty();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder responseString = new StringBuilder();
        while ((inputLine = in.readLine()) != null){
            responseString.append(inputLine);
        }
        in.close();
        JsonObject response = JsonParser.parseString(responseString.toString()).getAsJsonObject();
        return Optional.of(response);
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
