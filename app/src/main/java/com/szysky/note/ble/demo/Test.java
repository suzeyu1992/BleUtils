package com.szysky.note.ble.demo;

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


        String pattern = "[0-9]+";

        //  创建 Pattern 实例对象
        Pattern compile = Pattern.compile(pattern);

        Matcher matcher = compile.matcher("aaaaaa");

        boolean b = matcher.find();
        if (b){
        }
        System.out.println("find匹配结果 "+b);
        boolean matches = matcher.matches();
        System.out.println("matches匹配结果 "+b);


    }
}
