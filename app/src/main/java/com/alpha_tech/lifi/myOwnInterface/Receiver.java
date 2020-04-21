package com.alpha_tech.lifi.myOwnInterface;

import android.content.Intent;
import android.widget.TextView;

import org.w3c.dom.Text;

public interface Receiver {

    public void updateUI(final String type, final String payloadBits, final TextView textView);
    public Intent getAppIntent(String packageName);
    public void executeCommand(String received);
    public void displayText(String received, TextView textView);
}
