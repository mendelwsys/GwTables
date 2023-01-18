package com.mwlib.tablo.db;

import com.mwlib.tablo.ICliManager;
import com.mwlib.tablo.UpdateContainer;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICacheFactory;
import com.mwlib.tablo.cache.ICliUpdater;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 13:35
 * Осуществляет периодический опрос источника событий и реализует логику наполнения
 * ими
 */
public class ServerUpdaterT2 implements Runnable, ICascadeUpdater {

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

    protected Map<String, ICache> caches;// = new HashMap<>(); //Имя в кэш
    private IEventProvider eventProvider;
    private ICliManager cliManager;
    private ICacheFactory cacheFactory;


    @Override
    public String getUpdaterName()
    {
        return eventProvider.getClass().getName();
    }

    public ServerUpdaterT2(IEventProvider eventProvider, ICliManager cliManager,ICacheFactory cacheFactory)
    {
        this.eventProvider = eventProvider;
        this.cliManager = cliManager;
        this.cacheFactory=cacheFactory;
    }

    Map[] testData;

    boolean test=false;
    int k=0;
    Map<String, Object> outParams = new HashMap<String, Object>();


    @Override
    public void initStartParams()
    {
        test=false;
        k = 0;
        outParams = new HashMap<String, Object>();
    }


    @Override
    public void run()
    {

//        try { //TODO Без этого почему-то не инициализировался пулл derbyDB, почему мне не ясно пока 17.11.2014
//            Connection conn = DbUtil.getConnection2(DbUtil.DS_ORA_NAME);
//            conn.close();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (SQLException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }


        initStartParams();
        while (!terminate)
        {
            long ln = System.currentTimeMillis();

            performUpdate();

            ln = System.currentTimeMillis() - ln;

            try
            {
                    Thread.sleep(Math.max(3 * 1000 - ln, 100));
            }
            catch (InterruptedException e) {//
            }

            k++;
        }
    }

