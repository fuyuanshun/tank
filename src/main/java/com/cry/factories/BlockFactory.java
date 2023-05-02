package com.cry.factories;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.cry.components.FlagOnHitComponent;
import com.cry.constract.Config;
import com.cry.enums.GameType;
import com.cry.util.GameUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * 墙壁工厂，用于生成墙壁实体
 */
public class BlockFactory implements EntityFactory {
    private final AnimationChannel seaAnimationChannel = new AnimationChannel(FXGL.image("map/sea_anim.png"), Duration.seconds(1.5), 2);

    private static final List<Image> STONE_BRICK_FLICKER = asList(
            FXGL.image("map/stone.png"),
            FXGL.image("map/brick.png")
    );
    private static final AnimationChannel STONE_BRICK_FLICKER_CHANNEL = new AnimationChannel(STONE_BRICK_FLICKER, Duration.seconds(0.5));

    /**
     * 游戏周围的墙壁
     * @param spawnData
     * @return
     */
    @Spawns("border")
    public Entity createBorder(SpawnData spawnData){
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.BORDER)
                .viewWithBBox(new Rectangle(spawnData.<Integer>get("width"), spawnData.<Integer>get("height"), Color.LIGHTGRAY))
                .neverUpdated()
                .collidable()
                .build();
    }

    /**
     * 土墙
     * @param spawnData
     * @return
     */
    @Spawns("brick")
    public Entity createBrick(SpawnData spawnData){
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.BRICK)
                .bbox(BoundingShape.box(Config.BLOCK_CELL, Config.BLOCK_CELL))
                .neverUpdated()
                .collidable()
                .build();
    }

    /**
     * 草地，坦克会被遮挡
     * @param spawnData
     * @return
     */
    @Spawns("greens")
    public Entity createGreens(SpawnData spawnData){
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.GREENS)
                .neverUpdated()
                .zIndex(2)
                .build();
    }

    /**
     * 雪地，坦克的移动速度更快
     * @param spawnData
     * @return
     */
    @Spawns("snow")
    public Entity createSnow(SpawnData spawnData){
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.SNOW)
                .bbox(BoundingShape.box(Config.BLOCK_CELL, Config.BLOCK_CELL))
                .neverUpdated()
                .collidable()
                .build();
    }

    /**
     * 石头
     * 三级坦克才可以摧毁
     * @param spawnData
     * @return
     */
    @Spawns("stone")
    public Entity createStone(SpawnData spawnData){
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.STONE)
                .bbox(BoundingShape.box(Config.BLOCK_CELL, Config.BLOCK_CELL))
                .neverUpdated()
                .collidable()
                .build();
    }

    /**
     * 吃到铁锹道具后的基地的墙壁
     * 可以进行闪烁
     * @param spawnData
     * @return
     */
    @Spawns("itemStone")
    public Entity createItemStone(SpawnData spawnData){
        AnimatedTexture animatedTexture = new AnimatedTexture(STONE_BRICK_FLICKER_CHANNEL);

        FXGL.runOnce(animatedTexture::loop, Config.FLAG_WALL_FLICKER_SECONDS);
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.STONE)
                .viewWithBBox(animatedTexture)
                .collidable()
                .build();
    }

    /**
     * 海
     * 坦克无法通过，但是子弹可以通过
     * @param spawnData
     * @return
     */
    @Spawns("sea")
    public Entity createSea(SpawnData spawnData){
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.SEA)
                .viewWithBBox(new AnimatedTexture(seaAnimationChannel).loop())
                .collidable()
                .build();
    }

    /**
     * 基地
     * @param spawnData
     * @return
     */
    @Spawns("flag")
    public Entity createFlag(SpawnData spawnData){
        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.FLAG)
                .bbox(BoundingShape.box(48, 48))
                .with(new FlagOnHitComponent())
                .neverUpdated()
                .collidable()
                .build();
    }
}
