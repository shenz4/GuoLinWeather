package com.zhangshen147.android.GuoLinWeather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 张申 on 2017/11/16 0016.
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }

}
