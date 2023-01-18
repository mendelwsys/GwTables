package com.mwlib.tablo.derby.pred;

import com.mwlib.tablo.analit2.pred.NNodeXML;
import com.mwlib.tablo.cache.WrongParam;
import com.mwlib.tablo.db.*;
import com.mwlib.tablo.db.desc.*;
import com.mwlib.tablo.derby.DerbyUtils;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.GrpDef;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.UtilsData;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.cache.INm2Ix;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.ICliProvider;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.ICliProviderFactoryImpl;
import com.mwlib.tablo.analit2.NNodeBuilder;



import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.10.14
 * Time: 12:30
 * Загрузчик консолидационных таблиц дерби
 */
public class ConsolidatorLoader
{
    public static final String DEF_SESSIONID = "CONSOLIDATOR";

    //TODO Надо бы что бы этот список синхронизировался автоматом с провайдером кешей "исходных" данных
    public static final String[] loaderTypes =
    {       TablesTypes.DELAYS_ABVGD,TablesTypes.ZMTABLE,TablesTypes.KMOTABLE,
            TablesTypes.LOST_TRAIN,TablesTypes.VAGTOR,TablesTypes.VIP_GID,TablesTypes.DELAYS_GID,
            TablesTypes.WARNINGSINTIME,TablesTypes.TECH,TablesTypes.VIOLATIONS,TablesTypes.REFUSES,
            TablesTypes.WINDOWS,TablesTypes.WINDOWS_CURR,TablesTypes.WINDOWS_OVERTIME,
            TablesTypes.WARNINGS,TablesTypes.WARNINGS_NP
    };

//    public static final String[] loaderTypes ={
//            TablesTypes.DELAYS_ABVGD,TablesTypes.DELAYS_GID
//    };


    private Set<String> allTables = new HashSet<String>();

//    static
//    {
//        allTables.addAll(Arrays.asList(loaderTypes));
//    }



    public Map[] mapParms=new Map[loaderTypes.length];


    public static final String DERBYKEY = "KEY_DERBY00";
    public static final String DERBYUPDATE = "KEY_DERBY_UPD00";

    private int ixReq=0;

    private boolean test;

    public ConsolidatorLoader(boolean test)
    {
        this.test=test;
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

                String serv_char = rs.getString("SERV_CHAR");
                if (serv_char==null)
                    continue;

                int dor_kod = initDOR_KOD(rs, tuple);

                {

                    tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod)+"_"+serv_char);
                    tuple.put(TablesTypes.PRED_NAME, TablesTypes.Z_PREDVAL);


                    tuple.put(TablesTypes.VID_NAME, serv_char);
                    switch (serv_char)
                    {
                        case "П":
                            tuple.put(TablesTypes.VID_ID, 28);
                            break;
                        case "Ш":
                            tuple.put(TablesTypes.VID_ID, 70);
                            break;
                        case "Э":
                            tuple.put(TablesTypes.VID_ID, 40);
                            break;
                        case "В":
                            tuple.put(TablesTypes.VID_ID, 44);
                            break;
                        case "ДПМ":
                            tuple.put(TablesTypes.VID_ID, 6666);
                            break;
//                        case "В":
//                            tuple.put(TablesTypes.VID_ID, 700);
//                            break;
//                        case "Э":
//                            tuple.put(TablesTypes.VID_ID, 1400);
//                            break;
//                        case "П":
//                            tuple.put(TablesTypes.VID_ID, 800);
//                            break;
//                        case "Ш":
//                            tuple.put(TablesTypes.VID_ID, 900);
//                            break;
                        default:
                        {
                            tuple.put(TablesTypes.PRED_ID, String.valueOf(-dor_kod));
                            tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                            tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
                        }
                            break;
                    }
                }
                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));

                rv.add(tuple);
                i++;
            }
            System.out.println("VIP GID consolidation rows:" + i + " total:" + total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }

    private int initDOR_KOD(ResultSet rs, Map<String, Object> tuple) throws SQLException {
        int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
        tuple.put(TablesTypes.DOR_CODE, dor_kod);
        Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
        tuple.put("DOR_NAME",byDorCode.getSNAME());
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
                if (dor_kod==0)
                    continue;
                {
                    tuple.put("PRED_ID", String.valueOf(-dor_kod));
                    tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);
                    tuple.put("VID_NAME", TablesTypes.Z_SERVAL);
                    tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);
                }
                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));

                rv.add(tuple);
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
                    tuple.put("PRED_ID", String.valueOf(-dor_kod));
                    tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);
                    tuple.put("VID_NAME", TablesTypes.Z_SERVAL);
                    tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);
                }
                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));

                rv.add(tuple);
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

                tuple.put("VID_NAME",TablesTypes.Z_SERVAL);
                tuple.put("VID_ID", TablesTypes.Z_ID_SUM_SERVAL);

                tuple.put("PRED_ID", TablesTypes.HIDE_ATTR);
                tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);

                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));

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
                tuple.put("DOR_NAME",byDorCode.getSNAME());

                tuple.put("VID_NAME",TablesTypes.Z_SERVAL);
                tuple.put("VID_ID", TablesTypes.Z_ID_SUM_SERVAL);
