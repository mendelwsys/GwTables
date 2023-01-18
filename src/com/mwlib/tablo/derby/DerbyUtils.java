package com.mwlib.tablo.derby;

import com.smartgwt.client.types.ListGridFieldType;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.10.14
 * Time: 13:12
 * Транслятор в типы дерби
 */
public class DerbyUtils
{
    public static String translate2DerbyType(String attr_type) throws SQLException
    {
        if (ListGridFieldType.TEXT.toString().equalsIgnoreCase(attr_type))
            return "VARCHAR (1255)";
        else if (ListGridFieldType.INTEGER.toString().equalsIgnoreCase(attr_type))
            return "int";
        else if (ListGridFieldType.FLOAT.toString().equalsIgnoreCase(attr_type))
            return "FLOAT";
        else if (ListGridFieldType.TIME.toString().equalsIgnoreCase(attr_type))
            return "TIME";
        else if (ListGridFieldType.DATE.toString().equalsIgnoreCase(attr_type))
            return "DATE";
        else if (ListGridFieldType.DATETIME.toString().equalsIgnoreCase(attr_type))
            return "TIMESTAMP";
        throw new SQLException("ERROR BROIDE TYPE:" + attr_type);
    }

}
