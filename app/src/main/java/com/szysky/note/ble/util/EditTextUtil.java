package com.szysky.note.ble.util;

import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

import java.util.Arrays;
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
    public static void setEditTextDisplayAndFilter(int numberMode, final EditText editText){

        switch (numberMode){
            //  设置为16进制输入模式
            case 16:
                editText.setFilters(new InputFilter[]{new InputFilter(){
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        SuLogUtils.e("filter "+source+"-");



                        // 判断是删除操作还是添加操作
                        if (start==0 && end==0 ){

                        }else{
                            // ----添加操作-----
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
                            // 重新组装的容器
                            StringBuffer stringBuffer = new StringBuffer();

                            int desLength = dest.toString().length();

                            // 判断是否需要添加空格
                            // 判断长度是否大于2 并且已存在的最后两个元素是否已经不存在空格, 如果条件为true那么需要在新加入的内容最前面添加空格
                            if (desLength >= 2 && (!dest.toString().substring(desLength-2, desLength).contains(" "))){
                                stringBuffer.append(" ");
                            }



                            // ---------------------开始处理填入数据内容---------------------
                            // 获得长度判断需要多少个空格
                            int blankNum = source.toString().length() / 2;
                            if (blankNum >= 1){
                                // 获取源数据的字符数组
                                char[] chars = source.toString().toCharArray();

                                // 这里有两种情况, 一种是已存在数据为 'AA AA A', 另外一种是'AA AA'. 分别处理
                                boolean desSingle = false;
                                if (desLength == 1){
                                    desSingle = true;
                                }else if (desLength >= 2 && (dest.toString().substring(desLength - 2, desLength).contains(" "))){
                                    desSingle = true;

                                }

                                // 开始插入空格 重新组装数据
                                for (int i = blankNum; i > 0 ; i--) {
                                    int copyIndex = blankNum - i;
                                    // 这里有两种情况, 一种是已存在数据为 'AA AA A', 另外一种是'AA AA'. 分别处理
                                    if (desSingle){
                                        stringBuffer.append( chars[copyIndex * 2] ).append(' ').append(chars[copyIndex * 2 + 1]);
                                        if (chars.length%2 == 1){
                                            stringBuffer.append(chars[chars.length-1]);     // 对填充为集合为单数时, 进行遗漏数据补充
                                        }
                                    }else {

                                        stringBuffer.append( chars[copyIndex * 2] ).append(chars[copyIndex * 2 + 1]).append(' ');

                                        // 对最后一次循环进行确认 并修补
                                        if (i == 1){
                                            if (chars.length%2 == 1){
                                                stringBuffer.append(chars[chars.length-1]);     // 对填充为集合为单数时, 进行遗漏数据补充
                                            }else{
                                                stringBuffer.delete(stringBuffer.length()-1, stringBuffer.length());    // 清除最后面的空格
                                            }
                                        }
                                    }
                                }

                                source = stringBuffer.toString();


                            }else{
                                source = stringBuffer.append(source);
                            }

                        }




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
                        char[] chars = "1234567890ABCDEF ".toCharArray();
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
