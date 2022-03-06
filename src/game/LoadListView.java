package game;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class LoadListView extends ListView<String> {

    private final File dictionaryDir = new File("./medialab");
    private final File[] dictionaryArr = dictionaryDir.listFiles();
    private final double windowWidth, windowHeigth;
    private ObservableList<String> items = FXCollections.observableArrayList();
    
    LoadListView(double windowWidth, double windowHeigth) {

        this.windowWidth = windowWidth;
        this.windowHeigth = windowHeigth;

        for (File elem : dictionaryArr) {
            items.add(elem.getName().replace("hangman_", "").replace(".txt", ""));
        }
        this.setItems(items);
        this.setPrefSize(windowWidth, windowHeigth - 300);
    }

}
