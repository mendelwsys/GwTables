package com.mwlib.tablo.test.derby;


import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.10.14
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
public class CheckBusinessReq
{
    public static void main(String[] args) throws Exception
    {

        Map[] map=getTestData();

        System.out.println("map = " + map.length);


    }

    public static Map[] getTestData() throws Exception
    {
        Directory.initDictionary(true);


        Connection conn= DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);

        List<Map> all = new LinkedList<Map>();

        {
            List<Map> rv= new LinkedList<Map>();
            warnReq(rv, conn,"select DOR_KOD,PRED_NAME,PRED_ID,count(*) CNT,SUM(LEN) TLN from WARNINGS WHERE TIM_OTM =" +
//                    "TO_TIMESTAMP ('31-12-9999 00:00:00', 'DD-MM-RRRR HH24:MI:SS')" +
                    "TIMESTAMP('9999-12-31', '00:00:00') " +
                    " group by PRED_ID,PRED_NAME,DOR_KOD");
            for (Map map : rv)
                map.put("EVENT_TYPE","WRD");
            all.addAll(rv);

        }

        {
            List<Map> rv = new LinkedList<Map>();
            warnReq(rv, conn,"select DOR_KOD,PRED_NAME,PRED_ID,count(*) CNT,SUM(LEN) TLN from WARNINGS group by PRED_ID,PRED_NAME,DOR_KOD");
            for (Map map : rv)
                map.put("EVENT_TYPE","WR");
            all.addAll(rv);
        }



        {
            List<Map> rv= new LinkedList<Map>();
            String query = "select DOR_KOD,PRED_ID,count(*) CNT from WINDOWS group by PRED_ID,DOR_KOD";
            Window(conn, rv, query);
            for (Map map : rv)
                map.put("EVENT_TYPE","WIND");
            all.addAll(rv);
        }

        DbUtil.closeAll(null,null,conn, false);

        return all.toArray(new Map[all.size()]);
    }

    private static void Window(Connection conn, List<Map> rv,String query) throws SQLException {
        Statement stmt;ResultSet rs;
        //String query = ;
        stmt = conn.createStatement();

        rs = stmt.executeQuery(query);
        int i=0;
        while (rs.next())
        {
            Map<String,Object> tuple=new HashMap<String,Object>();
            int dor_kod = rs.getInt("DOR_KOD");
            tuple.put("DOR_KOD", dor_kod);
            Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
            tuple.put("DOR_NAME",byDorCode.getSNAME());
            int pred_id = rs.getInt("PRED_ID");
            tuple.put("PRED_ID", pred_id);
            Directory.Pred pred = Directory.getByPredId(pred_id);
            tuple.put("PRED_NAME",pred.getSNAME());

            Directory.Vid vid = Directory.getByVidCode(pred.getVD_ID());
            tuple.put("VID_NAME", vid.getSNAME());
            tuple.put("VID_ID", vid.getVD_ID());

            tuple.put("CNT",rs.getInt("CNT"));
            rv.add(tuple);
            i++;
        }
        System.out.println("i = " + i);

        DbUtil.closeAll(rs, stmt, null, false);
    }

    private static void warnReq(List<Map> rv, Connection conn,String query) throws SQLException {
        Statement stmt;
        ResultSet rs;
        {
            //String query = "select DOR_KOD,PRED_NAME,PRED_ID,count(*) CNT,SUM(LEN) TLN from WARNINGS group by PRED_ID,PRED_NAME,DOR_KOD";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            int i=0;
            while (rs.next())
            {

                Map<String,Object> tuple=new HashMap<String,Object>();
                int dor_kod = rs.getInt("DOR_KOD");
                tuple.put("DOR_KOD", dor_kod);
                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                tuple.put("DOR_NAME",byDorCode.getSNAME());


                tuple.put("PRED_NAME",rs.getString("PRED_NAME"));
                int pred_id = rs.getInt("PRED_ID");
                tuple.put("PRED_ID", pred_id);

                Directory.Pred pred = Directory.getByPredId(pred_id);
                Directory.Vid vid = Directory.getByVidCode(pred.getVD_ID());
                tuple.put("VID_NAME",vid.getSNAME());
                tuple.put("VID_ID", vid.getVD_ID());


                tuple.put("CNT",rs.getInt("CNT"));
                tuple.put("TLN", rs.getInt("TLN"));

                rv.add(tuple);
                i++;
            }
            System.out.println("i = " + i);
        }
        DbUtil.closeAll(rs, stmt, null, false);
    }
}

