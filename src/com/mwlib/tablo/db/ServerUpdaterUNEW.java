package com.mwlib.tablo.db;

import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICacheFactory;
import com.mwlib.tablo.cache.ICliUpdater;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 13:35
 * Осуществляет периодический опрос источника событий и реализует логику наполнения
 * ими (На основе ServerUpdaterT, добавлена фабрика кешей, далее планируется делать конструкцию на основе конфиг файла)
 * (Апдейтер с введенными режимами тестирования в частности проверки целостности данных, а так же генерации измений в кеш данных)
 */
public class ServerUpdaterUNEW implements Runnable {

    final Random random = new Random();
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
    private TablesTypes.TESTMODES testMode;
    private String dsCacheName;
    private String testTable;//Таблица которая учавствует

//    public ServerUpdaterUNEW(IEventProvider eventProvider, ICliManager cliManager, ICacheFactory cacheFactory)
//    {
//        this(eventProvider,cliManager,cacheFactory,null);
//    }

    private int period = TablesTypes.DEFPERIOD;

    public int getPeriod()
    {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }



    public ServerUpdaterUNEW(IEventProvider eventProvider, ICliManager cliManager, ICacheFactory cacheFactory, Map<String,Object> params)
    {
        this.eventProvider = eventProvider;
        this.cliManager = cliManager;
        this.cacheFactory=cacheFactory;

        if (params!=null)
        {
            Object s = params.get(TablesTypes.TESTMODEVAL);
            this.testMode = (s!=null)? ((TablesTypes.TESTMODES)s):null;
            s = params.get(TablesTypes.DS_CACHE_NAME);
            this.dsCacheName = (s!=null)?String.valueOf(s):null;

            s = params.get(TablesTypes.TESTTABLE);
            this.testTable = (s!=null)?String.valueOf(s):null;
        }
    }


    List<Pair<IMetaProvider, Map[]>> deleteList=new LinkedList<>();

    @Override
    public void run()
       {
           while (!terminate) {
               try {
                   _run();
               } catch (RuntimeException e) {
                   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

               } catch (Exception e) {
                   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }
           }
       }

    public void _run()
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

        IMetaProvider metaProvider = eventProvider.getMetaProvider();
        if (metaProvider!=null)
            test=metaProvider.isTest();

