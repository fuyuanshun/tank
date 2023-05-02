package com.cry.collision;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.CollisionHandler;
import com.cry.enums.GameType;
import com.cry.enums.TankLevel;

import java.io.Serializable;
import java.util.List;

/**
 * @author fys
 * 子弹和墙体的碰撞检测
 */
public class BulletBlockHandler extends CollisionHandler {

    public BulletBlockHandler() {
        this(GameType.BULLET, GameType.BRICK);
    }

    public BulletBlockHandler(Object a, Object b) {
        super(a, b);
    }

    @Override
    protected void onCollision(Entity bullet, Entity block) {
        //获取与子弹碰撞的土墙、石头
        List<Entity> entitiesFiltered = FXGL.getGameWorld().getEntitiesFiltered(e ->
                e.isColliding(bullet)
                        && (e.isType(GameType.BRICK) || e.isType(GameType.STONE)));
        int tankLevel = bullet.getInt("tankLevel");
        boolean isDestroy = false;
        for (Entity entity : entitiesFiltered) {
            Serializable type = entity.getType();
            switch ((GameType)type) {
                case BRICK : {
                    entity.removeFromWorld();
                    isDestroy = true;
                    break;
                }
                case STONE : {
                    if (tankLevel == TankLevel.LEVEL3.getLevel()) {
                        entity.removeFromWorld();
                        isDestroy = true;
                    }
                    break;
                }
                default : {
                }
            }
        }
        bullet.removeFromWorld();
        if (isDestroy) {
            FXGL.spawn("explode", bullet.getCenter().subtract(50/2.0, 50/2.0));
        } else {
            FXGL.play("shoot_stone.wav");
        }
    }
}
