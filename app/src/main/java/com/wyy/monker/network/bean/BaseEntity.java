package com.wyy.monker.network.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/25.
 */

public class BaseEntity<T> extends BaseM implements Serializable {
    private String status;
    private String info;
    private String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}