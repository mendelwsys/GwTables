package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;


import java.sql.ResultSet;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 22.04.15
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public class TotalEventProvider extends EventProvider implements IEventProvider
{



    IRowOperation rowOperation;

    @Override
    public IMetaProvider getMetaProvider() {
        return metaProvider;
    }

    IMetaProvider metaProvider;


    protected String getDelCorTip()
    {
        return "= 'X' "; //TODO Для того что бы не возвращать данные
    }

    protected String getUpdateCorTip()
    {
        return "IN ('I','U')";//"IN ('D','I','U') ";
    }


    public String getSQL(String corTip,String tableTypes,String dor_kod)
    {
        return "select da.data_obj_id,da.cor_time,da.attr_id AS "+ATTR_ID_NM+",al.attr_type as "+ATTR_TYPE_NM+",\n" +
            "da.value_s,da.value_i,da.value_f,da.value_t,da.datatype_id,al.attr_name as "+ATTR_NAME_NM+ ",al.num as "+ NUM_NM +
             ","+TablesTypes.DOR_CODE+" from \n" +
            "ICG0.tablo_h_data_attributes da,\n" +
            "ICG0.tablo_data_attr_list al  \n" +
            "where al.attr_id=da.attr_id and\n" +
            "al.datatype_id=da.DATATYPE_ID and\n" +
            "da.cor_tip " +corTip+dor_kod+
            "and da.DATATYPE_ID "+tableTypes+" \n" +
            "and da.cor_time> ? \n  and da.cor_time < TO_TIMESTAMP ('02-02-2015 00:00:00', 'DD-MM-RRRR HH24:MI:SS') " +
            "order by da.data_obj_id,da.datatype_id,"+TablesTypes.DOR_CODE;
    }


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

    public TotalEventProvider(IMetaProvider metaProvider, String dsName)
    {
        super(dsName);
        initIt(metaProvider);


    }
    public TotalEventProvider(IMetaProvider metaProvider)
    {
        initIt(metaProvider);
    }

    /**
     * создать класс для ддоступа к событиям
     * @param metaProvider - интерфейс для распределения событий и запоминания метаинформации к ним
     */
    protected void initIt(IMetaProvider metaProvider)
    {

        //TODO Инициализируем провайдер исходя их метаинформации полученой
        this.metaProvider=metaProvider;
        this.test=metaProvider.isTest();

        rowOperation=new IRowOperation()
        {
            @Override
            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception
            {
                ITypes2NameMapper mapper = metaProvider.getTypes2NamesMapper();
                String[] tblNames=mapper.getNames();
                for (String tblName : tblNames)
                {
                    BaseTableDesc tableDesc = mapper.getTableDescByTblName(tblName);
                    IRowOperation operation=tableDesc.getRowOperation();
                    if (operation!=null)
                        operation.setObjectAttr(metaProvider, attr, rs, tuple);
                }
            }
        };
    }

    boolean test;
    boolean wasMetaInit;

    @Override
    public Pair<IMetaProvider,Map[]> getDeletedTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception
    {
        initMeta();

        String delCorSQL = getSQL(getDelCorTip(), getTableTypes(),getDorKod());
        Pair<IMetaProvider, Map[]> iMetaProviderPair;
        if (metaProvider!=null) {
            iMetaProviderPair = this._getDbTable(delCorSQL, mapParams, outParams, metaProvider, rowOperation);
        }
        else
            iMetaProviderPair = this._getDbTable(delCorSQL,mapParams,outParams, rowOperation);

        if (iMetaProviderPair.second!=null)
            for (Map map : iMetaProviderPair.second)
                map.put(TablesTypes.ACTUAL, 0);//Указание что кортеж не актуален
        return iMetaProviderPair;
    }


    @Override
    public Pair<IMetaProvider,Map[]> getUpdateTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception
    {
//        if (test)
//        {
//            BaseTable table = WindowsT.getInstance(test);
//            Map[] data_upd = table.getRawMap();
//            int[] serv = new int[]{54, 56, 59};
//            for (Map map : data_upd) {
//                map.put(TablesTypes.DATATYPE_ID,serv[((int)(Math.random()*30))%serv.length]);
//            }
//            metaProvider.addToMeta(TablesTypes.WINDOWS,table.getMeta());
//            metaProvider.addToMeta(TablesTypes.WINDOWS+"_E",table.getMeta());
//
//
////            Timestamp maxTimestamp=new Timestamp(FillFromDb.getDefaultTimeStamp());
////            outParams.put(TablesTypes.ID_TM, maxTimestamp.getTime());
////            outParams.put(TablesTypes.ID_TN, maxTimestamp.getNanos());
//
//
//            return new Pair(metaProvider, data_upd);
//        }

        initMeta();
        String updateSQL = getSQL(getUpdateCorTip(), getTableTypes(),getDorKod());
        if (metaProvider!=null)
            return this._getDbTable(updateSQL,mapParams,outParams, metaProvider,rowOperation);
        else
            return this._getDbTable(updateSQL,mapParams,outParams, rowOperation);
    }

    private void initMeta() throws Exception {
        if (!wasMetaInit)
        {//Инициализация метапровайдера
            if (metaProvider!=null)
                fillMetaProvider(this.getMetaSQL(this.getTableTypes()),metaProvider,null);
            wasMetaInit=true;
        }
    }

}
