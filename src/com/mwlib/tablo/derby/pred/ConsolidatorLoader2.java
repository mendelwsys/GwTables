package com.mwlib.tablo.derby.pred;

import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.ICliProvider;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.cache.WrongParam;
import com.mwlib.tablo.db.desc.DelayABGDDesc;
import com.mwlib.tablo.db.desc.DelayGIDTDesc;
import com.mwlib.tablo.test.tpolg.CliProviderFactoryImpl3;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;

import com.mwlib.tablo.analit2.BusinessUtils;


import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.10.14
 * Time: 12:30
 * Загрузчик консолидационных таблиц дерби, отличается от ConsolidatorLoader тем что
 * не создает не загружет данные в таблицу, исключительно "следит" за обновлением таблиц
 * и производит консолидацию
 */
public class ConsolidatorLoader2
{
    public static final String DEF_SESSIONID = "CONSOLIDATOR2";

    private Set<String> allTables = new HashSet<String>();

    private int ixReq=0;
    private Map[] mapParms;

    private boolean test;
    private String[] loaderTypes;
    private String dsName;// = DbUtil.DS_JAVA_CACHE_NAME;

    public ConsolidatorLoader2(String[] loaderTypes,String dsName,boolean test)
    {
        this.test=test;
        this.loaderTypes=loaderTypes;
        this.dsName=dsName;
        mapParms=new Map[loaderTypes.length];
    }


    protected void vipGid(Connection conn, List<Map> rv, String query) throws SQLException  //TODO !!!НЕ ВЕРНО!!!!
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
                tuple.put("EVENT_TYPE", TablesTypes.VIP_GID);

                String vid_name = rs.getString(TablesTypes.VID_NAME);
                if (vid_name==null)
                    continue;

                int dor_kod = initDOR_KOD(rs, tuple);

                {
                    int vid_id = rs.getInt(TablesTypes.VID_ID);
                    if (vid_id==TablesTypes.Z_ID_SERVAL)
                    {
                        tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                        tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                    }
                    else
                    {
                        tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod)+"_"+vid_name);
                        tuple.put(TablesTypes.VID_NAME, vid_name);
                    }
                    tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
                    tuple.put(TablesTypes.VID_ID, vid_id);
                }

                tuple.put("CNT",rs.getInt("CNT"));

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.VIP_GID))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("VIP GID consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }

//    protected void vipGid(Connection conn, List<Map> rv, String query) throws SQLException  //TODO !!!НЕ ВЕРНО!!!!
//    {
//        Statement stmt=null;
//        ResultSet rs=null;
//
//        try {
//            stmt = conn.createStatement();
//            rs = stmt.executeQuery(query);
//            int i=0;
//            int total=0;
//            while (rs.next())
//            {
//                Map<String,Object> tuple=new HashMap<String,Object>();
//                tuple.put("EVENT_TYPE", TablesTypes.VIP_GID);
//
//                String serv_char = rs.getString("SERV_CHAR");
//                if (serv_char==null)
//                    continue;
//
//                int dor_kod = initDOR_KOD(rs, tuple);
//
//                {
//
//                    tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod)+"_"+serv_char);
//                    tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
//
//
//                    tuple.put(TablesTypes.VID_NAME, serv_char);
//                    switch (serv_char)
//                    {
//                        case "П":
//                            tuple.put(TablesTypes.VID_ID, 28);
//                            break;
//                        case "Ш":
//                            tuple.put(TablesTypes.VID_ID, 70);
//                            break;
//                        case "Э":
//                            tuple.put(TablesTypes.VID_ID, 40);
//                            break;
//                        case "В":
//                            tuple.put(TablesTypes.VID_ID, 44);
//                            break;
////                        case "В":
////                            tuple.put(TablesTypes.VID_ID, 700);
////                            break;
////                        case "Э":
////                            tuple.put(TablesTypes.VID_ID, 1400);
////                            break;
////                        case "П":
////                            tuple.put(TablesTypes.VID_ID, 800);
////                            break;
////                        case "Ш":
////                            tuple.put(TablesTypes.VID_ID, 900);
////                            break;
//                        default:
//                        {
//                            tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
//                            tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
//                            tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
//                        }
//                            break;
//                    }
//                }
//
//                tuple.put("CNT",rs.getInt("CNT"));
//
//                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.VIP_GID))
//                {
//                    total+=rs.getInt("CNT");
//                    rv.add(tuple);
//                }
//                i++;
//            }
//            System.out.println("VIP GID consolidation rows:" + i + " total:" + total);
//
//        }
//        finally
//        {
//            DbUtil.closeAll(rs, stmt, null, false);
//        }
//    }

    private int initDOR_KOD(ResultSet rs, Map<String, Object> tuple) throws SQLException {
        int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
        tuple.put(TablesTypes.DOR_CODE, dor_kod);
        Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
        tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());
        return dor_kod;
    }


    protected void vagInTor(Connection conn, List<Map> rv, String query) throws SQLException
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
                tuple.put("EVENT_TYPE", TablesTypes.VAGTOR);

                int dor_kod = initDOR_KOD(rs, tuple);
