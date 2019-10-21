package com.fansp.quickdev.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;


import com.fansp.quickdev.R;

import java.util.List;

public class PopUtils {
    @SuppressLint("StaticFieldLeak")
    private static ListView mPopListView;
    private static PopupWindow mPopupWindow;
    private static ArrayAdapter<String> sAdapter;

    /**
     * 显示arraypopupwindow
     * @param context
     * @param view
     * @param list
     * @param onItemClickListener
     */
    public static void showArrayPopWindow(Context context, View view, List<String> list, AdapterView.OnItemClickListener onItemClickListener) {
        if (mPopListView == null) {
            mPopListView = new ListView(context);
        }
        mPopupWindow = new PopupWindow(mPopListView, view.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_corner_white)); // 设置popWindow背景颜色
        mPopupWindow.setFocusable(true); // 让popWindow获取焦点
        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        backgroundAlpha(context,0.5f);
        mPopupWindow.showAsDropDown(view, 0, 5);
        //添加pop窗口关闭事件
        mPopupWindow.setOnDismissListener(() -> {
            backgroundAlpha(context,1f);
        });
        sAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
        mPopListView.setAdapter(sAdapter);
        mPopListView.setOnItemClickListener(onItemClickListener);
    }

    public static void closePopupWindow(){
        if (mPopupWindow!=null){
            mPopupWindow.dismiss();
        }
        if (mPopListView != null){
            mPopListView = null;
        }
        if (sAdapter != null){
            sAdapter = null;
        }
    }
    /**
     * 显示自定义popupwindow
     * @param context
     * @param view
     * @param onItemClickListener
     */
    public static void showCustomPopWindow(Context context, View view, BaseAdapter adapter, AdapterView.OnItemClickListener onItemClickListener) {
        final ListView mPopListView = new ListView(context);
        final PopupWindow mPopupWindow = new PopupWindow(mPopListView, view.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.shape_corner_white)); // 设置popWindow背景颜色
        mPopupWindow.setFocusable(true); // 让popWindow获取焦点
        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        backgroundAlpha(context,0.5f);
        mPopupWindow.showAsDropDown(view, 0, 5);
        //添加pop窗口关闭事件
        mPopupWindow.setOnDismissListener(() -> backgroundAlpha(context,1f));
        mPopListView.setAdapter(adapter);
        mPopListView.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public static void backgroundAlpha(Context context,float bgAlpha) {
        WindowManager.LayoutParams lp =((Activity)context).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity)context).getWindow().setAttributes(lp);
    }

}
