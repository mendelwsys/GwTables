package com.mwlib.tablo;

import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICliUpdater;
import com.mycompany.common.Pair;
import com.mwlib.tablo.db.BaseTableDesc;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 16:37
 *
 */
public interface ICliManager
{
    /*
    Получить мно-во апдейтеров клиента, для информировании об обновления данных
    */
    ICliUpdater[] getCachesForName(String name);

    /**
     *
     * @param eventName - имя события
     * @param cache  - кэш и дескриптор таблиц
     * @return - кэш и дескриптор таблиц
     */
    Pair<ICache, BaseTableDesc> putCacheForName(String eventName, Pair<ICache,BaseTableDesc> cache);


    void removeCache(ICliUpdater cache);

    void removeDeadSessions();

}
