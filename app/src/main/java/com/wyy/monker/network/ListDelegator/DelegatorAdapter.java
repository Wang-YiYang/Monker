package com.wyy.monker.network.ListDelegator;


import android.view.View;

import com.wyy.monker.base.adapter.recycleView.BaseRecycerAdapter;

import java.util.List;


public interface DelegatorAdapter<T> {
    
    void addFootView(View view);

    void setOnItemClickListener(BaseRecycerAdapter.OnItemClick onItemClick);

    List<T> getDataList();

    void setDataToAdapter(List<T> data);

    int getCount();

    void addData(List<T> data);


}
