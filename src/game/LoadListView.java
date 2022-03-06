package game;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class LoadListView extends GridPane {

    private final File dictionaryDir = new File("./medialab");
    private final File[] dictionaryArr = dictionaryDir.listFiles();
    private final double windowWidth, windowHeigth;
    private ObservableList<String> items = FXCollections.observableArrayList();
    private ListView<String> list = new ListView<String>();
    
    LoadListView(double windowWidth, double windowHeigth) {

        this.windowWidth = windowWidth;
        this.windowHeigth = windowHeigth;
        this.setPadding(new Insets(20, 20, 20, 20));

        for (File elem : dictionaryArr) {
            items.add(elem.getName().replace("hangman_", "").replace(".txt", ""));
        }
        list.setItems(items);
        list.setPrefSize(windowWidth - 20, windowHeigth - 300);

        Label helpText = new Label("Select Dictionary");
        helpText.setId("help_text");

        this.add(helpText, 1, 0);
        GridPane.setHalignment(helpText, HPos.CENTER);
        this.add(list, 1, 1);
    }

}
