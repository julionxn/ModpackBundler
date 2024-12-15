package me.julionxn.modpackbundler.app.profile;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import me.julionxn.modpackbundler.BaseController;
import me.julionxn.modpackbundler.app.ProfilesController;
import me.julionxn.modpackbundler.models.LoaderType;
import me.julionxn.modpackbundler.models.Profile;
import me.julionxn.modpackbundler.models.Project;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileDataController extends BaseController {

    @FXML private TextField profileName;
    @FXML private MenuButton version;
    @FXML private MenuButton loaderType;
    @FXML private MenuButton loaderVersion;
    @FXML private TextField profileImagePath;
    @FXML private TextField description;
    private ProfilesController controller;
    private Profile profileModified;
    private LoaderType selectedLoaderType;
    private List<String> valid;

    public void init(ProfilesController controller, @Nullable Profile profileModified, List<String> releases, List<String> fabricReleases){
        this.controller = controller;
        this.profileModified = profileModified;
        String name = profileModified != null ? profileModified.name : "";
        profileName.setText(name);
        if (profileModified != null){
            String version = profileModified.getVersion();
            this.version.setText(version);
            LoaderType profileLoaderType = profileModified.getLoaderInfo().loaderType();
            loaderVersion.setDisable(profileLoaderType == LoaderType.VANILLA);
            loaderType.setText(profileLoaderType.name());
            selectedLoaderType = profileLoaderType;
            String loaderVersion = profileModified.getLoaderInfo().version();
            this.loaderVersion.setText(loaderVersion);
            profileModified.getImagePath().ifPresent(path -> {
                this.profileImagePath.setText(path.toString());
            });
            this.description.setText(profileModified.getDescription());
            this.valid = profileModified.getValidUUIDs();
        } else {
            loaderType.setText(LoaderType.VANILLA.name());
            selectedLoaderType = LoaderType.VANILLA;
            loaderVersion.setDisable(true);
        }
        loaderType.getItems().clear();
        for (LoaderType value : LoaderType.values()) {
            String valueName = value.name();
            MenuItem menuItem = new MenuItem(valueName);
            menuItem.setOnAction(event ->{
                loaderVersion.setDisable(value == LoaderType.VANILLA);
                selectedLoaderType = value;
                loaderType.setText(valueName);
            });
            loaderType.getItems().add(menuItem);
        }
        version.getItems().clear();
        for (String release : releases) {
            MenuItem menuItem = new MenuItem(release);
            menuItem.setOnAction(event -> version.setText(release));
            version.getItems().add(menuItem);
        }
        loaderVersion.getItems().clear();
        for (String release : fabricReleases) {
            MenuItem menuItem = new MenuItem(release);
            menuItem.setOnAction(event -> loaderVersion.setText(release));
            loaderVersion.getItems().add(menuItem);
        }
    }

    public void changeImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            profileImagePath.setText(selectedFile.getAbsolutePath());
        }
    }

    public void openUUIDs(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/me/julionxn/modpackbundler/uuids-view.fxml"));
            Parent root = loader.load();
            UUIDController controller = loader.getController();
            controller.init(this, valid);
            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setScene(new Scene(root));
            stage.setTitle("Valid UUIDs");
            stage.show();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void setUUIDs(List<String> uuids){
        uuids.removeIf(item -> item.equals("New UUID"));
        valid = uuids;
    }

    public void done(){
        ProfileData profileData = new ProfileData(profileName.getText(),
                version.getText(),
                selectedLoaderType,
                selectedLoaderType == LoaderType.VANILLA ? "" : loaderVersion.getText(),
                profileImagePath.getText().isEmpty() ? null : profileImagePath.getText(),
                description.getText(),
                valid == null ? new ArrayList<>() : valid
        );
        if (profileModified != null){
            controller.editProfile(profileModified, profileData);
        } else {
            controller.addProfile(profileData);
        }
        controller.reloadProfiles();
        closeWindow();
    }

}
