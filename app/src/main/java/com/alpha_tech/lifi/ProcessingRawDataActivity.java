package com.alpha_tech.lifi;

import android.content.Intent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alpha_tech.lifi.myOwnInterface.Receiver;
import com.alpha_tech.lifi.utils.Code;

public class ProcessingRawDataActivity extends AppCompatActivity implements Receiver {

    public boolean commandReceived = false;
    private TextView textView;
    private SensorManager mSensorManager;
    private SensorEventListener mEventListenerLight;
    private Code code = new Code();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        updateUI(ReceiveCommandActivity.payload,ReceiveCommandActivity.textView);
        ReceiveCommandActivity.payload = "";//next command should have a clean payload
    }

    @Override
    public void updateUI(final String payloadBits, final TextView textView) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Payloaddddd  " + payloadBits);

                String message = code.decode(payloadBits);

                System.out.println("messsgggggg " + message);

                if (message != null && !commandReceived) {
                    //mTextViewLightLabel.setText("Received command." + "" + message + "" + "" + payloadBits + "");
                    Log.d("Received:", message);
                    commandReceived = true;
                    if(ReceiveTextActivity.commandType.equals("Text"))
                    {
                        displayText(message,textView);
                        ReceiveTextActivity.commandType = "";
                    }
                    else
                        executeCommand(message,textView);
                } else {
                    textView.setText("Command was not found.");
                }
            }
        });

    }

    @Override
    public void executeCommand(String received,TextView textView) {

       Intent intent = null;
        System.out.println("recccc "+received);
        switch (received) {
            case "L":
                Log.d("Got A.", received);
                String fileLocation = "file:///" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/Music/Faded.mp3";
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(fileLocation), "audio/*");
                 break;

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

            default:
                textView.setText("Bits corrupted ! Command not found.");
        }
        if (intent != null) {
            startActivity(intent);
            finish();
        }
}

    @Override
    public Intent getAppIntent(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
            return launchIntent;
    }

    @Override
    public void displayText(String received,TextView textView) {
       switch (received){

           case "A":
              textView.setText("Hi");
              finish();
               break;

           case "B":
               textView.setText("How are you ?");
               finish();
               break;

           case "C":
               textView.setText("I am good,thanks");
               finish();
               break;

           case "D":
               textView.setText("Bye,take care");
               finish();
               break;
       }
    }
}
