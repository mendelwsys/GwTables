package com.mwlib.tablo.db;

import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.smartgwt.client.types.ListGridFieldType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 29.09.14
 * Time: 18:22
 * Обращение за событиями в  БД Oracle
 */
public class EventProvider
{

    public static final String DATE_MIN_ND = "DATE_MIN_ND";
//    public static final String DATE_MAX_KD = "DATE_MAX_KD";
    public static final String COR_MAX_TIME = "COR_MAX_TIME";

    public Map<String,ParamVal> getNextUpdateParams(Map<String, Object> outParams)
    {

        Map<String, ParamVal> updateMap = new HashMap<String, ParamVal>();
        Timestamp maxTimeStamp = EventProvider.getMaxTimeStamp2(outParams);
        {
            maxTimeStamp.setTime(maxTimeStamp.getTime()-10000);
            updateMap.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp, Types.NULL));
            updateMap.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp, Types.TIMESTAMP));

        }
        return updateMap;
    }

    protected void setInt2Tuple(ResultSet rs, Map tuple, String id) throws SQLException {
        BigDecimal anInt = rs.getBigDecimal(id);
        if (anInt!=null)
            tuple.put(id, anInt.intValue());
        else
            tuple.put(id, null);
    }

    protected void setDouble2Tuple(ResultSet rs, Map tuple, String id) throws SQLException {
        BigDecimal anDouble = rs.getBigDecimal(id);
        if (anDouble!=null)
            tuple.put(id, anDouble.doubleValue());
        else
            tuple.put(id, null);
    }



    //TODO Изменить запрос, поскольку соьытие может содержать не все данные и соответсвенно мы не получим всю метаинформацию по нему

/*
        TODO Мысль такая - выгребать все события одним запросом
        а дальше раскладывать по кэшам в зависимости от типа события.

        Ожидаемый результат - медленное начальное заполнение кэша
        быстрый апдейт - за один просмотр
*/


    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public EventProvider(String dsName)
    {
        this.dsName=dsName;
    }

    public EventProvider()
    {
    }

    protected String getDelCorTip()
    {
        return "= 'D' ";
    }

    protected String getUpdateCorTip()
    {
        return "IN ('I','U') ";
    }

    protected String getTableTypes()
    {
        return "IN (54,56,59) ";
    }


    private List<Integer> dor_codes = new LinkedList<>();

    public void addDorKod(int[] dor_kods)
    {
        for (int dor_kod : dor_kods)
            dor_codes.add(dor_kod);

    }

    public void removeDorKod(int[] dor_kods)
    {
        for (int dor_kod : dor_kods)
            dor_codes.remove(dor_kod);
    }

    public String getDorKod()
    {
        String res="";
        if (dor_codes.size()>0)
        {
            if (dor_codes.size()==1)
            res+=" and da.dor_kod = "+dor_codes.get(0)+" \n";
            else
            {
                res+=" and da.dor_kod IN (";

                for (int i = 0, dor_codesSize = dor_codes.size(); i < dor_codesSize; i++)
                {
                    Integer dor_code = dor_codes.get(i);

                    res += dor_code;
                    if (i<dor_codesSize-1)
                        res +=",";
                }
                res +=") \n";
            }
        }
        return res;
    }

    public String getSQL(String corTip,String tableTypes,String dor_kod)
    {
        return "select da.data_obj_id,da.cor_time,da.attr_id AS "+ATTR_ID_NM+",al.attr_type as "+ATTR_TYPE_NM+",\n" +
            "da.value_s,da.value_i,da.value_f,da.value_t,da.datatype_id,al.attr_name as "+ATTR_NAME_NM+ ",al.num as "+ NUM_NM +
             ","+TablesTypes.DOR_CODE+" ,da.DATE_ND,da.DATE_KD from \n" +
            "ICG0.tablo_data_attributes da,\n" +
            "ICG0.tablo_data_attr_list al  \n" +
            "where al.attr_id=da.attr_id and\n" +
            "al.datatype_id=da.DATATYPE_ID and\n" +
            "da.cor_tip " +corTip+dor_kod+
            "and da.DATATYPE_ID "+tableTypes+" \n" +
            "and da.cor_time> ? \n" +
            "order by da.data_obj_id,da.datatype_id,"+TablesTypes.DOR_CODE;
    }


    public String getMetaSQL(String tableTypes)
    {
        return "select DATATYPE_ID,NUM,ATTR_ID,ATTR_NAME,ATTR_TYPE from ICG0.tablo_data_attr_list al where DATATYPE_ID "+ tableTypes+
                " order by DATATYPE_ID,NUM";
    }






    public static final int ID_IX = 1;
    public static final int CORTIME_IX = 2;

    public static final String NUM_NM="NUM";
    public static final String ATTR_ID_NM = "ATTR_ID";
    public static final String ATTR_NAME_NM = "ATTR_NAME";
    public static final String ATTR_TYPE_NM = "ATTR_TYPE";

    protected String dsName=DbUtil.DS_ORA_NAME;

    public void fillMetaProvider(String reqSQL, IMetaProvider metaProvider, IRowOperation rowOperation) throws Exception
    {
        BaseTableDesc[] allDesc = metaProvider.getTypes2NamesMapper().getAllTableDesc();
        for (BaseTableDesc baseTableDesc : allDesc)
            baseTableDesc.addMeta2Type(metaProvider); //Заполнение специфическими данными всех таблиц

        Connection conn=null;
        Statement cs = null;
        ResultSet rs = null;

        try
        {
            conn = DbUtil.getConnection2(dsName);
            cs = conn.createStatement();
            rs = cs.executeQuery(reqSQL);
            Map dummy = new HashMap();
            int nRec = 0;
            while (rs.next())
            {
//TODO Для удаления 22.04.2015
//            if (nRec==0)
//                {
////                    metaProvider.setObjectAttr(metaProvider, new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, DbUtils.translate2AttrType("INTEGER"),-1), rs, dummy); //Раньше TablesTypes.DATATYPE_ID создавался при считывании данных, а это не верно!!!!
//                    metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, ListGridFieldType.INTEGER.toString(),-1));
//                    metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.KEY_FNAME, TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),-2));
//                    metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.ACTUAL, TablesTypes.ACTUAL, ListGridFieldType.INTEGER.toString(),-3));
//                }

                String attr_id = rs.getString(ATTR_ID_NM);
                String attr_name = rs.getString(ATTR_NAME_NM);
                String attr_type = rs.getString(ATTR_TYPE_NM);
                int attr_num = rs.getInt(NUM_NM);

                metaProvider.setObjectAttr(metaProvider, new ColumnHeadBean(attr_name, attr_id, DbUtils.translate2AttrType(attr_type),attr_num), rs, dummy);
                nRec++;
            }

//            if (nRec==0)
            {
//                    metaProvider.setObjectAttr(metaProvider, new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, DbUtils.translate2AttrType("INTEGER"),-1), rs, dummy); //Раньше TablesTypes.DATATYPE_ID создавался при считывании данных, а это не верно!!!!
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, ListGridFieldType.INTEGER.toString(),-1));
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.KEY_FNAME, TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),-2));
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.ACTUAL, TablesTypes.ACTUAL, ListGridFieldType.INTEGER.toString(),-3));
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.DATA_OBJ_ID, TablesTypes.DATA_OBJ_ID, ListGridFieldType.TEXT.toString(),-4));

                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(DATE_MIN_ND,DATE_MIN_ND, ListGridFieldType.DATETIME.toString(),-5));
