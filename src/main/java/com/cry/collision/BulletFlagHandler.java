package com.cry.collision;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.cry.Main;
import com.cry.components.FlagOnHitComponent;
import com.cry.enums.GameType;

/**
 * @author fys
 * 子弹与基地碰撞检测
 */
public class BulletFlagHandler extends CollisionHandler {

    public BulletFlagHandler() {
        super(GameType.BULLET, GameType.FLAG);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity flag) {
        //移除子弹
        bullet.removeFromWorld();
        //爆炸特效
        FXGL.spawn("explode", flag.getCenter().subtract(50/2.0, 50/2.0));
        //更换图片
        flag.getComponent(FlagOnHitComponent.class).onHit();

        FXGL.<Main>getAppCast().gameOver();
    }
}
