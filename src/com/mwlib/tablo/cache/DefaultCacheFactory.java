package com.mwlib.tablo.cache;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.07.15
 * Time: 13:10
 * простая фабрика для кеше в виде ключ / кортеж;
 */
public class DefaultCacheFactory implements ICacheFactory
{
    @Override
    public ICache createCache(Map<String, Object> params) {
        return new Cache();
    }
}
