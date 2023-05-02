package com.cry.enums;

import javafx.geometry.Point2D;

/**
 * @author fys
 * 方向枚举
 */
public enum DirectionType {
    /**
     * 向上
     */
    UP(new Point2D(0, -1)),
    /**
     * 向下
     */
    DOWN(new Point2D(0, 1)),
    /**
     * 向左
     */
    LEFT(new Point2D(-1, 0)),
    /**
     * 向右
     */
    RIGHT(new Point2D(1, 0));

    public final Point2D point2D;

    DirectionType(Point2D point){
        this.point2D = point;
    }

    public Point2D getPoint2D(){
        return this.point2D;
    }
}
