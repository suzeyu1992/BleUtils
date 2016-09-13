package com.szysky.note.ble.activity;

import android.opengl.ETC1;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.szysky.note.ble.R;
import com.szysky.note.ble.util.EditTextUtil;
import com.szysky.note.ble.util.SuLogUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private EditText mSendDataEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_send);

        // 找到需要的控件
        mSendDataEditText = (EditText) findViewById(R.id.et_data_transmission);
        mSetupBinaryButton = (Button) findViewById(R.id.btn_write_type_binary);
        mSetupHexButton = (Button) findViewById(R.id.btn_write_type_hex);

        // 添加监听
        mSetupBinaryButton.setOnClickListener(this);
        mSetupHexButton.setOnClickListener(this);

        // 设置EditText的输入模式
        EditTextUtil.setEditTextDisplayAndFilter(16, mSendDataEditText);

        mSendDataEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 用于处理删除动作时候如果遇到空格, 那么就手动删除空格的角标-1的元素
                int length = s.toString().length();
                if (length > 2){
                    boolean equals = s.toString().substring(length - 1, length).equals(" ");
                    if (equals){
                        s.delete(length-1, length);

                    }
                }

            }
        });

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

                EditTextUtil.setEditTextDisplayAndFilter(16, mSendDataEditText);


                Toast.makeText(getApplicationContext(), "设置十六进制输入模式", Toast.LENGTH_SHORT).show();

                break;
            
            
            // 二进制按钮
            case R.id.btn_write_type_binary:
                // 恢复十六进制按钮, 并设置本身为不可点击状态
                mSetupHexButton.setEnabled(true);
                v.setEnabled(false);
                mCurrentDataTypeIsHex = false;

                EditTextUtil.setEditTextDisplayAndFilter(2, mSendDataEditText);


                Toast.makeText(getApplicationContext(), "设置二进制输入模式", Toast.LENGTH_SHORT).show();

                break;
            
        }
    }
}
