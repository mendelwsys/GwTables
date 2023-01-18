package com.mwlib.tablo.derby;

import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.INm2IxEx;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.SQLUtils;
import com.mwlib.tablo.UpdateContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
 * Полное обновление данных
 */
public class DerbyCache implements ICache, INm2IxEx

{

    public static final int DERBY_START = -1;
    protected String tblName;
    protected  boolean test=false;

    DerbyTableOperations derbyTableOperations;

    public DerbyCache(Map<String, Object> params)
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

    public DerbyCache(String tblName,String dsCacheName)
    {
        this.tblName = tblName;
        this.derbyTableOperations=DerbyTableOperations.getDerbyTableOperations(dsCacheName);
    }
    public DerbyCache(String tblName)
    {
        this(tblName,null);
    }


    private Map<String,Integer> colName2Ix=new HashMap<String,Integer>();
    private Map<Integer,String> ix2ColName=new HashMap<Integer,String>();
    private volatile ColumnHeadBean[] cols;

    private long[] lg;
    private int transaction;

    @Override
    public ColumnHeadBean[] setMeta(ColumnHeadBean[] cols) throws CacheException
    {
        try
        {
            if (!test)
            {
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
            }
            else
            {

                cols=derbyTableOperations.getMetaTable(tblName);
                setCacheByCols(cols);

                Object[][] res=SQLUtils.execQuery(derbyTableOperations.getDerbyConnection(),"select MAX(KEY_DERBY_UPD00) from "+tblName,null);

                if (res!=null && res.length>0 && res[0][0]!=null)
                    transaction=((Long)res[0][0]).intValue()+1;//тут следующая транзакция, а не та с которой был сделан последний апдейт
                else
                    transaction=0;
            }

            return cols;
        }
        catch (Exception e)
        {
            throw new CacheException(e);
        }
    }

    protected String[] getIXColNames()
    {
        return new String[]{TablesTypes.DATA_OBJ_ID};
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

    @Override
    public UpdateContainer update(Map[] inTuples, boolean insertAllNotFound) throws CacheException
    {

        if(inTuples==null || inTuples.length==0)
            return new UpdateContainer();
        try
        {

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
            }
            else
            {
//Собрать все входящие ключи (TODO возможно рассмотреть другой вариант апдейта, когда делается прямой апдейт)
                //Выбрать из кеша данные, сформировать пул изменений
                Map<Object,Map> update0=new HashMap<Object,Map>();
                IKeyGenerator generator = getKeyGenerator();
                for (Map inTuple : inTuples)
                {
                    Object key=generator.getKeyByTuple(inTuple);
                    update0.put(key, inTuple);
                }

                changes = derbyTableOperations.getChanges(update0, tblName,lg, insertAllNotFound);

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
            return new UpdateContainer(new HashMap<>(changes),params);
        }
        catch (Exception e)
        {
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
        return removeAll(delTuples.keySet());
    }

    @Override
    public Map<Object, long[]> removeAll(Set<Object> keys4Remove) throws CacheException {

        Map<Object, long[]> rv=new HashMap<Object, long[]>();
        try {
            Object[] _key4Remove= keys4Remove.toArray(new Object[keys4Remove.size()]);


//            if (tblName.contains("PLACES"))
//            {
//                for (Object o : _key4Remove)
//                {
//                    Object[][] objs=SQLUtils.execQuery(derbyTableOperations.getDerbyConnection(),"SELECT * FROM "+tblName+" WHERE "+DerbyTableOperations.DERBYKEY+"='"+o+"'",null);
//                    if (objs!=null && objs.length>0)
//                        System.out.println("objs = " + objs[0][0]);
//
//                }
//            }




            int res[]=derbyTableOperations.deleteAll(_key4Remove,tblName);




            for (int i = 0, resLength = res.length; i < resLength; i++)
                if (res[i] > 0)
                    rv.put(_key4Remove[i],null);
            return rv;
        }
        catch (Exception e)
        {
            throw new CacheException(e);
        }
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
