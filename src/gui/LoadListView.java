package gui;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

public class LoadListView extends GridPane {

    private final File dictionaryDir = new File("./dictionaries");
    private final File[] dictionaryArr = dictionaryDir.listFiles();
    private ObservableList<String> items = FXCollections.observableArrayList();
    private ListView<String> list = new ListView<String>();
    private Button loadButton;
    
    LoadListView(double windowWidth, double windowHeigth, Button loadButton) {

        this.loadButton = loadButton;
        this.setPadding(new Insets(20, 20, 20, 20));

        for (File elem : dictionaryArr) {
            items.add(elem.getName().replace("hangman_", "").replace(".txt", ""));
        }
        list.setItems(items);
        list.setPrefSize(windowWidth - 20, windowHeigth - 400);

        Label helpText = new Label("Select Dictionary");
        helpText.setId("help_text");

        this.add(helpText, 1, 0);
        GridPane.setHalignment(helpText, HPos.CENTER);
        this.add(list, 1, 1);
        setSelectionListener(loadButton);
    }

    private void setSelectionListener(Button loadButton) {
        list.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
                public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
                    loadButton.setDisable((new_val == null) ? true : false);
                }
            }
        );
    }

    public ListView<String> getListView() {
        return list;
    }

    public void disableButton() {
        loadButton.setText("Load");
        loadButton.setDisable(true);
    }

}
