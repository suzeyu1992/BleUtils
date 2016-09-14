package com.szysky.note.ble.demo;

import com.szysky.note.ble.util.EditTextUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author :  suzeyu
 * Time   :  2016-09-13  下午3:52
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 * ClassDescription :
 */

public class Test {

    public static void main(String args[]){


        if (0 > ((byte)257)){
            System.out.println("此数小于零 "+(byte)257);
        }else{
            System.out.println("此数大于零 "+(byte)257);
        }


        String pattern = "[^0-9a-fA-F]+";

        //  创建 Pattern 实例对象
        Pattern compile = Pattern.compile(pattern);

        Matcher matcher = compile.matcher("qqqa123aaaaaAqqqqqaaaaa");

        boolean b = matcher.find();

        System.out.println("find匹配结果 "+b +"     start:"+matcher.start()+ "      end:"+matcher.end());
        boolean matches = matcher.matches();
        System.out.println("matches匹配结果 "+matches);
        System.out.println("替换不匹配的 "+matcher.replaceAll(""));

        // 检测十六进制转换十进制
        System.out.println("-------------------------------检测十六进制转换十进制--------------------------------------- ");
        String hexStr = "aa ee e d a c 3 5 11 ff 00 ";
        String resultDecimal = EditTextUtil.hex2Decimal(hexStr);
        System.out.println("  十六进制: "+hexStr);
        System.out.println("转换十进制: "+resultDecimal);
        System.out.println("---------------------------------------------------------------------- ");



        // 检测十进制转换十六进制
        System.out.println("-------------------------------检测十进制转换十六进制--------------------------------------- ");
        String decimalStr = "555.12.1.2.a.33. 44.16.17";
        String resultHex = EditTextUtil.decimal2Hex(decimalStr);
        System.out.println("    十进制: "+decimalStr);
        System.out.println("转换十六进制: "+resultHex);
        System.out.println("---------------------------------------------------------------------- ");





    }
}
