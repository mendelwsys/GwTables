package com.mwlib.tablo.tables;

import com.mycompany.common.FieldException;
import com.mycompany.common.tables.ColumnHeadBean;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.05.14
 * Time: 17:21
 * Простое поле для отображения табличных данных
 */
public class SimpleFieldB extends AField
{
    public SimpleFieldB(String title) {
        super(title);
    }

    public SimpleFieldB(String name, String title) {
        super(name, title);
    }

    public SimpleFieldB(String name, String title, String type, boolean visible) {
        super(name, title, type, visible);
    }

    public Object getS(Map<String,ColumnHeadBean> columns, Map tuple, Map<String, Object> _outTuple) throws FieldException
    {
        return tuple.get(name);
    }
}
