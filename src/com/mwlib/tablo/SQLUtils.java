package com.mwlib.tablo;

import com.mwlib.tablo.db.DbUtils;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.tables.ColumnHeadBean;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 04.06.15
 * Time: 13:52
 * Некоторые утилиты для запроспа данных
 */
public class SQLUtils
{

    public static String constructSQLRequest(String pSQL, Map<String, List<Integer>> name2pos)
    {
        if (pSQL!=null)
        {
            int ixSemi=0;
            int ix=1;
            while ((ixSemi=pSQL.indexOf(":",ixSemi))>=0)
            {
                int ixEnd=pSQL.indexOf(" ",ixSemi);
                if (ixEnd<0)
                    ixEnd=pSQL.length();
                String name=pSQL.substring(ixSemi,ixEnd);
                name = name.substring(1);

                List<Integer> positions = name2pos.get(name);
                if (positions==null)
                    name2pos.put(name, positions = new LinkedList<Integer>());
                positions.add(ix);

                pSQL=pSQL.substring(0,ixSemi)+" ? "+pSQL.substring(ixEnd);
            }
        }
        return pSQL;
    }

    public static Object[][] execSQL(Connection conn,String pSQL, Map<String, Object> params, Map<String, List<Integer>> name2pos,List<ColumnHeadBean> listOfColumn)  throws Exception
    {
        PreparedStatement stmt=null;
        ResultSet rs = null;
        try
        {
            stmt=conn.prepareStatement(pSQL);
            for (String nameP : params.keySet())
            {
                List<Integer> lPos = name2pos.get(nameP);
                for (Integer lPo : lPos)
                    stmt.setObject(lPo,params.get(nameP));
            }

            rs=stmt.executeQuery();

            if (listOfColumn!=null)
                setMetaData(rs,listOfColumn);


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
        finally
        {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    public static Object[][] execQuery(Connection conn,String query,List<ColumnHeadBean> listOfColumn) throws Exception
    {
        Statement stmt=null;
        ResultSet rs = null;
        try
        {
            stmt=conn.createStatement();
            rs=stmt.executeQuery(query);

            if (listOfColumn!=null)
                setMetaData(rs,listOfColumn);

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
        finally
        {
            DbUtil.closeAll(rs, stmt, conn, true);
        }
    }

    public static void setMetaData(ResultSet rs,List<ColumnHeadBean> listOfColumn) throws Exception
    {
        ResultSetMetaData meta = rs.getMetaData();
        int colCnt = meta.getColumnCount();
        for (int i = 1; i <= colCnt; i++)
            listOfColumn.add(new ColumnHeadBean(meta.getColumnName(i),meta.getColumnName(i), DbUtils.translate2AttrTypeByClassName(meta.getColumnClassName(i))));
    }


}
