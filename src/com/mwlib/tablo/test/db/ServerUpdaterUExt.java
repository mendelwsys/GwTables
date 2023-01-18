package com.mwlib.tablo.test.db;

import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICacheFactory;
import com.mwlib.tablo.cache.ICliUpdater;
import com.mwlib.tablo.db.*;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.ICliManager;
import com.mwlib.tablo.UpdateContainer;


import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 13:35
 * Осуществляет периодический опрос источника событий и реализует логику наполнения
 * ими
 */
public class ServerUpdaterUExt implements Runnable, ICascadeUpdater {


    public static final int MAXVOL = 120;//Максимальный объем данных допустимый в очереди, если он превышен очередь убираем.

    public boolean isTerminate()
    {
        return terminate;
    }


    public void setTerminate()
    {
        this.terminate = true;
    }

    private boolean terminate = false;

    private Map<String, ICache> caches = new HashMap<>(); //Имя в кэш
    private IEventProvider eventProvider;
    protected ICliManager cliManager;
    private ICacheFactory cacheFactory;

    public ServerUpdaterUExt(IEventProvider eventProvider, ICliManager cliManager, ICacheFactory cacheFactory)
    {
        this.eventProvider = eventProvider;
        this.cliManager = cliManager;
        this.cacheFactory=cacheFactory;

    }

    @Override
    public String getUpdaterName()
    {
        return eventProvider.getClass().getName();
    }



    boolean test;
    protected IMetaProvider metaProvider;

    int k;
    Map<String, Object> outParams_del;
    Map<String, Object> outParams_update;

    Timestamp maxTimeStamp_del;
    Timestamp maxTimeStamp_update;

    @Override
    public void initStartParams()
    {

        test=false;
        metaProvider = eventProvider.getMetaProvider();
        if (metaProvider!=null)
            test=metaProvider.isTest();



        k = 0;
        outParams_del = new HashMap<String, Object>();
        outParams_update = new HashMap<String, Object>();

        maxTimeStamp_del=null;
        maxTimeStamp_update=null;

    }

