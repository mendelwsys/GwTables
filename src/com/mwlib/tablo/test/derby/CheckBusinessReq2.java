package com.mwlib.tablo.test.derby;


import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.10.14
 * Time: 17:16
 *
 */
public class CheckBusinessReq2
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
            {
                map.put("EVENT_TYPE","WR");
                map.put("A","LONG");
            }
            all.addAll(rv);

        }

        {
            List<Map> rv = new LinkedList<Map>();
            warnReq(rv, conn,"select DOR_KOD,PRED_NAME,PRED_ID,count(*) CNT,SUM(LEN) TLN from WARNINGS group by PRED_ID,PRED_NAME,DOR_KOD");
            for (Map map : rv)
            {
                map.put("EVENT_TYPE","WR");
                map.put("A","REAL");
            }
            all.addAll(rv);
        }



        {
            List<Map> rv= new LinkedList<Map>();
            String query = "select DOR_KOD,PRED_ID,count(*) CNT from WINDOWS group by PRED_ID,DOR_KOD";
            Window(conn, rv, query);
            for (Map map : rv)
            {
                map.put("EVENT_TYPE","WIND");
                map.put("A","CURR");
            }
            all.addAll(rv);
        }

        DbUtil.closeAll(null,null,conn, false);

        fillTestData(all);


        return all.toArray(new Map[all.size()]);
    }

    private static void fillTestData(List<Map> all) {
        //Сделать много данных
        if (false)
        {

            Set preds_ID = new HashSet(Directory.predId2Pred.keySet());
            for (Map map : all)
            {
                Object key=map.get("PRED_ID");
                preds_ID.remove(key);
            }

            {
                Set iters = new HashSet(preds_ID);
                for (Object o : iters)
                {
                    Directory.Pred pred = Directory.predId2Pred.get(o);
                    Integer vd_id = pred.getVD_ID();
                    if (vd_id==null)
                    {
                        preds_ID.remove(o);
                        continue;
                    }
                    Directory.Vid vid = Directory.getByVidCode(vd_id);
                    if (
                            !(vid.getSNAME().startsWith("ШЧ") || vid.getSNAME().startsWith("ПЧ")
                            || vid.getSNAME().startsWith("ЭЧ") || vid.getSNAME().equalsIgnoreCase("П"))
                            )
                        preds_ID.remove(o);
                }
            }


            ArrayList arlst = new ArrayList(preds_ID);

            List<List<Map>> addTuples=new LinkedList<List<Map>>();
            Directory.dorCode2Rail.remove(0);
            Directory.dorCode2Rail.remove(1);
            Collection<Directory.RailRoad> codes = Directory.dorCode2Rail.values();

            int ixx=0;
            br:
            for (Directory.RailRoad code : codes)
            {
                List<Map> newTuples = new LinkedList<Map>();
                for (Map map : all)
                {
                    Map newTuple=new HashMap(map);
                    newTuple.put("DOR_KOD", code.getDOR_KOD());
                    newTuple.put("DOR_NAME", code.getSNAME());

                    if (arlst.size()==0)
                        break br;

                    int ix=(int)Math.round(Math.random()*arlst.size()*100);

                    ix=ix%arlst.size();
                    Object pred_ID=arlst.remove(ix);
                    Directory.Pred pred = Directory.predId2Pred.get(pred_ID);

                    newTuple.put("PRED_NAME", pred.getSNAME());
                    newTuple.put("PRED_ID", pred.getPRED_ID());

                    Directory.Vid vid = Directory.getByVidCode(pred.getVD_ID());
                    newTuple.put("VID_NAME", vid.getSNAME());
                    newTuple.put("VID_ID", vid.getVD_ID());
                    newTuples.add(newTuple);
               }
               addTuples.add(newTuples);
//               if (ixx>4)
//                  break;
                ixx++;

            }

            for (List<Map> addTuple : addTuples)
                all.addAll(addTuple);
        }
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

