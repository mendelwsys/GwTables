package com.mwlib.tablo.test.hist;

import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.INm2IxEx;
import com.mwlib.tablo.derby.DerbyTableOperations;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.SQLUtils;
import com.mwlib.tablo.UpdateContainer;
import com.mwlib.tablo.db.EventProvider;

import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 18.11.14
 * Time: 15:58
 * Кеш на основе талицы DerbyDB
 *
 * Первоначальные требования
 *
 * Создание таблицы на основе вызова метода setMeta
 * Полное обновление данных  (!!!без сброса данных!!!)
 */
public class DerbyCacheH implements ICache, INm2IxEx

{

    public static final int DERBY_START = -1;
    protected String tblName;
    protected  boolean test=false;
    protected boolean justInit=true;

    DerbyTableOperations derbyTableOperations;

    public DerbyCacheH(Map<String, Object> params)
    {
        tblName = (String)params.get(ICache.CACHENAME);
        if (tblName==null)
            throw new UnsupportedOperationException("Can't construct cache with empty tablName");
        Boolean tst= (Boolean) params.get(ICache.TEST);
        if (tst !=null)
            test=tst;

        String dsCacheName  = (String)params.get(TablesTypes.DS_CACHE_NAME);
        this.derbyTableOperations=DerbyTableOperations.getDerbyTableOperations(dsCacheName);
    }

    public DerbyCacheH(String tblName, String dsCacheName)
    {
        this.tblName = tblName;
        this.derbyTableOperations=DerbyTableOperations.getDerbyTableOperations(dsCacheName);
    }
    public DerbyCacheH(String tblName)
    {
        this(tblName,null);
    }


    private Map<String,Integer> colName2Ix=new HashMap<String,Integer>();
    private Map<Integer,String> ix2ColName=new HashMap<Integer,String>();
    private volatile ColumnHeadBean[] cols;

    private long[] lg;
    private int transaction;

    private Timestamp corTime;


    @Override
    public ColumnHeadBean[] setMeta(ColumnHeadBean[] cols) throws CacheException
    {
        System.out.println("== start setMeta Table " + tblName+" testMode:"+test);
        long ln=System.currentTimeMillis();

        try
        {


            try
            {
                cols=derbyTableOperations.getMetaTable(tblName);
                setCacheByCols(cols);

                String[] ixColNames = getIXColNames();
                List<String> newIxNames=new LinkedList<String>();
                for (String ixColName : ixColNames)
                    if (!derbyTableOperations.isTableIxExists(tblName,tblName+"_"+ixColName+"_IX"))
                        newIxNames.add(ixColName);

                if (newIxNames.size()>0)
                    derbyTableOperations.createDerbyIxTable(tblName,newIxNames.toArray(new String[newIxNames.size()]));


                Object[][] res=SQLUtils.execQuery(derbyTableOperations.getDerbyConnection(),"select MAX(KEY_DERBY_UPD00) from "+tblName,null);

                //TODO Здесь проверить  совпадение и сделать alter table если добавлись изменения
                if (res!=null && res.length>0 && res[0][0]!=null)
                    transaction=((Long)res[0][0]).intValue()+1;//тут следующая транзакция, а не та с которой был сделан последний апдейт
                else
                    transaction=0;

                Object[][] corTime=SQLUtils.execQuery(derbyTableOperations.getDerbyConnection(),"select MAX("+EventProvider.COR_MAX_TIME+") from "+tblName,null);
                if (corTime!=null && corTime.length>0 && corTime[0][0]!=null)
                    this.corTime= (Timestamp) corTime[0][0];
                else {
                    final long l = TablesTypes.YEAR_MILS;
                    this.corTime=new Timestamp(System.currentTimeMillis()- l*10l);
                }

                System.out.println("== End setMeta Table " + tblName+" transaction:"+transaction+" testMode:"+test+
                        "by Derby table, duration sec:" +(System.currentTimeMillis()-ln)/1000);
                return cols;
            }
            catch (Exception e)
            { //Если произошла ошибка т.е. нет данных и нет таблиц, тогда сброс и инициализация данных

            }

            if (!test)
            {
                System.out.println("== create Table " + tblName);
                setCacheByCols(cols);

                //проверка того что таблица есть удаление ее

                if (derbyTableOperations.isTableExists(tblName))
                    derbyTableOperations.dropIXTable(tblName, getIXColNames());

                if (derbyTableOperations.isTableExists(tblName+DerbyTableOperations.META_TABLE_EXT))
                    derbyTableOperations.dropTable(tblName + DerbyTableOperations.META_TABLE_EXT);

                //Создание таблицы с колонками
                //Установка первичного ключа согласно генератора ключа
                derbyTableOperations.createDerbyTable(tblName,cols, getIXColNames());
                derbyTableOperations.createDerbyMetaTable(tblName,cols);
                transaction=0;
                System.out.println("== End create Table " + tblName+" transaction:"+transaction+" testMode:"+test+
                        " duration sec:" +(System.currentTimeMillis()-ln)/1000);
            }
        }
        catch (Exception e)
        {
            System.out.println("!!! Error setMeta Table " + tblName+" duration sec:" +(System.currentTimeMillis()-ln)/1000+" error: ");
            e.printStackTrace();
            throw new CacheException(e);
        }
        finally
        {
            justInit=true;
        }
        return cols;
    }

