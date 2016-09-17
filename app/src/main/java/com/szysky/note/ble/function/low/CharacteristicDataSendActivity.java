package com.szysky.note.ble.function.low;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.szysky.note.ble.R;
import com.szysky.note.ble.util.EditTextUtil;
import com.szysky.note.ble.util.SuLogUtils;

import java.util.Arrays;

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
     * 用于界面返回数据Intent中的而一个标志位.
     *  代表要发送数据数组的键名
     */
    public static final String RESULT_SEND_DATA = "data_send";
    /**
     *  设置EditText框输入内容的所使用的格式 (例如是十六进制还是二级制)
     */
    private TextView mSetupBinaryButton;
    private TextView mSetupHexButton;

    public boolean mCurrentDataTypeIsHex = true;
    private EditText mSendDataEditText;
    private TextView mDisplayDataTypeTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_send);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("编辑发送数据");

        // 找到需要的控件
        mSendDataEditText = (EditText) findViewById(R.id.et_data_transmission);
        mSetupBinaryButton = (TextView) findViewById(R.id.btn_write_type_binary);
        mSetupHexButton = (TextView) findViewById(R.id.btn_write_type_hex);
        mDisplayDataTypeTextView = (TextView) findViewById(R.id.tv_display_data_type);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_data_request, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            // 发送数据按钮
            case R.id.menu_write_data_request:

                // 用户体验判断
                if (mSendDataEditText.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(), "发送数据为空!!!", Toast.LENGTH_SHORT).show();
                    return true;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("确定写入文本框的数据?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SuLogUtils.d("发送成功");

                                // 当前内容按字节分组成字符串数组
                                String[] rawSplitArr = null;

                                // 把文本内容转成字节数组
                                // 判断当前显示输出的禁止类型
                                if (mCurrentDataTypeIsHex){
                                    // 当前内容为16进制
                                    String binaryArr = EditTextUtil.hex2Decimal(mSendDataEditText.getText().toString());
                                    rawSplitArr = binaryArr.split("\\.");
                                }else{
                                    rawSplitArr = mSendDataEditText.getText().toString().trim().split("\\.");
                                }

                                // 创建一个字节数, 用于返回上一级界面, 并要写入特征的数据集合
                                byte[] resultBytes = new byte[rawSplitArr.length];

                                for (int i = 0; i < resultBytes.length; i++) {
                                    resultBytes[i] = (byte) Integer.parseInt(rawSplitArr[i]);
                                }

                                SuLogUtils.d(Arrays.toString(resultBytes));

                                // 设置返回数据
                                Intent intent = new Intent();
                                intent.putExtra(RESULT_SEND_DATA, resultBytes);

                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setNegativeButton("no,wait", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;

            // 导航栏的向上箭头
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            // 十六进制按钮
            case R.id.btn_write_type_hex:
                // 恢复十进制按钮, 并设置本身为不可点击状态
                mSetupBinaryButton.setEnabled(true);
                v.setEnabled(false);
                mCurrentDataTypeIsHex = true;
                // 设置当前输入的提示文字
                mDisplayDataTypeTextView.setText("数据类型: 十六进制模式");

                EditTextUtil.setEditTextDisplayAndFilter(16, mSendDataEditText);

                // 进行数据进制的转换
                String convertStr = EditTextUtil.decimal2Hex(mSendDataEditText.getText().toString());
                mSendDataEditText.setText(convertStr);

                Toast.makeText(getApplicationContext(), "设置十六进制输入模式", Toast.LENGTH_SHORT).show();

                break;
            
            
            // 十进制按钮
            case R.id.btn_write_type_binary:
                // 恢复十六进制按钮, 并设置本身为不可点击状态
                mSetupHexButton.setEnabled(true);
                v.setEnabled(false);
                mCurrentDataTypeIsHex = false;
                // 设置当前输入的提示文字
                mDisplayDataTypeTextView.setText("数据类型: 十进制模式");

                EditTextUtil.setEditTextDisplayAndFilter(2, mSendDataEditText);
                // 进行数据进制的转换
                String convertDecimalStr = EditTextUtil.hex2Decimal(mSendDataEditText.getText().toString());
                mSendDataEditText.setText(convertDecimalStr);


                Toast.makeText(getApplicationContext(), "设置十进制输入模式", Toast.LENGTH_SHORT).show();

                break;
            
        }
    }
}
