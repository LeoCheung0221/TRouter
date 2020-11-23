package com.tufusi.trouter.baselib.bean;

/**
 * Created by LeoCheung on 2020/11/19.
 *
 * @description 路径对象（公共基础库中所有子模块都可以调用）
 * such as:
 * path : ”order/Order_MainActivity”
 * clazz : Order_MainActivity.class
 */
public class PathBean {

    private String path;
    private Class<?> clazz;

    public PathBean(String path, Class<?> clazz) {
        this.path = path;
        this.clazz = clazz;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}