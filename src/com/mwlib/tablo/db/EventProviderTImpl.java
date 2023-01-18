package com.mwlib.tablo.db;

import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;

import java.sql.ResultSet;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 14:39
 * Провадер событий на инфраструктуре из оракловой БД
 */
public class EventProviderTImpl extends EventProvider implements IEventProvider
{

    IRowOperation rowOperation;

    @Override
    public IMetaProvider getMetaProvider() {
        return metaProvider;
    }

    IMetaProvider metaProvider;


//    public Map<String,ParamVal> getNextUpdateParams(Map<String, Object> outParams)
//    {
//
//        Map<String, ParamVal> updateMap = new HashMap<String, ParamVal>();
//        Timestamp maxTimeStamp = EventProvider.getMaxTimeStamp2(outParams);
//        {
//            maxTimeStamp.setTime(maxTimeStamp.getTime()-10000);
//            updateMap.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp, Types.NULL));
//            updateMap.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp, Types.TIMESTAMP));
//
//        }
//        return updateMap;
//    }


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

    public EventProviderTImpl(IMetaProvider metaProvider,String dsName)
    {
        super(dsName);
        initIt(metaProvider);


    }
    public EventProviderTImpl(IMetaProvider metaProvider)
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
