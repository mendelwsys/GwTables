package com.mycompany.client.test.DemoChart;

import com.smartgwt.client.data.Record;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 05.03.15
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class SpeedFilters
{
    static Map<Object,IFilter> filterMap=new HashMap<Object,IFilter>();
    static
    {
           filterMap.put("15",new Filter15());
           filterMap.put("25",new Filter25());
           filterMap.put("40",new Filter40());
           filterMap.put(">40",new FilterOther());
    }

    public static Map<Object,IFilter> getSpeedFilters()
    {
        return filterMap;
    }

    static class Filter15 implements IFilter
    {
        @Override
        public boolean isAccept(Record record)
        {
            final Integer colorStatus = record.getAttributeAsInt("colorStatus");
            return colorStatus!=null && colorStatus==0;
        }
    }

    static class Filter25 implements IFilter
    {
        @Override
        public boolean isAccept(Record record)
        {
            final Integer colorStatus = record.getAttributeAsInt("colorStatus");
            return colorStatus!=null && colorStatus==1;
        }
    }


    static class Filter40 implements IFilter
    {
        @Override
        public boolean isAccept(Record record)
        {
            final Integer colorStatus = record.getAttributeAsInt("colorStatus");
            return colorStatus!=null && colorStatus==2;
        }
    }

    static class FilterOther implements IFilter
    {
        @Override
        public boolean isAccept(Record record)
        {
            final Integer colorStatus = record.getAttributeAsInt("colorStatus");
            return colorStatus==null || colorStatus==3;
        }
    }

}
