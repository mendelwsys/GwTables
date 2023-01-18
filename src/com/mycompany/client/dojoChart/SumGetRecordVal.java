package com.mycompany.client.dojoChart;

import com.smartgwt.client.data.Record;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.02.15
 * Time: 14:12
 *
 */
public class SumGetRecordVal extends AGetRecordVal
{
    public SumGetRecordVal(Object idVal, IFormatValue formatValue, String baseTitle,String chartTitle, String color)
    {
        super(idVal, formatValue,baseTitle, chartTitle, color);
    }

    @Override
    public Object getRecordsVal(Map<Object, Record> records, ValDef def)
    {
        Map<Object, ValDef> valDefs = def.getChildDef();

        for (ValDef valDef :valDefs.values())
//            if (valDef.isRecalc()) //TODO после отладки раскоментировать для вычисления только изменившихся ветвей
                valDef.reCalcGroups(records);

//        Set forRemove=new HashSet(); //TODO Это не верно, нельзя здесь принять решение о нужности или о не нужности ValDef без перерасчета всех полей, которые могут содержаться в данном ValDef,
// TODO более того если кортеж появляется, значит он содержит хотя бы одно поле
//        for (ValDef valDef : valDefs.values())
//        {
//            if (valDef.getVal().size()==0)
//                forRemove.add(valDef.getGrpValue());
//        }
//
//        for (Object o : forRemove)
//            valDefs.remove(o);

        Double resVal=0d;
        for (ValDef valDef : valDefs.values())
        {
            Double _val= (Double) valDef.getMeanValue(idVal);
            if (_val!=null)
               resVal+=_val;
            else
               valDef.removeVal(idVal);
        }
        def.setVal(resVal, formatValue,idVal);
        return resVal;
    }
}
