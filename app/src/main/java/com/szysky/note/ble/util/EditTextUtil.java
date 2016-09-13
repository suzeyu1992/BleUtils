package com.szysky.note.ble.util;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author :  suzeyu
 * Time   :  2016-09-13  下午10:10
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription :   针对EditText的输入模式等进行设置
 */

public class EditTextUtil {

    /**
     * 设置EditText的输入模式和软键盘弹出时候的模式
     * @param numberMode    可以是2或者16, 默认为16代表输入框接收16进制的数字录入;
     *                      如果是2或者非16的数子, 那么就代码输入的模式为10进制表示已逗号进行分割
     */
    public static void setEditTextDisplayAndFilter(int numberMode, EditText editText){

        switch (numberMode){
            //  设置为16进制输入模式
            case 16:
                editText.setFilters(new InputFilter[]{new InputFilter(){
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        SuLogUtils.e("filter "+source);
                        //  使用正则进行十六进制的匹配规则
                        String pattern = "[0-9A-Fa-f]+";
                        Pattern compile = Pattern.compile(pattern);
                        Matcher matcher = compile.matcher(source);

                        // 如果不能完全匹配 那么对字符内容进行无用的字符过滤
                        if (!matcher.matches()){
                            source = Pattern.compile("[^0-9A-Fa-f]+").matcher(source).replaceAll("");
                        }

                        // 为了保证16进制的工整, 转换为大写
                        source = source.toString().toUpperCase();
                        SuLogUtils.e("改变后 "+source);

                        return source;
                    }
                }});
                // 设置软键盘弹出的输入类型 这里是字母键盘
                editText.setKeyListener(new DigitsKeyListener() {
                    @Override
                    public int getInputType() {
                        return InputType.TYPE_CLASS_TEXT;
                    }

                    @Override
                    protected char[] getAcceptedChars() {
                        char[] chars = "1234567890ABCDEF".toCharArray();
                        return chars;
                    }
                });
                break;


            default:
                editText.setFilters(new InputFilter[]{});

                editText.setKeyListener(new DigitsKeyListener() {
                    @Override
                    public int getInputType() {
                        return InputType.TYPE_CLASS_NUMBER;
                    }

                    @Override
                    protected char[] getAcceptedChars() {
                        char[] chars = "1234567890,".toCharArray();
                        return chars;
                    }
                });
                break;
        }


    }
}
