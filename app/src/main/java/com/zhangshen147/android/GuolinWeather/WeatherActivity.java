package com.zhangshen147.android.GuolinWeather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhangshen147.android.GuolinWeather.gson.Forecast;
import com.zhangshen147.android.GuolinWeather.gson.Weather;
import com.zhangshen147.android.GuolinWeather.util.HttpUtil;
import com.zhangshen147.android.GuolinWeather.util.JsonUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public static final String HEFENG_WEATHER__KEY= "8aa7b7dcd33042909c07522be5120836";

    // views
    public DrawerLayout mDrawerLayout;
    public SwipeRefreshLayout mSwipRefresh;
    private ScrollView mScrollView;
    private TextView mCityTextView;
    private TextView mUpdateTextView;
    private TextView mDegreeTextView;
    private TextView mWeatherInfoTextView;
    private LinearLayout mForecastLine;
    private TextView mAqiTextView;
    private TextView mPm25TextView;
    private TextView mComfortTextView;
    private TextView mCarWashView;
    private TextView mSportTextView;
    private ImageView mBingPicImg;
    private ImageView mMenuButton;

    private View mBottomSheet;
    private View mBottomSheetDialog;
    private BottomSheetBehavior mBehavior;

    // others
    private String mWeatherId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        findView();
        addListener();

        // 底部菜单栏默认设置为折叠
        mBehavior = (BottomSheetBehavior)BottomSheetBehavior.from(mBottomSheet);
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        // 创建底部菜单对话框
        // TODO



        // 得到 SP 缓存
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sp.getString("weather",null);

        if (weatherString != null){
            // 有缓存时直接解析天气数据并显示
            Weather weather = JsonUtil.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            // 无缓存时去服务器查询天气数据并显示
            mWeatherId = getIntent().getStringExtra("weather_id");
            mForecastLine.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }


        String bingPic = sp.getString("bing_pic",null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(mBingPicImg);
        }else {
            loadBingPic();
        }

    }

    private void findView() {

        mScrollView = findViewById(R.id.weather_layout);
        mCityTextView = findViewById(R.id.title_city_textView);
        mUpdateTextView = findViewById(R.id.title_update_time_textView);
        mDegreeTextView = findViewById(R.id.now_degree_textView);
        mWeatherInfoTextView = findViewById(R.id.weather_info_text);
        mForecastLine = findViewById(R.id.forecast_line);
        mAqiTextView = findViewById(R.id.aqi_textView);
        mPm25TextView = findViewById(R.id.pm25_textView);
        mComfortTextView = findViewById(R.id.comfort_textView);
        mCarWashView = findViewById(R.id.car_wash_textView);
        mSportTextView = findViewById(R.id.sport_textView);
        mSwipRefresh = findViewById(R.id.swipe_refreshLayout);
        mBingPicImg = new ImageView(this);
        mBottomSheet = findViewById(R.id.origin_bottom_sheet);
        mMenuButton = findViewById(R.id.menu_button);
    }

    private void addListener(){

        mSwipRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mWeatherId != null){
                    requestWeather(mWeatherId);
                }
            }
        });

        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else {
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }


    private void loadBingPic(){
        // 加载必应每日一图
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Toast.makeText(WeatherActivity.this, "从服务器上加载必应一图失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(mBingPicImg);
                    }
                });
            }
        });
    }

    /**
     * describe:
     * 根据天气 id 请求城市天气数据
     * 成功后及时更新数据
     */
    public void requestWeather(final String weatherId){
        String weatherURL = "http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=" + HEFENG_WEATHER__KEY;
        HttpUtil.sendOkHttpRequest(weatherURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        mSwipRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = JsonUtil.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            //用SP暂存刚得到的Weather实体类
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            //显示
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        mSwipRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }


    private void showWeatherInfo(Weather weather){
        // 解析并更新数据

        // basic 和 now 里的信息
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "°C";
        String weatherInfo = weather.now.more.info;

        mCityTextView.setText(cityName);
        mUpdateTextView.setText(updateTime);
        mDegreeTextView.setText(degree);
        mWeatherInfoTextView.setText(weatherInfo);

        // forecast 里的信息
        mForecastLine.removeAllViews();
        for (Forecast forecast:weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,mForecastLine,false);

            TextView dateText = (TextView) view.findViewById(R.id.forecast_item_date_textView);
            TextView infoText = (TextView) view.findViewById(R.id.forecast_item_info_textView);
            TextView maxText = (TextView) view.findViewById(R.id.forecast_item_max_textView);
            TextView minText = (TextView) view.findViewById(R.id.forecast_item_min_textView);

            dateText.setText(forecast.data);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            mForecastLine.addView(view);
        }

        // aqi 里的信息
        if (weather.aqi != null){
            mAqiTextView.setText(weather.aqi.city.aqi);
            mPm25TextView.setText(weather.aqi.city.pm25);
        }

        // suggestion 里的信息
        String comfort = "舒适度: " + weather.suggestion.comfort.info;
        String carwash = "洗车指数: " + weather.suggestion.carWash.info;
        String sport = "运动建议: " + weather.suggestion.sport.info;

        mComfortTextView.setText(comfort);
        mCarWashView.setText(carwash);
        mSportTextView.setText(sport);

        mScrollView.setVisibility(View.VISIBLE);

    }

}
