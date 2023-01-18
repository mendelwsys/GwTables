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


import java.sql.Timestamp;
import java.sql.Types;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 03.09.15
 * Time: 12:03
 * Осуществляет периодический опрос источника событий и реализует логику наполнения
 * ими кешей,  алгоритм наполнения изменен - делается полный запрос к БД, те данные которые не вошли
 * в полный запрос удаляются, остальные апдейтятся
 */
public class ServerUpdaterFullRequestProvider  implements Runnable, ICascadeUpdater
{


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
    private ICliManager cliManager;
    private ICacheFactory cacheFactory;

    public ServerUpdaterFullRequestProvider(IEventProvider eventProvider, ICliManager cliManager, ICacheFactory cacheFactory)
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


    public void performUpdate()
    {
        if ((System.currentTimeMillis()-ln)>=skipIfLessTime)
        {
            try {

                //Это главный цикл обновления кэшей таблиц.
                Map<String, ParamVal> valMap_upd = new HashMap<String, ParamVal>();
                Timestamp maxTimeStamp_update = EventProvider.getMaxTimeStamp2(outParams_update);
                {
                    maxTimeStamp_update.setTime(maxTimeStamp_update.getTime()-10000);
                    valMap_upd.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp_update, Types.NULL));
                    valMap_upd.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp_update, Types.TIMESTAMP));
                    System.out.println("ix: " + k + " Request for update time parameter:" + maxTimeStamp_update.toString());
                }


                Pair<IMetaProvider, Map[]> data_upd1;
                if (test)
                    data_upd1=new Pair<IMetaProvider, Map[]>(metaProvider,new Map[0]);
                else
                {
                    long lg=System.currentTimeMillis();
                    data_upd1= eventProvider.getUpdateTable(valMap_upd, outParams_update);
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

                        ColumnHeadBean[] meta = metaProvider.getColumnsByEventName(eventName);
                        if (!test && (meta ==null || meta.length==0))
                            throw new IllegalStateException("meta data can't not be null");

                        cache.setMeta(meta);

                        cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME}, cache));
                        caches.put(eventName,cache);
                        cliManager.putCacheForName(eventName,new Pair<ICache, BaseTableDesc>(cache,metaProvider.getTypes2NamesMapper().getTableDescByTblName(eventName)));
                    }
                }

                ITypes2NameMapper type2Name = metaProvider.getTypes2NamesMapper();

                Map<String,Set<Object>>  name2KeyDelSet= new HashMap<>();
                Map<String, UpdateContainer> eventName2key2cols = update2Cache(data_upd1, type2Name,name2KeyDelSet);

                boolean wasUpdated=false;
                for (UpdateContainer objectMap : eventName2key2cols.values())
                {
                        wasUpdated=wasUpdated || objectMap.dataRef.size()>0;
                        if (wasUpdated) break;
                }


                for (String eventName : name2KeyDelSet.keySet())
                {
                    ICache cache = caches.get(eventName);
                    if (cache==null)
                        throw new CacheException("Event Cashes for name "+eventName+" not found");
                    Set<Object> keys4Remove = name2KeyDelSet.get(eventName);
                    if (keys4Remove!=null && keys4Remove.size()>0)
                    {
                        Map<Object, long[]> removed = cache.removeAll(keys4Remove); //Удаление из кэша всех данных для удаления
                        UpdateContainer dataUpdate= eventName2key2cols.get(eventName);
                        if (dataUpdate==null)
                                eventName2key2cols.put(eventName,new UpdateContainer(new HashMap<Object, long[]>()));
                        wasUpdated=wasUpdated || (removed.size()>0);
                        dataUpdate.dataRef.putAll(removed);
                        for (Object o : keys4Remove)
                            System.out.println("delete key = " + o+" for type name "+eventName);
                    }
                }

                // Блок распределения  по кешам клиентов, через манагер апдетов.
                for (String eventName : eventName2key2cols.keySet())
                {

                    ICache cache = caches.get(eventName);
                    UpdateContainer key2cols = eventName2key2cols.get(eventName);
                    if (key2cols!=null && key2cols.dataRef!=null && key2cols.dataRef.size()>0)
                    {
                        ICliUpdater[] cashes = cliManager.getCachesForName(eventName);
                        for (ICliUpdater cliCashes : cashes)
                        {
                            cliCashes.updateData(key2cols);
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
            ln=System.currentTimeMillis();
        }


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
    }

    private void checkCacheConsistency() throws Exception
    {
        Map<String, ParamVal> valMap_begin = new HashMap<String, ParamVal>();
        {
            Timestamp maxTimeStamp_Begin = EventProvider.getMaxTimeStamp2(new HashMap<String, Object>());
            valMap_begin.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp_Begin, Types.NULL));
            valMap_begin.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp_Begin, Types.TIMESTAMP));
        }
        Pair<IMetaProvider, Map[]> resFull = eventProvider.getUpdateTable(valMap_begin, new HashMap<String, Object>());

        Map<Object,Map> fullCache = new HashMap<Object,Map>();
        if (resFull!=null)
        {
            fullCache.clear();
            for (Map map : resFull.second)
            {
                Object key=map.get(TablesTypes.KEY_FNAME);
                fullCache.put(key, map);
            }
        }

        for (String typename : caches.keySet())
        {
            ICache cache = caches.get(typename);
            Set<Object> keys = cache.getAllDataKeys();
            for (Object key : keys) {
                if (!fullCache.containsKey(key))
                    System.out.println(" key was not delete? " + key+" for data "+typename);
            }
        }
        fullCache.clear();
    }

    private Map<String, UpdateContainer > update2Cache(Pair<IMetaProvider, Map[]> data_upd, ITypes2NameMapper type2Name,Map<String,Set<Object>> eventName2Key4Delete) throws CacheException {

        Map<String,UpdateContainer>  datasUpdate= new HashMap<String,UpdateContainer>();
        { //TODO Полностью переработал поскольку апдейт должен быть массовым что бы избежать доп.расходов на апдейт по одному кортежу
            Map<String,List<Map>>  eventName2DataList= new HashMap<String,List<Map>>();
            for (Map tuple : data_upd.second)
            {

                Integer typeId = (Integer)tuple.get(TablesTypes.DATATYPE_ID);;
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
                    dataUpdate.add(tuple);

                    ICache cache = caches.get(eventName);
                    Set<Object> keys4Delete= eventName2Key4Delete.get(eventName);
                    if (keys4Delete==null)
                        eventName2Key4Delete.put(eventName,keys4Delete = cache.getAllDataKeys());//Заполним всеми ключами которые есть в кеше

//                    Object key=cache.getKeyGenerator().getKeyByTuple(tuple);
//                    keys4Delete.remove(key); //Если пришедший ключ есть в БД, его надо удалить (Делается позже, после апдейта стр. 273, даже если изменений нет, апдейт все равно
//                    возвращает ключи апдейта с нулевым вторым элементом, и они убираются из удаляемых ключей )

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
                            System.out.println(this.getClass().getName()+" : key id " + key4update.toString()+" still actual and not deleted");//удаление из списка удаляемых событий, т.е. если событие было удалено а потом мы его нащли в актуальныъ событиях
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


    boolean test;
    protected IMetaProvider metaProvider;
    int k;
    Map<String, Object> outParams_update;
    Timestamp maxTimeStamp_update;

    long ln=System.currentTimeMillis();
    long skipIfLessTime=10000;

    @Override
    public void initStartParams()
    {

        test=false;
        metaProvider = eventProvider.getMetaProvider();
        if (metaProvider!=null)
            test=metaProvider.isTest();

        k = 0;
        outParams_update = new HashMap<String, Object>();
        maxTimeStamp_update=null;
        ln=System.currentTimeMillis();

    }


    @Override
    public void run()
    {
        try {
            Thread.sleep(2000); //TODO Чего-не заводится дерби без инициализации пула Oracle
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

}
