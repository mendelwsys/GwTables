package com.mwlib.tablo.test.tables;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mwlib.tablo.db.desc.BabkenDesc;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;

import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.db.EventProvider;
import com.mwlib.tablo.db.IEventProvider;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.ParamVal;
import com.smartgwt.client.types.ListGridFieldType;


import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: NEMO
 * Date: 09.10.19
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
public class BabkenProvider implements IEventProvider
{



    @Override
    public IMetaProvider getMetaProvider() {
        return metaProvider;
    }



    public BabkenProvider(IMetaProvider metaProvider)
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
    IMetaProvider metaProvider;


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


    private void initMeta() throws Exception
    {
        if (!wasMetaInit)
        {//Инициализация метапровайдера
            if (metaProvider!=null)
            {
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.KEY_FNAME, TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),-2));
//                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.DATA_OBJ_ID, TablesTypes.DATA_OBJ_ID, ListGridFieldType.TEXT.toString(),-1));
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, ListGridFieldType.INTEGER.toString(),-1));
//                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.OBJ_OSN_ID, TablesTypes.OBJ_OSN_ID, ListGridFieldType.INTEGER.toString(),-10));
//                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.PUTGL_ID, TablesTypes.PUTGL_ID, ListGridFieldType.INTEGER.toString(),-11));
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.DOR_CODE, TablesTypes.DATATYPE_ID, ListGridFieldType.INTEGER.toString(),-3));
            }
            wasMetaInit=true;
        }
    }


    @Override
    public Pair<IMetaProvider,Map[]> getDeletedTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception
    {
        initMeta();
        return new Pair<IMetaProvider,Map[]>(metaProvider,new Map[0]);
    }

    Timestamp maxTimestamp;

    @Override
    public Pair<IMetaProvider,Map[]> getUpdateTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception
    {
        initMeta();

        final ParamVal maxTimestampVal = mapParams.get(TablesTypes.MAX_TIMESTAMP);
        Timestamp maxTimestamp = (Timestamp) maxTimestampVal.getVal();

        if (maxTimestamp!=null && this.maxTimestamp!=null && this.maxTimestamp.before(maxTimestamp))
        {
            return new Pair<IMetaProvider,Map[]>(metaProvider,new Map[0]);
        }
        else
        {
            BufferedReader fis = new BufferedReader( new FileReader("C:\\PapaWK\\Projects\\JavaProj\\WsReCall\\o.json"));
            String rs = fis.readLine();
            Gson gson = new GsonBuilder().serializeNulls().create();
            Type type = new TypeToken<List<Map<String,String>>>(){}.getType();
            List dataList = gson.fromJson(rs, type);
            Map[] arr = null;
            if (dataList.size()>0)
            {
                maxTimestamp = this.maxTimestamp = new Timestamp(EventProvider.getDefaultTimeStamp());
                arr = (Map[]) dataList.toArray(new Map[dataList.size()]);
                for (int i = 0, arrLength = arr.length; i < arrLength; i++)
                {
                    Map map = arr[i];
                    map.put(TablesTypes.DATATYPE_ID, BabkenDesc.BABKENTYPEID);
//                    map.put(TablesTypes.OBJ_OSN_ID,1);
//                    map.put(TablesTypes.DATA_OBJ_ID,BabkenDesc.BABKENTYPEID);
                    map.put(TablesTypes.KEY_FNAME, i);
                    map.put(TablesTypes.DOR_CODE, 1);
                }

                if (outParams != null)
                {
                    outParams.put(TablesTypes.ID_TM, maxTimestamp.getTime());
                    outParams.put(TablesTypes.ID_TN, maxTimestamp.getNanos());
                }

            }

            return new Pair<IMetaProvider,Map[]>(metaProvider,arr);
        }

    }

}
