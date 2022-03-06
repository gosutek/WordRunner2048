package game;

import requesters.SubjectRequester;
import requesters.WorksRequester;

import java.io.File;
import java.util.Random;

import dictionary.Dictionary;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.util.Duration;

public class MainMenuGUI extends Pane{

    private final float windowWidth = 1600;
    private final float windowHeigth = 900;

    private final int GRID_WIDTH = 3000;
    private final int GRID_HEIGHT = 3000;
    private final int CELL_SIZE = 100;

    private CustomGraphics title, press_start;
    private CustomButton button_1, button_2, button_3, button_4, button_5, back;
    private SubScene gridScene;
    private Group graphics, buttons;
    private Dictionary activeDictionary;
    private boolean selectedDictionary;
    private final MainMenuGUI thisObj = this;

    MainMenuGUI() {

        this.selectedDictionary = false;
        this.prefWidth(windowWidth);
        this.prefHeight(windowHeigth);

        AnimatedGrid grid = new AnimatedGrid(GRID_WIDTH, GRID_HEIGHT, CELL_SIZE);
        grid.setTranslateX(-900);
        grid.setTranslateZ(900);
        this.gridScene = new SubScene(grid, GRID_WIDTH, GRID_HEIGHT, true, SceneAntialiasing.BALANCED);
        this.getChildren().add(gridScene);
        gridScene.widthProperty().bind(this.widthProperty());
        gridScene.heightProperty().bind(this.heightProperty());
        gridScene.setCamera(createCamera());
        gridScene.setViewOrder(1);


        createGraphics();

        this.graphics = new Group();
        graphics.getChildren().addAll(title, press_start);
        this.getChildren().add(graphics);

    }

    private PerspectiveCamera createCamera() {
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.getTransforms().addAll(
            new Rotate(-25, Rotate.X_AXIS),
            new Translate(this.getTranslateX() + 100, this.getTranslateY(),  -500)
        );
        return camera;
    }

    private void createGraphics() {

        this.title = new CustomGraphics(this.getClass().getResourceAsStream("../graphics/title_icon.png"), 800.0, 801.0, 1.0);
        this.press_start = new CustomGraphics(this.getClass().getResourceAsStream("../graphics/press_start_cropped.png"), 1024.0, 1600.0, 1.0);

        title.setTranslateX(windowWidth / 4);
        press_start.setTranslateY(windowHeigth - windowHeigth / 5);
        press_start.setTranslateX(windowWidth / 2 - windowWidth / 3);
    }