    @Override
    public void run()
    {
        try {
            Thread.sleep(2000); //TODO Чего-не заводится дерби без инициализации пула Oracle
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        boolean test=false;
//        IMetaProvider metaProvider = eventProvider.getMetaProvider();
//        if (metaProvider!=null)
//            test=metaProvider.isTest();
//
//
//
//        int k = 0;
//        Map<String, Object> outParams_del = new HashMap<String, Object>();
//        Map<String, Object> outParams_update = new HashMap<String, Object>();
//
//        Timestamp maxTimeStamp_del=null;
//        Timestamp maxTimeStamp_update=null;


        initStartParams();
        while (!terminate)
        {
            long ln = System.currentTimeMillis();
            performUpdate();
            ln = System.currentTimeMillis() - ln;
            try
            {
                    Thread.sleep(Math.max(5 * 1000 - ln, 100));
            } catch (InterruptedException e) {//
            }
        }
    }

    @Override
    public void performUpdate()
    {
        try {

            //Это по идее главный цикл обновления кэшей таблиц.
            Map<String, ParamVal> valMap_del = new HashMap<String, ParamVal>();
            maxTimeStamp_del = EventProvider.getMaxTimeStamp2(outParams_del);
            {
                maxTimeStamp_del.setTime(maxTimeStamp_del.getTime()-10000);
                valMap_del.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp_del, Types.NULL));
                valMap_del.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp_del, Types.TIMESTAMP));
                System.out.println("ix: " + k + " Request for delete time parameter:" + maxTimeStamp_del.toString());
            }

            Map<String, ParamVal> valMap_upd = new HashMap<String, ParamVal>();
            maxTimeStamp_update = EventProvider.getMaxTimeStamp2(outParams_update);
            {
                maxTimeStamp_update.setTime(maxTimeStamp_update.getTime()-10000);
                valMap_upd.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp_update, Types.NULL));
                valMap_upd.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp_update, Types.TIMESTAMP));
                System.out.println("ix: " + k + " Request for update time parameter:" + maxTimeStamp_update.toString());
            }

            Pair<IMetaProvider, Map[]> data_upd1;
            Pair<IMetaProvider, Map[]> data_del1=null;

            if (test)
            {
                data_upd1=new Pair<IMetaProvider, Map[]>(metaProvider,new Map[0]);
                data_del1=new Pair<IMetaProvider, Map[]>(metaProvider,new Map[0]);
            }
            else
            {
                long lg=System.currentTimeMillis();
                if (k>0)
                    data_del1= eventProvider.getDeletedTable(valMap_del, outParams_del);
                data_upd1= eventProvider.getUpdateTable(valMap_upd, outParams_update);
                if (data_del1==null)
                {
                    outParams_del.clear();
                    outParams_del.putAll(outParams_update);
                    data_del1=new Pair<IMetaProvider,Map[]>(data_upd1.first,new Map[0]);
                }
                System.out.println(this.getClass().getName()+": get Update data time = " + (System.currentTimeMillis()-lg)/1000);
                test=data_upd1.first.isTest();//Можно на каком-то этапе перебросить в тестовый режим
            }
            metaProvider = data_upd1.first;

            String[] eventNames = metaProvider.getEventNames();
            for (String eventName : eventNames)
            {
                ICache cache = caches.get(eventName);
                if (cache == null)
                {
                    Map<String,Object> params = new HashMap<String,Object>();
                    params.put(ICache.CACHENAME,eventName);
                    params.put(ICache.TEST,test);

                    cache = cacheFactory.createCache(params);
                    if (cache==null)
                        continue;

                    ColumnHeadBean[] meta = getCacheColumns(eventName);

                    if (!test && (meta ==null || meta.length==0))
                        throw new IllegalStateException("meta data can't not be null");

                    meta=cache.setMeta(meta);
                    cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME}, cache));
                    caches.put(eventName,cache);
                    put2CliManager(eventName, cache);
                }
            }

            ITypes2NameMapper type2Name = metaProvider.getTypes2NamesMapper();

            Map<String,Set<Object>>  name2KeyDelSet= new HashMap<>();
            for (Map map : data_del1.second) //формипрвание списка удаляемых кортежей
            {
                Integer typeId = getTupleTypeId(map);
                if (typeId==null)
                {
                    //TODO Ошибка в лог.
                    continue;
                }

                String[] eventsForTypeNames=type2Name.getNameFromType(typeId);
                if (eventsForTypeNames ==  null)
                {
                    //TODO Ошибка в лог.
                    continue;
                }

                for (String eventName : eventsForTypeNames)
                {
                    Set<Object> keys4Delete=name2KeyDelSet.get(eventName);
                    if (keys4Delete==null)
                            name2KeyDelSet.put(eventName,keys4Delete=new HashSet());

                    ICache cache = caches.get(eventName);
                    if (cache==null)
                        throw new CacheException("Event Cashes for name "+eventName+" not found");
                    {
                        Object key = cache.getKeyGenerator().getKeyByTuple(map);
                        if (key!=null)
                            keys4Delete.add(key);
                        else
                           throw new Exception(this.getClass().getName()+"!!!Generate key error!!!");
                    }
                }
            }

            for (String typeName : name2KeyDelSet.keySet())
            {
                Set<Object> objectMap=name2KeyDelSet.get(typeName);
                if (objectMap!=null)
                    for (Object o : objectMap)
                        System.out.println("need for delete key = " + o+" for type name "+typeName+" request time:"+maxTimeStamp_del.toString());
            }

            long lg=System.currentTimeMillis();
            Map<String, UpdateContainer> eventName2Updater = update2Cache(data_upd1, type2Name,name2KeyDelSet);
            System.out.println("tm for update = " + (System.currentTimeMillis()-lg)+" ms  tuples length:"+data_upd1.second.length);




            boolean wasUpdated=false;

            for (UpdateContainer objectMap : eventName2Updater.values())
                    wasUpdated=wasUpdated || objectMap.dataRef.size()>0;


            for (String eventName : name2KeyDelSet.keySet())
            {
                ICache cache = caches.get(eventName);
                if (cache==null)
                    throw new CacheException("Event Cashes for name "+eventName+" not found");
                Set<Object> keys4Delete = name2KeyDelSet.get(eventName);
                if (keys4Delete!=null)
                {
                    Map<Object, long[]> removed = cache.removeAll(keys4Delete); //Удаление из кэша всех данных для удаления (ВОЗВРАТ ФАКТИЧЕСКИ УДАЛЕННЫХ)
                    UpdateContainer dataUpdate = eventName2Updater.get(eventName);
                    if (dataUpdate==null)
                            eventName2Updater.put(eventName,dataUpdate=new UpdateContainer());
                    wasUpdated=wasUpdated || (removed.size()>0);
                    dataUpdate.dataRef.putAll(removed);
                    for (Object o : keys4Delete)
                        System.out.println("delete key = " + o+" for type name "+eventName+" request time:"+maxTimeStamp_del.toString());
                }
            }

            if (!wasUpdated && (data_del1.second.length>0 || data_upd1.second.length>0))
                System.out.println("skip update Data for delete: "+data_del1.second.length+" Data for update: "+data_upd1.second.length+ " , the cache was not changed: tmDel:"+maxTimeStamp_del+" tmUpdate:"+ maxTimeStamp_update);


            //Блок распределения  по кешам клиентов, через манагер апдетов.
            for (String eventName : eventName2Updater.keySet())
            {
                //TODO Обеспечить одновременный доступ
                ICache cache = caches.get(eventName);
                UpdateContainer updateContainer = eventName2Updater.get(eventName);
                if (updateContainer.dataRef.size()!=0)
                {
                    ICliUpdater[] cashes = getCliCacheforName(eventName);
                    for (ICliUpdater cliCashes : cashes)
                    {
                        cliCashes.updateData(updateContainer);
                        if (cache.size()==0)
                            continue;
                        int size = cliCashes.getQueueVolume() * 100 / cache.size();
                        System.out.println("T sizeV = " + size);
                        if (size > MAXVOL)
                            cliManager.removeCache(cliCashes);
                    }
                }
            }

            cliManager.removeDeadSessions(); //TODO Включить после проверки.

        } catch (Exception e) {
            e.printStackTrace();
        }

        k++;

