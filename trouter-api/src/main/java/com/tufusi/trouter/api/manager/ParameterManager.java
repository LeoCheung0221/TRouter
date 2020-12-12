package com.tufusi.trouter.api.manager;

import android.app.Activity;
import android.util.LruCache;

import androidx.annotation.NonNull;

import com.tufusi.trouter.api.core.ParameterLoad;

/**
 * Created by LeoCheung on 2020/12/12.
 *
 * @description
 */
public class ParameterManager {

    private static volatile ParameterManager singleton = null;
    private LruCache<String, ParameterLoad> cache;
    private static final String FILE_SUFFIX_NAME = "$$Parameter";

    private ParameterManager() {
        cache = new LruCache<>(200);
    }

    public static ParameterManager getInstance() {
        if (singleton == null) {
            synchronized (ParameterManager.class) {
                if (singleton == null) {
                    singleton = new ParameterManager();
                }
            }
        }
        return singleton;
    }

    public void loadParameter(@NonNull Activity activity) {
        String activityClassName = activity.getClass().getName();
        ParameterLoad iParameterLoad = cache.get(activityClassName);
        try {
            // 缓存没命中，则添加到LruCache
            if (iParameterLoad == null) {
                Class<?> clazz = Class.forName(activityClassName + FILE_SUFFIX_NAME);
                iParameterLoad = (ParameterLoad) clazz.newInstance();

                cache.put(activityClassName, iParameterLoad);
            }
            iParameterLoad.loadParameter(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

} 