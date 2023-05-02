package com.cry.components;

import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityGroup;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.cry.constract.Config;
import com.cry.enums.DirectionType;
import com.cry.enums.GameType;
import com.cry.enums.TankLevel;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fys
 * 移动组件
 */
public class TankEventComponent extends Component {

    /**
     * 移动的距离
     */
    private double distance;

    /**
     * 子弹发射频率
     */
    private LocalTimer localTimer;

    /**
     * 坦克当前的方向
     */
    private DirectionType dir = DirectionType.UP;

    /**
     * 坦克当前是否正在移动，用于控制坦克同时只能朝一个方向移动
     */
    private boolean moving;

    /**
     * 碰撞墙体
     * @param tpf time per frame
     */
    private LazyValue<EntityGroup> collisionListLazyValue = new LazyValue<>(()->
            entity.getWorld()
                //碰撞检测类型
                .getGroup(GameType.BORDER, GameType.BRICK, GameType.FLAG, GameType.STONE, GameType.SEA, GameType.PLAYER, GameType.ENEMY)
    );

    @Override
    public void onAdded() {
        localTimer = FXGL.newLocalTimer();
    }

    @Override
    public void onUpdate(double tpf) {
        distance = tpf * entity.<Double>getPropertyOptional("specialMoveSpeed").orElse(Config.MOVE_SPEED);
        moving = false;
    }

    /**
     * 向上移动
     */
    public boolean moveUp(){
        if (moving) {
            return false;
        }
        moving = true;
        entity.setRotation(0);
        dir = DirectionType.UP;
        return move();
    }

    /**
     * 向下移动
     */
    public boolean moveDown(){
        if (moving) {
            return false;
        }
        moving = true;
        entity.setRotation(180);
        dir = DirectionType.DOWN;
        return move();
    }

    /**
     * 向左移动
     */
    public boolean moveLeft(){
        if (moving) {
            return false;
        }
        moving = true;
        entity.setRotation(270);
        dir = DirectionType.LEFT;
        return move();
    }

    /**
     * 向右移动
     */
    public boolean moveRight(){
        if (moving) {
            return false;
        }
        moving = true;
        entity.setRotation(90);
        dir = DirectionType.RIGHT;
        return move();
    }

    /**
     * 发射子弹
     */
    public void shoot(){
        TankLevelComponent component = entity.getComponent(TankLevelComponent.class);
        int tankLevel = component.getLevel();
        double tankLevelShootSpeed = Config.LEVEL1_BULLET_SPEED;
        int count = 0;
        boolean canShoot = true;
        //获取场上所有的子弹实体
        List<Entity> ownerTypeList = FXGL.getGameWorld().getGroup(GameType.BULLET).getEntitiesCopy().stream().map(e -> e.<Entity>getObject("ownerType")).collect(Collectors.toList());
        //场上是否包含当前坦克的子弹
        for (Entity ownerType : ownerTypeList) {
            if (ownerType == entity) {
                count ++;
            }
        }
        //一级子弹最多只能发射一颗子弹
        if (tankLevel == TankLevel.LEVEL1.getLevel() && count >= Config.LEVEL1_SHOOT_COUNT) {
            canShoot = false;
            tankLevelShootSpeed = Config.LEVEL1_BULLET_SPEED;
        }
        //二级子弹可以同时发射两颗子弹
        if (tankLevel == TankLevel.LEVEL2.getLevel() && count >= Config.LEVEL2_SHOOT_COUNT) {
            canShoot = false;
            tankLevelShootSpeed = Config.LEVEL2_BULLET_SPEED;
        }
        //三级子弹可以同时发射两颗子弹
        if (tankLevel == TankLevel.LEVEL3.getLevel() && count >= Config.LEVEL3_SHOOT_COUNT) {
            canShoot = false;
            tankLevelShootSpeed = Config.LEVEL3_BULLET_SPEED;
        }
        //需要满足两个条件：1.同时发射的子弹数量  2.经过指定的时间频率
        if (canShoot && localTimer.elapsed(Config.SHOOT_FREQUENCY)) {
            //发射子弹的位置，坦克的中心点-子弹的中心点
            Point2D direction = entity.getCenter().subtract(8 / 2.0, 10 / 2.0);
            SpawnData spawnData = new SpawnData(direction);
            //发射的方向
            spawnData.put("shootVec", dir.getPoint2D());
            //发射子弹的实体，用于忽略友方子弹
            spawnData.put("ownerType", entity);
            spawnData.put("tankLevel", tankLevel);
            spawnData.put("bulletMoveSpeed", Math.max(entity.<Double>getPropertyOptional("specialBulletSpeed").orElse(tankLevelShootSpeed), tankLevelShootSpeed));
            //发射子弹
            FXGL.spawn("bullet", spawnData);
            localTimer.capture();
        }
    }

    public boolean move(){
        //可碰撞实体列表
        List<Entity> collisionList = collisionListLazyValue.get().getEntitiesCopy();
        //把自己从碰撞列表移除
        collisionList.remove(entity);
        //获取总共的移动距离，每次移动1
        for (int i = 0; i < Math.round(distance); i++) {
            //移动
            entity.translate(dir.getPoint2D());
            //获取可碰撞列表，如果发生碰撞，则移动回去
            for (Entity e : collisionList) {
                if (e.isColliding(entity)) {
                    entity.translate(-dir.getPoint2D().getX(), -dir.getPoint2D().getY());
                    return true;
                }
            }
        }
        return false;
    }

    public void setDir(DirectionType dir) {
        this.dir = dir;
    }
}