    public void performUpdate() {
        try {

            //Это по идее главный цикл обновления кэшей таблиц.
            Pair<IMetaProvider, Map[]> data_upd1=null;
            Pair<IMetaProvider, Map[]> data_del1=null;

            if (!test || data_upd ==null)
            {

                    HashMap<String, ParamVal> valMap = new HashMap<String, ParamVal>();

                    long lg = System.currentTimeMillis();

                    if (data_del != null)
                        data_del1 = eventProvider.getDeletedTable(valMap, outParams);
                    data_upd1 = eventProvider.getUpdateTable(valMap, outParams);

//                if (eventProvider instanceof Ref12EventProviderTImpl)
//                {
//                    Map[] map=data_upd1.second;
//                    for (Map map1 : map)
//                    {
//                        final String place_id = map1.get("PLACE_ID").toString();
//                        if (map1.get("0")!=null && !String.valueOf(map1.get("0")).equals("0") && !place_id.endsWith("##00") && !place_id.endsWith("##"))// && place_id.startsWith("1##"))
//                            System.out.println("map1 = " + map1);
//                    }
//                    System.out.println("map = " + map.length);
//                }
//                    data_upd1 = new Pair<IMetaProvider, Map[]>(data_upd1.first, new Map[0]);

                    if (data_del1 == null)
                        data_del1 = new Pair<IMetaProvider, Map[]>(data_upd1.first, new Map[0]);

                    System.out.println(this.getClass().getName() + ": get Update data time = " + (System.currentTimeMillis() - lg) / 1000);
                    test = data_upd1.first.isTest();

//                    if (test && data_upd1.second!=null)
//                    {//Ограничиваем одной дорогой все данные.
//                        List<Map> ll=new LinkedList<Map>();
//                        for (Map map : data_upd1.second)
//                        {
//                            Integer key=(Integer)map.get(TablesTypes.DOR_CODE);
//                            if (key.equals(1) || key<0)
//                                ll.add(map);
//                        }
//                        data_upd1.second = ll.toArray(new Map[ll.size()]);
//                    }
            }
            if (data_upd == null)
            {
                data_del=data_del1;
                data_upd = data_upd1;

            }
            else
            if (test)
            {
                data_del1=new Pair<IMetaProvider, Map[]>();
                data_del1.first= data_del.first;

                data_upd1=new Pair<IMetaProvider, Map[]>();
                data_upd1.first= data_upd.first;

                data_upd1.second=data_upd.second;
                data_del1.second= new Map[0];

                {
                    boolean isDel=false;
                    if (isDel)
                    {

                        boolean test1=false;
                        if (test1)
                        {
                            data_upd1.second=data_upd.second=testData;

//                                br:
//                                for (Map map : data_upd1.second)
//                                {
//                                    String key = (String) map.get(TablesTypes.KEY_FNAME);
//                                    Object pred_id=(Object)map.get("PRED_ID");
//                                        //key.startsWith("-1") &&
//
//                                    if (!pred_id.toString().contains(TablesTypes.HIDE_ATTR))
//                                    {
//
//                                        Set<String> cntNotNull=new HashSet<String>();
//                                        for (int ix=0;ix<30;ix++)
//                                        {
//                                            String key1 = String.valueOf(ix);
//                                            if (map.get(key1)!=null)
//                                                cntNotNull.add(key1);
//                                        }
//                                        if (cntNotNull.size()>=2)
//                                        {
//                                            for (String s : cntNotNull)
//                                            {
//                                                map.put(s,null);
//                                                break br;
//                                            }
//                                        }
//                                    }
//                                }
                        }
                        else
                        {
                            List<Map> ll=new LinkedList<Map>(Arrays.asList(data_upd1.second));
                            for (int i = 0, llSize = ll.size(); i < llSize; i++)
                            {
                                Map map = ll.get(i);
                                String key = (String) map.get(TablesTypes.KEY_FNAME);
                                Object pred_id = (Object) map.get("PRED_ID");
                                if (!pred_id.toString().contains(TablesTypes.HIDE_ATTR))
                                {
                                    ll.remove(i);
                                    break;
                                }
                            }

                            testData=data_upd.second;
                            data_upd.second=data_upd1.second=ll.toArray(new Map[ll.size()]);
                        }

                    }
                }
            }

            IMetaProvider metaProvider = data_upd1.first;
            String[] eventNames = metaProvider.getEventNames();

            for (String eventName : eventNames)
            {

                ICache cache = getCaches().get(eventName);
                if (cache == null)
                {
                    //cache = new Cache();

                    Map<String,Object> params = new HashMap<String,Object>();
                    params.put(ICache.CACHENAME,eventName);
                    params.put(ICache.TEST,test);
                    cache = cacheFactory.createCache(params);
                    if (cache==null)
                        continue;

                    ColumnHeadBean[] meta = metaProvider.getColumnsByEventName(eventName);
                    if (meta ==null || meta.length==0)
                        throw new IllegalStateException("meta data can't not be null");

                    cache.setMeta(meta);

                    cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME}, cache));
                    getCaches().put(eventName, cache);
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

                    ICache cache = getCaches().get(eventName);
                    if (cache==null)
                        throw new CacheException("Event Cashes for name "+eventName+" not found");

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
                           throw new Exception(this.getClass().getName()+": !!!Generate key error!!!");
                    }

                }
            }


            Map<String, Map<Object, long[]>> eventName2key2cols = update2Cache(data_upd1, type2Name,name2KeyDelSet);

            for (String eventName : name2KeyDelSet.keySet())
            {
                ICache cache = getCaches().get(eventName);
                if (cache==null)
                    throw new CacheException("Event Cashes for name "+eventName+" not found");
                Set<Object> keys4Remove = name2KeyDelSet.get(eventName);
                if (keys4Remove!=null)
                {
                    Map<Object, long[]> removed = cache.removeAll(keys4Remove); //Удаление из кэша всех данных для удаления
                    Map<Object, long[]> dataUpdate = eventName2key2cols.get(eventName);
                    if (dataUpdate==null)
                            eventName2key2cols.put(eventName,dataUpdate=new HashMap<Object, long[]>());
                    dataUpdate.putAll(removed);
                }
            }

