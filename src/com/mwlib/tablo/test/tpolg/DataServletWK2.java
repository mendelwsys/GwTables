package com.mwlib.tablo.test.tpolg;

import com.mwlib.tablo.db.*;
import com.mwlib.tablo.test.db.PlacesEventProvider;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.EventUtils;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICacheFactory;
import com.mwlib.tablo.cache.ICliUpdater;

import com.mwlib.tablo.derby.DerbyCache;
import com.mwlib.tablo.servlets.ServletUtils;
import com.mwlib.tablo.tables.DataSender4;
import com.mwlib.tablo.tables.TblUtils;
import com.mwlib.tablo.test.db.ServerUpdaterUExt;

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
 * Сервлет подкачики данных в таблицы клиентов
 */
public class DataServletWK2 extends HttpServlet {

    private CascadeUpdater updater;

    public void init() throws ServletException
    {

        boolean test=false;
        try
        {
            test = ServletUtils.setTestByParams(getServletConfig().getInitParameter("test"), test);
            String dsName = ServletUtils.setName(getServletConfig().getInitParameter(TablesTypes.DS_NAME), DbUtil.DS_ORA_NAME);

            Directory.initDictionary(test);

            EventTypeDistributer metaProvider = new EventTypeDistributer(TableDescSwitcher.getInstance().getMapper(), test);


            final List<ICascadeUpdater> updaters=new LinkedList<ICascadeUpdater>();
            {
                EventProviderTImpl eventProvider = new EventProviderTImpl(metaProvider,dsName);
                updaters.add(new ServerUpdaterUExt(eventProvider, CliProviderFactoryImpl3.getCliManagerInstance(),new ICacheFactory()
                {
                    @Override
                    public ICache createCache(Map<String, Object> params)
                    {
                        final Object cacheName = params.get(ICache.CACHENAME);
                        if (TablesTypes.PLACEPOLG.equals(cacheName))
                            return null;
                        return new DerbyCache(params);
                    }
                }));
            }


            {
                IEventProvider eventProvider = new PlacesEventProvider(metaProvider);
                updaters.add(new ServerUpdaterUExt(eventProvider, CliProviderFactoryImpl3.getCliManagerInstance(),
                        new ICacheFactory()
                {
                    @Override
                    public ICache createCache(Map<String, Object> params)
                    {
                        final Object cacheName = params.get(ICache.CACHENAME);

                       if (TablesTypes.PLACES.equals(cacheName))
                            return null;
                        if (TablesTypes.PLACEPOLG.equals(cacheName))
                            return null;
                        if (TablesTypes.BABKEN_TYPE.equals(cacheName))
                            return null;

                        params.put(ICache.CACHENAME, cacheName +"_"+TablesTypes.PLACES);
                        return new PolgCache(params)
                        {
                            protected String[] getIXColNames()
                            {
                                return new String[]{TablesTypes.DATA_OBJ_ID,TablesTypes.OBJ_OSN_ID};
                            }

                        };
                    }

                })
                {

                    protected ICliUpdater[] getCliCacheforName(String eventName) {
                        return cliManager.getCachesForName(eventName +"_"+TablesTypes.PLACES);
                    }

                    protected void put2CliManager(String eventName, ICache cache) {
                        cliManager.putCacheForName(eventName +"_"+TablesTypes.PLACES,new Pair<ICache, BaseTableDesc>(cache,metaProvider.getTypes2NamesMapper().getTableDescByTblName(eventName)));
                    }

                    protected ColumnHeadBean[] getCacheColumns(String eventName)
                    {
                        return metaProvider.getColumnsByEventName(TablesTypes.PLACES);
                    }

                });
            }

//TODO Нет таблицы в тестовой базе данных

//            {
//                final Type2NameMapperAuto types2names = new Type2NameMapperAuto(new BaseTableDesc[]{new UMsgDesc(test)});
//                TableDescSwitcher.getInstance().addMapper(types2names);
//                EventTypeDistributer metaProvider2 = new EventTypeDistributer(types2names, test);
//                IEventProvider eventProvider = new UMsgEventProvider(metaProvider2,dsName);
//                updaters.add(new ServerUpdaterFullRequestProvider(eventProvider, CliProviderFactoryImpl3.getCliManagerInstance(),new DerbyDefaultCacheFactory()));
//            }



            updater = new CascadeUpdater(updaters);
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

        DataSender4 dataSender4 = TblUtils.createDataSender5(map,CliProviderFactoryImpl3.getProviderFactoryInstance());
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
