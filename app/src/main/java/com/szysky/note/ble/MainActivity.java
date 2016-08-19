package com.szysky.note.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.szysky.note.ble.low.DeviceInfoActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * 蓝牙设备管理者  开始蓝牙的核心类
     */
    private BluetoothAdapter mBluetoothAdapter;

    private Handler mHandler = new Handler();


    /**
     * 开始蓝牙扫描的回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    Log.i("hahaha", "run: " + device.getName());
                    mDeviceListAdapter.addDevice(device);
                    mDeviceListAdapter.notifyDataSetChanged();

                }
            };
    private LeDeviceListAdapter mDeviceListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv_main = (ListView) findViewById(R.id.lv_main);
        mDeviceListAdapter = new LeDeviceListAdapter();
        lv_main.setAdapter(mDeviceListAdapter);

        // 判断当前设备是否支持BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "设备不支持ble,已经退出", Toast.LENGTH_LONG).show();
            finish();
        }


        // 获得蓝牙的管理者
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 判断蓝牙在设备上是已经开启.
        // 如果没有开启, 那么就弹窗申请开启
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 99);
        }

        //开启蓝牙扫描
        scanBleDevice(true);

        lv_main.setOnItemClickListener(this);

    }


    /**
     * 控制扫描蓝牙设备的方法
     *
     * @param enable 如果为true 那么就开始扫描; 否则就停止扫描
     */
    private void scanBleDevice(boolean enable) {
        if (enable) {
            // 发送一个延迟消息, 在10秒后关闭扫描
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, 10000);

            // 开始扫描
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = mDeviceListAdapter.getDevice(position);
        if (device == null) {
            Log.w(TAG, "onItemClick: 无法正确获得设备");
            return;
        }

        Intent intent = new Intent(getApplicationContext(), DeviceInfoActivity.class);


    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = MainActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.item_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
                viewHolder.deviceAddress.setText(device.getAddress());

            } else {
                viewHolder.deviceName.setText("没有名字的设备");
                viewHolder.deviceAddress.setText(device.getAddress());
            }

            return view;
        }

        class ViewHolder {
            TextView deviceName;
            TextView deviceAddress;
        }
    }


}
