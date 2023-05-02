package com.cry.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;

/**
 * 基地组件，提供被击中时更换图片的方法
 */
public class FlagOnHitComponent extends Component {
    public void onHit(){
        ViewComponent viewComponent = entity.getViewComponent();
        viewComponent.clearChildren();
        viewComponent.addChild(FXGL.texture("map/flag_failed.png"));
    }
}
