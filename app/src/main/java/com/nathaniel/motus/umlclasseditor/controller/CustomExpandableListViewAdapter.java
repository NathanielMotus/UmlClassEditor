package com.nathaniel.motus.umlclasseditor.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.nathaniel.motus.umlclasseditor.R;
import com.nathaniel.motus.umlclasseditor.model.AdapterItem;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mGroups;
    private HashMap<String,List<AdapterItem>> mChildren;
    private Fragment mFragment;

    public CustomExpandableListViewAdapter(Context context, List<String> groups, HashMap<String, List<AdapterItem>> children) {
        mContext = context;
        mGroups = groups;
        mChildren = children;
    }

        @Override
    public int getGroupCount() {
        return this.mGroups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.mChildren.get(this.mGroups.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.mGroups.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.mChildren.get(this.mGroups.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String title=this.mGroups.get(i);
        if (view == null) {
            LayoutInflater layoutInflater=(LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.list_group,null);
        }
        TextView textViewGroup=view.findViewById(R.id.group_text);
        textViewGroup.setText(title);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String item=((AdapterItem) this.getChild(i,i1)).getName();
        if (view == null) {
            LayoutInflater layoutInflater=(LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=layoutInflater.inflate(R.layout.list_item,null);
        }
        TextView childText=view.findViewById(R.id.child_text);
        childText.setText(item);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
