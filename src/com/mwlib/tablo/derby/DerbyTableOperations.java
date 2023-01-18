package com.mwlib.tablo.derby;

import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.io.IOUtil;
import com.mycompany.common.Pair;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.db.EventProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 18.11.14
 * Time: 16:51
 * Некоторые утилиты для кеша дерби.
 */
public class DerbyTableOperations
{

    public static final String DERBYKEY = "KEY_DERBY00";
    public static final String DERBYUPDATE = "KEY_DERBY_UPD00";
    public static final String META_TABLE_EXT = "_META";
    public static final String TABLE_NAME_COL = "TABLE_NAME";
    public static final String TABLE_TYPE = "TABLE";
    public static final String COUNT_REC_COL = "count";
    private String dsCacheName;

    private DerbyTableOperations(String dsCacheName)
    {
        this.dsCacheName = dsCacheName;
    }


    public static final ConcurrentMap<String,DerbyTableOperations> derbyes=new ConcurrentHashMap<String,DerbyTableOperations>();

    public static DerbyTableOperations getDefDerbyTableOperations()
    {
        return getDerbyTableOperations(DbUtil.DS_JAVA_CACHE_NAME);
    }

    public static DerbyTableOperations getDerbyTableOperations(String dsCacheName)
    {

        if (dsCacheName==null || dsCacheName.length()==0)
            return getDefDerbyTableOperations();

        if (!derbyes.containsKey(dsCacheName))
            derbyes.putIfAbsent(dsCacheName,new DerbyTableOperations(dsCacheName));
        return derbyes.get(dsCacheName);
    }


    public Connection getDerbyConnection() throws Exception {
        return DbUtil.getConnection2(this.dsCacheName);//
    }

//TODO Внимание адейт должен происходить в одной транзации!!!! Иначе у нас обновления могут потеряться

    public int[] deleteAll(Object[] key4Delete, String tableName) throws Exception
    {
        PreparedStatement stmt=null;
        Connection conn= null;
        Boolean bl=null;
        try
        {
            conn= getDerbyConnection();
            bl=conn.getAutoCommit();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement("DELETE FROM "+tableName+" WHERE "+DERBYKEY+"=?");
            for (Object key : key4Delete)
            {
                stmt.setObject(1,key);
                stmt.addBatch();

            }
            int[] res=stmt.executeBatch();
            conn.commit();
            return res;
        }
        finally
        {
            try {
                if (conn!=null && bl!=null)
                    conn.setAutoCommit(bl);
            } catch (SQLException e) {
                // ignore
            }
            DbUtil.closeAll(null, stmt, conn, true);
        }
    }



    public void deleteNotActualByCorTime(String tableName, Timestamp timestamp) throws Exception
    {
        PreparedStatement stmt=null;
        Connection conn= null;
        try
        {
            conn= getDerbyConnection();
            stmt = conn.prepareStatement("DELETE FROM " + tableName + " WHERE ACTUAL=0 and " + EventProvider.COR_MAX_TIME + "< ? ");
            stmt.setObject(1,timestamp);
            stmt.execute();
        }
        finally
        {
            DbUtil.closeAll(null, stmt, conn, true);
        }
    }


    public int[] updateActual(Object[] key4Delete, String tableName,int actual,Timestamp corTime) throws Exception
    {
        PreparedStatement stmt=null;
        Connection conn= null;
        Boolean bl=null;
        try
        {
            conn= getDerbyConnection();
            bl=conn.getAutoCommit();
            conn.setAutoCommit(false);

            if (corTime!=null)
            {
                stmt = conn.prepareStatement("UPDATE "+tableName+" SET ACTUAL="+ actual+","+ EventProvider.COR_MAX_TIME+"=? WHERE "+DERBYKEY+"=?");
                for (Object key : key4Delete)
                {
                    stmt.setObject(1,corTime);
                    stmt.setObject(2,key);
                    stmt.addBatch();

                }
            }
            else
            {
                stmt = conn.prepareStatement("UPDATE "+tableName+" SET ACTUAL="+ actual+" WHERE "+DERBYKEY+"=?");
                for (Object key : key4Delete)
                {
                    stmt.setObject(1,key);
                    stmt.addBatch();
                }
            }
            int[] res=stmt.executeBatch();
            conn.commit();
            return res;
        }
        finally
        {
            try {
                if (conn!=null && bl!=null)
                    conn.setAutoCommit(bl);
            } catch (SQLException e) {
                // ignore
            }
            DbUtil.closeAll(null, stmt, conn, true);
        }
    }


