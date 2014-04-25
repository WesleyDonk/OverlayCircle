package com.transparentcircle.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.OvershootInterpolator;

public class MainActivity extends Activity {

    private TransparentCircle transparentCircle;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        transparentCircle = (TransparentCircle) findViewById(R.id.circle);
    }

    @Override
    public void onResume() {
        super.onResume();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                transparentCircle.fillScreen(new OvershootInterpolator(), false, null);
            }
        }, 1500);
    }
}
