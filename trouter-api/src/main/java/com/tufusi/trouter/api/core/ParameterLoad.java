package com.tufusi.trouter.api.core;

/**
 * Created by LeoCheung on 2020/12/11.
 *
 * @description
 */
public interface ParameterLoad {

    /**
     * 在目标类中实现之后，插入Activity的生命周期创建期，通过调用赋值传递参数
     *
     * @param target 目标跳转类
     */
    void loadParameter(Object target);

}

