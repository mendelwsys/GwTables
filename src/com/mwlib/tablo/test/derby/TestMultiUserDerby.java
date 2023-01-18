package com.mwlib.tablo.test.derby;

import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.derby.pred.ConsolidatorLoader;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 18.11.14
 * Time: 11:17
 *
 */



public class TestMultiUserDerby implements Runnable

{
    public static void main(String[] args) throws ClassNotFoundException, SQLException
    {

        //Class.forName("org.apache.derby.jdbc.ClientDriver");
//        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        final Connection conn = DriverManager.getConnection("jdbc:derby:C:/PapaWK/Projects/JavaProj/SGWTVisual2/db/udb;create=true");
        //conn.setReadOnly(true);

//        Thread t2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
////                Connection conn = null;
//                Statement stmt = null;
//                ResultSet results = null;
//
//                try {
////                    conn = DriverManager.getConnection("jdbc:derby:C:/PapaWK/Projects/JavaProj/SGWTVisual2/db/udb;create=true");
//                    stmt = conn.createStatement();
//
//
//                    for (int i=0;i<10;i++)
//                    {
//                        try {
//                            results = stmt.executeQuery("select count(*) from " + TablesTypes.WINDOWS_OVERTIME);
//                            while(results.next())
//                            {
//                                String cnt= results.getString(1);
//                                System.err.println("cnt11 = " + cnt + " i=" + i);
//                            }
//                        }
//                        finally
//                        {
//                            DbUtil.closeAll(results, null, null, false);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    DbUtil.closeAll(null, stmt, null, true);
//                }
//            }
//        });


        try
        {
            for (int i=0;i<1;i++)
            {
                new Thread(new TestMultiUserDerby(i,conn)).start();
            }
            total=1;
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {

        }
    }

    int ix=0;
    Connection conn;
    static int total=0;

    TestMultiUserDerby(int ix,Connection conn)
    {
        this.ix=ix;
        this.conn=conn;
    }

    @Override
    public void run()
    {
        Statement stmt = null;
        ResultSet results = null;
        Connection conn = null;
        try {

            conn = DriverManager.getConnection("jdbc:derby:C:/PapaWK/Projects/JavaProj/SGWTVisual2/db/udb;create=true");
            while (total==0)
            {
                Thread.sleep(100);
            }


            long ln=System.currentTimeMillis();




            String[] strs=new String[]{"Э","П","Ш"};
            for (int i = 0; i < 1; i++)
            {
                try
                {


                    //String sql = "select * from " + TablesTypes.WINDOWS + " WHERE " + ConsolidatorLoader.DERBYUPDATE + ">=1 AND DOR_KOD=86 AND O_SERV='"+strs[i%3]+"' AND COMMENT like '%зажим%'";
                    String sql = "select "+ ConsolidatorLoader.DERBYKEY+" from " + TablesTypes.WINDOWS;
                    stmt = conn.createStatement();
                    results = stmt.executeQuery(sql);//
//                    ResultSetMetaData rsmd = results.getFieldsMetaDS();
//            int numberCols = rsmd.getColumnCount();
//            for (int k=1; k<=numberCols; k++)
//            {
//                //print Column Names
//                System.out.print(rsmd.getColumnLabel(k)+"\n");
//            }
                    Set<Object> allKeys=new HashSet<Object>();
                    while (results.next())
                    {
                        //System.err.println("resssx1 = " + results.getString(3) + " i=" + i+" k "+ix);
                        allKeys.add(results.getObject(1));
//                        System.out.println("res = " + res);
                    }
                    results.close();
                    stmt.close();

                    ln=System.currentTimeMillis();

                    PreparedStatement pstmt = conn.prepareStatement("select * from " + TablesTypes.WINDOWS + " WHERE " + ConsolidatorLoader.DERBYKEY + "= ? and DOR_KOD=?");
//                    PreparedStatement pstmt = conn.prepareStatement("select * from " + TablesTypes.WINDOWS + " WHERE " + ConsolidatorLoader.DERBYKEY + "= ?");

//                    int k=0;
                    for (Object key : allKeys)
                    {

                        pstmt.setObject(1,key);
                        pstmt.setObject(2,new BigDecimal("83"));
                        results = pstmt.executeQuery();//
                        if (results.next())
                        {
                            int cnt=results.getMetaData().getColumnCount();
                            for (int ix=1;ix<=cnt;ix++)
                                results.getObject(ix);
                        }
//                        k++;

                    }
                    pstmt.close();
                }
                finally
                {
                    DbUtil.closeAll(results, null,null, false);
                }
            }
            System.err.println("End the test ix "+ix+" ln="+(System.currentTimeMillis()-ln));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //DbUtil.closeAll(null, stmt, null, false);
            DbUtil.closeAll(null, stmt, conn, true);
        }
    }

}
