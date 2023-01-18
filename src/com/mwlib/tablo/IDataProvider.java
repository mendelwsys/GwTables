package com.mwlib.tablo;

import com.mycompany.common.cache.CacheException;
import com.mycompany.common.tables.ColumnHeadBean;

import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.09.14
 * Time: 12:50
 *
 */
public interface IDataProvider
{

    Object[][] getTuplesByParameters(Map<String, Object> parameters, UpdateContainer containerParams) throws CacheException;

    Object[] getTupleByKey(Object key) throws CacheException;

    Set<Object> getAllDataKeys() throws CacheException;

    String getColNameByIx(int ix);

    Integer getIxByColName(String colName);

    /**
     * @return Отдать метаданные поставщика данных
     */
    ColumnHeadBean[] getMeta();

}
