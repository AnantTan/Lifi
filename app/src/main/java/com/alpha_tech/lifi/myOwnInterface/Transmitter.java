package com.alpha_tech.lifi.myOwnInterface;

import android.view.View;

public interface Transmitter {

    void showNoFlashLightAlert();
    void showEmptyMessageAlert();
    void startTransmission(View view);
    void showProgress();
    void transmitData();
}
