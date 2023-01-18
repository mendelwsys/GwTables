package com.mwlib.tablo.analit2.pred;

import com.mwlib.tablo.analit2.StateDesc;
import com.mwlib.tablo.db.*;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.GrpDef;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.UtilsData;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.cache.INm2Ix;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.analit2.NNodeBuilder;

import com.mwlib.tablo.derby.pred.ConsolidatorLoader;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 14:39
 *
 */
public class ConsolidateEventProviderTImpl
        implements IEventProvider
{


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


    protected IRowOperation rowOperation;
    protected IMetaProvider metaProvider;


    private IAnalisysDesc desc;
    private Map<String, Integer> key2Number;
    protected StateDesc stateDesc;

    boolean test = true;

    public IMetaProvider getMetaProvider() {
        return metaProvider;
    }

    public static IKeyGenerator getKeyGenrator() throws CacheException
    {
        return new SimpleKeyGenerator(new String[]{"PRED_ID"},new INm2Ix()
        {
            @Override
            public Map<String, Integer> getColName2Ix()
            {
                Map<String, Integer> rv = new HashMap<String, Integer>();
                rv.put("PRED_ID",0);
                return rv;
            }

            @Override
            public Map<Integer, String> getIx2ColName() {
                Map<Integer,String> rv = new HashMap<Integer,String>();
                rv.put(0,"PRED_ID");
                return rv;
            }
        });
    }

    /**
     * создать класс для ддоступа к событиям
     * @param desc -
     * @param test -
     * @throws Exception -
     */
    public ConsolidateEventProviderTImpl(IAnalisysDesc desc,boolean test) throws Exception {
        this.desc = desc;
        this.test=test;
        //UtilsData.getKey2key2Number(desc.getNodes(), "", key2Number = new HashMap<String, Integer>(), 0);
        initMeta();
        this.test=metaProvider.isTest();
        key2Number = stateDesc.getKey2Number();


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



    @Override
    public Pair<IMetaProvider,Map[]> getDeletedTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception
    {
        initMeta();

        Pair<IMetaProvider, Map[]> iMetaProviderPair= new Pair<IMetaProvider, Map[]>(metaProvider,new Map[0]);

        //TODO Удаление в консолидационной таблице так же может иметь место.....

        if (iMetaProviderPair.second!=null)
            for (Map map : iMetaProviderPair.second)
                map.put(TablesTypes.ACTUAL, 0);//Указание что кортеж не актуален
        return iMetaProviderPair;
    }

    @Override
    public Pair<IMetaProvider,Map[]> getUpdateTable(Map<String, ParamVal> mapParams, Map<String, Object> outParams) throws Exception
    {
        initMeta();

        Map<Object,Map<String,Object> > key2Tuple=null;
        IKeyGenerator generator = getKeyGenrator();
        GrpDef[] keyDef = desc.getGrpXHierarchy();
        Map[] data = loader.getData();

        if (data.length>0)
            key2Tuple=new HashMap<Object,Map<String,Object>>();

        int tupleIx=0;
        Map<Object,Map<Integer,List<String>>>  resKey2TupleIX2FilledKey= new HashMap<Object,Map<Integer,List<String>>>();

        for (Map tuple : data)
        {
            Object key = generator.getKeyByTuple(tuple);
            Map<String,Object> resMap=key2Tuple.get(key);
            if (resMap==null)
            {
                key2Tuple.put(key,resMap=new HashMap<String,Object>());
                for (GrpDef grpDef : keyDef)
                {//Инициализация ключевых полей показателей
                    String tid=grpDef.getTid();
                    resMap.put(tid,tuple.get(tid));
                    String head=grpDef.gettColId();
                    if (!tid.equals(head))
                        resMap.put(head,tuple.get(head));
                }



                resMap.put(TablesTypes.KEY_FNAME,key);//TODO !!!!подпорка!!!!
                resMap.put(TablesTypes.DATATYPE_ID,stateDesc.getDataTypes()[0]);//TODO !!!!подпорка!!!!
            }

            try
            {
                List<String> fillKeys = new LinkedList<String>();
                UtilsData.conVertMapByNode("",desc.getNodes(),tuple,resMap,key2Number, fillKeys);
                Map<Integer, List<String>> map = resKey2TupleIX2FilledKey.get(key);
                if (map ==null)
                    resKey2TupleIX2FilledKey.put(key,map = new HashMap<Integer, List<String>>());
                map.put(tupleIx, fillKeys);
            }
            catch (UtilsData.BusyKeyException e)
            {
                Map<Integer, List<String>> tuplesIx = resKey2TupleIX2FilledKey.get(key);//Все данные которые заполняли данный кортеж
                //Найдем в них кортеж с занятым ключом.
                for (Integer _tupleIx : tuplesIx.keySet())
                {
                    int ix=tuplesIx.get(_tupleIx).indexOf(e.getBusyKey());
                    if (ix>=0)
                    {
                        Map tupleConflict = data[_tupleIx];
                        System.out.println("CONFLICT tuples while consolidate in ConsolidateEventProviderTImpl2 tuple1:" + tupleConflict+" current tuple"+tuple+" key conflict:"+key+" "+e.getBusyKey());
                        break;
                    }
                }
//                e.printStackTrace();
            }


//            if (tuple.containsKey(TablesTypes.HIDE_ATTR))
//            {
//                if (resMap.containsKey(TablesTypes.HIDE_ATTR))
//                {
//                    Object o = resMap.get(TablesTypes.HIDE_ATTR);
//                    Object o1 = tuple.get(TablesTypes.HIDE_ATTR);
//                    if (!((o ==null && o1==null) || (o!=null && o.equals(o1))))
//                      throw new Exception("conflict the HIDE key:"+TablesTypes.HIDE_ATTR+" is busy");
//                }
//                resMap.put(TablesTypes.HIDE_ATTR,tuple.get(TablesTypes.HIDE_ATTR));
//            }

            tupleIx++;
        }


        if (key2Tuple!=null)
        {
            for (Map<String, Object> map : key2Tuple.values())
            {
                for (Integer ix : key2Number.values())
                {
                    String key = String.valueOf(ix);
                    if (!map.containsKey(key))
                        map.put(key,null);
                }
            }

            return new Pair(metaProvider,key2Tuple.values().toArray(new Map[key2Tuple.size()]));
        }
        else
            return new Pair(metaProvider,null);
    }



    private ConsolidatorLoader loader;
    private void initMeta() throws Exception
    {
            if (metaProvider==null)
            {//Инициализация метапровайдера

                stateDesc = new StateDesc(desc, test)
                {
                    @Override
                    public String getTableType() {
                        return TablesTypes.STATEDESC;
                    }
                };
                metaProvider = new EventTypeDistributer(new Type2NameMapperAuto(new BaseTableDesc[]{stateDesc}),test);

                metaProvider.addColumnByName(TablesTypes.STATEDESC,new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, DbUtils.translate2AttrType("INTEGER"),-1));
                metaProvider.addColumn2AllTypes(new ColumnHeadBean(TablesTypes.KEY_FNAME, TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),-2));
                metaProvider.addColumn2AllTypes(new ColumnHeadBean(TablesTypes.ACTUAL, TablesTypes.ACTUAL, ListGridFieldType.INTEGER.toString(),-3));


                FieldTranslator[] transl = stateDesc.getFieldTranslator();
                for (FieldTranslator fieldTranslator : transl)
                    metaProvider.addColumn2AllTypes(fieldTranslator.getColumnHeadBean());
                loader = new ConsolidatorLoader(test);
            }
    }

    public static IEventProvider getConsolidateProvider(boolean test) throws Exception
    {
        IAnalisysDesc desc=new NNodeBuilder().xml2Desc(NNodeXML.xml);
        UtilsData.removeEmptyNodes(desc.getNodes());
        return new ConsolidateEventProviderTImpl(desc,test);

    }
}
