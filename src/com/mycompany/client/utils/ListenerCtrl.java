package com.mycompany.client.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.02.15
 * Time: 13:53
 *
 */
public class ListenerCtrl<T> implements IListenerCtrl<T>,IClickListener<T>
{
    @Override
    public List<IClickListener<T>> getClickListeners()
    {
        return clickListener;
    }

    @Override
    public void copyClickListeners(IListenerCtrl<T> listenerCtrl)
    {
        clickListener.addAll(listenerCtrl.getClickListeners());
    }


    protected List<IClickListener<T>> clickListener = new LinkedList<IClickListener<T>>();
    @Override
    public int addIndexListener(IClickListener clickListener)
    {
        int rv=-1;
        if (clickListener!=null)
        {
            rv=this.clickListener.size();
            this.clickListener.add(clickListener);
        }
        return rv;
    }

    @Override
    public boolean removeIndexListener(IClickListener clickListener)
    {
        return this.clickListener.remove(clickListener);
    }

    @Override
    public IClickListener removeIndexListener(int ix)
    {
        return this.clickListener.remove(ix);
    }



    @Override
    public void clickIndex(T index)
    {
        for (IClickListener<T> iClickListener : clickListener)
            iClickListener.clickIndex(index);
    }
}
