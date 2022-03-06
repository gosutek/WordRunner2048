package game;

import java.util.Scanner;
import java.util.regex.Pattern;

import dictionary.Dictionary;
import dictionary.Word;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SessionGUI extends GridPane {
    
    private final float windowWidth = 1600;
    private final float windowHeigth = 900;

    private Session session;
    private String input;
    private HBox guessWordBox, inputBox, candidateWordBox;
    private VBox leftPane;
    private Label wordCountLabel, scoreLabel, percentageLabel, inputLabel, solutionLabel;
    private double percentage;
    private int wordCount, score, userLetterSelection;
    private final CustomGraphics[] crossWordGraphics = {
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang0.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang1.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang2.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang3.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang4.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang5.png"), 500, 500, 1.0),
        new CustomGraphics(this.getClass().getResourceAsStream("../graphics/hang6.png"), 500, 500, 1.0),

    };

    private Dictionary activeDictionary;

    SessionGUI(Dictionary dictionary) {

        leftPane = new VBox(10);
        guessWordBox = new HBox(10);
        inputBox = new HBox(10);
        candidateWordBox = new HBox(10);

        score = 0;
        percentage = 0.00;

        this.setMinSize(windowWidth, windowHeigth);
        this.setMaxSize(windowWidth, windowHeigth);
        this.setWidth(windowWidth);
        this.setHeight(windowHeigth);
        this.setAlignment(Pos.CENTER);
        //this.setGridLinesVisible(true);


        activeDictionary = dictionary;
        wordCount = activeDictionary.getWords();

        session = new Session(activeDictionary);

        wordCountLabel = new Label("Remaining words: " + Integer.toString(wordCount));
        scoreLabel = new Label("Score: " + Integer.toString(score));
        percentageLabel = new Label("Correct word %: " + Double.toString(percentage));
        inputLabel = new Label("Input(ENTER):");
        solutionLabel = new Label(session.getHiddenWord().toString());
        solutionLabel.setOpacity(0);

        leftPane.getChildren().addAll(solutionLabel, guessWordBox);
        guessWordBox.setAlignment(Pos.CENTER);


        TextField userInput = new TextField();
        userInput.setPromptText("Press Enter...");
        setTextFieldHandler(userInput);
        userInput.setId("user_input");


        //inputBox.getChildren().addAll(inputLabel, userInput);


        this.setPadding(new Insets(20, 20, 20, 20));
        this.add(wordCountLabel, 0, 0);
        this.add(scoreLabel, 1, 0);
        GridPane.setHalignment(scoreLabel, HPos.CENTER);
        this.add(percentageLabel, 2, 0);
        this.add(leftPane, 0, 1);
        leftPane.setAlignment(Pos.CENTER);
        this.add(crossWordGraphics[6], 1, 1);
        GridPane.setHalignment(crossWordGraphics[6], HPos.CENTER);
        this.add(candidateWordBox, 2, 1);
        candidateWordBox.setAlignment(Pos.CENTER);
        this.add(inputLabel, 0, 2);
        inputLabel.setAlignment(Pos.CENTER);
        this.add(userInput, 1, 2);
        userInput.setAlignment(Pos.CENTER);
        createBottomButtons();
        /* this.add(new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 250, 250, 0.8), 0, 3);
        this.add(new HBox (
            new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 250, 250, 0.8),
            new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 250, 250, 0.8)),
            1,
            3
        );
        this.add(new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 250, 250, 0.8), 2, 3); */

        play(session);

    }
    protected Session getSession() {
        return session;
    }
    private void setUserLetterSelection(int input) {
        userLetterSelection = input;
        updateCandidateWordLabel(session, userLetterSelection);
    }

    private void setUserInputSelection(String input) {
        this.input = input;
    }

    private void createBottomButtons() {
        Button button_1, button_2, button_3, button_4;
        button_1 = new Button("Dictionary");
        button_2 = new Button("Rounds");
        button_3 = new Button("Solution");
        button_4 = new Button("Back");

        button_1.setGraphic(new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 150, 150, 0.5));
        button_1.setContentDisplay(ContentDisplay.CENTER);
        button_2.setGraphic(new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 150, 150, 0.5));
        button_2.setContentDisplay(ContentDisplay.CENTER);
        button_3.setGraphic(new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 150, 150, 0.5));
        button_3.setContentDisplay(ContentDisplay.CENTER);
        button_4.setGraphic(new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 150, 150, 0.5));
        button_4.setContentDisplay(ContentDisplay.CENTER);


        this.add(button_1, 0, 3);
        this.setVgap(0);
        GridPane.setHalignment(button_1, HPos.CENTER);
        GridPane.setValignment(button_1, VPos.CENTER);
        HBox middleBox = new HBox(100);
        middleBox.getChildren().addAll(button_2, button_3);
        this.add(middleBox, 1, 3);
        GridPane.setHalignment(middleBox, HPos.CENTER);
        GridPane.setValignment(middleBox, VPos.CENTER);
        this.add(button_4, 2, 3);
        GridPane.setHalignment(button_4, HPos.CENTER);
        GridPane.setValignment(button_4, VPos.CENTER);
        
    }

    private void updateCandidateWordLabel(Session session, int pos) {
        session.calcProb();
        candidateWordBox.getChildren().clear();
        Label[] candidateWordLabelArray = new Label[session.getCandidateLetters(pos).length];
        for (int idx = 0; idx < candidateWordLabelArray.length; idx++) {
            candidateWordLabelArray[idx] = new Label(session.getCandidateLetters(pos)[idx]);
            candidateWordBox.getChildren().add(candidateWordLabelArray[idx]);
        }
    }

    private void updateGuessWordLabel(Word hiddenWord, Word guessWord) {

    }

    private void createGuessWordLabels(Word guessWord) {
        Label[] guessWordLabelArray = new Label[guessWord.length()];
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
                    System.out.println(input.getText());
                }
                
            }
            
        });
    }

    void play(Session session) {
        String input = new String();
        Scanner scanner = new Scanner(System.in);
        Word guessWord = new Word(session.getHiddenWord().toString().replaceAll("[A-Z]", "_"));
        createGuessWordLabels(guessWord);
        /* while(!guessWord.equals(session.getHiddenWord())) {
            session.calcProb();
        } */
        
        /* System.out.println(hiddenWord);
        System.out.println(candidateWords);
        while(!guessWord.equals(hiddenWord)) {
            calcProb();
            System.out.println("Input letter: ");
            input = scanner.next();
            System.out.println("Input pos: ");
            
            pos = scanner.nextInt();
            while (!(pos instanceof Integer)) {
                System.out.println("Posision must be a number");
                pos = scanner.nextInt();
            }
            if (updateCandidates(input, pos)) {
                System.out.println("Correct!");
                guessWord.replaceLetter(input, pos);

            } else {
                System.out.println("False!");
            }
            if (lives >= 6) {
                System.out.println("Game over");
                System.out.println("Hidden word was: " + hiddenWord);
                break;
            }
            System.out.println(candidateWords);
        }
        if (guessWord.equals(hiddenWord)) {
            System.out.println("You win!");
        }
        scanner.close(); */
    }

}
