package com.tufusi.trouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LeoCheung on 2020/12/11.
 *
 * @description
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface Parameter {

    /**
     * 注解参数，如果传递name，那么getIntent()键值用name替代，否则就使用该属性名称
     */
    String name() default "";

}
