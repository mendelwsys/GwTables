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

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 13:35
 * Осуществляет периодический опрос источника событий и реализует логику наполнения
 * ими (На основе ServerUpdaterT, добавлена фабрика кешей, далее планируется делать конструкцию на основе конфиг файла)
 */
public class ServerUpdaterU2 implements Runnable {


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


    public ServerUpdaterU2(ProvidersWrapper[] eventProviderWrappers, ICliManager cliManager, ICacheFactory cacheFactory)
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
//        Map<String, Object> outParams_del = new HashMap<String, Object>();
//        Map<String, Object> outParams_update = new HashMap<String, Object>();

        boolean test=false;

        if (eventProviderWrappers==null || eventProviderWrappers.length==0)
            return;

        IMetaProvider metaProvider = eventProviderWrappers[0].eventProvider.getMetaProvider();
        if (metaProvider!=null)
            test=metaProvider.isTest();

        while (!terminate)
        {
            long ln = System.currentTimeMillis();
            try {
                String sValDel="";
                String sValUpdate="";
                for (ProvidersWrapper providerWrapper : eventProviderWrappers)
                {
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
                    data_upd1=new Pair<IMetaProvider, Map[]>(metaProvider,new Map[0]);
                    data_del1=new Pair<IMetaProvider, Map[]>(metaProvider,new Map[0]);
                }
                else
                {
                    long lg = System.currentTimeMillis();
                    {
                        Map<Object,Map> key2tuple_update= null;
                        Map<Object,Map> key2tuple_delete= null;

                        for (int i = 0, eventProviderWrappersLength = eventProviderWrappers.length; i < eventProviderWrappersLength; i++)
                        {
                            ProvidersWrapper providerWrapper = eventProviderWrappers[i];
                            Pair<IMetaProvider, Map[]> l_data_upd1;
                            Pair<IMetaProvider, Map[]> l_data_del1=null;

                            //TODO Теперь интерпретируем данные для удаления как установку в null значения этих данных
                            if (k > 0)
                            {
                                l_data_del1 = providerWrapper.eventProvider.getDeletedTable(providerWrapper.valMap_del, providerWrapper.outParams_del);
                            }

                            l_data_upd1 = providerWrapper.eventProvider.getUpdateTable(providerWrapper.valMap_update, providerWrapper.outParams_update);
                            if (l_data_del1 == null)
                            {
                                providerWrapper.outParams_del.clear();
                                providerWrapper.outParams_del.putAll(providerWrapper.outParams_update);
                                l_data_del1 = new Pair<IMetaProvider, Map[]>(l_data_upd1.first, new Map[0]);
                            }

                            final String provName = PROV + "_" + i;
                            if (k == 0)
                                metaProvider.addColumn2AllTypesIfNotExists(new ColumnHeadBean(provName, provName, ListGridFieldType.INTEGER.toString(), -20-i));


                            for (Map tuple : l_data_upd1.second)
                                tuple.put(provName,1);
                            for (Map tuple : l_data_del1.second)
                                tuple.put(provName,1);

                            key2tuple_update = mergeUpdates(key2tuple_update,l_data_upd1);
                            key2tuple_delete = mergeUpdates(key2tuple_delete,l_data_del1);

                            if (data_upd1==null)
                                data_upd1=l_data_upd1;
                            if (data_del1==null)
                                data_del1=l_data_del1;

                        }
                            if (key2tuple_update!=null)
                                data_upd1.second=key2tuple_update.values().toArray(new Map[key2tuple_update.size()]);
                            if (key2tuple_delete!=null)
                                data_del1.second=key2tuple_delete.values().toArray(new Map[key2tuple_delete.size()]);



                    }
                    System.out.println(this.getClass().getName() + ": get Update data time = " + (System.currentTimeMillis() - lg) / 1000);
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

                        ColumnHeadBean[] meta = metaProvider.getColumnsByEventName(eventName);

                        if (!test && (meta ==null || meta.length==0))
                            throw new IllegalStateException("meta data can't not be null");

                        meta=cache.setMeta(meta);

                        cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME}, cache));
                        caches.put(eventName,cache);
                        cliManager.putCacheForName(eventName,new Pair<ICache, BaseTableDesc>(cache,metaProvider.getTypes2NamesMapper().getTableDescByTblName(eventName)));
                    }
                }

                ITypes2NameMapper type2Name = metaProvider.getTypes2NamesMapper();

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

                            //TODO Здесь вопрос надо ли проверять то что ключ содержится в кжше (Дальше наверное от этого будет зависить передача изменеий на клиенты)
                            //TODO Есть менение что для DerbyCache проверять не надо, просто генерируем ключ и добавляем в удаленные (Если фактического удаления не происходит тогда у нас ключи
                            //TODO  не войдут в конечное множество измененных ключей)

                            //TODO ПОСЛЕ ОТЛАДКИ ПРИВЕСТИ к ЕДИНОМУ КОНТРАКТУ В ЭТОМ СМЫСЛЕ ОБЫЧНЫЙ И УДАЛЕННЯЕМЫЙ КЕШ
