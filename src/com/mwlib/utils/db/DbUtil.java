package com.mwlib.utils.db;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public final class DbUtil
{
    //private static final Logger logger = SpkuLog.getInstance().getSpkuLogger();
    private static boolean jdbcConnection = true;
    private static String jdbcName = null;
    private static String jdbcPassword = null;
    private static String CONNECTION_PROPERTIES_PATH = "connection.properties";
    private static String PROPERTY_CONNECTION = "connection";
    private static String PROPERTY_JDBC_NAME = "jdbc_name";
    private static String PROPERTY_JDBC_PASSWORD = "jdbc_password";
    private static String PROPERTY_VALUE_JDBC = "jdbc";



//    static
//    {
//        jdbcConnection = readConnectionParams();
//
//        DataSource ds_l = null;
//        InitialContext initialContext = null;
//        try {
//            initialContext = new InitialContext();
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
//
//        if (initialContext!=null)
//        {
//            try {
//                ds_l = (DataSource) initialContext.lookup(DS_ORA_NAME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                if (ds_l==null)
//                {
//                    Context envCtx = (Context) initialContext.lookup("java:comp/env");
//                    ds_l = (DataSource) envCtx.lookup(DS_ORA_NAME);
//                }
//            } catch (NamingException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (ds_l!=null)
//          ds = ds_l;
//
//    }

    //
    private DbUtil(){}
    //
    private static boolean readConnectionParams() {
        String connectionType = System.getProperty(PROPERTY_CONNECTION);
        if (PROPERTY_VALUE_JDBC.equalsIgnoreCase(connectionType)) {
            jdbcName = System.getProperty(PROPERTY_JDBC_NAME);
            jdbcPassword = System.getProperty(PROPERTY_JDBC_PASSWORD);
            return true;
        } else {
            return false;
        }

    }

    //
    private static void closeContext(Context context) throws NamingException
    {
        context.close();
    }   



    public static String DS_ORA_NAME = "jdbc/tabl";
    public static String DS_DB2_NAME = "jdbc/cnsi";
    public static String DS_DB2_NAME2 = "jdbc/cnsi2";
    public static String DS_JAVA_NAME = "jdbc/db";
    public static String DS_JAVA_CACHE_NAME = "jdbc/dbc";
    public static String DS_JAVA_CACHE_H_NAME = "jdbc/dbcH";

    public interface IJdbCOnnection
    {
        Connection getConnection() throws ClassNotFoundException, SQLException;
    }

    public static Map<String,Connection> name2Connection= new ConcurrentHashMap<String,Connection>();
    public static Map<String,IJdbCOnnection> name2Connector= new ConcurrentHashMap<String,IJdbCOnnection>();
    public static final String DB_PATH = "C:/PapaWK/Projects/JavaProj/SGWTVisual2/";

    static
    {
        name2Connector.put(DS_ORA_NAME,new IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                return getOracleJdbcConnection();
            }
        });
        name2Connector.put(DS_DB2_NAME,new IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                return getJdbcDb2Connection();
            }
        });

        name2Connector.put(DS_DB2_NAME2,new IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                return getJdbcDb2Connection2();
            }
        });

        name2Connector.put(DS_JAVA_NAME,new IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                return DriverManager.getConnection("jdbc:derby:" + DB_PATH + "db/udb;create=true");
            }
        });


        name2Connector.put(DS_JAVA_CACHE_NAME,new IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                return DriverManager.getConnection("jdbc:derby:" + DB_PATH + "db/udb2;create=true");
            }
        });

        name2Connector.put(DS_JAVA_CACHE_H_NAME,new IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {

                return DriverManager.getConnection("jdbc:derby:" + DB_PATH + "db/udb4;create=true");
            }
        });

    }
    public static Connection getConnection2(String nameConnection) throws ClassNotFoundException, SQLException
    {


        Connection connection;
        try {
            System.out.println("request nameConnection = " + nameConnection);
            connection = name2Connection.get(nameConnection);
            if (connection !=null && !connection.isClosed())
                return connection;
            try {
                return getJNDIConnection(nameConnection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (name2Connector.containsKey(nameConnection))
                name2Connection.put(nameConnection,connection=name2Connector.get(nameConnection).getConnection());

            return  connection;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
          }
//        finally {
//            System.out.println("got nameConnection = " + nameConnection+" "+ connection);
//        }
    }

//    private static Connection getDerbyConnection() throws ClassNotFoundException, SQLException
//    {
//            //Class.forName("org.apache.derby.jdbc.ClientDriver");
//            //Get a connection
//              return DriverManager.getConnection("jdbc:derby:" + DB_PATH + "db/udb;create=true");
//    }


    public static Connection getJdbcDb2Connection() throws ClassNotFoundException, SQLException
    {

        Class.forName("com.ibm.db2.jcc.DB2Driver");
        return DriverManager.getConnection("jdbc:db2://host_db2:port_db2/bddb00", "user", "user");
    }

    public static Connection getJdbcDb2Connection2() throws ClassNotFoundException, SQLException
    {

        Class.forName("com.ibm.db2.jcc.DB2Driver");
        return DriverManager.getConnection("jdbc:db2://host_db2:port_db/bddb01", "user", "user");
    }


    public static Connection getOracleJdbcConnection() throws ClassNotFoundException, SQLException
    {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection("jdbc:oracle:thin:@ora_host:ora_port:ora01", "user", "user");
    }

    //
    public static Connection getJNDIConnection(String ds_name) throws SQLException /*throws SQLException, NamingException, ClassNotFoundException*/
    {
        DataSource ds = null;
        InitialContext initialContext = null;
        try
        {
            initialContext = new InitialContext();
        }
        catch (NamingException e)
        {
            throw new SQLException(e);
        }



        try
        {
            ds = (DataSource) initialContext.lookup(ds_name);
        }
        catch (Exception e)
        {//
           // e.printStackTrace();
        }

        try
        {
            if (ds==null)
            {
                Context envCtx = (Context) initialContext.lookup("java:comp/env");
                ds = (DataSource) envCtx.lookup(ds_name);
            }
        }
        catch (NamingException e)
        {
            throw new SQLException(e);
        }

        try
        {
            return ds.getConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.out.println("Error got connections, connections in pull = " + ix);
            throw e;
        }
        finally
        {
          System.out.println("got connections in pull = " + ix.incrementAndGet()+" "+ds_name);
        }
    }


    //
    public static void closeConnection(final Connection conn){
        try{
            if(conn != null)
            {
//                if (!conn.isClosed())
                {
                    conn.close();
                    System.out.println("release connections in pull = " + ix.decrementAndGet());
                }
//                else
//                    System.out.println("conn = " + conn+" is closed");
            }
        }catch(SQLException e){
            // ignore
        }
    }


    static volatile AtomicInteger ix=new AtomicInteger(0);
    //
    public static void saveCloseConnection(final Connection conn){

        if (conn == null)
            return;

        if (name2Connection==null)
            return;
        Collection<Connection> values = name2Connection.values();

        Set hs=new HashSet(values);
        try
        {
            for (Object h : hs)
                if (h==conn)
                    return;
//            if (!conn.isClosed())
            {
                conn.close();
                System.out.println("release connections in pull = " + ix.decrementAndGet());
            }
//            else
//                System.out.println("conn = " + conn+" is closed");

        }
        catch(SQLException e)
        {
            e.printStackTrace();
            // ignore
        }
    }
    //
    public static void closeStatement(final Statement stmt) {
        try{
            if(stmt != null) stmt.close();
        }catch(SQLException e){
            // ignore
        }
    }
    //
    public static void closeResultSet(final ResultSet rs){
        try{
            if(rs != null) rs.close();
        }catch(SQLException e){
            // ignore
        }
    }
    //
    public static void closeAll(final ResultSet rs, final Statement stmt, final Connection conn, boolean save){
        try{
            if(rs   != null) rs.close();
        }
        catch(SQLException e)
        {
            // ignore
        }

        try{
            if(stmt != null) stmt.close();
        }
        catch(SQLException e)
        {
            // ignore
        }

        try{
            if(conn != null)
                if (save)
                    saveCloseConnection(conn);
                else
                {
//                    if (!conn.isClosed())
                    {
                        conn.close();
                        System.out.println("release connections in pull = " + ix.decrementAndGet());
                    }
//                    else
//                        System.out.println("conn = " + conn+" is closed");
                }
        }
        catch(SQLException e)
        {
            // ignore
        }

    }
    //
    // test only 
    //
//    public static Connection getTestConnection() throws Exception {
//        //
//        try{
//            //
//            Properties prop = new Properties();
//            prop.setProperty("java.naming.factory.initial", "com.evermind.server.ApplicationClientInitialContextFactory");
//            prop.setProperty("java.naming.provider.url", "ormi://localhost/spku");
//            //23800
//            //prop.setProperty("java.naming.provider.url", "ormi://localhost:23800/spku");
//
//
//            prop.setProperty("java.naming.security.principal",   "admin");
//            prop.setProperty("java.naming.security.credentials", "admin");
//            //
//            Context context = new InitialContext(prop);
//            System.out.println("context initialized...");
//            //
//            DataSource ds = (DataSource)context.lookup("jdbc/SpkuDS");
//            //
//            Connection connection = ds.getConnection();
//            /*if(connection==null || connection.isClosed()){
//               System.out.println("Utils.getJNDIConnection received already closed connection: "+connection);
//               LogFactory.dao.error("Utils.getJNDIConnection received already closed connection: "+connection);
//               return null;
//            }*/
//            return connection;
//        }catch(SQLException e){
//            //LogFactory.dao.error(e.getMessage(), e);
//            e.printStackTrace();
//            //throw new SysException(e.getMessage());
//            throw e;
//        }
//    }
}
