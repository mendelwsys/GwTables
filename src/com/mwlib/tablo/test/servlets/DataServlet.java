package com.mwlib.tablo.test.servlets;

import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.EventUtils;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.ICliProviderFactoryImpl;
import com.mwlib.tablo.db.EventProviderTImpl;
import com.mwlib.tablo.db.EventTypeDistributer;
import com.mwlib.tablo.db.ServerUpdaterT;
import com.mwlib.tablo.db.TableDescSwitcher;
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
 * Сервлет подкачики данных в таблицы клиентов (Тестовый)
 */
public class DataServlet extends HttpServlet {

    private ServerUpdaterT updater;

    public void init() throws ServletException {

        //TODO вообще говоря здесь во время инициализации из конфигурационного файла.
        TableDescSwitcher descSwitcher = TableDescSwitcher.getInstance();
        EventTypeDistributer metaProvider = new EventTypeDistributer(descSwitcher.getMapper(), descSwitcher.isTest());
        EventProviderTImpl eventProvider = new EventProviderTImpl(metaProvider);
        updater = new ServerUpdaterT(eventProvider, ICliProviderFactoryImpl.getCliManagerInstance());
//TODO        new Thread(updater).start();//запуск апдейтера кэшей данных.
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

        DataSender4 dataSender4 = TblUtils.createDataSender(map);

        DataSender4[] sender = new DataSender4[]{dataSender4};
        String res = EventUtils.toJson(sender);

        /*
        String res= EventUtils.toJson(new DataSender4(dataSender2.getTuples(),new TransactSender(dataSender2),dataSender2.getDesc()));

         */

        response.setContentType("application/json;charset=UTF-8");
        response.setContentLength(res.getBytes("UTF-8").length);

        PrintWriter writer = response.getWriter();
        writer.write(res);
        writer.flush();


    }

//    private DataSender2 createDataSender(Map map) throws Exception
//    {
//        ICliProviderFactory providerFactoryInstance = ICliProviderFactoryImpl.getProviderFactoryInstance();
//        ICliProvider provider = providerFactoryInstance.getProvider(map); //TODO по идее здесь провайдер уже относится только к запрашиваемой таблице
//        Map<Object, long[]> newKeys = provider.getNewDataKeys(map);
//
//        System.out.println("size of new keys:" + newKeys.size());
//
//
//        Map[] chs = new Map[newKeys.size()]; //!!!TODO - обязательно заменить на массивы!!!! Очень много лишней информации прет!!!!
//
//        Map<String, ColumnHeadBean> mapMeta = new HashMap<String, ColumnHeadBean>();
//        {
//            ColumnHeadBean[] meta = provider.getMeta();
//            for (ColumnHeadBean aMeta : meta)
//                mapMeta.put(aMeta.getName(), aMeta);
//        }
//
//        int ix = 0;
//        for (Object key : newKeys.keySet())
//        {
//                chs[ix] = new HashMap();
//
//                Object[] o = provider.getTupleByKey(key);
//
//                for (int ixCol = 0; ixCol < o.length; ixCol++)
//                    chs[ix].put(provider.getColNameByIx(ixCol), o[ixCol]);
//                chs[ix] = provider.getTableDesc(map).translateTuple(chs[ix], mapMeta);
//
//                if (newKeys.get(key) == null)
//                    chs[ix].put(TablesTypes.ACTUAL, 0);//
//
//                //TablesTypes.HMASK //TODO Пока не сделали поскольку не известно как меняются оттранслированные таплы в зависимости от входного тапла
//                ix++;
//        }
//
//        //TODO проработка 1. удаления кортежей
//        //TODO и выработка единого пула зарпросов к БД из всех открытых окон клиента
//        //TODO  т.е. необходимо сделать еще одно окно (создав соответсвующий дескриптор, открыть их и проверить наполнение данными, далее объединять в единый пул запросов и ответов системы)
//
////        DataSender2[] sender = new DataSender2[]{new DataSender2()};
////        sender[0].setTuples(chs);
////        sender[0].setCliCnt(provider.getCliCnt());
////        sender[0].setTblId(provider.getTblId());
//
//        DataSender2 dataSender2 = new DataSender2();
//        dataSender2.setTuples(chs);
//        dataSender2.setCliCnt(provider.getCliCnt());
//        dataSender2.setTblId(provider.getTblId());
//
//        return dataSender2;
//    }

}