//                            Object key = cache.createContainsKey(map);
//                            if (key!=null)
//                                keys4Delete.add(key);
//                            else
//                            {
//                                key = cache.getKeyGenerator().getKeyByTuple(map);
//                                if (key!=null)
//                                    System.out.println("key " + key + " not found in cache ");
////                                  keys4Delete.add(key);//Если в кеше нет ключа его не надо и удалять
//                                else
//                                   throw new Exception(this.getClass().getName()+"!!!Generate key error!!!");
//                            }
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
                        wasUpdated=wasUpdated || objectMap.size()>0;


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

            try {
                    Thread.sleep(Math.max(3 * 1000 - ln, 100));
            } catch (InterruptedException e) {//
            }
            doSomeJob();
        }
    }

    private Map<Object, Map> mergeDelete(Map<Object, Map> key2tuple_delete, Pair<IMetaProvider, Map[]> l_data_del1)
    {
        if (key2tuple_delete==null)
        {
                key2tuple_delete= new HashMap<Object,Map>();
                for (Map map : l_data_del1.second)
                    key2tuple_delete.put(map.get(TablesTypes.KEY_FNAME),map);
        }
        else
        {
            for (Map map : l_data_del1.second)
            {
                final Object key = map.get(TablesTypes.KEY_FNAME);
                key2tuple_delete.put(key,map);
            }
        }
        return key2tuple_delete;
    }

    private Map<Object, Map> mergeUpdates(Map<Object, Map> key2tuple_update, Pair<IMetaProvider, Map[]> l_data_upd1)
    {
        if (l_data_upd1!=null)
        {
            if (key2tuple_update==null)
            {
                    key2tuple_update= new HashMap<Object,Map>();
                    for (Map map : l_data_upd1.second)
                        key2tuple_update.put(map.get(TablesTypes.KEY_FNAME),map);
            }
            else
            {
                for (Map map : l_data_upd1.second)
                {
                    final Object key = map.get(TablesTypes.KEY_FNAME);

                    if (key2tuple_update.containsKey(key))
                        key2tuple_update.get(key).putAll(map); //Просто добавляем если содержит данные
                    else
                        key2tuple_update.put(key,map);
                }
            }
        }
        return key2tuple_update;
    }

    protected void doSomeJob()
    {

    }

    private Map<String, Map<Object, long[]>> update2Cache(Pair<IMetaProvider, Map[]> data_upd, ITypes2NameMapper type2Name,Map<String,Map<Object,Map>> eventName2Key4Delete) throws CacheException {

        Map<String,Map<Object, long[]>>  datasUpdate= new HashMap<>();
        { //TODO Полностью переработал поскольку апдейт должен быть массовым что бы избежать доп.расходов на апдейт по одному кортежу
            Map<String,List<Map>>  eventName2DataList= new HashMap<String,List<Map>>();
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

            for (String eventName : eventName2DataList.keySet())
            {
                Map<Object, long[]> dataUpdate=datasUpdate.get(eventName);
                if (dataUpdate==null)
                        datasUpdate.put(eventName,dataUpdate=new HashMap<Object, long[]>());

                ICache cache = caches.get(eventName);
                if (cache==null)
                    throw new CacheException("Event Cashes for name "+eventName+" not found");

                List<Map> maps = eventName2DataList.get(eventName);
                Map<Object, long[]> update= cache.update(maps.toArray(new Map[maps.size()]), true).dataRef;
                for (Object key4update : update.keySet())
                {
                        Map<Object,Map> key4Delete = eventName2Key4Delete.get(eventName);
                        Map tupleDel;
                        if (key4Delete!=null && key4Delete.containsKey(key4update))
                        {
                            tupleDel=key4Delete.remove(key4update);
                            System.out.println("Restored key id " + key4update.toString());//удаление из списка удаляемых событий, т.е. если событие было удалено а потом мы его нащли в актуальныъ событиях

                            Map[] tupleUpdate=data_upd.second;
                            for (int i = 0; i < tupleUpdate.length; i++) {
                                if (tupleUpdate[i].get(TablesTypes.KEY_FNAME).equals(key4update))
                                {
                                    System.out.println("i = " + i);
                                }
                            }

                            if (tupleDel!=null)
                                System.out.println("tuple4Del = " + tupleDel);

                            System.out.println();//, тогда считаем что событие актуально
                        }
                }

                //проверка того что у нас изменилось хотя бы одно поле, того добавляем в передаваемые данные
                for (Object key : update.keySet())
                {
                    long[] lg = update.get(key);
                    for (long l : lg)
                    {
                        if (l!=0)
                        {
                            dataUpdate.put(key,update.get(key));
                            break;
                        }
                    }
                }
            }
        }

//        int ix=0;
//        for (Map map : data_upd.second)
//        {
//            Integer typeId=(Integer)map.get(TablesTypes.DATATYPE_ID);
//            if (typeId==null)
//            {
//                //TODO Ошибка в лог.
//                continue;
//            }
//
//            String[] eventsForTypeNames=type2Name.getNameFromType(typeId);
//            if (eventsForTypeNames ==  null)
//            {
//                //TODO Ошибка в лог.
//                continue;
//            }
//
//            for (String eventName : eventsForTypeNames)
//            {
//                Map<Object, long[]> dataUpdate=datasUpdate.get(eventName);
//                if (dataUpdate==null)
//                        datasUpdate.put(eventName,dataUpdate=new HashMap<Object, long[]>());
//
//                ICache cache = caches.get(eventName);
//                if (cache==null)
//                    throw new CacheException("Event Cashes for name "+eventName+" not found");
//
//                Map<Object, long[]> update= cache.update(map);
//
//                for (Object key4update : update.keySet())
//                {
//                        Set<Object> key4Delete = eventName2Key4Delete.get(eventName);
//                        if (key4Delete!=null && key4Delete.remove(key4update))
//                        {
//                            System.out.println("Restored key id " + key4update.toString());//удаление из списка удаляемых событий, т.е. если событие было удалено а потом мы его нащли в актуальныъ событиях
//                            System.out.println();//, тогда считаем что событие актуально
//                        }
//                }
//
//                //проверка того что у нас изменилось хотя бы одно поле, того добавляем в передаваемые данные
//                for (Object key : update.keySet())
//                {
//                    long[] lg = update.get(key);
//                    for (long l : lg)
//                    {
//                        if (l!=0)
//                        {
//                            dataUpdate.put(key,update.get(key));
//                            break;
//                        }
//                    }
//                }
////                dataUpdate.putAll(update);
//            }
//            ix++;
//        }
        return datasUpdate;
    }

}