    public void titleFadeOutAnimation() {

        Timeline titleFadeOut = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(press_start.translateXProperty(), press_start.getTranslateX())),
            new KeyFrame(new Duration(500), new KeyValue(press_start.translateXProperty(), -3500))
        );

        titleFadeOut.play();
        titleFadeOut.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                titleFadeOut.stop(); // unecessary?
                createMainMenuButtons();
                mainMenuFadeInAnimation();
            }
            
        });

    }

    private void mainMenuFadeInAnimation() {

        Timeline newGameAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(button_1.translateYProperty(), button_1.getTranslateY())),
            new KeyFrame(new Duration(200), new KeyValue(button_1.translateYProperty(), 400)) ,
            new KeyFrame(new Duration(200), new KeyValue(button_2.translateYProperty(), button_2.getTranslateY())),
            new KeyFrame(new Duration(400), new KeyValue(button_2.translateYProperty(), 550))
        );

        newGameAnimation.play();
    }

    private void createMainMenuButtons() {
        this.gridScene.setEffect(null);
        this.buttons = new Group();

        this.button_1 = new CustomButton("New Game", windowWidth / 2 - windowWidth / 10, 2000);
        //button_1.setId("new_game_btn");

        this.button_2 = new CustomButton("Quit", windowWidth / 2 - windowWidth / 9.8, 2000);
        //button_2.setId("quit_btn");

        buttons.getChildren().addAll(button_1, button_2);
        this.getChildren().add(buttons);

        button_2.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                System.exit(0);
            }
            
        });

        button_1.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                buttons.getChildren().removeAll(button_1, button_2);
                createNewGameButtons();
                newGameMenuAnimation();
            }
            
        });

    }

    private void createNewGameButtons() {
        button_1.setText("Random");
        button_1.setTranslateY(2000);
        button_2.setText("Load");
        button_2.setTranslateY(2000);

        this.button_3 = new CustomButton("Download", windowWidth / 2 - windowWidth / 10, 2000);

        this.back = new CustomButton("Back", windowWidth / 2 - windowWidth / 10, 2000);

        buttons.getChildren().addAll(button_1, button_2, button_3, back);

        button_1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                thisObj.gridScene.setEffect(new BoxBlur());
                graphics.getChildren().remove(title);
                buttons.getChildren().removeAll(button_1, button_2, button_3, back);
                startOfflineMode(thisObj);
            }
            
        });

        button_3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                buttons.getChildren().removeAll(button_1, button_2, button_3, back);
                createThemeButtons();
                downloadDictionaryAnimation();
                
            }
        });

        back.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                buttons.getChildren().removeAll(button_1, button_2, button_3, back);
                createMainMenuButtons();
                mainMenuFadeInAnimation();
            }
        });
    }

    private void newGameMenuAnimation() {

        this.gridScene.setEffect(new BoxBlur());

        Timeline newGameAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(button_1.translateYProperty(), button_1.getTranslateY())),
            new KeyFrame(new Duration(150), new KeyValue(button_1.translateYProperty(), 350)),
            new KeyFrame(new Duration(150), new KeyValue(button_2.translateYProperty(), button_2.getTranslateY())),
            new KeyFrame(new Duration(300), new KeyValue(button_2.translateYProperty(), 475)),
            new KeyFrame(new Duration(300), new KeyValue(button_3.translateYProperty(), button_3.getTranslateY())),
            new KeyFrame(new Duration(450), new KeyValue(button_3.translateYProperty(), 600)),
            new KeyFrame(new Duration(450), new KeyValue(back.translateYProperty(), back.getTranslateY())),
            new KeyFrame(new Duration(600), new KeyValue(back.translateYProperty(), 725))
        );

        newGameAnimation.play();

    }

    private void createThemeButtons() {

        Label feedback_1 = new Label("Establishing connection...");
        feedback_1.setTranslateX(button_1.getTranslateX() - 50);
        feedback_1.setTranslateY(button_1.getTranslateY() + 10);
        feedback_1.setId("feedback_1");

        Label feedback_2 = new Label("Please wait...");
        feedback_2.setTranslateX(button_1.getTranslateX() - 50);
        feedback_2.setTranslateY(button_1.getTranslateY() + 10);
        feedback_2.setId("feedback_2");

        VBox feedbackBox = new VBox(10);

        Label themeLabel = new Label("Please select a theme");
        themeLabel.setTranslateX(windowWidth / 2 - windowWidth / 5);
        themeLabel.setTranslateY(375);
        themeLabel.setId("theme_label");

        button_1.setText("Sci-Fi");
        button_1.setTranslateY(2000);
        button_2.setText("Mystery");
        button_2.setTranslateY(2000);
        button_3.setText("Horror");
        button_3.setTranslateY(2000);

        this.button_4 = new CustomButton("Fantasy", windowWidth / 2 - windowWidth / 10, 2000);

        this.button_5 = new CustomButton("Custom", windowWidth / 2 - windowWidth / 10, 2000);

        feedbackBox.getChildren().addAll(feedback_1, feedback_2);

        buttons.getChildren().addAll(themeLabel, button_1, button_2, button_3, button_4, button_5, back);

        button_1.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(button_1, button_2, button_3, button_4, back);
                /* buttons.getChildren().removeAll(themeLabel, theme_1_box, theme_2_box, theme_3_box, theme_4_box, back_box); */
                
                loadingSubject(new SubjectRequester(), new WorksRequester(), "science-fiction", feedbackBox, feedback_1, feedback_2);
                setSelectedDictionary();
                
            }
            
        });

        button_2.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(button_1, button_2, button_3, button_4, back);
                /* buttons.getChildren().removeAll(themeLabel, theme_1_box, theme_2_box, theme_3_box, theme_4_box, back_box); */
                
                loadingSubject(new SubjectRequester(), new WorksRequester(), "mystery", feedbackBox, feedback_1, feedback_2);
                setSelectedDictionary();
                
            }
            
        });

        button_3.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(button_1, button_2, button_3, button_4, back);
                /* buttons.getChildren().removeAll(themeLabel, theme_1_box, theme_2_box, theme_3_box, theme_4_box, back_box); */
                
                loadingSubject(new SubjectRequester(), new WorksRequester(), "horror", feedbackBox, feedback_1, feedback_2);
                setSelectedDictionary();
                
            }
            
        });

        button_4.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(button_1, button_2, button_3, button_4, back);
                /* buttons.getChildren().removeAll(themeLabel, theme_1_box, theme_2_box, theme_3_box, theme_4_box, back_box); */
                
                loadingSubject(new SubjectRequester(), new WorksRequester(), "fantasy", feedbackBox, feedback_1, feedback_2);
                setSelectedDictionary();
                
            }
            
        });

        back.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                buttons.getChildren().removeAll(themeLabel, button_1, button_2, button_3, button_4, button_5, back);
                button_1.revertToDefault();
                button_2.revertToDefault();
                button_3.revertToDefault();
                button_4.revertToDefault();
                button_5.revertToDefault();
                createNewGameButtons();
                newGameMenuAnimation();
            }
            
        });
    }

    private void downloadDictionaryAnimation() {

        Timeline downloadDictionaryTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(button_1.translateYProperty(), button_1.getTranslateY())),
            new KeyFrame(new Duration(100), new KeyValue(button_1.translateYProperty(), 475)),
            new KeyFrame(Duration.ZERO, new KeyValue(button_1.translateXProperty(), button_1.getTranslateX())),
            new KeyFrame(new Duration(100), new KeyValue(button_1.translateXProperty(), 0)),

            new KeyFrame(new Duration(100), new KeyValue(button_2.translateYProperty(), button_2.getTranslateY())),
            new KeyFrame(new Duration(200), new KeyValue(button_2.translateYProperty(), 475)),
            new KeyFrame(new Duration(100), new KeyValue(button_2.translateXProperty(), button_2.getTranslateX())),
            new KeyFrame(new Duration(200), new KeyValue(button_2.translateXProperty(), 400)),

            new KeyFrame(new Duration(200), new KeyValue(button_3.translateYProperty(), button_3.getTranslateY())),
            new KeyFrame(new Duration(300), new KeyValue(button_3.translateYProperty(), 475)),
            new KeyFrame(new Duration(200), new KeyValue(button_3.translateXProperty(), button_3.getTranslateX())),
            new KeyFrame(new Duration(300), new KeyValue(button_3.translateXProperty(), 800)),

            new KeyFrame(new Duration(300), new KeyValue(button_4.translateYProperty(), button_4.getTranslateY())),
            new KeyFrame(new Duration(400), new KeyValue(button_4.translateYProperty(), 475)),
            new KeyFrame(new Duration(300), new KeyValue(button_4.translateXProperty(), button_4.getTranslateX())),
            new KeyFrame(new Duration(400), new KeyValue(button_4.translateXProperty(), 1200)),

            new KeyFrame(new Duration(400), new KeyValue(button_5.translateYProperty(), button_5.getTranslateY())),
            new KeyFrame(new Duration(500), new KeyValue(button_5.translateYProperty(), 600)),

            new KeyFrame(new Duration(400), new KeyValue(back.translateYProperty(), back.getTranslateY())),
            new KeyFrame(new Duration(500), new KeyValue(back.translateYProperty(), 725))
        );

        downloadDictionaryTimeline.play();
    }

    private void loadingSubject(SubjectRequester subjectRequester, WorksRequester worksRequester, String subject, VBox feedbackBox, Label feedback_1, Label feedback_2) {
        Task<Dictionary> subjectTask = new Task<Dictionary>() {

            @Override
            protected Dictionary call() throws Exception {
                String workKey = new String();
                Dictionary dictionary;
                try {
                    workKey = subjectRequester.readFromURL(subject)[0];
                    dictionary = new Dictionary(workKey);
                    //results = worksRequester.readFromURL(workKey);
                } catch (Exception exc) {
                    exc.printStackTrace();
                    dictionary = null;
                }
                return dictionary;
            }
            
        };

        Thread subjectThread = new Thread(subjectTask);
        subjectThread.setDaemon(true);
        subjectThread.start();
        buttons.getChildren().add(feedbackBox);

        subjectTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                feedback_1.setText("Choosen book: " + subjectRequester.getResults()[1]);
                feedback_2.setText("");
                setActiveDictionary(subjectTask.getValue());
            }
        });
        subjectTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent evt) {
                feedback_1.setText("Problem connecting to openlibrary.org");
                feedback_2.setText("");
                graphics.getChildren().addAll(button_1, button_2, button_3, button_4, back);
                newGameMenuAnimation();
            }
        });

    }

    private void startOfflineMode(MainMenuGUI thisObj) {

        File directory = new File("./medialab");
        File[] dirArr = directory.listFiles();
        Random rng = new Random();

        Task<SessionGUI> offlineTask = new Task<SessionGUI>() {

            @Override
            protected SessionGUI call() throws Exception {
                SessionGUI sessionGUI;
                try {
                    File randomDictionaryFile = dirArr[rng.nextInt(dirArr.length)];
                    Dictionary randomDictionary = new Dictionary(randomDictionaryFile);
                    sessionGUI = new SessionGUI(randomDictionary);
                    sessionGUI.setViewOrder(-1);
                } catch (Exception exc) {
                    exc.printStackTrace();
                    sessionGUI = null;
                }
                return sessionGUI;
            }
        };

        Thread offlineThread = new Thread(offlineTask);
        offlineThread.setDaemon(true);
        offlineThread.start();

        offlineTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                thisObj.getChildren().add(offlineTask.getValue());
            }
        });
        offlineTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent evt) {

            }
        });

    }

    private void setActiveDictionary(Dictionary dictionary) {
        this.activeDictionary = dictionary;
    }

    public Dictionary getActiveDictionary() {
        return activeDictionary;
    }

    private void setSelectedDictionary() {
        selectedDictionary = true;
    }

    public boolean selectedDictionary() {
        return selectedDictionary;
    }

}
