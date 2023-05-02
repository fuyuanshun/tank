package com.cry.collision;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.cry.Main;
import com.cry.components.EnemyOnHitComponent;
import com.cry.components.PlayerOnHitComponent;
import com.cry.enums.GameType;

/**
 * @author fys
 * 子弹与坦克的碰撞检测
 */
public class BulletTankHandler extends CollisionHandler {
    public BulletTankHandler() {
        this(GameType.BULLET, GameType.PLAYER);
    }

    public BulletTankHandler(Object a, Object b) {
        super(a, b);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity tank) {
        if (tank.isType(GameType.PLAYER)) {
            tank.getComponent(PlayerOnHitComponent.class).onHit(bullet, tank);
        } else {
            tank.getComponent(EnemyOnHitComponent.class).onHit(bullet, tank);
        }
    }
}
