package com.cry.effects;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.cry.constract.Config;
import javafx.util.Duration;

/**
 * 安全帽效果类
 */
public class HelmetEffect extends Effect {

    private AnimatedTexture animatedTexture;

    public HelmetEffect() {
        super(Config.HELMET_KEEP_SECONDS);
        animatedTexture = new AnimatedTexture(new AnimationChannel(FXGL.image("item/armed_helmet.png"), Duration.seconds(1.0), 4));
    }

    @Override
    public void onStart(Entity entity) {
        animatedTexture.setTranslateX(entity.getWidth()/2.0 - animatedTexture.getFitWidth()/2.0);
        animatedTexture.setTranslateY(entity.getHeight()/2.0 - animatedTexture.getFitHeight()/2.0);
        animatedTexture.loop();
        entity.getViewComponent().addChild(animatedTexture);
    }

    @Override
    public void onEnd(Entity entity) {
        entity.getViewComponent().removeChild(animatedTexture);
    }
}
