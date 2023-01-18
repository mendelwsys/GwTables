package com.mwlib.tablo;

import com.mwlib.tablo.cache.WrongParam;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.09.14
 * Time: 12:53
 * Фабрика для классов
 */
public interface ICliProviderFactory
{
    public static String CLIID="CLIID";

    ICliProvider[] getProvider(Map parameters) throws WrongParam;

    void addNotSessionCliIds(String clId);

    void removeNotSessionCliIds(String clId);
}
