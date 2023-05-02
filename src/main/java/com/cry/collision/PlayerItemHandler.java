package com.cry.collision;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.cry.components.TankLevelComponent;
import com.cry.constract.Config;
import com.cry.effects.HelmetEffect;
import com.cry.enums.GameType;
import com.cry.enums.ItemType;
import com.cry.util.GameUtils;
import javafx.geometry.Point2D;

import java.util.List;

/**
 * 玩家与道具的碰撞检测
 */
public class PlayerItemHandler extends CollisionHandler {

    public PlayerItemHandler() {
        super(GameType.PLAYER, GameType.ITEM);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity item) {
        ItemType itemType = item.getObject("itemType");

        item.removeFromWorld();
        FXGL.play("item_collision.wav");

        TankLevelComponent component = player.getComponent(TankLevelComponent.class);
        switch (itemType) {
            //星星，加一级子弹（最高3级）
            case STAR -> component.upgrade();
            //手枪，子弹变为最高级（3级）
            case GUN -> component.upgradeMax();
            //玩家坦克进入无敌状态
            case HELMET -> player.getComponent(EffectComponent.class).startEffect(new HelmetEffect());
            //摧毁敌方所有坦克
            case BOMB -> GameUtils.destroyGameWordEnemy();
            //增加一条命
            case TANK -> FXGL.inc("tank", 1);
            //暂停敌方所有坦克
            case TIME -> GameUtils.pauseEnemyTank();
            //加固基地
            case SPADE -> GameUtils.upgradeFlagWall();
        }
    }
}
