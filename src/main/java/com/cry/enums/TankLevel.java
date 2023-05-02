package com.cry.enums;

public enum TankLevel {
    /**
     * 等级1
     */
    LEVEL1(1),
    /**
     * 等级2
     */
    LEVEL2(2),
    /**
     * 等级3
     */
    LEVEL3(3);

    private final int level;

    TankLevel(int level){
        this.level = level;
    }

    public int getLevel(){
        return level;
    }
}
