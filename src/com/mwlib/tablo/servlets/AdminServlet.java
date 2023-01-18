package com.mwlib.tablo.servlets;

import com.mwlib.tablo.EventUtils;
import com.mwlib.tablo.HttpSessionCollector;
import com.mwlib.tablo.derby.DerbyTableOperations;
import com.mwlib.tablo.tables.AdminInfo;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.TablesTypes;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.09.14
 * Time: 12:37
 * Сервлет для получения данных о состоянии системы
 */
public class AdminServlet extends HttpServlet {


    public static final String DERBYNAME = "DERBYNAME";
    public static final String CORDAYS = "CORDAYS";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            process(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            process(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    DerbyTableOperations.ITableFilter tableFilter=new DerbyTableOperations.ITableFilter()
            {

                @Override
                public boolean isFit(ResultSet rs) throws Exception
                {
                    String tblName=rs.getString(DerbyTableOperations.TABLE_NAME_COL);
                    return (
                            tblName!=null &&
                            !tblName.endsWith(DerbyTableOperations.META_TABLE_EXT)
                            &&
                            !tblName.startsWith(TablesTypes.PLACES)
                            &&
                            !tblName.startsWith("POLG")
                            &&
                            !tblName.startsWith("TABLO_POLG_LIST")
                    );
                }
            };


    protected void process(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Map map = request.getParameterMap();

        String dbName=getParameter(map,DERBYNAME);
        if (dbName==null)
               dbName=DbUtil.DS_JAVA_CACHE_H_NAME;


        Integer corDays=null;
        try {
            String _corDays=getParameter(map, CORDAYS);
            if (_corDays!=null)
                corDays=Integer.parseInt(_corDays);
        }
        catch (NumberFormatException e)
        {

        }

        AdminInfo adminInfo=new AdminInfo();
        adminInfo.setSessions(HttpSessionCollector.getCountSessions());

        DerbyTableOperations derbyTableOperations = DerbyTableOperations.getDerbyTableOperations(dbName);
        if (corDays!=null)
        {
            long lg=corDays.longValue()*TablesTypes.DAY_MILS;
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

        }


        Map[] tables= derbyTableOperations.getMetricsDerbyTables(tableFilter);
        adminInfo.setTables(tables);

        String res = EventUtils.toJson(adminInfo);
        response.setContentType("application/json;charset=UTF-8");
        response.setContentLength(res.getBytes("UTF-8").length);

        PrintWriter writer = response.getWriter();
        writer.write(res);
        writer.flush();

    }

    private String getParameter(Map parameters, String paramName) {
        String[] arCliId=(String[])parameters.get(paramName);
        String cliId=null;
        if (arCliId!=null && arCliId.length==1)
            cliId=arCliId[0];
        return cliId;
    }


}
