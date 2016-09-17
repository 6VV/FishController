package com.lyyjy.yfyb.fishcontroller;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Administrator on 2016/9/17.
 */
public interface IBluetoothCallback {
    void onSearch(BluetoothDevice device);

    void onConnectChanged();
}
