package me.julionxn.modpackbundler.app;

import com.google.gson.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import me.julionxn.modpackbundler.BaseController;
import me.julionxn.modpackbundler.app.profile.ProfileData;
import me.julionxn.modpackbundler.app.profile.ProfileDataController;
import me.julionxn.modpackbundler.app.profile.ProfileItem;
import me.julionxn.modpackbundler.models.LoaderInfo;
import me.julionxn.modpackbundler.models.LoaderType;
import me.julionxn.modpackbundler.models.Profile;
import me.julionxn.modpackbundler.models.Project;
import me.julionxn.modpackbundler.system.SystemController;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ProfilesController extends BaseController {

    @FXML private AnchorPane profilesContainer;
    private Project project;
    private final List<ProfileItem> profileItems = new ArrayList<>();
    private ProfileItem currentProfile;
    private SystemController systemController;

    public void setProject(SystemController systemController, Project project) {
        this.project = project;
        this.systemController = systemController;
        reloadProfiles();
    }

    public void setCurrentProfile(ProfileItem profile) {
        if (this.currentProfile != null){
            this.currentProfile.setActive(false);
        }
        this.currentProfile = profile;
        profile.setActive(true);
    }

    public void addProfile(){
        openProjectDataView(null);
    }

    public void addProfile(ProfileData profileData){
        String name = profileData.name();
        String version = profileData.version();
        LoaderType loaderType = profileData.loaderType();
        String loaderVersion = profileData.loaderVersion();
        Profile profile = project.addProfile(name);
        profile.setVersion(version);
        profile.setLoaderInfo(new LoaderInfo(loaderType, loaderVersion));
        profile.setImagePath(profileData.profileImage());
        profile.setDescription(profileData.description());
        profile.saveManifest();
    }

    public void editProfile(){
        openProjectDataView(currentProfile.getProfile());
    }

    public void editProfile(Profile profile, ProfileData profileData){
        String name = profileData.name();
        String version = profileData.version();
        LoaderType loaderType = profileData.loaderType();
        String loaderVersion = profileData.loaderVersion();
        profile.rename(name);
        profile.setVersion(version);
        profile.setLoaderInfo(new LoaderInfo(loaderType, loaderVersion));
        profile.setImagePath(profileData.profileImage());
        profile.setDescription(profileData.description());
        profile.saveManifest();
    }

    private void openProjectDataView(@Nullable Profile profileModified){
        String title = profileModified == null ? "Add Profile" : "Edit Profile";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/julionxn/modpackbundler/data-views/profile-data-view.fxml"));
            Parent newView = loader.load();
            ProfileDataController controller = loader.getController();
            controller.init(this, profileModified);
            Stage newWindow = new Stage();
            controller.setStage(newWindow);
            newWindow.setTitle(title);
            newWindow.setScene(new Scene(newView));
            newWindow.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void openProfile(){
        File folder = new File(String.valueOf(currentProfile.getProfile().path));
        if (folder.exists() && folder.isDirectory()) {
            try {
                Desktop.getDesktop().open(folder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Folder does not exist or is not a directory.");
        }
    }

    public void removeProfile(){
        Profile profile = currentProfile.getProfile();
        boolean success1 = project.removeProfile(profile.name);
        boolean success2 = profile.remove();
        if (success1 && success2) {
            reloadProfiles();
        }
    }

    public void reloadProfiles(){
        List<Profile> profiles = project.getProfiles();
        profileItems.clear();
        for (Profile profile : profiles) {
            ProfileItem item = new ProfileItem(this, profile);
            profileItems.add(item);
        }
        cleanAndShowItems();
    }

    private void cleanAndShowItems(){
        profilesContainer.getChildren().clear();
        int padding = 20;
        int itemWidth = 60;
        int itemHeight = 90;
        int itemsPerRow = (int) ((profilesContainer.getPrefWidth() - padding) / (itemWidth + padding));
        for (int i = 0; i < profileItems.size(); i++) {
            StackPane item = profileItems.get(i).getStackPane();
            int row = i / itemsPerRow;
            int column = i % itemsPerRow;
            double x = column * (itemWidth + padding) + padding;
            double y = row * (itemHeight + padding) + padding;
            item.setLayoutX(x);
            item.setLayoutY(y);
            profilesContainer.getChildren().add(item);
        }
    }

    public void back(){
        stage.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/julionxn/modpackbundler/projects-view.fxml"));
            Parent root = loader.load();

            ProjectsController controller = loader.getController();
            controller.setSystemController(systemController);

            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setScene(new Scene(root));
            stage.setTitle("ModpackBundler - " + project.name);
            stage.show();

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void bundle() {
        JsonObject jsonOutput = new JsonObject();
        File rootDirectory = project.path.toFile();
        if (rootDirectory.isDirectory()) {
            File[] mainDirs = rootDirectory.listFiles(File::isDirectory);
            if (mainDirs != null) {
                for (File mainDir : mainDirs) {
                    JsonObject dirNode = new JsonObject();
                    File manifestFile = new File(mainDir, "manifest.json");
                    if (manifestFile.exists() && manifestFile.isFile()) {
                        String relativeManifestPath = getRelativePath(manifestFile, rootDirectory);
                        Optional<JsonObject> manifestDataOpt = openJsonAsObject(manifestFile);
                        if (manifestDataOpt.isEmpty()) continue;
                        JsonObject manifestData = manifestDataOpt.get();
                        String folderHash;
                        List<String> folderFiles = new ArrayList<>();
                        try {
                            folderHash = generateFolderHash(mainDir);
                            try (Stream<Path> stream = Files.list(mainDir.toPath())){
                                folderFiles = stream.map(Path::getFileName).map(Path::toString).toList();
                            }
                        } catch (IOException | NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                        JsonObject hashObject = new JsonObject();
                        hashObject.addProperty("hash", folderHash);
                        JsonArray filesArray = new JsonArray();
                        for (String folderFile : folderFiles) {
                            filesArray.add(folderFile);
                        }
                        hashObject.add("files", filesArray);
                        manifestData.add("check", hashObject);
                        saveJsonObject(manifestData, manifestFile);
                        JsonObject imageData = manifestData.getAsJsonObject("image");
                        if (imageData.get("has").getAsBoolean()){
                            String path = imageData.get("path").getAsString();
                            moveImageToFolder(path, mainDir);
                        }
                        dirNode.addProperty("manifest", relativeManifestPath);
                    } else {
                        dirNode.addProperty("manifest", "not-found");
                    }
                    JsonObject filesNode = new JsonObject();
                    traverseDirectory(mainDir, filesNode, mainDir.getName());
                    dirNode.add("files", filesNode);
                    jsonOutput.add(mainDir.getName(), dirNode);
                }
            }
        }
        File manifestFile = new File(rootDirectory, "manifest.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fileWriter = new FileWriter(manifestFile)) {
            gson.toJson(jsonOutput, fileWriter);
            System.out.println("Successfully wrote manifest.json to the root directory.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        zipDirectory(rootDirectory);
    }

    private void saveJsonObject(JsonObject jsonObject, File file){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(jsonObject);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void moveImageToFolder(String sourcePath, File mainDir) {
        try {
            if (!mainDir.exists()) return;
            File targetFile = new File(mainDir, "profile.png");
            Files.move(Path.of(sourcePath), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File moved successfully to: " + targetFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error moving the file: " + e.getMessage());
        }
    }

    private Optional<JsonObject> openJsonAsObject(File file) {
        try (FileReader reader = new FileReader(file)) {
            return Optional.ofNullable(JsonParser.parseReader(reader).getAsJsonObject());
        } catch (IOException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("Error parsing the JSON file: " + e.getMessage());
        }
        return Optional.empty();
    }

    private void zipDirectory(File rootDirectory) {
        File zipFile = new File(rootDirectory, project.name + ".zip");
        if (zipFile.exists()) {
            System.out.println("Existing bundle.zip found, overwriting...");
            zipFile.delete();
        }
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
             Stream<Path> stream = Files.walk(rootDirectory.toPath())) {
            stream.filter(path -> !path.toString().endsWith(".zip"))
                    .forEach(path -> {
                        try {
                            Path relativePath = rootDirectory.toPath().relativize(path);
                            if (Files.isDirectory(path)) {
                                zipOut.putNextEntry(new ZipEntry(relativePath + "/"));
                            } else {
                                zipOut.putNextEntry(new ZipEntry(relativePath.toString()));
                                Files.copy(path, zipOut);
                            }
                            zipOut.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            System.out.println("Successfully zipped the directory into bundle.zip");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void traverseDirectory(File rootDir, JsonObject parentNode, String parentPath) {
        Deque<TraversalNode> stack = new ArrayDeque<>();
        stack.push(new TraversalNode(rootDir, parentNode, parentPath));
        while (!stack.isEmpty()) {
            TraversalNode currentNode = stack.pop();
            File directory = currentNode.directory;
            JsonObject parentNodeForDir = currentNode.parentNode;
            String currentPath = currentNode.currentPath;
            File[] contents = directory.listFiles();
            if (contents != null) {
                for (File file : contents) {
                    if (file.getName().equals("manifest.json")) continue;
                    String relativePath = currentPath + "/" + file.getName();
                    if (file.isDirectory()) {
                        JsonObject dirNode = new JsonObject();
                        dirNode.addProperty("type", "directory");
                        JsonObject nestedFilesNode = new JsonObject();
                        stack.push(new TraversalNode(file, nestedFilesNode, relativePath));
                        dirNode.add("files", nestedFilesNode);
                        parentNodeForDir.add(relativePath, dirNode);
                    } else {
                        JsonObject fileNode = new JsonObject();
                        fileNode.addProperty("type", "file");
                        String hash = getSHA1Hash(file);
                        fileNode.addProperty("hash", hash);
                        fileNode.addProperty("size", file.length());
                        parentNodeForDir.add(relativePath, fileNode);
                    }
                }
            }
        }
    }

    protected String getSHA1Hash(File file) {
        try {
            byte[] hashBytes = computeFileHashSHA1(file);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public String generateFolderHash(File folder) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        updateDigestWithFolder(digest, folder, folder.getPath());
        return bytesToHex(digest.digest());
    }

    private void updateDigestWithFolder(MessageDigest digest, File folder, String rootPath) throws IOException, NoSuchAlgorithmException {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    updateDigestWithFolder(digest, file, rootPath);
                } else {
                    String relativePath = file.getPath().substring(rootPath.length());
                    digest.update(relativePath.getBytes());
                    digest.update(computeFileHashSHA256(file));
                }
            }
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private byte[] computeFileHashSHA1(File file) throws IOException, NoSuchAlgorithmException {
        return computeFileHash(file, "SHA-1");
    }

    private byte[] computeFileHashSHA256(File file) throws IOException, NoSuchAlgorithmException {
        return computeFileHash(file, "SHA-256");
    }

    private byte[] computeFileHash(File file, String algoritm) throws IOException, NoSuchAlgorithmException {
        MessageDigest fileDigest = MessageDigest.getInstance(algoritm);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fileDigest.update(buffer, 0, bytesRead);
            }
        }
        return fileDigest.digest();
    }

    private String getRelativePath(File file, File rootDirectory) {
        String rootPath = rootDirectory.getAbsolutePath();
        String filePath = file.getAbsolutePath();
        return filePath.startsWith(rootPath) ? filePath.substring(rootPath.length() + 1).replace("\\", "/") : filePath;
    }

    private record TraversalNode(File directory, JsonObject parentNode, String currentPath) {
    }

}
