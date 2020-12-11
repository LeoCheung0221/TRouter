package com.tufusi.trouter.compiler.utils;

/**
 * Created by LeoCheung on 2020/12/10.
 *
 * @description 常量类
 */
public class Constants {

    /**
     * 注解处理器汇总支持的注解类型
     */
    public static final String TROUTER_ANNOTATION_TYPES = "com.tufusi.trouter.annotation.TRouter";

    /**
     * 每个模块的模块名 跟gradle里面的对应，为了可以传参到注解处理器接收
     */
    public static final String MODULE_NAME = "moduleName";

    /**
     * 用于存放APT生成的类文件目录参数
     */
    public static final String APT_PACKAGE_NAME = "packageNameForAPT";

    /**
     * Activity全类名
     */
    public static final String ACTIVITY = "android.app.Activity";

    /**
     * TRouter对外提供的接口模块包名
     */
    public static final String TROUTER_API_PKG = "com.tufusi.trouter.api";

    /**
     * TRouter-api中 core文件夹下的对外提供实现接口的组名加载接口和路径加载接口
     */
    public static final String TROUTER_GROUP = TROUTER_API_PKG + ".core.TRouterGroupLoad";
    public static final String TROUTER_PATH = TROUTER_API_PKG + ".core.TRouterPathLoad";

    /**
     * JavaPoet 生成文件相关常量类提取
     */
    public static final String METHOD_PATH_LOAD_NAME = "loadPath";
    public static final String METHOD_GROUP_LOAD_NAME = "loadGroup";
    public static final String PARAMETER_PATH_LOAD_NAME = "pathMap";
    public static final String PARAMETER_GROUP_LOAD_NAME = "groupMap";

    /**
     * 路径对象生成文件的文件名前缀
     */
    public static final String PATH_FILE_NAME = "TRouter$$Path$$";
    public static final String GROUP_FILE_NAME = "TRouter$$Group$$";
}