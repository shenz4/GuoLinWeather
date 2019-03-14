package com.zhangshen147.android.GuoLinWeather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * @author zhangshen
 * @version 1.0
 */
public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";

    private ImageView mSplashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 使状态栏透明
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);

        mSplashImage = (ImageView)findViewById(R.id.start_image);
        playImage();
    }


    private void playImage(){
        // 为开屏图片播放动画

        mSplashImage.setImageResource(R.drawable.start);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.4f,1.0f,1.4f,1.0f,
                 Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(2000);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(StartActivity.this,MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
                Log.d(TAG,"SplashActivity");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        mSplashImage.startAnimation(scaleAnimation);
    }
}
