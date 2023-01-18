package com.mwlib.tablo.derby.warnagr;

import com.mwlib.tablo.cache.WrongParam;
import com.mwlib.tablo.derby.IDataLoader;
import com.mwlib.tablo.test.tpolg.CliProviderFactoryImpl3;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.ICliProvider;
import com.mwlib.tablo.ICliProviderFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.08.15
 * Time: 13:02
 */
public class WarnAGRLoader implements IDataLoader
{
    public static final String DEF_SESSIONID = "CONSOLIDATOR2";

    private Set<String> allTables = new HashSet<String>();

    private int ixReq=0;
    private Map[] mapParms;

    private boolean test;
    private String[] loaderTypes;
    private String dsName;// = DbUtil.DS_JAVA_CACHE_NAME;

    public WarnAGRLoader(String[] loaderTypes, String dsName, boolean test)
    {
        this.test=test;
        this.loaderTypes=loaderTypes;
        this.dsName=dsName;
        mapParms=new Map[loaderTypes.length];
    }

    private int initDOR_KOD(ResultSet rs, Map<String, Object> tuple) throws SQLException {
        int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
        tuple.put(TablesTypes.DOR_CODE, dor_kod);
        Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
        tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());
        return dor_kod;
    }


    protected void warnInTime(Connection conn, List<Map> rv, String query, String eventType) throws SQLException
    {
        Statement stmt=null;
        ResultSet rs=null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            int i=0;
            int total=0;
            while (rs.next())
            {
                Map<String,Object> tuple=new HashMap<String,Object>();
                tuple.put("EVENT_TYPE", eventType);

                int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                if (byDorCode==null)
                    continue;
                tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());

                tuple.put("CNT",rs.getInt("CNT"));
                tuple.put("TLN", rs.getFloat("TLN"));

                if (commonFilter(tuple,dor_kod,TablesTypes.WARNINGSINTIME))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("warnInTime consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }


    protected void warnReq(List<Map> rv, Connection conn, String query, String eventType) throws SQLException
    {


        Set<Integer> inCodes=new HashSet<Integer>();

        Statement stmt=null;
        ResultSet rs=null;
        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            int i=0;
            int total=0;
            while (rs.next())
            {

                Map<String,Object> tuple=new HashMap<String,Object>();
                tuple.put("EVENT_TYPE", eventType);

                int dor_kod=initDOR_KOD(rs, tuple);
                inCodes.add(dor_kod);
                tuple.put("CNT",rs.getInt("CNT"));
                tuple.put("TLN", rs.getFloat("TLN"));

                if (commonFilter(tuple,dor_kod,TablesTypes.WARNINGS))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }

            Set<Integer> dor_codes = Directory.dorCode2Rail.keySet();
            for (Integer dor_kod : dor_codes)
            {
                if (!inCodes.contains(dor_kod))
                {
                    Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                    if (byDorCode==null || !Integer.valueOf(20).equals(byDorCode.getADM_KOD()))
                        continue;

                    Map<String,Object> tuple=new HashMap<String,Object>();
                    tuple.put(TablesTypes.DOR_CODE, dor_kod);
                    tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());

                    tuple.put("EVENT_TYPE", eventType);


                    tuple.put("CNT",0);
                    tuple.put("TLN", 0.0f);

                    if (commonFilter(tuple,dor_kod,TablesTypes.WARNINGS))
                        rv.add(tuple);
                }
            }

            System.out.println("Warning consolidation rows:" + i + " total:" + total);
        }
        finally {
            DbUtil.closeAll(rs, stmt, null, false);
        }

    }





    protected Map[] consolidateData() throws Exception
    {

        Connection conn= null;
        try {

            if (!fillTables())
                return new Map[0];

            Directory.initDictionary(test);
            conn= getDerbyConnection();

            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String stringDate=sdf.format(dt);
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
            String time=sdf2.format(dt);


            List<Map> all = new LinkedList<Map>();

            List<Map> rvG= new LinkedList<Map>();
            if (hasTable(TablesTypes.WARNINGSINTIME))
            try {

                String query = "select "+TablesTypes.DOR_CODE+",SUM(F401_GVN) CNT,SUM(F401_GVL) TLN from "+TablesTypes.WARNINGSINTIME+" group by "+TablesTypes.DOR_CODE;
                warnInTime(conn, rvG, query, TablesTypes.WARNINGSINTIME+"#ALL");
                all.addAll(rvG);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }



            List<Map> rvF = new LinkedList<Map>();
            if (hasTable(TablesTypes.WARNINGS))
            try
            {

                String query = "select " + TablesTypes.DOR_CODE + ", count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " tbl " +
                        " WHERE tbl.TIM_BEG <= TIMESTAMP('"+stringDate+"', '"+time+"') and TIMESTAMP('"+stringDate+"', '"+time+"') <= tbl.TIM_OTM " +
                        " group by " + TablesTypes.DOR_CODE;
                warnReq(rvF, conn, query, TablesTypes.WARNINGS+"#ALL");
                all.addAll(rvF);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            {
                Map<Integer,Map> code2Tuple=new HashMap<Integer,Map>();
                for (Map map : rvG)
                    code2Tuple.put((Integer)map.get(TablesTypes.DOR_CODE),map);
                for (Map tupleF : rvF)
                {
                    Map tupleG = code2Tuple.get(tupleF.get(TablesTypes.DOR_CODE));
                    if (tupleF!=null)
                    {
                        Map nTuple = new HashMap(tupleF);

                        {
                            Number fCnt = (Number) tupleF.get("CNT");
                            if (fCnt==null)
                                fCnt=0;
                            Number gCnt = (Number) tupleG.get("CNT");
                            if (gCnt==null)
                                gCnt=0;
                            nTuple.put("CNT", fCnt.intValue() - gCnt.intValue());
                        }

                        {
                            Number fCnt = (Number) tupleF.get("TLN");
                            if (fCnt==null)
                                fCnt=0;
                            Number gCnt = (Number) tupleG.get("TLN");
                            if (gCnt==null)
                                gCnt=0;
                            nTuple.put("TLN", fCnt.floatValue()-gCnt.floatValue());
                        }
                        nTuple.put("EVENT_TYPE","GvsF");
                        nTuple.remove("A");
                        all.add(nTuple);
                    }

                }
            }

            if (hasTable(TablesTypes.WARNINGSINTIME))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",SUM(F401_G15N) CNT,SUM(F401_G15L) TLN from "+TablesTypes.WARNINGSINTIME+" group by "+TablesTypes.DOR_CODE;
                warnInTime(conn, rv, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","G15");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WARNINGS))
            try
            {
                List<Map> rv = new LinkedList<Map>();
                String query = "select " + TablesTypes.DOR_CODE + ", count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " tbl " +
                        " WHERE tbl.V<=15 and tbl.TIM_BEG <= TIMESTAMP('"+stringDate+"', '"+time+"') and TIMESTAMP('"+stringDate+"', '"+time+"') <= tbl.TIM_OTM " +
                        " group by " + TablesTypes.DOR_CODE;
                warnReq(rv, conn, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","F15");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.WARNINGSINTIME))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",SUM(F401_G25N) CNT,SUM(F401_G25L) TLN from "+TablesTypes.WARNINGSINTIME+" group by "+TablesTypes.DOR_CODE;
                warnInTime(conn, rv, query,TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","G25");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WARNINGS))
            try
            {
                List<Map> rv = new LinkedList<Map>();
                String query = "select " + TablesTypes.DOR_CODE + ", count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " tbl " +
                        " WHERE tbl.V<=25 and tbl.V>15 and tbl.TIM_BEG <= TIMESTAMP('"+stringDate+"', '"+time+"') and TIMESTAMP('"+stringDate+"', '"+time+"') <= tbl.TIM_OTM " +
                        " group by " + TablesTypes.DOR_CODE;
                warnReq(rv, conn, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","F25");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WARNINGSINTIME))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",SUM(F401_G40N) CNT,SUM(F401_G40L) TLN from "+TablesTypes.WARNINGSINTIME+" group by "+TablesTypes.DOR_CODE;
                warnInTime(conn, rv, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","G40");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.WARNINGS))
            try
            {
                List<Map> rv = new LinkedList<Map>();
                String query = "select " + TablesTypes.DOR_CODE + ", count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " tbl " +
                        " WHERE tbl.V<=40 and tbl.V>25 and tbl.TIM_BEG <= TIMESTAMP('"+stringDate+"', '"+time+"') and TIMESTAMP('"+stringDate+"', '"+time+"') <= tbl.TIM_OTM " +
                        " group by " + TablesTypes.DOR_CODE;
                warnReq(rv, conn, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","F40");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WARNINGSINTIME))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",SUM(F401_G50N) CNT,SUM(F401_G50L) TLN from "+TablesTypes.WARNINGSINTIME+" group by "+TablesTypes.DOR_CODE;
                warnInTime(conn, rv, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","G50");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.WARNINGS))
            try
            {
                List<Map> rv = new LinkedList<Map>();
                String query = "select " + TablesTypes.DOR_CODE + ", count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " tbl " +
                        " WHERE tbl.V<=50 and tbl.V>40 and tbl.TIM_BEG <= TIMESTAMP('"+stringDate+"', '"+time+"') and TIMESTAMP('"+stringDate+"', '"+time+"') <= tbl.TIM_OTM " +
                        " group by " + TablesTypes.DOR_CODE;
                warnReq(rv, conn, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","F50");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WARNINGSINTIME))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",SUM(F401_G60N) CNT,SUM(F401_G60L) TLN from "+TablesTypes.WARNINGSINTIME+" group by "+TablesTypes.DOR_CODE;
                warnInTime(conn, rv, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","G60");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.WARNINGS))
            try
            {
                List<Map> rv = new LinkedList<Map>();
                String query = "select " + TablesTypes.DOR_CODE + ", count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " tbl " +
                        " WHERE tbl.V<=60 and tbl.V>50 and tbl.TIM_BEG <= TIMESTAMP('"+stringDate+"', '"+time+"') and TIMESTAMP('"+stringDate+"', '"+time+"') <= tbl.TIM_OTM " +
                        " group by " + TablesTypes.DOR_CODE;
                warnReq(rv, conn, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","F60");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WARNINGSINTIME))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",SUM(F401_G80N) CNT,SUM(F401_G80L) TLN from "+TablesTypes.WARNINGSINTIME+" group by "+TablesTypes.DOR_CODE;
                warnInTime(conn, rv, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","GOTHER");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.WARNINGS))
            try
            {
                List<Map> rv = new LinkedList<Map>();
                String query = "select " + TablesTypes.DOR_CODE + ", count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " tbl " +
                        " WHERE tbl.V>60 and tbl.TIM_BEG <= TIMESTAMP('"+stringDate+"', '"+time+"') and TIMESTAMP('"+stringDate+"', '"+time+"') <= tbl.TIM_OTM " +
                        " group by " + TablesTypes.DOR_CODE;
                warnReq(rv, conn, query, TablesTypes.WARNINGS);
                for (Map map : rv)
                    map.put("A","FOTHER");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            return all.toArray(new Map[all.size()]);
        }
        finally
        {
            DbUtil.closeAll(null,null,conn,true);
        }
    }

    private boolean hasTable(String tblName) {
        return allTables.contains(tblName);
    }

    private Connection getDerbyConnection() throws Exception {

        return DbUtil.getConnection2(dsName);
    }


    protected boolean checkLoading() throws Exception
    {
        boolean rv =false;         //TODO Здесь обработать так же суточный переход в тор, т.е. провести вернуть true и провести заново консолидацию
        try
        {
            ICliProviderFactory providerFactoryInstance = CliProviderFactoryImpl3.getProviderFactoryInstance();
            providerFactoryInstance.addNotSessionCliIds(DEF_SESSIONID);

            for (int i = 0, mapParmsLength = mapParms.length; i < mapParmsLength; i++)
            {
                if (mapParms[i]==null)
                {
                    mapParms[i] = new HashMap();
                    mapParms[i].put(ICliProviderFactory.CLIID, new String[]{DEF_SESSIONID});
                    mapParms[i].put(AppContext.APPCONTEXT, new AppContext[]{new AppContext()});
                    mapParms[i].put(TablesTypes.TTYPE, new String[]{loaderTypes[i]});
                    mapParms[i].put("CLICNT",1000);
                }
                mapParms[i].put(TablesTypes.ID_REQN, new String[]{String.valueOf(ixReq)});

                ICliProvider[] providers = null; //здесь провайдер уже относится только к запрашиваемой таблице
                try {
                    providers = providerFactoryInstance.getProvider(mapParms[i]);
                }
                catch (WrongParam wrongParam)
                {
                    System.out.println("Can't find provider for "+loaderTypes[i]+" possible not ready");
                    //wrongParam.printStackTrace();
                }
                if (providers==null || providers.length==0)
                    continue;

                ICliProvider provider=providers[0];

                int cliCnt=(Integer)mapParms[i].get("CLICNT");
                int currCnt = provider.getCliCnt();
                mapParms[i].put("CLICNT",currCnt);
                rv= rv || (currCnt -cliCnt)<0;

                ixReq++;

                mapParms[i].put(TablesTypes.TBLID,new String[]{provider.getTblId()});

                Map<Object, long[]> newKeys = provider.getNewDataKeys(mapParms[i]).dataRef;
                rv = rv ||(newKeys.size()>0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        return rv;
    }



    private boolean fillTables()   throws Exception
    {

        if (allTables.size()==loaderTypes.length)
            return true;

        Connection conn=null;
        Statement stmt=null;
        ResultSet rs = null;
        Set<String> needTables = new HashSet<String>();
        needTables.addAll(Arrays.asList(loaderTypes));

        allTables.clear();

        try
        {
            conn= getDerbyConnection();
            DatabaseMetaData dbmd = conn.getMetaData();


            rs = dbmd.getTables(null,"APP", null, new String[]{"TABLE","VIEW"});

            while (rs.next())
            {

                String tableName = rs.getString("TABLE_NAME");
                if (needTables.contains(tableName))
                    allTables.add(tableName);

//                String TABLE_CAT = rs.getString("TABLE_CAT");
//                String TABLE_SCHEM = rs.getString("TABLE_SCHEM");
//                String TABLE_TYPE = rs.getString("TABLE_TYPE");
//                String REMARKS = rs.getString("REMARKS");
//                String TYPE_CAT = rs.getString("TYPE_CAT");
//                String TYPE_SCHEM = rs.getString("TYPE_SCHEM");
//                String  TYPE_NAME = rs.getString("TYPE_NAME");
            }
        }
        finally
        {
            DbUtil.closeAll(rs,stmt,conn, true);
        }
        return allTables.size()>0;
    }

    public Map [] getData() throws Exception
    {
        boolean isConsolidate=test;
        Map [] rv =new Map[0];
        if (!test)
        {
            long lg= System.currentTimeMillis();
            isConsolidate= checkLoading();
            long mills = System.currentTimeMillis() - lg;
            printDelayTime(mills,"fill DerbyTable by Events :");
        }

        {
            long lg= System.currentTimeMillis();
            if (isConsolidate)
                rv=consolidateData();
            long mills = System.currentTimeMillis() - lg;
            printDelayTime(mills,"Consolidate Events :");
        }
        return rv;
    }


    protected boolean commonFilter(Map<String,Object> tuple,int dorKod,String type)
    {
        if (dorKod==0)
        {
            System.out.print("skip tuple for type = " + type+" dorKod:"+dorKod+" tuple:");
            System.out.println("tuple = " + tuple);
            return false;
        }
        return true;
    }


    public static void printDelayTime(long mills,String message) {
        double sec = (1.0 * mills) / 10;
        System.out.println(message + 1.0*((int)(sec))/100+" sec.");
    }

}
