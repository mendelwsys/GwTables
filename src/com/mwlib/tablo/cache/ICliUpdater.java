package com.mwlib.tablo.cache;

import com.mycompany.common.cache.CacheException;
import com.mwlib.tablo.UpdateContainer;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.09.14
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */
public interface ICliUpdater
{
    int getQueueVolume();
    void updateData(UpdateContainer dataRef) throws CacheException;
    String getTblId();
    String[] getTType();

    String getCliId();
}
