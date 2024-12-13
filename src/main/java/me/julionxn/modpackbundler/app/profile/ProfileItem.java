package me.julionxn.modpackbundler.app.profile;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import me.julionxn.modpackbundler.app.BaseItem;
import me.julionxn.modpackbundler.app.ProfilesController;
import me.julionxn.modpackbundler.models.Profile;

public class ProfileItem extends BaseItem {

    private final ProfilesController controller;
    private final Profile profile;
    private final StackPane stackPane;

    public ProfileItem(ProfilesController controller, Profile profile) {
        this.controller = controller;
        this.profile = profile;
        this.stackPane = getStackPane(this.profile.name);
    }

    private StackPane getStackPane(String name) {
        Label label = getLabel(name);
        StackPane stackPane = new StackPane(rectangle, label);
        stackPane.setOnMouseClicked(event -> {
            controller.setCurrentProfile(this);
        });
        stackPane.setOnMouseEntered(event -> setHovered(true));
        stackPane.setOnMouseExited(event -> setHovered(false));
        return stackPane;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public Profile getProfile() {
        return profile;
    }
}
