package me.julionxn.modpackbundler.app.profile;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import me.julionxn.modpackbundler.BaseController;
import me.julionxn.modpackbundler.app.ProfilesController;
import me.julionxn.modpackbundler.models.LoaderType;
import me.julionxn.modpackbundler.models.Profile;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ProfileDataController extends BaseController {

    @FXML private TextField profileName;
    @FXML private TextField version;
    @FXML private MenuButton loaderType;
    @FXML private TextField loaderVersion;
    @FXML private TextField profileImagePath;
    @FXML private TextField description;
    private ProfilesController controller;
    private Profile profileModified;
    private LoaderType selectedLoaderType;

    public void init(ProfilesController controller, @Nullable Profile profileModified){
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

    public void done(){
        ProfileData profileData = new ProfileData(profileName.getText(),
                version.getText(),
                selectedLoaderType,
                loaderVersion.getText(),
                profileImagePath.getText(),
                description.getText()
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
