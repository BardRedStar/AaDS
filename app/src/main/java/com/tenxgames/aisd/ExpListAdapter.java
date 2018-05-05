package com.tenxgames.aisd;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.tenxgames.aisd.sqlite.SortRecord;

import java.util.ArrayList;

public class ExpListAdapter extends BaseExpandableListAdapter {

    ArrayList<SortRecord> listRecords;
    Context context;

    public ExpListAdapter(Context context, ArrayList<SortRecord> listRecords)
    {
        this.context = context;
        this.listRecords = listRecords;
    }

    @Override
    public int getGroupCount() {
        return listRecords.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return listRecords.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPostition) {
        switch (childPostition) {
            case 0:
                return listRecords.get(groupPosition).sortTime;
            case 1:
                return listRecords.get(groupPosition).sequenceStart;
            case 2:
                return listRecords.get(groupPosition).sequenceSorted;
            default:
                return listRecords.get(groupPosition).sequenceStart;
        }
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int childPostion) {
        return childPostion;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPostition, boolean isExpanded, View view, ViewGroup viewGroup) {
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group_view, viewGroup, false);
        }

        TextView tw = view.findViewById(R.id.groupHeader);
        tw.setText(listRecords.get(groupPostition).time);

        return view;
    }

    @Override
    public View getChildView(int groupPostition, int childPosition, boolean isLastChild,
                             View view, ViewGroup viewGroup) {
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_view, viewGroup, false);
        }
        TextView tw = view.findViewById(R.id.childSortTime);
        tw.setText("Время (сек): " + listRecords.get(groupPostition).sortTime);
        tw = view.findViewById(R.id.childSeqStart);
        tw.setText("Начальная последовательность: \n" +
                listRecords.get(groupPostition).sequenceStart);
        tw = view.findViewById(R.id.childSeqSorted);
        tw.setText("Отсортированная последовательность: \n" +
                listRecords.get(groupPostition).sequenceSorted);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
