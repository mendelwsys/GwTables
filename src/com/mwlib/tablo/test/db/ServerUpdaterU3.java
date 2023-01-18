package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.ICliManager;
import com.mwlib.tablo.UpdateContainer;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICacheFactory;
import com.mwlib.tablo.cache.ICliUpdater;

import com.smartgwt.client.types.ListGridFieldType;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 13:35
 * Осуществляет периодический опрос источника событий и реализует логику наполнения
 * ими (На основе ServerUpdaterT, добавлена фабрика кешей, далее планируется делать конструкцию на основе конфиг файла)
 */
public class ServerUpdaterU3 implements Runnable {


    public static final int MAXVOL = 120;//Максимальный объем данных допустимый в очереди, если он превышен очередь убираем.
    public static final String PROV = "PROV";

    public boolean isTerminate()
    {
        return terminate;
    }


    public void setTerminate()
    {
        this.terminate = true;
    }


    static public class ProvidersWrapper
    {
        Map<String, Object> outParams_del = new HashMap<String, Object>();
        Map<String, Object> outParams_update = new HashMap<String, Object>();

        Map<String, ParamVal> valMap_del = new HashMap<String, ParamVal>();
        Map<String, ParamVal> valMap_update = new HashMap<String, ParamVal>();

        IEventProvider eventProvider;

        Pair<IMetaProvider, Map[]> l_data_upd1;
        Pair<IMetaProvider, Map[]> l_data_del1;

        Map<String,ICache> localCache;
        ICacheFactory cacheFactory;

        ProvidersWrapper(IEventProvider eventProvider)
        {
            this.eventProvider=eventProvider;
        }

        public boolean isMaster() {
            return isMaster;
        }

        public void setMaster(boolean master) {
            isMaster = master;
        }

        boolean isMaster=false;

    }



    private boolean terminate = false;

    private Map<String, ICache> caches = new HashMap<>(); //Имя в кэш
    private ProvidersWrapper[] eventProviderWrappers;
    private ICliManager cliManager;
    private ICacheFactory cacheFactory;


    public ServerUpdaterU3(ProvidersWrapper[] eventProviderWrappers, ICliManager cliManager, ICacheFactory cacheFactory)
    {
        this.eventProviderWrappers=eventProviderWrappers;
        this.cliManager = cliManager;
        this.cacheFactory=cacheFactory;
    }




