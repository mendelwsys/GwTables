package com.mwlib.tablo.db;

import com.smartgwt.client.types.ListGridFieldType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 24.10.14
 * Time: 20:43
 * To change this template use File | Settings | File Templates.
 */
public class DbUtils
{
    public static Object getAttrValByAttrType(ResultSet rs, String attr_type) throws SQLException
    {
        if ("STRING".equalsIgnoreCase(attr_type))
            return rs.getString("VALUE_S");
        else if ("INTEGER".equalsIgnoreCase(attr_type))
        {
            BigDecimal bd=rs.getBigDecimal("VALUE_I");
            if (bd!=null)
                return bd.intValue();//rs.getInt("VALUE_I");
            else
                return bd;
        }
        else if ("FLOAT".equalsIgnoreCase(attr_type))
        {
            BigDecimal bd=rs.getBigDecimal("VALUE_F");
            if (bd!=null)
                return bd.doubleValue();//rs.getInt("VALUE_I");
            else
                return bd;
//            return rs.getDouble("VALUE_F");
        }
        else if ("TIMESTAMP".equalsIgnoreCase(attr_type))
            return rs.getTimestamp("VALUE_T");
        throw new SQLException("ERROR BROIDE TYPE");
    }

    public static String translate2AttrType(String attr_type) throws SQLException
    {
        if ("STRING".equalsIgnoreCase(attr_type))
            return ListGridFieldType.TEXT.toString();
        else if ("INTEGER".equalsIgnoreCase(attr_type))
            return ListGridFieldType.INTEGER.toString();
        else if ("FLOAT".equalsIgnoreCase(attr_type))
            return ListGridFieldType.FLOAT.toString();
        else if ("TIMESTAMP".equalsIgnoreCase(attr_type))
            return ListGridFieldType.DATETIME.toString();
        throw new SQLException("ERROR BROIDE TYPE:" + attr_type);
    }


    public static int translate2SqlCode(String attr_type) throws SQLException
    {
        if (ListGridFieldType.TEXT.getValue().equalsIgnoreCase(attr_type))
            return Types.VARCHAR;
        else if (ListGridFieldType.INTEGER.getValue().equalsIgnoreCase(attr_type))
            return Types.INTEGER;
        else if (ListGridFieldType.FLOAT.getValue().equalsIgnoreCase(attr_type))
            return Types.FLOAT;
        else if (ListGridFieldType.DATETIME.getValue().equalsIgnoreCase(attr_type))
            return Types.TIMESTAMP;
        else if (ListGridFieldType.DATE.getValue().equalsIgnoreCase(attr_type))
            return Types.DATE;
        else if (ListGridFieldType.TIME.getValue().equalsIgnoreCase(attr_type))
            return Types.TIME;
        throw new SQLException("ERROR BROIDE TYPE:" + attr_type);
    }


    public static String translate2AttrTypeByClassName(String attr_type) throws SQLException
    {
        if (String.class.getName().equals(attr_type))
            return ListGridFieldType.TEXT.toString();
        else if (Integer.class.getName().equals(attr_type))
            return ListGridFieldType.INTEGER.toString();
        else if (Float.class.getName().equals(attr_type))
            return ListGridFieldType.FLOAT.toString();
        else if (Double.class.getName().equals(attr_type))
            return ListGridFieldType.FLOAT.toString();
        else if (Date.class.getName().equals(attr_type))
            return ListGridFieldType.DATE.toString();
        else if (java.sql.Date.class.getName().equals(attr_type))
            return ListGridFieldType.DATE.toString();
        else if (Time.class.getName().equals(attr_type))
            return ListGridFieldType.TIME.toString();
        else if (Timestamp.class.getName().equals(attr_type))
            return ListGridFieldType.DATETIME.toString();
        throw new SQLException("Can't find for JAVA TYPE:" + attr_type);
    }


    public static String translate2AttrType(Class attr_type) throws SQLException
    {
        if (String.class.equals(attr_type))
            return ListGridFieldType.TEXT.toString();
        else if (Integer.class.equals(attr_type))
            return ListGridFieldType.INTEGER.toString();
        else if (Float.class.equals(attr_type))
            return ListGridFieldType.FLOAT.toString();
        else if (Double.class.equals(attr_type))
            return ListGridFieldType.FLOAT.toString();
        else if (java.sql.Date.class.equals(attr_type))
            return ListGridFieldType.DATE.toString();
        else if (Time.class.equals(attr_type))
            return ListGridFieldType.TIME.toString();
        else if (Timestamp.class.equals(attr_type))
            return ListGridFieldType.DATETIME.toString();
        throw new SQLException("Can't find for JAVA TYPE:" + attr_type);
    }



}
