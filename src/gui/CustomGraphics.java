package gui;

import java.io.InputStream;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * {@code ImageView} of the graphics wrapped in a {@code Group}
 */

class CustomGraphics extends Group {
    
    CustomGraphics(InputStream URL, double fitWidth, double fitHeight, double opacity) {
        Image image = new Image(URL);
        ImageView imgView = new ImageView(image);

        imgView.setFitWidth(fitWidth);
        imgView.setFitHeight(fitHeight);
        imgView.setPickOnBounds(true);
        imgView.setPreserveRatio(true);
        imgView.setOpacity(opacity);

        this.getChildren().add(imgView);
    }

}
