package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.IEventProvider;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.ParamVal;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;

import com.mwlib.tablo.db.desc.UMsgDesc;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 14:39
 * Провадер пользовательскиз сообщений из оракловой БД
 * содержит дополнительные запросы для наполнения событий из других оракловых таблиц
 */
public class UMsgEventProvider implements IEventProvider
{


    public static final String TABLO_ID = "TABLO_ID";
    public static final String ID_GROUP_DEST = "ID_GROUP_DEST";



    @Override
    public IMetaProvider getMetaProvider() {
        return metaProvider;
    }

    IMetaProvider metaProvider;

    private StringBuffer createStringByTblTypes(StringBuffer rv, BaseTableDesc baseTableDesc) {
        int[] types=baseTableDesc.getDataTypes();
        for (int type : types) {
            rv=rv.append(type).append(",");
        }
        return rv;
    }

    protected String dsName=DbUtil.DS_ORA_NAME;
    public UMsgEventProvider(IMetaProvider metaProvider, String dsName)
    {
        this.dsName=dsName;
        initIt(metaProvider);


    }
    public UMsgEventProvider(IMetaProvider metaProvider)
    {
        initIt(metaProvider);
    }

    /**
     * создать класс для ддоступа к событиям
     * @param metaProvider - интерфейс для распределения событий и запоминания метаинформации к ним
     */
    protected void initIt(IMetaProvider metaProvider)
    {
        this.metaProvider=metaProvider;
        this.test=metaProvider.isTest();
    }

    boolean test;
    boolean wasMetaInit;


    public Map<String, ParamVal> getNextUpdateParams(Map<String, Object> outParams)
    {
        return new HashMap<String, ParamVal>();
    }

    @Override
    public Pair<IMetaProvider,Map[]> getDeletedTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception
    {
        throw  new UnsupportedOperationException("Сan't get delete messages for user Event provder");
    }


    @Override
    public Pair<IMetaProvider,Map[]> getUpdateTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception
    {
        initMeta();
        return getActualData(getActualDataSQL(), mapParams, outParams);
    }