    @Override
    public void run()
    {
        try {
            Thread.sleep(2000); //TODO Чего-не заводится дерби без инициализации пула Oracle
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int k = 0;
        boolean test=false;

        if (eventProviderWrappers==null || eventProviderWrappers.length==0)
            return;

        IMetaProvider masterMetaProvider=null;

        while (!terminate)
        {
            long ln = System.currentTimeMillis();
            try {
                String sValDel="";
                String sValUpdate="";
                for (ProvidersWrapper providerWrapper : eventProviderWrappers)
                {

                    if (k==0 && providerWrapper.isMaster())
                    {
                        masterMetaProvider=providerWrapper.eventProvider.getMetaProvider();
                        if (masterMetaProvider!=null)
                            test=masterMetaProvider.isTest();
                    }

                    providerWrapper.valMap_del = new HashMap<String, ParamVal>();
                    providerWrapper.valMap_del = providerWrapper.eventProvider.getNextUpdateParams(providerWrapper.outParams_del);
                    sValDel = providerWrapper.valMap_del.get(TablesTypes.MAX_TIMESTAMP).getVal().toString();
                    System.out.println("ix: " + k + " Request for time parameter:" + sValDel);

                    providerWrapper.valMap_update = new HashMap<String, ParamVal>();
                    providerWrapper.valMap_update = providerWrapper.eventProvider.getNextUpdateParams(providerWrapper.outParams_update);
                    sValUpdate = providerWrapper.valMap_update.get(TablesTypes.MAX_TIMESTAMP).getVal().toString();
                    System.out.println("ix: " + k + " Request for update time parameter:" + sValUpdate);
                }

                Pair<IMetaProvider, Map[]> data_upd1=null;
                Pair<IMetaProvider, Map[]> data_del1=null;

                if (test)
                {
                    data_upd1=new Pair<IMetaProvider, Map[]>(masterMetaProvider,new Map[0]);
                    data_del1=new Pair<IMetaProvider, Map[]>(masterMetaProvider,new Map[0]);
                }
                else
                {
                    long lg = System.currentTimeMillis();
                    {

                        for (int i = 0, eventProviderWrappersLength = eventProviderWrappers.length; i < eventProviderWrappersLength; i++)
                        {
                            ProvidersWrapper providerWrapper = eventProviderWrappers[i];
                            if (k > 0)
                                providerWrapper.l_data_del1 = providerWrapper.eventProvider.getDeletedTable(providerWrapper.valMap_del, providerWrapper.outParams_del);

                            providerWrapper.l_data_upd1 = providerWrapper.eventProvider.getUpdateTable(providerWrapper.valMap_update, providerWrapper.outParams_update);
                            if (providerWrapper.l_data_del1 == null)
                            {
                                providerWrapper.outParams_del.clear();
                                providerWrapper.outParams_del.putAll(providerWrapper.outParams_update);
                                providerWrapper.l_data_del1 = new Pair<IMetaProvider, Map[]>(providerWrapper.l_data_upd1.first, new Map[0]);
                            }

                            final String provName = PROV + "_" + i;
                            if (k == 0)
                                masterMetaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(provName, provName, ListGridFieldType.INTEGER.toString(), -20-i));

                            for (Map tuple : providerWrapper.l_data_del1.second)
                                tuple.put(provName,1);
                            for (Map tuple : providerWrapper.l_data_upd1.second)
                                tuple.put(provName,1);

                            if (!providerWrapper.isMaster())
                            { //Обновим локальные кеши данных

                                final IMetaProvider localMetaProvider = providerWrapper.eventProvider.getMetaProvider();
                                if (providerWrapper.localCache==null)
                                {
                                    providerWrapper.localCache= new HashMap<String,ICache>();
                                    Map<String,Object> params = new HashMap<String,Object>();
                                    String[] eventsName = localMetaProvider.getEventNames();
                                    for (String eventName : eventsName)
                                    {
                                        params.put(ICache.CACHENAME,eventName);
                                        params.put(ICache.TEST,test);
                                        ICache l_cache = providerWrapper.cacheFactory.createCache(params);
                                        l_cache.setMeta(localMetaProvider.getColumnsByEventName(eventName));
                                        l_cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME}, l_cache));
                                        providerWrapper.localCache.put(eventName, l_cache);
                                    }
                                }

                                ITypes2NameMapper type2Name = localMetaProvider.getTypes2NamesMapper();
                                if (providerWrapper.l_data_del1!=null)
                                {

                                    Map<Object, Map> key2tuple = getMapFromArray(providerWrapper.l_data_del1.second);
                                    String[] eventsName = localMetaProvider.getEventNames();
                                    for (String eventName : eventsName)
                                        providerWrapper.localCache.get(eventName).removeAll(key2tuple.keySet());
                                }

                                if (providerWrapper.l_data_upd1!=null)
                                {
                                    Map<String, List<Map>> eventName2DataList_S=new HashMap<String, List<Map>>();
                                    splitUpdatesByTypes(providerWrapper.l_data_upd1, type2Name, eventName2DataList_S);
                                    for (String eventName : eventName2DataList_S.keySet())
                                    {
                                        List<Map> ll = eventName2DataList_S.get(eventName);
                                        providerWrapper.localCache.get(eventName).update(ll.toArray(new Map[ll.size()]),true);
                                    }
                                }
                            }
                            else
                            {
                                data_del1=providerWrapper.l_data_del1;
                                data_upd1=providerWrapper.l_data_upd1;
                            }
                        }

                        if (k==0)
                        { //Инициализируем основной метапровайдер дополнительными полями из других провайдеров
                            for (ProvidersWrapper providerWrapper : eventProviderWrappers)
                            {
                                if (!providerWrapper.isMaster())
                                { //Обновим локальные кеши данных
                                    final IMetaProvider localMetaProvider = providerWrapper.eventProvider.getMetaProvider();
                                    String[] eventsName = localMetaProvider.getEventNames();
                                    for (String eventName : eventsName)
                                    {
                                        ColumnHeadBean[] columns = localMetaProvider.getColumnsByEventName(eventName);
                                        for (ColumnHeadBean column : columns)
                                            masterMetaProvider.addColumnByName(eventName,column);
                                    }
                                }
                            }
                        }

                    }
                    System.out.println(this.getClass().getName() + ": get Update data time = " + (System.currentTimeMillis() - lg) / 1000);
                    test=data_upd1.first.isTest();//Можно на каком-то этапе перебросить в тестовый режим
                }