//                if (dor_kod==0)
//                    continue;
                {
                    tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                    tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
                    tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                    tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
                }

                tuple.put("CNT",rs.getInt("CNT"));
                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.VAGTOR))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }

                i++;
            }
            System.out.println("Vag in TS consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }

    protected void lostTr(Connection conn, List<Map> rv, String query) throws SQLException
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
                tuple.put("EVENT_TYPE", TablesTypes.LOST_TRAIN);

                int dor_kod = initDOR_KOD(rs, tuple);
                {
                    tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                    tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
                    tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                    tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
                }

                tuple.put("CNT",rs.getInt("CNT"));

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.LOST_TRAIN))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }

                i++;
            }
            System.out.println("lost trains consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }




    protected void delayABGD_DOR_COD(Connection conn, List<Map> rv, String query) throws SQLException
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
                int datatype_id = rs.getInt(TablesTypes.DATATYPE_ID);
                String servName= DelayABGDDesc.getServNameByDataTypeId(datatype_id);
                if (servName!=null)
                    tuple.put("A",servName);
                else
                {
                    System.out.println("can't define datatype_id for gid delays= " + datatype_id);
                    continue;
                }

                tuple.put("EVENT_TYPE", TablesTypes.DELAYS_ABVGD);
                tuple.put(TablesTypes.DOR_CODE, TablesTypes.DOR_CODE_4_DELAY_TRAINS_SUM_TOTAL);

                tuple.put(TablesTypes.VID_NAME,TablesTypes.Z_SERVAL);
                tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SUM_SERVAL);

                tuple.put(TablesTypes.PRED_ID, TablesTypes.HIDE_ATTR);
                tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);

                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));
                //if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.DELAYS_ABVGD)) TODO Здесь не фильтровать, поскольку поезд должен быть соотнесен с дорогой правильно
                rv.add(tuple);

                i++;
            }
            System.out.println("delay ABGD Total consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }

    protected void delayABGD_SUMM(Connection conn, List<Map> rv, String query) throws SQLException
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
                int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
                int datatype_id = rs.getInt(TablesTypes.DATATYPE_ID);

                String servName= DelayABGDDesc.getServNameByDataTypeId(datatype_id);
                if (servName!=null)
                    tuple.put("A",servName);
                else
                {
                    System.out.println("can't define datatype_id for gid delays= " + datatype_id);
                    continue;
                }

                tuple.put("EVENT_TYPE", TablesTypes.DELAYS_ABVGD);
                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());

                tuple.put(TablesTypes.VID_NAME,TablesTypes.Z_SERVAL);
                tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SUM_SERVAL);
//                tuple.put(TablesTypes.HIDE_ATTR,1);//Не надо показывать и вставлять в таблицу

                tuple.put(TablesTypes.PRED_ID, -dor_kod+TablesTypes.HIDE_ATTR);
                tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);

                tuple.put("CNT",rs.getInt("CNT"));

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.DELAYS_ABVGD))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("delay ABGD Total consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }

    protected void delayABGD(Connection conn, List<Map> rv, String query) throws SQLException
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
                int dor_kod = rs.getInt(TablesTypes.DOR_CODE);

                int datatype_id = rs.getInt(TablesTypes.DATATYPE_ID);
                String servName= DelayABGDDesc.getServNameByDataTypeId(datatype_id);
                if (servName!=null)
                    tuple.put("A",servName);
                else
                {
                    System.out.println("can't define datatype_id for gid delays= " + datatype_id);
                    continue;
                }

                tuple.put("EVENT_TYPE", TablesTypes.DELAYS_ABVGD);
                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());
