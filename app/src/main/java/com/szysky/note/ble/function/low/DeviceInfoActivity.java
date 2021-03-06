package com.szysky.note.ble.function.low;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.szysky.note.ble.R;
import com.szysky.note.ble.util.ComputerUtils;
import com.szysky.note.ble.util.SampleGattAttributes;
import com.szysky.note.ble.util.SuLogUtils;
import com.szysky.note.ble.view.MyExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Author : suzeyu
 * Time   : 16/8/19  下午5:09
 * Blog   : http://szysky.com
 * GitHub : https://github.com/suzeyu1992
 * <p>
 * ClassDescription : 根据获取的设备
 */
public class DeviceInfoActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 打开界面的请求码
     */
    private static final int REQUEST_CODE_EDIT_DATA = 0x60;
    /**
     * 蓝牙设备信息
     */
    private String mDeviceName, mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private TextView mDataField, mConnectionState;

    /**
     * listView的Item名称
     */
    private final String LIST_NAME = "NAME", LIST_UUID = "UUID";

    /**
     * 蓝牙设备的连接状态
     */
    private boolean mConnected;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothLeService mBluetoothLeService;

    /**
     * 对类中进行选择操作的 弹出窗口
     */
    private PopupWindow mPopupWindow;

    /**
     * 要准备发送的特征, 只有在点击item并点击写入数据调转页面的时候 此值才不为null
     */
    private BluetoothGattCharacteristic mPrepareCharacteristic;
    private MenuItem mCurrentMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        // 接收传入的设备信息
        Intent intent = getIntent();
        mDeviceName = intent.getStringExtra("name");
        mDeviceAddress = intent.getStringExtra("address");

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);


        getSupportActionBar().setTitle(mDeviceName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SuLogUtils.d("onCreate");
        getMenuInflater().inflate(R.menu.connection_ble, menu);
        mCurrentMenu = menu.findItem(R.id.menu_connection_ble);
        mCurrentMenu.setTitle(mConnected==true? "断开连接": "重新连接");
        return super.onCreateOptionsMenu(menu);

    }

    private long lastClickOptionItemTime ;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SuLogUtils.d("option");

        switch (item.getItemId()){

            case R.id.menu_connection_ble:

                SuLogUtils.d("时间差: "+(System.currentTimeMillis() - lastClickOptionItemTime));

                // 防止频繁断开
                if ((System.currentTimeMillis() - lastClickOptionItemTime) < 3000){
                    Toast.makeText(getApplicationContext(), "你点击的太快哦, 等一会再点.", Toast.LENGTH_SHORT).show();
                    return true;
                }

                // 进行设备的连接和断开的按钮
                if (mConnected) {
                    mConnected = false;
                    mBluetoothLeService.disconnect();
                    mBluetoothLeService.close();
                } else {
                    mConnected = true;
                    mBluetoothLeService.connect(mDeviceAddress);
                }
                lastClickOptionItemTime = System.currentTimeMillis();
                return true;

            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(ComputerUtils.TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {


                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();

                        displayPopup(characteristic);

                        SuLogUtils.e("获取的特征属性值:" + charaProp);


                        return true;
                    }
                    return false;
                }


            };


    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState("Connected");
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState("Disconnected");
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    /**
     * 更新最新的连接状态显示在TextView上
     */
    private void updateConnectionState(final String strBody) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(strBody);
                if (mCurrentMenu != null){
                    SuLogUtils.e("@@ +要改变的文本: "+strBody);
                    mCurrentMenu.setTitle(strBody);
                }


            }
        });
    }

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText("没有数据");
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "未知服务";
        String unknownCharaString = "未知特征";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        MyExpandableListAdapter myExpandableListAdapter = new MyExpandableListAdapter(getApplicationContext(), gattServiceData, gattCharacteristicData);
