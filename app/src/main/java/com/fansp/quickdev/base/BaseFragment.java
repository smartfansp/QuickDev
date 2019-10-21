package com.fansp.quickdev.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fansp.quickdev.util.SPUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Objects;

import butterknife.ButterKnife;

public abstract class BaseFragment<V, T extends BasePresenter<V>> extends Fragment {

    private T mPresenter;
    private boolean isInit = false;
    private boolean isLoad = false;
    public String TAG;
    public SPUtils spUtils;
    public Context mContext;
    public RxPermissions mRxPermisson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        TAG=AppConst.TAG;
        //判断是否使用MVP模式
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView((V) this);//因为之后所有的子类都要实现对应的View接口
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //子类不再需要设置布局ID，也不再需要使用ButterKnife.bind()
        View rootView = inflater.inflate(provideContentViewId(), container, false);
        ButterKnife.bind(this, rootView);
        spUtils = SPUtils.getInstance(getActivity());
        mRxPermisson = new RxPermissions(this);
        initView(rootView);
        initData();
        initListener();
//        isInit = true;
//        isCanLoadData();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        initData();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
//    /**
//     * 视图是否已经对用户可见，系统的方法
//     */
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        isCanLoadData();
//    }
    private ProgressDialog mProgressDialog;//网络加载对话框
    public  void showProgressDialog(Context context, String msg) {
        if (context != null) {
            Activity a = (Activity) context;
            if (!a.isFinishing()) {
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(context);
                }
                mProgressDialog.setCanceledOnTouchOutside(false);//设置对话框点击域外不消失
                if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                    mProgressDialog.setMessage(msg);
                    mProgressDialog.show();
                }
            }
        }
    }
    /**
     * 显示加载数据的dialog
     */
    public void showProgressDialog(Context context) {
        if (context != null) {
            Activity a = (Activity) context;
            if (!a.isFinishing()) {
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(context);
                }
                mProgressDialog.setCanceledOnTouchOutside(false);//设置对话框点击域外不消失
//        mProgressDialog.setCancelable(false);//设置进度条是否可以按退回键取消
                if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                    mProgressDialog.setMessage("数据加载中....");
                    mProgressDialog.show();
                }
            }
        }

    }
    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
//    private void isCanLoadData() {
//        if (!isInit) {
//            return;
//        }
//
//        if (getUserVisibleHint()) {
//            if (!isLoad){
//                initData();
//                initListener();
//                isLoad = true;
//            }
//        } else {
//            if (isLoad) {
//                stopLoad();
//            }
//        }
//    }
//    protected void stopLoad() {
//    }
    /**
     * 视图销毁的时候讲Fragment是否初始化的状态变为false
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
        unbindDrawables(Objects.requireNonNull(getView()));
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    public void init() {

    }

    public void initView(View rootView) {
    }

    public void initData() {

    }
    public void jumpToActivity(Class activity) {
        Intent intent = new Intent(getActivity(), activity);
        startActivity(intent);
    }
    public void ToActivityWithExtra(Class activity, String str, String s) {
        Intent intent = new Intent(getActivity(), activity);
        intent.putExtra(str, s);
        startActivity(intent);
    }

    public void initListener() {

    }

    //用于创建Presenter和判断是否使用MVP模式(由子类实现)
    protected abstract T createPresenter();

    //得到当前界面的布局文件id(由子类实现)
    protected abstract int provideContentViewId();
}