    protected Pair<IMetaProvider, Map[]> getActualData(String placesSQL, Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws ClassNotFoundException, SQLException
    {

        Map<Object,Map> key2tuple=new HashMap<Object,Map>();

        long lg=System.currentTimeMillis();

        Connection conn=null;
        PreparedStatement cs = null;
        ResultSet rs = null;

        try
        {
            conn = DbUtil.getConnection2(dsName);

            final ParamVal maxTimestampVal = mapParams.get(TablesTypes.MAX_TIMESTAMP);
            Timestamp maxTimestamp = (Timestamp) maxTimestampVal.getVal();

            cs = conn.prepareStatement(placesSQL);
//            for (ParamVal mapParam : mapParams.values())
//                if (mapParam.getIndex() > 0)
//                    cs.setObject(mapParam.getIndex(), mapParam.getVal(), mapParam.getSqlType());
            cs.setFetchSize(2000);//производительноть повысилась в 10! раз
            rs = cs.executeQuery();
            int nRec = 0;
            while (rs.next())
            {
                Timestamp cor_time = rs.getTimestamp(TablesTypes.CORTIME);
                String idEvent = rs.getString(TABLO_ID)+";"+rs.getString(TablesTypes.DOR_CODE)+";"+rs.getString(UMsgDesc.ID_USER)+";"+Long.toString(cor_time.getTime());

                String key=idEvent+ TablesTypes.KEY_SEPARATOR +TablesTypes.UMSG_DATATYPE_ID;//ключ объекта должен быть сформирован так же.

                Map tuple = new HashMap();
                key2tuple.put(key, tuple);
                tuple.put(TablesTypes.DATATYPE_ID, TablesTypes.UMSG_DATATYPE_ID);
                tuple.put(TablesTypes.DATA_OBJ_ID, idEvent);
                tuple.put(TablesTypes.KEY_FNAME, key);

                tuple.put(TABLO_ID,rs.getInt(TABLO_ID));

                tuple.put(UMsgDesc.DOR_CODE_FROM,rs.getInt(TablesTypes.DOR_CODE));
                tuple.put(TablesTypes.DOR_CODE,rs.getInt(ID_GROUP_DEST));

                tuple.put(UMsgDesc.ID_USER,rs.getInt(UMsgDesc.ID_USER));


                tuple.put(UMsgDesc.MESSAGE_TYPE,rs.getInt(UMsgDesc.MESSAGE_TYPE));
                tuple.put(UMsgDesc.MESSAGE_TEXT,rs.getString(UMsgDesc.MESSAGE_TEXT));
                tuple.put(TablesTypes.CORTIME,cor_time);
                tuple.put(TablesTypes.ACTUAL, 1);

                if (maxTimestamp.before(cor_time))
                    maxTimestamp = cor_time;
                nRec++;
            }

            System.out.println(this.getClass().getName()+" :count of TABLO_USER_MESSAGES records = " + nRec +" tuples:" + key2tuple.size()+" tuples time:"+((System.currentTimeMillis()-lg)/10)*1.0/100);

            Collection<Map> values = key2tuple.values();
            Map[] arr = values.toArray(new Map[values.size()]);
            if (outParams != null)
            {
                outParams.put(TablesTypes.ID_TM, maxTimestamp.getTime());
                outParams.put(TablesTypes.ID_TN, maxTimestamp.getNanos());
            }

            return new Pair<IMetaProvider,Map[]>(metaProvider,arr);
        }
        finally
        {
            DbUtil.closeAll(rs, cs, conn, true);
        }
    }

    protected String getActualDataSQL()
    {
        return "select COR_TIME,TABLO_ID,DOR_KOD,ID_USER,ID_GROUP_DEST,MESSAGE_TYPE,MESSAGE_TEXT,COR_TIP  from ICG0.TABLO_USER_MESSAGES where COR_TIP in ('I','U')";
    }

//    protected void setInt2Tuple(ResultSet rs, Map tuple, String id) throws SQLException {
//        BigDecimal anInt = rs.getBigDecimal(id);
//        if (anInt!=null)
//            tuple.put(id, anInt.intValue());
//        else
//            tuple.put(id, null);
//    }
//
//    protected void setDouble2Tuple(ResultSet rs, Map tuple, String id) throws SQLException {
//        BigDecimal anDouble = rs.getBigDecimal(id);
//        if (anDouble!=null)
//            tuple.put(id, anDouble.doubleValue());
//        else
//            tuple.put(id, null);
//    }


    private void initMeta() throws Exception
    {
        if (!wasMetaInit)
        {//Инициализация метапровайдера
            if (metaProvider!=null)
            {
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.KEY_FNAME, TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),-2));
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.DATA_OBJ_ID, TablesTypes.DATA_OBJ_ID, ListGridFieldType.TEXT.toString(),-1));
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, ListGridFieldType.INTEGER.toString(),-1));
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.ACTUAL, TablesTypes.ACTUAL, ListGridFieldType.INTEGER.toString(), -3));

                final int DATATYPE_ID = TablesTypes.UMSG_DATATYPE_ID;
                metaProvider.addColumnByEventType(DATATYPE_ID,new ColumnHeadBean(TABLO_ID, TABLO_ID, ListGridFieldType.INTEGER.toString(),1));
                metaProvider.addColumnByEventType(DATATYPE_ID,new ColumnHeadBean(TablesTypes.DOR_CODE, TablesTypes.DOR_CODE,ListGridFieldType.INTEGER.toString(),2));
                metaProvider.addColumnByEventType(DATATYPE_ID,new ColumnHeadBean(UMsgDesc.ID_USER,UMsgDesc.ID_USER, ListGridFieldType.INTEGER.toString(),3));
                metaProvider.addColumnByEventType(DATATYPE_ID,new ColumnHeadBean(UMsgDesc.DOR_CODE_FROM, UMsgDesc.DOR_CODE_FROM, ListGridFieldType.INTEGER.toString(),4));
                metaProvider.addColumnByEventType(DATATYPE_ID,new ColumnHeadBean(UMsgDesc.MESSAGE_TYPE, UMsgDesc.MESSAGE_TYPE, ListGridFieldType.INTEGER.toString(),5));
                metaProvider.addColumnByEventType(DATATYPE_ID,new ColumnHeadBean(UMsgDesc.MESSAGE_TEXT, UMsgDesc.MESSAGE_TEXT, ListGridFieldType.TEXT.toString(),6));
                metaProvider.addColumnByEventType(DATATYPE_ID,new ColumnHeadBean(TablesTypes.CORTIME,TablesTypes.CORTIME, ListGridFieldType.DATETIME.toString(),7));
            }
            wasMetaInit=true;
        }
    }
}
