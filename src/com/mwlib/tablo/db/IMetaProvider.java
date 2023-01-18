package com.mwlib.tablo.db;

import com.mycompany.common.tables.ColumnHeadBean;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 30.09.14
 * Time: 14:18
 * * Распределение  событий по их типам,
 * после выборки  из БД возвращает распределенную по
 * типам метаинформацию
 */
public interface IMetaProvider extends IRowOperation
{
    //ColumnHeadBean[] getColumnsByEventType(int data_type);

    ColumnHeadBean[] getColumnsByEventName(String name);

    void addColumnByName(String name, ColumnHeadBean columnHeadBean);

    void addColumnByEventType(int data_type, ColumnHeadBean columnHeadBean);

    void addColumn2AllTypes(ColumnHeadBean columnHeadBean);

    void addColumn2AllTypesIfNotExists(ColumnHeadBean columnHeadBean);

    String[] getEventNames();

    Integer[] getEventTypes();

    ITypes2NameMapper getTypes2NamesMapper();

    boolean isTest();

    void addToMeta(String eventTypeName, ColumnHeadBean[] meta);
}
