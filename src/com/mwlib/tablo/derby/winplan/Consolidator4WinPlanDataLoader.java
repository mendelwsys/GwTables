package com.mwlib.tablo.derby.winplan;

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
 * Date: 27.10.14
 * Time: 12:30
 * консолидатор для ОТС 1-й и 2-й категори
 */
public class Consolidator4WinPlanDataLoader implements IDataLoader
{
    final HashMap dummyHM = new HashMap();

    public static final String DEF_SESSIONID = "CONSOLIDATOR_PLACES_LOADER";

    private Set<String> allTables = new HashSet<String>();

    private int ixReq=0;
    private Map[] mapParms;

    private boolean test;
    private String[] loaderTypes;
    private String dsName;// = DbUtil.DS_JAVA_CACHE_NAME;

    public Consolidator4WinPlanDataLoader(String[] loaderTypes, String dsName, boolean test)
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
        if (byDorCode!=null)
            tuple.put(TablesTypes.DOR_NAME,byDorCode.getNAME());
        else
            tuple.put(TablesTypes.DOR_NAME,"UNKNOWN");
        return dor_kod;
    }


    protected void fillDorTuplesByQuery(Connection conn, List<Map> rv, String query, String tblName, Map<String, String> addRSParams, boolean total, String cntName) throws SQLException
    {
        Statement stmt=null;
        ResultSet rs=null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            int i=0;
            int _total=0;
            while (rs.next())
            {
                Map<String,Object> tuple=new HashMap<String,Object>();
                int dor_kod=0;
                if (!total)
                    dor_kod = initDOR_KOD(rs, tuple);

                if (total || commonFilter(tuple,dor_kod,tblName))
                {
                    tuple.put("EVENT_TYPE", tblName);
                    tuple.put(TablesTypes.PLACE_ID,rs.getObject(TablesTypes.PLACE_ID));
                    tuple.put(cntName,rs.getInt(cntName));
                    if (addRSParams!=null)
                        for (String fld : addRSParams.keySet())
                            tuple.put(fld,rs.getObject(addRSParams.get(fld)));
                    _total+=rs.getInt(cntName);
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println(tblName+" consolidation rows:" + i+" total:"+_total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }


    protected void fillTuplesByQuery(Connection conn, List<Map> rv, String query, String tblName, Map<String, String> addRSParams, String cntName) throws SQLException
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
                int dor_kod = initDOR_KOD(rs, tuple);

                if (commonFilter(tuple,dor_kod,tblName))
                {
                    tuple.put("EVENT_TYPE", tblName);
                    tuple.put(TablesTypes.POLG_ID,rs.getInt(TablesTypes.POLG_ID));
                    tuple.put(TablesTypes.POLG_NAME,rs.getObject(TablesTypes.POLG_NAME));
                    tuple.put(TablesTypes.PLACE_ID,rs.getObject(TablesTypes.PLACE_ID));
                    tuple.put("NUM",rs.getInt("NUM"));
                    tuple.put(cntName,rs.getInt(cntName));

                    if (addRSParams!=null)
                        for (String fld : addRSParams.keySet())
                            tuple.put(fld,rs.getObject(addRSParams.get(fld)));

                    total+=rs.getInt(cntName);
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println(tblName+" consolidation rows:" + i+" total:"+total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }




    String railSQLRoadFilter =null;
    Set<Integer> railSet =new HashSet<Integer>();

    protected Map[] consolidateData() throws Exception
    {

        Connection conn= null;
        try {

            if (!fillTables())
                return new Map[0];

            Directory.initDictionary(test);


            if (railSQLRoadFilter==null)
            {
                for (Directory.RailRoad railRoad : Directory.dorCode2Rail.values())
                {
                    if (Integer.valueOf(20).equals(railRoad.getADM_KOD()))
                    {
                        final Integer dor_kod = railRoad.getDOR_KOD();
                        if (railSQLRoadFilter ==null)
                            railSQLRoadFilter = dor_kod.toString();
                        else
                            railSQLRoadFilter +=","+ dor_kod.toString();
                        railSet.add(dor_kod);

                    }
                }
                if (railSQLRoadFilter ==null)
                    railSQLRoadFilter ="";
                else
                    railSQLRoadFilter =" IN ("+ railSQLRoadFilter +")";
            }

            conn= getDerbyConnection();
//TODO !!!!проверить политику заполнения кеша данных после получения консолидационных таблиц!!!!
            List<Map> outTuples = new LinkedList<Map>();

            Date dt = new Date();
            Date dtN = new Date(dt.getTime()+TablesTypes.DAY_MILS);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String stringDate=sdf.format(dt);
            String stringDateN=sdf.format(dtN);

            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
            String time=sdf2.format(dt);


                final String tblName = TablesTypes.WINDOWS;


                {
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("A","GRP");
                    addInParams.put("EVENT_TYPE",tblName);
                    sqlReamerConsolidator(conn, outTuples, tblName,addInParams);
                }

                {
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("EVENT_TYPE",tblName+"#TOT_ALL_D");
                    String addStringFilter="STATUS_PL<>1  \n" +
                            "and tbl.KD>=TIMESTAMP('"+stringDate+"','00:00')  \n" +
                            "and \n" +
                            "(\n" +
                            "\t(tbl.DT_KD>TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}>=60)\n" +
                            "\tor\n" +
                            "\t(tbl.DT_KD<TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}>=60)\n" +
                            "\tor\n" +
                            "\t(tbl.DT_KD>TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}>=60 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}<=0)" +
                            ")";

                    sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams, "CNT");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false, "CNT");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true, "CNT");

                    addStringFilter="ID_TUCH<>0 and "+addStringFilter;
                    addInParams.put("EVENT_TYPE",tblName+"#TOT_ALL_DP");
                    sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams, "CNT2");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false, "CNT2");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true, "CNT2");

                }

                {
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("EVENT_TYPE",tblName+"#2");
                    String addStringFilter="STATUS_PL<>1  \n" +
                            "and tbl.KD>=TIMESTAMP('"+stringDate+"','00:00')  \n" +
                            "and \n" +
                            "(\n" +
                            "\t(tbl.DT_KD>TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}<=120 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}>=60)\n" +
                            "\tor\n" +
                            "\t(tbl.DT_KD<TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}<=120 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}>=60)\n" +
                            "\tor\n" +
                            "\t(tbl.DT_KD>TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}<=120 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}>=60 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}<=0)" +
                            ")";

                    sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams, "CNT");

                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false, "CNT");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true, "CNT");


                    addStringFilter="ID_TUCH<>0 and "+addStringFilter;
                    addInParams.put("EVENT_TYPE",tblName+"#2P");
                    sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams, "CNT2");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false, "CNT2");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true, "CNT2");

                }

                {
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("EVENT_TYPE",tblName+"#23");
                    String addStringFilter="STATUS_PL<>1  \n" +
                            "and tbl.KD>=TIMESTAMP('"+stringDate+"','00:00')  \n" +
                            "and \n" +
                            "(\n" +
                            "\t(tbl.DT_KD>TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}<=180 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}>120)\n" +
                            "\tor\n" +
                            "\t(tbl.DT_KD<TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}<=180 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}>120)\n" +
                            "\tor\n" +
                            "\t(tbl.DT_KD>TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}<=180 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}>120 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}<=0)" +
                            ")";

                    sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams, "CNT");

                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false, "CNT");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true, "CNT");

                    addStringFilter="ID_TUCH<>0 and "+addStringFilter;
                    addInParams.put("EVENT_TYPE",tblName+"#23P");
                    sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams, "CNT2");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false, "CNT2");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true, "CNT2");

                }

                {
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("EVENT_TYPE",tblName+"#35");
                    String addStringFilter="STATUS_PL<>1  \n" +
                            "and tbl.KD>=TIMESTAMP('"+stringDate+"','00:00')  \n" +
                            "and \n" +
                            "(\n" +
                            "\t(tbl.DT_KD>TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}<=300 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}>180)\n" +
                            "\tor\n" +
                            "\t(tbl.DT_KD<TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}<=300 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}>180)\n" +
                            "\tor\n" +
                            "\t(tbl.DT_KD>TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}<=300 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}>180 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}<=0)" +
                            ")";

                    sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams, "CNT");

                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false, "CNT");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true, "CNT");

                    addStringFilter="ID_TUCH<>0 and "+addStringFilter;
                    addInParams.put("EVENT_TYPE",tblName+"#35P");
                    sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams, "CNT2");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false, "CNT2");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true, "CNT2");

                }

                {
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("EVENT_TYPE",tblName+"#5");
                    String addStringFilter="STATUS_PL<>1  \n" +
                            "and tbl.KD>=TIMESTAMP('"+stringDate+"','00:00')  \n" +
                            "and \n" +
                            "(\n" +
                            "\t(tbl.DT_KD>TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}>300)\n" +
                            "\tor\n" +
                            "\t(tbl.DT_KD<TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}>300)\n" +
                            "\tor\n" +
                            "\t(tbl.DT_KD>TIMESTAMP('1900-01-01','00:00') and {fn timestampdiff(SQL_TSI_MINUTE,tbl.ND,tbl.KD)}>300 and {fn timestampdiff(SQL_TSI_MINUTE,tbl.DT_ND,tbl.DT_KD)}<=0)" +
                            ")";

                    sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams, "CNT");

                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false, "CNT");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true, "CNT");

                    addStringFilter="ID_TUCH<>0 and "+addStringFilter;
                    addInParams.put("EVENT_TYPE",tblName+"#5P");
                    sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams, "CNT2");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false, "CNT2");
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true, "CNT2");

                }


            return outTuples.toArray(new Map[outTuples.size()]);
        }
        finally
        {
            DbUtil.closeAll(null,null,conn,true);
        }
    }

    private String getDelaySQL4Total(String tblName, String addFilter, String trainIdField)
    {
        if (addFilter==null)
            addFilter="";
        else if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
            addFilter=" and "+addFilter;

        return "select '##' as PLACE_ID,count(distinct " + trainIdField + ") CNT  from " +tblName+" tbl "+" WHERE "+ TablesTypes.DOR_CODE+railSQLRoadFilter+addFilter;
    }

    private String getDelaySQL4DOR(String tblName, String addFilter, String trainIdField) {

        if (addFilter==null)
            addFilter="";
        else if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
            addFilter=" and "+addFilter;

        return "select "+ TablesTypes.DOR_CODE+",trim(char(int("+TablesTypes.DOR_CODE+ "))) || '##00' as PLACE_ID,count(distinct " + trainIdField + ") CNT " +
                        " from "+tblName+" tbl WHERE "+TablesTypes.DOR_CODE+railSQLRoadFilter+addFilter+" group by DOR_KOD";
    }

    private String getDelaySQL(String tblName, String addFilter, String trainIdField)
    {

        if (addFilter==null)
            addFilter="";
        else if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
            addFilter=" and "+addFilter;

        return "select pl.DOR_KOD,pl.POLG_ID,pl.NAME as PNAME,pl.NUM,trim ( char(pl.DOR_KOD)) || '##' || trim ( char(pl.POLG_ID)) as PLACE_ID \n" +
                ",count( distinct  trim (char(tdp.DATATYPE_ID)) || '##' ||trim(char(po.POLG_ID))  || '##'  || " + trainIdField + ") as CNT\n" +
                            "from "+tblName+"_PLACES tdp,"+tblName+" tbl,POLG_OBJ po, TABLO_POLG_LIST pl\n" +
                            "where po.POLG_TYPE=100185 and po.OBJ_OSN_ID=tdp.OBJ_OSN_ID and pl.POLG_ID=po.POLG_ID and pl.DOR_KOD<>0 and pl.DOR_KOD=tbl.DOR_KOD and pl.POLG_TYPE=100185 \n" +
                            "and tbl.DATA_OBJ_ID=tdp.DATA_OBJ_ID "+addFilter+"\n" +
                            "group by pl.POLG_ID,pl.NAME,pl.DOR_KOD,pl.NUM order by DOR_KOD,pl.NUM";
    }

    private void sqlConsolidator(Connection conn, List<Map> outTuples, String tblName, String addFilter, Map<String, Object> addParams, String cntName) {
        if (hasTable(tblName) && hasTable(tblName +"_"+ TablesTypes.PLACES))
        try
        {
            List<Map> rv= new LinkedList<Map>();

            if (addFilter==null)
                addFilter="";
            else if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
                addFilter=" and "+addFilter;

            String query = "select pl.DOR_KOD,pl.POLG_ID,pl.NAME as PNAME,pl.NUM,trim ( char(pl.DOR_KOD)) || '##' || trim ( char(pl.POLG_ID)) as " + TablesTypes.PLACE_ID + " ,count( distinct trim ( char(tdp.DATATYPE_ID)) ||'##' || tdp.DATA_OBJ_ID || '##' || trim(char(po.POLG_ID)) ) as " + cntName + " \n" +
                    "from "+tblName+"_PLACES tdp,"+tblName+" tbl,POLG_OBJ po, TABLO_POLG_LIST pl\n" +
                    "where po.POLG_TYPE=100185 and po.OBJ_OSN_ID=tdp.OBJ_OSN_ID and pl.POLG_ID=po.POLG_ID and pl.DOR_KOD<>0 and pl.DOR_KOD=tbl.DOR_KOD and pl.POLG_TYPE=100185 \n" +
                    "and tbl.DATA_OBJ_ID=tdp.DATA_OBJ_ID "+addFilter+"\n" +
                    "group by pl.POLG_ID,pl.NAME,pl.DOR_KOD,pl.NUM order by DOR_KOD,pl.NUM";

            fillTuplesByQuery(conn, rv, query, tblName,null, cntName);
            for (Map map : rv)
                for (String key : addParams.keySet())
                    map.put(key,addParams.get(key));
            outTuples.addAll(rv);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


//    private void sqlConsolidator2(final Connection conn, final List<Map> outTuples, String tblName, String addFilter,String addAggregate,
//                                  final Map<String, Object> addInParams,
//                                  final Map<String, String> addRSParams
//    ) {
//        if (hasTable(tblName) && hasTable(tblName +"_"+ TablesTypes.PLACES))
//        try
//        {
//            List<Map> rv= new LinkedList<Map>();
//
//            if (addAggregate==null)
//                addAggregate="";
//            else if (addAggregate.length()>0 && !addAggregate.trim().startsWith(","))
//                addAggregate=","+addAggregate;
//
//            if (addFilter==null)
//                addFilter="";
//            else if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
//                addFilter=" and "+addFilter;
//
//            String query = "select pl.DOR_KOD,pl.POLG_ID,pl.NAME as PNAME,pl.NUM,trim ( char(pl.DOR_KOD)) || '##' || trim ( char(pl.POLG_ID)) as "+TablesTypes.PLACE_ID+", \n" +
//                            "count(*) as CNT "+addAggregate+" \n" +
//                            "from (select  distinct tdp.DATATYPE_ID,tdp.DATA_OBJ_ID,po.POLG_ID from "+tblName+"_PLACES tdp,POLG_OBJ po\n" +
//                            "\t\twhere po.POLG_TYPE=100185 and po.OBJ_OSN_ID=tdp.OBJ_OSN_ID) tdp,"+tblName+" tbl,TABLO_POLG_LIST pl\n" +
//                            "where  pl.DOR_KOD<>0 \n" +
//                            "\tand pl.DOR_KOD=tbl.DOR_KOD \n" +
//                            "\tand pl.POLG_TYPE=100185 \n" +
//                            "\tand pl.POLG_ID=tdp.POLG_ID \n" +
//                            "\tand tbl.DATA_OBJ_ID=tdp.DATA_OBJ_ID \n" +
//                            "\tand tbl.DATATYPE_ID=tdp.DATATYPE_ID \n" +
//                             addFilter +
//                            " group by pl.POLG_ID,pl.NAME,pl.DOR_KOD,pl.NUM order by DOR_KOD,pl.NUM";
//
//
//            fillTuplesByQuery(conn, rv, query, tblName,addRSParams, "CNT");
//            for (Map map : rv)
//                for (String key : addInParams.keySet())
//                    map.put(key,addInParams.get(key));
//            outTuples.addAll(rv);
//        }
//        catch (SQLException e)
//        {
//            e.printStackTrace();
//        }
//    }


    //Делает развертку таблиц, что бы были видны все полигоны, даже если в них не было событий
    private void sqlReamerConsolidator(final Connection conn, final List<Map> outTuples, String tblName,final Map<String, Object> addInParams) {
        if (hasTable(tblName))
        try
        {

            List<Map> rv= new LinkedList<Map>();

            String query ="select distinct pl."+TablesTypes.DOR_CODE+",pl."+TablesTypes.POLG_ID+",pl.NAME || '(' || trim ( char(pl."+TablesTypes.POLG_ID+")) ||  ')' as "+TablesTypes.POLG_NAME+",pl.NUM,trim ( char(pl."+TablesTypes.DOR_CODE+")) || '##' || trim ( char(pl."+TablesTypes.POLG_ID+")) as "+TablesTypes.PLACE_ID+"\n" +
                    " from TABLO_POLG_LIST pl where  pl.DOR_KOD "+railSQLRoadFilter+" and pl.POLG_TYPE=100185 order by DOR_KOD,pl.NUM";

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
                    int dor_kod = initDOR_KOD(rs, tuple);

                    if (commonFilter(tuple,dor_kod,tblName))
                    {
                        tuple.put("EVENT_TYPE", tblName);
                        tuple.put(TablesTypes.POLG_ID,rs.getInt(TablesTypes.POLG_ID));
                        tuple.put(TablesTypes.POLG_NAME,rs.getObject(TablesTypes.POLG_NAME));
                        tuple.put(TablesTypes.PLACE_ID,rs.getObject(TablesTypes.PLACE_ID));
                        tuple.put("NUM",rs.getInt("NUM"));
                        total+=1;
                        rv.add(tuple);
                    }
                    i++;
                }
                System.out.println(tblName+" Reamer consolidation rows:" + i+" total:"+total);

            }
            finally
            {
                DbUtil.closeAll(rs, stmt, null, false);
            }

            for (Map map : rv)
                for (String key : addInParams.keySet())
                    map.put(key,addInParams.get(key));
            outTuples.addAll(rv);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }



//    private void sqlDorConsolidator2(final Connection conn, final List<Map> outTuples, String tblNames,String query,
//                                  final Map<String, Object> addInParams,
//                                  final Map<String, String> addRSParams,boolean total
//
//    )
//    {
//        sqlDorConsolidator2(conn, outTuples, new String[]{tblNames},query,addInParams,addRSParams,total, "CNT");
//    }



//    private void sqlDorConsolidator2(final Connection conn, final List<Map> outTuples, String[] tblNames, String query,
//                                     final Map<String, Object> addInParams,
//                                     final Map<String, String> addRSParams, boolean total, String cntName
//
//    ) {
//
//        for (String tblName : tblNames)
//            if (!hasTable(tblName))
//                return;
//        try
//        {
//            List<Map> rv= new LinkedList<Map>();
//            fillDorTuplesByQuery(conn, rv, query, tblNames[0],addRSParams,total, cntName);
//            for (Map map : rv)
//                for (String key : addInParams.keySet())
//                    map.put(key,addInParams.get(key));
//            outTuples.addAll(rv);
//        }
//        catch (SQLException e)
//        {
//            e.printStackTrace();
//        }
//    }


    //
    private void sqlDorConsolidator(final Connection conn, final List<Map> outTuples, String tblName, String addFilter, String addAggregate,
                                    final Map<String, Object> addInParams,
                                    final Map<String, String> addRSParams, boolean total, String cntName
    ) {
        if (hasTable(tblName))
        try
        {
            List<Map> rv= new LinkedList<Map>();

            if (addAggregate==null)
                addAggregate="";
            else if (addAggregate.length()>0 && !addAggregate.trim().startsWith(","))
                addAggregate=","+addAggregate;

            if (addFilter==null)
                addFilter="";
            if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
                addFilter=" and "+addFilter;


            String query;
            if (total)
                query = "select '##' as PLACE_ID,count(*) " + cntName + " " +addAggregate + " from "+tblName+" tbl "+" WHERE "+TablesTypes.DOR_CODE+railSQLRoadFilter+addFilter;
            else
                query = "select "+TablesTypes.DOR_CODE+",trim(char(int("+TablesTypes.DOR_CODE+ "))) || '##00' as PLACE_ID,count(*) " + cntName + " " +
                    addAggregate +
                    " from "+tblName+" tbl WHERE "+TablesTypes.DOR_CODE+railSQLRoadFilter+addFilter+" group by DOR_KOD";


            fillDorTuplesByQuery(conn, rv, query, tblName, addRSParams,total, cntName);
            for (Map map : rv)
                for (String key : addInParams.keySet())
                    map.put(key,addInParams.get(key));
            outTuples.addAll(rv);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
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
            }
        }
        finally
        {
            DbUtil.closeAll(rs,stmt,conn, true);
        }
        return allTables.size()>0;
    }

    @Override
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
        if (!railSet.contains(dorKod))
        {
            System.out.print("skip tuple for type = " + type+" dorKod:"+dorKod+" tule:");
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
