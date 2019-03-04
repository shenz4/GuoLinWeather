package com.zhangshen147.android.GuolinWeather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangshen147.android.GuolinWeather.MainActivity;
import com.zhangshen147.android.GuolinWeather.R;
import com.zhangshen147.android.GuolinWeather.WeatherActivity;
import com.zhangshen147.android.GuolinWeather.db.City;
import com.zhangshen147.android.GuolinWeather.db.County;
import com.zhangshen147.android.GuolinWeather.db.Province;
import com.zhangshen147.android.GuolinWeather.util.HttpUtil;
import com.zhangshen147.android.GuolinWeather.util.JsonUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 张申 on 2017/11/14 0014.
 */


public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private static final String TAG = "ChooseAreaFragment";
    public static final String ADDRESS_QUERY_COUNTRY = "http://guolin.tech/api/china";
    public static final String ADDRESS_QUERY_CITY_PRE = "http://guolin.tech/api/china";
    public static final String ADDRESS_QUERY_COUNTY_PRE = "http://guolin.tech/api/china";

    // views
    private ProgressDialog mProgressDialog;
    private TextView mLevelText;
    private Button mBackButton;
    private ListView mListView;
    private ArrayAdapter<String> mListAdapter;
    private List<String> mDataList = new ArrayList<>();
    private View mRootView;

    //省、市、县列表
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    //选中的省、市、县
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    // others
    private int currentLevel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_choose_area, container, false);

        findView();
        mListAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_string, mDataList);
        mListView.setAdapter(mListAdapter);

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        queryProvinces();
        addListener();
    }

    private void addListener() {
        // 添加监听
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(position).getWeatherId();

                    //由于当前fragment在MainActivity和WeatherActivity两个地方被复用，所以需要根据当前Context执行不同的操作
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.mDrawerLayout.closeDrawers();
                        activity.mSwipRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }

                }
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });

    }


    private void queryProvinces() {
        // 查询全国所有的省，优先从数据库查询，如果没有再去服务器上查询

        mLevelText.setText(R.string.china);
        mBackButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);

        if (provinceList.size() > 0) {
            mDataList.clear();
            for (Province p : provinceList) {
                mDataList.add(p.getProvinceName());
            }
            mListAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(ADDRESS_QUERY_COUNTRY, "province");
            Log.v(TAG, "queryProvince");
        }
    }


    private void queryCities() {
        // 查询本省所有的市

        mLevelText.setText(selectedProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectedProvince.getId()))
                .find(City.class);
        if (cityList.size() > 0) {
            mDataList.clear();
            for (City c : cityList) {
                mDataList.add(c.getCityName());
            }
            mListAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china" + "/"
                    + provinceCode;
            queryFromServer(address, "city");
        }
    }


    private void queryCounties() {
        // 查询本市所有的县

        mLevelText.setText(selectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",
                String.valueOf(selectedCity.getId()))
                .find(County.class);
        if (countyList.size() > 0) {
            mDataList.clear();
            for (County c : countyList) {
                mDataList.add(c.getCountyName());
            }
            mListAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china" + "/"
                    + selectedProvince.getProvinceCode() + "/"
                    + cityCode;
            queryFromServer(address, "county");
        }
    }


    private void queryFromServer(String address, final String type) {
        // 根据传入的地址和类型从服务器上查询数据

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = JsonUtil.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = JsonUtil.handleCityResponce(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = JsonUtil.handleCountyResponce(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });

    }

    private void findView() {
        // 绑定视图
        mLevelText = (TextView) mRootView.findViewById(R.id.choose_area_textView);
        mBackButton = (Button) mRootView.findViewById(R.id.back_button);
        mListView = (ListView) mRootView.findViewById(R.id.choose_area_list_view);
    }


    private void showProgressDialog() {
        // 显示进度条
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }


    private void closeProgressDialog() {
        // 关闭进度条
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
