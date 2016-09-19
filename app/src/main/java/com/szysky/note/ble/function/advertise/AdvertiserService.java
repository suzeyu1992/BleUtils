package com.szysky.note.ble.function.advertise;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.szysky.note.ble.R;

import java.util.concurrent.TimeUnit;

/**
 * Author : suzeyu
 * Time   : 16/9/17  下午6:21
 * Blog   : http://szysky.com
 * GitHub : https://github.com/suzeyu1992
 *
 * ClassDescription : 一个独立管理模拟BLE外围设备服务类
 *                    即使应用程序锁屏, 或者应用进程被杀掉,此服务仍然存在.
 */

public class AdvertiserService extends Service {

    private static final String TAG = AdvertiserService.class.getSimpleName();

    /**
     * 一个用来显示模拟状态的标识符的全局变量, 如果你需要更好的方法, 那么可以看如下定义
     * https://groups.google.com/forum/#!topic/android-developers/jEvXMWgbgzE
     */
    public static boolean running = false;

    public static final String ADVERTISING_FAILED =
            "com.szysky.note.ble.bluetoothadvertisements.advertising_failed";
    public static final String ADVERTISING_FAILED_EXTRA_CODE = "failureCode";

    public static final int ADVERTISING_TIMED_OUT = 6;

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private AdvertiseCallback mAdvertiseCallback;

    private Handler mHandler;

    private Runnable timeoutRunnable;

    /**
     * 定义一个长度时间, 在模拟外围设备后, 十分钟的的时候关闭此操作
     */
    private long TIMEOUT = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);

    @Override
    public void onCreate() {
        running = true;
        initialize();
        startAdvertising();
        setTimeout();
        super.onCreate();
    }

    @Override
    public void onDestroy() {

        running = false;
        stopAdvertising();
        mHandler.removeCallbacks(timeoutRunnable);
        super.onDestroy();
    }

    /**
     * 因为目前仅需要用此服务做到启动的效果, 所以不需要进行bind
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 当我们没有了系统的蓝牙操作对象, 那么可以使用此方法进行重新的获取
     */
    private void initialize() {
        if (mBluetoothLeAdvertiser == null) {
            BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager != null) {
                BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();
                if (mBluetoothAdapter != null) {
                    mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
                } else {
                    Toast.makeText(this, getString(R.string.bt_null), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.bt_null), Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * 开启一个延迟的Runnable接口, 他将会在BLE Advertising 超时和设定的时间到达时候停止.
     */
    private void setTimeout(){
        mHandler = new Handler();
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "AdvertiserService has reached timeout of "+TIMEOUT+" milliseconds, stopping advertising.");
                sendFailureIntent(ADVERTISING_TIMED_OUT);
                stopSelf();
            }
        };
        mHandler.postDelayed(timeoutRunnable, TIMEOUT);
    }

    /**
     * 开启 BLE Advertising.
     */
    private void startAdvertising() {
        Log.d(TAG, "Service: Starting Advertising");

        if (mAdvertiseCallback == null) {
            AdvertiseSettings settings = buildAdvertiseSettings();
            AdvertiseData data = buildAdvertiseData();
            mAdvertiseCallback = new SampleAdvertiseCallback();

            if (mBluetoothLeAdvertiser != null) {
                mBluetoothLeAdvertiser.startAdvertising(settings, data,
                        mAdvertiseCallback);
            }
        }
    }

    /**
     * 停止 BLE Advertising.
     */
    private void stopAdvertising() {
        Log.d(TAG, "Service: Stopping Advertising");
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            mAdvertiseCallback = null;
        }
    }

    /**
     * 返回一个包含服务的UUID 和 设备名称的 AdvertiseData对象
     */
    private AdvertiseData buildAdvertiseData() {

        /**
         * Note: There is a strict limit of 31 Bytes on packets sent over BLE Advertisements.
         *  This includes everything put into AdvertiseData including UUIDs, device info, &
         *  arbitrary service or manufacturer data.
         *  Attempting to send packets over this limit will result in a failure with error code
         *  AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE. Catch this error in the
         *  onStartFailure() method of an AdvertiseCallback implementation.
         */

        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.addServiceUuid(Constants.Service_UUID);
        dataBuilder.setIncludeDeviceName(true);

        /* 例如 - 这将导致Advertise文件失败（超过大小限制） */
        //String failureData = "asdghkajsghalkxcjhfa;sghtalksjcfhalskfjhasldkjfhdskf";
        //dataBuilder.addServiceData(Constants.Service_UUID, failureData.getBytes());

        return dataBuilder.build();
    }

    /**
     * 返回一个 {@link AdvertiseSettings}对象, 这个对象设置了使用低功率模式(减少手机电池的使用),
     * 并且禁用了内置超时, 因为这个段代码使用自己的超时即可.
     */
    private AdvertiseSettings buildAdvertiseSettings() {
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        settingsBuilder.setTimeout(0);

        return settingsBuilder.build();
    }

    /**
     * 设置Advertising 模式外围设备操作的结果回调. 如果模拟失败会发送一个带有错误码的广播. 并可在接收处提示并关闭此服务
     */
    private class SampleAdvertiseCallback extends AdvertiseCallback {

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);

            Log.d(TAG, "Advertising failed");
            sendFailureIntent(errorCode);
            stopSelf();

        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.d(TAG, "Advertising successfully started");
        }
    }

    /**
     * 封装一个发送广播的方法, 当模式周边设备失败, 传入一个错误码即可. 内部会把错误码作为intent的外部传递数据供广播接收者接收处理
     */
    private void sendFailureIntent(int errorCode){
        Intent failureIntent = new Intent();
        failureIntent.setAction(ADVERTISING_FAILED);
        failureIntent.putExtra(ADVERTISING_FAILED_EXTRA_CODE, errorCode);
        sendBroadcast(failureIntent);
    }

}