//                tuple.put("SNM",rs.getString("SNM"));



                String serv=BusinessUtils.fillVIDByBidName(tuple,rs.getString("SNM"));
                if (serv!=null)
                    tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod)+"_"+serv);
                else
                    tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);

                tuple.put("CNT",rs.getInt("CNT"));

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.DELAYS_ABVGD))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("delay ABGD consolidation rows:" + i + " total:" + total);
        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }



    protected void zmEAM(Connection conn, List<Map> rv, String query) throws SQLException
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
                tuple.put("EVENT_TYPE", TablesTypes.ZMTABLE);
                int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
//                if (dor_kod==0)
//                    continue;
                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                if (byDorCode==null)
                    continue;
                tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());
                int pred_id = rs.getInt(TablesTypes.PRED_ID);
                tuple.put(TablesTypes.PRED_ID, pred_id);

                {
                    Directory.Pred pred = Directory.getByPredId(pred_id);
                    if (pred!=null)
                    {
                        tuple.put(TablesTypes.PRED_NAME,pred.getSNAME());
                        BusinessUtils.fillVIDByPred(tuple, pred_id);
                    }
                    else
                    {
                        tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                        tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
                        tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                        tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);

                    }
                    tuple.put("CNT",rs.getInt("CNT"));
                }

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.ZMTABLE))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("ZM consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }


    protected void kmoGid(Connection conn, List<Map> rv, String query) throws SQLException
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
                tuple.put("EVENT_TYPE", TablesTypes.KMOTABLE);
                int dor_kod = rs.getInt(TablesTypes.DOR_CODE);

                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());
                int pred_id = rs.getInt(TablesTypes.PRED_ID);
                tuple.put(TablesTypes.PRED_ID, pred_id);

                {
                    Directory.Pred pred = Directory.getByPredId(pred_id);
                    if (pred!=null)
                    {
                        tuple.put(TablesTypes.PRED_NAME,pred.getSNAME());
                        BusinessUtils.fillVIDByPred(tuple, pred_id);
                    }
                    else
                    {
                        tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                        tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
                        tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                        tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);

                    }
                    tuple.put("CNT",rs.getInt("CNT"));
                }
                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.KMOTABLE))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("KMO consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }


    protected void delayGid_DOR_COD(Connection conn, List<Map> rv, String query) throws SQLException
    {
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
                int datatype_id = rs.getInt(TablesTypes.DATATYPE_ID);
                String servName= DelayGIDTDesc.getServNameByDataTypeId(datatype_id);
                if (servName!=null)
                    tuple.put("A",servName);
                else
                {
                    System.out.println("can't define datatype_id for gid delays= " + datatype_id);
                    continue;
                }
                tuple.put("EVENT_TYPE", TablesTypes.DELAYS_GID);
                tuple.put(TablesTypes.DOR_CODE, TablesTypes.DOR_CODE_4_DELAY_TRAINS_SUM_TOTAL);
                tuple.put(TablesTypes.DOR_NAME,"");

                tuple.put(TablesTypes.PRED_ID, TablesTypes.HIDE_ATTR);
                tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);

                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));
                //if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.DELAYS_GID)) TODO Здесь не фильтровать, поскольку поезд должен быть соотнесен с дорогой правильно
                rv.add(tuple);
                i++;
            }
            System.out.println("delay GID total consolidation by VID rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }



    protected void delayGid_SUMM_VID(Connection conn, List<Map> rv, String query) throws SQLException
    {
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
                int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
                int datatype_id = rs.getInt(TablesTypes.DATATYPE_ID);

                String servName= DelayGIDTDesc.getServNameByDataTypeId(datatype_id);
                if (servName!=null)
                    tuple.put("A",servName);
                else
                {
                    System.out.println("can't define datatype_id for gid delays= " + datatype_id);
                    continue;
                }

                tuple.put("EVENT_TYPE", TablesTypes.DELAYS_GID);
                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());

//                tuple.put(TablesTypes.HIDE_ATTR,1);//Не надо показывать и вставлять в таблицу

                String serv=BusinessUtils.fillVIDByBidName(tuple,rs.getString("SNM"));

                tuple.put(TablesTypes.PRED_ID, -dor_kod+TablesTypes.HIDE_ATTR+serv);
                tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);

                tuple.put("CNT",rs.getInt("CNT"));

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.DELAYS_GID))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }

                i++;
            }
            System.out.println("delay GID total consolidation by VID rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }


    protected void delayGid_SUMM(Connection conn, List<Map> rv, String query) throws SQLException
    {
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
                int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
                int datatype_id = rs.getInt(TablesTypes.DATATYPE_ID);

                String servName= DelayGIDTDesc.getServNameByDataTypeId(datatype_id);
                if (servName!=null)
                    tuple.put("A",servName);
                else
                {
                    System.out.println("can't define datatype_id for gid delays= " + datatype_id);
                    continue;
                }

                tuple.put("EVENT_TYPE", TablesTypes.DELAYS_GID);
                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());

                tuple.put(TablesTypes.VID_NAME,TablesTypes.Z_SERVAL);
                tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SUM_SERVAL);
