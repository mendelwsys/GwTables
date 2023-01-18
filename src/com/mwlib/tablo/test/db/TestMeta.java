package com.mwlib.tablo.test.db;

import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.derby.DerbyTableOperations;
import com.smartgwt.client.types.ListGridFieldType;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 29.11.14
 * Time: 12:15
 * To change this template use File | Settings | File Templates.
 */
public class TestMeta
{
    public static void main(String[] args) throws Exception
    {
        DerbyTableOperations derbyTableOperations=DerbyTableOperations.getDefDerbyTableOperations();

        ColumnHeadBean[] cols=new ColumnHeadBean[]{
                new ColumnHeadBean("1","A1", ListGridFieldType.TEXT.toString()),
                new ColumnHeadBean("2","A2", ListGridFieldType.FLOAT.toString()),
                new ColumnHeadBean("3","A3", ListGridFieldType.INTEGER.toString())
        };
        if (derbyTableOperations.isTableExists("TEST"+DerbyTableOperations.META_TABLE_EXT))
            derbyTableOperations.dropTable("TEST"+DerbyTableOperations.META_TABLE_EXT);
        derbyTableOperations.createDerbyMetaTable("TEST",cols);

        ColumnHeadBean[] cols2=derbyTableOperations.getMetaTable("TEST");
        System.out.println("cols2 = " + cols2.length);

    }
}
