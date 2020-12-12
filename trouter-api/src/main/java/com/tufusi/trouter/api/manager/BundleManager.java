package com.tufusi.trouter.api.manager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.tufusi.trouter.api.core.IProvider;

/**
 * Created by LeoCheung on 2020/12/12.
 *
 * @description 统一参数管理工具类
 */
public class BundleManager {

    private Bundle bundle = new Bundle();
    private boolean isActivityResult;
    private IProvider iProvider;

    // 携带参数类型罗列
    public BundleManager withString(@NonNull String key, @NonNull String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleManager withResultString(@NonNull String key, @NonNull String value) {
        bundle.putString(key, value);
        isActivityResult = true;
        return this;
    }

    public BundleManager withInt(@NonNull String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withResultInt(@NonNull String key, int value) {
        bundle.putInt(key, value);
        isActivityResult = true;
        return this;
    }

    public BundleManager withBoolean(@NonNull String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withResultBoolean(@NonNull String key, boolean value) {
        bundle.putBoolean(key, value);
        isActivityResult = true;
        return this;
    }

    public BundleManager withBundle(@NonNull String key, Bundle value) {
        this.bundle = value;
        return this;
    }

    public BundleManager withResultBundle(@NonNull String key, Bundle value) {
        this.bundle = value;
        isActivityResult = true;
        return this;
    }

    public Object navigation(Context context){
        return RouterManager.getInstance().navigation(this, context, -1);
    }

    public Object navigation(Context context, int code){
        return RouterManager.getInstance().navigation(this, context, code);
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public IProvider getProvider() {
        return iProvider;
    }

    public void setProvider(IProvider iProvider) {
        this.iProvider = iProvider;
    }

    public boolean isActivityResult() {
        return isActivityResult;
    }

    public void setActivityResult(boolean activityResult) {
        isActivityResult = activityResult;
    }
}