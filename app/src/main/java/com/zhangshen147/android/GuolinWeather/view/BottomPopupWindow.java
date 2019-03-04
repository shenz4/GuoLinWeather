package com.zhangshen147.android.GuolinWeather.view;

import android.content.Context;
import android.widget.TextView;

import com.zhangshen147.android.GuolinWeather.R;
import com.zhangshen147.android.GuolinWeather.base.BasePopupWindow;

/**
 * Created by zhangshen147 2019/1/10
 */

public class BottomPopupWindow extends BasePopupWindow {

    public final static int FLAG_EDIT = 0;
    public final static int FLAG_DELETE = 1;

    private TextView mEditText;
    private TextView mDeleteText;
    private TextView mCancelText;

    BottomPopupWindow(Context context){
        super(context);
    }

    @Override
    protected void findView() {

    }

    @Override
    protected int setPopupView() {
        return 0;
    }

    @Override
    protected int setRemindView() {
        return R.layout.window_remind;
    }
}
