package com.lexinsmart.xushun.indoorlocation.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.palmaplus.nagrand.view.overlay.OverlayCell;

/**
 * Created by androidlzj on 2015/6/24.
 */
public class LogoMark2 extends ImageView implements OverlayCell {
    private double[] geoCoordinate;

    /**
     * 此Mark所属楼层的id.
     */
    public long mFloorId;

    public LogoMark2(Context context) {
        super(context);
    }

    public LogoMark2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LogoMark2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void init(double[] doubles) {
        geoCoordinate = doubles;
    }

    @Override
    public double[] getGeoCoordinate() {
        return geoCoordinate;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void position(double[] doubles) {

        setX((float) doubles[0] - getWidth() / 2);
        setY((float) doubles[1]);
    }

    @Override
    public long getFloorId() {
        return mFloorId;
    }

    /**
     * 设置楼层id.
     */
    public void setFloorId(long floorId) {
        mFloorId = floorId;
    }
}
