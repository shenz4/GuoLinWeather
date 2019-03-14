package com.zhangshen147.android.GuoLinWeather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.TouchDelegate;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhangshen147.android.GuoLinWeather.R;

/**
 * @author zhangshen
 * @version 1.0
 */
public class BottomRemindView extends LinearLayout {

    private int mViewWidth;
    private int mViewHeight;
    private int mPointsNum;

    private Context mContext;
    private int mBackgroundColor;
    private ImageView mMenuView;

    private Paint mPaint;
    private OnClickListener mClickListener;

    public BottomRemindView(Context context, int num) {
        super(context);

        mContext = context;
        mPointsNum = num;
        mBackgroundColor = Color.parseColor("#00ffffff");
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // 展开菜单的图标
        mMenuView = new ImageView(context);
        mMenuView.setBackgroundColor(mBackgroundColor);
        mMenuView.setImageResource(R.drawable.ic_menu);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        // 绘制背景
        mPaint.setColor(mBackgroundColor);
        canvas.drawRect(0, 0, mViewWidth, mViewHeight, mPaint);

        // 绘制展开菜单的图标
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void setTouchDelegate(TouchDelegate delegate) {
        super.setTouchDelegate(delegate);
    }
}
