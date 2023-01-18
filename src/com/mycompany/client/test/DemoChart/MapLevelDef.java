package com.mycompany.client.test.DemoChart;

import com.mycompany.client.dojoChart.ChartLevelDef;
import com.smartgwt.client.data.Record;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 05.03.15
 * Time: 14:16
 * To change this template use File | Settings | File Templates.
 */
public class MapLevelDef extends ChartLevelDef
{

    private Map<Object, IFilter> val2filetr;


    public MapLevelDef(String colRefName, String colName, String colTextName, Map<Object, IFilter> val2filetr)
    {
        super(colRefName, colName, colTextName);
        this.val2filetr = val2filetr;
    }

    @Override
    public String getGrpViewName(Record record)
    {
        return String.valueOf(calcGrpValue(record));
    }


    @Override
    public Object calcGrpValue(Record record)
    {
        Object defVal=null;
        for (Object rv : val2filetr.keySet())
        {
            final IFilter iFilter = val2filetr.get(rv);
            if (iFilter!=null && iFilter.isAccept(record))
                return rv;
            else if (iFilter==null)
                defVal=rv;
        }
        return defVal;
    }

}
