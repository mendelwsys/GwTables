package com.mwlib.tablo.servlets.places;

import com.mwlib.tablo.analit2.places.PlacesEventProviderTImpl;
import com.mwlib.tablo.cache.DefaultCacheFactory;
import com.mwlib.tablo.servlets.ServletUtils;
import com.mwlib.tablo.tables.DataSender4;
import com.mwlib.tablo.tables.TblUtils;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.EventUtils;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.ICliProviderFactoryImpl;
import com.mwlib.tablo.db.ITypes2NameMapper;
import com.mwlib.tablo.db.ServerUpdaterT2;
import com.mwlib.tablo.db.TableDescSwitcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.09.14
 * Time: 12:37
 * Сервлет подкачики данных в таблицы клиентов для сводной таблицы по местам событий
 * TODO Имеет смысл подумать о обобщении всех сводных таблиц через единый сервлет, или хотя бы обощении классов реализующих данную функциональность
 */
public class DataServletPlacesCons extends HttpServlet {

    private ServerUpdaterT2 updater;

    public void init() throws ServletException {


        boolean test=false;
        try
        {
            test = ServletUtils.setTestByParams(getServletConfig().getInitParameter("test"), test);
            String dsName = ServletUtils.setName(getServletConfig().getInitParameter(TablesTypes.DS_NAME),DbUtil.DS_JAVA_CACHE_NAME);
            String dsOraName = ServletUtils.setName(getServletConfig().getInitParameter(TablesTypes.DS_ORA_NAME),DbUtil.DS_ORA_NAME);


            ITypes2NameMapper mapper = TableDescSwitcher.getInstance().getMapper();


            final String[] loaderTypes = mapper.getNames();
            List<String> newLoaderTypes = new LinkedList<String>();
            for (String loaderType : loaderTypes)
            {
                if (loaderType.equals(TablesTypes.PLACES) || loaderType.equals(TablesTypes.PLACEPOLG))
                    newLoaderTypes.add(loaderType);
                else
                {
                    newLoaderTypes.add(loaderType);
                    newLoaderTypes.add(loaderType+"_"+TablesTypes.PLACES);
                }
            }

            updater = new ServerUpdaterT2(PlacesEventProviderTImpl.getConsolidateProvider(dsName,dsOraName, newLoaderTypes.toArray(new String[newLoaderTypes.size()]), test), ICliProviderFactoryImpl.getCliManagerInstance(),new DefaultCacheFactory());
            new Thread(updater).start();//запуск апдейтера кэшей данных.
            System.out.println("DataServletDCons was startted in  mode test=" + test);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    public void destroy()
    {

        if (updater != null)
            updater.setTerminate();
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

    static int cntI=0;

    protected void process(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        HttpSession session = request.getSession();

        AppContext ctx=(AppContext)session.getAttribute(AppContext.APPCONTEXT);

        if (ctx==null)
            session.setAttribute(AppContext.APPCONTEXT,ctx=new AppContext());

        String sessionId = session.getId();

        Map map = request.getParameterMap();
        map = new HashMap(map);

        map.put(ICliProviderFactory.CLIID, new String[]{sessionId});
        map.put(AppContext.APPCONTEXT, new AppContext[]{ctx});

        DataSender4 dataSender4 = TblUtils.createDataSender(map);

//        DataSender2[] sender = new DataSender2[]{dataSender2};
//        String res = EventUtils.toJson2(sender);

        String res = EventUtils.toJson2(dataSender4);
//        System.out.println("size2 is = " + res.length()/1024+" K");

        response.setContentType("application/json;charset=UTF-8");
        int length = res.getBytes("UTF-8").length;
        response.setContentLength(length);


//        System.out.println(cntI+"  BEGIN  ================================= ");
//        System.out.println("res = " + res+" "+length);
//        System.out.println(cntI+"   END  ================================= ");

// Для теста ошибок сервера
//        String test=null;
//        test.charAt(0);

        cntI++;

        PrintWriter writer = response.getWriter();
        writer.write(res);
        writer.flush();
    }


}
