package com.lexinsmart.xushun.indoorlocation.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lexinsmart.xushun.indoorlocation.R;
import com.palmaplus.nagrand.data.DataSource;
import com.palmaplus.nagrand.view.MapOptions;

import butterknife.ButterKnife;

/**
 * Created by lchad on 2016/11/1.
 * Github: https://github.com/lchad
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Handler mHandler;

    private ProgressDialog mProgressDialog;

    protected DataSource mDataSource;

    protected MapOptions mMapOptions;

    /**
     * Get the layout Id.
     */
    protected abstract int getLayoutId();

    /**
     * Init the data.
     */
    protected abstract void initData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        mHandler = new Handler();
        /**
         * 修改它，可以改变mapView交互的一些属性
         */
        mMapOptions = new MapOptions();
        setView();
        initData();
    }

    /**
     * Init the listener.
     */
    protected abstract void setView();

    /**
     * 设置页面标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    /**
     * 设置页面标题
     *
     * @param id 标题字符串id
     */
    public void setTitle(int id) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(id));
        }
    }

    public void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    public void hideLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showProgressDialog() {
        showProgressDialog(getString(R.string.loading));
    }

    protected void showProgressDialog(String info) {
        showProgressDialog(info, true);
    }

    protected void showProgressDialog(int id) {
        showProgressDialog(getResources().getString(id), true);
    }

    protected void showProgressDialog(String info, boolean cancelable) {
        showProgressDialog("", info, cancelable);
    }

    protected void showProgressDialog(String title, String info,
                                      boolean cancelable) {
        if (!isFinishing()) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(this, title, info, true,
                        cancelable, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                mProgressDialog.dismiss();
                            }
                        });
            } else {
                if (!mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                }
            }

        }
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDataSource != null) {
            mDataSource.drop();
        }
    }

}