                masterMetaProvider = data_upd1.first;
                String[] eventNames = masterMetaProvider.getEventNames();
                for (String eventName : eventNames)
                {
                    ICache cache = caches.get(eventName);
                    if (cache == null)
                    {
                        Map<String,Object> params = new HashMap<String,Object>();
                        params.put(ICache.CACHENAME,eventName);
                        params.put(ICache.TEST,test);

                        cache = cacheFactory.createCache(params);

                        ColumnHeadBean[] meta = masterMetaProvider.getColumnsByEventName(eventName);

                        if (!test && (meta ==null || meta.length==0))
                            throw new IllegalStateException("meta data can't not be null");

                        meta=cache.setMeta(meta);

                        cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME}, cache));
                        caches.put(eventName,cache);
                        cliManager.putCacheForName(eventName,new Pair<ICache, BaseTableDesc>(cache,masterMetaProvider.getTypes2NamesMapper().getTableDescByTblName(eventName)));
                    }
                }

                ITypes2NameMapper type2Name = masterMetaProvider.getTypes2NamesMapper();

                Map<String,Map<Object,Map>>  name2KeyDelSet= new HashMap<String,Map<Object,Map>>();
                for (Map map : data_del1.second) //формипрвание списка удаляемых кортежей
                {
                    Integer typeId=(Integer)map.get(TablesTypes.DATATYPE_ID);
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

                        Map<Object,Map> keys4Delete=name2KeyDelSet.get(eventName);
                        if (keys4Delete==null)
                                name2KeyDelSet.put(eventName,keys4Delete=new HashMap());

                        ICache cache = caches.get(eventName);
                        if (cache==null)
                            throw new CacheException("Event Cashes for name "+eventName+" not found");
                        {
                            Object key = cache.getKeyGenerator().getKeyByTuple(map);
                            if (key!=null)
                                keys4Delete.put(key, map);
                            else
                               throw new Exception(this.getClass().getName()+"!!!Generate key error!!!");
                        }
                    }
                }

                for (String typeName : name2KeyDelSet.keySet())
                {
                    Map<Object,Map> objectMap=name2KeyDelSet.get(typeName);
                    if (objectMap!=null)
                        for (Object o : objectMap.keySet())
                            System.out.println("need for delete key = " + o+" for type name "+typeName+" request time:"+sValDel);
                }

                long lg=System.currentTimeMillis();
                Map<String, Map<Object, long[]>> eventName2key2cols = update2Cache(data_upd1, type2Name,name2KeyDelSet);
                System.out.println("tm for update = " + (System.currentTimeMillis()-lg)+" ms  tuples length:"+data_upd1.second.length);




                boolean wasUpdated=false;

                for (Map<Object, long[]> objectMap : eventName2key2cols.values())
                {
                        wasUpdated=wasUpdated || objectMap.size()>0;
                        if (wasUpdated)
                            break;
                }


                for (String eventName : name2KeyDelSet.keySet())
                {
                    ICache cache = caches.get(eventName);
                    if (cache==null)
                        throw new CacheException("Event Cashes for name "+eventName+" not found");
                    Map<Object,Map> keys4Delete = name2KeyDelSet.get(eventName);
                    if (keys4Delete!=null)
                    {
                        Map<Object, long[]> removed = cache.removeAll(keys4Delete.keySet()); //Удаление из кэша всех данных для удаления (ВОЗВРАТ ФАКТИЧЕСКИ УДАЛЕННЫХ)
                        Map<Object, long[]> dataUpdate = eventName2key2cols.get(eventName);
                        if (dataUpdate==null)
                                eventName2key2cols.put(eventName,dataUpdate=new HashMap<Object, long[]>());
                        wasUpdated=wasUpdated || (removed.size()>0);
                        dataUpdate.putAll(removed);
                        for (Object o : keys4Delete.keySet())
                            System.out.println("delete key = " + o+" for type name "+eventName+" request time:"+sValDel);
                    }
                }

                if (!wasUpdated && (data_del1.second.length>0 || data_upd1.second.length>0))
                    System.out.println("skip update Data for delete: "+data_del1.second.length+" Data for update: "+data_upd1.second.length+ " , the cache was not changed: tmDel:"+sValDel+" tmUpdate:"+ sValUpdate);


                //Блок распределения  по кешам клиентов, через манагер апдетов.
                for (String eventName : eventName2key2cols.keySet())
                {

                    ICache cache = caches.get(eventName);
                    Map<Object, long[]> key2cols = eventName2key2cols.get(eventName);
                    if (key2cols.size()!=0)
                    {
                        ICliUpdater[] cashes = cliManager.getCachesForName(eventName);
                        for (ICliUpdater cliCashes : cashes)
                        {
                            cliCashes.updateData(new UpdateContainer(key2cols));
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

            ln = System.currentTimeMillis() - ln;
            k++;

            try
            {
                if (k%17==0)   //TODO ввести режимы тестирования, для того что бы тестировать разныве части
                {
                    System.out.println("\n\n\n\n===================== Check Begin consistency =======================");
                        checkCacheConsistency();
                    System.out.println("===================== Check END consistency =======================\n\n\n\n");
                }
            } catch (Exception e)
            {
                System.out.println("\n\n\n\n===================== Error call check consistency =======================");
                e.printStackTrace();
                System.out.println("\n\n\n\n===================== END Error call check consistency =======================");
            }

            try {
//                if (!test)//TODO ввести режимы тестирования, для того что бы тестировать разныве части
                    Thread.sleep(Math.max(3 * 1000 - ln, 100));
//                else
//                           if (k<1)
//                    Thread.sleep(10 * 1000);//10 second
//                           else
//                               break;
            } catch (InterruptedException e) {//
            }
            doSomeJob();
        }
    }

    private Map<Object, Map> getMapFromArray(Map[] array) {
        Map<Object,Map> key2tuple= new HashMap<Object,Map>();
        for (Map map : array)
            key2tuple.put(map.get(TablesTypes.KEY_FNAME),map);
        return key2tuple;
    }


    protected void doSomeJob()
    {

    }

    private Map<String, Map<Object, long[]>> update2Cache(Pair<IMetaProvider, Map[]> data_upd, ITypes2NameMapper type2Name,Map<String,Map<Object,Map>> eventName2Key4Delete) throws CacheException {

        Map<String,Map<Object, long[]>>  datasUpdate= new HashMap<>();
        { //TODO Полностью переработал поскольку апдейт должен быть массовым что бы избежать доп.расходов на апдейт по одному кортежу


            Map<String,List<Map>>  eventName2DataList_M= new HashMap<String,List<Map>>();
            splitUpdatesByTypes(data_upd, type2Name, eventName2DataList_M);

            Map<String,List<Map>>  eventName2DataList_S= new HashMap<String,List<Map>>();
            for (ProvidersWrapper providerWrapper : eventProviderWrappers)
            {
                if (!providerWrapper.isMaster())
                {
                    splitUpdatesByTypes(providerWrapper.l_data_del1, type2Name, eventName2DataList_S);
                    splitUpdatesByTypes(providerWrapper.l_data_upd1, type2Name, eventName2DataList_S);
                }
            }

            //Апдейтим для всех неосновных данных (на тот случай если)
            for (String eventName : eventName2DataList_S.keySet())
            {
                Map<Object, long[]> dataUpdate=datasUpdate.get(eventName);
                if (dataUpdate==null)
                    datasUpdate.put(eventName,dataUpdate=new HashMap<Object, long[]>());

                ICache cache = caches.get(eventName);
                if (cache==null)
                    throw new CacheException("Event Cashes for name "+eventName+" not found");

                List<Map> maps = eventName2DataList_S.get(eventName);

                Map<Object, long[]> updated= cache.update(maps.toArray(new Map[maps.size()]), false).dataRef;
                checkForRestore(eventName2Key4Delete, eventName, updated);
                setDataChanged(dataUpdate, updated);

            }



            //Апдейтим для всех основных данных
            for (String eventName : eventName2DataList_M.keySet())
            {
                Map<Object, long[]> dataUpdate=datasUpdate.get(eventName);
                if (dataUpdate==null)
                    datasUpdate.put(eventName,dataUpdate=new HashMap<Object, long[]>());

                ICache cache = caches.get(eventName);
                if (cache==null)
                    throw new CacheException("Event Cashes for name "+eventName+" not found");

                List<Map> maps = eventName2DataList_M.get(eventName);

                //Дополняем обновляемые данные из соседних кешей.
                for (Map map : maps)
                {
                    Object key=map.get(TablesTypes.KEY_FNAME);
                    for (ProvidersWrapper providerWrapper : eventProviderWrappers)
                        if (!providerWrapper.isMaster() && providerWrapper.localCache!=null)
                        {
                            final ICache localCache = providerWrapper.localCache.get(eventName);
                            if (localCache!=null)
                            {
                                Object[] tuplePart = localCache.getTupleByKey(key);
                                if (tuplePart!=null)
                                    for (int i = 0, tuplePartLength = tuplePart.length; i < tuplePartLength; i++)
                                    {
                                        String colName=localCache.getColNameByIx(i);
                                        if (colName!=null)
                                            map.put(colName,tuplePart[i]);
                                    }
                            }
                        }
                }

                Map<Object, long[]> updated= cache.update(maps.toArray(new Map[maps.size()]), true).dataRef;
                checkForRestore(eventName2Key4Delete, eventName, updated);
                setDataChanged(dataUpdate, updated);
            }
        }

        return datasUpdate;
    }

    private void checkForRestore(Map<String, Map<Object, Map>> eventName2Key4Delete, String eventName, Map<Object, long[]> updated) {
        for (Object key4update : updated.keySet())
        {
                Map<Object,Map> key4Delete = eventName2Key4Delete.get(eventName);
                if (key4Delete!=null && key4Delete.containsKey(key4update))
                {
                    //Map коtupleDel=key4Delete.remove(key4update);
                    System.out.println("Restored key id " + key4update.toString());//удаление из списка удаляемых событий, т.е. если событие было удалено а потом мы его нащли в актуальныъ событиях
                    System.out.println();//, тогда считаем что событие актуально

//                            Map[] tupleUpdate=data_upd.second;
//                            for (int i = 0; i < tupleUpdate.length; i++) {
//                                if (tupleUpdate[i].get(TablesTypes.KEY_FNAME).equals(key4update))
//                                {
//                                    System.out.println("i = " + i);
//                                }
//                            }
//
//                            if (tupleDel!=null)
//                                System.out.println("tuple4Del = " + tupleDel);
//
                }
        }
    }

    private void setDataChanged(Map<Object, long[]> dataUpdate, Map<Object, long[]> update) {
        //проверка того что у нас изменилось хотя бы одно поле, того добавляем в передаваемые данные
        for (Object key : update.keySet())
        {
            long[] lg = update.get(key);
            for (long l : lg)
            {
                if (l!=0)
                {
                    if (dataUpdate.containsKey(key))
                    {
                        long[] lwas = dataUpdate.get(key);
                        for (int i = 0; i < Math.min(lwas.length,lg.length); i++)
                            lwas[i]|=lg[i];
                        dataUpdate.put(key,lwas);
                    }
                    else
                        dataUpdate.put(key,update.get(key));
                    break;
                }
            }
        }
    }

    private void splitUpdatesByTypes(Pair<IMetaProvider, Map[]> data_upd, ITypes2NameMapper type2Name, Map<String, List<Map>> eventName2DataList) {
        for (Map map : data_upd.second)
        {
            Integer typeId=(Integer)map.get(TablesTypes.DATATYPE_ID);
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
    }



    protected Map<Object,Pair<Map,Integer>> suspectedDelKeys =new HashMap<Object,Pair<Map,Integer>>(); //не удаленные подозрительные ключи
    protected Map<Object,Pair<Map,Integer>> suspectedUpdateKeys =new HashMap<Object,Pair<Map,Integer>>();//не обновленные подозрительные ключи
    protected Map<Object,Pair<Map,Integer>> suspectedValKeys =new HashMap<Object,Pair<Map,Integer>>();//подозрительные ключи, в которых не совпадают значения

    protected void checkCacheConsistency() throws Exception
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



        IEventProvider eventProvider=null;
        for (ProvidersWrapper eventProviderWrapper : eventProviderWrappers)
        {
            if (eventProviderWrapper.isMaster())
            {
                eventProvider=eventProviderWrapper.eventProvider;
                break;
            }
        }

        Pair<IMetaProvider, Map[]> resFull = eventProvider.getUpdateTable(getValBegins(), new HashMap<String, Object>());

        List<Map<Object,Map>> lofKey2Tuple = new LinkedList<Map<Object,Map>>();


        for (ProvidersWrapper eventProviderWrapper : eventProviderWrappers)
        {
            if (!eventProviderWrapper.isMaster())
            {
                Pair<IMetaProvider, Map[]> _resFull=eventProviderWrapper.eventProvider.getUpdateTable(getValBegins(), new HashMap<String, Object>());

                Map<Object,Map> key2Tuple=new HashMap<Object,Map>();
                for (Map map : _resFull.second)
                    key2Tuple.put(map.get(TablesTypes.KEY_FNAME),map);
                lofKey2Tuple.add(key2Tuple);
            }
        }


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
                                    String.valueOf(keyInTuple).startsWith("PROV_")
                                    ||
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
