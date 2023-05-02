package com.cry.components;

import com.almasb.fxgl.entity.component.Component;

/**
 * 坦克的等级
 * 1级坦克可以打掉土墙，无法打掉石头，只能同时发射一颗子弹
 * 2级坦克可以打掉土墙，无法打掉石头，并且可以同时发射两发子弹
 * 3级坦克可以打掉土墙，可以打掉石头，并且可以同时发射两发子弹
 */
public class TankLevelComponent extends Component {
    private int level;

    public TankLevelComponent(){
        this(1);
    }

    public TankLevelComponent(int level){
        this.level = level;
    }

    public void upgrade(){
        level++;
    }

    public void upgradeMax(){
        level = 3;
    }

    public int getLevel() {
        return level;
    }
}
