package com.mwlib.tablo.derby.delay;

import com.mwlib.tablo.cache.WrongParam;
import com.mwlib.tablo.derby.IDataLoader;
import com.mwlib.tablo.test.tpolg.CliProviderFactoryImpl3;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.ICliProvider;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.db.desc.DelayGIDTDesc;

import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.03.15
 * Time: 12:30
 */
public class ConsolidatorDelay  implements IDataLoader
{
    public static final String DEF_SESSIONID = "CONSOLIDATOR_DELAY";

    private Set<String> allTables = new HashSet<String>();

    private int ixReq=0;
    private Map[] mapParms;

    private boolean test;
    private String[] loaderTypes;
    private String dsName;// = DbUtil.DS_JAVA_CACHE_NAME;

    public ConsolidatorDelay(String[] loaderTypes, String dsName, boolean test)
    {
        this.test=test;
        this.loaderTypes=loaderTypes;
        this.dsName=dsName;
        mapParms=new Map[loaderTypes.length];
    }

    private ConsolidatorDelay()
    {
    }

    protected void delayGid(Connection conn, List<Map> rv, String query,String prefix,Map<String,Object> add2tuple) throws SQLException
    {
        Statement stmt=null;
        ResultSet rs=null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            //rs = stmt.executeQuery("SELECT  * from "+TablesTypes.DELAYS_GID);
            int i=0;
            int total=0;
            while (rs.next())
            {
                Map<String,Object> tuple=new HashMap<String,Object>();
                tuple.put("EVENT_TYPE", TablesTypes.DELAYS_GID);

                int datatype_id = rs.getInt(TablesTypes.DATATYPE_ID);

                tuple.put("DEL_ID", prefix+TablesTypes.KEY_SEPARATOR+String.valueOf(datatype_id));
                tuple.put(TablesTypes.DATATYPE_ID, datatype_id);

                String trainTypeName= DelayGIDTDesc.getServNameByDataTypeId(datatype_id);
                if (trainTypeName==null)
                {
                    System.out.println("can't define datatype_id for gid delays= " + datatype_id);
                    continue;
                }
                tuple.put("TRTYPE", trainTypeName);


                final String servName = rs.getString("SNM");
                if (servName!=null)
                {
                    tuple.put("A", servName);
                }
                else
                {
                    System.out.println("can't define servName for gid delays= " + servName);
                    continue;

                }
                tuple.put("HOZ_ID",rs.getInt("HOZ_ID"));
                tuple.put("CNT",rs.getInt("CNT"));

                for (String addKey : add2tuple.keySet())
                    tuple.put(addKey,add2tuple.get(addKey));

                total+=rs.getInt("CNT");



                rv.add(tuple);

                i++;
            }
            System.out.println("delay GID consolidation rows:" + i + " total:" + total);

        }
        finally
        {
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

            List<Map> all = new LinkedList<Map>();

            if (hasTable(TablesTypes.DELAYS_GID))
            try{
                    List<Map> rv= new LinkedList<Map>();

                    String query = "select DATATYPE_ID," +
                            "CASE \n" +
                             "      WHEN HOZ_ID=150305 THEN 'П' \n" +
                             "      WHEN HOZ_ID=150306 THEN 'Ш' \n" +
                             "      WHEN HOZ_ID=150309 THEN 'Э' \n" +
                             "      WHEN HOZ_ID=150304 THEN 'В' \n" +
                             "      ELSE '" + TablesTypes.Z_SERVAL + "' \n" +
                             "   END as SNM, " +
                            "HOZ_ID,count(distinct TRAIN_ID) CNT from "+TablesTypes.DELAYS_GID+" WHERE UPPER(SYS_TYPE) = ";

                    Map<String, Object> add2tuple = new HashMap<String, Object>();
                    add2tuple.put("SYS_TYPE","Kasant".toUpperCase());
                    add2tuple.put("EvName","Отказы");


                    delayGid(conn, rv, query + "UPPER('Kasant') group by DATATYPE_ID,HOZ_ID", "REF", add2tuple);
                    all.addAll(rv);

                    rv.clear();
                    add2tuple.clear();
                    add2tuple.put("SYS_TYPE", "Kasat".toUpperCase());
                    add2tuple.put("EvName", "Нарушения");

                    delayGid(conn, rv, query + "UPPER('Kasat') group by DATATYPE_ID,HOZ_ID", "VIOL", add2tuple);
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

        if (dsName==null)
            dsName=DbUtil.DS_JAVA_CACHE_NAME;

        return DbUtil.getConnection2(dsName);
    }


    protected boolean checkLoading() throws Exception
    {
        boolean rv =false;
        try
        {
//            ICliProviderFactory providerFactoryInstance = IDerbyCliProviderFactoryImpl.getProviderFactoryInstance();
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


//    protected boolean commonFilter(Map<String,Object> tuple,int dorKod,Object predId,String type)
//    {
//        if (dorKod==0)
//        {
//            String _predId=String.valueOf(predId);
//            System.out.print("skip tuple for type = " + type+" dorKod:"+dorKod+" PRED_ID:"+_predId+" tuple:");
//            System.out.println("tuple = " + tuple);
//            return false;
//        }
//        return true;
//    }


    public static void printDelayTime(long mills,String message) {
        double sec = (1.0 * mills) / 10;
        System.out.println(message + 1.0*((int)(sec))/100+" sec.");
    }

//    public static void main(String[] args) throws Exception
//    {
//        new ConsolidatorDelay().consolidateData();
//    }

}
