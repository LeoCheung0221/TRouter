package com.tufusi.trouter.baselib;

import com.tufusi.trouter.baselib.bean.PathBean;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LeoCheung on 2020/12/7.
 *
 * @description 全局路径记录器（依据子模块分组）
 */
public class RecordPathManager {

    /**
     * 对应模块下的所有路径对象集合
     * key: “order” 组
     * value：order 子模块下所有的Activity路径信息
     */
    private static Map<String, List<PathBean>> groupMap = new HashMap<>();

    /**
     * 将路径信息加入全局Map
     *
     * @param groupName 组名 如 “order”
     * @param pathName  路径名 如 “Order_MainActivity”
     * @param clazz     类对象 如 Order_MainActivity.class
     */
    public static void joinGroup(String groupName, String pathName, Class<?> clazz) {
        List<PathBean> pathList = groupMap.get(groupName);
        if (pathList == null) {
            pathList = new ArrayList<>();
            pathList.add(new PathBean(pathName, clazz));

            groupMap.put(groupName, pathList);
        } else {
            for (PathBean pathBean : pathList) {
                if (!pathName.equals(pathBean.getPath())) {
                    pathList.add(new PathBean(pathName, clazz));
                    groupMap.put(groupName, pathList);
                }
            }
        }
    }

    /**
     * 根据组名和路径名获取指定的类对象，从而达到跳转目的
     *
     * @param groupName 组名
     * @param pathName  路径名
     * @return 目标类对象
     */
    public static Class<?> getTargetClass(String groupName, String pathName) {
        List<PathBean> pathList = groupMap.get(groupName);
        if (pathList == null) {
            return null;
        }

        for (PathBean pathBean : pathList) {
            if (pathName.equalsIgnoreCase(pathBean.getPath())) {
                return pathBean.getClazz();
            }
        }

        return null;
    }
} 