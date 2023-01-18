package com.mwlib.tablo.db;

import com.mycompany.common.tables.ColumnHeadBean;

import java.sql.ResultSet;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 30.09.14
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public interface IRowOperation {
    void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception;
}
