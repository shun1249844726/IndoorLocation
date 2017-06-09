package com.lexinsmart.xushun.indoorlocation;

/**
 * Created by xushun on 2017/6/7.
 */

import com.palmaplus.nagrand.data.Param;

/**
 * Created by lchad on 2016/11/1.
 * Github: https://github.com/lchad
 */
public class Constant {
    /**
     * lua地址
     */
    public static final String LUR_NAME = "Nagrand/lua";
    /**
     * 正大广场ID
     */
    public static final int SINGLE_BUILDING_ID = 606;
    /**
     * 上海证券大厦ID
     */
    public static final int MULTI_BUILDING_ID = 1473;
    /**
     * 地图数据服务器地址
     */
    public static final String SERVER_URL = "https://api.ipalmap.com/";
    /**
     * 楼层下拉菜单中显示字段
     */
    public static final Param<String> FLOOR_SHOW_FIELD = new Param<>("address", String.class);
    /**
     * AppKey，可以从图聚的开发者平台上获取
     */
    public static final String APP_KEY = "acd35e27f3c143baa97c9d8f8391ef5b";

}
