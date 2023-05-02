package com.cry.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.cry.constract.Config;
import com.cry.enums.DirectionType;
import javafx.util.Duration;

import java.util.List;

/**
 * @author fys
 * 敌人坦克移动组件
 */
public class AutoEnemyComponent extends Component {
    private TankEventComponent tankEventComponent;

    private AnimatedTexture spawnAnimated;

    /**
     * 坦克的方向
     */
    private DirectionType moveDir;

    /**
     * 敌方坦克加载完成前不能移动
     */
    private boolean canMove;

    @Override
    public void onAdded() {
        List<Texture> textureList = entity.getObject("textureList");
        Texture texture = textureList.get(textureList.size() - 1);

        moveDir = DirectionType.DOWN;
        spawnAnimated = new AnimatedTexture(new AnimationChannel(FXGL.image("tank/spawnTank.png"), Duration.seconds(0.4), 4));
        spawnAnimated.setTranslateX(entity.getWidth()/2.0 - spawnAnimated.getFitWidth()/2.0);
        spawnAnimated.setTranslateY(entity.getHeight()/2.0 - spawnAnimated.getFitHeight()/2.0);
        spawnAnimated.loop();
        entity.getViewComponent().addChild(spawnAnimated);

        FXGL.runOnce(() -> {
            if (entity != null && entity.isActive()) {
                ViewComponent viewComponent = entity.getViewComponent();
                viewComponent.addChild(texture);
                viewComponent.removeChild(spawnAnimated);
                canMove = true;
            }
        }, Duration.seconds(1));
    }

    @Override
    public void onUpdate(double tpf) {
        if (!canMove || !FXGL.getb("pauseEnemy")) {
            return;
        }
        //概率调整方向
        if (FXGLMath.randomBoolean(Config.ENEMY_RE_DIR)) {
            moveDir = FXGLMath.random(DirectionType.values()).get();
        }
        //射击
        if (FXGLMath.randomBoolean(Config.ENEMY_SHOOT)) {
            tankEventComponent.shoot();
        }
        //移动，并且获取是否撞墙了
        if (moveAndIsCollision(moveDir)) {
            DirectionType newDir;
            //如果重新调整的方向和移动的方向相同，则重新调整方向
            do {
                newDir = FXGLMath.random(DirectionType.values()).get();
            } while (newDir.getPoint2D().equals(moveDir.getPoint2D()));
            tankEventComponent.setDir(newDir);
        }
    }

    /**
     * 移动，如果碰到障碍物则递归重新朝另一个方向移动
     * @param directionType
     */
    public boolean moveAndIsCollision(DirectionType directionType){
        boolean needRedirect = false;
        switch (directionType) {
            case UP -> needRedirect = tankEventComponent.moveUp();
            case DOWN -> needRedirect = tankEventComponent.moveDown();
            case LEFT -> needRedirect = tankEventComponent.moveLeft();
            case RIGHT -> needRedirect = tankEventComponent.moveRight();
            default -> {}
        }
        return needRedirect;
    }

    public boolean isCanMove() {
        return canMove;
    }
}