//                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(DATE_MAX_KD, DATE_MAX_KD, ListGridFieldType.DATETIME.toString(),-6));
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(COR_MAX_TIME, COR_MAX_TIME, ListGridFieldType.DATETIME.toString(),-7));
            }



            System.out.println(this.getClass().getName()+" :Meta count of records = " + nRec);

        } finally {
            DbUtil.closeAll(rs, cs, conn, true);
        }

    }


    public Pair<IMetaProvider,Map[]> _getDbTable(String ReqSQL, Map<String, ParamVal> mapParams, Map<String, Object> outParams,IRowOperation rowOperation) throws Exception
    {
        return _getDbTable(ReqSQL, mapParams,outParams, new EventTypeDistributer(), rowOperation);
    }


    public Pair<IMetaProvider,Map[]> _getDbTable(String ReqSQL, Map<String, ParamVal> mapParams, Map<String, Object> outParams, IMetaProvider metaProvider, IRowOperation rowOperation) throws Exception
    {

        Map<String, Map<String, Object>> key2tuple = new HashMap<String, Map<String, Object>>();

        Connection conn=null;
        PreparedStatement cs = null;
        ResultSet rs = null;

        try {
            conn = DbUtil.getConnection2(dsName);

            Timestamp maxTimestamp = (Timestamp) mapParams.get(TablesTypes.MAX_TIMESTAMP).getVal();

            Timestamp minDateND=null;
            Timestamp maxDateKD=null;
            Timestamp maxTupleCorTime=null;


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

                    if (maxTimestamp.before(cor_time))
                        maxTimestamp = cor_time;


                    Map<String, Object> tuple = key2tuple.get(key);
                    if (tuple == null)
                    {
    //                    if (key2tuple.size()>10)
    //                        break;

                        key2tuple.put(key, tuple = new HashMap());

                        metaProvider.setObjectAttr(metaProvider, new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, DbUtils.translate2AttrType("INTEGER"),-1), rs, tuple);
                        tuple.put(TablesTypes.DATATYPE_ID, dataTypeId);
                        tuple.put(TablesTypes.DATA_OBJ_ID, idEvent);

                        maxDateKD=null;
                        minDateND=null;
                        maxTupleCorTime=null;

                    }


                    Timestamp date_nd=rs.getTimestamp("DATE_ND");
                    if (minDateND==null || minDateND.after(date_nd))
                        minDateND=date_nd;

                    Timestamp date_kd=rs.getTimestamp("DATE_KD");
                    if (maxDateKD==null || maxDateKD.before(date_kd))
                        maxDateKD=date_kd;


                    if (maxTupleCorTime==null || maxTupleCorTime.before(cor_time))
                        maxTupleCorTime=cor_time;


                    tuple.put(DATE_MIN_ND,minDateND);
//                    tuple.put(DATE_MAX_KD,maxDateKD);
                    tuple.put(COR_MAX_TIME,maxTupleCorTime);


//                String attr_id = rs.getString(ATTR_ID_IX);
//                String attr_type = rs.getString(ATTR_TYPE_IX);

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
//                if (nRec%2001==0)
//                    System.out.println("nRec = " + nRec);
            } //while rs.next()

            System.out.println(this.getClass().getName()+" :count of records = " + nRec +" tuples:" + key2tuple.size()+" tuples time:"+((System.currentTimeMillis()-lg)/10)*1.0/100);


            metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.KEY_FNAME, TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),-2));
            metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.ACTUAL, TablesTypes.ACTUAL, ListGridFieldType.INTEGER.toString(),-3));

            metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(DATE_MIN_ND,DATE_MIN_ND, ListGridFieldType.DATETIME.toString(),-5));
