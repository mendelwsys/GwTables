package com.mwlib.tablo.derby.loco;

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
 * Date: 26.03.15
 * Time: 12:30
 * Консолидировать RSM
 */
public class ConsolidatorLOC implements IDataLoader
{
    public static final String DEF_SESSIONID = "CONSOLIDATOR_LOCO";


    private Set<String> allTables = new HashSet<String>();

    private int ixReq=0;
    private boolean test;
    private String dsName;

    private Map[] mapParms;
    private String[] loaderTypes;


//    public ConsolidatorLOC(String dsName, boolean test)
//    {
//        this.test=test;
//        this.dsName=dsName;
//    }

    public ConsolidatorLOC(String[] loaderTypes, String dsName, boolean test)
    {
        this.test=test;
        this.loaderTypes=loaderTypes;
        this.dsName=dsName;
        mapParms=new Map[loaderTypes.length];
    }


    private ConsolidatorLOC()
    {
    }


    private void execReq(Connection conn,List<Map> rv,Map<String,Object> addParam,String query,String eventType) throws SQLException
    {
        Statement stmt=null;
        ResultSet rs=null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            int i=0;
            int total=0;
            String prefix="000";
            while (rs.next())
            {
                Map<String,Object> tuple=new HashMap<String,Object>();
                tuple.put("EVENT_TYPE", eventType);
                final int cnt = rs.getInt("CNT");
                tuple.put("CNT", cnt);
                String name = rs.getString("NAME_P");
                if (name!=null)
                    name=name.trim();
                tuple.put("NAME_P", name);
                String dor_code = String.valueOf(rs.getInt(TablesTypes.DOR_CODE));
                if (dor_code.length()<3)
                    dor_code=prefix.substring(0,3-dor_code.length())+dor_code;

                tuple.put("DOR_KOD_NAME_P_ID", dor_code +";"+name);
                if (initDOR_KOD(rs,tuple)<=0)
                    continue;
                rv.add(tuple);

                for (String key : addParam.keySet())
                    tuple.put(key,addParam.get(key));

                total+= cnt;
                i++;
            }
            System.out.println("LOCO consolidation rows:" + i + " total:" + total);

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

            if (!fillTables() || !hasTable(TablesTypes.LOCREQ))
                return new Map[0];


            final Calendar instance = Calendar.getInstance();
            int hh= instance.get(Calendar.HOUR_OF_DAY);
            int mm= instance.get(Calendar.MINUTE);
            Date dt= new Date();
            if ((hh*60+mm)>= TablesTypes.LOC_TR_MM)
                dt=new Date(dt.getTime()+ TablesTypes.DAY_MILS);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String stringDate=sdf.format(dt);

            Date dtN = new Date(dt.getTime()+TablesTypes.DAY_MILS);
            String stringDateN=sdf.format(dtN);


            List<Map> all = new LinkedList<Map>();
            conn=getConnection();
            try
            {
                String queryC="select DOR_KOD,NAME_P,SUM(mm) as CNT from\n" +
                        "(\n" +
                        "select  id,NAME_P,DOR_KOD,MAX(COUNT_P) mm from \n" +
                        "(\n" +
                        "\tselect trim(CHAR(DOR_KOD)) || ';' || trim(CHAR(ID_Z)) || ';' || trim(CHAR(ZLID)) || ';' || trim(CHAR(NMB)) id,NAME_P,COUNT_P,DOR_KOD from LOCREQ t\n" +
                        "\twhere TIME_P>=TIMESTAMP('"+stringDate+"', '00:00') and  TIME_P<TIMESTAMP('"+stringDateN+"', '00:00') \n" +
                        ") s \n" +
                        "GROUP BY id,NAME_P,DOR_KOD\n" +
                        ") s2 GROUP by NAME_P,DOR_KOD order BY DOR_KOD,NAME_P";

                final HashMap<String, Object> addParam = new HashMap<String, Object>();
                addParam.put("A","DM");
                execReq(conn, all, addParam, queryC, TablesTypes.LOCREQ);


            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            try
            {
                String queryC="select DOR_KOD,NAME_P,SUM(mm) as CNT from\n" +
                        "(\n" +
                        "select  id,NAME_P,DOR_KOD,MAX(COUNT_P) mm from \n" +
                        "(\n" +
                        "\tselect trim(CHAR(DOR_KOD)) || ';' || trim(CHAR(ID_Z)) || ';' || trim(CHAR(ZLID)) || ';' || trim(CHAR(NMB)) id,NAME_P,COUNT_P,DOR_KOD from LOCREQ t\n" +
                        "\twhere TIME_P>=TIMESTAMP('"+stringDate+"', '00:00') and  TIME_P<TIMESTAMP('"+stringDateN+"', '00:00') and STATUS in (1,2) \n" +
                        ") s \n" +
                        "GROUP BY id,NAME_P,DOR_KOD\n" +
                        ") s2 GROUP by NAME_P,DOR_KOD order BY DOR_KOD,NAME_P";

                final HashMap<String, Object> addParam = new HashMap<String, Object>();
                addParam.put("A","1Y");
                execReq(conn,all, addParam, queryC,TablesTypes.LOCREQ);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            try
            {
                String queryC="select DOR_KOD,NAME_P,SUM(mm) as CNT from\n" +
                        "(\n" +
                        "select  id,NAME_P,DOR_KOD,MAX(COUNT_P) mm from \n" +
                        "(\n" +
                        "\tselect trim(CHAR(DOR_KOD)) || ';' || trim(CHAR(ID_Z)) || ';' || trim(CHAR(ZLID)) || ';' || trim(CHAR(NMB)) id,NAME_P,COUNT_P,DOR_KOD from LOCREQ t\n" +
                        "\twhere TIME_P>=TIMESTAMP('"+stringDate+"', '00:00') and  TIME_P<TIMESTAMP('"+stringDateN+"', '00:00') and STATUS in (0,1) \n" +
                        ") s \n" +
                        "GROUP BY id,NAME_P,DOR_KOD\n" +
                        ") s2 GROUP by NAME_P,DOR_KOD order BY DOR_KOD,NAME_P";

                final HashMap<String, Object> addParam = new HashMap<String, Object>();
                addParam.put("A","2N");
                execReq(conn,all, addParam, queryC,TablesTypes.LOCREQ);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

//            for (Map map : all)
//                map.put("NDAY",stringDate);
            return all.toArray(new Map[all.size()]);
        }
        finally
        {
            DbUtil.closeAll(null,null,conn,true);
        }
    }


    private Connection getConnection() throws Exception {

        if (dsName==null)
            dsName=DbUtil.DS_JAVA_CACHE_NAME;

        return DbUtil.getConnection2(dsName);
    }


    private boolean hasTable(String tblName) {
        return allTables.contains(tblName);
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
            conn= getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();


            rs = dbmd.getTables(null,"APP", null, new String[]{"TABLE","VIEW"});

            while (rs.next())
            {

                String tableName = rs.getString("TABLE_NAME");
                if (needTables.contains(tableName))
                    allTables.add(tableName);
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


    public static void printDelayTime(long mills,String message) {
        double sec = (1.0 * mills) / 10;
        System.out.println(message + 1.0 * ((int) (sec)) / 100 + " sec.");
    }





    protected boolean checkLoading() throws Exception
    {
        boolean rv =false;         //TODO Здесь обработать так же суточный переход в тор, т.е. провести вернуть true и провести заново консолидацию
        try
        {
            //ICliProviderFactory providerFactoryInstance = IDerbyCliProviderFactoryImpl.getProviderFactoryInstance();
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



    private int initDOR_KOD(ResultSet rs, Map<String, Object> tuple) throws SQLException {
        int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
        tuple.put(TablesTypes.DOR_CODE, dor_kod);
        Directory.RailRoad railRoad = Directory.getByDorCode(dor_kod);
        if (Integer.valueOf(20).equals(railRoad.getADM_KOD()))
        {
            tuple.put(TablesTypes.DOR_NAME,railRoad.getSNAME());
            return dor_kod;
        }
        else
            return -1;

    }

}
