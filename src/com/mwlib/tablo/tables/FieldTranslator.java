package com.mwlib.tablo.tables;

import com.mycompany.common.FieldException;
import com.mycompany.common.tables.ColumnHeadBean;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.05.14
 * Time: 17:11
 * Tрасляция
 */
public interface FieldTranslator
{
    ColumnHeadBean getColumnHeadBean() throws FieldException;
    Object getS(Map<String,ColumnHeadBean> columns, Map tuple, Map<String, Object> _outTuple) throws FieldException;
    Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException;
}
