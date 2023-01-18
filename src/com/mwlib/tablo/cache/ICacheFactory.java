package com.mwlib.tablo.cache;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 19.11.14
 * Time: 13:16
 * Фабрика кеша (для апарметрического конструирования кеша данных)
 */
public interface ICacheFactory
{
    ICache createCache(Map<String, Object> params);
}