        while (!terminate)
        {
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

                Pair<IMetaProvider, Map[]> data_upd1;
                Pair<IMetaProvider, Map[]> data_del1=null;

                if (test)
                {

                    List<Map> updateTuple=new LinkedList<>();
                    List<Map> deleteTuple=new LinkedList<>();

                    String[] eventNames = metaProvider.getEventNames();

                    if (TablesTypes.TESTMODES.FULLUPADATE.equals(testMode) || TablesTypes.TESTMODES.UPDATEREMOVE.equals(testMode))
                    {
                        for (String eventName : eventNames)
                        {
                            ICache cache;
                            if ((cache=caches.get(eventName)) == null)
                               if (!initCache(test,metaProvider,eventName))
                                   continue;

                            //Далее генерируем случайным образом апдейт на базе тех данных которые есть в БД
                            if (eventName!=null && eventName.equals(testTable) && k>0 && k%4==0)
                            {
                                Object[][] tuplesCandidate=cache.getTuplesByParameters(new HashMap(),new UpdateContainer());
                                if (tuplesCandidate.length==0)
                                    continue;

                                int[] numbersUpdate=new int[Math.max(tuplesCandidate.length/60,1)];
                                Set<Integer> ints=new HashSet<Integer>();
                                for (int i=0;i<numbersUpdate.length;i++)
                                {
                                    do
                                    {
                                        numbersUpdate[i]= Math.abs(random.nextInt()%tuplesCandidate.length);
                                    } while (ints.contains(numbersUpdate[i]));
                                    ints.add(numbersUpdate[i]);
                                }



                                for (int i = 0, numbersUpdateLength = numbersUpdate.length; i < numbersUpdateLength; i++)
                                {
                                    int updateNumber = numbersUpdate[i];
                                    Object[] o = tuplesCandidate[updateNumber];
                                    Map tuple = new HashMap();
                                    for (int ixCol = 0; ixCol < o.length; ixCol++) {
                                        String colNameByIx = cache.getColNameByIx(ixCol);
                                        Object value = o[ixCol];
                                        if (value instanceof String && !TablesTypes.KEY_FNAME.equals(colNameByIx) && !"IS_VSP".equals(colNameByIx))
                                            value = String.valueOf(value) +"_XXYY_"+String.valueOf(k);
                                        tuple.put(colNameByIx, value);
                                    }
                                    if (i<numbersUpdate.length/2 || (numbersUpdate.length==1 && Math.abs(random.nextInt()%13)>4))
                                        updateTuple.add(tuple);
                                    else
                                        deleteTuple.add(tuple);
                                }
                            }
                        }
                    }

                    if (TablesTypes.TESTMODES.FULLUPADATE.equals(testMode) && deleteList.size()>7 && Math.abs(random.nextInt()%2)==1)
                    {
                        data_upd1=deleteList.remove(Math.abs(random.nextInt()%(deleteList.size()-2)));

//                        for (Map map : data_upd1.second)
//                        {
//                            final Object o = map.get(TablesTypes.ACTUAL);
//                            if (o!=null && 0==(Integer)o)
//                                System.out.println("o = " + o);
//                        }

                        data_del1=new Pair<IMetaProvider, Map[]>(metaProvider,new Map[0]);
                    }
                    else
                    {
                        data_upd1=new Pair<IMetaProvider, Map[]>(metaProvider,updateTuple.toArray(new Map[updateTuple.size()]));
                        data_del1=new Pair<IMetaProvider, Map[]>(metaProvider,deleteTuple.toArray(new Map[deleteTuple.size()]));

                        if (data_del1.second.length>0)
                            deleteList.add(data_del1);
                    }

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
                }
                metaProvider = data_upd1.first;

                String[] eventNames = metaProvider.getEventNames();

                for (String eventName : eventNames)
                    if (caches.get(eventName) == null)
                        initCache(test, metaProvider, eventName);

                ITypes2NameMapper type2Name = metaProvider.getTypes2NamesMapper();

                Map<String,Map<Object,Map>>  name2KeyDelSet= new HashMap<>();
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
                                name2KeyDelSet.put(eventName,keys4Delete=new HashMap<Object,Map>());

                        ICache cache = caches.get(eventName);
                        if (cache==null)
                            throw new CacheException("Event Cashes for name "+eventName+" not found");
                        {
                            Object key = cache.getKeyGenerator().getKeyByTuple(map);
                            if (key!=null)
                                keys4Delete.put(key,map);
                            else
                               throw new Exception(this.getClass().getName()+"!!!Generate key error!!!");

                            //TODO Здесь вопрос надо ли проверять то что ключ содержится в кэше (Дальше наверное от этого будет зависить передача изменеий на клиенты)
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
                            System.out.println("need for delete key = " + o+" for type name "+typeName+" request time:"+maxTimeStamp_del.toString());
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
                        Map<Object, long[]> removed = cache.removeTuples(keys4Delete); //Удаление из кэша всех данных для удаления (ВОЗВРАТ ФАКТИЧЕСКИ УДАЛЕННЫХ)
                        Map<Object, long[]> dataUpdate = eventName2key2cols.get(eventName);
                        if (dataUpdate==null)
                                eventName2key2cols.put(eventName,dataUpdate=new HashMap<Object, long[]>());
                        wasUpdated=wasUpdated || (removed.size()>0);
                        dataUpdate.putAll(removed);
                        for (Object o : keys4Delete.keySet())
                            System.out.println("delete key = " + o+" for type name "+eventName+" request time:"+maxTimeStamp_del.toString());
                    }
                }

