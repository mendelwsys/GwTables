package com.mycompany.client.dojoChart;

import com.smartgwt.client.data.Record;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 20.05.15
 * Time: 12:24
 * получение значения записи по имени поля
 */
public class GetRecordValByColName  extends AGetRecordVal
{

    protected String colId;

    public GetRecordValByColName(Object idVal,String colId,IFormatValue formatValue, String baseTitle,String chartTitle, String color)
    {
        super(idVal,formatValue,baseTitle,chartTitle,color);
        this.colId=colId;
    }

    @Override
    public Object getRecordsVal(Map<Object, Record> records, ValDef def)
    {
         Record record=records.get(def.getRecRef());
         if (record!=null)
         {
             Object val = record.getAttributeAsObject(colId);
             if (val instanceof Number)
                 val=((Number) val).doubleValue();
             def.setVal(val,getFormatValue(),idVal);
             return val;
         }
         else
         {
             def.removeVal(idVal);
             return null;
         }
    }
}
