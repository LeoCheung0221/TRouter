package com.tufusi.trouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LeoCheung on 2020/12/8.
 *
 * @description 定义路由注解
 * <ul>
 * <li>@Target(ElementType.TYPE)   // 接口、类、枚举、注解</li>
 * <li>@Target(ElementType.FIELD) // 属性、枚举的常量</li>
 * <li>@Target(ElementType.METHOD) // 方法</li>
 * <li>@Target(ElementType.PARAMETER) // 方法参数</li>
 * <li>@Target(ElementType.CONSTRUCTOR)  // 构造函数</li>
 * <li>@Target(ElementType.LOCAL_VARIABLE)// 局部变量</li>
 * <li>@Target(ElementType.ANNOTATION_TYPE)// 该注解使用在另一个注解上</li>
 * <li>@Target(ElementType.PACKAGE) // 包</li>
 * <li>@Retention(RetentionPolicy.RUNTIME) <br>注解会在class字节码文件中存在，jvm加载时可以通过反射获取到该注解的内容</li>
 * </ul>
 * <p>
 * 生命周期：SOURCE < CLASS < RUNTIME
 * 1、一般如果需要在运行时去动态获取注解信息，用RUNTIME注解
 * 2、要在编译时进行一些预处理操作，如ButterKnife，用CLASS注解。注解会在class文件中存在，但是在运行时会被丢弃
 * 3、做一些检查性的操作，如@Override，用SOURCE源码注解。注解仅存在源码级别，在编译的时候丢弃该注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface TRouter {

    /**
     * 路由路径， 如："/app/MainActivity"
     */
    String path();

    /**
     * 路由组名 （选填，如果开发者不填写，可以从path中截取出来）
     */
    String group() default "";

    /**
     * app跳转协议码，为后台或前端调用本地页面跳转的协议如 “app://10000" 跳转到登录页
     */
    int intentCode() default -1;

    /**
     * 拦截器，用于跳转到当前目的页面前先判断某些条件是否成立，若不成立则拦截跳转到其他页面，
     * 如，某些页面需要登录才能跳转到该页面则添加一个登录的拦截器，如果没有登录则跳转到登录页，反之，则跳转到目的页面
     */
    Class<?>[] interceptors() default {};

}