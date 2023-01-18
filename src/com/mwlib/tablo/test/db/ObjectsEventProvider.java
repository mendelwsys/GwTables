package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
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
 * Date: 01.10.14
 * Time: 14:39
 * Провадер событий на инфраструктуре из оракловой БД
 * содержит дополнительные запросы для наполнения событий из других оракловых таблиц
 */
public class ObjectsEventProvider implements IEventProvider
{
    private List<Integer> dor_codes = new LinkedList<>();

    public static final int ID_IX = 1;
    public static final int CORTIME_IX = 2;

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


    protected String getDelCorTip()
    {
        return "= 'D' ";
    }

    protected String getUpdateCorTip()
    {
        return "IN ('I','U') ";
    }

    @Override
    public IMetaProvider getMetaProvider() {
        return metaProvider;
    }

    IMetaProvider metaProvider;


    public String getTableTypes()
    {
        BaseTableDesc[] arr = metaProvider.getTypes2NamesMapper().getAllTableDesc();
        StringBuffer rv= new StringBuffer(" IN (");
        for (BaseTableDesc baseTableDesc : arr)
        {
            rv = createStringByTblTypes(rv, baseTableDesc);
        }
        return rv.substring(0,rv.length()-1)+" ) ";
    }

    private StringBuffer createStringByTblTypes(StringBuffer rv, BaseTableDesc baseTableDesc) {
        int[] types=baseTableDesc.getDataTypes();
        for (int type : types) {
            rv=rv.append(type).append(",");
        }
        return rv;
    }

    protected String dsName=DbUtil.DS_ORA_NAME;
    public ObjectsEventProvider(IMetaProvider metaProvider, String dsName)
    {
        this.dsName=dsName;
        initIt(metaProvider);


    }
    public ObjectsEventProvider(IMetaProvider metaProvider)
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

        Map<String, ParamVal> updateMap = new HashMap<String, ParamVal>();
        Timestamp maxTimeStamp = EventProvider.getMaxTimeStamp2(outParams);
        {
            maxTimeStamp.setTime(maxTimeStamp.getTime()-10000);
            updateMap.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp, Types.NULL));
            updateMap.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp, Types.TIMESTAMP));

        }
        return updateMap;
    }

    @Override
    public Pair<IMetaProvider,Map[]> getDeletedTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception
    {
        initMeta();
        return getDataPlaces(getSQL(getDelCorTip(), getTableTypes(), getDorKod()),mapParams, outParams, 0);
    }


    @Override
    public Pair<IMetaProvider,Map[]> getUpdateTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception
    {
        initMeta();
        return getDataPlaces(getSQL(getUpdateCorTip(), getTableTypes(), getDorKod()),mapParams, outParams, 1);
    }

    protected Pair<IMetaProvider, Map[]> getDataPlaces(String placesSQL, Map<String, ParamVal> mapParams, Map<String, Object> outParams, int actual) throws ClassNotFoundException, SQLException
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
            for (ParamVal mapParam : mapParams.values())
                if (mapParam.getIndex() > 0)
                    cs.setObject(mapParam.getIndex(), mapParam.getVal(), mapParam.getSqlType());
            cs.setFetchSize(2000);//производительноть повысилась в 10! раз

            rs = cs.executeQuery();
            int nRec = 0;
            while (rs.next())
            {
                String idEvent = rs.getString(ID_IX); //TODO Индекс идентификатора должен быть первым
                Integer dataTypeId=rs.getInt(TablesTypes.DATATYPE_ID);
                String key=idEvent+ TablesTypes.KEY_SEPARATOR +dataTypeId.toString();//ключ объекта должен быть сформирован так же.


                Map tuple = new HashMap();
                key2tuple.put(key, tuple);
                tuple.put(TablesTypes.DATATYPE_ID, dataTypeId);
                tuple.put(TablesTypes.KEY_FNAME, key);

                setInt2Tuple(rs, tuple, TablesTypes.OBJ_OSN_ID);
                setInt2Tuple(rs, tuple, TablesTypes.PUTGL_ID);
                tuple.put(TablesTypes.ACTUAL, actual);

                Timestamp cor_time = rs.getTimestamp(CORTIME_IX);//TODO Индекс времени должен быть второй
                if (maxTimestamp.before(cor_time))
                    maxTimestamp = cor_time;
                nRec++;
            }

            System.out.println(this.getClass().getName()+" :count of TABLO_DATA_OBJECTS records = " + nRec +" tuples:" + key2tuple.size()+" tuples time:"+((System.currentTimeMillis()-lg)/10)*1.0/100);

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

    protected String getSQL(String corTip, String tableTypes, String dor_kod)
    {
        return "select DATA_OBJ_ID,"+ TablesTypes.CORTIME+",TEXT,"+TablesTypes.DOR_CODE+","+TablesTypes.DATATYPE_ID+" " +
                        "from ICG0.TABLO_DATA_OBJECTS WHERE COR_TIP " + corTip + dor_kod+
                        "and DATATYPE_ID "+tableTypes+" \n" +
                        "and "+TablesTypes.CORTIME+" > ? order by data_obj_id,datatype_id,"+TablesTypes.DOR_CODE;
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


    private void initMeta() throws Exception
    {
        if (!wasMetaInit)
        {//Инициализация метапровайдера
            if (metaProvider!=null)
            {
                BaseTableDesc[] allDesc = metaProvider.getTypes2NamesMapper().getAllTableDesc();
                for (BaseTableDesc baseTableDesc : allDesc)
                    baseTableDesc.addMeta2Type(metaProvider); //Заполнение специфическими данными всех таблиц

                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.TEXT, TablesTypes.TEXT, ListGridFieldType.INTEGER.toString(),-10));
            }
            wasMetaInit=true;
        }
    }

}
