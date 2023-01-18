package com.mwlib.tablo.derby.rsm;

import com.mwlib.tablo.derby.IDataLoader;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.TablesTypes;

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
public class ConsolidatorRSM implements IDataLoader
{
    public static final String DEF_SESSIONID = "CONSOLIDATOR_RSM";


    private int ixReq=0;

    private boolean test;
    private String dsName;

    public ConsolidatorRSM(String dsName, boolean test)
    {
        this.test=test;
        this.dsName=dsName;
    }

    private ConsolidatorRSM()
    {
    }


    private void execReq(Connection conn,List<Map> rv,String query,String eventType) throws SQLException
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
                tuple.put("CNT",rs.getInt("CNT"));
                tuple.put("STATE_ID",rs.getInt("STATE_ID"));
                tuple.put("NPP",rs.getInt("NPP"));
                String name = rs.getString("NAME");

                if (name!=null)
                    name=name.trim();
                tuple.put("NAME", name);

//                for (String addKey : add2tuple.keySet())
//                    tuple.put(addKey,add2tuple.get(addKey));

                total+=rs.getInt("CNT");
                rv.add(tuple);
                i++;
            }
            System.out.println("RSM consolidation rows:" + i + " total:" + total);

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


            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String stringDate=sdf.format(dt);

            Date dtP = new Date(dt.getTime()-TablesTypes.DAY_MILS);
            String stringDateP=sdf.format(dtP);


            List<Map> all = new LinkedList<Map>();
            conn=getConnection();
            try
            {
//                final String query = "select count( distinct PRSM_ID) as CNT  from ICG0.RSM_DATA where  DATE_D >= TO_TIMESTAMP('30.08.2015 00:00','DD.MM.YYYY HH24:MI')\n" +
//                        " and DATE_D < TO_TIMESTAMP('31.08.2015 00:00','DD.MM.YYYY HH24:MI')\n" +
//                        " and STATUS_ID in (10,11,12,13,14,21)";

                String query="select count( distinct s1.PRSM_ID) as CNT,st.STATE_ID,st.NPP,st.NAME  from ICG0.RSM_STATUS stu LEFT JOIN\n" +
                        "(select * from ICG0.RSM_DATA dt where dt.DATE_D >= TO_TIMESTAMP('"+stringDateP+" 00:00','DD.MM.YYYY HH24:MI')\n" +
                        "  and \n" +
                        "  dt.DATE_D < TO_TIMESTAMP('"+stringDate+" 00:00','DD.MM.YYYY HH24:MI')) s1 \n" +
                        "  on stu.STATUS_ID=s1.STATUS_ID ,ICG0.RSM_STATE st\n" +
                        "  where  \n" +
                        "    st.STATE_ID=stu.STATE_ID \n" +
                        " group by st.NAME,st.NPP,st.STATE_ID order by st.NPP";

                execReq(conn,all, query,TablesTypes.RSM_DATA);
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


    private Connection getConnection() throws Exception {

        if (dsName==null)
            dsName=DbUtil.DS_ORA_NAME;

        return DbUtil.getConnection2(dsName);
    }


    public Map [] getData() throws Exception
    {
//        boolean isConsolidate=test;
        Map [] rv =new Map[0];
        {
            long lg= System.currentTimeMillis();
//            if (isConsolidate)
             rv=consolidateData();
            long mills = System.currentTimeMillis() - lg;
            printDelayTime(mills,"Consolidate Events :");
        }
        return rv;
    }


    public static void printDelayTime(long mills,String message) {
        double sec = (1.0 * mills) / 10;
        System.out.println(message + 1.0*((int)(sec))/100+" sec.");
    }

    public static void main(String[] args) throws Exception
    {
        new ConsolidatorRSM().consolidateData();
    }

}
