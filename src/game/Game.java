package game;

import java.io.File;
import java.io.IOException;

import gui.MainMenuGUI;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Game extends Application {

    private final float windowWidth = 1600;
    private final float windowHeigth = 900;
    private MainMenuGUI mainMenuGUI;


    @Override
    public void start(Stage stage) throws Exception {
        File dictionaryFile = new File("dictionaries");
        if(!dictionaryFile.exists()) {
            dictionaryFile.mkdir();
        }

        this.mainMenuGUI = new MainMenuGUI();
        Scene startScene = new Scene(mainMenuGUI, windowWidth, windowHeigth, true);
        startScene.setFill(Color.BLACK);
        startTitleScreenEventListeners(mainMenuGUI, startScene);

        stage.setTitle("Word Runner 2048");

        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        stage.setScene(startScene);
        stage.setResizable(false);
        startScene.getStylesheets().add(Game.class.getResource("Game.css").toExternalForm());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void startTitleScreenEventListeners(MainMenuGUI mainMenuGUI, Scene startScene) {
        final EventHandler<MouseEvent> titleScreenEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                startScene.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                mainMenuGUI.titleFadeOutAnimation();
            }
            
        };
        startScene.addEventHandler(MouseEvent.MOUSE_CLICKED, titleScreenEventHandler);
    }

}
