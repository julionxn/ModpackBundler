package me.julionxn.modpackbundler.app.profile;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import me.julionxn.modpackbundler.BaseController;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UUIDController extends BaseController {

    @FXML private ListView<String> listView;
    private ProfileDataController controller;

    public void init(ProfileDataController controller, @Nullable List<String> uuids){
        this.controller = controller;
        if (uuids != null){
            for (String uuid : uuids) {
                listView.getItems().add(uuid);
            }
        }
        String placeHolder = "New UUID";
        listView.getItems().add(placeHolder);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setOnEditCommit(event -> {
            int index = event.getIndex();
            String newValue = event.getNewValue();
            listView.getItems().set(index, newValue);
            int size = listView.getItems().size();
            if (newValue.isEmpty()){
                listView.getItems().remove(index);
            }
            if (size == index + 1 && !newValue.equals(placeHolder)){
                listView.getItems().add(placeHolder);
            }
        });
        listView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                int index = listView.getSelectionModel().getSelectedIndex();
                String value = listView.getItems().get(index);
                int size = listView.getItems().size();
                if (value != null && !value.equals(placeHolder)){
                    listView.getItems().remove(index);
                    if (size == index + 1){
                        listView.getItems().add(placeHolder);
                    }
                }
            }
        });
    }

    public void done(){
        controller.setUUIDs(listView.getItems());
        closeWindow();
    }

}
