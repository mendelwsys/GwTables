package com.mwlib.tablo.analit2.ref12;

import com.mwlib.tablo.analit2.StateDesc;
import com.mwlib.tablo.db.*;
import com.mwlib.tablo.derby.IDataLoader;
import com.mwlib.tablo.derby.ref12.Consolidator4Ref12DataLoader;
import com.mwlib.tablo.tables.AField;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mycompany.common.FieldException;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.ColDef;
import com.mycompany.common.analit2.GrpDef;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.UtilsData;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.cache.INm2Ix;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.analit2.NNodeBuilder;

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
 * провайдер для отказов с начала суток
 */
public class Ref12EventProviderTImpl
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

    public IMetaProvider getMetaProvider() {
        return metaProvider;
    }

    protected IMetaProvider metaProvider;

    private IAnalisysDesc desc;
    protected BaseTableDesc stateDesc;

    private Map<String, Integer> key2Number;

    protected String[] loaderTypes;
    private String dsName;
    protected boolean test = true;


    public static IKeyGenerator getKeyGenrator() throws CacheException
    {
        return new SimpleKeyGenerator(new String[]{TablesTypes.PLACE_ID},new INm2Ix()
        {
            @Override
            public Map<String, Integer> getColName2Ix()
            {
                Map<String, Integer> rv = new HashMap<String, Integer>();
                rv.put(TablesTypes.PLACE_ID,0);
                return rv;
            }

            @Override
            public Map<Integer, String> getIx2ColName() {
                Map<Integer,String> rv = new HashMap<Integer,String>();
                rv.put(0,TablesTypes.PLACE_ID);
                return rv;
            }
        });
    }

    /**
     * создать класс для доступа к событиям
     * @param desc -
     * @param dsName-
     * @param loaderTypes -
     * @param test -
     * @throws Exception -
     */
    protected Ref12EventProviderTImpl(IAnalisysDesc desc, String dsName, String[] loaderTypes, boolean test, IMetaProvider _metaProvider) throws Exception {
        this.desc = desc;
        this.dsName = dsName;
        this.test=test;
        this.loaderTypes=loaderTypes;
        this.metaProvider=_metaProvider;
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
        //Удаление в консолидационной таблице так же может иметь место, но формирование удалямых ключей идет на уровне ServerUpdaterT2 по принципу: те ключи которые отсутвуют в консолидационном множестве - удаляются
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
                resMap.put("NUM",tuple.get("NUM"));//TODO !!!!подпорка!!!!
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
                        map.put(key,0);//Установка нулей
                }
            }

            return new Pair(metaProvider,key2Tuple.values().toArray(new Map[key2Tuple.size()]));
        }
        else
            return new Pair(metaProvider,null);
    }



    private IDataLoader loader;
    private boolean wasInit=false;
    private void initMeta() throws Exception
    {
            if (!wasInit)
            {//Инициализация метапровайдера



                if (metaProvider==null)
                {
                    stateDesc=createBaseTableDesc(desc,test);
                    metaProvider = new EventTypeDistributer(new Type2NameMapperAuto(new BaseTableDesc[]{stateDesc}),test);
                }
                else
                {
                    stateDesc=metaProvider.getTypes2NamesMapper().getTableDescByTblName(getConsTableType());
                    if (stateDesc==null)
                       throw new Exception("Can't find type "+ getConsTableType() +" in meta provider");
                }


                metaProvider.addColumnByName(getConsTableType(),new ColumnHeadBean(TablesTypes.DATATYPE_ID, TablesTypes.DATATYPE_ID, ListGridFieldType.INTEGER.toString(),-1));

                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.KEY_FNAME, TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),-2));
                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(TablesTypes.ACTUAL, TablesTypes.ACTUAL, ListGridFieldType.INTEGER.toString(),-3));

//                metaProvider.addColumn2AllTypes(new ColumnHeadBean(TablesTypes.KEY_FNAME, TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),-2));
//                metaProvider.addColumn2AllTypes(new ColumnHeadBean(TablesTypes.ACTUAL, TablesTypes.ACTUAL, ListGridFieldType.INTEGER.toString(),-3));

//                metaProvider.addColumnByName(TablesTypes.STATEPLACES,new ColumnHeadBean("NUM", "NUM", ListGridFieldType.INTEGER.toString(),-4));


                FieldTranslator[] transl = stateDesc.getFieldTranslator();
                for (FieldTranslator fieldTranslator : transl)
                    metaProvider.addColumnByName(getConsTableType(),fieldTranslator.getColumnHeadBean());

                loader = new Consolidator4Ref12DataLoader(loaderTypes,dsName,test); //TODO Абстрагировать лоадер????
                wasInit=true;
            }
    }

    public static String getConsTableType()
    {
        return TablesTypes.STATEREF12;
    }

    public static BaseTableDesc createBaseTableDesc(IAnalisysDesc desc,boolean test) throws Exception
    {
        return new StateDesc(desc, test)
        {
            protected void initFieldTranslator(List<AField> fields)
            {
                Map<String, ColDef> def = desc.getTupleDef();
                ColDef colDef=def.get("NUM");

                fields.add(
                        new AField(colDef.getColName(),colDef.getTitle(),colDef.getFtype(),colDef.isHide())
                        {
                            @Override
                            public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                            {
                                return tuple.get(name);
                            }
                        }

                );
                super.initFieldTranslator(fields);
            }

            @Override
            public String getTableType() {
                return getConsTableType();
            }

        };
    }



    public static IEventProvider getConsolidateProvider(String dsName,String[] loaderTypes,boolean test) throws Exception
    {
        IAnalisysDesc desc = getDesc();
        return new Ref12EventProviderTImpl(desc,dsName,loaderTypes,test,null);
    }

    public static IEventProvider getConsolidateProvider(String dsName,String[] loaderTypes,boolean test,IMetaProvider metaProvider) throws Exception
    {
        IAnalisysDesc desc=null;
        if (metaProvider!=null)
        {
            BaseTableDesc tbl = metaProvider.getTypes2NamesMapper().getTableDescByTblName(getConsTableType());
            if (tbl!=null && (tbl instanceof StateDesc))
                desc=((StateDesc)tbl).getDesc();
        }
        if (desc==null)
            desc = getDesc();

        return new Ref12EventProviderTImpl(desc,dsName,loaderTypes,test,metaProvider);
    }

    public static IAnalisysDesc getDesc() throws Exception {
        IAnalisysDesc desc=new NNodeBuilder().xml2Desc(Ref12XML.xml);
        UtilsData.removeEmptyNodes(desc.getNodes());
        return desc;
    }
}
