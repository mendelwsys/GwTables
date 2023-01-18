package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
import com.mwlib.tablo.db.desc.*;
import com.mwlib.utils.db.DbUtil;
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
public class TestProviders_bu
{

    /*
        Тестирование того что у апдейты приходят правильно по удаленным записям
     */


    /*
    public static void main(String[] args) throws Exception {

        DbUtil.getOracleJdbcConnection();

        long tm = System.currentTimeMillis();

        Map<String, ParamVal> valMap = new HashMap<String, ParamVal>();
        Timestamp maxTimeStamp = getMaxTimeStamp(new HashMap());

        valMap.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp, Types.NULL));
        valMap.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp, Types.TIMESTAMP));

        Map<String, Object> outParams = new HashMap<String, Object>();

        EventProvider eventProvider = new EventProvider()
        {
            protected String getTableTypes()
            {
                return "IN (54,56,59,56,59,51,48,49,50,73,44,68,69,70,71,72) ";
            }

        };
  //      eventProvider.addDorKod(new int[] {28,1});

        Pair<IMetaProvider, Map[]> res = eventProvider._getDbTable(eventProvider.getSQL(eventProvider.getUpdateCorTip(),eventProvider.getTableTypes(),eventProvider.getDorKod()), valMap, outParams, new EventTypeDistributer(new Type2NameMapperAuto(TablesTypes.WINDOWS, new int[]{54, 56, 59}),true), null);

        System.out.println("time = " + (System.currentTimeMillis() - tm)/1000+" length res = "+res.second.length);

    }

     */
    public static void __main(String[] args) throws Exception
    {
        boolean test=false;

        Type2NameMapperAuto mapper = new Type2NameMapperAuto
        (
          new BaseTableDesc[]{
                new WarningInTimeDesc(test),new TechDesc(test),new ViolationDesc(test),new RefuseDesc(test),new WindowsDesc(test), new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test),new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})}
        );

        EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
        TotalEventProvider eventProvider = new TotalEventProvider(metaProvider);
        Map<String, Object> outParams = new HashMap<String, Object>();



        Pair<IMetaProvider, Map[]> resUpdate = null;
        Pair<IMetaProvider, Map[]> delt= null;
        Map<String, ParamVal> valMap = new HashMap<String, ParamVal>();
        Map<Object,Map> testCache = new HashMap<Object,Map>();
        Map<Object,Map> fullCache = new HashMap<Object,Map>();

        for (;;)
        {

            Timestamp maxTimeStamp = EventProvider.getMaxTimeStamp2(outParams);
            valMap.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp, Types.NULL));
            valMap.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp, Types.TIMESTAMP));

            long ln=System.currentTimeMillis();


            Map<String, ParamVal> valMap_begin = new HashMap<String, ParamVal>();
            {
                Timestamp maxTimeStamp_Begin = EventProvider.getMaxTimeStamp2(new HashMap<String, Object>());
                valMap_begin.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp_Begin, Types.NULL));
                valMap_begin.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp_Begin, Types.TIMESTAMP));
            }

            Pair<IMetaProvider, Map[]> resFull = eventProvider.getUpdateTable(valMap_begin, new HashMap<String, Object>());
            if (resFull!=null)
            {
                fullCache.clear();
                for (Map map : resFull.second)
                {
                    Object key=map.get(TablesTypes.KEY_FNAME);
                    fullCache.put(key, map);
                }
            }
            System.out.println("time Full req = " + (System.currentTimeMillis()-ln)/1000);

            if (resUpdate!=null)
                delt=eventProvider.getDeletedTable(valMap, outParams);
            resUpdate = eventProvider.getUpdateTable(valMap, outParams);

            if (delt!=null)
                for (Map map : delt.second)
                {
                    Object key=map.get(TablesTypes.KEY_FNAME);
                    testCache.remove(key);
                }

            if (resUpdate!=null)
                for (Map map : resUpdate.second)
                {
                    Object key=map.get(TablesTypes.KEY_FNAME);
                    testCache.put(key,map);
                }

            //1. Пройтись по testCache удалить от туда все не акутальные записи полученные delt
            //2. testCache  и проверить что все записи там содержаться в fullCahe
            System.out.println("Start checking update method");
            for (Object key : testCache.keySet())
                if (!fullCache.containsKey(key))
                    System.out.println("timeREQ = "+maxTimeStamp.toString()+" key = " + key);
            System.out.println("Finish checking update method");

            System.out.println("time Test req = " + (System.currentTimeMillis()-ln)/1000);
            fullCache.clear();


            Thread.sleep(60*1000 - (System.currentTimeMillis()-ln));
        }
    }

    public static void main(String[] args) throws Exception
    {
        boolean test=false;
        Type2NameMapperAuto mapper = new Type2NameMapperAuto
        (
          new BaseTableDesc[]{
              new DelayABGDDesc(test),new ZMDesc(test),new KMODesc(test),
              new LostTrDesc(test),new VagInTORDesc(test),new VIPGidDesc(test),new DelayGIDTDesc(test),
                new WarningInTimeDesc(test),new TechDesc(test),new ViolationDesc(test),new RefuseDesc(test),new WindowsDesc(test), new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test),new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})}
        );

        EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
        TotalEventProvider eventProvider = new TotalEventProvider(metaProvider);


        String dor_kod=eventProvider.getDorKod();
        String tableTypes=eventProvider.getTableTypes();
        Map<String, Object> outParams = new HashMap<String, Object>();
        Timestamp maxTimeStamp1 = EventProvider.getMaxTimeStamp2(outParams);