                if (!wasUpdated && (data_del1.second.length>0 || data_upd1.second.length>0))
                    System.out.println("skip update Data for delete: "+data_del1.second.length+" Data for update: "+data_upd1.second.length+ " , the cache was not changed: tmDel:"+maxTimeStamp_del+" tmUpdate:"+ maxTimeStamp_update);


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
                if (TablesTypes.TESTMODES.CONSISTENCY.equals(testMode))   //TODO ввести режимы тестирования, для того что бы тестировать разныве части
                {
                    System.out.println("\n\n\n\n===================== Check Begin consistency =======================");
                        checkCacheConsistency(k);
                    System.out.println("===================== Check END consistency =======================\n\n\n\n");

                }
            } catch (Exception e) {
                System.out.println("\n\n\n\n===================== Error call check consistency =======================");
                e.printStackTrace();
                System.out.println("\n\n\n\n===================== END Error call check consistency =======================");
            }
            try {
                Thread.sleep(Math.max(period - ln, 100));
            } catch (InterruptedException e) {//
            }
            doSomeJob();
        }
    }

    protected boolean initCache(boolean test, IMetaProvider metaProvider, String eventName) throws CacheException
    {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put(ICache.CACHENAME,eventName);
        params.put(TablesTypes.DS_CACHE_NAME,dsCacheName);
        params.put(ICache.TEST,test);

        ICache cache = cacheFactory.createCache(params);
        if (cache==null)
            return false;

        ColumnHeadBean[] meta = metaProvider.getColumnsByEventName(eventName);

        if (!test && (meta ==null || meta.length==0))
            throw new IllegalStateException("meta data can't not be null");

        meta=cache.setMeta(meta);

        cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME}, cache));
        caches.put(eventName,cache);
        cliManager.putCacheForName(eventName,new Pair<ICache,BaseTableDesc>(cache,metaProvider.getTypes2NamesMapper().getTableDescByTblName(eventName)));

        return true;
    }

    protected void doSomeJob()
    {

    }


    private Map<String, Map<Object, long[]>> update2Cache(Pair<IMetaProvider, Map[]> data_upd, ITypes2NameMapper type2Name,Map<String,Map<Object,Map>> eventName2Key4Delete) throws CacheException {

        Map<String,Map<Object, long[]>>  datasUpdate= new ConcurrentHashMap<>();
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
                        if (key4Delete!=null && key4Delete.containsKey(key4update))
                        {
                            key4Delete.remove(key4update);
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
            }
        }

        return datasUpdate;
    }

    protected CheckCacheConsistency consistency;
    protected  void checkCacheConsistency(int nIter) throws Exception //TODO Пересмотреть тест на более подробный
    {
        if (consistency==null)
            consistency=new CheckCacheConsistency();

        if (nIter%17==0)
             consistency.checkCacheConsistency();
        consistency.viewCheckResult();
    }

    protected class CheckCacheConsistency
    {
        protected Map<Object,Pair<Map,Integer>> suspectedDelKeys =new HashMap<Object,Pair<Map,Integer>>(); //не удаленные подозрительные ключи
        protected Map<Object,Pair<Map,Integer>> suspectedUpdateKeys =new HashMap<Object,Pair<Map,Integer>>();//не обновленные подозрительные ключи
        protected Map<Object,Pair<Map,Integer>> suspectedValKeys =new HashMap<Object,Pair<Map,Integer>>();//подозрительные ключи, в которых не совпадают значения

        protected void viewCheckResult()
        {
            final int sizeUpdate = suspectedUpdateKeys.size();
            final int sizeDelete = suspectedDelKeys.size();
            final Map<Object,Pair<Map,Integer>> sizeVal = suspectedValKeys;
            if (sizeUpdate >0 || sizeDelete >0 || sizeVal.size()>0)
            {
                System.out.println("\n\n\n\n??????????????????????? Check Begin consistency RESULSTS ???????????????????????");
                System.out.println("sizeVal = " + sizeVal+" sizeDelete= "+ sizeDelete+" sizeUpdate="+ sizeUpdate);
                System.out.println("??????????????????????? Check END consistency RESULSTS ???????????????????????\n\n\n\n");
            }
        }


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

            Pair<IMetaProvider, Map[]> resFull = eventProvider.getUpdateTable(getValBegins(), new HashMap<String, Object>()); //получить полностые данные от провайдера


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





}
