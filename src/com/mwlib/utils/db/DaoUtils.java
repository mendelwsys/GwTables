package com.mwlib.utils.db;

/**
 * Created by IntelliJ IDEA.
 * User: VMendelevich
 * Date: 19.12.11
 * Time: 15:08
 *
 */

import com.mwlib.utils.io.IOUtil;
import com.mycompany.common.Pair;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: VMendelevich
 * Date: 07.12.11
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
public final class DaoUtils {



    public enum ToCase {
        ToLowerCase,
        notChange,
        ToUpperCase
    }

//    public static void testBaseQuery(String[] args) {
//        FssUser user = new FssUser("12", "23", "34", "56", "78", "344", "1223234", "QWERTY","1");
//
//        Map<String, Object> attrMap = DaoUtils.object2paramMap(user, ToCase.ToUpperCase, DaoUtils.createRefMap(new String[][]{{"USERID", "LOGIN"}, {"COMMENT", "PERSON_COMMENT"}}));
//        for (String s : attrMap.keySet()) {
//            System.out.println("s = " + s);
//        }
//    }

    public static Map<String, String> createRefMap(String[][] refs) {
        Map<String, String> rv = new HashMap<String, String>();
        for (String[] ref : refs)
            rv.put(ref[0], ref[1]);
        return rv;
    }

