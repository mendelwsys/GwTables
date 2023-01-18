package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
import com.mwlib.tablo.db.desc.*;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;



import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 15.10.14
 * Time: 12:19
 *
 */
public class TestProviders
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
            public Pair<IMetaProvider,Map[]> _getDbTable(String ReqSQL, Map<String, ParamVal> mapParams, Map<String, Object> outParams, IMetaProvider metaProvider, IRowOperation rowOperation) throws Exception
            {
                Map<String, Map<String, Object>> key2tupleO = new HashMap<String, Map<String, Object>>();
                Timestamp maxTimestampO = (Timestamp) mapParams.get(TablesTypes.MAX_TIMESTAMP).getVal();

                {
                    Connection conn=null;
                    PreparedStatement cs = null;
                    ResultSet rs = null;

                    try
                    {
                        conn = DbUtil.getConnection2(dsName);



                        cs = conn.prepareStatement(ReqSQL);

                        for (ParamVal mapParam : mapParams.values())
                            if (mapParam.getIndex() > 0)
                                cs.setObject(mapParam.getIndex(), mapParam.getVal(), mapParam.getSqlType());

                        long lg=System.currentTimeMillis();

                        cs.setFetchSize(2000);//производительноть повысилась в 10! раз
                        rs = cs.executeQuery();
                        int nRec = 0;
                        while (rs.next())
                        {

                            //if (false) //проверка вермени обработки
                            {
                                String idEvent = rs.getString(ID_IX); //TODO Индекс идентификатора должен быть первым
                                Integer dataTypeId=rs.getInt(TablesTypes.DATATYPE_ID);
                                String key=idEvent+ TablesTypes.KEY_SEPARATOR +dataTypeId.toString();


                                Timestamp cor_time = rs.getTimestamp(CORTIME_IX);//TODO Индекс времени должен быть второй

                                if (maxTimestampO.before(cor_time))
                                    maxTimestampO = cor_time;


                                Map<String, Object> tuple = key2tupleO.get(key);
                                if (tuple == null)
                                {

                                    key2tupleO.put(key, tuple = new HashMap());

                                    metaProvider.setObjectAttr(metaProvider, new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, DbUtils.translate2AttrType("INTEGER"),-1), rs, tuple);
                                    tuple.put(TablesTypes.DATATYPE_ID, dataTypeId);
                                }

                                String attr_id = rs.getString(ATTR_ID_NM);
                                String attr_name = rs.getString(ATTR_NAME_NM);
                                String attr_type = rs.getString(ATTR_TYPE_NM);
                                int attr_num = rs.getInt(NUM_NM);

                                Object att_val = DbUtils.getAttrValByAttrType(rs, attr_type);
                                if (att_val instanceof String && "NULL".equalsIgnoreCase((String) att_val))
                                    att_val = null;
                                if (tuple.containsKey(attr_id))
                                {
                                    System.out.println("attr_id = " + attr_id+" attr_val = " +att_val );
                                }


                                tuple.put(attr_id, att_val);






                                metaProvider.setObjectAttr(metaProvider, new ColumnHeadBean(attr_name, attr_id, DbUtils.translate2AttrType(attr_type),attr_num), rs, tuple);

                                if (rowOperation != null)
                                    rowOperation.setObjectAttr(metaProvider, new ColumnHeadBean(attr_name, attr_id, DbUtils.translate2AttrType(attr_type),attr_num), rs, tuple);
                            }

                            nRec++;
                        } //while rs.next()
                        System.out.println(this.getClass().getName()+" :count of records = " + nRec +" tuples:" + key2tupleO.size()+" tuples time:"+((System.currentTimeMillis()-lg)/10)*1.0/100);
                    } finally {
                        DbUtil.closeAll(rs, cs, conn, true);
                    }
                }

                Thread.sleep(3000);

                {
                    Connection conn=null;
                    PreparedStatement cs = null;
                    ResultSet rs = null;

                    try
                    {
                        conn = DbUtil.getConnection2(dsName);

//                        Timestamp maxTimestamp = (Timestamp) mapParams.get(TablesTypes.MAX_TIMESTAMP).getVal();

                        cs = conn.prepareStatement(ReqSQL);

                        for (ParamVal mapParam : mapParams.values())
                            if (mapParam.getIndex() > 0)
                                cs.setObject(mapParam.getIndex(), mapParam.getVal(), mapParam.getSqlType());

                        long lg=System.currentTimeMillis();

                        cs.setFetchSize(2000);//производительноть повысилась в 10! раз
                        rs = cs.executeQuery();
                        int nRec = 0;
                        while (rs.next())
                        {

                            //if (false) //проверка вермени обработки
                            {
                                String idEvent = rs.getString(ID_IX); //TODO Индекс идентификатора должен быть первым
                                Integer dataTypeId=rs.getInt(TablesTypes.DATATYPE_ID);
                                String key=idEvent+ TablesTypes.KEY_SEPARATOR +dataTypeId.toString();


                                Timestamp cor_time = rs.getTimestamp(CORTIME_IX);//TODO Индекс времени должен быть второй


//                                if (maxTimestamp.before(cor_time))
//                                    maxTimestamp = cor_time;

                                if (cor_time.before(maxTimestampO) || cor_time.equals(maxTimestampO))
                                {
                                    //Должен войти в предыдущий запрос....
                                    Map<String, Object> oldTuple = key2tupleO.get(key);
                                    if (oldTuple==null)
                                    {
                                        throw new Exception("can't find tuple for  key:" + key);
                                    }

                                   String attr_id = rs.getString(ATTR_ID_NM);
                                   String attr_type = rs.getString(ATTR_TYPE_NM);
                                   Object oldVal = oldTuple.get(attr_id);
                                   Object newVal = DbUtils.getAttrValByAttrType(rs, attr_type);

                                    if (newVal instanceof String && "NULL".equalsIgnoreCase((String) newVal))
                                        newVal = null;

                                   if (oldVal!=newVal && (oldVal==null || !oldVal.equals(newVal)))
                                   {
                                        throw new Exception("same tuple has not equals values:" + key+" oldVal="+oldVal+" newVal="+newVal);
                                   }


                                }



//                                Map<String, Object> tuple = new HashMa;
//                                if (tuple == null)
//                                {
//
//                                    key2tuple.put(key, tuple = new HashMap());
//
//                                    metaProvider.setObjectAttr(metaProvider, new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, DbUtils.translate2AttrType("INTEGER"),-1), rs, tuple);
//                                    tuple.put(TablesTypes.DATATYPE_ID, dataTypeId);
//                                }
//
//                                String attr_id = rs.getString(ATTR_ID_NM);
//                                String attr_name = rs.getString(ATTR_NAME_NM);
//                                String attr_type = rs.getString(ATTR_TYPE_NM);
//                                int attr_num = rs.getInt(NUM_NM);
//
//                                Object att_val = DbUtils.getAttrValByAttrType(rs, attr_type);
//                                if (att_val instanceof String && "NULL".equalsIgnoreCase((String) att_val))
//                                    att_val = null;
//                                if (tuple.containsKey(attr_id))
//                                {
//                                    System.out.println("attr_id = " + attr_id+" attr_val = " +att_val );
//                                }
//
//
//                                tuple.put(attr_id, att_val);

//                                metaProvider.setObjectAttr(metaProvider, new ColumnHeadBean(attr_name, attr_id, DbUtils.translate2AttrType(attr_type),attr_num), rs, tuple);
//
//                                if (rowOperation != null)
//                                    rowOperation.setObjectAttr(metaProvider, new ColumnHeadBean(attr_name, attr_id, DbUtils.translate2AttrType(attr_type),attr_num), rs, tuple);
                            }

                            nRec++;
                        } //while rs.next()

                        System.out.println(this.getClass().getName()+" :count of records = " + nRec +" tuples:" + key2tupleO.size()+" tuples time:"+((System.currentTimeMillis()-lg)/10)*1.0/100);
                    } finally {
                        DbUtil.closeAll(rs, cs, conn, true);
                    }
                }



                Collection<Map<String, Object>> values = key2tupleO.values();
                Map[] arr = values.toArray(new Map[values.size()]);
                if (outParams != null)
                {
                    outParams.put(TablesTypes.ID_TM, maxTimestampO.getTime());
                    outParams.put(TablesTypes.ID_TN, maxTimestampO.getNanos());
                }
                return new Pair<IMetaProvider,Map[]>(metaProvider,arr);

            }


        };
        Map<String, Object> outParams = new HashMap<String, Object>();



        Pair<IMetaProvider, Map[]> resUpdate = null;
        Map<String, ParamVal> valMap = new HashMap<String, ParamVal>();



        for (;;)
        {

            Timestamp maxTimeStamp = EventProvider.getMaxTimeStamp2(outParams);
            valMap.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp, Types.NULL));
            valMap.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp, Types.TIMESTAMP));

            outParams.clear();
            resUpdate = eventProvider.getUpdateTable(valMap, outParams);

            Thread.sleep(3000);

        }
    }


}

