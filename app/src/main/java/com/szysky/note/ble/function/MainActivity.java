package com.szysky.note.ble.function;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.szysky.note.ble.R;
import com.szysky.note.ble.function.advertise.AdvertiseActivity;
import com.szysky.note.ble.function.low.BLEActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_main);

        View conBleButton =  findViewById(R.id.btn_try_connection_ble);
        View conSocketButton = findViewById(R.id.btn_try_connection_socket);
        View conPhone2PhoneButton = findViewById(R.id.btn_try_phone2phone);
        View testButton = findViewById(R.id.btn_test);

        setViewAnimation(conBleButton, conSocketButton, conPhone2PhoneButton, testButton);

        getSupportActionBar().hide();
        // 设置监听
        conBleButton.setOnClickListener(this);
        conPhone2PhoneButton.setOnClickListener(this);
        conSocketButton.setOnClickListener(this);
        testButton.setOnClickListener(this);

        boolean gpsEnable = isGpsEnable(getApplicationContext());
        if (!gpsEnable){
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivityForResult(intent,22);

            openGPS(getApplicationContext());
        }

    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
    // Gps是否可用
    public static final boolean isGpsEnable(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    private void setViewAnimation(View... views){

        int[][] ints = new int[][]{
                {0xffffa000, 0xffffa0ff},
                {0xffa0ff00, 0xffa0ffff},
                {0xffffafa0, 0xff00afa0},
                {0xff5050a0, 0xffff50a0},

        };

        int tempOptionNum = 0;

        for (View temp : views) {
            ObjectAnimator colorAnim = ObjectAnimator.ofInt(temp, "backgroundColor", ints[tempOptionNum%4][0], ints[tempOptionNum++%4][1]);
            colorAnim.setDuration(3000);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.start();
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        switch (v.getId()){
            //  使用蓝牙4.0进行后续处理
            case R.id.btn_try_connection_ble:
                intent.setClass(getApplicationContext(), BLEActivity.class);
                startActivity(intent);
                break;

            //  使用蓝牙2.0进行后续处理
            case R.id.btn_try_connection_socket:

                break;

            //  使用蓝牙4.0进行连接, 模拟设备当外围设备
            case R.id.btn_try_phone2phone:
                intent.setClass(getApplicationContext(), AdvertiseActivity.class);
                startActivity(intent);
                break;

            //  测试跳转无具体内容
            case R.id.btn_test:
//                intent.setClass(getApplicationContext(), ExpandableTest.class);
//                startActivity(intent);
                Toast.makeText(getApplicationContext(), "暂无功能", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
