package com.alpha_tech.lifi;

import android.content.Intent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alpha_tech.lifi.myOwnInterface.Receiver;
import com.alpha_tech.lifi.utils.Code;

public class ProcessingRawDataActivity extends AppCompatActivity implements Receiver {

    public boolean commandReceived = false;
    private TextView mTextViewLightLabel;
    private SensorManager mSensorManager;
    private SensorEventListener mEventListenerLight;
    private Code code = new Code();
    private String payload = "";

    public ProcessingRawDataActivity(String type,String bits,TextView textView)
    {
        updateUI(type,bits,textView);
    }

    @Override
    public void updateUI(final String type, final String payloadBits, final TextView textView) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Payloaddddd  " + payload);

                String message = code.decode(payload);

                System.out.println("messsgggggg " + message);

                if (message != null && !commandReceived) {
                    //mTextViewLightLabel.setText("Received command." + "" + message + "" + "" + payloadBits + "");
                    Log.d("Received:", message);
                    commandReceived = true;
                    switch (type)
                    {
                        case "Command":
                            executeCommand(message);
                            break;
                        case "Text":
                            displayText(message,textView);
                            break;
                    }
                } else {
                    mTextViewLightLabel.setText("Command was not found.");
                }
            }
        });

    }

    @Override
    public void executeCommand(String received) {

        System.out.println("in11");
        Intent intent = null;
        System.out.println("caseeeeesss");
        switch (received) {
            case "L":
                Log.d("Got A.", received);
                String fileLocation = "file:///" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/Music/Faded.mp3";
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(fileLocation), "audio/*");
         break;

            //F
            case "B":
                String url = "http://www.google.com";
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                break;

            case "C":
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                break;

            case "D":
                intent = new Intent(Intent.ACTION_DIAL);
                break;

            case "E":
                intent = getAppIntent("com.android.settings");
                break;


            case "M":
                intent = new Intent(Intent.ACTION_SEND);
                intent.createChooser(intent, "Choose email app");
                break;

        }
        if (intent != null) {
            startActivity(intent);
        }
}

    @Override
    public Intent getAppIntent(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            return launchIntent;
    }

    @Override
    public void displayText(String received,TextView textView) {
        System.out.println("on22");
        textView.setText("Hi");
    }
}