//                tuple.put(TablesTypes.HIDE_ATTR,1);//Не надо показывать и вставлять в таблицу

                tuple.put("PRED_ID", -dor_kod+TablesTypes.HIDE_ATTR);
                tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);

                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));

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
                tuple.put("DOR_NAME",byDorCode.getSNAME());
//                tuple.put("SNM",rs.getString("SNM"));



                String serv=fillVIDByBidName(tuple,rs.getString("SNM"));
                if (serv!=null)
                    tuple.put("PRED_ID", String.valueOf(-dor_kod)+"_"+serv);
                else
                    tuple.put("PRED_ID", String.valueOf(-dor_kod));
                tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);

                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));

                rv.add(tuple);
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
                if (dor_kod==0)
                    continue;
                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                tuple.put("DOR_NAME",byDorCode.getSNAME());
                int pred_id = rs.getInt("PRED_ID");
                tuple.put("PRED_ID", pred_id);

                {
                    Directory.Pred pred = Directory.getByPredId(pred_id);
                    if (pred!=null)
                    {
                        tuple.put("PRED_NAME",pred.getSNAME());

                        fillVIDByPred(tuple, pred_id);
                    }
                    else
                    {
                        tuple.put("PRED_ID", String.valueOf(-dor_kod));
                        tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);
                        tuple.put("VID_NAME", TablesTypes.Z_SERVAL);
                        tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);

                    }
                    total+=rs.getInt("CNT");
                    tuple.put("CNT",rs.getInt("CNT"));
                }

                rv.add(tuple);
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
                tuple.put("DOR_NAME",byDorCode.getSNAME());
                int pred_id = rs.getInt("PRED_ID");
                tuple.put("PRED_ID", pred_id);

                {
                    Directory.Pred pred = Directory.getByPredId(pred_id);
                    if (pred!=null)
                    {
                        tuple.put("PRED_NAME",pred.getSNAME());
                        fillVIDByPred(tuple, pred_id);
                    }
                    else
                    {
                        tuple.put("PRED_ID", String.valueOf(-dor_kod));
                        tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);
                        tuple.put("VID_NAME", TablesTypes.Z_SERVAL);
                        tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);

                    }
                    total+=rs.getInt("CNT");
                    tuple.put("CNT",rs.getInt("CNT"));
                }

                rv.add(tuple);
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
                tuple.put("DOR_NAME","");

                tuple.put("PRED_ID", TablesTypes.HIDE_ATTR);
                tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);

                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));

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
                tuple.put("DOR_NAME",byDorCode.getSNAME());

//                tuple.put(TablesTypes.HIDE_ATTR,1);//Не надо показывать и вставлять в таблицу

                String serv=fillVIDByBidName(tuple,rs.getString("SNM"));

                tuple.put("PRED_ID", -dor_kod+TablesTypes.HIDE_ATTR+serv);
                tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);

                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));

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
                tuple.put("DOR_NAME",byDorCode.getSNAME());

                tuple.put("VID_NAME",TablesTypes.Z_SERVAL);
                tuple.put("VID_ID", TablesTypes.Z_ID_SUM_SERVAL);
