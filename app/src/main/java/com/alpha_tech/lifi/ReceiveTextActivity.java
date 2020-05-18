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

public class ReceiveTextActivity extends AppCompatActivity {


    private SensorManager mSensorManager;
    private SensorEventListener mEventListenerLight;
    private float currentLightIntensity;
    private float bgIntensity = -1;
    private float lightOn = 1000;
    public static TextView textView;
    public static String commandType="";
    private ArrayList<Float> intensityValues = new ArrayList<>();
    private TreeMap<Long, Float> records;
    private long startTime;
    private long lastTime;
    private String bit;
    private String rawReading = "";
    private boolean started = false;
    public static String payload = "";
    private int counter = 0;
    private boolean lightCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_text);

        records = new TreeMap<Long, Float>();

        System.out.println("initii");
        textView = (TextView) findViewById(R.id.textSensorValue);
        textView.setText("Waiting for transfer...");
        textView.setEnabled(false);//cannot edit the text
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mTextViewLightLabel.setText("Receiving...");

        mEventListenerLight = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //event is listening do something right away !!!!!!!
                if (bgIntensity == -1) {
                    //startTime= System.currentTimeMillis();
                    //startTime = event.timestamp;
                    startTime = System.currentTimeMillis();
//                    referenceTime = System.currentTimeMillis();
                    Log.d("Start timestamp: ", String.valueOf(startTime));
//                    Log.d("Start timestamp: ", String.valueOf(referenceTime));
                    bgIntensity = event.values[0];
                    records.put(0L, bgIntensity);
                    Log.d("Background Intensity: ", String.valueOf(bgIntensity));
//                    rawReading += "0";
                }

                Log.d("RawReading:", String.valueOf(rawReading));
                currentLightIntensity = event.values[0];
                if (currentLightIntensity > 1000 && !started) {
                    lastTime = System.currentTimeMillis();
                    started = true;
                }

                ///////make code class to all 11111111 and else condition do nothing just reset the counter value and add only q bits

                if (event.values[0] > lightOn) {
                    lightCheck = true;
                    bit = "1";
                    payload += bit;
                    textView.setText("Received: "+ payload);
                    System.out.println("bit 1" + event.values[0]);
                    System.out.println("readdddd " + payload);
                    lightOn = 25000;
                    // lightOff = 0;
                    counter = 0;
                }

                //if led is off density will go below 200 increase the counter and if it continues update the UI
                if (event.values[0] < 200 && lightCheck)
                { counter ++;
                    lightOn = 1000;
                    if(counter>=10)
                    {
                        commandType = "Text";//in the receiving class check the type
                        ProcessingRawDataActivity activity = new ProcessingRawDataActivity();
                        activity.updateUI(payload,textView);
                        payload = "";
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
