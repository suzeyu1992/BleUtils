package com.szysky.note.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.szysky.note.ble.db.ScanDeviceInfoBean;
import com.szysky.note.ble.low.DeviceInfoActivity;
import com.szysky.note.ble.util.ComputerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler = new MyHandler();


    /**
     * handler 进行更新ListView的标记
     */
    private static final int UPDATE_LISTVIEW_ADAPTER = 0X15;

    /**
     * 蓝牙设备管理者  开始蓝牙的核心类
     */
    private BluetoothAdapter mBluetoothAdapter;

    /**
     * 扫描回调记录的结果结合,   用于后续进行过滤赋值给  listview的集合展示用
     */
    private HashMap<String,ScanDeviceInfoBean > mExistDevList ;

    /**
     * 停止循环标志位
     */
    private boolean isStop;


    /**
     * 开始蓝牙扫描的回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    int type = device.getType();
                    ParcelUuid[] uuids = device.getUuids();
                    Log.i(ComputerUtils.TAG, "run: " + device.getName() +" rssi强度" +rssi);

                    //判断扫描的设备是否已经存在
                    boolean containsKey = mExistDevList.containsKey(device.getAddress());
                    if (!containsKey){
                        //不存在创建一个扫描对象存入到 scan的集合中去
                        ScanDeviceInfoBean scanDeviceInfoBean = new ScanDeviceInfoBean();
                        scanDeviceInfoBean.setmDevices(device);
                        scanDeviceInfoBean.setmRssi(rssi);
                        scanDeviceInfoBean.setLastTime(System.currentTimeMillis());

                        mExistDevList.put(device.getAddress() , scanDeviceInfoBean);
                    }else{
                        // 如果存在, 那就利用角标更新rssi值
                        ScanDeviceInfoBean scanDeviceInfoBean = mExistDevList.get(device.getAddress());

                        scanDeviceInfoBean.setmRssi(rssi);
                        scanDeviceInfoBean.setLastTime(System.currentTimeMillis());

                    }


                }
            };
    private LeDeviceListAdapter mDeviceListAdapter;
    private BluetoothLeScanner bluetoothLeScanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv_main = (ListView) findViewById(R.id.lv_main);


        mExistDevList = new HashMap<>();
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

    @Override
    protected void onResume() {
        super.onResume();
        isStop = false;
        scanBleDevice(true);

        // 创建一个集合用于循环刷新最新的scan结果
        new Thread(new Runnable() {
            @Override
            public void run() {

                // 用于进行备份一直处于scan返回结果的集合  防止异步操作一个集合的异常
                HashMap<String, ScanDeviceInfoBean> copyHashmap = new HashMap<>();


                while (!isStop){
                    long currentTime = System.currentTimeMillis();

                    // scan集合复制  准备遍历
                    copyHashmap.putAll(mExistDevList);
                    Set<String> strings = copyHashmap.keySet();

                    //清空listview的集合
                    mDeviceListAdapter.clear();

                    // 遍历扫描结果集合,  对两秒中出现的设备添加到listview中
                    for (String str : strings) {
                        ScanDeviceInfoBean entryInfo = copyHashmap.get(str);
                        long timeOffset = currentTime - entryInfo.getLastTime();
                        if (2000 > timeOffset){
                            mDeviceListAdapter.mLeDevices.add(entryInfo);
                        }
                    }

                    // 清空复制集合  并进行刷新
                    copyHashmap.clear();
                    mHandler.obtainMessage(UPDATE_LISTVIEW_ADAPTER).sendToTarget();
                    Log.e(TAG, "adapter的大小:"+mDeviceListAdapter.mLeDevices.size() );
                    SystemClock.sleep(1000);
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        // 进行扫尾工作
        isStop = true;
        scanBleDevice(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // 进行扫尾工作
        isStop = true;
        scanBleDevice(false);
        super.onDestroy();

    }

    /**
     * 控制扫描蓝牙设备的方法
     *
     * @param enable 如果为true 那么就开始扫描; 否则就停止扫描
     */
    private void scanBleDevice(boolean enable) {
        if (enable) {
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = mDeviceListAdapter.getDevice(position).getmDevices();
        if (device == null) {
            Log.w(TAG, "onItemClick: 无法正确获得设备");
            return;
        }

        Intent intent = new Intent(getApplicationContext(), DeviceInfoActivity.class);
        // 打开新的界面并把选中的设备名称和地址传入下一个界面
        intent.putExtra("name", device.getName());
        intent.putExtra("address", device.getAddress());

        startActivity(intent);

    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        public ArrayList<ScanDeviceInfoBean> mLeDevices;

        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<ScanDeviceInfoBean>();
            mInflator = MainActivity.this.getLayoutInflater();
        }

        public void addDevice(ScanDeviceInfoBean  devicesInfo) {
            mLeDevices.add(devicesInfo);
        }

        public ScanDeviceInfoBean getDevice(int position) {
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
                viewHolder.tv_rssi = (TextView) view.findViewById(R.id.tv_signal_num);
                viewHolder.iv_signal = (ImageView) view.findViewById(R.id.iv_signal_display);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            //设置扫描到的名字和mac地址
            ScanDeviceInfoBean device = mLeDevices.get(i);
            final String deviceName = device.getmDevices().getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
                viewHolder.deviceAddress.setText(device.getmDevices().getAddress());

            } else {
                viewHolder.deviceName.setText("没有名字的设备");
                viewHolder.deviceAddress.setText(device.getmDevices().getAddress());
            }

            //设置距离值
            int rssiInt = device.getmRssi();
            String scan_rssi = getResources().getString(R.string.scan_rssi);
            viewHolder.tv_rssi.setText(String.format(scan_rssi, rssiInt));

            //设置信号强弱图片
            if (rssiInt > -45){
                viewHolder.iv_signal.setImageLevel(4);

            }else if (rssiInt > -60){
                viewHolder.iv_signal.setImageLevel(3);

            }else if (rssiInt > -75){
                viewHolder.iv_signal.setImageLevel(2);

            }else if (rssiInt > -90){
                viewHolder.iv_signal.setImageLevel(1);

            }else {
                viewHolder.iv_signal.setImageLevel(0);

            }



            return view;
        }

        class ViewHolder {
            TextView deviceName;
            TextView deviceAddress;
            TextView tv_rssi;
            ImageView iv_signal;
        }
    }


    /**
     * 处理handler事件
     */
     class  MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_LISTVIEW_ADAPTER:
                    // 更新adapter集合
                    mDeviceListAdapter.notifyDataSetChanged();
                    Log.d(TAG, "handleMessage: ok = " +mDeviceListAdapter.mLeDevices.size());
                    break;

                default:
                    super.handleMessage(msg);

            }
        }
    }

}
