package com.mycompany.client.dojoChart;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.02.15
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class GetRecordValByColNameEx  extends GetRecordValByColName
{

    ColumnMeta columnMeta;
    public GetRecordValByColNameEx(Object idVal, ColumnMeta columnMeta, String baseTitle,String chartTitle, String color)
    {
        super(idVal,columnMeta.getColId(),columnMeta,baseTitle,chartTitle,color);
        this.columnMeta=columnMeta;
    }
}