//            try {
//                if (k%17==0)   //TODO ввести режимы тестирования, для того что бы тестировать разныве части
//                {
//                    System.out.println("\n\n\n\n===================== Check Begin consistency =======================");
//                    checkCacheConsistency(maxTimeStamp_update,maxTimeStamp_del);
//                    System.out.println("===================== Check END consistency =======================\n\n\n\n");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        doSomeJob(maxTimeStamp_update,maxTimeStamp_del,EventProvider.getMaxTimeStamp2(outParams_update),EventProvider.getMaxTimeStamp2(outParams_del));
    }

    protected ICliUpdater[] getCliCacheforName(String eventName) {
        return cliManager.getCachesForName(eventName);
    }

    protected void put2CliManager(String eventName, ICache cache) {
        cliManager.putCacheForName(eventName,new Pair<ICache, BaseTableDesc>(cache,metaProvider.getTypes2NamesMapper().getTableDescByTblName(eventName)));
    }

    protected ColumnHeadBean[] getCacheColumns(String eventName) {
        return metaProvider.getColumnsByEventName(eventName);
    }

    protected Integer getTupleTypeId(Map tuple)
    {
        return (Integer)tuple.get(TablesTypes.DATATYPE_ID);
    }

    protected void doSomeJob(Timestamp maxTimeStamp_update,Timestamp maxTimeStamp_delete,Timestamp nextMaxTimeStamp_update,Timestamp nextMaxTimeStamp_delete)
    {

    }

    private Map<String, UpdateContainer > update2Cache(Pair<IMetaProvider, Map[]> data_upd, ITypes2NameMapper type2Name,Map<String,Set<Object>> eventName2Key4Delete) throws CacheException {

        Map<String,UpdateContainer>  datasUpdate= new HashMap<String,UpdateContainer>();
        { //TODO Полностью переработал поскольку апдейт должен быть массовым что бы избежать доп.расходов на апдейт по одному кортежу
            Map<String,List<Map>>  eventName2DataList= new HashMap<String,List<Map>>();
            for (Map map : data_upd.second)
            {

                Integer typeId = getTupleTypeId(map);
                if (typeId==null)
                {
                    //TODO Ошибка в лог.
                    continue;
                }

                String[] eventsForTypeNames=type2Name.getNameFromType(typeId);
                if (eventsForTypeNames ==  null)
                {
                    //TODO Ошибка в лог.
                    continue;
                }

                for (String eventName : eventsForTypeNames)
                {
                    List<Map> dataUpdate=eventName2DataList.get(eventName);
                    if (dataUpdate==null)
                            eventName2DataList.put(eventName,dataUpdate=new LinkedList<Map>());
                    dataUpdate.add(map);
                }
            }

            for (String eventName : eventName2DataList.keySet())
            {
                ICache cache = caches.get(eventName);
                if (cache==null)
                    throw new CacheException("Event Cashes for name "+eventName+" not found");

                List<Map> maps = eventName2DataList.get(eventName);
                UpdateContainer update= cache.update(maps.toArray(new Map[maps.size()]), true);
                for (Object key4update : update.dataRef.keySet())
                {
                        Set<Object> key4Delete = eventName2Key4Delete.get(eventName);
                        if (key4Delete!=null && key4Delete.remove(key4update))
                        {
                            System.out.println("Restored key id " + key4update.toString());//удаление из списка удаляемых событий, т.е. если событие было удалено а потом мы его нащли в актуальныъ событиях
                            System.out.println();//, тогда считаем что событие актуально
                        }
                }

                //проверка того что у нас изменилось хотя бы одно поле, того добавляем в передаваемые данные
                Set<Object> updateDataKeys = new HashSet<Object>(update.dataRef.keySet());

                for (Object key : updateDataKeys)
                {
                    br:
                    {

                        long[] lg = update.dataRef.get(key);
                        for (long l : lg)
                        {
                            if (l!=0)
                            {
//                                dataUpdate.put(key,update.get(key));
                                break br;
                            }
                        }
                        update.dataRef.remove(key);
                        System.out.println("skip update for key = " + key+" lg:" + ((lg!=null && lg.length>0)?lg[0]:" no "));
                    }
                }

                if (update.dataRef.size()>0)
                {
                    if (datasUpdate.containsKey(eventName))
                        System.out.println("Error of construct updates for eventType:" + eventName);
                    datasUpdate.put(eventName,update);
                }
            }
        }

        return datasUpdate;
    }




    protected Map<Object,Pair<Map,Integer>> suspectedDelKeys =new HashMap<Object,Pair<Map,Integer>>(); //не удаленные подозрительные ключи
    protected Map<Object,Pair<Map,Integer>> suspectedUpdateKeys =new HashMap<Object,Pair<Map,Integer>>();//не обновленные подозрительные ключи
    protected Map<Object,Pair<Map,Integer>> suspectedValKeys =new HashMap<Object,Pair<Map,Integer>>();//подозрительные ключи, в которых не совпадают значения

    protected void checkCacheConsistency(Timestamp maxTimeStamp_update,Timestamp maxTimeStamp_delete) throws Exception
    {

        if (suspectedDelKeys.size()>0)
        {
            Set<Object> suspectedKeys = new HashSet<Object>(suspectedDelKeys.keySet());
            for (Object suspectedKey : suspectedKeys)
            {
                br:
                {
                    for (ICache cache : caches.values())
                    {
                        Set<Object> keys = cache.getAllDataKeys();
                        if (keys.contains(suspectedKey))
                            break br;
                    }
                    suspectedDelKeys.remove(suspectedKey);
                }
            }
        }

        if (suspectedUpdateKeys.size()>0)
        {
            Set<Object> suspectedKeys = new HashSet<Object>(suspectedUpdateKeys.keySet());
            for (Object suspectedKey : suspectedKeys)
            {
                for (ICache cache : caches.values())
                {
                    Set<Object> keys = cache.getAllDataKeys();
                    if (keys.contains(suspectedKey))
                    {
                        suspectedUpdateKeys.remove(suspectedKey);
                        break;
                    }
                }
            }
        }



        if (suspectedValKeys.size()>0)
        {
            Set<Object> suspectedKeys = new HashSet<Object>(suspectedValKeys.keySet());
            for (Object suspectedKey : suspectedKeys)
            {
                br:
                {
                    for (ICache cache : caches.values())
                    {
                        Set<Object> keys = cache.getAllDataKeys();
                        if (keys.contains(suspectedKey))
                            break br;
                    }

                    if (suspectedValKeys.get(suspectedKey).second<4)
                        suspectedValKeys.remove(suspectedKey);//Считаем что это нормально, когда кортежи перед удалением не совпадают по значению
                }
            }
        }




        Pair<IMetaProvider, Map[]> resFull = eventProvider.getUpdateTable(getValBegins(), new HashMap<String, Object>());

        List<Map<Object,Map>> lofKey2Tuple = new LinkedList<Map<Object,Map>>();


        Map<Object,Map> fullCache = new HashMap<Object,Map>();
        if (resFull!=null)
        {
            fullCache.clear();
            for (Map map : resFull.second)
            {
                Object key=map.get(TablesTypes.KEY_FNAME);
                for (Map<Object, Map> objectMapMap : lofKey2Tuple)
                {
                    Map addTuple = objectMapMap.get(key);
                    if (addTuple!=null)
                        map.putAll(addTuple);
                }
                fullCache.put(key, map);
            }
        }

//проверка того что ключи в кеше не содержат удаленные ключи
        for (String typename : caches.keySet())
        {
            ICache cache = caches.get(typename);
            Set<Object> keys = cache.getAllDataKeys();
            for (Object key : keys)
            {
                if (!fullCache.containsKey(key))
                { //кеш содержит ключ который не содержится в полном апдейте
                    add2Suspect(cache, key, suspectedDelKeys);
                }
            }
        }


//проверка того что все апдейты пришли в кеш
        Set inCaches = new HashSet();
        for (String typename : caches.keySet())
        {
            ICache cache = caches.get(typename);
            Set<Object> keys = cache.getAllDataKeys();
            inCaches.addAll(keys);
        }


        Set<Object> updateKeys = fullCache.keySet();
        for (Object updateKey : updateKeys)
        {
            if (!inCaches.contains(updateKey))
                add2Suspect(updateKey,suspectedUpdateKeys,fullCache.get(updateKey));
        }


//проверка того что данные сопадают.
        for (String typename : caches.keySet())
        {
            ICache cache = caches.get(typename);
            Set<Object> keys = cache.getAllDataKeys();
            for (Object key : keys)
            {
                if (fullCache.containsKey(key))
                {
                    Map cacheTuple = getMapByKey(cache, key);
                    Map fullTuple = fullCache.get(key);

                    br:
                    {
                        for (Object keyInTuple : cacheTuple.keySet())
                        {
                            Object valInCache=cacheTuple.get(keyInTuple);
                            Object valInFull=fullTuple.get(keyInTuple);

                            if (
                                    TablesTypes.ACTUAL.equals(keyInTuple)
                                    ||
                                    (valInCache==null && valInFull==null)
                                            ||
                                    (valInCache!=null && valInCache.equals(valInFull))
                               )
                               continue;
                              add2Suspect(cache,key,suspectedValKeys);
                              break br;
                        }
                        suspectedValKeys.remove(key);//Удаление если внесено - все значения равны
                    }
                }
            }
        }

        fullCache.clear();
    }


    private Map<String, ParamVal> getValBegins() {
        Map<String, ParamVal> valMap_begin = new HashMap<String, ParamVal>();
        {
            Timestamp maxTimeStamp_Begin = EventProvider.getMaxTimeStamp2(new HashMap<String, Object>());
            valMap_begin.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp_Begin, Types.NULL));
            valMap_begin.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp_Begin, Types.TIMESTAMP));
        }
        return valMap_begin;
    }

    private void add2Suspect(ICache cache, Object key, Map<Object, Pair<Map, Integer>> suspectedKeys) throws CacheException
    {
        Map tuple = getMapByKey(cache, key);
        add2Suspect(key, suspectedKeys, tuple);
    }

    private void add2Suspect(Object key, Map<Object, Pair<Map, Integer>> suspectedKeys, Map tuple) {
        if (!suspectedKeys.containsKey(key))
            suspectedKeys.put(key, new Pair<Map, Integer>(tuple, 1));
        else
        {
            final Pair<Map,Integer> pair= suspectedKeys.get(key);
            pair.first=tuple;
            pair.second+=1;
        }
    }

    private Map getMapByKey(ICache cache, Object key) throws CacheException {
        Map tuple=new HashMap();
        Object[] otuple=cache.getTupleByKey(key);
        for (int i = 0; i < otuple.length; i++)
        {
            final String colNameByIx = cache.getColNameByIx(i);
            if (colNameByIx!=null)
                tuple.put(colNameByIx,otuple[i]);
        }
        return tuple;
    }


}
