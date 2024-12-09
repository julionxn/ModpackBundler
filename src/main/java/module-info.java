module me.julionxn.modpackbundler {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires org.jetbrains.annotations;
    requires java.desktop;


    opens me.julionxn.modpackbundler to javafx.fxml;
    exports me.julionxn.modpackbundler;
    exports me.julionxn.modpackbundler.system;
    exports me.julionxn.modpackbundler.models;
    opens me.julionxn.modpackbundler.models to javafx.fxml;
    exports me.julionxn.modpackbundler.app;
    exports me.julionxn.modpackbundler.app.project;
    exports me.julionxn.modpackbundler.app.profile;
    opens me.julionxn.modpackbundler.app to javafx.fxml;
    opens me.julionxn.modpackbundler.app.project to javafx.fxml;
    opens me.julionxn.modpackbundler.app.profile to javafx.fxml;
}