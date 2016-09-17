package com.szysky.note.ble.function.advertise;

import android.bluetooth.le.AdvertiseCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.szysky.note.ble.R;

/**
 * Author : suzeyu
 * Time   : 16/9/17  下午5:30
 * Blog   : http://szysky.com
 * GitHub : https://github.com/suzeyu1992
 *
 * ClassDescription : 把手机端 模拟成在BLE模式下的外围设备 , 可以与默认情况下手机BLE连接作为中心设备 与其对接
 */

public class AdvertiseActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 控制模拟BLE外围设备Advertising的开关控件
     */
    private Switch mSwitch;

    /**
     * 监听并通知 {@code AdvertiserService} 当模拟Ble Advertising成功或者失败的时候.
     * 因为广播只需要在当前活动页面存在即可, 暂时不需要静态注册, 只需要动态注册即可
     */
    private BroadcastReceiver advertisingFailureReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);

        // 设置titleBar文字显示
        setTitle("模拟BLE外围设备");

        mSwitch = (Switch) findViewById(R.id.swi_advertise_switch);
        mSwitch.setOnClickListener(this);

        advertisingFailureReceiver = new BroadcastReceiver() {

            /**
             * Receives Advertising error codes from {@code AdvertiserService} and displays error messages
             * to the user. Sets the advertising toggle to 'false.'
             */
            @Override
            public void onReceive(Context context, Intent intent) {

                int errorCode = intent.getIntExtra(AdvertiserService.ADVERTISING_FAILED_EXTRA_CODE, -1);

                mSwitch.setChecked(false);

                String errorMessage = getString(R.string.start_error_prefix);
                switch (errorCode) {
                    case AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED:
                        errorMessage += " " + getString(R.string.start_error_already_started);
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE:
                        errorMessage += " " + getString(R.string.start_error_too_large);
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                        errorMessage += " " + getString(R.string.start_error_unsupported);
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR:
                        errorMessage += " " + getString(R.string.start_error_internal);
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                        errorMessage += " " + getString(R.string.start_error_too_many);
                        break;
                    case AdvertiserService.ADVERTISING_TIMED_OUT:
                        errorMessage = " " + getString(R.string.advertising_timedout);
                        break;
                    default:
                        errorMessage += " " + getString(R.string.start_error_unknown);
                }

                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * 当界面获取焦点的时候, 根据Ble Advertising 的状态来设置控件的显示.
         * 并且注册广播, 当模式外围设备失败的失败, 会接收到广播
         */
        if (AdvertiserService.running) {
            mSwitch.setChecked(true);
        } else {
            mSwitch.setChecked(false);
        }

        IntentFilter failureFilter = new IntentFilter(AdvertiserService.ADVERTISING_FAILED);
        registerReceiver(advertisingFailureReceiver, failureFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(advertisingFailureReceiver);
    }

    /**
     * Returns Intent addressed to the {@code AdvertiserService} class.
     */
    private  Intent getServiceIntent() {
        return new Intent(getApplicationContext(), AdvertiserService.class);
    }
    /**
     * Starts BLE Advertising by starting {@code AdvertiserService}.
     */
    private void startAdvertising() {
        startService(getServiceIntent());
    }

    /**
     * Stops BLE Advertising by stopping {@code AdvertiserService}.
     */
    private void stopAdvertising() {
        stopService(getServiceIntent());
        mSwitch.setChecked(false);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //  设置模拟外围设备开关
            case R.id.swi_advertise_switch:
                // Is the toggle on?
                boolean on = ((Switch) v).isChecked();

                if (on) {
                    startAdvertising();
                } else {
                    stopAdvertising();
                }
                break;
        }


    }
}
