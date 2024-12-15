package me.julionxn.modpackbundler.app.profile;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import me.julionxn.modpackbundler.app.BaseItem;
import me.julionxn.modpackbundler.app.ProfilesController;
import me.julionxn.modpackbundler.models.Profile;

public class ProfileItem extends BaseItem {

    private final ProfilesController controller;
    private final Profile profile;

    public ProfileItem(ProfilesController controller, Profile profile) {
        super(profile.name);
        this.controller = controller;
        this.profile = profile;
    }

    @Override
    protected void onClick(MouseEvent event) {
        controller.setCurrentProfile(this);
        if (event.getClickCount() == 2){
            controller.openProfile();
        }
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public Profile getProfile() {
        return profile;
    }
}
