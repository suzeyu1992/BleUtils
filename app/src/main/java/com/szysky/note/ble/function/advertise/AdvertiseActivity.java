package com.szysky.note.ble.function.advertise;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.szysky.note.ble.R;
import com.szysky.note.ble.util.SuLogUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    BluetoothGattServer bluetoothGattServer=null;
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


        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();


        new Thread(new Runnable() {
            @Override
            public void run() {
                if (true){
                    SystemClock.sleep(1000);

                    bluetoothGattServer = mBluetoothManager.openGattServer(AdvertiseActivity.this, new BluetoothGattServerCallback() {
                        @Override
                        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                            super.onConnectionStateChange(device, status, newState);
                            SuLogUtils.d("onConnectionStateChange");

                            BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(device.getAddress());
                            String name = remoteDevice.getName();



//                            boolean connect = bluetoothGattServer.connect(device, false);

                            List<BluetoothDevice> connectedDevices = bluetoothGattServer.getConnectedDevices();



                        }

                        @Override
                        public void onServiceAdded(int status, BluetoothGattService service) {
                            super.onServiceAdded(status, service);
                            SuLogUtils.d("onServiceAdded");
                        }

                        @Override
                        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
                            SuLogUtils.d("onCharacteristicReadRequest");
                        }

                        @Override
                        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                            SuLogUtils.d("onCharacteristicWriteRequest");
                            SuLogUtils.d("onDescriptorWriteRequest  request:"+requestId +" prepareWrite:"+preparedWrite +" responseNeeded:"+responseNeeded
                                    +" offset:"+offset +" 数据:"+ Arrays.toString(value));
                        }

                        @Override
                        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
                            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
                            SuLogUtils.d("onDescriptorReadRequest");

                        }

                        @Override
                        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);

                        }

                        @Override
                        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
                            super.onExecuteWrite(device, requestId, execute);
                            SuLogUtils.d("onExecuteWrite");
                        }

                        @Override
                        public void onNotificationSent(BluetoothDevice device, int status) {
                            super.onNotificationSent(device, status);
                            SuLogUtils.d("onNotificationSent");
                        }

                        @Override
                        public void onMtuChanged(BluetoothDevice device, int mtu) {
                            super.onMtuChanged(device, mtu);
                            SuLogUtils.d("onMtuChanged");
                        }
                    });
                    BluetoothGattService bluetoothGattService = new BluetoothGattService(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"), BluetoothGattService.SERVICE_TYPE_PRIMARY);

                    BluetoothGattCharacteristic bluetoothGattCharacteristic =
                            new BluetoothGattCharacteristic(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"),
                                    BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE, BluetoothGattCharacteristic.PERMISSION_WRITE);
                    bluetoothGattService.addCharacteristic(bluetoothGattCharacteristic);

                    boolean b = bluetoothGattServer.addService(bluetoothGattService);
                }
            }
        }).start();


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
