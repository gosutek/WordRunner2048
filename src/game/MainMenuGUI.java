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

    private CustomGraphics title, press_start, button_1, button_2, button_3, button_4, back;
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
        graphics.getChildren().addAll(title, press_start, button_1, button_2, button_3, button_4, back);
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
        this.press_start = new CustomGraphics(this.getClass().getResourceAsStream("../graphics/press_start_cropped.png"), 1024.0, 1024.0, 1.0);
        this.button_1 = new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 500, 500, 0.8);
        this.button_2 = new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 500, 500, 0.8);
        this.button_3 = new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 500, 500, 0.8);
        this.button_4 = new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 500, 500, 0.8);
        this.back = new CustomGraphics(this.getClass().getResourceAsStream("../graphics/button.png"), 500, 500, 0.8);

        title.setTranslateX(120.0);
        button_1.setTranslateX(260.0);
        button_1.setTranslateY(2000);
        button_2.setTranslateX(260.0);
        button_2.setTranslateY(2000);
        button_3.setTranslateX(260.0);
        button_3.setTranslateY(2000);
        button_4.setTranslateX(260.0);
        button_4.setTranslateY(2000);
        back.setTranslateX(260.0);
        back.setTranslateY(2000);
        press_start.setTranslateY(600.0);
    }

    public void titleFadeOutAnimation() {

        Timeline titleFadeOut = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(title.translateYProperty(), title.getTranslateX())),
            new KeyFrame(new Duration(500), new KeyValue(title.translateYProperty(), - 2000)),
            new KeyFrame(Duration.ZERO, new KeyValue(press_start.translateXProperty(), press_start.getTranslateX())),
            new KeyFrame(new Duration(500), new KeyValue(press_start.translateXProperty(), -3500))
        );

        titleFadeOut.play();
        titleFadeOut.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                titleFadeOut.stop(); // unecessary?
                mainMenuFadeInAnimation();
            }
            
        });

    }

    private void mainMenuFadeInAnimation() {

        Timeline newGameAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(button_1.translateYProperty(), button_1.getTranslateY())),
            new KeyFrame(new Duration(125), new KeyValue(button_1.translateYProperty(), 200)),
            new KeyFrame(new Duration(125), new KeyValue(button_2.translateYProperty(), button_2.getTranslateY())),
            new KeyFrame(new Duration(250), new KeyValue(button_2.translateYProperty(), 350)),
            new KeyFrame(new Duration(250), new KeyValue(button_3.translateYProperty(), button_3.getTranslateY())),
            new KeyFrame(new Duration(375), new KeyValue(button_3.translateYProperty(), 500))
        );

        newGameAnimation.play();
        newGameAnimation.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                createMainMenuButtons();
            }
        });
    }

    private void createMainMenuButtons() {
        this.gridScene.setEffect(null);
        this.buttons = new Group();

        VBox new_game_box = new VBox();
        Button new_game_btn = new Button("New Game");
        new_game_btn.setId("new_game_btn");

        VBox offline_mode_box = new VBox();
        Button offline_mode_btn = new Button("Offline");
        offline_mode_btn.setId("offline_mode_btn");

        VBox quit_box = new VBox();
        Button quit_btn = new Button("Quit");
        quit_btn.setId("quit_btn");

        new_game_box.getChildren().add(new_game_btn);
        new_game_box.setTranslateX(button_1.getTranslateX() + 105);
        new_game_box.setTranslateY(button_1.getTranslateY() + 95);

        offline_mode_box.getChildren().add(offline_mode_btn);
        offline_mode_box.setTranslateX(button_2.getTranslateX() + 125);
        offline_mode_box.setTranslateY(button_2.getTranslateY() + 100);

        quit_box.getChildren().add(quit_btn);
        quit_box.setTranslateX(button_3.getTranslateX() + 145);
        quit_box.setTranslateY(button_3.getTranslateY() + 85);
        buttons.getChildren().addAll(new_game_box, offline_mode_box, quit_box);
        this.getChildren().add(buttons);

        quit_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                System.exit(0);
            }
            
        });

        new_game_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                buttons.getChildren().removeAll(new_game_box, offline_mode_box, quit_box);
                themeFadeInAnimation();
            }
            
        });

        offline_mode_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                thisObj.gridScene.setEffect(new BoxBlur());
                startOfflineMode(thisObj, new_game_box, offline_mode_box, quit_box);
            }
            
        });

    }

    private void themeFadeInAnimation() {

        button_3.setOpacity(1);
        button_4.setOpacity(1);
        back.setOpacity(1);

        this.gridScene.setEffect(new BoxBlur());

        Timeline themeAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(button_1.translateYProperty(), button_1.getTranslateY())),
            new KeyFrame(new Duration(125), new KeyValue(button_1.translateYProperty(), 0)),
            new KeyFrame(new Duration(125), new KeyValue(button_2.translateYProperty(), button_2.getTranslateY())),
            new KeyFrame(new Duration(250), new KeyValue(button_2.translateYProperty(), 125)),
            new KeyFrame(new Duration(250), new KeyValue(button_3.translateYProperty(), button_3.getTranslateY())),
            new KeyFrame(new Duration(375), new KeyValue(button_3.translateYProperty(), 250)),
            new KeyFrame(new Duration(375), new KeyValue(button_4.translateYProperty(), button_4.getTranslateY())),
            new KeyFrame(new Duration(500), new KeyValue(button_4.translateYProperty(), 375)),
            new KeyFrame(new Duration(500), new KeyValue(back.translateYProperty(), back.getTranslateY())),
            new KeyFrame(new Duration(625), new KeyValue(back.translateYProperty(), 500))
        );

        themeAnimation.play();
        themeAnimation.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                createThemeButtons();
            }
            
        });

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
        themeLabel.setTranslateX(button_1.getTranslateX() - 50);
        themeLabel.setTranslateY(button_1.getTranslateY() + 10);
        themeLabel.setId("theme_label");

        VBox theme_1_box = new VBox();
        Button theme_1_btn = new Button("Sci-Fi");
        theme_1_btn.setId("theme_1_btn");

        VBox theme_2_box = new VBox();
        Button theme_2_btn = new Button("Mystery");
        theme_2_btn.setId("theme_2_btn");

        VBox theme_3_box = new VBox();
        Button theme_3_btn = new Button("Horror");
        theme_3_btn.setId("theme_3_btn");

        VBox theme_4_box = new VBox();
        Button theme_4_btn = new Button("Fantasy");
        theme_4_btn.setId("theme_4_btn");

        VBox back_box = new VBox();
        Button back_btn = new Button("Back");
        back_btn.setId("back_btn");

        theme_1_box.getChildren().add(theme_1_btn);
        theme_1_box.setTranslateX(button_1.getTranslateX() + 115);
        theme_1_box.setTranslateY(button_1.getTranslateY() + 97);

        theme_2_box.getChildren().add(theme_2_btn);
        theme_2_box.setTranslateX(button_2.getTranslateX() + 115);
        theme_2_box.setTranslateY(button_2.getTranslateY() + 97);

        theme_3_box.getChildren().add(theme_3_btn);
        theme_3_box.setTranslateX(button_3.getTranslateX() + 115);
        theme_3_box.setTranslateY(button_3.getTranslateY() + 97);

        theme_4_box.getChildren().add(theme_4_btn);
        theme_4_box.setTranslateX(button_4.getTranslateX() + 115);
        theme_4_box.setTranslateY(button_4.getTranslateY() + 97);

        back_box.getChildren().add(back_btn);
        back_box.setTranslateX(back.getTranslateX() + 115);
        back_box.setTranslateY(back.getTranslateY() + 97);

        feedbackBox.getChildren().addAll(feedback_1, feedback_2);

        buttons.getChildren().addAll(themeLabel, theme_1_box, theme_2_box, theme_3_box, theme_4_box, back_box);

        theme_1_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(button_1, button_2, button_3, button_4, back);
                buttons.getChildren().removeAll(themeLabel, theme_1_box, theme_2_box, theme_3_box, theme_4_box, back_box);
                
                loadingSubject(new SubjectRequester(), new WorksRequester(), "science-fiction", feedbackBox, feedback_1, feedback_2);
                setSelectedDictionary();
                
            }
            
        });

        theme_2_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(button_1, button_2, button_3, button_4, back);
                buttons.getChildren().removeAll(themeLabel, theme_1_box, theme_2_box, theme_3_box, theme_4_box, back_box);
                
                loadingSubject(new SubjectRequester(), new WorksRequester(), "mystery", feedbackBox, feedback_1, feedback_2);
                setSelectedDictionary();
                
            }
            
        });

        theme_3_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(button_1, button_2, button_3, button_4, back);
                buttons.getChildren().removeAll(themeLabel, theme_1_box, theme_2_box, theme_3_box, theme_4_box, back_box);
                
                loadingSubject(new SubjectRequester(), new WorksRequester(), "horror", feedbackBox, feedback_1, feedback_2);
                setSelectedDictionary();
                
            }
            
        });

        theme_4_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(button_1, button_2, button_3, button_4, back);
                buttons.getChildren().removeAll(themeLabel, theme_1_box, theme_2_box, theme_3_box, theme_4_box, back_box);
                
                loadingSubject(new SubjectRequester(), new WorksRequester(), "fantasy", feedbackBox, feedback_1, feedback_2);
                setSelectedDictionary();
                
            }
            
        });

        back_btn.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                button_4.setOpacity(0);
                back.setOpacity(0);
                buttons.getChildren().removeAll(themeLabel, theme_1_box, theme_2_box, theme_3_box, theme_4_box, back_box);
                mainMenuFadeInAnimation();
            }
            
        });

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
                themeFadeInAnimation();
            }
        });

    }

    private void startOfflineMode(MainMenuGUI thisObj, VBox new_game_box, VBox offline_mode_box, VBox quit_box) {

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
                graphics.getChildren().removeAll(button_1, button_2, button_3);
                buttons.getChildren().removeAll(new_game_box, offline_mode_box, quit_box);
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
