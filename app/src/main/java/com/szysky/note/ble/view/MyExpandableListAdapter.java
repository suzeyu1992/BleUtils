package com.szysky.note.ble.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

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
    @Override
    public int getGroupCount() {
        return mGroupStrings.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildStrings.length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupStrings[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildStrings[groupPosition][childPosition];
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
            convertView = new TextView(mContent);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView) convertView;
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(mGroupStrings[groupPosition]);
        return convertView;
    }

    //获取显示指定分组中的指定子选项的视图
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = new TextView(mContent);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvTitle = (TextView) convertView;
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tvTitle.setText(mChildStrings[groupPosition][childPosition]);
        return convertView;
    }

    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    static class GroupViewHolder {
        TextView tvTitle;
    }
    static class ChildViewHolder {
        TextView tvTitle;
    }
}
