package com.cry.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.cry.Main;
import com.cry.effects.HelmetEffect;
import com.cry.enums.GameType;
import com.cry.util.GameUtils;

public class PlayerOnHitComponent extends Component {
    public void onHit(Entity bullet, Entity tank){
        //移除子弹
        bullet.removeFromWorld();
        //有安全帽，直接返回.
        if (tank.getComponent(EffectComponent.class).hasEffect(HelmetEffect.class)) {
            return;
        }
        //移除玩家坦克
        tank.removeFromWorld();
        //爆炸特效
        FXGL.spawn("explode", bullet.getCenter().subtract(50/2.0, 50/2.0));

        if (FXGL.geti("tank") < 1) {
            //玩家死亡，游戏结束
            FXGL.<Main>getAppCast().gameOver();
        } else {
            Entity player = GameUtils.livePlayer();
            Main appCast = FXGL.getAppCast();
            appCast.setPlayerOne(player);
            appCast.setPlayerOneTankEventComponent(player.getComponent(TankEventComponent.class));
        }
    }
}
