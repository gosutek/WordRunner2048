package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;


import org.json.*;

import dictionary.Dictionary;
import dictionary.Word;
import game.Session;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SessionGUI extends GridPane {
    
    private final float windowWidth = 1600;

    private Session session;
    private String outcome;
    private HBox guessWordBox, candidateWordBox;
    private VBox leftPane, rightPane;
    private Label wordCountLabel, scoreLabel, percentageLabel, inputLabel, solutionLabel, gameOverLabel;
    private float percentage;
    private int wordCount, score, userLetterSelection;
    private final TextField userInput;
    private Dictionary activeDictionary;
    private Button button_1, button_2, button_3, returnButton;
    private final GridPane detailsPane;
    private final TreeView<String> recordsTree;
    private Label[] guessWordLabelArray;

    private final CustomGraphics[] crossWordGraphics = {
        new CustomGraphics(this.getClass().getResourceAsStream("graphics/hang0.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("graphics/hang1.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("graphics/hang2.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("graphics/hang3.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("graphics/hang4.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("graphics/hang5.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("graphics/hang6.png"), 500, 500, 1.0),

    };


    SessionGUI(Dictionary dictionary) {

        leftPane = new VBox(10);
        rightPane = new VBox(10);
        guessWordBox = new HBox(10);
        candidateWordBox = new HBox(10);
        userLetterSelection = -1;

        score = 0;
        percentage = 0f;

        ColumnConstraints columnConstraints = new ColumnConstraints(windowWidth / 3, windowWidth / 3, Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true);
        this.getColumnConstraints().addAll(columnConstraints, columnConstraints, columnConstraints);
        RowConstraints topRowConstraints = new RowConstraints(50, 50, Double.MAX_VALUE, Priority.ALWAYS, VPos.CENTER, true);
        RowConstraints graphicsRowConstraints = new RowConstraints(600, 600, Double.MAX_VALUE, Priority.ALWAYS, VPos.CENTER, true);
        this.getRowConstraints().addAll(topRowConstraints, graphicsRowConstraints);
        this.setAlignment(Pos.CENTER);


        activeDictionary = dictionary;
        wordCount = activeDictionary.getWords();


        final float[] dictionaryStats = activeDictionary.getDictionaryStatistics();
        detailsPane = new GridPane(); 

        detailsPane.add(new Label("Dictionary Details"), 0, 0);
        detailsPane.add(new Label(""), 0, 1);
        detailsPane.add(new Label(""), 0, 2);
        detailsPane.add(new Label("6 letters: " + String.format("%.2f", dictionaryStats[0]) + "%"), 0, 3);
        detailsPane.add(new Label("7 to 9 letters: " + String.format("%.2f", dictionaryStats[1])  + "%"), 0, 4);
        detailsPane.add(new Label("10 or more letters: " + String.format("%.2f", dictionaryStats[2]) + "%"), 0, 5);

        TreeItem<String> rootItem = new TreeItem<String>("Past Games");
        rootItem.setExpanded(true);
        createRecordsTree(rootItem);
        recordsTree = new TreeView<String>(rootItem);
        rightPane.getChildren().add(recordsTree);


        session = new Session(activeDictionary);

        wordCountLabel = new Label("Remaining words: " + Integer.toString(wordCount));
        scoreLabel = new Label("Score: " + Integer.toString(score));
        percentageLabel = new Label("Correct letters: " + Double.toString(percentage) + "%");
        inputLabel = new Label("Input(ENTER):");
        solutionLabel = new Label(session.getHiddenWord().toString());
        solutionLabel.setOpacity(0);
        gameOverLabel = new Label("Wrong!");
        gameOverLabel.setOpacity(0);
        gameOverLabel.setId("game-over-label");

        leftPane.getChildren().addAll(solutionLabel, guessWordBox);
        guessWordBox.setAlignment(Pos.CENTER);


        userInput = new TextField();
        userInput.setPromptText("Press Enter...");
        setTextFieldHandler(userInput);
        userInput.setId("user_input");




        this.add(wordCountLabel, 0, 0);
        this.add(scoreLabel, 1, 0);
        GridPane.setHalignment(scoreLabel, HPos.CENTER);
        this.add(percentageLabel, 2, 0);
        this.add(leftPane, 0, 1);
        leftPane.setAlignment(Pos.CENTER);
        this.add(crossWordGraphics[0], 1, 1);
        GridPane.setHalignment(crossWordGraphics[0], HPos.CENTER);
        this.add(candidateWordBox, 2, 1);
        candidateWordBox.setAlignment(Pos.CENTER);
        this.add(inputLabel, 0, 2);
        inputLabel.setAlignment(Pos.CENTER);
        this.add(userInput, 1, 2);
        userInput.setAlignment(Pos.CENTER);
        this.add(gameOverLabel, 1, 3);
        GridPane.setHalignment(gameOverLabel, HPos.CENTER);
        GridPane.setValignment(gameOverLabel, VPos.CENTER);
        createBottomButtons();
        createGuessWordLabels(session.initialize());
        selectNextSpace();

    }

    public String getOutcome() {
        return outcome;
    }

    public Session getSession() {
        return session;
    }
    private void setUserLetterSelection(int input) {
        userLetterSelection = input;
        updateCandidateWordLabel(userLetterSelection);
    }

    public Button getReturnButton() {
        return returnButton;
    }

    private void createBottomButtons() {
        button_1 = new Button("Dictionary");
        button_1.setId("session-gui-dictionary");
        button_2 = new Button("Rounds");
        button_2.setId("session-gui-rounds");
        button_3 = new Button("Solution");
        button_3.setId("session-gui-solution");
        returnButton = new Button("Back");
        returnButton.setId("session-gui-back");

        button_1.setGraphic(new CustomGraphics(this.getClass().getResourceAsStream("graphics/button.png"), 200, 200, 0.5));
        button_1.setContentDisplay(ContentDisplay.CENTER);
        button_2.setGraphic(new CustomGraphics(this.getClass().getResourceAsStream("graphics/button.png"), 200, 200, 0.5));
        button_2.setContentDisplay(ContentDisplay.CENTER);
        button_3.setGraphic(new CustomGraphics(this.getClass().getResourceAsStream("graphics/button.png"), 200, 200, 0.5));
        button_3.setContentDisplay(ContentDisplay.CENTER);
        returnButton.setGraphic(new CustomGraphics(this.getClass().getResourceAsStream("graphics/button.png"), 200, 200, 0.5));
        returnButton.setContentDisplay(ContentDisplay.CENTER);


        this.add(button_1, 0, 4);
        this.setVgap(0);
        GridPane.setHalignment(button_1, HPos.CENTER);
        GridPane.setValignment(button_1, VPos.CENTER);
        HBox middleBox = new HBox(100);
        middleBox.getChildren().addAll(button_2, button_3);
        this.add(middleBox, 1, 4);
        GridPane.setHalignment(middleBox, HPos.CENTER);
        GridPane.setValignment(middleBox, VPos.CENTER);
        this.add(returnButton, 2, 4);
        GridPane.setHalignment(returnButton, HPos.CENTER);
        GridPane.setValignment(returnButton, VPos.CENTER);

        setBottomButtonHandlers();
        
    }

    private void setBottomButtonHandlers() {

        button_1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent arg0) {
                replaceLeftPane();
            }

        });

        button_2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent arg0) {
                replaceRightPane();
            }

        });

        button_3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            
            @Override
            public void handle(MouseEvent arg0) {
                solutionLabel.setOpacity(1);
                userInput.setDisable(true);
                button_1.setDisable(true);
                button_2.setDisable(true);
                button_3.setDisable(true);
                gameOverLabel.setText("Game Over");
                gameOverLabel.setOpacity(1);
                changeHangmanGraphics(crossWordGraphics[6]);
            }
        });
    }

    private void createRecordsTree(TreeItem<String> rootItem) {
        File recordFile = new File("game_records.json");
        if (!recordFile.exists()) {
            try {
                recordFile.createNewFile();
                FileWriter jsonCreator = new FileWriter(recordFile);
                jsonCreator.write("{records:[]}");
                jsonCreator.close();
            } catch (IOException exc) {
                exc.printStackTrace();
                return;
            }
        }
        StringBuilder jsonString = new StringBuilder();
        try {
            Scanner stringScanner = new Scanner(recordFile);
            while(stringScanner.hasNextLine()) {
                jsonString.append(stringScanner.nextLine());
            }
            stringScanner.close();
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
            jsonString = null;
        }
        JSONObject mainObj = (JSONObject) new JSONTokener(jsonString.toString()).nextValue();
        JSONArray recordsArr = mainObj.getJSONArray("records");
        String datetime;
        for (int i = 0; i < recordsArr.length(); i++) {
            JSONObject currObj = recordsArr.getJSONObject(i);
            datetime = currObj.getString("datetime");
            datetime = datetime.replaceAll("\\.[0-9]+", ""); 
            TreeItem<String> datetimeItem = new TreeItem<String>(datetime);

            TreeItem<String> outcomeItem = new TreeItem<String>("Outcome: " + currObj.getString("outcome"));
            TreeItem<String> hiddenWordItem = new TreeItem<String>("Hidden word: " + currObj.getString("hidden-word"));
            TreeItem<String> triesItem = new TreeItem<String>("#Tries: " + currObj.getString("number-of-tries"));
            TreeItem<String> scoreItem = new TreeItem<String>("Score: " + Integer.toString(currObj.getInt("score")));

            datetimeItem.getChildren().addAll(outcomeItem, hiddenWordItem, triesItem, scoreItem);
            rootItem.getChildren().add(datetimeItem);
            
        }
    }

    /* Switches between game records and letter hints */
    private void replaceRightPane() {
        ObservableList<Node> cells = this.getChildren();
        Node targetHbox, targetTreeView;
        targetHbox = targetTreeView = null;
        for (Node elem : cells) {
            int row = GridPane.getRowIndex(elem);
            int column = GridPane.getColumnIndex(elem);
            if (elem instanceof HBox && row == 1 && column == 2) {
                targetHbox = elem;
            } else if (elem instanceof TreeView && row == 1 && column == 2) {
                targetTreeView = elem;
            }
        }
        if (targetHbox == null) {
            this.getChildren().remove(targetTreeView);
            this.add(candidateWordBox, 2, 1);
            candidateWordBox.setAlignment(Pos.CENTER);
            button_1.setDisable(false);
            button_3.setDisable(false);
            userInput.setDisable(false);
            returnButton.setDisable(false);
        } else {
            this.getChildren().remove(targetHbox);
            this.add(recordsTree, 2, 1);
            button_1.setDisable(true);
            button_3.setDisable(true);
            userInput.setDisable(true);
            returnButton.setDisable(true);
        }
    }

    /* Switches between the dictionary details and the guess word label */
    private void replaceLeftPane() {
        ObservableList<Node> cells = this.getChildren();
        Node targetVBox, targetGridPane;
        targetVBox = targetGridPane = null;
        for(Node elem: cells) {
            int row = GridPane.getRowIndex(elem);
            int column = GridPane.getColumnIndex(elem);
            if (elem instanceof VBox && row == 1 && column == 0) {
                targetVBox = elem;
            } else if (elem instanceof GridPane && row == 1 && column == 0) {
                targetGridPane = elem;
            }
        }
        if (targetGridPane == null) {
            this.getChildren().remove(targetVBox);
            this.add(detailsPane, 0, 1);
            detailsPane.setAlignment(Pos.CENTER);
            button_2.setDisable(true);
            button_3.setDisable(true);
            userInput.setDisable(true);
            returnButton.setDisable(true);
        } else {
            this.getChildren().remove(targetGridPane);
            this.add(leftPane, 0, 1);
            button_2.setDisable(false);
            button_3.setDisable(false);
            userInput.setDisable(false);
            returnButton.setDisable(false);
        }
    }

    private void changeHangmanGraphics(CustomGraphics newVal) {
        ObservableList<Node> cells = this.getChildren();
        Node targetNode = null;
        for (Node elem : cells) {
            int row = GridPane.getRowIndex(elem);
            int column = GridPane.getColumnIndex(elem);
            if (elem instanceof CustomGraphics && row == 1 && column == 1) {
                targetNode = elem;
                break;
            }
        }
        this.getChildren().remove(targetNode);
        this.add(newVal, 1, 1);
        GridPane.setHalignment(newVal, HPos.CENTER);
        
    }

    private void updateCandidateWordLabel(int pos) {
        session.calcProb();
        candidateWordBox.getChildren().clear();
        Label[] candidateWordLabelArray = new Label[session.getCandidateLetters(pos).length];
        for (int idx = 0; idx < candidateWordLabelArray.length; idx++) {
            candidateWordLabelArray[idx] = new Label(session.getCandidateLetters(pos)[idx]);
            candidateWordBox.getChildren().add(candidateWordLabelArray[idx]);
        }
    }

    private void updateGuessWordLabel(String input, int pos) {
        guessWordLabelArray[pos].setText(input);
    }

    private void createGuessWordLabels(Word guessWord) {
        guessWordLabelArray = new Label[guessWord.length()];
        for (int idx = 0; idx < guessWord.length(); idx++) {
            guessWordLabelArray[idx] = new Label(guessWord.getLetters()[idx].toString());
            guessWordBox.getChildren().add(guessWordLabelArray[idx]);
        }
        setGuessWordHandlers(guessWordLabelArray);
    }

    private void setGuessWordHandlers(Label[] guessWordLabelArray) {
        for (Label elem : guessWordLabelArray) {
            elem.setOnMouseEntered(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent arg0) {
                    elem.setEffect(new BoxBlur());
                }
                
            });
            elem.setOnMouseExited(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent arg0) {
                    elem.setEffect(null);
                }
            });
            elem.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent arg0) {
                    guessWordBox.getChildren().forEach(elem -> elem.setStyle(null));
                    elem.setStyle("-fx-font-size: 40px;");
                    setUserLetterSelection(guessWordBox.getChildren().indexOf(elem));
                }
            });
        }
    }

    private void setTextFieldHandler(TextField input) {

        Pattern alphaBetaPattern = Pattern.compile("[a-zA-Z]*");
        /**
         * Ensures that the user input will be of length 1.
         */
        input.lengthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    if (input.getText().length() >= 1) {
                        input.setText(input.getText().substring(0, 1));
                    }
                }
            }
            
        });
        /**
         * Ensures that the user input will be capitalized.
         */
        input.setTextFormatter(new TextFormatter<>((text) -> {
            if (alphaBetaPattern.matcher(text.getText()).matches()) {
                text.setText(text.getText().toUpperCase());
                return text;
            } else {
                return null;
            }
        }));

        input.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent arg0) {
                if (arg0.getCode() == KeyCode.ENTER) {
                    /* Correct input */
                    if(session.nextState(input.getText(), userLetterSelection)) {
                        updateGuessWordLabel(input.getText(), userLetterSelection);
                        updateCandidateWordLabel(userLetterSelection);
                        selectNextSpace();
                        input.clear();
                    } else {
                        changeHangmanGraphics(crossWordGraphics[session.getLives()]);
                        updateCandidateWordLabel(userLetterSelection);
                        gameOverLabel.setText("Wrong!");
                        gameOverLabel.setOpacity(1);
                        Timeline gameOverLabelFadeOff = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(gameOverLabel.opacityProperty(), gameOverLabel.getOpacity())),
                            new KeyFrame(new Duration(2000), new KeyValue(gameOverLabel.opacityProperty(), 0))
                        );
                        gameOverLabelFadeOff.play();
                        if (session.getLives() >= 6) {
                            gameOverLabelFadeOff.stop();
                            gameOverLabel.setText("Game Over!");
                            gameOverLabel.setOpacity(1);
                            button_1.setDisable(true);
                            button_2.setDisable(true);
                            userInput.setDisable(true);
                            outcome = "LOST";

                        }

                    }
                    if (session.getGuessWord().equals(session.getHiddenWord())) {
                        outcome = "WON";
                        button_1.setDisable(true);
                        button_2.setDisable(true);
                        button_3.setDisable(true);
                        userInput.setDisable(true);
                        guessWordLabelArray[userLetterSelection].setDisable(true);
                        gameOverLabel.setText("You Win!");
                        gameOverLabel.setOpacity(1);
                    }
                    scoreLabel.setText("Score: " + Integer.toString(session.getScore()));
                    percentage = 100 * ((float) session.getCorrectTries() / session.getTries());
                    percentageLabel.setText("Correct letters: " + String.format("%.2f", percentage) + "%");
                }
                
            }
            
        });
    }

    /* Automatically select next available guess word position */
    private void selectNextSpace() {
        if (userLetterSelection < guessWordLabelArray.length - 1) {
            guessWordBox.getChildren().forEach(elem -> elem.setStyle(null));
            if (userLetterSelection != -1) {
                guessWordLabelArray[userLetterSelection].setDisable(true);
            }
            int idx = 1;
            if ((userLetterSelection + idx) >= guessWordLabelArray.length) {
                return;
            }
            while(guessWordLabelArray[userLetterSelection + idx].isDisabled()) {
                idx++;
                if ((userLetterSelection + idx) >= guessWordLabelArray.length) {
                    return;
                }
            }
            guessWordLabelArray[userLetterSelection + idx].setStyle("-fx-font-size: 40px;");
            setUserLetterSelection(userLetterSelection + idx);
        } else if (userLetterSelection >= guessWordLabelArray.length - 1) {
            guessWordLabelArray[userLetterSelection].setDisable(true);
        }
    }

}
