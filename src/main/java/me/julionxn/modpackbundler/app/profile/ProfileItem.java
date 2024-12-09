package me.julionxn.modpackbundler.app.profile;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import me.julionxn.modpackbundler.app.ProfilesController;
import me.julionxn.modpackbundler.models.Profile;

public class ProfileItem {

    private final ProfilesController controller;
    private final Profile profile;
    private final StackPane stackPane;

    public ProfileItem(ProfilesController controller, Profile profile) {
        this.controller = controller;
        this.profile = profile;
        this.stackPane = getStackPane(this.profile.name);
    }

    private StackPane getStackPane(String name) {
        Rectangle rect = new Rectangle();
        rect.setWidth(60);
        rect.setHeight(90);
        rect.setFill(Color.SALMON);
        Label label = new Label(name);
        StackPane stackPane = new StackPane(rect, label);
        stackPane.setOnMouseClicked(event -> {
            controller.setCurrentProfile(this);
        });
        stackPane.setOnMouseEntered(event -> rect.setStroke(Color.BLUE));
        stackPane.setOnMouseExited(event -> rect.setStroke(Color.TRANSPARENT));
        return stackPane;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public Profile getProfile() {
        return profile;
    }
}
