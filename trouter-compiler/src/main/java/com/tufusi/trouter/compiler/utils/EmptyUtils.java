package com.tufusi.trouter.compiler.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by LeoCheung on 2020/12/11.
 *
 * @description 字符串、集合判空处理工具类
 */
public class EmptyUtils {

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
} 