package gui;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;

/**
 * {@code Button} subclass for implementing the buttons of the GUI.
 */

public class CustomButton extends Button {
    private final CustomGraphics buttonGraphics = new CustomGraphics(this.getClass().getResourceAsStream("graphics/button.png"), 300, 300, 0.7);
    private final double defaultWidth, defaultHeight;

    CustomButton(String text, double customX, double customY) {
        this.defaultWidth = customX;
        this.defaultHeight = customY;
        this.setText(text);
        this.setGraphic(buttonGraphics);
        this.setContentDisplay(ContentDisplay.CENTER);
        this.setTranslateX(customX);
        this.setTranslateY(customY);
    }

    public void revertToDefault() {
        this.setTranslateX(defaultWidth);
        this.setTranslateY(defaultHeight);
    }
}
