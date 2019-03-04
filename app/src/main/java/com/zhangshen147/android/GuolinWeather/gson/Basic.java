package com.zhangshen147.android.GuolinWeather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 张申 on 2017/11/16 0016.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;
    }
}