//                tuple.put(TablesTypes.HIDE_ATTR,1);//Не надо показывать и вставлять в таблицу

                tuple.put("PRED_ID", -dor_kod+TablesTypes.HIDE_ATTR);
                tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);

                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));

                rv.add(tuple);
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
                tuple.put("DOR_NAME",byDorCode.getSNAME());
                int pred_id = rs.getInt("PRED_ID");
                tuple.put("PRED_ID", pred_id);

                {
                    Directory.Pred pred = Directory.getByPredId(pred_id);
                    String serv=fillVIDByHozId(tuple,rs.getInt("HOZ_ID"));
                    if (pred!=null)
                    {
                        tuple.put("PRED_NAME",pred.getSNAME());
                        tuple.put("PRED_ID", pred_id);
                        String servByPRed=fillVIDByPred(tuple,pred_id);
                        if ((servByPRed==null && servByPRed!=serv) || (servByPRed!=null && !servByPRed.equals(serv)))
                            System.out.println("conflicting definition of service for predId:"+pred_id+" name:"+pred.getSNAME()+" servByPRed = " + servByPRed+" servByHozId = "+serv);   //!!!TODO Обработать!!!!
                    }
                    else
                    {
                        if (serv!=null)
                            tuple.put("PRED_ID", String.valueOf(-dor_kod)+"_"+serv);
                        else
                            tuple.put("PRED_ID", String.valueOf(-dor_kod));
                        tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);
                    }

                    total+=rs.getInt("CNT");
                    tuple.put("CNT",rs.getInt("CNT"));
                }
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
                int pred_id = rs.getInt("PRED_ID");
                tuple.put("PRED_ID", pred_id);

                {
                    Directory.Pred pred = Directory.getByPredId(pred_id);
                    if (pred!=null)
                    {
                        tuple.put("PRED_NAME",pred.getSNAME());
                        fillVIDByPred(tuple, pred.getPRED_ID());
                    }
                    else
                    {
                        String serv=fillVIDByHozId(tuple,rs.getInt("HOZ_ID"));
                        if (serv!=null)
                            tuple.put("PRED_ID", String.valueOf(-dor_kod)+"_"+serv);
                        else
                            tuple.put("PRED_ID", String.valueOf(-dor_kod));
                        tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);

                    }
                    total+=rs.getInt("CNT");
                    tuple.put("CNT",rs.getInt("CNT"));
                }
                rv.add(tuple);
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
                int pred_id = rs.getInt("PRED_ID");
                tuple.put("PRED_ID", pred_id);

                {
                    Directory.Pred pred = Directory.getByPredId(pred_id);
                    if (pred!=null)
                    {
                        tuple.put("PRED_NAME",pred.getSNAME());

                        fillVIDByPred(tuple, pred_id);
                    }
                    else
                    {
                        String serv=fillVIDByHozId(tuple,rs.getInt("HOZ_ID"));
                        if (serv!=null)
                            tuple.put("PRED_ID", String.valueOf(-dor_kod)+"_"+serv);
                        else
                            tuple.put("PRED_ID", String.valueOf(-dor_kod));
                        tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);


                    }
                    total+=rs.getInt("CNT");
                    tuple.put("CNT",rs.getInt("CNT"));
                }
                rv.add(tuple);
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
                if (dor_kod==0)
                    continue;
                tuple.put(TablesTypes.DOR_CODE, dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                tuple.put("DOR_NAME",byDorCode.getSNAME());
                {
                    tuple.put("PRED_ID", String.valueOf(-dor_kod));
                    tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);
                    tuple.put("VID_NAME", TablesTypes.Z_SERVAL);
                    tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);
                }
                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));
                tuple.put("TLN", rs.getFloat("TLN"));

                rv.add(tuple);
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
                    tuple.put("PRED_ID", String.valueOf(-dor_kod));
                    tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);
                    tuple.put("VID_NAME", TablesTypes.Z_SERVAL);
                    tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);
                }
                total+=rs.getInt("CNT");
                tuple.put("CNT",rs.getInt("CNT"));

                rv.add(tuple);
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
            while (rs.next())
            {
                Map<String,Object> tuple=new HashMap<String,Object>();
                tuple.put("EVENT_TYPE",TablesTypes.WINDOWS);
                int dor_kod=initDOR_KOD(rs, tuple);

                int pred_id = rs.getInt("PRED_ID");

                Directory.Pred pred = Directory.getByPredId(pred_id);
                if (pred!=null)
                {
                    tuple.put("PRED_ID", pred_id);
                    tuple.put("PRED_NAME", pred.getSNAME());
                    fillVIDByPred(tuple, pred_id);
                }
                else
                {
                    tuple.put("PRED_ID", String.valueOf(-dor_kod));
                    tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);
                    tuple.put("VID_NAME",TablesTypes.Z_SERVAL);
                    tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);
                }


                tuple.put("CNT",rs.getInt("CNT"));


                rv.add(tuple);
                i++;
            }
            System.out.println("Windows consolidation rows:" + i);

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
            while (rs.next())
            {

                Map<String,Object> tuple=new HashMap<String,Object>();
                tuple.put("EVENT_TYPE",TablesTypes.WARNINGS);

                int dor_kod=initDOR_KOD(rs, tuple);

                int pred_id = rs.getInt("PRED_ID");
                Directory.Pred pred = Directory.getByPredId(pred_id);
                if (pred!=null)
                {
                    tuple.put("PRED_ID", pred_id);
                    tuple.put("PRED_NAME", pred.getSNAME());
                    fillVIDByPred(tuple, pred_id);
                }
                else
                {
                    tuple.put("PRED_ID", String.valueOf(-dor_kod));
                    tuple.put("PRED_NAME", TablesTypes.Z_PREDVAL);
                    tuple.put("VID_NAME",TablesTypes.Z_SERVAL);
                    tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);
                }

                tuple.put("CNT",rs.getInt("CNT"));
                tuple.put("TLN", rs.getFloat("TLN"));

                rv.add(tuple);
                i++;
            }
            System.out.println("Warning consolidation rows:" + i);
        }
        finally {
            DbUtil.closeAll(rs, stmt, null, false);
        }

    }

    private String fillVIDByHozId(Map<String, Object> tuple, int hoz_id)
    {
        String rv=null;
        switch (hoz_id)
        {
            case 150305:
                tuple.put("VID_NAME",rv="П");
                tuple.put("VID_ID", 28);
                break;
            case 150306:
                tuple.put("VID_NAME",rv="Ш");
                tuple.put("VID_ID", 70);
                break;
            case 150309:
                tuple.put("VID_NAME",rv="Э");
                tuple.put("VID_ID", 40);
                break;
            case 150304:
                tuple.put("VID_NAME",rv="В");
                tuple.put("VID_ID", 44);
                break;
            default:
            {
                tuple.put("VID_NAME",TablesTypes.Z_SERVAL);
                tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);
            }
        }
        return rv;
    }

    private String fillVIDByBidName(Map<String, Object> tuple, String  vid_name)
    {
        String rv=vid_name;
        tuple.put("VID_NAME",vid_name);
        switch (vid_name)
        {
            case "П":
                tuple.put("VID_ID", 28);
                break;
            case "Ш":
                tuple.put("VID_ID", 70);
                break;
            case "Э":
                tuple.put("VID_ID", 40);
                break;
            case "В":
                tuple.put("VID_ID", 44);
                break;
            case "ДПМ":
                tuple.put("VID_ID", 6666);
                break;
            default:
            {
                rv=null;
                tuple.put("VID_NAME",TablesTypes.Z_SERVAL);
                tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);
            }
        }
        return rv;

    }

    private String fillVIDByPred(Map<String, Object> tuple, int pred_id) {

        Directory.Pred pred = Directory.getByPredId(pred_id);
        Directory.UkGR vid = Directory.getByGrId(pred.getGR_ID());
        tuple.put("VID_ID", vid.getGR_ID());

        String rv=null;
        switch (vid.getGR_ID())
        {
            case 25:
            case 28:
                tuple.put("VID_NAME",rv="П");
                tuple.put("VID_ID", 28);
                break;
            case 70:
                tuple.put("VID_NAME",rv="Ш");
                break;
            case 40:
                tuple.put("VID_NAME",rv="Э");
                break;
            case 44:
                tuple.put("VID_NAME",rv="В");
                break;
            default:
                tuple.put("VID_NAME",TablesTypes.Z_SERVAL);
                tuple.put("VID_ID", TablesTypes.Z_ID_SERVAL);
        }

        return rv;

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
                String query = "select "+TablesTypes.DOR_CODE+",SERV_CHAR,count(*) CNT from "+TablesTypes.VIP_GID+" group by "+TablesTypes.DOR_CODE+",SERV_CHAR";
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
                String query = "select " + TablesTypes.DOR_CODE + ",PRED_ID,count(*) CNT,SUM(LEN)/1000.0 TLN from " + TablesTypes.WARNINGS + " WHERE TIM_OTM = TIMESTAMP('9999-12-31', '00:00:00') " +
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

    private static Connection getDerbyConnection() throws Exception {
        return DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
    }


    public static boolean  tWasDelete=false;

    protected boolean loading() throws Exception
    {
        Connection conn= null;



        try {
            conn= getDerbyConnection();
            conn.setAutoCommit(false);
            boolean rv =false;
            try
            {
                ICliProviderFactory providerFactoryInstance = ICliProviderFactoryImpl.getProviderFactoryInstance();
                providerFactoryInstance.addNotSessionCliIds(DEF_SESSIONID);

                for (int i = 0, mapParmsLength = mapParms.length; i < mapParmsLength; i++)
                {
                    boolean truncate =false;
                    if (mapParms[i]==null)
                    {
                        mapParms[i] = new HashMap();
                        mapParms[i].put(ICliProviderFactory.CLIID, new String[]{DEF_SESSIONID});
                        mapParms[i].put(AppContext.APPCONTEXT, new AppContext[]{new AppContext()});
                        mapParms[i].put(TablesTypes.TTYPE, new String[]{loaderTypes[i]});
                        mapParms[i].put("CLICNT",1000);
                        truncate = true;
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


                    if (providers==null ||providers.length==0)
                        continue;

                    ICliProvider provider=providers[0];


                    int cliCnt=(Integer)mapParms[i].get("CLICNT");
                    int currCnt = provider.getCliCnt();
                    mapParms[i].put("CLICNT",currCnt);
                    truncate=(currCnt -cliCnt)<0 || truncate;

                    ixReq++;

                    mapParms[i].put(TablesTypes.TBLID,new String[]{provider.getTblId()});
                    boolean wascreate=createDerbyTable(loaderTypes[i], provider);//Создаем таблицу если она еще не создана

                    Map<Object, long[]> newKeys = provider.getNewDataKeys(mapParms[i]).dataRef;
                    rv = rv||(newKeys.size()>0);

                    if (!wascreate && truncate)
                    { //Сброс таблицы
                        truncateTable(loaderTypes[i]);
                        conn.commit();
                    }

                    if (wascreate || truncate)
                    { //Заполнение инсертом
                        insertAll(provider, newKeys, loaderTypes[i]);
                    }
                    else
                    {

                       getUpdateQuery3(provider,newKeys,loaderTypes[i]);



    //                //Делаем апдейт таблицы // Есть проблема с производительностью при апдейте, сделал batchMode
    //                    Statement stmt = null;
    //                    try
    //                    {
    //                        if (newKeys.size()>0)
    //                        {
    //                            stmt = conn.createStatement();
    //
    //                            Set ukeys=new HashSet(newKeys.keySet());
    //                            for (Object key : ukeys)
    //                            {
    //                               String updateQ=getUpdateQuery(provider,key,newKeys.get(key),loaderTypes[i]);
    //                                int res=0;
    //                                if (updateQ!=null)
    //                                {
    //                                    res = stmt.executeUpdate(updateQ);
    //                                }
    //
    //                                if (res>0 || updateQ==null)
    //                                    newKeys.remove(key);
    //                            }
    //
    //                            insertAll(provider, newKeys, loaderTypes[i]);
    //                            conn.commit();
    //                        }
    //                    }
    //                    finally
    //                    {
    //                        DbUtil.closeAll(null, stmt, null);
    //                    }
                    }
                }
                conn.commit();
                if (rv)
                    transactionNumber++;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw e;
            }
            finally
            {
                if (conn!=null && !conn.isClosed())
                    conn.setAutoCommit(true);
            }
            return rv;
        }
        finally
        {
            DbUtil.closeAll(null,null,conn,true);
        }
    }

    private void truncateTable(String loaderType) throws Exception
    {
        runSimpleQuery("TRUNCATE TABLE "+loaderType);
    }


    private String getUpdateQuery(ICliProvider provider, Object key,long[] lg, String loaderType) throws Exception
    {
        ColumnHeadBean[] cols = provider.getMeta();

        StringBuffer updateSQL=new StringBuffer("update  "+loaderType+" SET ");

        StringBuilder vals = new StringBuilder();
        Object[] oVals=provider.getTupleByKey(key);

        if (oVals==null || lg==null)
        {
            if (oVals!=null)
                System.out.println("Error in state of prvider ? tuple in provider not null but request it for delete key is "+ key);

            return "DELETE FROM "+loaderType+" WHERE "+DERBYKEY+"='"+key.toString()+"'";
        }


        for (int i = 0; i < oVals.length; i++)
        {
            vals.append(cols[i].getName()).append("=");
            Object val = oVals[i];
            if (val instanceof String)
                vals.append("'").append(val).append("',");
            else if (val instanceof Timestamp)
                vals.append("'").append(val).append("',");
            else
                vals.append(val).append(",");
        }
        updateSQL= updateSQL.append(vals.substring(0, vals.length() - 1)).append(" WHERE ").append(DERBYKEY).append("=").append("'").append(key).append("'");
        return updateSQL.toString();
    }


    private void getUpdateQuery3(ICliProvider provider, Map<Object, long[]> newKeys, String loaderType) throws Exception
    {
        if (newKeys.size()!=0)
        {
            System.out.println(" Keys size:"+newKeys.size()+" for table "+loaderType);
            long ln=System.currentTimeMillis();

            deleteAll(newKeys.keySet(),loaderType);
            insertAll(provider,newKeys,loaderType);

            System.out.println("Updated tuples for table "+loaderType+" tm= " + (System.currentTimeMillis()-ln)/1000+" keys:"+(newKeys.size()));
        }


    }

    //Все равно при больших объемах данных очень долго опдейтит (в частности больше 400 уже становится не приемелемым, так что отработать механизм сброса обязателно!!!!)
    private void getUpdateQuery2(ICliProvider provider, Map<Object, long[]> newKeys, String loaderType) throws Exception
    {
        if (newKeys.size()!=0)
        {
            ColumnHeadBean[] cols = provider.getMeta();
            StringBuilder updateSQL=new StringBuilder("update  "+loaderType+" SET ");

            for (ColumnHeadBean col : cols)
            {
                updateSQL = updateSQL.append(col.getName()).append("= ?,");
            }

            updateSQL= updateSQL.replace(updateSQL.length() - 1,updateSQL.length()," ").append(" WHERE ").append(DERBYKEY).append("= ?");

            System.out.println(" Keys size:"+newKeys.size()+" for table "+loaderType);
            long ln=System.currentTimeMillis();

            Set<Object> key4Delete=new HashSet<Object>();
            {
                PreparedStatement stmt=null;
                Connection conn= null;
                try
                {
                    conn= getDerbyConnection();
                    stmt = conn.prepareStatement(updateSQL.toString());
                    for (Object key : newKeys.keySet())
                    {
                        Object[] ovals = provider.getTupleByKey(key);
                        if (ovals == null)
                        {
                            //TODO Пишем в лог, такое может быть если запись появилась и быстро удалилась
                            key4Delete.add(key);
                            continue;
                        }

                        for (int i1 = 0, ovalsLength = ovals.length; i1 < ovalsLength; i1++)
                            stmt.setObject(i1+1,ovals[i1]);
                        stmt.setObject(ovals.length+1,key);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                    conn.commit();
                }
                finally
                {
                    DbUtil.closeAll(null, stmt, conn, true);
                }
            }
            System.out.println("Updated tuples for table "+loaderType+" tm= " + (System.currentTimeMillis()-ln)/1000+" keys:"+(newKeys.size()-key4Delete.size()));

            deleteAll(key4Delete, loaderType);
        }
    }



    private void deleteAll(Set<Object> key4Delete, String loaderType) throws Exception {
        PreparedStatement stmt=null;
        Connection conn= null;
        try
        {
            conn= getDerbyConnection();
            stmt = conn.prepareStatement("DELETE FROM "+loaderType+" WHERE "+DERBYKEY+"=?");
            for (Object key : key4Delete)
            {
                stmt.setObject(1,key);
                stmt.addBatch();

            }
            stmt.executeBatch();
            conn.commit();
        }
        finally
        {
            DbUtil.closeAll(null, stmt, conn, true);
        }
    }


    long transactionNumber=0;
    private void insertAll(ICliProvider provider, Map<Object, long[]> newKeys, String loaderType) throws Exception
    {
        if (newKeys.size()!=0)
        {
                ColumnHeadBean[] cols = provider.getMeta();
                //1.Создать запрос на загрузку таблицы
                StringBuilder itbl = new StringBuilder(loaderType).append(" ( ");
                StringBuilder valStr = new StringBuilder("(");
                for (ColumnHeadBean col : cols)
                {
                    itbl = itbl.append(col.getName()).append(" ,");
                    valStr=valStr.append("?,");

                }
                itbl = itbl.append(DERBYUPDATE).append(" ,");
                itbl = itbl.append(DERBYKEY).append(" )  values ");
                valStr=valStr.append("?,? )");
                StringBuilder insertSQL = new StringBuilder("insert into ").append(itbl).append(valStr);

                PreparedStatement stmt=null;
                Connection conn= null;
                try
                {
                    conn= getDerbyConnection();
                    stmt = conn.prepareStatement(insertSQL.toString());
                    for (Object key : newKeys.keySet())
                    {
                        Object[] ovals = provider.getTupleByKey(key);
                        if (ovals == null || newKeys.get(key)==null)
                        {
                            //TODO Пишем в лог, такое может быть если запись появилась и быстро удалилась
                            System.out.println("inserting:tuple was delete with key:" + key);
                            tWasDelete=true;
                            continue;
                        }

                        for (int i1 = 0, ovalsLength = ovals.length; i1 < ovalsLength; i1++)
                            stmt.setObject(i1+1,ovals[i1]);
                        stmt.setLong(ovals.length+1,transactionNumber);
                        stmt.setObject(ovals.length+2,key);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                    conn.commit();
                }
                finally
                {

                    DbUtil.closeAll(null, stmt, conn, true);
                }
        }
    }

    private void runSimpleQuery(String query) throws Exception {
        Statement stmt=null;
        Connection conn= null;
        try {
            conn= getDerbyConnection();
            stmt=conn.createStatement();
            stmt.execute(query);
        }
        finally
        {
            DbUtil.closeAll(null, stmt, conn, true);
        }
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

    private boolean createDerbyTable(String tblName, ICliProvider provider) throws Exception
    {
        Connection conn=null;
        Statement stmt=null;
        ResultSet rs = null;
        boolean rv=false;

        try {
            conn= getDerbyConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            rs = dbmd.getTables(null, null, tblName, null);

            if(!rs.next())
            {

                Map<String,ColumnHeadBean> nColNames=new HashMap<String,ColumnHeadBean>();
                ColumnHeadBean[] cols = provider.getMeta();
                for (ColumnHeadBean col : cols)
                    nColNames.put(col.getName(),col);//TODO а возможно все таки по заголовку?

                //Создаем сначала таблицу
                //1.Создать запрос на создание
                StringBuffer tbl=new StringBuffer(tblName+" ( ");
                for (String colNames : nColNames.keySet())
                        tbl = tbl.append(colNames).append(" ").append(DerbyUtils.translate2DerbyType(nColNames.get(colNames).getType())).append(" ,");

                tbl = tbl.append(DERBYUPDATE).append(" BIGINT ,");

                tbl = tbl.append(DERBYKEY).append(" VARCHAR (255) "+", PRIMARY KEY ("+DERBYKEY+")"+"  )");



                String createTbl="create table "+tbl.toString();
                stmt = conn.createStatement();
                stmt.execute(createTbl);

                //stmt.execute("CREATE INDEX "+tblName+"_IX ON "+tblName+" ("+DERBYUPDATE+")");

                rv=true;
            }
        }
        finally
        {
            DbUtil.closeAll(rs,stmt,conn,true);
        }
        return rv;
    }


    public Map [] getData() throws Exception
    {
        tWasDelete=false;

        boolean isConsolidate=test;
        Map [] rv =new Map[0];
        if (!test)
        {
            long lg= System.currentTimeMillis();
            isConsolidate=loading();
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
        System.out.println(message + 1.0*((int)(sec))/100);
    }


    public static void __main(String[] args) throws Exception

    {

        String[] str=new String[]{
        "76;1552844",
        "76;1548227",
                "76;1553086",
                "76;1550591",
                "76;1548472",
                "76;1552532",
                "76;1552591"};

        //new ConsolidatorLoader(true).fillTables();
        for (int i=0;i<str.length;i++

                ) {
            Statement stmt=null;
            Connection conn= null;
            ResultSet rs=null;
            try {
                conn= getDerbyConnection();
                stmt=conn.createStatement();



                rs=stmt.executeQuery("SELECT * from "+TablesTypes.WARNINGS_NP+" WHERE "+DERBYKEY+"='"+str[i]+"##79'");
                while (rs.next())
                {
                    rs.getString(DERBYKEY);
                }
            }
            finally
            {
                DbUtil.closeAll(rs, stmt, conn, true);
            }
        }

    }

    public static void _main(String[] args) throws Exception
    {
        boolean test=false;
        ConsolidatorLoader loader = new ConsolidatorLoader(test);
        Map[] data = loader.consolidateData();


        IAnalisysDesc desc=new NNodeBuilder().xml2Desc(NNodeXML.xml);
        UtilsData.removeEmptyNodes(desc.getNodes());


        Map<String, Integer> key2Number;
        UtilsData.getKey2key2Number(desc.getNodes(), "", key2Number = new HashMap<String, Integer>(), 0);



        Map<Object,Map<String,Object> > key2Tuple=new HashMap<Object,Map<String,Object>>();

        IKeyGenerator generator = new SimpleKeyGenerator(new String[]{"PRED_ID"},new INm2Ix()
        {
            @Override
            public Map<String, Integer> getColName2Ix()
            {
                Map<String, Integer> rv = new HashMap<String, Integer>();
                rv.put("PRED_ID",0);
                return rv;
            }

            @Override
            public Map<Integer, String> getIx2ColName() {
                Map<Integer,String> rv = new HashMap<Integer,String>();
                rv.put(0,"PRED_ID");
                return rv;
            }
        });

        GrpDef[] keyDef = desc.getGrpXHierarchy();

        for (Map tuple : data)
        {
            Object key = generator.getKeyByTuple(tuple);
            Map<String,Object> resMap=key2Tuple.get(key);
            if (resMap==null)
            {
                key2Tuple.put(key,resMap=new HashMap<String,Object>());
                for (GrpDef grpDef : keyDef)
                {//Инициализация ключевых полей показателей
                    String tid=grpDef.getTid();
                    resMap.put(tid,tuple.get(tid));
                    String head=grpDef.gettColId();
                    if (!tid.equals(head))
                        resMap.put(head,tuple.get(head));
                }
                resMap.put(TablesTypes.KEY_FNAME,key);//TODO !!!!подпорка!!!!
                resMap.put(TablesTypes.DATATYPE_ID,-100);//TODO !!!!подпорка!!!!
            }
            UtilsData.conVertMapByNode("",desc.getNodes(),tuple,resMap,key2Number, new LinkedList<String>());
        }
    }


    public static void main(String[] args) throws Exception
    {
        boolean test=false;

        Directory.initDictionary(test);


        if (!test)
        {
            Type2NameMapperAuto mapper = new Type2NameMapperAuto(new BaseTableDesc[]{
                    new DelayABGDDesc(test),new ZMDesc(test),new KMODesc(test),
                    new LostTrDesc(test),new VagInTORDesc(test),new VIPGidDesc(test),new DelayGIDTDesc(test),
                    new WarningInTimeDesc(test),new TechDesc(test),new ViolationDesc(test),
                    new RefuseDesc(test),new WindowsDesc(test),
                    new WindowsDesc(test,TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                    new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),
                    new WarningDesc(test),new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})
            });

            EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
            EventProviderTImpl eventProvider = new EventProviderTImpl(metaProvider);
            //eventProvider.addDorKod(new int[]{28,1,80});
            ServerUpdaterT updater = new ServerUpdaterT(eventProvider, ICliProviderFactoryImpl.getCliManagerInstance());
            new Thread(updater).start();//запуск апдейтера кэшей данных.//TODO !!!СРОЧНО!!!!, если по типу нет ни одного события возникает ошибка при опросе обновлений!!!!!!
        }

        ConsolidatorLoader consolidatorLoader = new ConsolidatorLoader(test);
        for (;;)
        {
            long lg1= System.currentTimeMillis();
            Map[] data=consolidatorLoader.getData();
            long mills = System.currentTimeMillis() - lg1;
            printDelayTime(mills, "main: data = " + data.length + " total time :");
            Thread.sleep(Math.max(100,3000));
        }
    }

//    private StringBuilder getKeyClauseBuilder(ICliProvider provider, Map<String, Integer> mp, Object[] oVals) {
//        StringBuilder vals;
//        String[] keyNames=provider.getKeyCols();
//        vals = new StringBuilder();
//        for (int i = 0; i < keyNames.length; i++)
//        {
//            String keyName = keyNames[i];
//
//            int ix=mp.get(keyNames[i]);
//            vals.append(keyName).append(" = ");
//            Object val = oVals[ix];
//            if (val instanceof String)
//                vals.append("'").append(val).append("'");
//            else if (val instanceof Timestamp)
//                vals.append("'").append(val).append("'");
//            else
//                vals.append(val);
//            if (i<keyNames.length-1)
//                vals.append(" AND ");
//        }
//        return vals;
//    }

}
