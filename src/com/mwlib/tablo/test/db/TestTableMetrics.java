package com.mwlib.tablo.test.db;

import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.derby.DerbyTableOperations;

import java.sql.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 19.11.14
 * Time: 14:09
 * Пример теста для получения метрик таблиц
 */
public class TestTableMetrics
{
    public static void main(String[] args) throws Exception
    {

        DerbyTableOperations.ITableFilter tableFilter = new DerbyTableOperations.ITableFilter() {

            @Override
            public boolean isFit(ResultSet rs) throws Exception {
                String tblName = rs.getString(DerbyTableOperations.TABLE_NAME_COL);
                return (tblName != null && !tblName.endsWith(DerbyTableOperations.META_TABLE_EXT)
                        &&
                        !tblName.startsWith(TablesTypes.PLACES)
                        &&
                        !tblName.startsWith("POLG")
                        &&
                        !tblName.startsWith("TABLO_POLG_LIST")
                );
            }
        };


        DerbyTableOperations derbyTableOperations = DerbyTableOperations.getDerbyTableOperations(DbUtil.DS_JAVA_CACHE_H_NAME);

        printMetrics(tableFilter, derbyTableOperations);

        long lg=10*TablesTypes.DAY_MILS;
        Timestamp currTimeStamp = new Timestamp(System.currentTimeMillis()-lg);

        String[] tblNames = derbyTableOperations.getTablesbyFilter(tableFilter);
        for (String tblName : tblNames)
        {
            try {
                derbyTableOperations.deleteNotActualByCorTime(tblName,currTimeStamp);
            } catch (Exception e)
            {
                System.out.println("delete error on tblName = " + tblName+" e"+ e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("==========================================================================================");
        printMetrics(tableFilter, derbyTableOperations);

    }

    private static void printMetrics(DerbyTableOperations.ITableFilter tableFilter, DerbyTableOperations derbyTableOperations) throws Exception {
        Map[] res = derbyTableOperations.getMetricsDerbyTables(
                tableFilter);
        for (Map re : res)
            System.out.println(re.get(DerbyTableOperations.TABLE_NAME_COL)+" count="+re.get(DerbyTableOperations.COUNT_REC_COL));
    }
}
