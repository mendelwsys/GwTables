package com.mwlib.tablo.db;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 30.09.14
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
public interface ITypes2NameMapper
{
    BaseTableDesc[] getAllTableDesc();

    BaseTableDesc getTableDescByTblName(String name);

    BaseTableDesc[] getTableDescByEventType(int data_type);

    String[] getNameFromType(int type);

    String[] getNames();

    Integer[] getTypes();
}
