package com.szysky.note.ble.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.szysky.note.ble.R;
import com.szysky.note.ble.util.SuLogUtils;

/**
 * Author :  suzeyu
 * Time   :  2016-09-13  下午4:14
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription :   用于对远程连接的设备中的某一个特征进行数据设置并写入发送的界面
 */

public class CharacteristicDataSendActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     *  设置EditText框输入内容的所使用的格式 (例如是十六进制还是二级制)
     */
    private Button mSetupBinaryButton;
    private Button mSetupHexButton;

    public boolean mCurrentDataTypeIsHex = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_send);

        // 找到需要的控件
        EditText sendDataEditText = (EditText) findViewById(R.id.et_data_transmission);
        mSetupBinaryButton = (Button) findViewById(R.id.btn_write_type_binary);
        mSetupHexButton = (Button) findViewById(R.id.btn_write_type_hex);

        // 添加监听
        mSetupBinaryButton.setOnClickListener(this);
        mSetupHexButton.setOnClickListener(this);

        sendDataEditText.addTextChangedListener(new TextWatcher() {
            int count = 0;
            int start = 0;
            int before = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//                SuLogUtils.d("before  "+s.toString()+"   start:"+start+"     after"+after+"        count"+count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                SuLogUtils.d("onTextChanged  "+s.toString()+"   start:"+start+"     before"+before+"        count"+count);
                this.count = count;
                this.start = start;
                this.before = before;
            }

            @Override
            public void afterTextChanged(Editable s) {
                SuLogUtils.d("afterTextChanged  "+s.toString());




            }
        });

//        sendDataEditText.setKeyListener(new DigitsKeyListener() {
//            @Override
//            public int getInputType() {
//                return InputType.TYPE_CLASS_NUMBER;
//            }
//
//            @Override
//            protected char[] getAcceptedChars() {
//                char[] chars = "1234567890".toCharArray();
//                return chars;
//            }
//        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            // 十六进制按钮
            case R.id.btn_write_type_hex:
                // 恢复二进制按钮, 并设置本身为不可点击状态
                mSetupBinaryButton.setEnabled(true);
                v.setEnabled(false);
                mCurrentDataTypeIsHex = true;


                Toast.makeText(getApplicationContext(), "设置十六进制输入模式", Toast.LENGTH_SHORT).show();

                break;
            
            
            // 二进制按钮
            case R.id.btn_write_type_binary:
                // 恢复十六进制按钮, 并设置本身为不可点击状态
                mSetupHexButton.setEnabled(true);
                v.setEnabled(false);
                mCurrentDataTypeIsHex = false;


                Toast.makeText(getApplicationContext(), "设置二进制输入模式", Toast.LENGTH_SHORT).show();

                break;
            
        }
    }
}
