package com.mwlib.tablo.db;

import com.mwlib.tablo.ICliManager;
import com.mwlib.tablo.UpdateContainer;
import com.mwlib.tablo.cache.Cache;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICliUpdater;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.db.desc.WindowsDesc;

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
public class ServerUpdaterT implements Runnable {


    public static final int MAXVOL = 120;//Максимальный объем данных допустимый в очереди, если он превышен очередь убираем.

    private Pair<IMetaProvider, Map[]> data_upd; //TODO Тестовое поля убрать!!!
    private Pair<IMetaProvider, Map[]> data_del; //TODO Тестовое поля убрать!!!

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


    public ServerUpdaterT(IEventProvider eventProvider,ICliManager cliManager)
    {
        this.eventProvider = eventProvider;
        this.cliManager = cliManager;
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
        Map<String, Object> outParams_del = new HashMap<String, Object>();
        Map<String, Object> outParams_update = new HashMap<String, Object>();
        boolean test=false;

        while (!terminate) {
            long ln = System.currentTimeMillis();

            try {

                //Это по идее главный цикл обновления кэшей таблиц.
                Map<String, ParamVal> valMap_del = new HashMap<String, ParamVal>();
                Timestamp maxTimeStamp_del = EventProvider.getMaxTimeStamp2(outParams_del);
                {
                    maxTimeStamp_del.setTime(maxTimeStamp_del.getTime()-10000);
                    valMap_del.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp_del, Types.NULL));
                    valMap_del.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp_del, Types.TIMESTAMP));
                    System.out.println("ix: " + k + " Request for delete time parameter:" + maxTimeStamp_del.toString());
                }

                Map<String, ParamVal> valMap_upd = new HashMap<String, ParamVal>();
                Timestamp maxTimeStamp_update = EventProvider.getMaxTimeStamp2(outParams_update);
                {
                    maxTimeStamp_update.setTime(maxTimeStamp_update.getTime()-10000);
                    valMap_upd.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp_update, Types.NULL));
                    valMap_upd.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp_update, Types.TIMESTAMP));
                    System.out.println("ix: " + k + " Request for update time parameter:" + maxTimeStamp_update.toString());
                }


                Pair<IMetaProvider, Map[]> data_upd1=null;
                Pair<IMetaProvider, Map[]> data_del1=null;
                if (!test || data_upd ==null)
                {

                    long lg=System.currentTimeMillis();

                    if (data_del!=null)
                    {
                        data_del1= eventProvider.getDeletedTable(valMap_del, outParams_del);
//                        Pair<IMetaProvider, Map[]> data_del2 = eventProvider.getDeletedTable(valMap_upd, outParams_update);
//                        if (data_del1.second!=null && (data_del2.second==null || data_del1.second.length>data_del2.second.length))
//                                System.out.println("lost Event Error: right:"+(data_del1.second.length)+" wrong:"+((data_del2.second==null)?0:(data_del2.second.length)));
                    }

                    data_upd1= eventProvider.getUpdateTable(valMap_upd, outParams_update);
                    if (data_del1==null)
                    {
                        outParams_del.clear();
                        outParams_del.putAll(outParams_update);
                        data_del1=new Pair<IMetaProvider,Map[]>(data_upd1.first,new Map[0]);
                    }

                    System.out.println(this.getClass().getName()+": get Update data time = " + (System.currentTimeMillis()-lg)/1000);
                    test=data_upd1.first.isTest();
                }
                if (data_upd == null)
                {
                    data_del=data_del1;
                    data_upd = data_upd1;

                }
                else
                if (test)
                {
                    String[] SERV={"П","Э","Ш"};
                    int[] TYPES={54,59,56};

                    data_upd1=new Pair<IMetaProvider, Map[]>();
                    data_upd1.first= data_upd.first;


                    data_del1=new Pair<IMetaProvider, Map[]>();
                    data_del1.first= data_del.first;


                    {
                        final int tableCount= data_upd.second.length/4;
                        Set<Integer> updates=new HashSet<Integer>();
                        List<Map> updatesTuples=new LinkedList<Map>();
                        for (int j = 0; j < tableCount; j++)
                        {
                            int ix = (int) Math.round(Math.random() * (data_upd.second.length - 1));
                            if (!updates.contains(ix))
                            {
                                updatesTuples.add(data_upd.second[ix]);
                                updates.add(ix);
                            }
                        }
                        data_upd1.second=updatesTuples.toArray(new Map[updatesTuples.size()]);
                    }

                    for (int j = 0; j < data_upd1.second.length; j++)
                    {
                        Integer tid=(Integer) data_upd.second[j].get(TablesTypes.DATATYPE_ID);
                        String[] namesFromType = data_upd1.first.getTypes2NamesMapper().getNameFromType(tid);
                        for (String nameFromType : namesFromType)
                        {
                            if (nameFromType.equals(TablesTypes.WINDOWS))
                            {
                              int ix =   (int) Math.round(Math.random() * 300);

                                {
                                    data_upd1.second[j].put(WindowsDesc.SERV, SERV[ix % SERV.length]);
//                                    data_upd1.second[j].put(WindowsDesc.STATE, String.valueOf(TYPES[ix % SERV.length]));
                                    data_upd1.second[j].put(TablesTypes.DATATYPE_ID, TYPES[ix % SERV.length]);
                                }
                            }
                            else if (nameFromType.equals(TablesTypes.WINDOWS+"_E"))
                            {
                                int fact = (int) Math.round(Math.random() * 3);
                                data_upd1.second[j].put(TablesTypes.STATUS_FACT,fact);
                            }
                        }
                    }


                    List<Map> updates=new LinkedList<Map>();

                    List<Map> deletes=new LinkedList<Map>();
                    //Сформировать тесстовый набор из удалений
                    for (int i = 0; i < data_upd1.second.length/3; i++)
                    {
                        int ix=(int)Math.round(Math.random()*(data_upd1.second.length-1));
                        if (data_upd1.second[ix]!=null)
                        {
                            deletes.add(data_upd1.second[ix]);
                            data_upd1.second[ix]=null;
                        }
                    }
                    data_del1.second=deletes.toArray(new Map[deletes.size()]);


                    for (Map map : data_upd1.second)
                        if (map!=null)
                            updates.add(map);
                    data_upd1.second=updates.toArray(new Map[updates.size()]);

                }

                IMetaProvider metaProvider = data_upd1.first;

                String[] eventNames = metaProvider.getEventNames();

                for (String eventName : eventNames)
                {
                    ICache cache = caches.get(eventName);
                    if (cache == null)
                    {
                        cache = new Cache();
                        ColumnHeadBean[] meta = metaProvider.getColumnsByEventName(eventName);
                        if (meta ==null || meta.length==0)
                            throw new IllegalStateException("meta data can't not be null");

                        cache.setMeta(meta);

                        cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME}, cache));
                        caches.put(eventName,cache);
                        cliManager.putCacheForName(eventName,new Pair<ICache,BaseTableDesc>(cache,metaProvider.getTypes2NamesMapper().getTableDescByTblName(eventName)));
                    }
                }

                ITypes2NameMapper type2Name = metaProvider.getTypes2NamesMapper();

                Map<String,Set<Object>>  name2KeyDelSet= new HashMap<>();
                for (Map map : data_del1.second)
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

                        Set<Object> dataDelete=name2KeyDelSet.get(eventName);
                        if (dataDelete==null)
                                name2KeyDelSet.put(eventName,dataDelete=new HashSet());

                        ICache cache = caches.get(eventName);
                        if (cache==null)
                            throw new CacheException("Event Cashes for name "+eventName+" not found");

                        {
                            Object key = cache.createContainsKey(map);
                            if (key!=null)
                                dataDelete.add(key);
                            else
                            {
                                key = cache.getKeyGenerator().getKeyByTuple(map);
                                if (key!=null)
                                    System.out.println("key " + key + " not found in cache ");
//                                  dataDelete.add(key);//Если в кеше нет ключа его не надо и удалять
                                else
                                   throw new Exception(this.getClass().getName()+"!!!Generate key error!!!");
                            }
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

                Map<String, Map<Object, long[]>> eventName2key2cols = update2Cache(data_upd1, type2Name,name2KeyDelSet);




                boolean wasUpdated=false;

                for (Map<Object, long[]> objectMap : eventName2key2cols.values())
                        wasUpdated=wasUpdated || objectMap.size()>0;


                for (String eventName : name2KeyDelSet.keySet())
                {
                    ICache cache = caches.get(eventName);
                    if (cache==null)
                        throw new CacheException("Event Cashes for name "+eventName+" not found");
                    Set<Object> keys4Remove = name2KeyDelSet.get(eventName);
                    if (keys4Remove!=null)
                    {
                        Map<Object, long[]> removed = cache.removeAll(keys4Remove); //Удаление из кэша всех данных для удаления
                        Map<Object, long[]> dataUpdate = eventName2key2cols.get(eventName);
                        if (dataUpdate==null)
                                eventName2key2cols.put(eventName,dataUpdate=new HashMap<Object, long[]>());
                        wasUpdated=wasUpdated || (removed.size()>0);
                        dataUpdate.putAll(removed);
                        for (Object o : keys4Remove)
                            System.out.println("delete key = " + o+" for type name "+eventName+" request time:"+maxTimeStamp_del.toString());
                    }
                }

                if (!wasUpdated && (data_del1.second.length>0 || data_upd1.second.length>0))
                    System.out.println("skip update Data for delete: "+data_del1.second.length+" Data for update: "+data_upd1.second.length+ " , the cache was not changed: tmDel:"+maxTimeStamp_del+" tmUpdate:"+ maxTimeStamp_update);


                //-- В тестовом варианте моделировуются  обновления, что бы отладить передачу данных на клиента
                // Блок распределения  по кешам клиентов, через манагер апдетов.
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

//            try {
//                if (k%11==0 && test)
//                    checkCacheConsistency();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            try {
//                if (!test)
                    Thread.sleep(Math.max(3 * 1000 - ln, 100));
//                else
//                           if (k<1)
//                    Thread.sleep(10 * 1000);//10 second
//                           else
//                               break;
            } catch (InterruptedException e) {//
            }

        }
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

    private Map<String, Map<Object, long[]>> update2Cache(Pair<IMetaProvider, Map[]> data_upd1, ITypes2NameMapper type2Name,Map<String,Set<Object>> eventName2Key4Delete) throws CacheException {

        Map<String,Map<Object, long[]>>  datasUpdate= new HashMap<>();

        int ix=0;
        for (Map map : data_upd1.second)
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
                Map<Object, long[]> dataUpdate=datasUpdate.get(eventName);
                if (dataUpdate==null)
                        datasUpdate.put(eventName,dataUpdate=new HashMap<Object, long[]>());

                ICache cache = caches.get(eventName);
                if (cache==null)
                    throw new CacheException("Event Cashes for name "+eventName+" not found");

                Map<Object, long[]> update= cache.update(map, true).dataRef;

                for (Object key4update : update.keySet())
                {
                        Set<Object> key4Delete = eventName2Key4Delete.get(eventName);
                        if (key4Delete!=null && key4Delete.remove(key4update))
                        {
                            System.out.println("Restored key id " + key4update.toString());//удаление из списка удаляемых событий, т.е. если событие было удалено а потом мы его нащли в актуальныъ событиях
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
//                dataUpdate.putAll(update);
            }
            ix++;
        }
        return datasUpdate;
    }

}
