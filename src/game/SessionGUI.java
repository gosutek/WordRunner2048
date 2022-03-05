package game;

import dictionary.Dictionary;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class SessionGUI extends GridPane {
    
    private final float windowWidth = 1024;
    private final float windowHeigth = 768;

    private Label wordCountLabel, scoreLabel, percentageLabel, inputLabel;
    private double percentage;
    private int wordCount, score;
    private final CustomGraphics[] crossWordGraphics = {
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang0.png"), 600, 600, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang1.png"), 600, 600, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang2.png"), 600, 600, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang3.png"), 600, 600, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang4.png"), 600, 600, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang5.png"), 600, 600, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang6.png"), 600, 600, 1.0),

    };

    private Dictionary activeDictionary;

    SessionGUI(Dictionary dictionary) {

        score = 0;
        percentage = 0.00;

        activeDictionary = dictionary;
        wordCount = activeDictionary.getWords();

        Session session = new Session(activeDictionary);

        wordCountLabel = new Label("Remaining words: " + Integer.toString(wordCount));
        scoreLabel = new Label("Score: " + Integer.toString(score));
        percentageLabel = new Label("Correct word %: " + Double.toString(percentage));
        inputLabel = new Label("Input:");

        TextField userInput = new TextField();
        userInput.setId("user_input");

        HBox inputBox = new HBox(inputLabel, userInput);

        HBox topSideBox = new HBox(wordCountLabel, scoreLabel, percentageLabel);
        topSideBox.setSpacing(150);

        this.setPadding(new Insets(20, 20, 20, 20));
        this.add(topSideBox, 0, 0);
        this.add(crossWordGraphics[6], 0, 1);
        this.add(inputBox, 0, 2);


    }

}
