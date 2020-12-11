package com.tufusi.trouter.annotation.mode;

import javax.lang.model.element.Element;

/**
 * Created by LeoCheung on 2020/12/10.
 *
 * @description 路由路径Path的封装对象
 * such as: "/app/MainActivity"
 * MainActivity 均有自己的属性对象
 */
public class RouterBean {

    public enum Type {
        /**
         * 页面标记
         */
        ACTIVITY
    }

    /**
     * 枚举类型 Activity：专门注解Activity，亦可以进行扩展
     */
    private Type type;

    /**
     * 注解使用的类对象：MainActivity.class
     */
    private Class<?> clazz;

    /**
     * 路由组名："app", "order", "person"
     */
    private String group;

    /**
     * 路由路径："/app/MainActivity"
     */
    private String path;

    /**
     * 类节点包裹对象
     */
    private Element element;

    private RouterBean(Builder builder) {
        this.element = builder.element;
        this.group = builder.group;
        this.path = builder.path;
    }

    private RouterBean(Type type, Class<?> clazz, String path, String group) {
        this.type = type;
        this.clazz = clazz;
        this.group = group;
        this.path = path;
    }

    public static final class Builder {

        // 这三个对外提供设置方法
        private String group;
        private String path;
        private Element element;

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setElement(Element element) {
            this.element = element;
            return this;
        }

        // 对外提供构建方法
        public RouterBean build() {
            // 做字段校验
            if (path == null || path.length() == 0) {
                throw new IllegalArgumentException("path must be need!");
            }
            return new RouterBean(this);
        }
    }

    /**
     * 对外提供新的创建对象方法以供创建实例
     */
    public static RouterBean create(Type type, Class<?> clazz, String path, String group) {
        return new RouterBean(type, clazz, path, group);
    }

    public Type getType() {
        return type;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getGroup() {
        return group;
    }

    public String getPath() {
        return path;
    }

    public Element getElement() {
        return element;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "RouterBean{" +
                "group='" + group + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}