//        Timestamp maxTimeStamp2 = EventProvider.getMaxTimeStamp2(outParams);

        String ReqSQL01=

                "select da.data_obj_id,da.cor_time,da.attr_id ,al.attr_type,\n" +
                "da.value_s,da.value_i,da.value_f,da.value_t,da.datatype_id,al.attr_name,al.num"+
            " from \n" +


            "tablo_data_attributes da,\n" +
            "tablo_data_attr_list al  \n" +
            "where al.attr_id=da.attr_id and\n" +
            "al.datatype_id=da.DATATYPE_ID and\n" +
            "da.cor_tip ='D' "+dor_kod+
            "and da.DATATYPE_ID "+tableTypes+" \n" +
            "and da.cor_time> ? \n" +
            "order by da.data_obj_id,da.datatype_id,"+TablesTypes.DOR_CODE;

        String ReqSQL02=
                "select da.data_obj_id,da.cor_time,da.attr_id ,al.attr_type,\n" +
                "da.value_s,da.value_i,da.value_f,da.value_t,da.datatype_id,al.attr_name,al.num"+
            " from \n" +

                "tablo_data_attributes da,\n" +
            "tablo_data_attr_list al  \n" +
            "where al.attr_id=da.attr_id and\n" +
            "al.datatype_id=da.DATATYPE_ID and\n" +
            "da.cor_tip in ('I','U') "+dor_kod+
            "and da.DATATYPE_ID "+tableTypes+" \n" +
            "and da.cor_time> ? \n" +
            "order by da.data_obj_id,da.datatype_id,"+TablesTypes.DOR_CODE;



        String ReqSQL11=

                "select da.data_obj_id,da.cor_time,da.attr_id ,al.attr_type,\n" +
                "da.value_s,da.value_i,da.value_f,da.value_t,da.datatype_id,al.attr_name,al.num "+
                        " from \n" +
            "tablo_data_attributes da,\n" +
            "tablo_data_attr_list al  \n" +
            "where al.attr_id=da.attr_id and\n" +
            "al.datatype_id=da.DATATYPE_ID and\n" +
            "da.cor_tip ='D' "+dor_kod+
            "and da.DATATYPE_ID "+tableTypes+" \n" +
            "and da.cor_time> ? \n" +
            "and da.cor_time<= ? \n" +
            "order by da.data_obj_id,da.datatype_id,"+TablesTypes.DOR_CODE;

        String ReqSQL12=
                "select da.data_obj_id,da.cor_time,da.attr_id ,al.attr_type,\n" +
                "da.value_s,da.value_i,da.value_f,da.value_t,da.datatype_id,al.attr_name,al.num "+
                        " from \n" +

            "tablo_data_attributes da,\n" +
            "tablo_data_attr_list al  \n" +
            "where al.attr_id=da.attr_id and\n" +
            "al.datatype_id=da.DATATYPE_ID and\n" +
            "da.cor_tip in ('I','U') "+dor_kod+
            "and da.DATATYPE_ID "+tableTypes+" \n" +
            "and da.cor_time> ? \n" +
            "and da.cor_time<= ? \n" +
            "order by da.data_obj_id,da.datatype_id,"+TablesTypes.DOR_CODE;


        for (int ix=0;;ix++)
        {

          Timestamp cor_time1=null;
//          Timestamp cor_time2=null;
          int cnt01=0;
          int cnt02=0;
          int cnt11=0;
          int cnt12=0;
//          if (ix>0)
          {
                Connection conn=null;
                PreparedStatement cs = null;
                ResultSet rs = null;
                cor_time1=maxTimeStamp1;
                try
                {

                    conn = DbUtil.getConnection2(DbUtil.DS_ORA_NAME);
                    cs = conn.prepareStatement(ReqSQL01);
                    cs.setObject(1, maxTimeStamp1,Types.TIMESTAMP);
                    rs = cs.executeQuery();
                    if (ix==0)
                        cs.setFetchSize(4000);//производительноть повысилась в 10! раз
                    cnt01=0;
                    while (rs.next())
                    {
                        rs.getString(1);
                        Timestamp cor_time11 = rs.getTimestamp(2);
                        if (cor_time1.before(cor_time11))
                            cor_time1=cor_time11;
                        cnt01++;
                    }



                } finally {
                    DbUtil.closeAll(rs, cs, conn, true);
                }


              Thread.sleep(2000);
            }

//            {
//                Connection conn=null;
//                PreparedStatement cs = null;
//                ResultSet rs = null;
//
//
//                try
//                {
//
//                    conn = DbUtil.getConnection2(DbUtil.DS_ORA_NAME);
//                    cs = conn.prepareStatement(ReqSQL02);
//                    cs.setObject(1, maxTimeStamp2,Types.TIMESTAMP);
//                  //  cs.setFetchSize(4000);//производительноть повысилась в 10! раз
//                    rs = cs.executeQuery();
//
//
//                    cnt02=0;
//                    while (rs.next())
//                    {
//                        rs.getString(1);
//                        Timestamp cor_time22 = rs.getTimestamp(2);
//                        if (cor_time2==null || cor_time2.before(cor_time22))
//                            cor_time2=cor_time22;
//
//                        cnt02++;
//                    }
//
//
//                }
//                finally
//                {
//                  DbUtil.closeAll(rs, cs, conn, true);
//                }
//            }


            if (ix>0)
            {
              {
                Connection conn=null;
                PreparedStatement cs = null;
                ResultSet rs = null;

                try
                {
                    conn = DbUtil.getConnection2(DbUtil.DS_ORA_NAME);

                    cs = conn.prepareStatement(ReqSQL11);
                    cs.setObject(1, maxTimeStamp1,Types.TIMESTAMP);
                    cs.setObject(2, cor_time1,Types.TIMESTAMP);

                    rs = cs.executeQuery();
                    cnt11=0;
                    while (rs.next())
                    {
                        rs.getString(1);
                        cnt11++;
                    }
                    if (cnt01!=cnt11)
                        System.out.println("Found Error for D status "+" cnt01:"+cnt01+" cnt11:"+cnt11);
                }
                finally {
                    DbUtil.closeAll(rs, cs, conn, true);
                }
               }

//                {
//                  Connection conn=null;
//                  PreparedStatement cs = null;
//                  ResultSet rs = null;
//
//                  try
//                  {
//                      conn = DbUtil.getConnection2(DbUtil.DS_ORA_NAME);
//
//                      cs = conn.prepareStatement(ReqSQL12);
//                      cs.setObject(1, maxTimeStamp2,Types.TIMESTAMP);
//                      cs.setObject(2, cor_time2,Types.TIMESTAMP);
//
//                      rs = cs.executeQuery();
//                      cnt12=0;
//                      while (rs.next())
//                      {
//                          rs.getString(1);
//                          cnt12++;
//                      }
//
//                      if (cnt02!=cnt12)
//                          System.out.println("Found Error");
//                  }
//                  finally {
//                      DbUtil.closeAll(rs, cs, conn, true);
//                  }
//                 }

            }

            maxTimeStamp1=cor_time1;
//            maxTimeStamp2=cor_time2;

            System.out.println("cnt01 = " + cnt01+" cnt11 "+ cnt11+"  "+"cnt02 = " + cnt02+" cnt12 "+ cnt12);
        }

    }

    /*

     */

    public static void ___main(String[] args) throws Exception
    {
        boolean test=false;

        Type2NameMapperAuto mapper = new Type2NameMapperAuto
        (
          new BaseTableDesc[]{
                new WarningInTimeDesc(test),new TechDesc(test),new ViolationDesc(test),new RefuseDesc(test),new WindowsDesc(test), new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test),new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})}
        );

        EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
        TotalEventProvider eventProvider = new TotalEventProvider(metaProvider);
        Map<String, Object> outParams = new HashMap<String, Object>();



        Pair<IMetaProvider, Map[]> resUpdate = null;
        Pair<IMetaProvider, Map[]> delt= null;

        Pair<IMetaProvider, Map[]> resUpdate1 = null;
        Pair<IMetaProvider, Map[]> delt1= null;

        Map<String, ParamVal> valMap = new HashMap<String, ParamVal>();


        for (;;)
        {

            Timestamp maxTimeStamp = EventProvider.getMaxTimeStamp2(outParams);
            valMap.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp, Types.NULL));
            valMap.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp, Types.TIMESTAMP));

            if (resUpdate!=null)
                delt=eventProvider.getDeletedTable(valMap, outParams);
            resUpdate = eventProvider.getUpdateTable(valMap, outParams);

            if (delt!=null)
            {
                Thread.sleep(1000);

                delt1=eventProvider.getDeletedTable(valMap, new HashMap<String, Object>());
                resUpdate1 = eventProvider.getUpdateTable(valMap, new HashMap<String, Object>());
                if (
                        delt1.second.length!=delt.second.length ||
                        resUpdate1.second.length!=resUpdate.second.length
                   )
                   System.out.println("Found Error");
            }
            Thread.sleep(3000);
        }
    }


}

