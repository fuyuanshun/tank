package com.cry.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.cry.enums.GameType;

import java.io.Serializable;

/**
 * @author fys
 * 我方子弹与敌方子弹碰撞检测
 */
public class BulletBulletHandler extends CollisionHandler {
    public BulletBulletHandler() {
        super(GameType.BULLET, GameType.BULLET);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity anotherBullet) {
        Serializable aOwnerType = bullet.<Entity>getObject("ownerType").getType();
        Serializable bOwnerType = anotherBullet.<Entity>getObject("ownerType").getType();
        if (aOwnerType != bOwnerType) {
            anotherBullet.removeFromWorld();
            anotherBullet.removeFromWorld();
        }
    }
}
