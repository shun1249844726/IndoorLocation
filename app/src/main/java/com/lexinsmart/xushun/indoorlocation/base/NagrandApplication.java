package com.lexinsmart.xushun.indoorlocation.base;

import android.app.Application;

import com.lexinsmart.xushun.indoorlocation.Constant;
import com.lexinsmart.xushun.indoorlocation.utils.FileUtils;
import com.palmaplus.nagrand.core.Engine;


/**
 * Created by lchad on 2016/11/1.
 * Github: https://github.com/lchad
 */
public class NagrandApplication extends Application {

    public static NagrandApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (FileUtils.copyLuaFinished()) {
            /**
             * 初始化引擎
             */
            Engine engine = Engine.getInstance();
            /**
             * 设置验证license，可以通过开发者平台去查找自己的license
             */
            engine.startWithLicense(Constant.APP_KEY, this);
        }
    }
}
