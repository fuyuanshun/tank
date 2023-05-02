package com.cry.factories;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.cry.components.PlayerOnHitComponent;
import com.cry.components.TankEventComponent;
import com.cry.components.TankLevelComponent;
import com.cry.constract.Config;
import com.cry.enums.GameType;
import com.cry.enums.ItemType;
import com.cry.util.GameUtils;
import javafx.util.Duration;

/**
 * @author fys
 * 坦克工厂，用于生成坦克实体类
 */
public class TankFactory implements EntityFactory {

    private final Duration explodeTime = Duration.seconds(0.5);
    private final AnimationChannel explodeAnimationChannel = new AnimationChannel(FXGL.image("explode/explode_level_2.png"), explodeTime, 9);

    /**
     * 玩家实体
     * @param spawnData
     * @return
     */
    @Spawns("player")
    public Entity createPlayerOne(SpawnData spawnData){
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.PLAYER)
                .viewWithBBox("tank/H1U.png")
                //坦克控制组件
                .with(new TankEventComponent())
                //玩家坦克被攻击逻辑组件
                .with(new PlayerOnHitComponent())
                //坦克等级组件
                .with(new TankLevelComponent(1))
                //效果组件
                .with(new EffectComponent())
                .collidable()
                .build();
    }

    /**
     * 敌方坦克实体
     * @param spawnData
     * @return
     */
    @Spawns("enemy")
    public Entity createEnemy(SpawnData spawnData){
        //随机生成的坦克编号
        int num = FXGLMath.random(1, 12);
        //根据生成坦克的不同，坦克具有不同的特点、血量、移动速度、子弹速度
        return GameUtils.generatedEnemyTank(spawnData, num);
    }

    /**
     * 子弹
     * @param spawnData
     * @return
     */
    @Spawns("bullet")
    public Entity createBullet(SpawnData spawnData){
        CollidableComponent collidableComponent = new CollidableComponent(true);
        collidableComponent.addIgnoredType(spawnData.<Entity>get("ownerType").getType());

        //发射子弹音效
        FXGL.play("tank_shoot.wav");
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.BULLET)
                .viewWithBBox("bullet/normal.png")
                .with(new ProjectileComponent(spawnData.get("shootVec"), spawnData.<Double>get("bulletMoveSpeed")))
                .with(collidableComponent)
                .build();
    }

    /**
     * 爆炸特效
     * @param spawnData
     * @return
     */
    @Spawns("explode")
    public Entity createExplode(SpawnData spawnData){
        //爆炸音效
        FXGL.play("shoot_boom.wav");
        return FXGL
                .entityBuilder(spawnData)
                .view(new AnimatedTexture(explodeAnimationChannel).play())
                .with(new ExpireCleanComponent(explodeTime))
                .build();
    }

    /**
     * 道具
     * @param spawnData
     * @return
     */
    @Spawns("item")
    public Entity createItem(SpawnData spawnData){
        ItemType itemType = FXGLMath.random(ItemType.values()).get();
        spawnData.put("itemType", itemType);
        AnimationChannel ac = new AnimationChannel(FXGL.image("item/" + itemType + ".png"), 1, 30, 28, Duration.seconds(.5), 0, 1);
        AnimatedTexture animatedTexture = new AnimatedTexture(ac);
        FXGL.runOnce(animatedTexture::loop, Config.ITEM_FLICKER_SECONDS);
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.ITEM)
                .viewWithBBox(animatedTexture)
                .with(new ExpireCleanComponent(Config.ITEM_KEEP_SECONDS))
                .collidable()
                .build();
    }
}
