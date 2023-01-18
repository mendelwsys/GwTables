package com.mycompany.client.apps.App.api.charts4table;

import com.mycompany.client.dojoChart.AGetRecordVal;
import com.mycompany.client.dojoChart.IFormatValue;
import com.mycompany.client.dojoChart.ValDef;
import com.mycompany.client.test.aggregates.FieldTypeToFunctionMapper;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.SummaryFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 19.05.15
 * Time: 17:10
 * Функция расчета значений графика в зависимости от агрегативной функции
 */
abstract public class CalcGetRecordVal extends AGetRecordVal
{
    abstract public ListGridField getFld();
//    {
//        return _fld;
//    }
//
//    private ListGridField _fld;

//    public CalcGetRecordVal(ListGridField fld,Object idVal, IFormatValue formatValue, String baseTitle,String chartTitle, String color)
//    {
//        super(idVal, formatValue,baseTitle, chartTitle, color);
//        this.fld = fld;
//    }


    public CalcGetRecordVal(Object idVal, IFormatValue formatValue, String baseTitle,String chartTitle, String color)
    {
        super(idVal, formatValue,baseTitle, chartTitle, color);
    }


    @Override
    public Object getRecordsVal(Map<Object, Record> records, ValDef def)
    {
        Map<Object, ValDef> valDefs = def.getChildDef();

        for (ValDef valDef :valDefs.values())
                valDef.reCalcGroups(records);


        String function=null;

        ListGridField fld = getFld();

        if (fld!=null)
        {
            function = fld.getAttribute(TablesTypes.AGGREGATE_FUNCTIONS_TYPES_KEY);//"originalFunction");
            if (function == null) function = fld.getAttribute(TablesTypes.AGGREGATE_CUSTOM_FUNCTIONS_KEY);//"summaryFunction");
        }


        if (function==null)
            return getCount(def, valDefs);
        else
        {
            SummaryFunction customFunction=FieldTypeToFunctionMapper.getCustomSummaryFunctionForType(fld.getType(),SummaryFunctionType.valueOf(function.toUpperCase()));
            if (customFunction!=null)
            {
                List<Record> recs=new LinkedList<Record>();
                for (ValDef valDef : valDefs.values())
                {
                    Double _val= (Double) valDef.getMeanValue(idVal);
                    if (_val!=null)
                    {
                        final Record record;
                        recs.add(record = new Record());
                        record.setAttribute(fld.getName(), _val);
                    }
                    else
                       valDef.removeVal(idVal);
                }
                Object resVal= customFunction.getSummaryValue(recs.toArray(new Record[recs.size()]),fld);
                def.setVal(resVal, formatValue,idVal);
                return resVal;
            }
            else
            {
                if (SummaryFunctionType.SUM.getValue().equals(function))
                    return getSumm(def, valDefs);
                else if (SummaryFunctionType.MAX.getValue().equals(function))
                    return getMax(def, valDefs);
                else if (SummaryFunctionType.MIN.getValue().equals(function))
                    return getMin(def, valDefs);
                else if (SummaryFunctionType.AVG.getValue().equals(function))
                    return getAvg(def, valDefs);
                else if (SummaryFunctionType.MULTIPLIER.getValue().equals(function))
                    return getMult(def, valDefs);
                else
                    return getCount(def,valDefs);

            }

        }
    }

