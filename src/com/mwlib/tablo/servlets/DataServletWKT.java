package com.mwlib.tablo.servlets;

import com.mwlib.tablo.derby.DerbyDefaultCacheFactory;
import com.mwlib.tablo.tables.DataSender4;
import com.mwlib.tablo.tables.TblUtils;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.EventUtils;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.IDerbyCliProviderFactoryImpl;
import com.mwlib.tablo.db.EventProviderTImpl;
import com.mwlib.tablo.db.EventTypeDistributer;
import com.mwlib.tablo.db.ServerUpdaterU;
import com.mwlib.tablo.db.TableDescSwitcher;

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
public class DataServletWKT extends HttpServlet {


    private ServerUpdaterU updater;

    public void init() throws ServletException
    {

        boolean test=false;
        try
        {
            test = ServletUtils.setTestByParams(getServletConfig().getInitParameter(TablesTypes.TESTVAL), test);
            String dsName = ServletUtils.setName(getServletConfig().getInitParameter(TablesTypes.DS_NAME), DbUtil.DS_ORA_NAME);

            Directory.initDictionary(test);
            EventTypeDistributer metaProvider = new EventTypeDistributer(TableDescSwitcher.getInstance().getMapper(), test);
            EventProviderTImpl eventProvider = new EventProviderTImpl(metaProvider,dsName);
            updater = new ServerUpdaterU(eventProvider, IDerbyCliProviderFactoryImpl.getCliManagerInstance(),new DerbyDefaultCacheFactory());
            new Thread(updater).start();//запуск апдейтера кэшей данных.
            Thread.sleep(1000);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    public void destroy() {
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

        DataSender4 dataSender4 = TblUtils.createDataSender5(map);
//TODO 22.02.2015 (Для удаления)
//        DataSender4 dataSender4 = TblUtils.createDataSender4(map);

//TODO 13.01.2015 (Для удаления)
//        DataSender2[] sender = new DataSender2[]{dataSender2};
//        String res = EventUtils.toJson(sender);
//        String res= EventUtils.toJson(new DataSender4(dataSender2.getTuples(),new TransactSender(dataSender2),dataSender2.getDesc()));

        String res = EventUtils.toJson(dataSender4);

        response.setContentType("application/json;charset=UTF-8");
        response.setContentLength(res.getBytes("UTF-8").length);

        PrintWriter writer = response.getWriter();
        writer.write(res);
        writer.flush();

    }

}
