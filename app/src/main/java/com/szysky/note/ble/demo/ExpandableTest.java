package com.szysky.note.ble.demo;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.szysky.note.ble.R;
import com.szysky.note.ble.view.MyExpandableListAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import static com.szysky.note.ble.R.id.parent;

/**
 * Author :  suzeyu
 * Time   :  2016-08-08  下午10:40
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 * ClassDescription :
 */

public class ExpandableTest extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ExpandableListView elv_main = (ExpandableListView) findViewById(R.id.elv_main);

        elv_main.setAdapter(new MyExpandableListAdapter(getApplicationContext()));

        //        设置分组项的点击监听事件
        elv_main.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), i+"", Toast.LENGTH_SHORT).show();
                return false;
            }});

//        设置子选项点击监听事件
            elv_main.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    Toast.makeText(getApplicationContext(), groupPosition+" "+childPosition, Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

        /**
         * setOnGroupCollapseListener   还可以设置分组合并监听
         * setOnGroupExpandListener     还可以设置分组展开监听
         */




    }
}