    public static String object2XML(Object obj, ToCase toCase, Map<String, String> replaceMap) {

        String rs = "";

        Map<String, Object> rv = object2paramMap(obj, toCase, replaceMap);
        for (String name : rv.keySet()) {

            Object nameobj = rv.get(name);
            if (nameobj == null)
                continue;
            if (nameobj instanceof Iterable) {
                Iterable coll = (Iterable) nameobj;
                rs += "<" + name + ">";
                for (Object co : coll) {
                    rs += "<" + co.getClass().getName() + ">";
                    rs += object2XML(co, toCase, replaceMap);
                    rs += "</" + co.getClass().getName() + ">";
                }
                rs += "<" + name + ">";
            } else if (!(nameobj.getClass().isArray())) {
                rs += "<" + name + ">";

                if (nameobj instanceof String ||
                        nameobj instanceof Integer ||
                        nameobj instanceof Long ||
                        nameobj instanceof Boolean ||
                        nameobj instanceof Character ||
                        nameobj instanceof StringBuffer) {

                    try {
                        rs += IOUtil.forXml(nameobj.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (nameobj instanceof Class) {
                    rs += ((Class) nameobj).getName();
                } else
                    rs += object2XML(nameobj, toCase, replaceMap);
                rs += "</" + name + ">";
            }
        }
        return rs;
    }


    public static Map<String, Method> getClassMethods(Class objClass, ToCase toCase, String prefix) {
        try {
            Map<String, Method> rv = new HashMap<String, Method>();
            if (objClass != null)
            {
                Method[] met = objClass.getMethods();
                for (Method method : met) { //find setters and call it
                    String mname = method.getName();
                    Class[] types;
                    if (mname.startsWith(prefix)) {
                        mname = mname.substring(prefix.length());
                        switch (toCase) {
                            case ToUpperCase:
                                mname = mname.toUpperCase();
                                break;
                            case ToLowerCase:
                                mname = mname.toLowerCase();
                                break;
                        }
                        //mname=(""+mname.charAt(0)).toLowerCase()+mname.substring(1);
                        rv.put(mname, method);
                    }
                }
            }
            return rv;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static void setObjectBySettersParams2(Object obj, Map<String, Object> params, ToCase toCase) {
        try {
            Class<? extends Object> objClass = obj.getClass();

            Method[] met = objClass.getMethods();
            for (Method method : met)
            {
                String mname = method.getName();
                Class[] types;
                if (mname.startsWith("set") && (types = method.getParameterTypes()).length == 1)
                {
                    mname = mname.substring("set".length());
                    switch (toCase) {
                        case ToUpperCase:
                            mname = mname.toUpperCase();
                            break;
                        case ToLowerCase:
                            mname = mname.toLowerCase();
                            break;
                    }
                    Object val = params.get(mname);
                    if (val != null && types[0].isInstance(val))
                    {
                            method.invoke(obj, val);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void setObjectBySettersParams(Object obj, Map<String, String> params, ToCase toCase) {
        try {
            Class<? extends Object> objClass = obj.getClass();

            Method[] met = objClass.getMethods();
            for (Method method : met) { //find setters and call it
                String mname = method.getName();
                Class[] types;
                if (mname.startsWith("set") && (types = method.getParameterTypes()).length == 1 && types[0].isInstance("")) {
                    mname = mname.substring("set".length());
                    switch (toCase) {
                        case ToUpperCase:
                            mname = mname.toUpperCase();
                            break;
                        case ToLowerCase:
                            mname = mname.toLowerCase();
                            break;
                    }
                    //mname=(""+mname.charAt(0)).toLowerCase()+mname.substring(1);
                    String val = params.get(mname);
                    if (val != null)
                        method.invoke(obj, val);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Pair<String,String[]> getRequestField(Class objClass, ToCase toCase, Map<String, String> replaceMap)
    {
        Map<String, Method> methodMap = getClassMethods(objClass, toCase, "set");
        Set<String> names=new HashSet<String>(methodMap.keySet());
        if (replaceMap!=null)
            for (String s : replaceMap.keySet())
                  if (names.remove(s))
                  {
                      String repVal = replaceMap.get(s);
                      if (repVal!=null)
                        names.add(repVal);
                  }

        StringBuilder res=new StringBuilder();
        for (String s : names)
            res=res.append(s).append(",");

        return new Pair(res.substring(0,res.length()-1),names.toArray(new String[names.size()]));
    }

    /**
     * @param obj        - object for translation
     * @param toCase
     * @param replaceMap - заместить в возвращаемом заначении значения с именем key на значения с именем value  @return - object getter name or field name to value
     */
    public static Map<String, Object> object2paramMap(Object obj, ToCase toCase, Map<String, String> replaceMap) {
        try {
            Map<String, Object> rv = new HashMap<String, Object>();
            Class<? extends Object> objClass = obj.getClass();
            Field[] res = objClass.getFields();
            for (Field re : res) {
                String name = re.getName();
                Object p = re.get(obj);
                if (p != null)
                    rv.put(name, p);
            }

            Method[] met = objClass.getMethods();
            for (Method method : met) { //find getter
                String mname = method.getName();
                if (mname.startsWith("get") && method.getParameterTypes().length == 0) {
                    mname = mname.substring("get".length());
                    switch (toCase) {
                        case ToUpperCase:
                            rv.put(mname.toUpperCase(), method.invoke(obj));
                            break;
                        case ToLowerCase:
                            rv.put(mname.toLowerCase(), method.invoke(obj));
                            break;
                        default:
                            rv.put(mname, method.invoke(obj));
                    }
                }
            }

            if (replaceMap != null && replaceMap.size() > 0) {
                Map<String, Object> rv2 = new HashMap<String, Object>();
                Set<String> keys = new HashSet<String>(rv.keySet());
                for (String key : keys) {
                    String inproc = replaceMap.get(key);
                    if (inproc != null)
                        rv2.put(inproc, rv.remove(key));
                }
                rv2.putAll(rv);
                rv = rv2;
            }
            return rv;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static public interface ResultProcessor {
        void processResult(ResultSet result) throws SQLException;

        void processConn(Connection con) throws SQLException;
    }

    static public class CommitResultProcessor implements ResultProcessor {
        public void processResult(ResultSet result) throws SQLException {
        }

        public void processConn(Connection con) throws SQLException {
            con.commit();
        }
    }

    static public abstract class DummyResultProcessor implements ResultProcessor {

        public void processConn(Connection con) throws SQLException {
        }
    }

    static public Set<String> querySet(final String queryToRun, final int resultLimit) {
        final Set<String> rv = new HashSet<String>();
        try {
            query(queryToRun, new DaoUtils.DummyResultProcessor() {
                public void processResult(ResultSet result) throws SQLException {
                    while (result.next()) {
                        if (resultLimit >= 0 && rv.size() > resultLimit) {
//                           if (debug.messageEnabled())
//                               debug.message("limit of query is exceeds: limit:" + resultLimit+" queryToRun:"+queryToRun);
                            break;
                        }
                        rv.add(result.getString(1));
                    }
                }
            });
        } catch (Exception ex1) {
//            if (debug.messageEnabled()) {
//                debug.message("JdbcSimpleRoleDao.search:" + ex1);
//            }
            throw new RuntimeException(ex1);
        }
        if (rv.isEmpty())
            return Collections.EMPTY_SET;
        return rv;
    }


    static public void callProc(Map<String, Object> attrMap, String pkg_name, String proc_name, String proc_prefix)
    {
        //Get metadata for procedure request
        String metadatareq = "SELECT ARGUMENT_NAME,POSITION,DEFAULTED " +
                "  FROM SYS.ALL_ARGUMENTS " +
                "  WHERE PACKAGE_NAME = '" + pkg_name +
                "'  AND " +
                "  OBJECT_NAME = '" + proc_name + "'";


        Map<Integer, String[]> pos2argname_def = new TreeMap<Integer, String[]>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        //RFE: later deal better with various types
        try {
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(metadatareq);
            while (rs.next())
                pos2argname_def.put(rs.getInt(2), new String[]{rs.getString(1), rs.getString(3)});
        } catch (Exception ex1) {
//            if (debug!=null && debug.messageEnabled())  {
//                debug.message("JdbcSimpleUserDao.createUser:" + ex1);
//            }
            throw new RuntimeException(ex1);
        } finally
        {
            DbUtil.closeAll(rs,stmt,con,true);
//            closeResultSet(rs);
//            closeStatement(stmt);
//            closeConnection(con);
        }

        String procparams = "";
        int posinparams = 1;
        Map<Integer, Object> ix2val = new HashMap<Integer, Object>();
        for (int ix : pos2argname_def.keySet()) {
            String[] argname_def = pos2argname_def.get(ix);
            String parname = argname_def[0];
            Object val = attrMap.get(parname);
            if (val == null && parname.startsWith(proc_prefix))
                val = attrMap.get(parname.substring(proc_prefix.length()));
            //get the value of the attribute
            if (val != null) {
                if (procparams.length() > 0)
                    procparams += ",";
                procparams += "?";
                ix2val.put(posinparams, val);
                posinparams++;
            } else if (argname_def[1].equalsIgnoreCase("Y"))
                break;
            else { //Параметр не определен, возможно это выходной параметр
                if (procparams.length() > 0)
                    procparams += ",";
                procparams += "?";
                posinparams++;
            }
        }
        procparams = "{call " + pkg_name + "." + proc_name + "(" + procparams + ")}";
        CallableStatement pstmt = null;
        //RFE: later deal better with various types
        try {
            con = getConnection();
            pstmt = con.prepareCall(procparams);

            ParameterMetaData md = pstmt.getParameterMetaData();
            int cnt = md.getParameterCount();
            for (int ix = 1; ix <= cnt; ix++) {
                if (ix2val.containsKey(ix)) {
                    Object o = ix2val.get(ix);
                    pstmt.setString(ix, o.toString());
                } else {
//                    if (debug!=null && debug.messageEnabled())
//                        debug.message("Can't find duty parameters for call procedure : " + proc_name + " paramix:" + ix + " set null");
                    pstmt.setNull(ix, Types.VARCHAR);
                }
            }
            pstmt.execute();
            con.commit();
        } catch (Exception ex1) {
//            if (debug!=null && debug.messageEnabled()) {
//                debug.message("JdbcSimpleUserDao.createUser:" + ex1);
//            }
            throw new RuntimeException(ex1);
        } finally {
            DbUtil.closeAll(null,pstmt,con,true);
//            closeStatement(pstmt);
//            closeConnection(con);
        }
    }


    static public void callProc2(Map<String, Object> attrInMap,Map<String, Object> attrOutMap, String pkg_name, String proc_name, String proc_prefix)
    {
        //Get metadata for procedure request
        String metadatareq = "SELECT ARGUMENT_NAME,POSITION,DEFAULTED " +
                "  FROM SYS.ALL_ARGUMENTS " +
                "  WHERE PACKAGE_NAME = '" + pkg_name +
                "'  AND " +
                "  OBJECT_NAME = '" + proc_name + "'";


        Map<Integer, String[]> pos2argname_def = new TreeMap<Integer, String[]>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        //RFE: later deal better with various types
        try {
            con = getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(metadatareq);
            while (rs.next())
                pos2argname_def.put(rs.getInt(2), new String[]{rs.getString(1), rs.getString(3)});
        } catch (Exception ex1) {
//            if (debug!=null && debug.messageEnabled())  {
//                debug.message("JdbcSimpleUserDao.createUser:" + ex1);
//            }
            throw new RuntimeException(ex1);
        } finally {
            DbUtil.closeAll(rs,stmt,con,true);
//            closeResultSet(rs);
//            closeStatement(stmt);
//            closeConnection(con);
        }

        String procparams = "";
        int posinparams = 1;
        Map<Integer, Object> ix2val = new HashMap<Integer, Object>();
        Map<String, Integer> parnameout2ix = new HashMap<String, Integer>();
        
        for (int ix : pos2argname_def.keySet()) {
            String[] argname_def = pos2argname_def.get(ix);
            String parname = argname_def[0];
            
            Object val = attrInMap.get(parname);
            if (val == null && parname.startsWith(proc_prefix))
                val = attrInMap.get(parname.substring(proc_prefix.length()));
            
            if (attrOutMap.containsKey(parname))
                parnameout2ix.put(parname,posinparams);
            else if (attrOutMap.containsKey(parname.substring(proc_prefix.length())))
                parnameout2ix.put(parname.substring(proc_prefix.length()),posinparams);
            
            //get the value of the attribute
            if (val != null) {
                if (procparams.length() > 0)
                    procparams += ",";
                procparams += "?";
                ix2val.put(posinparams, val);
                posinparams++;
            } else if (argname_def[1].equalsIgnoreCase("Y"))
                break;
            else { //Параметр не определен, возможно это выходной параметр
                if (procparams.length() > 0)
                    procparams += ",";
                procparams += "?";
                posinparams++;
            }
        }
        procparams = "{call " + pkg_name + "." + proc_name + "(" + procparams + ")}";
        CallableStatement pstmt = null;
        //RFE: later deal better with various types
        try {
            con = getConnection();
            pstmt = con.prepareCall(procparams);

            ParameterMetaData md = pstmt.getParameterMetaData();
            int cnt = md.getParameterCount();
            for (int ix = 1; ix <= cnt; ix++) {
                if (ix2val.containsKey(ix)) {
                    Object o = ix2val.get(ix);
                    pstmt.setString(ix, o.toString());
                } else {
//                    if (debug!=null && debug.messageEnabled())
//                        debug.message("Can't find duty parameters for call procedure : " + proc_name + " paramix:" + ix + " set null");
                    pstmt.setNull(ix, Types.VARCHAR);
                }
            }
            
            for (String parname : parnameout2ix.keySet())
                      pstmt.registerOutParameter(parnameout2ix.get(parname), Types.VARCHAR);

            pstmt.execute();

            for (String parname : parnameout2ix.keySet()) {
                Object val = pstmt.getObject(parnameout2ix.get(parname));
                attrOutMap.put(parname, val);
            }


            con.commit();



        } catch (Exception ex1) {
//            if (debug!=null && debug.messageEnabled()) {
//                debug.message("JdbcSimpleUserDao.createUser:" + ex1);
//            }
            throw new RuntimeException(ex1);
        } finally {
            DbUtil.closeAll(null,pstmt,con,true);
//            closeStatement(pstmt);
//            closeConnection(con);
        }
    }


    static public void query(String queryToRun, ResultProcessor processor) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(queryToRun);
            result = stmt.executeQuery();
            processor.processResult(result);
            processor.processConn(con);
        } catch (SQLException e) {
//            if (debug!=null && debug.messageEnabled())
//                debug.message("JdbcSimpleRoleDao.search:" + e);
            throw e;
        } finally {
            DbUtil.closeAll(result,stmt,con,true);
//            closeResultSet(result);
//            closeStatement(stmt);
//            closeConnection(con);
        }
    }

    static public void query(Connection conn,String queryToRun, ResultProcessor processor) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            stmt = conn.prepareStatement(queryToRun);
            result = stmt.executeQuery();
            processor.processResult(result);
            processor.processConn(conn);
        } catch (SQLException e) {
//            if (debug!=null && debug.messageEnabled())
//                debug.message("JdbcSimpleRoleDao.search:" + e);
            throw e;
        } finally {
            DbUtil.closeAll(result,stmt,conn,true);
//            closeResultSet(result);
//            closeStatement(stmt);
//            closeConnection(conn);
        }
    }


    static public Connection getConnection() throws SQLException {
        try {
            return DbUtil.getJNDIConnection(DbUtil.DS_ORA_NAME);
        //} catch (ClassNotFoundException e) {
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    static public void closeConnection(Connection dbConnection) {
        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
        } catch (SQLException se) {
//            if (debug!=null && debug.messageEnabled()) {
//                debug.message("JdbcSimpleUserDao.closeConnection: SQL Exception"
//                        + " while closing DB connection: \n" + se);
//            }
        }
    }

    //should I catch all Exceptions instead of just SQL ????? I think so
    static public void closeResultSet(ResultSet result) {
        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException se) {
//            if (debug!=null && debug.messageEnabled()) {
//                debug.message("JdbcSimpleUserDao.closeResultSet: SQL Exception"
//                        + " while closing Result Set: \n" + se);
//            }
        }
    }

    //should I catch all Exceptions instead of just SQL ????? I think so
    static public void closeStatement(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException se) {
//            if (debug!=null && debug.messageEnabled()) {
//                debug.message("JdbcSimpleUserDao.closeStatement: SQL Exception"
//                        + " while closing Statement : \n" + se);
//            }
        }
    }


    public static <T> void fillObjectCollection (Connection conn, Collection<T> reqlist, Class<T> objClass, String reqSQL) throws SQLException, InstantiationException, IllegalAccessException
    {
        Statement cs=null;
        ResultSet rs=null;
        try
        {
            Pair<String, String[]> res = DaoUtils.getRequestField(objClass, DaoUtils.ToCase.notChange, null);
            reqSQL=reqSQL.replace("*", res.first);
            cs = conn.createStatement();
            cs.setFetchSize(1000);
            rs = cs.executeQuery(reqSQL);
            String[] names=res.second;
            while(rs.next())
            {
                Map<String, Object> params = new HashMap<String, Object>();
                for (String name : names)
                    params.put(name, rs.getObject(name));
                T inst = objClass.newInstance();
                DaoUtils.setObjectBySettersParams2(inst,params, DaoUtils.ToCase.notChange);
                reqlist.add(inst);
            }
        }
        finally
        {
            DbUtil.closeAll(rs, cs, null, false);
        }
    }






}
