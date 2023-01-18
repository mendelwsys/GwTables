package com.mwlib.tablo.tables;

import com.mycompany.common.FieldException;
import com.mycompany.common.tables.ColumnHeadBean;
import com.smartgwt.client.types.ListGridFieldType;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.05.14
 * Time: 17:21
 * Простое поле для отображения табличных данных
 */
public class SimpleField extends AField
{
    public SimpleField(String title) {
        super(title);
    }

    public SimpleField(String name, String title) {
        super(name, title);
    }

    public SimpleField(String name, String title, boolean visible) {
        super(name, title);
        this.visible=visible;
    }

    public SimpleField(String name, String title, String type, boolean visible) {
        super(name, title, type, visible);
    }

    public Object formatVal(String type, Object val)
    {
        try
        {
            if (ListGridFieldType.TEXT.toString().equals(type))
            {
                if (val!=null)
                    return String.valueOf(val);
                else
                    return val;
            }
            else if (ListGridFieldType.BOOLEAN.toString().equals(type))
            {
                if (val instanceof Boolean)
                    return val;
                else if (val instanceof Number)
                    return ((Number)val).intValue()!=0;
                else if (val!=null)
                    return Boolean.parseBoolean(String.valueOf(val));
                else
                    return null;
            }
            else if (ListGridFieldType.INTEGER.toString().equals(type))
            {
                    if (val instanceof Number)
                        return ((Number)val).intValue();
                    else if (val!=null)
                        return Integer.parseInt(String.valueOf(val));
                    else
                        return null;
            }
            else if (ListGridFieldType.FLOAT.toString().equals(type))
            {
                if (val instanceof Number)
                    return ((Number)val).doubleValue();
                else if (val!=null)
                    return Double.parseDouble(String.valueOf(val));
                else
                    return null;
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        return String.valueOf(val);
    }

//    public Object getS(Map<String,ColumnHeadBean> columns, Map tuple, Map<String, Object> _outTuple) throws FieldException
//    {
//            return formatVal(type, tuple.get(name));
////        try {
////            ColumnHeadBean columnHeadBean = columns.get(name);
////            if (columnHeadBean!=null)
////                    return formatVal(columnHeadBean.getType(), tuple.get(name));
////            return "";
////        } catch (RuntimeException e)
////        {
////            throw e;
////        }
//
//    }

    public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
    {

//        for (int i = 0, columnLength = column.length; i < columnLength; i++) {
//            ColumnHeadBean columnHeadBean = column[i];
//            if (columnHeadBean.getName().equals(name))
//                return formatVal(columnHeadBean.getType(), tuple.get(name));
//        }
//        throw new FieldException("Can't find meta info");
        return formatVal(type, tuple.get(name));
    }
}
