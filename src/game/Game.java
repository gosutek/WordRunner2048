package game;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Game extends Application {

    private final float windowWidth = 1024;
    private final float windowHeigth = 768;
    private MainMenuGUI mainMenuGUI;
    private SessionGUI sessionGUI;


    @Override
    public void start(Stage stage) throws Exception {
        this.mainMenuGUI = new MainMenuGUI();
        Scene startScene = new Scene(mainMenuGUI, windowWidth, windowHeigth, true);
        startScene.setFill(Color.BLACK);
        startTitleScreenEventListeners(mainMenuGUI, startScene);

        stage.setTitle("Word Runner 2048");

        stage.getIcons().add(new Image(getClass().getResourceAsStream("../graphics/icon.png")));
        stage.setScene(startScene);
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
