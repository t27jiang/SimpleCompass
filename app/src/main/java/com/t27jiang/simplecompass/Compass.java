package com.t27jiang.simplecompass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by tianjiang on 2015-03-22.
 */
public class Compass implements SensorEventListener {
    private static final String TAG = "Compass";

    private SensorManager mSensorManager;
    // Gravity sensor
    private Sensor gsensor;
    private float[] mGravity = new float[3];
    // Magnetic sensor
    private Sensor msensor;
    private float[] mGeomagnetic = new float[3];

    private float azimuth = 0f;
    private float currectAzimuth = 0;

    // Compass arrow to rotate
    private ImageView arrow = null;

    public Compass(Context context) {
        mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        gsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void setArrowView(ImageView image) {
        arrow = image;
    }

    public void start() {
        //Register sensor at the fastest rate
        mSensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        // Unregister sensor to stop
        mSensorManager.unregisterListener(this);
    }

    private void changeDirection() {
        if (arrow == null) {
            Log.e(TAG, "arrow view is not set");
            return;
        }

        // Use animation to rotate compass arrow
        Animation an = new RotateAnimation(-currectAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currectAzimuth = azimuth;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        arrow.startAnimation(an);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        //Lock critical steps to prevent mistake
        synchronized (this) {
            // Low pass filter to filter out noise
            // human move is considered as low frequency movement
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];
            }

            // Get direction from sensors
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth + 360) % 360;
                changeDirection();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