    private Object getSumm(ValDef def, Map<Object, ValDef> valDefs)
    {
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

    private Object getMult(ValDef def, Map<Object, ValDef> valDefs)
    {
        Double resVal=0d;
        for (ValDef valDef : valDefs.values())
        {
            Double _val= (Double) valDef.getMeanValue(idVal);
            if (_val!=null)
               resVal*=_val;
            else
               valDef.removeVal(idVal);
        }
        def.setVal(resVal, formatValue,idVal);
        return resVal;
    }

    private Object getCount(ValDef def, Map<Object, ValDef> valDefs)
    {
        Double resVal=0d;
        for (ValDef valDef : valDefs.values())
        {
            if (!valDef.containsKey(idVal))
               valDef.removeVal(idVal);
            else
            {
                Object _val= valDef.getMeanValue(idVal);
                if (_val!=null)
                {
                  if (_val instanceof Double)
                      resVal+=(Double)_val;
                  else
                      resVal+=1;
                }
            }
        }
        def.setVal(resVal, formatValue,idVal);

        return resVal;
    }


    private Object getMax(ValDef def, Map<Object, ValDef> valDefs)
    {
        Double resVal=null;
        for (ValDef valDef : valDefs.values())
        {
            if (!valDef.containsKey(idVal))
               valDef.removeVal(idVal);
            else
            {
                Double _val= (Double) valDef.getMeanValue(idVal);
                if (_val!=null)
                {
                  if (resVal==null)
                      resVal=_val;
                  else
                      resVal= Math.max(resVal,_val);
                }
            }
        }
        def.setVal(resVal, formatValue,idVal);
        return resVal;
    }

    private Object getMin(ValDef def, Map<Object, ValDef> valDefs)
    {
        Double resVal=null;
        for (ValDef valDef : valDefs.values())
        {
            if (!valDef.containsKey(idVal))
               valDef.removeVal(idVal);
            else
            {
                Double _val= (Double) valDef.getMeanValue(idVal);
                if (_val!=null)
                {
                  if (resVal==null)
                      resVal=_val;
                  else
                      resVal= Math.min(resVal,_val);
                }
            }
        }
        def.setVal(resVal, formatValue,idVal);
        return resVal;
    }

    private Object getAvg(ValDef def, Map<Object, ValDef> valDefs)
    {
        _getAllSubSumm(def,valDefs);
        return def.getMeanValue(idVal);
    }

    private Pair<Double,Integer> _getAllSubSumm(ValDef def,Map<Object, ValDef> valDefs)
    {
        Double avgVal=0d;
        Double resVal=0d;
        int cnt=0;
        for (ValDef valDef : valDefs.values())
        {
            if (!valDef.containsKey(idVal))
               valDef.removeVal(idVal);
            else
            {
                Double _val;
                final Map<Object, ValDef> childDef = valDef.getChildDef();
                if (childDef==null || childDef.size()==0)
                {
                    _val= (Double) valDef.getMeanValue(idVal);
                    if (_val!=null)
                    {
                       resVal+=_val;
                       cnt++;
                    }
                }
                else
                {
                    Pair<Double, Integer> pr = _getAllSubSumm(valDef, childDef);
                    if (pr!=null)
                    {
                        resVal+=pr.first;
                        cnt+=pr.second;
                    }
                }
            }
        }

        if (cnt>0)
            avgVal=resVal/cnt;
        else
            avgVal=null;
        def.setVal(avgVal, formatValue,idVal);
        return new Pair(resVal,cnt);
    }


//    private Object getAvg(ValDef def, Map<Object, ValDef> valDefs)
//    {
//        Double avgVal=0d;
//        int cnt=0;
//        for (ValDef valDef : valDefs.values())
//        {
//            if (!valDef.containsKey(idVal))
//               valDef.removeVal(idVal);
//            else
//            {
//                Object _obj = valDef.getMeanValue(idVal);
//                if (_obj instanceof Pair)
//                {
//                    Pair<Double,Integer> pr = (Pair) _obj;
//                    avgVal+=pr.first;
//                    cnt+=pr.second;
//                }
//                else if (_obj!=null)
//                {
//                   avgVal+=(Double)_obj;
//                   cnt++;
//                }
//            }
//        }
//
//        def.setVal(new Pair<Double,Integer>(avgVal,cnt), formatValue,idVal);
//
//        if (cnt>0)
//            avgVal=avgVal/cnt;
//        else
//            avgVal=null;
//
//        return avgVal;
//    }


}
