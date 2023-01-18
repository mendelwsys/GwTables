package com.mycompany.client.test.fbuilder;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.03.15
 * Time: 16:56
 * Определитель функции (соответсвует одной строке таблицы)
 */
public class FunctionDet implements IAggregateCalculate
{
    Aggregates aggregate; //Агригативная функция
    String colName; //Имя колонки надо которой вычислиятся функция
    FilterDet filter; //Определитель фильтра

    public FunctionDet(Aggregates aggregate, String colName, FilterDet filter) {
        this.aggregate = aggregate;
        this.colName = colName;
        this.filter = filter;
    }

    @Override
    public Double getValue(Record[] records)
    {
        Record[] rs = filter.applyFilter(records, filter.getCriteria());

        if (aggregate.equals(Aggregates.COUNT))
        {

            if (colName==null || colName.length()==0)
                return (double)rs.length;
            else
            { //
                HashSet<Object> set= new HashSet<Object>();
                for (int i = 0, rsLength = rs.length; i < rsLength; i++)
                    set.add(rs[i].getAttributeAsObject(colName));
                return (double)set.size();
            }
        }


        Double[] res= new Double[rs.length];
        Double min=null,max=null,summ=null;
        for (int i = 0, rsLength = rs.length; i < rsLength; i++)
        {

            res[i]=rs[i].getAttributeAsDouble(colName);
            if (min==null)
                min=res[i];
            else if (res[i]!=null)
                min=Math.min(min,res[i]);

            if (max==null)
                max=res[i];
            else if (res[i]!=null)
                max=Math.max(max,res[i]);

            if (summ==null)
                summ=res[i];
            else if (res[i]!=null)
                summ+=res[i];
        }

        switch (aggregate)
        {
            case MAX:
                return max;
            case MIN:
                return min;
            case SUM:
                return summ;
            case ARG:
                return (summ!=null && rs.length>0)?(summ/rs.length):null;
        }
        return null;
   }
}
