package com.szysky.note.ble.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.szysky.note.ble.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Author :  suzeyu
 * Time   :  2016-08-08  下午10:50
 * Blog   :  http://szysky.com
 * GitHub :  https://github.com/suzeyu1992
 *
 * ClassDescription : 实现ExpandableListView所需要数据集合
 */

public class MyExpandableListAdapter extends BaseExpandableListAdapter{

    private final Context mContent;
    private ArrayList<HashMap<String, String>> mGroupDataList;
    private ArrayList<ArrayList<HashMap<String, String>>> mChildDataList;

    public MyExpandableListAdapter(Context context){
        mContent = context;
    }




    public String[] mGroupStrings = {"西游记", "水浒传", "三国演义", "红楼梦"};
    public String[][] mChildStrings = {
            {"唐三藏", "孙悟空", "猪八戒", "沙和尚"},
            {"宋江", "林冲", "李逵", "鲁智深"},
            {"曹操", "刘备", "孙权", "诸葛亮", "周瑜"},
            {"贾宝玉", "林黛玉", "薛宝钗", "王熙凤"}
    };



    public MyExpandableListAdapter(Context context, String[] mGroupData , String[][] mChildData){
        mContent = context;
        mGroupStrings = mGroupData;
        mChildStrings = mChildData;
    }

    public MyExpandableListAdapter(Context applicationContext, ArrayList<HashMap<String, String>> gattServiceData, ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData) {
        Toast.makeText(applicationContext, mChildStrings.length+"" , Toast.LENGTH_LONG).show();
        mContent = applicationContext;
        mGroupDataList = gattServiceData;
        mChildDataList = gattCharacteristicData;

    }

    @Override
    public int getGroupCount() {
        return mGroupDataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildDataList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupDataList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildDataList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //获取显示指定分组的视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContent, R.layout.expandable_item_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_group_name);
            groupViewHolder.uuidTextView = (TextView) convertView.findViewById(R.id.tv_group_uuid);
            groupViewHolder.newMessageImageView = (ImageView) convertView.findViewById(R.id.iv_group_new_message);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        // 获取组数据
        HashMap<String, String> tempDataMap = mGroupDataList.get(groupPosition);
        Set<String> keySet = tempDataMap.keySet();
        for (String name : keySet) {
            groupViewHolder.nameTextView.setText(name);
            groupViewHolder.uuidTextView.setText(tempDataMap.get(name));

        }


        return convertView;
    }

    //获取显示指定分组中的指定子选项的视图
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
//            convertView.setBackgroundColor(mContent.getResources().getColor(android.R.color.holo_orange_light));

            convertView = View.inflate(mContent, R.layout.expandable_item_child, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_child_name);
            childViewHolder.uuidTextView = (TextView) convertView.findViewById(R.id.tv_child_uuid);
            childViewHolder.newMessageImageView = (ImageView) convertView.findViewById(R.id.iv_child_new_message);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        // 获取组数据
        HashMap<String, String> tempDataMap = mChildDataList.get(groupPosition).get(childPosition);
//        Set<String> keySet = tempDataMap.keySet();
//        for (String name : keySet) {
//            childViewHolder.nameTextView.setText(name);
//            childViewHolder.uuidTextView.setText(tempDataMap.get(name));
//
//        }


        return convertView;
    }

    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    static class GroupViewHolder {
        TextView nameTextView;
        TextView uuidTextView;
        ImageView newMessageImageView;

    }
    static class ChildViewHolder {
        TextView nameTextView;
        TextView uuidTextView;
        ImageView newMessageImageView;
    }
}
