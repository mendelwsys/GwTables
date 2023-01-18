package com.mycompany.client.dojoChart;

import com.smartgwt.client.data.Record;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.02.15
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class GetConstRecordVal extends AGetRecordVal
{

    private double val;

    public GetConstRecordVal(Object idVal, IFormatValue formatValue, String baseTitle, String chartTitle, String color,double val)
    {
       super(idVal,formatValue,baseTitle,chartTitle,color);
       this.val=val;
    }

    @Override
    public Object getRecordsVal(Map<Object, Record> records, ValDef def)
    {
     Record record=records.get(def.getRecRef());
     if (record!=null)
     {
         return val;
     }
     else
     {
         def.removeVal(idVal);
         return null;
     }
    }
}
