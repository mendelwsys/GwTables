package com.mwlib.tablo.test.derby;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 02.09.14
 * Time: 14:31
 *  Тестовый пример derbyDB
 */
public class TestDerby
{
    private static String dbURL = "jdbc:derby:C:/PapaWK/Projects/JavaProj/db/udb;create=true";




    // jdbc Connection
    private static Connection conn = null;
    private static Statement stmt = null;
    private static String tableName = "user2event";


    private static void createTTable() throws SQLException
    {
        Statement stmt = conn.createStatement();
        String query ="CREATE TABLE "+tableName+" (uid INTEGER NOT NULL,eventId CHAR(127) NOT NULL,PRIMARY KEY (uid, eventId) )";
        stmt.execute(query);
    }


     public static void insertUser2Event(int uid, String eventId) throws SQLException
    {

            if (conn==null)
                createConnection();

            stmt = conn.createStatement();
            stmt.execute("insert into "+tableName+" values (" +
                    uid + ",'" + eventId +"')");
            stmt.close();
    }

    private static void createConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            //Get a connection
            conn = DriverManager.getConnection(dbURL);
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }

    public static boolean deleteEvents(int userId,String eventId) throws SQLException
    {
        if (conn==null)
            createConnection();

        stmt = conn.createStatement();
        return stmt.execute("delete from " + tableName +" where uid="+userId+" and eventId ='"+eventId+"'");
    }

    public static Set<String> selectEventsById(int userId) throws SQLException
    {
        Set<String> rv= new HashSet<String>();

            if (conn==null)
                createConnection();

            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("select * from " + tableName +" where uid="+userId);
//            ResultSetMetaData rsmd = results.getFieldsMetaDS();
//            int numberCols = rsmd.getColumnCount();
//            for (int i=1; i<=numberCols; i++)
//            {
//                //print Column Names
//                System.out.print(rsmd.getColumnLabel(i)+"\t\t");
//            }

            System.out.println("\n-------------------------------------------------");

            while(results.next())
            {
//                int id = results.getInt(1);
                rv.add(results.getString(2));
//                System.out.println(id + "\t\t" + restName);
            }
            results.close();
            stmt.close();

        return rv;
    }

    public static void shutdown()
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
            if (conn != null)
            {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
                conn.close();
            }
        }
        catch (SQLException sqlExcept)
        {

        }
    }

    public static void main(String[] args) throws Exception {
        createConnection();
        //createTTable();
        insertUser2Event(1,"TestEv3");
        selectEventsById(1);
        shutdown();
    }

}
