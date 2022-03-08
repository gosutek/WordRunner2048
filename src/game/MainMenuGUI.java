package game;

import requesters.SubjectRequester;
import requesters.WorksRequester;

import java.io.File;
import java.util.Random;

import dictionary.Dictionary;
import javafx.animation.Animation;
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
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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
    private final MainMenuGUI thisObj = this;

    MainMenuGUI() {

        this.prefWidth(windowWidth);
        this.prefHeight(windowHeigth);

        AnimatedGrid grid = new AnimatedGrid(GRID_WIDTH, GRID_HEIGHT, CELL_SIZE);
        grid.setTranslateX(-600);
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
        title.setTranslateY(-30);
        press_start.setTranslateY(windowHeigth - windowHeigth / 5);
        press_start.setTranslateX(windowWidth / 2 - windowWidth / 3);
    }

    public void titleFadeOutAnimation() {

        Timeline titleFadeOut = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(press_start.translateXProperty(), press_start.getTranslateX())),
            new KeyFrame(new Duration(250), new KeyValue(press_start.translateXProperty(), -3500))
        );

        titleFadeOut.play();
        titleFadeOut.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                titleFadeOut.stop();
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

        this.button_2 = new CustomButton("Quit", windowWidth / 2 - windowWidth / 9.8, 2000);

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

        LoadListView list = new LoadListView(windowWidth, windowHeigth, button_3);

        buttons.getChildren().addAll(button_1, button_2, button_3, back);

        button_1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                thisObj.gridScene.setEffect(new BoxBlur());
                graphics.getChildren().remove(title);
                buttons.getChildren().removeAll(button_1, button_2, button_3, back);
                startRandomSession(thisObj);
            }
            
        });

        button_2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                thisObj.gridScene.setEffect(new BoxBlur());
                graphics.getChildren().remove(title);
                buttons.getChildren().removeAll(button_1, button_2);
                list.disableButton();
                thisObj.getChildren().add(list);
                list.setAlignment(Pos.CENTER);
                EventHandler<MouseEvent> loadEvent = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent arg0) {
                        button_3.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                        String selection = list.getListView().getSelectionModel().getSelectedItem();
                        buttons.getChildren().removeAll(button_3, back);
                        thisObj.getChildren().remove(list);
                        startLoadingSequence(thisObj, selection);
                    }
                };
                button_3.addEventHandler(MouseEvent.MOUSE_CLICKED, loadEvent);
            }
            
        });

        button_3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                if (button_3.getText() == "Download") {
                    buttons.getChildren().removeAll(button_1, button_2, button_3, back);
                    createThemeButtons();
                    downloadDictionaryAnimation();
                }
            }
        });

        back.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                if (!graphics.getChildren().contains(title)) {
                    graphics.getChildren().add(title);
                }
                thisObj.getChildren().remove(list);
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

        Label feedback = new Label("Establishing connection...Please Wait...");
        feedback.setId("feedback_1");

        feedback.setOpacity(0);

        Label customIDLabel = new Label("Enter custom ID(ENTER):");
        TextField customIDTextField = new TextField();
        customIDTextField.setPromptText("OL76837W");
        customIDTextField.setPrefWidth(1500);
        customIDTextField.setId("custom_id_text_field");

        HBox customIDBox = new HBox();
        customIDBox.getChildren().addAll(customIDLabel, customIDTextField);
        customIDBox.setTranslateX(windowWidth / 2 - windowWidth / 4);
        customIDBox.setTranslateY(500);
        customIDBox.setOpacity(0);

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


        buttons.getChildren().addAll(themeLabel, button_1, button_2, button_3, button_4, button_5, back, feedback, customIDBox);

        button_1.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(title);
                buttons.getChildren().removeAll(themeLabel, button_1, button_2, button_3, button_4, button_5, back);
                
                loadingSubject(thisObj, new SubjectRequester(), new WorksRequester(), "science-fiction", feedback);
                
            }
            
        });

        button_2.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(title);
                buttons.getChildren().removeAll(themeLabel, button_1, button_2, button_3, button_4, button_5, back);
                
                loadingSubject(thisObj, new SubjectRequester(), new WorksRequester(), "mystery", feedback);
                
            }
            
        });

        button_3.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(title);
                buttons.getChildren().removeAll(themeLabel, button_1, button_2, button_3, button_4, button_5, back);
                
                loadingSubject(thisObj, new SubjectRequester(), new WorksRequester(), "horror", feedback);
                
            }
            
        });

        button_4.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                graphics.getChildren().removeAll(title);
                buttons.getChildren().removeAll(themeLabel, button_1, button_2, button_3, button_4, button_5, back);
                
                loadingSubject(thisObj, new SubjectRequester(), new WorksRequester(), "fantasy", feedback);
                
            }
            
        });

        button_5.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                customIDBox.setOpacity(1);
                buttons.getChildren().removeAll(themeLabel, button_1, button_2, button_3, button_4, button_5);
                customIDTextField.getParent().requestFocus();
                customIDTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {

                    @Override
                    public void handle(KeyEvent arg0) {
                        if (arg0.getCode() == KeyCode.ENTER) {
                            startCustomSession(thisObj, "/works/" + customIDTextField.getText(), customIDBox);
                        }
                    }
                    
                });
            }
        });

        back.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                customIDBox.setOpacity(0);
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

    private void setBackButtonHandlers(SessionGUI sessionGUI, Button backBtn) {
        backBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                Session currentSession = sessionGUI.getSession();
                graphics.getChildren().add(title);
                thisObj.getChildren().remove(sessionGUI);
                String outcome = (sessionGUI.getOutcome() == null) ? "LOST" : sessionGUI.getOutcome();
                RecordGame record = new RecordGame(
                    currentSession.getHiddenWord().toString(), 
                    outcome,
                    currentSession.getTries(),
                    currentSession.getScore()
                );
                record.save();
                createMainMenuButtons();
                mainMenuFadeInAnimation();
            }
        });
    }

    private void loadingSubject(MainMenuGUI thisObj, SubjectRequester subjectRequester, WorksRequester worksRequester, String subject, Label feedback) {
        Task<SessionGUI> subjectTask = new Task<SessionGUI>() {

            @Override
            protected SessionGUI call() throws Exception {
                SessionGUI sessionGUI;
                String workKey = new String();
                Dictionary dictionary;
                try {
                    Timeline loadingAnimation = new Timeline(
                        new KeyFrame(new Duration(1000), new KeyValue(feedback.textProperty(), "Establishing connection...Please Wait.")),
                        new KeyFrame(new Duration(2000), new KeyValue(feedback.textProperty(), "Establishing connection...Please Wait..")),
                        new KeyFrame(new Duration(3000), new KeyValue(feedback.textProperty(), "Establishing connection...Please Wait..."))
                    );

                    loadingAnimation.setCycleCount(Animation.INDEFINITE);
                    loadingAnimation.play();

                    workKey = subjectRequester.readFromURL(subject)[0];
                    dictionary = new Dictionary(workKey);
                    sessionGUI = new SessionGUI(dictionary);
                    sessionGUI.setViewOrder(-1);
                    loadingAnimation.stop();
                } catch (Exception exc) {
                    exc.printStackTrace();
                    sessionGUI = null;
                }
                return sessionGUI;
            }
            
        };

        Thread subjectThread = new Thread(subjectTask);
        subjectThread.setDaemon(true);
        subjectThread.start();
        feedback.setOpacity(1);

        subjectTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                String errorMessage = subjectTask.getValue().getSession().getDictionary().getErrorMessage();
                if (errorMessage != null) {
                    feedback.setText(errorMessage + "\n Aborting...");
                } else {
                    feedback.setText("Chosen book: " + subjectRequester.getResults()[1]);
                    Timeline loadingOnSucceedAnimation = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(feedback.opacityProperty(), feedback.getOpacity())),
                        new KeyFrame(new Duration(2000), new KeyValue(feedback.opacityProperty(), 0))
                    );
                    loadingOnSucceedAnimation.play();
                    loadingOnSucceedAnimation.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent arg0) {
                            buttons.getChildren().removeAll(feedback);
                            thisObj.getChildren().add(subjectTask.getValue());
                            setBackButtonHandlers(subjectTask.getValue(), subjectTask.getValue().getReturnButton());
                        }
                    });
                }
            }
        });
        subjectTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent evt) {
                feedback.setText("Problem connecting to openlibrary.org");
                graphics.getChildren().addAll(title);
                Timeline loadingOnFailAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(feedback.opacityProperty(), feedback.getOpacity())),
                    new KeyFrame(new Duration(2000), new KeyValue(feedback.opacityProperty(), 0))
                );
                loadingOnFailAnimation.play();
                loadingOnFailAnimation.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent arg0) {
                        buttons.getChildren().removeAll(feedback);
                        createThemeButtons();
                        downloadDictionaryAnimation();
                    }
                });
                newGameMenuAnimation();
            }
        });

    }

    private void startRandomSession(MainMenuGUI thisObj) {

        File directory = new File("./medialab");
        File[] dirArr = directory.listFiles();
        Random rng = new Random();

        if (dirArr.length == 0) {
            System.out.println("Dictionary directory is empty");
            return;
        }

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
                setBackButtonHandlers(offlineTask.getValue(), offlineTask.getValue().getReturnButton());
            }
        });
        offlineTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent evt) {

            }
        });

    }

    private void startCustomSession(MainMenuGUI thisObj, String workKey, HBox customIDBox) {

        Task<SessionGUI> customTask = new Task<SessionGUI>() {

            @Override
            protected SessionGUI call() throws Exception {
                SessionGUI sessionGUI;
                Dictionary dictionary;
                try {
                    dictionary = new Dictionary(workKey);
                    sessionGUI = new SessionGUI(dictionary);
                    sessionGUI.setViewOrder(-1);
                } catch (Exception exc) {
                    exc.printStackTrace();
                    sessionGUI = null;
                }
                return sessionGUI;
            }
        };

        Thread customThread = new Thread(customTask);
        customThread.setDaemon(true);
        customThread.start();

        customTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                graphics.getChildren().remove(title);
                buttons.getChildren().removeAll(customIDBox, back);
                thisObj.getChildren().add(customTask.getValue());
                setBackButtonHandlers(customTask.getValue(), customTask.getValue().getReturnButton());
            }
        });
        customTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent evt) {
            }
        });

    }

    private void startLoadingSequence(MainMenuGUI thisObj, String dictionaryID) {

        Task<SessionGUI> loadTask = new Task<SessionGUI>() {

            @Override
            protected SessionGUI call() throws Exception {
                SessionGUI sessionGUI;
                Dictionary dictionary;
                File dictionaryPath = new File("./medialab/hangman_" + dictionaryID + ".txt");
                try {
                    dictionary = new Dictionary(dictionaryPath);
                    sessionGUI = new SessionGUI(dictionary);
                    sessionGUI.setViewOrder(-1);
                } catch (Exception exc) {
                    exc.printStackTrace();
                    sessionGUI = null;
                }
                return sessionGUI;
            }
        };

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();

        loadTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                graphics.getChildren().remove(title);
                buttons.getChildren().removeAll(back);
                thisObj.getChildren().add(loadTask.getValue());
                setBackButtonHandlers(loadTask.getValue(), loadTask.getValue().getReturnButton());
            }
        });
        loadTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent evt) {
            }
        });

    }
}
