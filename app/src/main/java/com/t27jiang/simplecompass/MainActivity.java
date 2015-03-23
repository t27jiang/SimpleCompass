package com.t27jiang.simplecompass;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

/**
 * Created by tianjiang on 2015-03-22.
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    private Compass mCompass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView view = (ImageView) findViewById(R.id.main_image_arrow);
        mCompass = new Compass(this);
        mCompass.setArrowView(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        mCompass.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCompass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCompass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCompass.stop();
    }

}