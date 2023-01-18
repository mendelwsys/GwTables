package com.mwlib.tablo.db;

import com.mycompany.common.Pair;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 15:00
 * Интерфейс получения событий из БД
 */
public interface IEventProvider
{
    Pair<IMetaProvider,Map[]> getUpdateTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception;

    Pair<IMetaProvider,Map[]> getDeletedTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception;

    IMetaProvider getMetaProvider();

    Map<String,ParamVal> getNextUpdateParams(Map<String, Object> outParams);
}
