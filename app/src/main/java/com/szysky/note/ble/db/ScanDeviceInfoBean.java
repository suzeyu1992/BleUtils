package com.szysky.note.ble.db;

import android.bluetooth.BluetoothDevice;

/**
 * Author :  suzeyu
 * Time   :  2016-08-08  下午9:47
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 扫描的蓝牙设备时, 返回的数据存储集合
 */

public class ScanDeviceInfoBean {
    private BluetoothDevice mDevices;
    private int mRssi;

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    private long lastTime;

    public BluetoothDevice getmDevices() {
        return mDevices;
    }

    public void setmDevices(BluetoothDevice mDevices) {
        this.mDevices = mDevices;
    }

    public int getmRssi() {
        return mRssi;
    }

    public void setmRssi(int mRssi) {
        this.mRssi = mRssi;
    }
}
