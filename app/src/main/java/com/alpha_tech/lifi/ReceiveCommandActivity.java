package com.alpha_tech.lifi;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alpha_tech.lifi.utils.Code;

import java.util.ArrayList;
import java.util.TreeMap;

public class ReceiveCommandActivity extends AppCompatActivity {

    public static TextView textView;
    private SensorManager mSensorManager;
    private SensorEventListener mEventListenerLight;
    private float currentLightIntensity;
    private float bgIntensity = -1;
    private float lightOn = 1000;
    private float lightOff = 0;
    private ArrayList<Float> intensityValues = new ArrayList<>();
    private TreeMap<Long, Float> records;
    private Code code = new Code();
    private long startTime;
    private long lastTime;
    private String bit;
    private String rawReading = "";
    private boolean started = false;
    private String lastFiveBits;
    public static String payload = "";
    private boolean startBitDetected = false;
    private boolean isTransferring = true;
    private int counter = 0;
    private boolean lightCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

         super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_receive_command);

                records = new TreeMap<Long, Float>();

                textView = (TextView) findViewById(R.id.commandSensorValue);
                textView.setText("Waiting for transfer...");
                textView.setEnabled(false);
                mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mTextViewLightLabel.setText("Receiving...");

        mEventListenerLight = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {


                //event is listening do something right away !!!!!!!

                if (bgIntensity == -1) {
                    //startTime = event.timestamp;
                    startTime = System.currentTimeMillis();
//                    referenceTime = System.currentTimeMillis();
                    Log.d("Start timestamp: ", String.valueOf(startTime));
//                    Log.d("Start timestamp: ", String.valueOf(referenceTime));
                    bgIntensity = event.values[0];
                    records.put(0L, bgIntensity);
                    Log.d("Background Intensity: ", String.valueOf(bgIntensity));
                }

                Log.d("RawReading:", String.valueOf(rawReading));
                currentLightIntensity = event.values[0];
                if (currentLightIntensity > 1000 && !started) {
                    lastTime = System.currentTimeMillis();
                    started = true;
             }

                //make code class to all 11111111 and else condition do nothing just reset the counter value and add only q bits

                if (event.values[0] > lightOn) {
                    lightCheck = true;//first bit has been received
                    bit = "1";
                    payload += bit;
                    textView.setText("Received: "+ payload);
                    lightOn = 25000;
                    counter = 0;
                }

                //if led is off density will go below 200 increase the counter and if it continues update the UI
                //only execute when the first bit has been received
                if (event.values[0] < 200 && lightCheck)
                {
                    counter ++;
                    lightOn = 1000;

                    if(counter>=10)
                    {
                        Intent intent = new Intent(ReceiveCommandActivity.this,ProcessingRawDataActivity.class);
                        startActivity(intent);
                        counter=0;
                    }
            }
             rawReading = "";//refresh the string to take in fresh bits
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mEventListenerLight, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop() {
        Log.d("Read all intensities:", String.valueOf(intensityValues));
        Log.d("Read all values:", String.valueOf(records));
        Log.d("Read all data:", rawReading);
        mSensorManager.unregisterListener(mEventListenerLight);
        super.onStop();
    }
}