//                tuple.put(TablesTypes.HIDE_ATTR,1);//Не надо показывать и вставлять в таблицу

                tuple.put(TablesTypes.PRED_ID, -dor_kod+TablesTypes.HIDE_ATTR);
                tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);

                tuple.put("CNT",rs.getInt("CNT"));

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.DELAYS_GID))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("delay GID total consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }


    protected void delayGid(Connection conn, List<Map> rv, String query) throws SQLException
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
                tuple.put("EVENT_TYPE", TablesTypes.DELAYS_GID);
                int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
                int datatype_id = rs.getInt(TablesTypes.DATATYPE_ID);

                String servName=DelayGIDTDesc.getServNameByDataTypeId(datatype_id);
                if (servName!=null)
                    tuple.put("A",servName);
                else
                {
                    System.out.println("can't define datatype_id for gid delays= " + datatype_id);
                    continue;
                }

                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());
                int pred_id = rs.getInt(TablesTypes.PRED_ID);
                tuple.put(TablesTypes.PRED_ID, pred_id);

                {
                    Directory.Pred pred = Directory.getByPredId(pred_id);
                    String serv=BusinessUtils.fillVIDByHozId(tuple,rs.getInt(TablesTypes.HOZ_ID));
                    if (pred!=null)
                    {
                        tuple.put(TablesTypes.PRED_NAME,pred.getSNAME());
                        tuple.put(TablesTypes.PRED_ID, pred_id);
                        String servByPRed=BusinessUtils.fillVIDByPred(tuple,pred_id);
                        if ((servByPRed==null && servByPRed!=serv) || (servByPRed!=null && !servByPRed.equals(serv)))
                            System.out.println("conflicting definition of service for predId:"+pred_id+" name:"+pred.getSNAME()+" servByPRed = " + servByPRed+" servByHozId = "+serv);   //!!!TODO Обработать!!!!
                    }
                    else
                    {
                        if (serv!=null)
                            tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod)+"_"+serv);
                        else
                            tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                        tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
                    }
                    tuple.put("CNT",rs.getInt("CNT"));
                }
                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.DELAYS_GID))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("delay GID consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }

    protected void refuse(Connection conn, List<Map> rv,String query) throws SQLException
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
                tuple.put("EVENT_TYPE",TablesTypes.REFUSES);
                int dor_kod = initDOR_KOD(rs, tuple);
                int pred_id = rs.getInt(TablesTypes.PRED_ID);
                tuple.put(TablesTypes.PRED_ID, pred_id);

                {
                    Directory.Pred pred = Directory.getByPredId(pred_id);
                    if (pred!=null)
                    {
                        tuple.put(TablesTypes.PRED_NAME,pred.getSNAME());
                        BusinessUtils.fillVIDByPred(tuple, pred.getPRED_ID());
                    }
                    else
                    {
                        String serv=BusinessUtils.fillVIDByHozId(tuple,rs.getInt(TablesTypes.HOZ_ID));
                        if (serv!=null)
                            tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod)+"_"+serv);
                        else
                            tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                        tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);

                    }
                    tuple.put("CNT",rs.getInt("CNT"));
                }

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.REFUSES))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("Refuses consolidation rows:" + i+" total:"+total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }


    protected void viol(Connection conn, List<Map> rv,String query) throws SQLException
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
                tuple.put("EVENT_TYPE",TablesTypes.VIOLATIONS);
                int dor_kod = initDOR_KOD(rs, tuple);
                int pred_id = rs.getInt(TablesTypes.PRED_ID);
                tuple.put(TablesTypes.PRED_ID, pred_id);

                {
                    Directory.Pred pred = Directory.getByPredId(pred_id);
                    if (pred!=null)
                    {
                        tuple.put(TablesTypes.PRED_NAME,pred.getSNAME());
                        BusinessUtils.fillVIDByPred(tuple, pred_id);
                    }
                    else
                    {
                        String serv=BusinessUtils.fillVIDByHozId(tuple,rs.getInt(TablesTypes.HOZ_ID));
                        if (serv!=null)
                            tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod)+"_"+serv);
                        else
                            tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                        tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);


                    }
                    tuple.put("CNT",rs.getInt("CNT"));
                }

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.VIOLATIONS))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("Windows consolidation rows:" + i+" total:"+total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }


    protected void warnInTime(Connection conn, List<Map> rv, String query) throws SQLException
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
                tuple.put("EVENT_TYPE", TablesTypes.WARNINGS);
                tuple.put("A","GRP");

                int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
