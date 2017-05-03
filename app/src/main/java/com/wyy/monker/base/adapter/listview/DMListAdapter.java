package com.wyy.monker.base.adapter.listview;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonychen on 15/10/19.
 */
public abstract class DMListAdapter<U> extends BaseAdapter {
    protected List<U> mData = new ArrayList<U>();
    protected Context mContext;


    public DMListAdapter(Context context) {
        mContext = context;
    }

    public DMListAdapter(Context context, List<U> data) {
        mContext = context;
        mData = data;
    }

    public void setData(List data) {
        if (data == null) {
            return;
        }
        mData = data;
        this.notifyDataSetChanged();
    }

    public void addData(List data) {
        this.mData.addAll(data);
        this.notifyDataSetChanged();
    }

    public void insertData(U item, int atIndex) {
        this.mData.add(atIndex, item);
        this.notifyDataSetChanged();
    }

    public List<U> getData() {
        return mData;
    }


    public final void removeData(U item) {
        this.mData.remove(item);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }


    @Override
    public U getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
