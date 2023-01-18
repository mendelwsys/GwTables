package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
import com.mwlib.tablo.db.desc.*;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;



import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 15.10.14
 * Time: 12:19
 *
 */
public class TestProviders_bun
{

    public static void main(String[] args) throws Exception
    {
        boolean test=false;

        Type2NameMapperAuto mapper = new Type2NameMapperAuto
        (
          new BaseTableDesc[]{
                new WarningInTimeDesc(test),new TechDesc(test),new ViolationDesc(test),new RefuseDesc(test),new WindowsDesc(test), new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test),new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})}
        );

        EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
        EventProviderTImpl eventProvider = new EventProviderTImpl(metaProvider)
        {
            public String getSQL(String corTip,String tableTypes,String dor_kod)
            {
                return "select da.data_obj_id,da.cor_time,da.attr_id AS "+ATTR_ID_NM+",al.attr_type as "+ATTR_TYPE_NM+",\n" +
                    "da.value_s,da.value_i,da.value_f,da.value_t,da.datatype_id,al.attr_name as "+ATTR_NAME_NM+ ",al.num as "+ NUM_NM +
                     ","+TablesTypes.DOR_CODE+" from \n" +
                    "ICG0.tablo_data_attributes da,\n" +
                    "ICG0.tablo_data_attr_list al  \n" +
                    "where al.attr_id=da.attr_id and\n" +
                    "al.datatype_id=da.DATATYPE_ID and\n" +
                    "da.cor_tip " +corTip+dor_kod+
                    "and da.DATATYPE_ID "+tableTypes+" \n" +
                    "and da.cor_time> ?  and da.cor_time<= ? \n" +
                    "order by da.data_obj_id,da.datatype_id,"+TablesTypes.DOR_CODE;
            }

        };
        Map<String, Object> outParams = new HashMap<String, Object>();



        Pair<IMetaProvider, Map[]> resUpdate = null;
        Pair<IMetaProvider, Map[]> delt= null;

        Map<String, ParamVal> valMap = new HashMap<String, ParamVal>();



        Map<Object,Map> cache= new HashMap<Object,Map>();

        br:
        for (;;)
        {

            Timestamp maxTimeStamp = EventProvider.getMaxTimeStamp2(outParams);
            Timestamp maxTimeStamp2= new Timestamp(System.currentTimeMillis()+TablesTypes.DAY_MILS);

            valMap.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp, Types.NULL));
            valMap.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp, Types.TIMESTAMP));
            valMap.put(TablesTypes.CORTIME+"_2", new ParamVal(2, maxTimeStamp2, Types.TIMESTAMP));



            cache.clear();
            outParams.clear();
            resUpdate = eventProvider.getUpdateTable(valMap, outParams);
            for (Map map : resUpdate.second)
                cache.put(map.get(TablesTypes.KEY_FNAME),map);



            Thread.sleep(3000);

            maxTimeStamp2 = EventProvider.getMaxTimeStamp2(outParams);
            valMap.put(TablesTypes.CORTIME+"_2", new ParamVal(2, maxTimeStamp2, Types.TIMESTAMP));

            outParams.clear();
            resUpdate = eventProvider.getUpdateTable(valMap, outParams);
//            if (cache.size()!=resUpdate.second.length)
//            {
//                System.out.println("caches has different size maxTimeStamp = " + maxTimeStamp+" maxTimeStamp2 = " + maxTimeStamp2);
//                break;
//            }

            for (Map newTuple : resUpdate.second)
            {
                Object keyNew = newTuple.get(TablesTypes.KEY_FNAME);
                if (!cache.containsKey(keyNew))
                {
                    System.out.println("caches not contains key "+keyNew+" maxTimeStamp = " + maxTimeStamp+" maxTimeStamp2 = " + maxTimeStamp2);
                    break br;
                }
                Map oldTuple=cache.get(keyNew);
//                if (oldTuple.size()!=newTuple.size())
//                {
//                    System.out.println("tuples has different size oldTuple = " + oldTuple+" newTuple = " + newTuple+" maxTimeStamp = " + maxTimeStamp+" maxTimeStamp2 = " + maxTimeStamp2);
//                    break br;
//                }

                for (Object tupleKey : oldTuple.keySet())
                {
                    Object oldVal = oldTuple.get(tupleKey);
                    Object newVal = newTuple.get(tupleKey);

                    if (oldVal==newVal || (oldVal!=null && oldVal.equals(newVal)))
                        continue;

                    {
                        System.out.println("tuples has different values on key "+tupleKey+" oldTuple = " + oldTuple+" newTuple = " + newTuple+" maxTimeStamp = " + maxTimeStamp+" maxTimeStamp2 = " + maxTimeStamp2);
                        break br;
                    }
                }


                for (Object tupleKey : newTuple.keySet())
                {
                    Object oldVal = oldTuple.get(tupleKey);
                    Object newVal = newTuple.get(tupleKey);

                    if (oldVal==newVal || (oldVal!=null && oldVal.equals(newVal)))
                        continue;

                    {
                        System.out.println("tuples has different values on key "+tupleKey+" oldTuple = " + oldTuple+" newTuple = " + newTuple+" maxTimeStamp = " + maxTimeStamp+" maxTimeStamp2 = " + maxTimeStamp2);
                        break br;
                    }
                }
            }

            Thread.sleep(3000);

        }
    }


}

