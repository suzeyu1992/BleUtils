package com.szysky.note.ble.util;

/**
 * Author :  suzeyu
 * Time   :  2016-08-08  下午5:56
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 用于辅助计算 而 提供的方法
 */

public class ComputerUtils {

    public static final String TAG = "susu";

    /**
     * 把一个字节数组转换成字符串
     * @param bys   接收一个字节数组
     * @return      转换后的字符串, 如果数组为空, 返回提示字符串
     */
    public static String byts2str(byte[] bys){

        if (bys== null)
            return "传入的数组为空!!!";

        StringBuilder stringBuilder = new StringBuilder();

        for (byte temp : bys) {
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }}
