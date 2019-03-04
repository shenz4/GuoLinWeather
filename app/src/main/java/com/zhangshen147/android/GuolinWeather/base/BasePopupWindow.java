package com.zhangshen147.android.GuolinWeather.base;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.PopupWindow;

/**
 * Created by zhangshen147 2019/01/10
 */


public abstract class BasePopupWindow extends PopupWindow{
    protected Activity mContext;
    protected View mContentView;
    protected AdapterView.OnItemClickListener mOnItemClickListener;

    public BasePopupWindow(Context context) {
        super(context);
        this.mContext = mContext;

        // 加载初始布局
        LayoutInflater inflater = (LayoutInflater)mContext.getLayoutInflater();
        mContentView = inflater.inflate(setRemindView(), null);

        findView();
        addListener();

        // 添加初始布局
        setContentView(mContentView);
        // 设置窗体宽高
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置为弹出窗体可点击
        setFocusable(true);
        // 设置弹出动画:渐变

        // 设置窗体透明度：0.5
        setBackGroundAlpha(0.5f);
    }


    private void setBackGroundAlpha(float alphaVaue){
        // 设置透明度

        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = alphaVaue;
        mContext.getWindow().setAttributes(lp);
    }


    private void addListener(){
        // 为窗体设置触摸监听，如果点击位置在窗体外部，则销毁弹出的窗体

        mContentView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mContentView.findViewById(setPopupView()).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    protected abstract void findView();

    protected abstract int setPopupView();
    protected abstract int setRemindView();
}
