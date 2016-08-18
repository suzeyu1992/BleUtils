package com.szysky.note.periphery;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.UUID;

public class MainActivity extends Activity {

    private String DESC_UUID = "00000000-1111-1111-1111-000000000000";
    private String CHAR_UUID = "00000000-1111-1111-1111-000000000001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        // 代开周边 (外围设备)
        BluetoothGattServer gattServer = bluetoothManager.openGattServer(this, new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                Log.e("sususu", "我被连接了?");
            }
        });

        //初始化描述、特性和服务

        //描述:
        BluetoothGattDescriptor bluetoothGattDescriptor = new BluetoothGattDescriptor(UUID.fromString(DESC_UUID), 0);
        //特性 :
        final int properties = BluetoothGattCharacteristic.PROPERTY_READ
                | BluetoothGattCharacteristic.PROPERTY_WRITE
                | BluetoothGattCharacteristic.PROPERTY_NOTIFY;
        final int permissions = BluetoothGattCharacteristic.PERMISSION_READ
        | BluetoothGattCharacteristic.PERMISSION_WRITE;
        BluetoothGattCharacteristic gattChar = new BluetoothGattCharacteristic(UUID.fromString(CHAR_UUID), properties, permissions);
        gattChar.addDescriptor(bluetoothGattDescriptor);


        //服务：
        BluetoothGattService bs = new BluetoothGattService( UUID.fromString(CHAR_UUID),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        bs.addCharacteristic(gattChar);


        //添加服务
        gattServer.addService(bs);

        //开始广播

    }
}
