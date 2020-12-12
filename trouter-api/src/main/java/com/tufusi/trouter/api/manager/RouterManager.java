package com.tufusi.trouter.api.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.tufusi.trouter.annotation.mode.RouterBean;
import com.tufusi.trouter.api.core.IProvider;
import com.tufusi.trouter.api.core.TRouterGroupLoad;
import com.tufusi.trouter.api.core.TRouterPathLoad;

import java.util.Map;

/**
 * Created by LeoCheung on 2020/12/12.
 *
 * @description
 */
public class RouterManager {

    // APT生成的路由组Group源文件前缀名
    private static final String GROUP_FILE_PREFIX_NAME = ".TRouter$$Group$$";
    private static volatile RouterManager singleton = null;
    // 路由组名
    private String group;
    // 路由Path路径
    private String path;
    // 存储实现 TRouterGroupLoad 接口的实现类
    private LruCache<String, TRouterGroupLoad> groupLruCache;
    // 存储实现 TRouterPathLoad 接口的实现类
    private LruCache<String, TRouterPathLoad> pathLruCache;

    private RouterManager() {
        // 最多100个组别
        groupLruCache = new LruCache<>(100);
        // 每组最多200条路径值
        pathLruCache = new LruCache<>(200);
    }

    public static RouterManager getInstance() {
        if (singleton == null) {
            synchronized (RouterManager.class) {
                if (singleton == null) {
                    singleton = new RouterManager();
                }
            }
        }
        return singleton;
    }

    /**
     * 路由跳转
     *
     * @param manager Bundle参数管理类
     * @param context 上下文环境
     * @param code    请求码 or 结果码
     * @return 除了activity跳转，也可以返回跨模块 实现 IProvider接口的类回调
     */
    Object navigation(BundleManager manager, Context context, int code) {
        // 全类名名称
        String groupClassName = context.getPackageName() + ".apt" + GROUP_FILE_PREFIX_NAME + group;
        Log.e("TRouter >>> ", "groupClassName -> " + groupClassName);

        try {
            // 首先在缓存中获取组名对应的实现接口类
            TRouterGroupLoad groupLoad = groupLruCache.get(group);
            if (groupLoad == null) {
                Class<?> clazz = Class.forName(groupClassName);
                groupLoad = (TRouterGroupLoad) clazz.newInstance();
                groupLruCache.put(group, groupLoad);
            }

            if (groupLoad.loadGroup().isEmpty()) {
                throw new RuntimeException("路由组加载失败");
            }

            TRouterPathLoad pathLoad = pathLruCache.get(path);
            if (pathLoad == null) {
                Class<? extends TRouterPathLoad> clazz = groupLoad.loadGroup().get(group);
                if (clazz != null) {
                    pathLoad = clazz.newInstance();
                }
                if (pathLoad != null) {
                    pathLruCache.put(path, pathLoad);
                }
            }

            if (pathLoad != null) {
                Map<String, RouterBean> pathMap = pathLoad.loadPath();
                if (pathMap.isEmpty()) {
                    throw new RuntimeException("路由路径加载失败");
                }
                RouterBean routerBean = pathMap.get(path);
                if (routerBean != null) {
                    switch (routerBean.getType()) {
                        // 如果是Activity类则处理跳转
                        case ACTIVITY:
                            Intent intent = new Intent(context, routerBean.getClazz());
                            intent.putExtras(manager.getBundle());

                            // 跳转是否携带请求码，或者结果码
                            if (manager.isActivityResult()) {
                                ((Activity) context).setResult(code, intent);
                                ((Activity) context).finish();
                            }

                            if (code > 0) {
                                ((Activity) context).startActivityForResult(intent, code, manager.getBundle());
                            } else {
                                ((Activity) context).startActivity(intent, manager.getBundle());
                            }
                            break;
                        // 如果实现了IProvider接口
                        case PROVIDER:
                            Class<?> clazz = routerBean.getClazz();
                            IProvider provider = (IProvider) clazz.newInstance();
                            manager.setProvider(provider);
                            return manager.getProvider();
                        default:
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("未按规范配置，如：/app/MainActivity");
        }

        group = interceptGroupName(path);
        this.path = path;

        return new BundleManager();
    }

    /**
     * 从路径中找出group，截取出组名
     *
     * @param path 配置路径地址
     * @return 截取组名
     */
    private String interceptGroupName(String path) {
        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("@TRouter注解未按规范配置，如：/app/MainActivity");
        }
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("@TRouter注解未按规范配置，如：/app/MainActivity");
        }
        return finalGroup;
    }
}