//                if (dor_kod==0)
//                    continue;
                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                if (byDorCode==null)
                    continue;
                tuple.put(TablesTypes.DOR_NAME,byDorCode.getSNAME());
                {
                    tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                    tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
                    tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                    tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
                }
                tuple.put("CNT",rs.getInt("CNT"));
                tuple.put("TLN", rs.getFloat("TLN"));


                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.WARNINGSINTIME))
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


    protected void tech(Connection conn, List<Map> rv,String query) throws SQLException
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
                {
                    tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                    tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
                    tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                    tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
                }

                tuple.put("CNT",rs.getInt("CNT"));

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.WINDOWS))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("Tech consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }





    protected void window(Connection conn, List<Map> rv,String query) throws SQLException
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
                tuple.put("EVENT_TYPE",TablesTypes.WINDOWS);
                int dor_kod=initDOR_KOD(rs, tuple);

                int pred_id = rs.getInt(TablesTypes.PRED_ID);

                Directory.Pred pred = Directory.getByPredId(pred_id);
                if (pred!=null)
                {
                    tuple.put(TablesTypes.PRED_ID, pred_id);
                    tuple.put(TablesTypes.PRED_NAME, pred.getSNAME());
                    BusinessUtils.fillVIDByPred(tuple, pred_id);
                }
                else
                {
                    tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                    tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
                    tuple.put(TablesTypes.VID_NAME,TablesTypes.Z_SERVAL);
                    tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
                }


                tuple.put("CNT",rs.getInt("CNT"));

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.WINDOWS))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println("Windows consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }

    protected void warnReq(List<Map> rv, Connection conn,String query) throws SQLException
    {
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
                tuple.put("EVENT_TYPE",TablesTypes.WARNINGS);

                int dor_kod=initDOR_KOD(rs, tuple);

                int pred_id = rs.getInt(TablesTypes.PRED_ID);
                Directory.Pred pred = Directory.getByPredId(pred_id);
                if (pred!=null)
                {
                    tuple.put(TablesTypes.PRED_ID, pred_id);
                    tuple.put(TablesTypes.PRED_NAME, pred.getSNAME());
                    BusinessUtils.fillVIDByPred(tuple, pred_id);
                }
                else
                {
                    tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                    tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);
                    tuple.put(TablesTypes.VID_NAME,TablesTypes.Z_SERVAL);
                    tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
                }

                tuple.put("CNT",rs.getInt("CNT"));
                tuple.put("TLN", rs.getFloat("TLN"));

                if (commonFilter(tuple,dor_kod,tuple.get(TablesTypes.PRED_ID),TablesTypes.WARNINGS))
                {
                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
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

            List<Map> all = new LinkedList<Map>();

            if (hasTable(TablesTypes.DELAYS_ABVGD))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",DATATYPE_ID,count(distinct TRAINID) CNT from "+TablesTypes.DELAYS_ABVGD+" group by DATATYPE_ID,"+TablesTypes.DOR_CODE;

                delayABGD_SUMM(conn, rv, query);       //Здесь кол-во поездов задержанных по все дороге.  (Здесь не надо проводить консолидацию внутри каждой службы, поскольку у нас нет разбивки по предприятиям)

                all.addAll(rv); //TODO Делаю передачу заголовков
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.DELAYS_ABVGD))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select DATATYPE_ID,count(distinct TRAINID) CNT from "+TablesTypes.DELAYS_ABVGD+" group by DATATYPE_ID";

                delayABGD_DOR_COD(conn, rv, query);       //Здесь кол-во поездов задержанных по все дороге.  (Здесь не надо проводить консолидацию внутри каждой службы, поскольку у нас нет разбивки по предприятиям)

                all.addAll(rv); //TODO Делаю передачу заголовков
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.DELAYS_ABVGD))
            try {
                List<Map> rv = new LinkedList<Map>();
                //String query = "select "+TablesTypes.DOR_CODE+",DATATYPE_ID,SERVICECODE,count(*) CNT from "+TablesTypes.DELAYS_ABVGD+" group by SERVICECODE,DATATYPE_ID,"+TablesTypes.DOR_CODE;
                String query = "select " + TablesTypes.DOR_CODE + ",DATATYPE_ID,SERVICECODE," +
                        "CASE \n" +
                        "      WHEN SERVICECODE=130 THEN 'В' \n" +
                        "      WHEN SERVICECODE=143 THEN 'П' \n" +

                        "      WHEN SERVICECODE=144 THEN 'П' \n" +

                        "      WHEN SERVICECODE=152 THEN 'Ш' \n" +
                        "      WHEN SERVICECODE=157 THEN 'Э' \n" +
                        "      WHEN SERVICECODE=6666 THEN 'ДПМ' \n" +
                        "      ELSE '" + TablesTypes.Z_SERVAL + "' \n" +
                        "   END as SNM, " +
                        "   TRAINID, count(*) CNT from " + TablesTypes.DELAYS_ABVGD + " group by TRAINID,SERVICECODE, DATATYPE_ID," + TablesTypes.DOR_CODE + " order by " + TablesTypes.DOR_CODE;

                query="SELECT "+TablesTypes.DOR_CODE+",DATATYPE_ID, SNM, count(distinct TRAINID) CNT FROM ("+query+") as tbl group by SNM, DATATYPE_ID,"+TablesTypes.DOR_CODE+" order by "+TablesTypes.DOR_CODE;

                delayABGD(conn, rv, query);       //Здесь кол-во поездов задержанных по вине той или иной службы.

                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.DELAYS_GID))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",DATATYPE_ID,count(distinct TRAIN_ID) CNT from "+TablesTypes.DELAYS_GID+" group by DATATYPE_ID,"+TablesTypes.DOR_CODE+" order by "+TablesTypes.DOR_CODE;
                delayGid_SUMM(conn, rv, query);       //Здесь кол-во поездов задержанных по все дороге.
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.DELAYS_GID))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",DATATYPE_ID," +
                       "CASE \n" +
                        "      WHEN HOZ_ID=150305 THEN 'П' \n" +
                        "      WHEN HOZ_ID=150306 THEN 'Ш' \n" +
                        "      WHEN HOZ_ID=150309 THEN 'Э' \n" +
                        "      WHEN HOZ_ID=150304 THEN 'В' \n" +
                        "      ELSE '" + TablesTypes.Z_SERVAL + "' \n" +
                        "   END as SNM, " +
                        "HOZ_ID," +
                        "TRAIN_ID, count(*) CNT from "+TablesTypes.DELAYS_GID+" group by DATATYPE_ID,HOZ_ID,TRAIN_ID,"+TablesTypes.DOR_CODE;

                 query="SELECT "+TablesTypes.DOR_CODE+",DATATYPE_ID, SNM, count(distinct TRAIN_ID) CNT FROM ("+query+") as tbl group by SNM, DATATYPE_ID,"+TablesTypes.DOR_CODE+" order by "+TablesTypes.DOR_CODE;

                delayGid_SUMM_VID(conn, rv, query);       //Здесь кол-во поездов задержанных по все дороге.
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.DELAYS_GID))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query =
                        "select DATATYPE_ID,count(distinct TRAIN_ID) CNT from "+TablesTypes.DELAYS_GID+" group by DATATYPE_ID";

                delayGid_DOR_COD(conn, rv, query);       //Здесь кол-во поездов задержанных по все дороге.
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }



            if (hasTable(TablesTypes.DELAYS_GID))
            try{
                    List<Map> rv= new LinkedList<Map>();
                    //String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,DATATYPE_ID,count(*) CNT from "+TablesTypes.DELAYS_GID+" group by PRED_ID,DATATYPE_ID,"+TablesTypes.DOR_CODE+" order by "+TablesTypes.DOR_CODE;
                    String query = "select "+TablesTypes.DOR_CODE+",DATATYPE_ID,HOZ_ID,PRED_ID,count(distinct TRAIN_ID) CNT from "+TablesTypes.DELAYS_GID+" group by PRED_ID,HOZ_ID,DATATYPE_ID,"+TablesTypes.DOR_CODE+" order by "+TablesTypes.DOR_CODE;
                    delayGid(conn, rv, query);
                    all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.ZMTABLE))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,count(*) CNT from "+TablesTypes.ZMTABLE+" WHERE DATATYPE_ID=84 group by PRED_ID,"+TablesTypes.DOR_CODE+" order by "+TablesTypes.DOR_CODE;
                zmEAM(conn, rv, query);
                for (Map map : rv)
                    map.put("A","Y");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.ZMTABLE))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,count(*) CNT from "+TablesTypes.ZMTABLE+" WHERE DATATYPE_ID=85 group by PRED_ID,"+TablesTypes.DOR_CODE+" order by "+TablesTypes.DOR_CODE;
                zmEAM(conn, rv, query);
                for (Map map : rv)
                    map.put("A","N");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.KMOTABLE))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,count(*) CNT from "+TablesTypes.KMOTABLE+" group by PRED_ID,"+TablesTypes.DOR_CODE;
                kmoGid(conn, rv, query);
                for (Map map : rv)
                    map.put("A","Y");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.KMOTABLE))
            try {
                 List<Map> rv= new LinkedList<Map>();
                 String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,count(*) CNT from "+TablesTypes.KMOTABLE+" WHERE  MERA_BEZ IS NOT NULL OR MERA_BEZ<> 'NULL'  group by PRED_ID,"+TablesTypes.DOR_CODE;
                 kmoGid(conn, rv, query);
                 for (Map map : rv)
                     map.put("A","N");
                 all.addAll(rv);
             }
             catch (SQLException e)
             {
                 e.printStackTrace();
             }

            if (hasTable(TablesTypes.LOST_TRAIN))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",count(*) CNT from "+TablesTypes.LOST_TRAIN+" group by "+TablesTypes.DOR_CODE;
                lostTr(conn, rv, query);
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.VAGTOR))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",count(*) CNT from "+TablesTypes.VAGTOR+" WHERE ADM_KOD=20 group by "+TablesTypes.DOR_CODE;
                vagInTor(conn, rv, query);
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.VIP_GID))
            try {
                List<Map> rv= new LinkedList<Map>();
                /*
                (select "+TablesTypes.DOR_CODE+",SERV_CHAR as "+TablesTypes.VID_NAME+",count(*) CNT from "+TablesTypes.VIP_GID+" group by "+TablesTypes.DOR_CODE+",SERV_CHAR) as tbl
                 */

                String query1 = "select "+TablesTypes.DOR_CODE+
                 ", CASE \n" +
                        "      WHEN SERV_CHAR IN ('П','Ш','Э','В') THEN  SERV_CHAR\n" +
                        "      WHEN SERV_CHAR IS NOT NULL  THEN 'Прочие службы'\n" +
                        "      ELSE NULL "+
                 "  END as "+TablesTypes.VID_NAME+
                 ", CASE \n" +
                 "      WHEN SERV_CHAR='П' THEN  28\n" +
                 "      WHEN SERV_CHAR='Ш' THEN  70\n" +
                 "      WHEN SERV_CHAR='Э' THEN  40\n" +
                 "      WHEN SERV_CHAR='В' THEN  44\n" +
                 "      WHEN SERV_CHAR IS NOT NULL  THEN "+TablesTypes.Z_ID_SERVAL+"\n" +
                 "      ELSE " + TablesTypes.Z_ID_SUM_SERVAL + " \n" +
                 "   END as "+TablesTypes.VID_ID+" from " +TablesTypes.VIP_GID;

                String query=" SELECT "+TablesTypes.DOR_CODE+","+TablesTypes.VID_NAME+","+TablesTypes.VID_ID+", count(*) CNT FROM ("+query1+") as tbl "+
                " group by "+TablesTypes.DOR_CODE+", "+TablesTypes.VID_NAME+","+TablesTypes.VID_ID;

                vipGid(conn, rv, query);
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.WARNINGSINTIME))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",SUM(F401_GVN) CNT,SUM(F401_GVL) TLN from "+TablesTypes.WARNINGSINTIME+" group by "+TablesTypes.DOR_CODE;
                warnInTime(conn, rv, query);
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.TECH))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",count(*) CNT from "+TablesTypes.TECH+" group by "+TablesTypes.DOR_CODE+"";
                tech(conn, rv, query);
                for (Map map : rv)
                {
                    map.put("EVENT_TYPE",TablesTypes.WINDOWS);
                    map.put("A","TECH");
                }
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.REFUSES))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,HOZ_ID,count(*) CNT from "+TablesTypes.REFUSES+" WHERE datatype_id<>73 group by PRED_ID,HOZ_ID,"+TablesTypes.DOR_CODE;
                refuse(conn, rv, query);
                for (Map map : rv)
                    map.put("A","Y");

                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.REFUSES))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,HOZ_ID,count(*) CNT from "+TablesTypes.REFUSES+" WHERE datatype_id=73 group by PRED_ID,HOZ_ID,"+TablesTypes.DOR_CODE;
                refuse(conn, rv, query);
                for (Map map : rv)
                    map.put("A","N");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.VIOLATIONS))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,HOZ_ID,count(*) CNT from "+TablesTypes.VIOLATIONS+" WHERE datatype_id<>72 group by PRED_ID,HOZ_ID,"+TablesTypes.DOR_CODE;
                viol(conn, rv, query);
                for (Map map : rv)
                    map.put("A","Y");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.VIOLATIONS))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,HOZ_ID,count(*) CNT from "+TablesTypes.VIOLATIONS+" WHERE datatype_id=72 group by PRED_ID,HOZ_ID,"+TablesTypes.DOR_CODE;
                viol(conn, rv, query);
                for (Map map : rv)
                    map.put("A","N");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }


            if (hasTable(TablesTypes.WARNINGS))
            try {
                List<Map> rv= new LinkedList<Map>();
//                String query = "select " + TablesTypes.DOR_CODE + ",PRED_ID,count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " WHERE TIM_OTM = TIMESTAMP('9999-12-31', '00:00:00') " +
                String query = "select " + TablesTypes.DOR_CODE + ",PRED_ID,count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " WHERE FIXED_END_DATE = 0 " +
                        " group by PRED_ID," + TablesTypes.DOR_CODE;
                warnReq(rv, conn, query);
                for (Map map : rv)
                    map.put("A","LONG");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WARNINGS))
            try {
                List<Map> rv = new LinkedList<Map>();
                String query = "select " + TablesTypes.DOR_CODE + ",PRED_ID,count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " group by PRED_ID," + TablesTypes.DOR_CODE;
                warnReq(rv, conn, query);
                for (Map map : rv)
                    map.put("A","REAL");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WARNINGS_NP))
            try {
                List<Map> rv = new LinkedList<Map>();
                String query = "select " + TablesTypes.DOR_CODE + ",PRED_ID,count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS_NP+" group by PRED_ID," + TablesTypes.DOR_CODE;
                warnReq(rv, conn, query);
                for (Map map : rv)
                    map.put("A","NP");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WINDOWS))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,count(*) CNT from "+TablesTypes.WINDOWS+" group by PRED_ID,"+TablesTypes.DOR_CODE;
                window(conn, rv, query);
                for (Map map : rv)
                    map.put("A","PLAN");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WINDOWS_CURR))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,count(*) CNT from "+TablesTypes.WINDOWS_CURR+" group by PRED_ID,"+TablesTypes.DOR_CODE;
                window(conn, rv, query);
                for (Map map : rv)
                    map.put("A","CURR");
                all.addAll(rv);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            if (hasTable(TablesTypes.WINDOWS_OVERTIME))
            try {
                List<Map> rv= new LinkedList<Map>();
                String query = "select "+TablesTypes.DOR_CODE+",PRED_ID,count(*) CNT from "+TablesTypes.WINDOWS_OVERTIME+" group by PRED_ID,"+TablesTypes.DOR_CODE;
                window(conn, rv, query);
                for (Map map : rv)
                    map.put("A","OVERTIME");
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


    protected boolean commonFilter(Map<String,Object> tuple,int dorKod,Object predId,String type)
    {
        if (dorKod==0)
        {
            String _predId=String.valueOf(predId);
            System.out.print("skip tuple for type = " + type+" dorKod:"+dorKod+" PRED_ID:"+_predId+" tuple:");
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
