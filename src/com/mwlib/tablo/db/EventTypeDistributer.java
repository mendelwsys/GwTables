package com.mwlib.tablo.db;

import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 30.09.14
 * Time: 13:02
 */
public class EventTypeDistributer implements IMetaProvider {


    @Override
    public ITypes2NameMapper getTypes2NamesMapper() {
        return types2names;
    }

    boolean test=false;

    @Override
    public boolean isTest() {
        return test;
    }

    private ITypes2NameMapper types2names;

    public EventTypeDistributer(
            ITypes2NameMapper types2names,boolean test
    )
    {
        this.types2names = types2names;
        this.test=test;
    }



    public EventTypeDistributer()
    {
        this.types2names = new Type2NameMapperAuto();
    }

    @Override
    public void addColumn2AllTypes(ColumnHeadBean columnHeadBean)
    {
        String[] allNames = types2names.getNames();
        for (String name : allNames)
        {
            Map<String, ColumnHeadBean> col2columns = name2colName2colDesc.get(name);
            if (col2columns==null)
                name2colName2colDesc.put(name,(col2columns =new HashMap<String, ColumnHeadBean>()));
            col2columns.put(columnHeadBean.getName(),columnHeadBean);
        }

    }

    @Override
    public void addColumn2AllTypesIfNotExists(ColumnHeadBean columnHeadBean)
    {

        String[] allNames = types2names.getNames();
        for (String name : allNames)
        {
            Map<String, ColumnHeadBean> col2columns = name2colName2colDesc.get(name);
            if (col2columns==null)
                name2colName2colDesc.put(name,(col2columns =new HashMap<String, ColumnHeadBean>()));

            final String colName = columnHeadBean.getName();
            if (!col2columns.containsKey(colName))
                col2columns.put(colName, columnHeadBean);
        }
    }


    @Override
    public void addColumnByName(String name, ColumnHeadBean columnHeadBean)
    {
        if (name!=null && columnHeadBean!=null)
        {
            Map<String, ColumnHeadBean> col2columns = name2colName2colDesc.get(name);
            if (col2columns==null)
                name2colName2colDesc.put(name,(col2columns =new HashMap<String, ColumnHeadBean>()));
            col2columns.put(columnHeadBean.getName(),columnHeadBean);
        }
    }

    @Override
    public void addColumnByEventType(int data_type, ColumnHeadBean columnHeadBean)
    {
        String[] namesFromType = types2names.getNameFromType(data_type);
        for (String nameFromType : namesFromType)
            addColumnByName(nameFromType,columnHeadBean);
    }

    @Override
    public String[] getEventNames()
    {
        return types2names.getNames();
    }

    @Override
    public Integer[] getEventTypes()
    {
        return types2names.getTypes();
    }

//    @Override
//    public ColumnHeadBean[] getColumnsByEventType(int data_type)
//    {
//        String[] nameFromType = types2names.getNameFromType(data_type);
//
//        return getColumnsByEventName(nameFromType);
//    }

    @Override
    public ColumnHeadBean[] getColumnsByEventName(String name)
    {
        ColumnHeadBean[] res=null;
        if (name !=null)
        {
            Map<String, ColumnHeadBean> col2columns = name2colName2colDesc.get(name);
            if (col2columns!=null)
                return col2columns.values().toArray(new ColumnHeadBean[col2columns.size()]);
        }
        return res;
    }

    private Map<String,Map<String,ColumnHeadBean>> name2colName2colDesc = new HashMap<>();

    @Override
    public void addToMeta(String eventTypeName, ColumnHeadBean[] meta)
    {
        Map<String,ColumnHeadBean> colName2colDesc=new HashMap<String,ColumnHeadBean>();
        for (ColumnHeadBean columnHeadBean : meta)
            colName2colDesc.put(columnHeadBean.getName(),columnHeadBean);
        name2colName2colDesc.put(eventTypeName,colName2colDesc);
    }
    @Override
    public void setObjectAttr(IMetaProvider _metaProvider, ColumnHeadBean columnHeadBean, ResultSet rs, Map<String, Object> _tuple) throws Exception
    {
        int typeId = rs.getInt(TablesTypes.DATATYPE_ID);
        String[] eventsTypeNames = types2names.getNameFromType(typeId);
        if (eventsTypeNames!=null)
            for (String eventTypeName : eventsTypeNames)
            {
                Map<String, ColumnHeadBean> colName2colDesc = name2colName2colDesc.get(eventTypeName);
                if (colName2colDesc==null)
                    name2colName2colDesc.put(eventTypeName,colName2colDesc=new HashMap<String, ColumnHeadBean>());

                if (!colName2colDesc.containsKey(columnHeadBean.getName()))
                    colName2colDesc.put(columnHeadBean.getName(), columnHeadBean);
            }
    }
}
