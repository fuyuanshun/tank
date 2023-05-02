package com.cry.scenes;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.cry.util.GameUtils;
import javafx.animation.PauseTransition;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * 过关场景
 */
public class SuccessScene extends SubScene {

    private final PauseTransition pt;

    public SuccessScene() {
        Rectangle rect = new Rectangle(getAppWidth(), getAppHeight(), Color.web("#666666"));
        Text hiText = new Text("HI-SCORE");
        hiText.setFont(Font.font(30));
        hiText.setFill(Color.web("#B53021"));
        hiText.setLayoutY(260);
        hiText.setLayoutX(222);
        Text scoreText = new Text("20000");
        scoreText.setFont(Font.font(30));
        scoreText.setFill(Color.web("#EAA024"));
        scoreText.setLayoutY(260);
        scoreText.setLayoutX(472);
        Text levelText = new Text();
        levelText.setFont(Font.font(25));
        levelText.textProperty().bind(getip("level").asString("STAGE %d"));
        levelText.setFill(Color.web("#EAA024"));
        levelText.setLayoutY(360);
        levelText.setLayoutX(347);

        getContentRoot().getChildren().addAll(rect, hiText, scoreText, levelText);
        pt = new PauseTransition(Duration.seconds(2));

        pt.setOnFinished(event -> {
            FXGL.getSceneService().popSubScene();
            inc("level", 1);
            GameUtils.startLevel();
        });
    }

    @Override
    public void onCreate() {
        pt.play();
    }
}