    public int[] updateActual(Pair<Object,Timestamp>[] key4Delete, String tableName,int actual) throws Exception
    {
        PreparedStatement stmt=null;
        Connection conn= null;
        Boolean bl=null;
        try
        {
            conn= getDerbyConnection();
            bl=conn.getAutoCommit();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement("UPDATE "+tableName+" SET ACTUAL="+ actual+","+ EventProvider.COR_MAX_TIME+"=? WHERE "+DERBYKEY+"=?");
            for (Pair pair : key4Delete)
            {
                stmt.setObject(1,pair.second);
                stmt.setObject(2,pair.first);
                stmt.addBatch();

            }

            int[] res=stmt.executeBatch();
            conn.commit();
            return res;
        }
        finally
        {
            try {
                if (conn!=null && bl!=null)
                    conn.setAutoCommit(bl);
            } catch (SQLException e) {
                // ignore
            }
            DbUtil.closeAll(null, stmt, conn, true);
        }
    }



    public void deleteAndInsert(ColumnHeadBean[] cols, Map<Object,Object[]> newKeys, String tableName,long transactionNumber)  throws Exception
    {
        if (newKeys!=null && newKeys.size()>0)
        {
            //1.Создать запрос на загрузку таблицы
            StringBuilder itbl = new StringBuilder(tableName).append(" ( ");
            StringBuilder valStr = new StringBuilder("(");
            for (ColumnHeadBean col : cols)
            {
                itbl = itbl.append(translateColumnName(col.getName())).append(" ,");
                valStr=valStr.append("?,");

            }
            itbl = itbl.append(DERBYUPDATE).append(" ,");
            itbl = itbl.append(DERBYKEY).append(" )  values ");
            valStr=valStr.append("?,? )");
            StringBuilder insertSQL = new StringBuilder("insert into ").append(itbl).append(valStr);


            PreparedStatement stmt=null;
            Connection conn= null;
            Boolean bl=null;

            try {
                conn= getDerbyConnection();
                bl=conn.getAutoCommit();
                conn.setAutoCommit(false);

                stmt = conn.prepareStatement("DELETE FROM "+tableName+" WHERE "+DERBYKEY+"=?");
                for (Object key : newKeys.keySet())
                {
                    stmt.setObject(1,key);
                    stmt.addBatch();
                }
                stmt.executeBatch(); //TODO ?!?!?!?!?!? Не потеряем мы этот стайтмент

                stmt = conn.prepareStatement(insertSQL.toString());
                for (Object key : newKeys.keySet())
                {
                    Object[] newTuple = newKeys.get(key);
                    for (int i1 = 0, ovalsLength = newTuple.length; i1 < ovalsLength; i1++)
                    {

                        final Object o = newTuple[i1];
//                        if (o ==null)
//                            stmt.setNull(i1+1, DbUtils.translate2SqlCode(cols[i1].getType()));
//                        else
                            stmt.setObject(i1+1,o);
                    }
                    stmt.setLong(newTuple.length+1,transactionNumber);
                    stmt.setObject(newTuple.length+2,key);
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
            }
            finally
            {
                try {
                    if (conn!=null && bl!=null)
                        conn.setAutoCommit(bl);
                } catch (SQLException e) {
                    // ignore
                }
                DbUtil.closeAll(null, stmt, conn, true);
            }

        }
    }


    public void insertAll(ColumnHeadBean[] cols, Map<Object,Object[]> newKeys, String tableName,long transactionNumber) throws Exception
    {
        if (newKeys!=null && newKeys.size()>0)
        {
                //1.Создать запрос на загрузку таблицы
                StringBuilder itbl = new StringBuilder(tableName).append(" ( ");
                StringBuilder valStr = new StringBuilder("(");
                for (ColumnHeadBean col : cols)
                {
                    itbl = itbl.append(translateColumnName(col.getName())).append(" ,");
                    valStr=valStr.append("?,");

                }
                itbl = itbl.append(DERBYUPDATE).append(" ,");
                itbl = itbl.append(DERBYKEY).append(" )  values ");
                valStr=valStr.append("?,? )");
                StringBuilder insertSQL = new StringBuilder("insert into ").append(itbl).append(valStr);

                PreparedStatement stmt=null;
                Connection conn= null;
                Boolean bl=null;
                try
                {
                    conn= getDerbyConnection();
                    bl=conn.getAutoCommit();
                    conn.setAutoCommit(false);
                    stmt = conn.prepareStatement(insertSQL.toString());
                    for (Object key : newKeys.keySet())
                    {

                        Object[] newTuple = newKeys.get(key);
                        for (int i1 = 0, ovalsLength = newTuple.length; i1 < ovalsLength; i1++)
                        {
                            final Object o = newTuple[i1];
//                            if (o ==null)
//                                stmt.setNull(i1+1, DbUtils.translate2SqlCode(cols[i1].getType()));
//                            else
                               stmt.setObject(i1+1, o);
                        }
                        stmt.setLong(newTuple.length + 1, transactionNumber);
                        stmt.setObject(newTuple.length + 2, key);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                    conn.commit();
                }
                finally
                {
                    try {
                        if (conn!=null && bl!=null)
                            conn.setAutoCommit(bl);
                    } catch (SQLException e) {
                        // ignore
                    }
                    DbUtil.closeAll(null, stmt, conn, true);
                }
        }
    }


    public boolean isTableExists(String tblName)  throws Exception
    {
        Connection conn=null;
        ResultSet rs = null;

        try
        {
            conn= getDerbyConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getTables(null, null, tblName, null);

            return rs.next();

        }
        finally
        {
            DbUtil.closeAll(rs, null, conn, true);
        }
    }


    public boolean isTableIxExists(String tblName,String tblIxName)  throws Exception
    {
        Connection conn=null;
        ResultSet rs = null;

        try
        {
            conn= getDerbyConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getIndexInfo(null, null,tblName, false,false);
            while (rs.next())
            {
                if (tblIxName.equals(rs.getString("INDEX_NAME")))
                    return true;
            }
            return false;

        }
        finally
        {
            DbUtil.closeAll(rs, null, conn, true);
        }
    }



    public  void dropIXTable(String tblName, String[] ixColNames)  throws Exception
    {
        Connection conn=null;
        Statement stmt = null;
        try
        {
            conn= getDerbyConnection();
            stmt = conn.createStatement();
            try
            {
                stmt.execute("DROP INDEX " + tblName + "_IX");
            }
            catch (Exception e)
            {
                // ignore
            }

            for (String ixColName : ixColNames)
            {
                try
                {
                    stmt.execute("DROP INDEX " + tblName+"_"+ ixColName+"_IX");
                }
                catch (Exception e)
                {
                    // ignore
                }
                }

            try
            {
                stmt.execute("DROP TABLE " + tblName);
            }
            catch (Exception e)
            {
                // ignore
            }
        }
        finally
        {
            DbUtil.closeAll(null,stmt, conn, true);
        }
    }

    public void dropTable(String tblName)  throws Exception
    {
        Connection conn=null;
        Statement stmt = null;
        try
        {
            conn= getDerbyConnection();
            stmt = conn.createStatement();
            stmt.execute("DROP TABLE "+tblName);
        }
        finally
        {
            DbUtil.closeAll(null,stmt, conn, true);
        }
    }

    public Pair<String,Integer>[] getMetaInfoByTable(String tblName)  throws Exception
    {
        List<Pair<String,Integer>> rv=new LinkedList<Pair<String,Integer>>();

        Connection conn=null;
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            conn= getDerbyConnection();
            stmt = conn.createStatement();
            rs=stmt.executeQuery("SELECT * FROM "+tblName);
            ResultSetMetaData meta = rs.getMetaData();
            int cnt=meta.getColumnCount();
            for (int i=1;i<=cnt;i++)
                rv.add(new Pair<String,Integer>(meta.getColumnName(i),meta.getColumnType(i)));
            return rv.toArray(new Pair[rv.size()]);
        }
        finally
        {
            DbUtil.closeAll(rs,stmt, conn, true);
        }
    }


    //Ох метод очень рискованый,поскольку может легче передать все таплы чем выискивать измененения
    public Map<Object,long[]> getChanges(Map<Object, Map> key2Updates, String tableName, long[] _lg, boolean insertAllNotFound) throws Exception
    {
        return getChanges(key2Updates,tableName,_lg,insertAllNotFound,new ChangeCheckerImplDef());
    }

    public Map<Object,long[]> getChanges(Map<Object, Map> key2Updates, String tableName, long[] _lg, boolean insertAllNotFound,IChangeChecker changeChecker) throws Exception
    {
        Map<Object,long[]> rv= new HashMap<Object,long[]>();

        PreparedStatement stmt=null;
        Connection conn= null;
        ResultSet rs = null;
        try
        {
            conn= getDerbyConnection();
            stmt = conn.prepareStatement("SELECT * FROM "+tableName+" WHERE "+DerbyTableOperations.DERBYKEY+"=?");
            for (Object key : key2Updates.keySet())
            {
                stmt.setObject(1,key);
                rs=stmt.executeQuery();
                if (rs.next())
                {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int cnt= metaData.getColumnCount();
                    Map newTuple=key2Updates.get(key);
                    final long[] lg=new long[_lg.length];

                    for (int ix=0;ix<cnt;ix++)
                    {
                        Object oldVal=rs.getObject(ix+1);
                        String columnName = metaData.getColumnName(ix + 1);

                        if (newTuple.containsKey(columnName))
                        {
                            Object newVal=newTuple.get(columnName);
                            if  (
                                    changeChecker.checkChanges(newTuple, columnName, oldVal, newVal)
                                )
                            {
                                long mask=1l<<(ix%Long.SIZE);
                                lg[ix/Long.SIZE]|=mask;
                            }
                        }
                        else
                            newTuple.put(columnName,oldVal);
                    }
                    rv.put(key,lg);
                }
                else if (insertAllNotFound) //Добавим только если необходимо инсертить не найденые кортежи
                    rv.put(key,_lg);
            }
            return rv;
        }
        finally
        {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    public static interface IChangeChecker
    {
        boolean checkChanges(Map newTuple, String columnName, Object oldVal, Object newVal);
    }

    public static class ChangeCheckerImplDef implements IChangeChecker
    {
        public boolean checkChanges(final Map newTuple, final String columnName, final Object oldVal, final Object newVal)
        {
            return (oldVal!=null && !oldVal.equals(newVal)) || (newVal!= null && !newVal.equals(oldVal));
        }
    }

    public ColumnHeadBean[] getMetaTable(String tblName) throws Exception
    {
        Connection conn=null;
        Statement stmt=null;
        ResultSet rs = null;
        try
        {
            conn= getDerbyConnection();
            stmt=conn.createStatement();
            rs = stmt.executeQuery("SELECT COLSBLOB FROM "+tblName+META_TABLE_EXT);
            if(rs.next())
            {
                Blob ablob = rs.getBlob(1);
                InputStream is = ablob.getBinaryStream();

                final int length = (int) ablob.length();
                ByteArrayOutputStream bos;
                if (length>0)
                    bos= new ByteArrayOutputStream(length);
                else
                    bos= new ByteArrayOutputStream();

                {
                    byte buff[] = new byte[7*1024];
                    for (int b = is.read(buff); b != -1; b = is.read(buff))
                        bos.write(buff, 0, b);
                    bos.close();
                    is.close();
                }
                return (ColumnHeadBean[])IOUtil.deserializeObject(bos.toByteArray());
            }
            return new ColumnHeadBean[0];
        }
        finally
        {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    public Map[] getMetricsDerbyTables() throws Exception
    {
        return getMetricsDerbyTables(null);
    }

    public Map[] getMetricsDerbyTables(ITableFilter filter) throws Exception
    {

        String[] tblNames = getTablesbyFilter(filter);

        List<Map> rv= new LinkedList<Map>();
        {
            Connection conn=null;
            Statement stmt=null;
            ResultSet rs = null;
            try
            {
                conn=getDerbyConnection();
                stmt=conn.createStatement();
                for (String tblName : tblNames)
                {
                   rs=stmt.executeQuery("SELECT count(*) FROM "+tblName);
                   Map map=new HashMap();
                   map.put(TABLE_NAME_COL,tblName);
                   if (rs.next())
                       map.put(COUNT_REC_COL,rs.getInt(1));
                    rv.add(map);
                }
            }
            finally
            {
                DbUtil.closeAll(rs,stmt,conn,true);
            }
        }
        return rv.toArray(new Map[rv.size()]);
    }

    public String[] getTablesbyFilter(ITableFilter filter) throws Exception {
        List<String> tblNames= new LinkedList<String>();
        {
            ResultSet rs = null;
            Connection conn=null;
            try {
                conn=getDerbyConnection();
                DatabaseMetaData dbmd = conn.getMetaData();
                rs = dbmd.getTables(null, null, null,new String[]{TABLE_TYPE});
                while (rs.next())
                    if (filter==null || filter.isFit(rs))
                        tblNames.add(rs.getString(TABLE_NAME_COL));
            }
            finally
            {
                DbUtil.closeAll(rs, null, conn, true);
            }
        }
        return tblNames.toArray(new String[tblNames.size()]);
    }

    public void createDerbyMetaTable(String tblName, ColumnHeadBean[] cols) throws Exception
    {
        tblName=tblName+ META_TABLE_EXT;

        Connection conn=null;
        PreparedStatement stmt=null;
        ResultSet rs = null;
        try
        {
            conn= getDerbyConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getTables(null, null, tblName, null);
            if(!rs.next())
            {

                byte[] serCols = IOUtil.serializeObject(cols);
                //Создаем сначала таблицу для хранения
                String createTbl="create table "+tblName +" ( COLSBLOB BLOB )";
                stmt = conn.prepareStatement(createTbl);
                stmt.execute();
                stmt.close();
                stmt = conn.prepareStatement("INSERT INTO "+tblName+" VALUES (?)");
                ByteArrayInputStream stream = new ByteArrayInputStream(serCols);
                stmt.setBinaryStream(1, stream,serCols.length);
                stmt.execute();
            }
        }
        finally
        {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    public void createDerbyTable(String tblName, ColumnHeadBean[] cols, String[] ixColNames) throws Exception
    {
        Connection conn=null;
        Statement stmt=null;
        ResultSet rs = null;

        try {
            conn= getDerbyConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getTables(null, null, tblName, null);

            if(!rs.next())
            {


                //Создаем сначала таблицу
                //1.Создать запрос на создание
                StringBuffer tbl=new StringBuffer(tblName+" ( ");
                for (ColumnHeadBean col : cols)
                {
                    String colName=translateColumnName(col.getName());
                    tbl = tbl.append(colName).append(" ").append(DerbyUtils.translate2DerbyType(col.getType())).append(" ,");
                }
                tbl = tbl.append(DERBYUPDATE).append(" BIGINT ,");
                tbl = tbl.append(DERBYKEY).append(" VARCHAR (255) "+", PRIMARY KEY ("+DERBYKEY+")"+"  )");

                String createTbl="create table "+tbl.toString();
                stmt = conn.createStatement();
                stmt.execute(createTbl);


                stmt.execute("CREATE INDEX "+tblName+"_IX ON "+tblName+" ("+DERBYUPDATE+")");
                for (String ixColName : ixColNames)
                    stmt.execute("CREATE INDEX "+tblName+"_"+ ixColName+"_IX ON "+tblName+" ("+ixColName+")");
            }
        }
        finally
        {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }


    public void createDerbyIxTable(String tblName,  String[] ixColNames) throws Exception
    {
        Connection conn=null;
        Statement stmt=null;
        ResultSet rs = null;

        try {
            conn= getDerbyConnection();
            stmt = conn.createStatement();
            for (String ixColName : ixColNames)
                stmt.execute("CREATE INDEX "+tblName+"_"+ ixColName+"_IX ON "+tblName+" ("+ixColName+")");
        }
        finally
        {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }


    private String translateColumnName(String colName) {
        if ("work".equalsIgnoreCase(colName))
            colName="\""+colName+"\"";
        return colName;
    }


    public static interface ITableFilter
    {
        boolean isFit(ResultSet rs) throws Exception;
    }
}
