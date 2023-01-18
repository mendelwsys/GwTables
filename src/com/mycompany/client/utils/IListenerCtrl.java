package com.mycompany.client.utils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.02.15
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public interface IListenerCtrl<T> {
    int addIndexListener(IClickListener clickListener);

    boolean removeIndexListener(IClickListener clickListener);

    IClickListener removeIndexListener(int ix);

    List<IClickListener<T>> getClickListeners();

    void copyClickListeners(IListenerCtrl<T> listenerCtrl);
}
