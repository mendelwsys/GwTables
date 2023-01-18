package com.mwlib.tablo.test.derby;

import com.mwlib.utils.db.DbUtil;

import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 17.10.14
 * Time: 12:26
 * Проверка раунд трип по группировочным  функциям для
 */
public class CheckDerbyFunctions
{

    //private static String dbURL="jdbc:derby:memory:myDB;create=true";


    private static Connection conn = null;



    public void testBaseQuery()  throws Exception
    {
        Connection conn= DbUtil.getConnection2(DbUtil.DS_JAVA_NAME);
        Statement stmt = conn.createStatement();
        String query = null;
        try {
            query = "drop table t";
            stmt.execute(query);
            query ="drop function f";
            stmt.execute(query);
        } catch (SQLException e) {
            //
        }
        query ="create table t( a int, b int )";
        stmt.execute(query);
        query ="create table tbl ( PRED_NAME VARCHAR(12) ,LEN int ,V int ,PRED_ID int ,PRICH_V_ID int  )";
        stmt.execute(query);

        query="insert into tbl ( PRED_NAME ,LEN ,V ,PRED_ID ,PRICH_V_ID  )  values  ('ПЧ-13',500,25,1918,130 ), ('ПЧ-13',800,60,1143,131 ), ('ПЧ-13',1200,60,793,106 ), ('ПЧ-13',100,50,1143,1103 ), ('ПЧ-13',1300,60,1143,1066 )";
        stmt.execute(query);

        query ="insert into t(a, b) values ( 1, 0 ), ( -1, 1 ), ( -2, 2 )";
        stmt.execute(query);
        query ="create function f\n" +
                "(\n" +
                "raw int\n" +
                ")\n" +
                "returns int\n" +
                "language java\n" +
                "parameter style java\n" +
                "deterministic\n" +
                "no sql\n" +
                "external name 'java.lang.Math.abs'";
        stmt.execute(query);

        query ="select fa, count(b) as cb from (select f(a) as fa, b from t) as t0 group by fa";

        ResultSet rs = stmt.executeQuery(query);
        while(rs.next())
        {
            int fa=rs.getInt("fa");
            int cb=rs.getInt("cb");
            System.out.println(" " + fa+" "+" "+cb);
        }

       conn.close();
    }
}
