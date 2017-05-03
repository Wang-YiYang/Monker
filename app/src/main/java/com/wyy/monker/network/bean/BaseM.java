package com.wyy.monker.network.bean;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/5/3.
 */

public class BaseM {
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
