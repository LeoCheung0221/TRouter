package com.tufusi.trouter.api.core;

import java.util.Map;

/**
 * Created by LeoCheung on 2020/12/10.
 *
 * @description 路由组Group对外提供加载数据的接口
 */
public interface TRouterGroupLoad {

    /**
     * 加载路由组Group数据
     * 比如： "app"， TRouter$$Path$$app.class（实现了TRouterPathLoad接口）
     *
     * @return key: "app" value: "app"分组对应的路由详细路径类对象
     */
    Map<String, Class<? extends TRouterPathLoad>> loadGroup();

}