//                if (ConsolidatorLoader.tWasDelete)
//                {
//                    if (eventName2key2cols.keySet().size()==0)
//                            System.out.println("Error");
//                }


            //-- В тестовом варианте моделировуются  обновления, что бы отладить передачу данных на клиента
            // Блок распределения  по кешам клиентов, через манагер апдетов.
            for (String eventName : eventName2key2cols.keySet())
            {

                ICache cache = getCaches().get(eventName);
                Map<Object, long[]> key2cols = eventName2key2cols.get(eventName);
                if (key2cols.size()!=0)
                {
                    ICliUpdater[] cashes = cliManager.getCachesForName(eventName);
                    for (ICliUpdater cliCashes : cashes)
                    {
                        cliCashes.updateData(new UpdateContainer(key2cols));
                        int size = cliCashes.getQueueVolume() * 100 / cache.size();
                        System.out.println("T2 sizeV = " + size);
                        if (size > MAXVOL)
                            cliManager.removeCache(cliCashes);
                    }
                }
                else
                {
//                        if (ConsolidatorLoader.tWasDelete)
//                            System.out.println("Error");
                }
            }
            cliManager.removeDeadSessions(); //TODO Включить после проверки.
        }
        catch (Exception e)
        {
            e.printStackTrace(); //TODO Выход из потока по ошибке ?????!
        }
    }

    protected Map<String, ICache> getCaches()
    {
        if (caches==null)
            caches = new HashMap<String, ICache>(); //Имя в кэш
        return caches;
    }

    private Map<String, Map<Object, long[]>> update2Cache(Pair<IMetaProvider, Map[]> data_upd1, ITypes2NameMapper type2Name,Map<String,Set<Object>> eventName2Key4Delete) throws CacheException
    {

        Map<String,Map<Object, long[]>>  datasUpdate= new HashMap<>();
        Set<String> eventNames = getCaches().keySet();

        if (data_upd1.second==null)
        {
            for (String eventName : eventNames)
            {
                Map<Object, long[]> dataUpdate=datasUpdate.get(eventName);
                if (dataUpdate==null)
                   datasUpdate.put(eventName,dataUpdate=new HashMap<Object, long[]>());
            }
            return datasUpdate;
        }

        for (String eventName : eventNames) //принцип формирования ключей для удаления прост, те данные которые не пришли после консолидации, считаются удаленными
            eventName2Key4Delete.put(eventName, getCaches().get(eventName).getAllDataKeys());

        //Set<Object> hs=new HashSet<Object>();
//        if (ConsolidatorLoader.tWasDelete)
//        {
//                for (String eventName : eventNames)
//                {
//                        System.out.println("still no Error");
//
//                        ICache cache = caches.get(eventName);
//                        if (cache==null)
//                            throw new CacheException("Event Cashes for name "+eventName+" not found");
//
//                        Map[] maps=data_upd1.second;
//
//                        for (Map map : maps)
//                        {
//                            Object keyO = map.get(TablesTypes.KEY_FNAME);
//                            Object[] oldTuple = cache.getTupleByKey(keyO);
//
//                            Map<String, Integer> col2ix = cache.getColName2Ix();
//                            for (String colKey : col2ix.keySet())
//                            {
//                                if (
//                                        !
//                                                (oldTuple[col2ix.get(colKey)] == map.get(colKey) || (oldTuple[col2ix.get(colKey)] != null && oldTuple[col2ix.get(colKey)].equals(map.get(colKey)))
//                                                        ||
//                                                        (map.get(colKey) != null && map.get(colKey).equals(oldTuple[col2ix.get(colKey)]))
//                                                )
//                                        )
//                                {
//                                    hs.add(keyO);
//                                    break;
//                                }
//                            }
//                        }
//                }
//        }


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

                ICache cache = getCaches().get(eventName);
                if (cache==null)
                    continue;
                    //throw new CacheException("Event Cashes for name "+eventName+" not found");

                Map<Object, long[]> update= cache.update(map, true).dataRef;
                for (Object key4update : update.keySet())
                {
                    Set<Object> key4Delete = eventName2Key4Delete.get(eventName);
                    if (key4Delete!=null && key4Delete.remove(key4update))
                    {//в данном случае все актуально, поскольку мы удаляем только те ключи, которых нет  в консолидированном мно-ве.
//TODO                        System.out.println("Restored key id " + key4update.toString());//удаление из списка удаляемых событий, т.е. если событие было удалено а потом мы его нащли в актуальныъ событиях
//                        System.out.println();//, тогда считаем что событие актально
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
            ix++;
        }



//        if (ConsolidatorLoader.tWasDelete)
//        {
//            if (datasUpdate.size()==0)
//                    System.out.println("Error");
//            else
//            {
//                for (String key : datasUpdate.keySet())
//                {
//                    Map<Object, long[]> objectMap=datasUpdate.get(key);
//                    if (objectMap.size()==0)
//                    {
//                        System.out.println("Error");
//
//
//                        if (hs.size()>0)
//                        {
//                            for (Object keyO : hs)
//                            {
//                                ICache cache = caches.get(key);
//                                if (cache==null)
//                                    throw new CacheException("Event Cashes for name "+key+" not found");
//                                Object[] oldTuple = cache.getTupleByKey(keyO);
//                            }
//                        }
//                        else
//                        {
//                            System.out.println("No Error");
//                        }
//                    }
//                }
//            }
//        }



        return datasUpdate;
    }

}