//        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//                this,
//                gattServiceData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[]{LIST_NAME, LIST_UUID},
//                new int[]{android.R.id.text1, android.R.id.text2},
//                gattCharacteristicData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[]{LIST_NAME, LIST_UUID},
//                new int[]{android.R.id.text1, android.R.id.text2}
//        );
        mGattServicesList.setAdapter(myExpandableListAdapter);
    }


    /**
     * 构建一个广播的Intent意图, 用于在蓝牙服务中各种状态回调时通知到显示界面.
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    /**
     * 封装一个创建popup的方法 只需接收一个layout布局对象
     */
    private void initPopup(View replace_popup) {

        mPopupWindow = null;
        // 点击 空白处pop可以消失
        mPopupWindow = new PopupWindow(replace_popup,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);

        mPopupWindow.setOutsideTouchable(true);

        // 当pop关闭的时候调用此监听方法
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f; // 0.0-1.0
                getWindow().setAttributes(lp);
            }
        });
    }

    public void displayPopup(BluetoothGattCharacteristic characteristic) {

        View popupup_check_image = getLayoutInflater().inflate(R.layout.popup_check_function, null);
        initPopup(popupup_check_image);
        //popup底部取消控件
        TextView checkFunCancel = (TextView) popupup_check_image.findViewById(R.id.tv_check_cancel);
        checkFunCancel.setOnClickListener(this);

        mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
        mPopupWindow.showAtLocation(new View(this), Gravity.END | Gravity.BOTTOM, 0, 0);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f; // 0.0-1.0
        getWindow().setAttributes(lp);

        TextView checkFunWrite = (TextView) popupup_check_image.findViewById(R.id.tv_check_write);
        checkFunWrite.setTag(characteristic);
        checkFunWrite.setOnClickListener(this);

        TextView checkFunNotify = (TextView) popupup_check_image.findViewById(R.id.tv_check_notify);
        checkFunNotify.setTag(characteristic);
        checkFunNotify.setOnClickListener(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_EDIT_DATA:
                if (data == null) return;

                byte[] byteArrayExtra = data.getByteArrayExtra(CharacteristicDataSendActivity.RESULT_SEND_DATA);
                if (byteArrayExtra == null || byteArrayExtra.length == 0) {
                    Toast.makeText(getApplicationContext(), "获取发送数据失败!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 开始处理返回来的字节数组并发送
                mPrepareCharacteristic.setValue(byteArrayExtra);
                mBluetoothLeService.mBluetoothGatt.writeCharacteristic(mPrepareCharacteristic);
                mPrepareCharacteristic = null;


                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 测试数据 无需关心
    public static ArrayList<byte[]> OsendArrs = new ArrayList<>();

    static {
        OsendArrs.add(new byte[]{2, 1, 0, 1, 0, 1, 0, 0, 3, 3});
        OsendArrs.add(new byte[]{2, 1, 3, -24, 0, 1, 0, 8, 126, 17, 0, 0, 0, 0, -1, -1, -114, 3});
        OsendArrs.add(new byte[]{2, 1, 0, 0, 0, 5, 0, 1, 6, 1, 3});
        OsendArrs.add(new byte[]{2, 1, 0, 0, 0, 6, 0, 1, 6, 2, 3});
        OsendArrs.add(new byte[]{2, 1, 0, 0, 0, 7, 0, 1, 6, 3, 3});
        OsendArrs.add(new byte[]{2, 1, 0, 0, 0, 8, 0, 1, 6, 12, 3});
        OsendArrs.add(new byte[]{2, 1, 0, 0, 0, 9, 0, 1, 6, 13, 3});
        OsendArrs.add(new byte[]{2, 1, 0, 0, 0, 10, 0, 1, 6, 14, 3});
        OsendArrs.add(new byte[]{2, 1, 0, 0, 0, 11, 0, 1, 6, 15, 3});
        OsendArrs.add(new byte[]{2, 1, 0, 0, 0, 12, 0, 1, 6, 8, 3});
        OsendArrs.add(new byte[]{2, 1, 0, 0, 0, 1, 0, 1, 6, 5, 3});
        OsendArrs.add(new byte[]{2, 1, 3, -23, 0, 1, 0, 10, 126, -124, 0, 0, 0, 67, 116, 109, 107, 73, 96, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -23, 0, 2, 0, 10, 100, 120, 61, 49, 124, 107, 101, 121, 84, 121, -41, 23});
        OsendArrs.add(new byte[]{2, 1, 0, 1, 0, 1, 0, 0, 3, 3});
        OsendArrs.add(new byte[]{2, 1, 3, -24, 0, 1, 0, 10, 126, -124, 0, 0, 0, 67, 116, 109, 107, 73, 97, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -24, 0, 2, 0, 10, 100, 120, 61, 49, 124, 107, 101, 121, 84, 121, -42, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -24, 0, 3, 0, 10, 112, 101, 61, 84, 68, 75, 124, 107, 101, 121, -103, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -24, 0, 4, 0, 10, 68, 97, 116, 97, 61, 67, 65, 48, 54, 70, -87, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -24, 0, 5, 0, 10, 57, 50, 53, 70, 68, 54, 66, 68, 69, 56, -106, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -24, 0, 6, 0, 10, 48, 65, 54, 49, 48, 56, 51, 52, 48, 55, -102, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -24, 0, 7, 0, 10, 70, 57, 68, 54, 51, 69, 55, 52, 67, 66, -100, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -24, 0, 8, 0, 5, 69, 65, 52, -1, -1, -43, 3});
        OsendArrs.add(new byte[]{2, 1, 0, 0, 0, 1, 0, 1, 6, 5, 3});
        OsendArrs.add(new byte[]{2, 1, 3, -23, 0, 1, 0, 10, 126, -122, 0, 0, 0, 44, 99, 97, 114, 100, 34, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -23, 0, 2, 0, 10, 84, 121, 112, 101, 61, 83, 73, 67, 124, 97, -96, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -23, 0, 3, 0, 10, 109, 111, 117, 110, 116, 61, 48, 48, 48, 48, -80, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -23, 0, 4, 0, 10, 48, 48, 48, 48, 49, 48, 48, 48, 124, 116, -18, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -23, 0, 5, 0, 10, 105, 109, 101, 111, 117, 116, 61, 51, 48, 48, -25, 23});
        OsendArrs.add(new byte[]{2, 1, 3, -23, 0, 6, 0, 2, -1, -1, -19, 3});
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            //点击取消按钮关闭popupWindow弹窗
            case R.id.tv_check_cancel:
                mPopupWindow.dismiss();
                break;

            // 对特征进行通道notify监听
            case R.id.tv_check_notify:
                // 获取点击的特征实例
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) v.getTag();
                // 获得特征的属性值
                int properties = characteristic.getProperties();



                // 获取特征的描述对象
                BluetoothGattDescriptor desc = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));


                // 对监听特征进行描述(description)的数据写入
                if ((properties != 16 && desc == null)) {
                    Toast.makeText(getApplicationContext(), "此特征不支持监听", Toast.LENGTH_SHORT).show();
                    mPopupWindow.dismiss();
                    return;
                }


                // 设置特征的描述 并设置并写入描述
                desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothLeService.mBluetoothGatt.writeDescriptor(desc);

                mNotifyCharacteristic = characteristic;

                // 对读通道开始进行监听
                mBluetoothLeService.setCharacteristicNotification(characteristic, true);

                mPopupWindow.dismiss();
                break;

            // 对写通道写入数据
            case R.id.tv_check_write:

                // 先保存要发送的特征
                mPrepareCharacteristic = (BluetoothGattCharacteristic) v.getTag();

                Intent intent = new Intent(getApplicationContext(), CharacteristicDataSendActivity.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_DATA);


                mPopupWindow.dismiss();
                break;
        }
    }
}
