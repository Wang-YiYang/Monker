package com.wyy.monker.network.common;

import com.wyy.monker.network.observer.BaseRefreshListObserver;

/**
 * Created by tonychen on 15/10/19.
 */

public abstract class Command<T> {
    public abstract void execute(int pageNumber, int pageSize, BaseRefreshListObserver<T> observer);
}
