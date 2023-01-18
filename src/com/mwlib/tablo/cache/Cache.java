package com.mwlib.tablo.cache;

import com.mycompany.common.Pair;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.UpdateContainer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 22.09.14
 * Time: 18:00
 * Реализация таблицы кеша данных
 */
public class Cache implements ICache {


    //AtomicReferenceArray referenceArray= new AtomicReferenceArray();


    @Override
    public IKeyGenerator getKeyGenerator()
    {
        return keyGenerator;
    }

    @Override
    public void setKeyGenerator(IKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    private IKeyGenerator keyGenerator;

    @Override
    public Map<String, Integer> getColName2Ix() {
        return colName2Ix;
    }
    @Override
    public Map<Integer, String> getIx2ColName() {
        return ix2ColName;
    }

    @Override
    public String getColNameByIx(int ix)
    {
        return ix2ColName.get(ix);
    }

    @Override
    public Integer getIxByColName(String colName)
    {
        return colName2Ix.get(colName);
    }


    private Map<String,Integer> colName2Ix=new HashMap<String,Integer>();
    private Map<Integer,String> ix2ColName=new HashMap<Integer,String>();
    //private volatile Map<Object,AtomicReferenceArray<Object[]>> key2Tuple = new ConcurrentHashMap<Object,AtomicReferenceArray<Object[]>>();
    private volatile Map<Object,Object[]> key2Tuple = new ConcurrentHashMap<Object,Object[]>();




    @Override
    public ColumnHeadBean[] getMeta()
    {

        return cols;
    }

    private volatile  ColumnHeadBean[] cols;


    public Cache()
    {

    }


    Pair<long[],Object[]> getSubTupleByUpdate(long[] vals, Object[] tuple)
    {

        List<Object> ll= new LinkedList<Object>();
        for (long val : vals)
        {

            int jix = 0;
            while (val != 0)
            {
                val = val / 2;
                if ((val & 0x01) != 0)
                    ll.add(tuple[jix]);
                jix++;
            }
        }
        return new Pair<long[],Object[]>(vals,ll.toArray(new Object[ll.size()]));
    }

    private long[] lg;


    @Override
    public ColumnHeadBean[] setMeta(ColumnHeadBean[] cols)
    {
        if (cols==null)
            cols=new ColumnHeadBean[0];

        for (int i = 0, colsLength = cols.length; i < colsLength; i++)
        {
            colName2Ix.put(cols[i].getName(),i);
            ix2ColName.put(i,cols[i].getName());
        }
        this.cols=cols;
        int ln = cols.length % Long.SIZE;
        lg=new long[cols.length / Long.SIZE +((ln!=0)?1:0)];
        for (int i = 0; i < lg.length; i++)
            lg[i]=-1;
        return cols;
    }

    @Override
    public int size()
    {
        return key2Tuple.size();
    }

    public UpdateContainer update(Map[] inTuples, boolean insertAllNotFound) throws CacheException
    {

        Map<Object,long[]> rv=new HashMap<Object,long[]>();

        boolean initAll=(this.key2Tuple==null || this.key2Tuple.size()==0);

        for (int i = 0, inTuplesLength = inTuples.length; i < inTuplesLength; i++)
        {

            Object key = keyGenerator.getKeyByTuple(inTuples[i]);
            Object[] oldTuple;
            long[] lg=this.lg;
            if (initAll || (oldTuple = this.key2Tuple.get(key))==null)
            {
                if (insertAllNotFound)
                {
                    Object[] newTuple=map2Tuple(inTuples[i],null);
                    this.key2Tuple.put(key,newTuple);
                    rv.put(key,lg);
                }
            }
            else
            {
                lg=new long[cols.length/Long.SIZE+((cols.length%Long.SIZE>0)?1:0)];
                updateTuple(inTuples[i],oldTuple,lg);
                rv.put(key,lg);
            }

        }
        return new UpdateContainer(rv);
    }


    @Override
    public Map<Object,long[]> remove(Map inTuple) throws CacheException
    {
        Map<Object,long[]> rv=new ConcurrentHashMap<Object,long[]>();

        Object key = keyGenerator.getKeyByTuple(inTuple);
        key2Tuple.remove(key);
        rv.put(key,null);
        return rv;
    }

    public Map<Object,long[]> removeTuples(Map<Object,Map> delTuples) throws CacheException
    {
        return removeAll(delTuples.keySet());
    }

    @Override
    public Map<Object,long[]> removeAll(Set<Object> keys4Remove) throws CacheException
    {
        Map<Object,long[]> rv=new HashMap<Object,long[]>();
        for (Object key : keys4Remove)
        {
            key2Tuple.remove(key);    //TODO ВОЗВРАЩАТЬ МНО_ВО КЛЮЧЕЙ КОТРОЕ РЕАЛЬНО БЫЛО В КЕШЕ, ЭТО  ПРИВЕДЕТ К ЕДИНОМУ КОНТРАКТУ С ДЕРБИ
            rv.put(key,null);
        }
        return rv;
    }


//    @Override
//    public Set<Object> createContainsKeys(Map[] inTuples) throws CacheException
//    {
//
//        Set<Object> rv=new HashSet<Object>();
//        for (Map inTuple : inTuples)
//        {
//            Object key = keyGenerator.getKeyByTuple(inTuple);
//            if (key2Tuple.containsKey(key))
//                rv.add(key);
//        }
//        return rv;
//    }

    @Override
    public Object createContainsKey(Map inTuple) throws CacheException
    {
        Object key = keyGenerator.getKeyByTuple(inTuple);
        if (key2Tuple.containsKey(key))
            return key;
        return null;
    }



    public UpdateContainer update(Map inTuples, boolean insertAllNotFound) throws CacheException
    {

        Map<Object,long[]> rv=new HashMap<Object,long[]>();
        boolean initAll=(this.key2Tuple==null || this.key2Tuple.size()==0);

        {

            Object key = keyGenerator.getKeyByTuple(inTuples);
            Object[] oldTuple;
            long[] lg=this.lg;
            if (initAll || (oldTuple = this.key2Tuple.get(key))==null)
            {
                if (insertAllNotFound)
                {
                    Object[] newTuple=map2Tuple(inTuples,null);
                    this.key2Tuple.put(key,newTuple);
                    rv.put(key,lg);
                }
            }
            else
            {
                lg=new long[cols.length/Long.SIZE+((cols.length%Long.SIZE>0)?1:0)];
                updateTuple(inTuples,oldTuple,lg);
                rv.put(key,lg);
            }
        }
        return new UpdateContainer(rv);
    }


    private Object[] updateTuple(Map inTuple, Object[] oldTuple,long[] lg)
    {

        for (Integer ix : ix2ColName.keySet())
        {
            String key = ix2ColName.get(ix);
            if (inTuple.containsKey(key))
            {
                Object newVal = inTuple.get(key);
                if  (
                        (oldTuple[ix]!=null && !oldTuple[ix].equals(newVal)) || (newVal!= null && !newVal.equals(oldTuple[ix]))
                    )
                {
                    long mask=1l<<(ix%Long.SIZE);
                    lg[ix/Long.SIZE]|=mask;
                    oldTuple[ix]= newVal;
                }
            }
        }
        return oldTuple;
    }

    private Object[] map2Tuple(Map inTuple, Object[] oldTuple)
    {

        Object[] objs=new Object[cols.length];
        for (Integer ix : ix2ColName.keySet())
        {
           objs[ix]=inTuple.get(ix2ColName.get(ix));
           if (
                   objs[ix]==null && oldTuple!=null && !inTuple.containsKey(ix2ColName.get(ix))
              )
              objs[ix]=oldTuple[ix];
        }
        return objs;
    }

//    @Override
//    public Map<Object,long[]> update(Object[][] inTuples) throws CacheException
//    {
////TODO Надо посмотреть, скорее всего можно вытаскивать из бд только те данные которые изменились.
////а не все параматры по тому или иному событию, тогда здесь предусмотреть обновление только измененных полей
////т.е. тогда мы тянем на клиент не обновленный кортеж полностью, а только его часть вместе с указанием тех полей кторые поменялись
//
//        /*
//        Например структура данных может быть такой
//            long[n]
//
//            Object[k]
//
//            Для того что бы обновить таблицу мы идем по массиву long[] и ищем следующую единицу по порядку, в этом массиве должно
//            быть установлено k единиц, найдя j-ую единицу на i-м шаге мы i-й объект устанавливаем на j-ое по порядку место в рассматриваемом
//            кортеже.
//         */
//
//        if (key2Tuple ==null || key2Tuple.size()==0)
//            return reset(inTuples);
//        else
//        {
//            Map<Object,long[]> rv=new HashMap<Object,long[]>();
//            for (int i = 0, inTuplesLength = inTuples.length; i < inTuplesLength; i++)
//            {
//                Object[] tuple = inTuples[i];
//                Object key = keyGenerator.getKeyByTuple(tuple);
//
////                AtomicReferenceArray<Object[]> tuples = this.key2Tuple.get(key);
//
//                this.key2Tuple.put(key,inTuples[i]);
//                rv.put(key,lg);
//            }
//            return rv;
//        }
//    }

    @Override
    public Object[][] getTuplesByParameters(Map<String, Object> parameters, UpdateContainer containerParams) throws CacheException {
        throw new UnsupportedOperationException("This operation does not support by the simple cache");
    }

    @Override
    public Object[] getTupleByKey(Object key) throws CacheException
    {
       return key2Tuple.get(key);
    }


    @Override
    public Set<Object> getAllDataKeys() throws CacheException
    {
       return new HashSet<Object>(key2Tuple.keySet());
    }


//    public Map<Object,long[]> reset(Object[][] inTuples) throws CacheException
//    {
//        Map<Object,Object[]> tuples = new ConcurrentHashMap<Object,Object[]>();
//        Map<Object,long[]> rv= new HashMap<Object,long[]>();
//        for (Object[] inTuple : inTuples)
//        {
//            Object key = keyGenerator.getKeyByTuple(inTuple);
//            tuples.put(key, inTuple);
//            rv.put(key, lg);
//        }
//        this.key2Tuple =tuples;
//        return rv;
//    }


}
