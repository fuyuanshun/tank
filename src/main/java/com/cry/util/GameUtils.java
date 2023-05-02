package com.cry.util;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.TimerAction;
import com.cry.Main;
import com.cry.components.AutoEnemyComponent;
import com.cry.components.EnemyOnHitComponent;
import com.cry.components.TankEventComponent;
import com.cry.components.TankLevelComponent;
import com.cry.constract.Config;
import com.cry.effects.HelmetEffect;
import com.cry.enums.GameType;
import com.cry.enums.TankLevel;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.set;
import static java.util.Arrays.asList;

/**
 * 游戏工具类，各种公用方法
 */
public class GameUtils {
    private static TimerAction upgradeFlagWallTimerAction;
    private static TimerAction pauseEnemyTankTimerAction;

    private static TimerAction timerAction;

    private GameUtils(){}

    /**
     * 生成道具
     */
    public static void generatedItem(){
        //生成新道具会覆盖地图上的当前道具
        List<Entity> itemList = FXGL.getGameWorld().getEntitiesByType(GameType.ITEM);
        for (Entity item : itemList) {
            item.removeFromWorld();
        }
        //生成新道具的位置
        Rectangle2D rectangle2D = new Rectangle2D(Config.BLOCK_CELL, Config.BLOCK_CELL,
                (Config.GAME_WIDTH_NOT_CONTAINS_INFO - (Config.BLOCK_CELL * 2)) - 30,
                (FXGL.getAppHeight() - (Config.BLOCK_CELL * 2)) - 28);
        int reRandomCount = 0;
        boolean reRandom = true;
        Point2D generatedLocation;
        do {
            generatedLocation = FXGLMath.randomPoint(rectangle2D);
            //如果重试三次还是没有，可能是没有非障碍物的位置，就在障碍物上生成
            reRandomCount++;
            if (reRandomCount > 3) {
                break;
            }
            List<Entity> entitiesInRange = FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(generatedLocation.getX(), generatedLocation.getY(), Config.PLAYER_TANK_WIDTH, Config.PLAYER_TANK_HEIGHT));
            if (entitiesInRange.stream().filter(e -> e.isType(GameType.STONE) || e.isType(GameType.FLAG) || e.isType(GameType.SEA) || e.isType(GameType.PLAYER) || e.isType(GameType.ENEMY)).toList().size() < 1) {
                reRandom = false;
            }
        } while (reRandom);
        FXGL.spawn("item", generatedLocation);
    }

    /**
     * 创建玩家坦克实体，并且伴有“安全帽”道具效果一段时间
     * @return
     */
    public static Entity livePlayer(){
        //创建玩家坦克实体
        Entity player = spawn("player", Config.PLAYER_DIRECTION);
        //创建玩家坦克后有一定的无敌时间
        player.getComponent(EffectComponent.class).startEffect(new HelmetEffect());
        FXGL.inc("tank", -1);
        return player;
    }

    /**
     * 摧毁场上的所有敌方坦克
     */
    public static void destroyGameWordEnemy(){
        List<Entity> enemyList = FXGL.getGameWorld().getEntitiesByType(GameType.ENEMY);
        for (Entity enemy : enemyList) {
            FXGL.spawn("explode", enemy.getCenter().subtract(50/2.0, 50/2.0));
            enemy.removeFromWorld();
        }
        FXGL.inc("destroyEnemyAmount", enemyList.size());
    }

    /**
     * 加固基地，将基地周围的墙升级为石头
     */
    public static void upgradeFlagWall(){
        if (upgradeFlagWallTimerAction != null && !upgradeFlagWallTimerAction.isExpired()) {
            upgradeFlagWallTimerAction.expire();
        }
        for (Point2D point2D : Config.BLOCK_OF_FLAG_LIST) {
            //移除土墙，升级为石头墙
            FXGL.getGameWorld().getEntitiesAt(point2D).stream().filter(e -> e.isType(GameType.BRICK)).forEach(Entity::removeFromWorld);
            FXGL.spawn("itemStone", point2D);
        }
        upgradeFlagWallTimerAction = runOnce(() -> {
            for (Point2D point2D : Config.BLOCK_OF_FLAG_LIST) {
                //变为土墙
                FXGL.getGameWorld().getEntitiesAt(point2D).stream().filter(e -> e.isType(GameType.STONE)).forEach(Entity::removeFromWorld);
                Entity stone = FXGL.spawn("brick", point2D);
                stone.getViewComponent().addChild(FXGL.texture("map/brick.png"));
            }
        }, Config.UPGRADE_FLAG_WALL_SECONDS);
    }

    /**
     * 根据生成坦克的不同，坦克具有不同的特点、血量、移动速度、子弹速度
     * @param spawnData 用于存放该实体的一些必要特性数据
     * @param num 具体是哪辆坦克
     * @return
     */
    public static Entity generatedEnemyTank(SpawnData spawnData, int num){
        int hp;
        TankLevelComponent tankLevelComponent = new TankLevelComponent(TankLevel.LEVEL1.getLevel());
        String mainTexture = "tank/E"+ num +"U.png";
        spawnData.put("textureList", List.of(FXGL.texture(mainTexture)));
        switch (num) {
            //3 9 12为红色坦克，击杀后生成道具
            case 3, 9, 12 -> {
                hp = 1;
                spawnData.put("generatedItem", true);
            }
            case 1, 2, 5, 6, 10, 11 -> hp = 1;
            case 4, 7 -> {
                if (num == 4) {
                    List<Texture> textureArray = asList(
                            FXGL.texture("tank/E5U.png"),
                            FXGL.texture(mainTexture)
                    );
                    spawnData.put("textureList", textureArray);
                } else {
                    List<Texture> textureArray = asList(
                            FXGL.texture("tank/E1U.png"),
                            FXGL.texture(mainTexture)
                    );
                    spawnData.put("textureList", textureArray);
                }
                hp = 2;
            }
            case 8 -> {
                List<Texture> textureArray = asList(
                        FXGL.texture("tank/E"+ 5 +"U.png"),
                        FXGL.texture("tank/E"+ 4 +"U.png"),
                        FXGL.texture(mainTexture)
                );
                spawnData.put("textureList", textureArray);
                hp = 3;
            }
            default -> hp = 0;
        }
        //不同坦克的不同特点
        switch (num) {
            //瘦坦克跑得比别的坦克快
            case 2, 6 -> spawnData.put("specialMoveSpeed", Config.SPECIAL_MOVE_SPEED);
            //子弹速度更快
            case 4, 5, 8 -> spawnData.put("specialBulletSpeed", Config.SPECIAL_BULLET_SPEED);
            //二级子弹
            case 1, 3, 7 -> tankLevelComponent = new TankLevelComponent(TankLevel.LEVEL2.getLevel());
            //三级子弹
            case 9, 10, 12 -> tankLevelComponent = new TankLevelComponent(TankLevel.LEVEL3.getLevel());
            default -> tankLevelComponent = new TankLevelComponent(TankLevel.LEVEL1.getLevel());
        }

        return FXGL
                .entityBuilder(spawnData)
                .type(GameType.ENEMY)
                .bbox(BoundingShape.box(Config.PLAYER_TANK_WIDTH, Config.PLAYER_TANK_HEIGHT))
                //移动组件，提供移动、射击方法
                .with(new TankEventComponent())
                //自动移动、射击
                .with(new AutoEnemyComponent())
                //敌方坦克生命值组件
                .with(new HealthIntComponent(hp))
                //敌方坦克被攻击逻辑组件
                .with(new EnemyOnHitComponent())
                //坦克等级组件
                .with(tankLevelComponent)
                .collidable()
                .build();
    }

    /**
     * 暂停所有敌方坦克
     */
    public static void pauseEnemyTank(){
        if (pauseEnemyTankTimerAction != null && !pauseEnemyTankTimerAction.isExpired()) {
            pauseEnemyTankTimerAction.expire();
        }
        FXGL.set("pauseEnemy", false);
        pauseEnemyTankTimerAction = FXGL.runOnce(()->{
            FXGL.set("pauseEnemy", true);
        }, Config.PAUSE_ENEMY_SECONDS);
    }

    /**
     * 加载关卡
     */
    public static void startLevel(){
        //播放关卡动画
        stageAnimated();
        //**************** 初始化参数 start **************
        //已经生成的敌人坦克数量
        set("generatedTankAmount", 0);
        //已经击杀的敌人坦克数量
        set("destroyEnemyAmount", 0);
        //敌方坦克是否可以移动
        set("pauseEnemy", true);
        //**************** 初始化参数 end **************

        //加载地图
        setLevelFromMap("level" + geti("level") + ".tmx");
        Entity player = GameUtils.livePlayer();
        //创建玩家实体
        FXGL.<Main>getAppCast().setPlayerOne(player);
        //移动组件
        FXGL.<Main>getAppCast().setPlayerOneTankEventComponent(player.getComponent(TankEventComponent.class));

        spawn("enemy", Config.GENERATED_ENEMY_LOCATION[0]);
        spawn("enemy", Config.GENERATED_ENEMY_LOCATION[1]);
        spawn("enemy", Config.GENERATED_ENEMY_LOCATION[2]);
        set("generatedTankAmount", 3);
        generatedEnemy();
    }

    /**
     * 播放关卡动画
     */
    public static void stageAnimated(){
        Rectangle rect1 = new Rectangle(getAppWidth(), getAppHeight() / 2.0, Color.web("#333333"));
        Rectangle rect2 = new Rectangle(getAppWidth(), getAppHeight() / 2.0, Color.web("#333333"));
        rect2.setLayoutY(getAppHeight() / 2.0);
        Text text = new Text("STAGE " + geti("level"));
        text.setFill(Color.WHITE);
        text.setFont(new Font(35));
        text.setLayoutX(getAppWidth() / 2.0 - 80);
        text.setLayoutY(getAppHeight() / 2.0 - 5);
        Pane p1 = new Pane(rect1, rect2, text);

        addUINode(p1);

        Timeline tl = new Timeline(
                new KeyFrame(Duration.seconds(1.2),
                        new KeyValue(rect1.translateYProperty(), -getAppHeight() / 2.0),
                        new KeyValue(rect2.translateYProperty(), getAppHeight() / 2.0)
                ));
        tl.setOnFinished(e -> removeUINode(p1));

        PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
        pt.setOnFinished(e -> {
            text.setVisible(false);
            tl.play();
        });
        pt.play();
        play("start.wav");
    }

    /**
     * 生成敌方坦克
     */
    public static void generatedEnemy(){
        timerAction = run(() -> {
            if (geti("generatedTankAmount") >= Config.ENEMY_COUNT_MAX) {
                if (timerAction != null && !timerAction.isExpired()) {
                    timerAction.expire();
                }
                return;
            }
            //当前场上的敌方坦克数量
            int currentEnemyCount = getGameWorld().getGroup(GameType.ENEMY).getEntitiesCopy().size();
            //当前场上坦克数量比配置中的最大存货数量小，可以尝试生成敌方坦克
            if (currentEnemyCount < Config.ALIVE_ENEMY_COUNT_MAX) {
                int reTryCount = 0;
                boolean flag = false;
                Point2D generatedPoint;
                do {
                    //重试次数
                    if (reTryCount >= 3) {
                        return;
                    }
                    reTryCount++;
                    //生成敌方坦克的位置，如果位置上没有障碍物，则生成，否则重新随机生成敌方坦克的位置
                    generatedPoint = FXGLMath.random(Config.GENERATED_ENEMY_LOCATION).get();
                    //生成坦克位置上的实体
                    List<Entity> entityList = getGameWorld().getEntitiesInRange(
                            new Rectangle2D(
                                    generatedPoint.getX(),
                                    generatedPoint.getY(),
                                    Config.PLAYER_TANK_WIDTH,
                                    Config.PLAYER_TANK_HEIGHT)
                    );
                    //实体包含障碍物（土墙、石头、海、敌方坦克、玩家坦克）时，重新随机生成位置
                    List<Entity> collect = entityList.stream().filter(entity -> entity.isType(GameType.BORDER) || entity.isType(GameType.BRICK) || entity.isType(GameType.STONE) || entity.isType(GameType.SEA) || entity.isType(GameType.PLAYER) || entity.isType(GameType.ENEMY)).toList();
                    if (collect.size() > 0) {
                        flag = true;
                    }
                } while (flag);
                //生成敌方坦克
                spawn("enemy", generatedPoint);
                inc("generatedTankAmount", 1);
            }
        }, Duration.seconds(1));
    }
}
