package com.mwlib.tablo.derby;

import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICacheFactory;
import com.mycompany.common.TablesTypes;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.07.15
 * Time: 13:12
 * простая фабрика для кешей в виде дерби таблиц
 */
public class DerbyDefaultCacheFactory implements ICacheFactory {
    @Override
    public ICache createCache(Map<String, Object> params)
    {
        final Object cacheName = params.get(ICache.CACHENAME);
        if (TablesTypes.PLACEPOLG.equals(cacheName))
            return null;
        return new DerbyCache(params);
    }
}
