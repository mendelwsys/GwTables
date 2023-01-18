package com.mwlib.tablo.servlets;

import com.mycompany.client.test.fbuilder.Aggregates;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.EventUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 10.03.15
 * Time: 11:54
 * To change this template use File | Settings | File Templates.
 */
public class FilterServlet  extends HttpServlet
{

    static Map<String,Map> function = new HashMap<String,Map>();
    static Set<String> functionFields =new HashSet<String>();
    static
    {
        functionFields.add("name");
        functionFields.add("filter");
        functionFields.add("aggregate");
        functionFields.add("colName");


        {
            HashMap e = new HashMap();
            e.put(TablesTypes.KEY_FNAME,1);
            e.put("name","Функт1");
            //e.put("filter", ""); //сохранять сюда полное JSON описание функций
            //e.put("colName", null);
            e.put("aggregate", Aggregates.COUNT);

            function.put("1", e);
        }

    }


    static Map<String,Map> filters = new HashMap<String,Map>();
    static Set<String> filterFields =new HashSet<String>();
    static int counter=2;
    static
    {
        filterFields.add("name");
        filterFields.add("table");
        filterFields.add("criteria");


        {
            HashMap e = new HashMap();
            e.put(TablesTypes.KEY_FNAME,1);
            e.put("name","Ф1");
            e.put("table", TablesTypes.WARNINGS);
            e.put("criteria", "{\n" +
                    "    \"_constructor\":\"AdvancedCriteria\", \n" +
                    "    \"operator\":\"and\", \n" +
                    "    \"criteria\":[\n" +
                    "        {\n" +
                    "            \"fieldName\":\"VPAS\", \n" +
                    "            \"operator\":\"lessThan\", \n" +
                    "            \"value\":20\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");
            filters.put("1", e);
        }

        {
            HashMap e = new HashMap();
            e.put(TablesTypes.KEY_FNAME,2);
            e.put("name","Ф2");
            e.put("table", TablesTypes.WINDOWS);
            e.put("criteria", "{\n" +
                    "    \"_constructor\":\"AdvancedCriteria\", \n" +
                    "    \"operator\":\"and\", \n" +
                    "    \"criteria\":[\n" +
                    "        {\n" +
                    "            \"fieldName\":\"o_serv\", \n" +
                    "            \"operator\":\"equals\", \n" +
                    "            \"value\":\"П\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");
            filters.put("2", e);
        }



    }

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


    protected void process(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String res=null;
        Map params=request.getParameterMap();

        String[] storage= (String[]) params.get("storage");

        Map<String, Map> function1;
        Set<String> filterFields1;

        if (storage==null || storage.length==0)
        {
            function1 = filters;
            filterFields1 = filterFields;
        }
        else
        {
            function1 = function;
            filterFields1 = functionFields;
        }

        if (params!=null && params.size()>0)
        {
            res = updateStorage(res, params, function1, filterFields1);
        }

        if (res ==null)
            res = EventUtils.toJson(function1.values());
        response.setContentType("application/json;charset=UTF-8");
        response.setContentLength(res.getBytes("UTF-8").length);

        PrintWriter writer = response.getWriter();
        writer.write(res);
        writer.flush();
    }

    private String updateStorage(String res, Map params, Map<String, Map> function1, Set<String> filterFields1) {
        String[] id = (String[]) params.get(TablesTypes.KEY_FNAME);//Update
        if (id!=null && id.length>0)
        {
            Map editFilter = function1.get(id[0]);
            if (editFilter==null)
            {
                int nextKey=++counter;
                id[0]=String.valueOf(nextKey);
                function1.put(id[0], editFilter = new HashMap());
                editFilter.put(TablesTypes.KEY_FNAME,nextKey);
                setFilter(params, editFilter, filterFields1);
                res = EventUtils.toJson(editFilter);
            }
            else
            {
                if (params.containsKey("remove"))
                {
                    Map map = function1.remove(id[0]);
                    res = EventUtils.toJson(map);
                }
                else
                    setFilter(params, editFilter, filterFields1);
            }
        }
        return res;
    }

    private void setFilter(Map params, Map editFilter, Set<String> filterFields) {
        for (Object key : params.keySet())
        {
            if (filterFields.contains(key))
            {
                String[] value = (String[]) params.get(key);
                if (value!=null && value.length>0)
                    editFilter.put(key, value[0]);
                else
                    editFilter.put(key, null);
            }
        }
    }
}