//            metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(DATE_MAX_KD, DATE_MAX_KD, ListGridFieldType.DATETIME.toString(),-6));
            metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(COR_MAX_TIME, COR_MAX_TIME, ListGridFieldType.DATETIME.toString(),-7));


            Set<String> keys = key2tuple.keySet();
            for (String key : keys)
            {
                Map<String, Object> tuple = key2tuple.get(key);
                tuple.put(TablesTypes.KEY_FNAME, key);
                tuple.put(TablesTypes.ACTUAL, 1);
            }


            Collection<Map<String, Object>> values = key2tuple.values();
            Map[] arr = values.toArray(new Map[values.size()]);
            if (outParams != null)
            {
                outParams.put(TablesTypes.ID_TM, maxTimestamp.getTime());
                outParams.put(TablesTypes.ID_TN, maxTimestamp.getNanos());
            }
            return new Pair<IMetaProvider,Map[]>(metaProvider,arr);

        } finally {
            DbUtil.closeAll(rs, cs, conn, true);
        }
    }


    private static String getSets(Integer[] dor_code, String replaceString) {
        for (int i = 0, dor_codeLength = dor_code.length; i < dor_codeLength; i++) {
            if (i > 0) replaceString += ",";
            replaceString += dor_code[i];
        }
        return replaceString;
    }

    public static long getDefaultTimeStamp() {
        Calendar cl = Calendar.getInstance();
        cl.set(Calendar.YEAR, 1999);
        cl.set(Calendar.MONTH, 12);
        cl.set(Calendar.DAY_OF_MONTH, 31);

        cl.set(Calendar.HOUR, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        return cl.getTimeInMillis();
    }

    public static Timestamp getMaxTimeStamp(Map mapParams) {
        Timestamp maxTimestamp = null;
        try {
            if (mapParams != null) {
                String[] params = (String[]) mapParams.get(TablesTypes.ID_TM);
                if (params != null && params.length > 0)
                    maxTimestamp = new Timestamp(Long.parseLong(params[0]));

                String[] params2 = (String[]) mapParams.get(TablesTypes.ID_TN);
                if (params2 != null && params2.length > 0 && maxTimestamp != null)
                    maxTimestamp.setNanos(Integer.parseInt(params2[0]));
            }
        } catch (NumberFormatException e) {
            //
        }
        if (maxTimestamp == null)
            maxTimestamp = new Timestamp(getDefaultTimeStamp());
        return maxTimestamp;
    }


    public static Timestamp getMaxTimeStamp2(Map<String,Object> mapParams) {
        Timestamp maxTimestamp = null;
        try {
            if (mapParams != null) {
                Long params = (Long) mapParams.get(TablesTypes.ID_TM);
                if (params != null)
                    maxTimestamp = new Timestamp(params);

                Integer params2 = (Integer) mapParams.get(TablesTypes.ID_TN);
                if (params2 != null && maxTimestamp != null)
                    maxTimestamp.setNanos(params2);
            }
        } catch (NumberFormatException e) {
            //
        }
        if (maxTimestamp == null)
            maxTimestamp = new Timestamp(getDefaultTimeStamp());
        return maxTimestamp;
    }



}
