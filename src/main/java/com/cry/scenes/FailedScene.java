package com.cry.scenes;


import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import com.cry.constract.Config;
import com.cry.enums.GameType;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.play;

public class FailedScene extends SubScene {
    private TranslateTransition translateTransition;
    public FailedScene(){
        Texture texture = FXGL.texture("ui/GameOver.png");
        //设置图片比例
        texture.setScaleX(2);
        texture.setScaleY(2);
        //设置图片位置
        texture.setLayoutX(Config.GAME_WIDTH_NOT_CONTAINS_INFO / 2.0 - texture.getWidth()/2.0);
        texture.setLayoutY(FXGL.getAppHeight());
        //动画
        translateTransition = new TranslateTransition(Duration.seconds(2.5), texture);
        //设置播放效果
        translateTransition.setInterpolator(Interpolators.ELASTIC.EASE_OUT());
        translateTransition.setFromY(0);
        translateTransition.setToY(-(FXGL.getAppHeight() - 260));
        translateTransition.setOnFinished(e->{
            getGameWorld().getEntitiesByType(
                    GameType.BULLET, GameType.ENEMY, GameType.PLAYER
            ).forEach(Entity::removeFromWorld);

            FXGL.getSceneService().popSubScene();
            FXGL.getGameController().gotoMainMenu();
        });
        getContentRoot().getChildren().add(texture);
    }

    @Override
    public void onCreate() {
        //播放游戏结束音效
        play("GameOver.wav");
        translateTransition.play();
    }
}
