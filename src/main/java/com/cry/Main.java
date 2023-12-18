package com.cry;

import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.StartupScene;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.cry.collision.*;
import com.cry.components.TankEventComponent;
import com.cry.constract.Config;
import com.cry.enums.GameType;
import com.cry.factories.BlockFactory;
import com.cry.factories.TankFactory;
import com.cry.scenes.*;
import com.cry.util.GameUtils;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author fys
 * 坦克大战 主启动类
 */
public class Main extends GameApplication {

    private Entity playerOne;

    private TankEventComponent playerOneTankEventComponent;

    /**
     * 懒加载失败场景
     */
    private LazyValue<FailedScene> failedSceneLazyValue = new LazyValue<>(FailedScene::new);
    private LazyValue<SuccessScene> successSceneLazyValue = new LazyValue<>(SuccessScene::new);

    @Override
    protected void initSettings(GameSettings settings) {
        //游戏标题
        settings.setTitle(Config.GAME_TITLE);
        //游戏版本号
        settings.setVersion(Config.GAME_VERSION);
        //游戏窗口宽度
        settings.setWidth(Config.GAME_WIDTH);
        //游戏窗口高度
        settings.setHeight(Config.GAME_HEIGHT);
        //游戏图标
        settings.setAppIcon("ui/icon.png");
        //设置鼠标样式
        settings.setDefaultCursor(new CursorInfo("ui/cursor.png", 0, 0));

        settings.setMainMenuEnabled(true);

        settings.setSceneFactory(new SceneFactory() {
            @Override
            public StartupScene newStartup(int width, int height) {
                //自定义启动场景
                return new GameStartupScene(width, height);
            }

            @Override
            public FXGLMenu newMainMenu() {
                //主菜单场景
                return new GameMainMenu();
            }

            @Override
            public LoadingScene newLoadingScene() {
                //游戏前的加载场景
                return new GameLoadingScene();
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        //已经生成的敌人坦克数量
        vars.put("generatedTankAmount", 0);
        //已经击杀的敌人坦克数量
        vars.put("destroyEnemyAmount", 0);
        //玩家坦克复活机会
        vars.put("tank", 3);
        //敌方坦克是否可以移动
        vars.put("pauseEnemy", true);
        //开始关卡
        vars.put("level", 1);
    }

    @Override
    protected void initGame() {
        //设置背景色
        getGameScene().setBackgroundColor(Color.BLACK);
        //添加坦克工厂生成坦克
        getGameWorld().addEntityFactory(new TankFactory());
        //添加墙壁工厂生成墙壁
        getGameWorld().addEntityFactory(new BlockFactory());
        //从第一关开始
        GameUtils.startLevel();
        getip("destroyEnemyAmount").addListener((ob, ov, nv)->{
            //击杀了所有敌方坦克
            if (nv.intValue() == Config.ENEMY_COUNT_MAX) {
                play("Win.wav");
                //游戏过关，3秒后弹出过关页面
                runOnce(()->{
                    getSceneService().pushSubScene(successSceneLazyValue.get());
                }, Duration.seconds(3));
            }
        });
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.UP, this::moveUp);
        onKey(KeyCode.W, this::moveUp);
        onKey(KeyCode.DOWN, this::moveDown);
        onKey(KeyCode.S, this::moveDown);
        onKey(KeyCode.LEFT, this::moveLeft);
        onKey(KeyCode.A, this::moveLeft);
        onKey(KeyCode.RIGHT, this::moveRight);
        onKey(KeyCode.D, this::moveRight);
        onKey(KeyCode.SPACE, this::shoot);
    }

    @Override
    protected void initPhysics() {
        //子弹与地图周围的墙壁碰撞检测
        getPhysicsWorld().addCollisionHandler(new BulletBorderHandler());
        //子弹与土墙的碰撞检测
        getPhysicsWorld().addCollisionHandler(new BulletBlockHandler());
        //子弹与石头的碰撞检测
        getPhysicsWorld().addCollisionHandler(new BulletBlockHandler(GameType.BULLET, GameType.STONE));
        //子弹与玩家坦克的碰撞检测
        getPhysicsWorld().addCollisionHandler(new BulletTankHandler());
        //子弹与敌方坦克的碰撞检测
        getPhysicsWorld().addCollisionHandler(new BulletTankHandler(GameType.BULLET, GameType.ENEMY));
        //玩家子弹与敌方坦克碰撞检测
        getPhysicsWorld().addCollisionHandler(new BulletBulletHandler());
        //子弹与基地碰撞检测
        getPhysicsWorld().addCollisionHandler(new BulletFlagHandler());
        //玩家与道具的碰撞检测
        getPhysicsWorld().addCollisionHandler(new PlayerItemHandler());
    }

    public boolean tankIsReady(){
        return playerOne != null && playerOneTankEventComponent != null;
    }

    private void moveUp(){
        if (tankIsReady()) {
            if (!playerOneTankEventComponent.moveUp()) {
                play("tank_move.wav");
            }
        }
    }

    private void moveDown(){
        if (tankIsReady()) {
            if (!playerOneTankEventComponent.moveDown()) {
                play("tank_move.wav");
            }
        }
    }

    private void moveLeft(){
        if (tankIsReady()) {
            if (!playerOneTankEventComponent.moveLeft()) {
                play("tank_move.wav");
            }
        }
    }

    private void moveRight(){
        if (tankIsReady()) {
            if (!playerOneTankEventComponent.moveRight()) {
                play("tank_move.wav");
            }
        }
    }

    private void shoot(){
        playerOneTankEventComponent.shoot();
    }

    /**
     * 游戏结束，推送游戏失败场景
     */
    public void gameOver(){
        FXGL.getSceneService().pushSubScene(failedSceneLazyValue.get());
    }

    /**
     * 玩家坦克死亡后，使用该方法重新指向复活的实体
     * @param playerOne
     */
    public void setPlayerOne(Entity playerOne) {
        this.playerOne = playerOne;
    }

    public void setPlayerOneTankEventComponent(TankEventComponent playerOneTankEventComponent) {
        this.playerOneTankEventComponent = playerOneTankEventComponent;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
