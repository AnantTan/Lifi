package com.alpha_tech.lifi;

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

    public boolean commandReceived = false;
    private TextView mTextViewLightLabel;
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
    private String payload = "";
    private boolean startBitDetected = false;
    private boolean isTransferring = true;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_text);

        records = new TreeMap<Long, Float>();

        System.out.println("initii");
        mTextViewLightLabel = (TextView) findViewById(R.id.textSensorValue);
        mTextViewLightLabel.setText("Waiting for transfer...");
        mTextViewLightLabel.setEnabled(false);//cannot edit the text
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
//                    mTextViewLightLabel.setText("1");
//                    rawReading += "1";
                }
                //long timestamp = event.timestamp;
//                if (currentLightIntensity > bgIntensity) {
//                    System.out.println("Crrrr "+currentLightIntensity);
//                    System.out.println("bgggg "+bgIntensity);
//                    bit = "1";
//                } else {
//                    System.out.println("Crrrr "+currentLightIntensity);
//                    System.out.println("bgggg "+bgIntensity);
//                    bit = "0";
//                }
//

                ///////make code class to all 11111111 and else condition do nothing just reset the counter value and add only q bits
                // lightOn = 1000;

                if (event.values[0] > lightOn) {
                    bit = "1";
                    payload += bit;
                    mTextViewLightLabel.setText("Received: "+ payload);
                    System.out.println("bit 1" + event.values[0]);
                    System.out.println("readdddd " + payload);
                    lightOn = 25000;
                    // lightOff = 0;
                    counter = 0;
                }

                //if led is off density will go below 200 increase the counter and if it continues update the UI
                if (event.values[0] < 200)
                { counter ++;
                    lightOn = 1000;

                    System.out.println("counterrrr "+counter);
                    if(counter>=10)
                    {
                        new ProcessingRawDataActivity("Text","1",mTextViewLightLabel);
                        counter=0;
                    }
                }

                /*long currentTime = System.currentTimeMillis();

                if ((currentTime - lastTime) > 499 && started) {
                    Log.d("1 second.", "passed.");
                    lastTime = currentTime;
                    records.put(currentTime - startTime, currentLightIntensity);
                    Log.d("Bit:", bit);
                    mTextViewLightLabel.setText(bit);
                    rawReading += bit;//raw reading will be updated
                }
                //System.out.println("rwwwwwwww " + rawReading);
                intensityValues.add(currentLightIntensity);

                //String startBits = code.getStartBits();
                //String stopBits = code.getStopBits();


                //dealing with total bits in order to create payload bits
                if (rawReading.length() >= 3) {
                    lastFiveBits = rawReading.substring(rawReading.length() - 3);//remove first 3 bits as they are start bits
                    System.out.println("last five bitssss " + lastFiveBits);
                    if (!startBitDetected) {
                        if (lastFiveBits.equals(startBits)) {
                            System.out.println("Start bit detected.");
                            String bits = String.valueOf(rawReading);
                            mTextViewLightLabel.setText("Start bit detected." + bits);//debug the received bits
                            startBitDetected = true;
                        }
                    } else {
                        if (!lastFiveBits.equals(stopBits)) {
                            payload += lastFiveBits;
                            System.out.println("Stop bit detected.");
                            isTransferring = false;
//                            mSensorManager.unregisterListener(mEventListenerLight);
//                            updateUI();
                            mSensorManager.unregisterListener(mEventListenerLight);
                            updateUI(payload);
                        }
                    }*/
                rawReading = "";//refresh the string to take in fresh bits
                //}
                // rawReading = "";

                // System.out.println("==>" + received);

//                updateUI();
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
//        updateUI();
        super.onStop();
    }
}
