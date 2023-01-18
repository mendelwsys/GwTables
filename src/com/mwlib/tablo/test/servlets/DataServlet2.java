package com.mwlib.tablo.test.servlets;

import com.mwlib.tablo.db.*;
import com.mwlib.tablo.db.desc.*;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.EventUtils;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.ICliProviderFactoryImpl;
import com.mwlib.tablo.analit2.pred.ConsolidateEventProviderTImpl;
import com.mwlib.tablo.cache.DefaultCacheFactory;


import com.mwlib.tablo.tables.DataSender4;
import com.mwlib.tablo.tables.TblUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.09.14
 * Time: 12:37
 * Сервлет подкачики данных в таблицы клиентов
 */
public class DataServlet2 extends HttpServlet {

    private ServerUpdaterT updater01;
    private ServerUpdaterT2 updater; //TODO Сделать все таки интерфйс расширенной от ранабле

    public void init() throws ServletException {


        boolean test=false;
        try
        {
            Directory.initDictionary(test);
            if (!test)
            {
                Type2NameMapperAuto mapper = new Type2NameMapperAuto(
                  new BaseTableDesc[]{
                        new DelayABGDDesc(test),new ZMDesc(test),new KMODesc(test),
                        new LostTrDesc(test),new VagInTORDesc(test),new VIPGidDesc(test),new DelayGIDTDesc(test),
                        new WarningInTimeDesc(test),new TechDesc(test),new ViolationDesc(test),new RefuseDesc(test),new WindowsDesc(test), new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                        new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test),new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})}
                );


//                Type2NameMapperAuto mapper = new Type2NameMapperAuto(new BaseTableDesc[]
//                        {
//                           new DelayABGDDesc(test), new DelayGIDTDesc(test)
//                        }
//                );


                EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
                EventProviderTImpl eventProvider = new EventProviderTImpl(metaProvider);
                updater01 = new ServerUpdaterT(eventProvider, ICliProviderFactoryImpl.getCliManagerInstance());
//                new Thread(updater01).start();//запуск апдейтера кэшей данных.
//                Thread.sleep(15000);//TODO Как-то засинхранищировать это с наполнением кэшей
            }
            updater = new ServerUpdaterT2(ConsolidateEventProviderTImpl.getConsolidateProvider(test), ICliProviderFactoryImpl.getCliManagerInstance(),new DefaultCacheFactory());
            new Thread(updater).start();//запуск апдейтера кэшей данных.

            if (!test)
                new Thread(updater01).start();//запуск апдейтера кэшей данных.
            System.out.println("DataServer2 was startted in  mode test=" + test);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    public void destroy() {

        if (updater01 != null)
            updater01.setTerminate();

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


    protected void process(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
//        System.out.println("size2 is = " + res.length()/1024+" K");
        String res = EventUtils.toJson2(dataSender4);

        response.setContentType("application/json;charset=UTF-8");
        response.setContentLength(res.getBytes("UTF-8").length);

        PrintWriter writer = response.getWriter();
        writer.write(res);
        writer.flush();


    }


}
