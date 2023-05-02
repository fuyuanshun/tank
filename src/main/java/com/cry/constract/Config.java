package com.cry.constract;

import javafx.geometry.Point2D;
import javafx.util.Duration;


/**
 * @author fys
 * 常量定义
 */
public class Config {
    /**
     * 游戏标题
     */
    public static final String GAME_TITLE = "坦克大战";

    /**
     * 游戏版本号
     */
    public static final String GAME_VERSION = "Version 0.1";

    /**
     * 玩家坦克的宽度
     */
    public static final int PLAYER_TANK_WIDTH = 39;

    /**
     * 玩家坦克的高度
     */
    public static final int PLAYER_TANK_HEIGHT = 39;

    /**
     * 每块地图元素的边长(px)
     */
    public static final int BLOCK_CELL = 24;

    /**
     * 游戏窗口宽度(px)，不包含信息窗口
     */
    public static final int GAME_WIDTH_NOT_CONTAINS_INFO = 26 * BLOCK_CELL;

    /**
     * 游戏窗口宽度(px)，包含信息窗口
     */
    public static final int GAME_WIDTH = (26 + 5) * BLOCK_CELL;

    /**
     * 游戏窗口高度(px)
     */
    public static final int GAME_HEIGHT = 26 * BLOCK_CELL;

    /**
     * 敌方普通坦克移动速度(px)
     */
    public static final double MOVE_SPEED = 100;

    /**
     * 敌方特殊坦克移动速度(px)
     */
    public static final double SPECIAL_MOVE_SPEED = MOVE_SPEED * 2.5;

    /**
     * 一级坦克子弹移动速度(px)
     */
    public static final double LEVEL1_BULLET_SPEED = 500;

    /**
     * 二级坦克子弹移动速度(px)
     */
    public static final double LEVEL2_BULLET_SPEED = 600;

    /**
     * 三级坦克子弹移动速度(px)
     */
    public static final double LEVEL3_BULLET_SPEED = 600;

    /**
     * 敌方特殊坦克子弹速度
     */
    public static final double SPECIAL_BULLET_SPEED = 750;

    /**
     * 玩家坦克发射子弹的频率
     */
    public static final Duration SHOOT_FREQUENCY = Duration.seconds(0.35);

    /**
     * 坦克等级为1时可以同时发射的子弹数量
     */
    public static final int LEVEL1_SHOOT_COUNT = 1;

    /**
     * 坦克等级为2时可以同时发射的子弹数量
     */
    public static final int LEVEL2_SHOOT_COUNT = 2;

    /**
     * 坦克等级为3时可以同时发射的子弹数量
     */
    public static final int LEVEL3_SHOOT_COUNT = 2;

    /**
     * 敌方坦克每帧调整方向的概率
     * 每秒60帧，每秒的概率为 (60 * ENEMY_RE_DIR)
     */
    public static final double ENEMY_RE_DIR = 0.025;

    /**
     * 敌方坦克每帧发射子弹的概率
     * 每秒60帧，每秒的概率为 (60 * ENEMY_SHOOT)
     */
    public static final double ENEMY_SHOOT = 0.01;

    /**
     * 生成玩家坦克的位置
     */
    public static final Point2D PLAYER_DIRECTION = new Point2D(
            //感觉宽度有问题，多加一个偏移像素
            (GAME_WIDTH_NOT_CONTAINS_INFO / 2.0) - (BLOCK_CELL * 2) - (PLAYER_TANK_WIDTH + 1),
            (GAME_HEIGHT - BLOCK_CELL) - (PLAYER_TANK_WIDTH + 1));

    /**
     * 生成敌方坦克的位置
     */
    public static final Point2D[] GENERATED_ENEMY_LOCATION = {
            new Point2D(BLOCK_CELL + 1, BLOCK_CELL + 1),
            new Point2D((GAME_WIDTH_NOT_CONTAINS_INFO / 2.0) - (PLAYER_TANK_WIDTH/2.0), BLOCK_CELL + 1),
            new Point2D((GAME_WIDTH_NOT_CONTAINS_INFO - (BLOCK_CELL + 1) - PLAYER_TANK_WIDTH), BLOCK_CELL + 1)
    };

    /**
     * 场上敌人的最大数量
     */
    public static final int ALIVE_ENEMY_COUNT_MAX = 5;

    /**
     * 默认的每局游戏的敌人数量
     */
    public static final int ENEMY_COUNT_MAX = 5;

    /**
     * 道具持续时间
     */
    public static final Duration ITEM_KEEP_SECONDS = Duration.seconds(15);

    /**
     * 生成道具多少秒后进行闪烁提示
     */
    public static final Duration ITEM_FLICKER_SECONDS = Duration.seconds(12);

    /**
     * 安全帽无敌时间
     */
    public static final Duration HELMET_KEEP_SECONDS = Duration.seconds(15);

    /**
     * 铁锹道具-加固基地时间
     */
    public static final Duration UPGRADE_FLAG_WALL_SECONDS = Duration.seconds(15);

    /**
     * 定时器道具-暂停敌方坦克时间
     */
    public static final Duration PAUSE_ENEMY_SECONDS = Duration.seconds(15);

    /**
     * 铁锹效果经过多少秒进行闪烁提示
     */
    public static final Duration FLAG_WALL_FLICKER_SECONDS = Duration.seconds(12);

    /**
     * 基地周围的墙坐标
     */
    public static final Point2D[] BLOCK_OF_FLAG_LIST = {
            new Point2D(264, 600 - BLOCK_CELL),
            new Point2D(264, 576 - BLOCK_CELL),
            new Point2D(264, 552 - BLOCK_CELL),
            new Point2D(288, 552 - BLOCK_CELL),
            new Point2D(312, 552 - BLOCK_CELL),
            new Point2D(336, 552 - BLOCK_CELL),
            new Point2D(336, 576 - BLOCK_CELL),
            new Point2D(336, 600 - BLOCK_CELL)
    };

    /**
     * 总关卡数
     */
    public static final int LEVEL_COUNT = 10;
}
