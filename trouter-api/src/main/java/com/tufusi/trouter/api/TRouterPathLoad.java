package com.tufusi.trouter.api;

import com.tufusi.trouter.annotation.mode.RouterBean;

import java.util.Map;

/**
 * Created by LeoCheung on 2020/12/10.
 *
 * @description 路由组Group对应的 精准Path路径对象的 加载数据接口
 * 比如APP分组下的所有需要加载的类对象
 */
public interface TRouterPathLoad {

    /**
     * 加载路由组Group中的path详细信息
     * 比如："app"分组下的一些信息
     *
     * @return key:"/app/MainActivity" value: MainActivity 信息封装在RouterBean中
     */
    Map<String, RouterBean> loadPath();

}

