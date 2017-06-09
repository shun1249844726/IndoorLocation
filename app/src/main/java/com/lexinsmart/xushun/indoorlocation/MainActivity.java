package com.lexinsmart.xushun.indoorlocation;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.lexinsmart.xushun.indoorlocation.base.BaseActivity;
import com.lexinsmart.xushun.indoorlocation.utils.DisplayUtils;
import com.lexinsmart.xushun.indoorlocation.utils.FileUtils;
import com.lexinsmart.xushun.indoorlocation.widget.Mark;
import com.palmap.widget.Compass;
import com.palmap.widget.Scale;
import com.palmaplus.nagrand.core.Engine;
import com.palmaplus.nagrand.core.Types;
import com.palmaplus.nagrand.data.DataSource;
import com.palmaplus.nagrand.data.Feature;
import com.palmaplus.nagrand.data.FloorModel;
import com.palmaplus.nagrand.data.LocationList;
import com.palmaplus.nagrand.data.LocationModel;
import com.palmaplus.nagrand.data.MapModel;
import com.palmaplus.nagrand.data.PlanarGraph;
import com.palmaplus.nagrand.geos.Coordinate;
import com.palmaplus.nagrand.navigate.NavigateManager;
import com.palmaplus.nagrand.position.Location;
import com.palmaplus.nagrand.position.PositioningManager;
import com.palmaplus.nagrand.position.ble.BeaconPositioningManager;
import com.palmaplus.nagrand.position.util.PositioningUtil;
import com.palmaplus.nagrand.view.MapOptions;
import com.palmaplus.nagrand.view.MapView;
import com.palmaplus.nagrand.view.adapter.DataListAdapter;
import com.palmaplus.nagrand.view.gestures.OnSingleTapListener;
import com.palmaplus.nagrand.view.gestures.OnZoomListener;
import com.palmaplus.nagrand.view.layer.FeatureLayer;
import com.palmaplus.nagrand.view.overlay.OverlayCell;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity {

    /**
     * 申请读写文件权限返回标志字段
     */
    private static final int REQUEST_WRITE_STORAGE = 112;

    public static final int PAGE_TYPE = PageType.NORMAL_SINGLE_BUILDING;
    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.spinner)
    Spinner mSpinner;
    @BindView(R.id.map_overlay_container)
    RelativeLayout mMapOverlayContainer;
    @BindView(R.id.search)
    EditText mSearch;
    @BindView(R.id.submit)
    Button mSubmit;
    @BindView(R.id.search_container)
    LinearLayout mSearchContainer;
    @BindView(R.id.container)
    LinearLayout mContainer;
    @BindView(R.id.info_text)
    TextView mInfoText;
    @BindView(R.id.zoom)
    SwitchButton mZoom;
    @BindView(R.id.move)
    SwitchButton mMove;
    @BindView(R.id.skew)
    SwitchButton mSkew;
    @BindView(R.id.rotate)
    SwitchButton mRotate;
    @BindView(R.id.click)
    SwitchButton mClick;
    @BindView(R.id.switch_container)
    LinearLayout mSwitchContainer;
    @BindView(R.id.architect)
    Scale mArchitect;
    @BindView(R.id.screen_coo)
    TextView mCurrentCoo;
    @BindView(R.id.al_la_coo)
    TextView mCenterCoordinate;
    @BindView(R.id.plan_coo)
    TextView mZoomLevel;
    @BindView(R.id.top_text_container)
    LinearLayout mTopTextContainer;
    @BindView(R.id.top_text)
    TextView mTopText;
    @BindView(R.id.compass)
    Compass mCompass;
    @BindView(R.id.wifi)
    ImageView mWifi;
    @BindView(R.id.bluetooth)
    ImageView mBluetooth;
    @BindView(R.id.locate_container)
    LinearLayout mLocateContainer;
    @BindView(R.id.config_origin)
    RadioButton mConfigOrigin;
    @BindView(R.id.config_new)
    RadioButton mConfigNew;
    @BindView(R.id.map_function_radio_group)
    RadioGroup mMapFunctionRadioGroup;
    @BindView(R.id.linestring)
    Button mLinestring;
    @BindView(R.id.rect)
    Button mRect;
    @BindView(R.id.geo_container)
    LinearLayout mGeoContainer;


    /**
     * 页面类型,对应每一种功能
     */
    private int mPageType;

    /**
     * 地图中心点
     */
    protected Types.Point mMapCenter;
    protected Types.Point mNorthPoint;

    /**
     * 途经点
     */
    long[] transientId = new long[]{186904};
    /**
     * 定位图层
     */
    FeatureLayer mPositioningLayer;
    /**
     * wifi定位接口
     */
    PositioningManager mWifiPositioningManager;
    /**
     * 蓝牙定位接口
     */
    PositioningManager mBlePositioningManager;
    /**
     * 存储地图标记的列表
     */
    private List<OverlayCell> mMarkList;
    private int mNum = 0;
    /**
     * 导航接口
     */
    private NavigateManager mNavigateManager;
    /**
     * 导航图层
     */
    private FeatureLayer mNavFeatureLayer;
    /**
     * 导航需要的坐标点
     */
    private double startX = 13525325.814450;
    private double startY = 3663568.547362;
    private long startId = 185817L;
    private double toX = 13525157.350047;
    private double toY = 3663465.373461;
    private long toId = 186094L;
    /**
     * 当前楼层id.
     */
    private long mCurrentFloorId;
    /**
     * 决定什么时候显示“没有导航数据”提示语
     */
    private boolean mIsNavigating;

    private Feature mPOICenterFeature = null;
    /**
     * 手机屏幕宽度.
     */
    private int mScreenWidth;
    /**
     * 手机屏幕高度.
     */
    private int mScreenHeight;
    /**
     * 手机状态栏高度
     */
    private int mStatusBarHeight;

    /**
     * 折线图层
     */
    private FeatureLayer linestringLayer;
    /**
     * 多边形图层
     */
    private FeatureLayer polygonLayer;

    protected Handler mHandler;

    protected DataSource mDataSource;
    private static final int REQUEST_BLUETOOTH_PERMISSION=10;

    private void requestBluetoothPermission(){
        //判断系统版本
        if (Build.VERSION.SDK_INT >= 23) {
            //检测当前app是否拥有某个权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            //判断这个权限是否已经授权过
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                //判断是否需要 向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this,"Need bluetooth permission.",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_BLUETOOTH_PERMISSION);
                return;
            }else{
            }
        } else {
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_single_building;
    }

    @Override
    protected void initData() {
        //是否拥有读写文件的权限,Android6.0及以上需开发者格外注意权限问题.
        boolean hasPermission = (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            //申请权限
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
            return;
        } else {
            copyLuaToStorage();
        }

        requestBluetoothPermission();



        mDataSource = new DataSource(Constant.SERVER_URL);

        mDataSource.requestMap(Constant.SINGLE_BUILDING_ID, new DataSource.OnRequestDataEventListener<MapModel>() {
            @Override
            public void onRequestDataEvent(DataSource.ResourceState resourceState, MapModel mapModel) {
                if (resourceState != DataSource.ResourceState.ok) {
                    Toast.makeText(MainActivity.this, R.string.map_load_fail, Toast.LENGTH_SHORT).show();
                    return;
                }
                mDataSource.requestPOI(MapModel.POI.get(mapModel), new DataSource.OnRequestDataEventListener<LocationModel>() {
                    @Override
                    public void onRequestDataEvent(DataSource.ResourceState resourceState, final LocationModel locationModel) {
                        if (resourceState != DataSource.ResourceState.ok) {
                            Toast.makeText(MainActivity.this, R.string.map_load_fail, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        switch (LocationModel.type.get(locationModel)) {
                            case LocationModel.PLANARGRAPH://平面图
                            case LocationModel.FLOOR://楼层
                                mSpinner.setVisibility(View.GONE);
                                mCurrentFloorId = LocationModel.id.get
                                        (locationModel);
                                mDataSource.requestPlanarGraph(LocationModel.id.get
                                        (locationModel), new DataSource.OnRequestDataEventListener<PlanarGraph>() {
                                    @Override
                                    public void onRequestDataEvent(DataSource.ResourceState resourceState, PlanarGraph planarGraph) {
                                        mMapView.drawPlanarGraph(planarGraph);

                                        //                        afterDrawPlanarGraph();

                                        mCenterCoordinate.setText(getString(R.string.center_point) + "(" + mMapCenter.x + ", " + mMapCenter.y + ")");
                                        mZoomLevel.setText(getString(R.string.zoom_level));
                                        mZoomLevel.append("" + (int) mMapView.getZoomLevel());
                                    }
                                });
                                break;
                            case LocationModel.BUILDING://建筑物
                                mDataSource.requestPOIChildren(LocationModel.id.get
                                        (locationModel), new DataSource.OnRequestDataEventListener<LocationList>() {
                                    @Override
                                    public void onRequestDataEvent(DataSource.ResourceState resourceState, LocationList locationList) {
                                        if (resourceState != DataSource.ResourceState.ok) {
                                            Toast.makeText(MainActivity.this, R.string.map_load_fail, Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        //楼层切换控件adapter
                                        DataListAdapter<LocationModel> floorAdapter = new DataListAdapter<>(
                                                MainActivity.this,
                                                android.R.layout.simple_spinner_item, locationList,
                                                Constant.FLOOR_SHOW_FIELD);

                                        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        mSpinner.setAdapter(floorAdapter);

                                        //设置默认楼层
                                        for (int i = 0; i < floorAdapter.getCount(); i++) {
                                            LocationModel model = floorAdapter.getItem(i);
                                            if (model != null) {
                                                if (FloorModel.default_.get(model)) {
                                                    mSpinner.setSelection(i);
                                                    break;
                                                }
                                            }
                                        }

                                    }
                                });
                                break;
                            default:
                                break;
                        }

                    }
                });

            }
        });
        mBlePositioningManager = new BeaconPositioningManager(this, Constant.APP_KEY);

        // 定位监听的事件，如果得到了新的位置数据，就会调用这个方法
        mBlePositioningManager.setOnLocationChangeListener(
                new PositioningManager.OnLocationChangeListener<Location>() {
                    @Override
                    public void onLocationChange(PositioningManager.LocationStatus status, final Location oldLocation,
                                                 final Location newLocation) {  // 分别代表着上一个位置点和新位置点
                        Log.d("TAG", "onLocationChange");

                        switch (status) {
                            case MOVE:
                                Coordinate coordinate = newLocation.getPoint().getCoordinate();

           //                     PositioningUtil.positionLocation(1L, mPositioningLayer, newLocation); //
                                // 当第二次返回点位点时，我们就可以让这个定位点开始移动了

                                Log.d("onLocationChange", "x = " + coordinate.x+ ", y =" +
                                        " " + coordinate.y);


                                mMapView.removeAllOverlay();
                                //创建一个覆盖物
                                Mark mark = new Mark(mMapView.getContext());
                                mark.setMark(++mNum, coordinate.getX(),coordinate.getY());
                                //把世界坐标传递给它
                                mark.init(new double[]{coordinate.x,coordinate.y});
                                mark.setFloorId(mCurrentFloorId);

                                //将这个覆盖物添加到MapView中
                                mMapView.addOverlay(mark);



                                break;
                            default:
                                break;
                        }

                    }
                });
        mBlePositioningManager.start(); // 开始定位

        mMarkList = new ArrayList<>();

        //地图单击事件
        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(MapView mapView, float x, float y) {
                Types.Point point = mMapView.converToWorldCoordinate(x, y);

                Mark mark = new Mark(mMapView.getContext());
                mark.setMark(++mNum, x, y);
                mark.init(new double[]{point.x, point.y});
                mark.setFloorId(mCurrentFloorId);
                mapView.addOverlay(mark);
                mMarkList.add(mark);

                System.out.println("x:+"+point.x +"\t"+point.y);
            }
        });
    }

    @Override
    protected void setView() {
        showProgressDialog();
        initDimens();
        mMapView.start();

        if (mPageType == PageType.NORMAL_SINGLE_BUILDING) {
            setTitle(R.string.single_building);
            mTopTextContainer.setVisibility(View.GONE);
        }
        mArchitect.setMapView(mMapView);
        mCompass.setMapView(mMapView);
        mMapView.setMapOptions(mMapOptions);
        mMapView.setOverlayContainer(mMapOverlayContainer);

        //切换楼层事件
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final LocationModel item = (LocationModel) parent.getAdapter().getItem(position);
                //根据floorId，加载地图数据
                showProgressDialog(R.string.map_loading);
                mCurrentFloorId = LocationModel.id.get(item);
                mDataSource.requestPlanarGraph(LocationModel.id.get(item),
                        new DataSource.OnRequestDataEventListener<PlanarGraph>() {
                            @Override
                            public void onRequestDataEvent(DataSource.ResourceState state, PlanarGraph planarGraph) {
                                hideProgressDialog();
                                if (state != DataSource.ResourceState.ok) {
                                    Toast.makeText(MainActivity.this, R.string.map_load_fail, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                mMapView.drawPlanarGraph(planarGraph);
                                //                     afterDrawPlanarGraph();
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mMapView.setOnZoomListener(new OnZoomListener() {
            @Override
            public void preZoom(MapView mapView, float x, float y) {

            }

            @Override
            public void onZoom(MapView mapView, boolean b) {

            }

            @Override
            public void postZoom(MapView mapView, float x, float y) {
                mZoomLevel.setText(getString(R.string.zoom_level));
                mZoomLevel.append("" + (int) mMapView.getZoomLevel());
                mCompass.invalidate();
                mArchitect.invalidate();

                Types.Point pointLeftTop = mMapView.converToWorldCoordinate(0, 0);
                Types.Point pointRightBottom = mMapView.converToWorldCoordinate(mScreenWidth, mScreenHeight);
                Types.Point pointCenter = mMapView.converToWorldCoordinate(mScreenWidth / 2, mScreenHeight / 2);

                mCurrentCoo.setText(getString(R.string.current_display_area) + "\n" + "(" + pointLeftTop.x + "," + pointLeftTop.y + ")" + "\n" + "(" + pointRightBottom.x +
                        "," + pointRightBottom.y +
                        ")");
                mCenterCoordinate.setText(getString(R.string.current_display_area) + "\n" + "(" + pointCenter.x + "," + pointCenter.y + ")");

            }
        });

    }


    private void initDimens() {

        mPageType = PAGE_TYPE;
        mScreenWidth = DisplayUtils
                .getScreenWidthPixel(MainActivity.this);
        mScreenHeight = DisplayUtils
                .getScreenHeightPixel(MainActivity.this);
        mStatusBarHeight = DisplayUtils.getStatusBarHeight(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDataSource != null) {
            mDataSource.drop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(MainActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                        Log.d("permission: ", getResources().getString(R.string.permission_denied));
                    }
                } else {
                    copyLuaToStorage();
                    Engine engine = Engine.getInstance(); //初始化引擎
                    engine.startWithLicense(Constant.APP_KEY, this);//设置验证license，可以通过开发者平台去查找自己的license
                    initData();
                }
            }
            default:
                break;
        }
    }

    /**
     * 把Asset目录下的lua配置文件复制到sd卡内
     */
    public void copyLuaToStorage() {
        if (FileUtils.copyLuaFinished()) {
            return;
        }
        if (FileUtils.checkoutSDCard()) {
            Log.d("lua:", "开始复制!");
            FileUtils.copyDirToSDCardFromAsserts(this, Constant.LUR_NAME, "font");
            FileUtils.copyDirToSDCardFromAsserts(this, Constant.LUR_NAME, Constant.LUR_NAME);
            Log.d("lua:", "复制完成!");
        } else {
            Toast.makeText(MainActivity.this, R.string.do_not_find_sdcard, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 页面类型,对应HomeActivity列表中的每一种功能.
     */
    @IntDef({PageType.NORMAL_SINGLE_BUILDING, PageType.SET_INITIAL_VISIBLE_AREA, PageType
            .SINGLE_CLICK, PageType.GESTURE, PageType.MARKER, PageType.GEOMETRY, PageType
            .SEARCH_POI, PageType.WIFI_LOCATE, PageType
            .BLUE_TOOTH_LOCATE, PageType.NAVIGATION_POINT, PageType.CUSTOM_STYLE})
    @Retention(RetentionPolicy.SOURCE)
    @interface PageType {
        /**
         * 单建筑物
         */
        int NORMAL_SINGLE_BUILDING = 0;
        /**
         * 设置初始显示区域
         */
        int SET_INITIAL_VISIBLE_AREA = 1;
        /**
         * 单击事件
         */
        int SINGLE_CLICK = 2;
        /**
         * 手势事件
         */
        int GESTURE = 3;
        /**
         * 标记
         */
        int MARKER = 4;
        /**
         * 绘制几何图形
         */
        int GEOMETRY = 5;
        /**
         * 搜索POI
         */
        int SEARCH_POI = 6;
        /**
         * 定位(WIFI)
         */
        int WIFI_LOCATE = 8;
        /**
         * 定位(蓝牙)
         */
        int BLUE_TOOTH_LOCATE = 13;
        /**
         * 途经点导航
         */
        int NAVIGATION_POINT = 9;
        /**
         * 个性化样式
         */
        int CUSTOM_STYLE = 11;
        /**
         * 路径规划
         */
        int NAVIGATION = 12;
    }
}
