package com.cry.collision;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.cry.enums.GameType;

/**
 * @author fys
 * 控制子弹无法超出地图外
 */
public class BulletBorderHandler extends CollisionHandler {

    public BulletBorderHandler() {
        super(GameType.BULLET, GameType.BORDER);
    }

    @Override
    protected void onCollision(Entity bullet, Entity border) {
        bullet.removeFromWorld();
        FXGL.play("shoot_stone.wav");
    }
}
