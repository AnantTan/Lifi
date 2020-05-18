package com.alpha_tech.lifi.myOwnInterface;

import android.view.View;

public interface Transmitter {

    void showNoFlashLightAlert();
    void startTransmission(View view);
    void showEmptyMessageAlert();
    void showProgress();
    void transmitData();
}
