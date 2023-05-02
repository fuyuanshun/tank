package com.cry.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import com.cry.util.GameUtils;

import java.util.HashMap;
import java.util.List;

public class EnemyOnHitComponent extends Component {

    /**
     * 敌方坦克被攻击的逻辑
     * 1. 坦克是否加载完成，未完成时无法被攻击。如果已经加载完成，见逻辑2
     * 2. 可能死亡，判断是否生成道具
     * 3. 不死亡，修改坦克的视图
     * @param bullet
     * @param tank
     */
    public void onHit(Entity bullet, Entity tank){
        //未加载完成时无法攻击
        if (!tank.getComponent(AutoEnemyComponent.class).isCanMove()) {
            return;
        }
        //移除子弹
        bullet.removeFromWorld();
        //被攻击后的生命值
        int hp;
        //获取生命值组件，判断是否死亡
        HealthIntComponent component = tank.getComponent(HealthIntComponent.class);
        component.setValue(hp = component.getValue() - 1);
        //已经死亡
        if (hp <= 0) {
            //是否生成道具
            boolean generatedItem = tank.<Boolean>getPropertyOptional("generatedItem").orElse(false);
            tank.removeFromWorld();
            FXGL.inc("destroyEnemyAmount", 1);
            //爆炸特效
            FXGL.spawn("explode", bullet.getCenter().subtract(50/2.0, 50/2.0));
            if (generatedItem) {
                GameUtils.generatedItem();
            }
        //没有死亡，没有爆炸特效。播放音效，并且替换坦克颜色
        } else {
            FXGL.play("shoot_stone.wav");
            changeTankView(tank, hp);
        }
    }

    /**
     * 坦克被攻击并且没有死亡时，修改坦克的视图
     * @param tank
     * @param hp
     */
    public void changeTankView(Entity tank, int hp){
        ViewComponent viewComponent = tank.getViewComponent();
        viewComponent.clearChildren();
        viewComponent.addChild(entity.<List<Texture>>getObject("textureList").get(hp-1));
    }

}