    protected String[] getIXColNames()
    {
        return new String[]{TablesTypes.DATA_OBJ_ID,EventProvider.DATE_MIN_ND,EventProvider.COR_MAX_TIME};
    }

    private void setCacheByCols(ColumnHeadBean[] cols) {
        for (int i = 0, colsLength = cols.length; i < colsLength; i++)
        {
            colName2Ix.put(cols[i].getName(),i);
            ix2ColName.put(i,cols[i].getName());
        }
        this.cols=cols;

        setLGTemplate(cols);
    }

    private void setLGTemplate(ColumnHeadBean[] cols) {
        int ln = cols.length % Long.SIZE;
        lg=new long[cols.length / Long.SIZE +((ln!=0)?1:0)];
        for (int i = 0; i < lg.length; i++)
            lg[i]=-1;
    }

    @Override
    public int size() throws CacheException
    {

        Connection conn= null;
        Statement stmt=null;
        ResultSet rs = null;
        try
        {
            conn= derbyTableOperations.getDerbyConnection();
            stmt=conn.createStatement();
            rs=stmt.executeQuery("select count(*) from "+tblName);
            if (rs.next())
                return rs.getInt(1);
        }
        catch (Exception e)
        {
            throw new CacheException(e);
        }
        finally
        {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
        return 0;
    }


    private Object[] map2Tuple(Map inTuple)
    {
        Object[] objs=new Object[cols.length];
        for (Integer ix : ix2ColName.keySet())
           objs[ix]=inTuple.get(ix2ColName.get(ix));
        return objs;
    }

//    private Map tuple2Map(Object[] inTuple)
//    {
//        Map outTuple=
//        Object[] objs=new Object[cols.length];
//        for (Integer ix : ix2ColName.keySet())
//           objs[ix]=inTuple.get(ix2ColName.get(ix));
//        return objs;
//    }


    @Override
    public UpdateContainer update(Map[] inTuples, boolean insertAllNotFound) throws CacheException
    {
        long ln=System.currentTimeMillis();

        if(inTuples==null || inTuples.length==0)
            return new UpdateContainer();
        try
        {
            System.out.println("== start update Table " + tblName+" transaction:"+transaction+" testMode:"+test);

            Map<Object, long[]> changes=new HashMap<Object,long[]>();
            Map<String, Object> params = new HashMap<String, Object>();
            if (transaction==0) //Первый апдейт
            {
                if (insertAllNotFound)
                {
                    Map<Object,Object[]> update=new HashMap<Object,Object[]>();
                    IKeyGenerator generator = getKeyGenerator();
                    for (Map inTuple : inTuples)
                    {
                        Object key=generator.getKeyByTuple(inTuple);
                        update.put(key, map2Tuple(inTuple));
                    }
                    derbyTableOperations.insertAll(cols,update,tblName,transaction);
                    for (Object keyUpdate : update.keySet())
                        changes.put(keyUpdate,lg);

                    params.put(TablesTypes.DB_TRANSACTION_N+"_"+tblName,transaction);
                    transaction++;
                }
                justInit=false;
            }
            else
            {
//Собрать все входящие ключи (TODO возможно рассмотреть другой вариант апдейта, когда делается прямой апдейт)
                //Выбрать из кеша данные, сформировать пул изменений
//Здесь апдетить только те данные которые больше чем кортайм

                Map<Object,Map> update0=new HashMap<Object,Map>();
                IKeyGenerator generator = getKeyGenerator();
                Timestamp maxCorTime=null;
                for (Map inTuple : inTuples)
                {
                    Object key=generator.getKeyByTuple(inTuple);
                    update0.put(key, inTuple);

                    Timestamp _corTime = (Timestamp) inTuple.get(EventProvider.COR_MAX_TIME);
                    if (maxCorTime==null || _corTime.after(maxCorTime))
                        maxCorTime=_corTime;
                }


                if (justInit)
                {
                    System.out.println("== Start reload Table " + tblName+" transaction:"+transaction+" update size Begin:"+update0.size());

                    //Получим актальные ключи которые есть в БД
                    Object[][] res=SQLUtils.execQuery(derbyTableOperations.getDerbyConnection(),"select KEY_DERBY00 from "+tblName+" WHERE ACTUAL=1",null);
                    Set<Object> notActualKeys=new HashSet<Object>();
                    for (Object[] re : res)
                        if (!update0.containsKey(re[0]))
                            notActualKeys.add(re[0]);
                    //Далее установить неактуальность данных и установить кортайм соответсвующий
                    derbyTableOperations.updateActual(notActualKeys.toArray(new Object[notActualKeys.size()]),tblName,0,maxCorTime);
                    Set inKeys = new HashSet(update0.keySet());
                    for (Object inKey : inKeys)
                    {//фильтрация по кортайму, будем обновлять только то, что произошло после или равное кортайму
                        Timestamp tupleCorTime = (Timestamp) update0.get(inKey).get(EventProvider.COR_MAX_TIME);
                        if (tupleCorTime.before(corTime))
                            update0.remove(inKey);
                    }
                    justInit=false;

                    System.out.println("== End reload Table " + tblName+" transaction:"+transaction+" reset Actual count: " + notActualKeys.size()+
                            " by corTime Filter update size:"+update0.size()+" duration reload sec:" +(System.currentTimeMillis()-ln)/1000);
                }



                changes = derbyTableOperations.getChanges(update0, tblName,lg, insertAllNotFound, new DerbyTableOperations.ChangeCheckerImplDef()
                {
                    public boolean checkChanges(final Map newTuple, final String columnName, final Object oldVal, final Object newVal)
                    {
                        final boolean rv = super.checkChanges(newTuple, columnName, oldVal, newVal);
                        if (EventProvider.DATE_MIN_ND.equals(columnName))
                        {
                            if (newVal==null)
                                return false;
                            if (rv)
                            {
                                if (((Timestamp)oldVal).after((Timestamp)newVal))
                                    return true;
                                else
                                {
                                    newTuple.put(columnName,oldVal);
                                    return false;
                                }
                            }
                        }
                        return rv;

                    }
                });

                Map<Object,Object[]> update2=new HashMap<Object,Object[]>();
                for (Object changedKey : changes.keySet())
                {
                    long[] longs = changes.get(changedKey);
                    for (long l : longs)
                        if (l!=0)
                        {
                            update2.put(changedKey,map2Tuple(update0.get(changedKey)));
                            break;
                        }
                }

                //Удалить данные lg!=0 и вставить новые данные lg!=0
                //При этом удаление и добавление должно быть в одной транзакции
                derbyTableOperations.deleteAndInsert(cols,update2,tblName,transaction);
                if ( update2.size()>0)
                {
                    params.put(TablesTypes.DB_TRANSACTION_N+"_"+tblName,transaction);
                    transaction++;
                }
            }
            System.out.println("== End update Table " + tblName+" transaction:"+transaction+" testMode:"+test+
                    " duration sec:" +(System.currentTimeMillis()-ln)/1000);

            return new UpdateContainer(new HashMap<>(changes),params);
        }
        catch (Exception e)
        {
            System.out.println("!!! Error setMeta Table " + tblName+" transaction:"+transaction+" testMode:"+test+
                    " duration sec:" +(System.currentTimeMillis()-ln)/1000+" error: ");
            e.printStackTrace();

            throw new CacheException(e);
        }
    }

    @Override
    public UpdateContainer update(Map inTuples, boolean insertAllNotFound) throws CacheException
    {
        return update(new Map[]{inTuples}, insertAllNotFound);
    }

    @Override
    public IKeyGenerator getKeyGenerator()
    {
        return keyGenerator;
    }

    IKeyGenerator keyGenerator;
    @Override
    public void setKeyGenerator(IKeyGenerator keyGenerator) {
        this.keyGenerator=keyGenerator;
    }

    @Override
    public Map<Object, long[]> remove(Map inTuple) throws CacheException
    {
        Object key=getKeyGenerator().getKeyByTuple(inTuple);
        Set<Object> keys4Remove=new HashSet<Object>();
        keys4Remove.add(key);
        return removeAll(keys4Remove);
    }

    public Map<Object,long[]> removeTuples(Map<Object,Map> delTuples) throws CacheException
    {
        Map<Object, long[]> rv=new HashMap<Object, long[]>();
        try
        {

            Pair<Object, Timestamp>[] delPairs = new Pair[delTuples.size()];
            {
                int i=0;
                for (Object key : delTuples.keySet())
                {
                    final Map tuple = delTuples.get(key);
                    delPairs[i]=new Pair(key,(Timestamp) tuple.get(EventProvider.COR_MAX_TIME));
                    i++;
                }
            }

        /* TODO Проблемы
            1. У нас не всегда приходит удаление и узнать актальное или нет можно лишь перезапустить клиент
            2. TODO Необходима ли установка cor_time в текушее время при сбросе actual ????? смотря какие запросы мы будем делать к БД
         */

            int res[]=derbyTableOperations.updateActual(delPairs,tblName,0);
            for (int i = 0, resLength = res.length; i < resLength; i++)
                if (res[i] > 0)
                    rv.put(delPairs[i].first,null);
            return rv; //Избегать удалений на клиенте придется по другому, либо через опцию, для блокирования передачи удалений на конкретный клиент
        }
        catch (Exception e)
        {
            throw new CacheException(e);
        }
    }

    @Override
    public Map<Object, long[]> removeAll(Set<Object> keys4Remove) throws CacheException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object createContainsKey(Map inTuple) throws CacheException
    {
        Object key=getKeyGenerator().getKeyByTuple(inTuple);
        Object[] objs=getTupleByKey(key);
        if (objs!=null && objs.length>0)
            return key;
        return null;
    }

    public Object[][] getTuplesByParameters(Map<String, Object> _parameters, UpdateContainer containerParams) throws CacheException
    {
        StringBuilder bld = getSQL(_parameters);

        Connection conn= null;
        Statement stmt=null;
        ResultSet rs = null;
        try {
            conn= derbyTableOperations.getDerbyConnection();
            stmt=conn.createStatement();
            rs=stmt.executeQuery(bld.toString());
            List<Object[]> rv=new LinkedList<Object[]>();
            int cnt=rs.getMetaData().getColumnCount();
            if (cnt>0)
            {
                while (rs.next())
                {
                    Object[] objs=new Object[cnt];
                    for (int i=1;i<=cnt;i++)
                        objs[i-1]=rs.getObject(i);
                    rv.add(objs);
                }
            }
            return rv.toArray(new Object[rv.size()][]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new Object[0][];//TODO Убрать подпорку, сделана из опасений что запрос с фильтром может упасть и не вернуть данные
//            throw new CacheException(e);
        }
        finally
        {

            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    /**
     * Отдать SQL запрос по параметрам
     * @param _parameters - параметры для формирования SQL Запроса
     * @return - готовая к исполнению строка выборки
     */
    protected StringBuilder getSQL(Map<String, Object> _parameters)
    {
        StringBuilder bld= new StringBuilder("select * from "+tblName+" ");
        //формируем параметры вызова с критерием вызова
        String[] filters=(String[]) _parameters.get(TablesTypes.FILTERDATAEXPR);
        if (filters!=null && filters.length>0)
        {
            bld.append(" WHERE ");
            for (int i = 0, filtersLength = filters.length; i < filtersLength; i++) {
                if (i>0)
                    bld.append(" AND ");
                bld.append("( ").append(filters[i]).append(" )");
            }
        }
        return bld;
    }

//        Map<Integer,ValDef> ix2Param=new HashMap<Integer,ValDef>();

//   ValDef valDef = (ValDef) _parameters.get(DerbyTableOperations.DERBYUPDATE);
//        Map<String, ValDef> reqParams = new HashMap<String, ValDef>();
//        if (filters!=null)
//            for (String filter : filters)
//                if (filter!=null && filter.length()>0)
//                    reqParams= EventUtils.createFilterByJson(filter, reqParams);

//        int ix=1;
//        for (int i1 = 0, parametersLength = _parameters.length; i1 < parametersLength; i1++)
//        {
//            Map<String, Object> parameter = _parameters[i1];
//            if (i1==0)
//                bld.append(" WHERE (");
//            else
//                bld.append(" OR ( ");
//
//            ix = buildStringAndParams(parameter, bld, ix2Param,ix);
//            bld.append(" ) ");
//
//        }


            //stmt=conn.prepareStatement(bld.toString());

//            for (Integer ixSql : ix2Param.keySet())
//            {
//                ValDef valDef = ix2Param.get(ixSql);
//                String oper = valDef.getOper();
//                Object[] val = valDef.getVal();
//                if (oper.toUpperCase().contains("IN"))
//                {
//                    for (int i = 0; i < val.length; i++)
//                        stmt.setObject(ixSql+i, val[i]);
//                }
//                else if (oper.toUpperCase().contains("BETWEEN"))
//                {
//                    stmt.setObject(ixSql, val[0]);
//                    stmt.setObject(ixSql+1, val[1]);
//                }
//                else
//                    stmt.setObject(ixSql, val[0]);
//            }


//    private int buildStringAndParams(Map<String, ValDef> reqParams, StringBuilder bld, Map<Integer, ValDef> ix2Param,int ix) {
//
//
//        for (String keyP : reqParams.keySet())
//        {
//            ValDef prm=reqParams.get(keyP);
//            if (ix>1)
//                bld.append(" AND ");
//            String oper = prm.getOper();
//            Object[] val = prm.getVal();
//            if (oper.toUpperCase().contains("IN"))
//            {
//                bld.append(keyP).append(oper).append("( ");
//                for (int i = 0; i < val.length; i++)
//                {
//                    if (i>0)
//                        bld.append(",");
//                    bld.append(" ? ");
//                    ix2Param.put(ix,prm);
//                    ix++;
//                }
//                bld.append(" )");
//            }
//            else if (oper.toUpperCase().contains("BETWEEN"))
//            {
//                bld.append("( ").append(keyP).append(" ").append(oper).append(" ? AND ? )");
//                ix2Param.put(ix,prm);
//                ix+=2;
//            }
//            else
//            {
//                if (val ==null || val.length==0 || val[0]==null)
//                    bld.append(keyP).append(oper);
//                else
//                {
//                    bld.append(keyP).append(" ").append(oper).append(" ?");
//                    ix2Param.put(ix,prm);
//                    ix++;
//                }
//            }
//        }
//        return ix;
//    }
//

    @Override
    public Object[] getTupleByKey(Object key) throws CacheException
    {
        Connection conn= null;
        PreparedStatement stmt=null;
        ResultSet rs = null;
        try {
            conn= derbyTableOperations.getDerbyConnection();
            stmt=conn.prepareStatement("select * from "+tblName+ " WHERE "+DerbyTableOperations.DERBYKEY+"= ?");
            stmt.setObject(1,key);
            rs=stmt.executeQuery();
            int cnt=rs.getMetaData().getColumnCount();
            Object[] objs=null;
            if (cnt>0)
            {
                objs=new Object[cnt];
                if (rs.next())
                    for (int i=1;i<=cnt;i++)
                        objs[i-1]=rs.getObject(i);
            }
            return objs;
        }
        catch (Exception e)
        {
            throw new CacheException(e);
        }
        finally
        {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    @Override
    public Set<Object> getAllDataKeys() throws CacheException
    {
        Connection conn= null;
        Statement stmt=null;
        ResultSet rs = null;
        Set<Object> rv=new HashSet<Object>();
        try {
            conn= derbyTableOperations.getDerbyConnection();
            stmt=conn.createStatement();
            rs=stmt.executeQuery("select "+DerbyTableOperations.DERBYKEY+" from "+tblName);
            while (rs.next())
            {
                rv.add(rs.getObject(1));
            }
            return rv;
        }
        catch (Exception e)
        {
            throw new CacheException(e);
        }
        finally
        {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    @Override
    public String getColNameByIx(int ix)
    {

        String name = ix2ColName.get(ix);
//        if (name==null)
//        {
//            if (ix)
//        }
        return name;
    }

    @Override
    public Integer getIxByColName(String colName)
    {
        Integer rv = colName2Ix.get(colName);

//        if (rv==null)
//        {
//            if (DerbyTableOperations.DERBYKEY.equals(colName))
//                return colName2Ix.size();
//            else if (DerbyTableOperations.DERBYUPDATE.equals(colName))
//                return colName2Ix.size()+1;
//        }
        return rv;
    }

    @Override
    public ColumnHeadBean[] getMeta() {
        return cols;
    }

    @Override
    public Map<String, Integer> getColName2Ix() {
        return colName2Ix;
    }

    @Override
    public Map<Integer, String> getIx2ColName() {
        return ix2ColName;
    }

    @Override
    public Map<String, Integer> getAllColName2Ix()
    {
        Map<String, Integer> rv = new HashMap<String, Integer>(getColName2Ix());

        rv.put(DerbyTableOperations.DERBYUPDATE,rv.size());
        rv.put(DerbyTableOperations.DERBYKEY,rv.size());
        return rv;
    }
}
