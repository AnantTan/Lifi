package com.alpha_tech.lifi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alpha_tech.lifi.myOwnInterface.Transmitter;
import com.alpha_tech.lifi.utils.Code;
import com.alpha_tech.lifi.utils.FlashLight;

import static java.lang.Thread.sleep;


public class SendTextActivity extends AppCompatActivity implements Transmitter, AdapterView.OnItemSelectedListener {

    private ProgressBar progressBar;
    private TextView textView;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private String userMessage;
    private String bitStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);

        // Code for spinner
        Spinner mySpinner = findViewById(R.id.text_spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SendTextActivity.this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.text_items));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
       mySpinner.setOnItemSelectedListener(this);
        //Code for drop down ends, onSelect methods at button

        //if there is no flashlight in the phone
        boolean hasFlash = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        Log.d("Has Flashlight:", Boolean.toString(hasFlash));
        if (!hasFlash) {
            showNoFlashLightAlert();
        }
    }

    @Override
    public void showNoFlashLightAlert() {
        new AlertDialog.Builder(this)
                .setTitle("No Flashlight!")
                .setMessage("Flashlight is not available on this device.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    finish(); // close the Android app
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.id_about) {
            //Intent to another activity
            Intent intentAbout = new Intent(SendTextActivity.this, AboutActivity.class);
            startActivity(intentAbout);
            return true;
        }
        return true;
    }


    @Override
    public void showEmptyMessageAlert() {
        new AlertDialog.Builder(this)
                .setTitle("No command selected!")
                .setMessage("Choose a command.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void startTransmission(View view) {
        progressBar = findViewById(R.id.text_progressBar);
        textView = findViewById(R.id.text_textView);

        //check if the permission is granted for the flashlight or not
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("Please allow access to camera for the flashlight")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish(); // close the Android app
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        Log.d("SendButton", "User clicked the button.");
        Code code = new Code();
        bitStream = code.getBitStream(userMessage);

        Log.d("User entered:", userMessage);
        if (userMessage.isEmpty()) {
            Log.d("Transmitter", "No command selected.");
            showEmptyMessageAlert();
        } else {
            new Thread() {
                public void run() {
                    transmitData();
                }
            }.start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    showProgress();
                }
            }).start();
        }
    }

    @Override
    public void showProgress() {

        int bitLength = bitStream.length();
        progressStatus = 0;
        while (progressStatus < 100) {
            progressStatus += 1;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progressStatus);
                    if (progressStatus < 100) {
                        textView.setText(new StringBuilder().append("Progress: ").append(progressStatus).append("/").append(progressBar.getMax()).toString());
                    } else {
                        textView.setText("Transmission Completed.");
                    }
                }
            });
            try {
                Thread.sleep(20 * bitLength);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void transmitData() {
        final int frequency = 1; // bps
        final int milliSecond = 1000 / frequency;
        FlashLight led = new FlashLight();
        try {
            for (char bit : bitStream.toCharArray()) {
                //when bit is 1 turn on LED
                //when bit is 0 turn off LED
                if (bit == '1') {
                    led.turnOn();
                    sleep(milliSecond);
                }
                led.turnOff();
                sleep(milliSecond);
            }
            led.release();
        } catch (InterruptedException e) {
            String TAG = "Flash";
            Log.w(TAG, "InterruptedException");
        }
     }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] codes = {"A", "B", "C", "D"};
        Log.d("nice", String.valueOf(position));
        userMessage = codes[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        userMessage = "";
    }
}