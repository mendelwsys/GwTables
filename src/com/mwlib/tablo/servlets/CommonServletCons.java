package com.mwlib.tablo.servlets;

import com.mwlib.tablo.*;
import com.mwlib.tablo.analit2.delay.DelayEventProviderTImpl;
import com.mwlib.tablo.analit2.loco.LOCEventProviderTImpl;
import com.mwlib.tablo.analit2.oindex.OIndxEventProviderTImpl;
import com.mwlib.tablo.analit2.places.PlacesEventProviderTImpl;
import com.mwlib.tablo.analit2.pred.ConsolidateEventProviderTImpl2;
import com.mwlib.tablo.analit2.ref12.Ref12EventProviderTImpl;
import com.mwlib.tablo.analit2.rsm.RSMEventProviderTImpl;
import com.mwlib.tablo.analit2.warnact.WarnActEventProviderTImpl;
import com.mwlib.tablo.analit2.warnv.WarnVEventProviderTImpl;
import com.mwlib.tablo.analit2.winrep.WinRepEventProviderTImpl;
import com.mwlib.tablo.cache.DefaultCacheFactory;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.db.*;
import com.mwlib.tablo.tables.DataSender4;
import com.mwlib.tablo.tables.TblUtils;
import com.mwlib.utils.db.DbUtil;
import com.mycompany.common.TablesTypes;

import com.mwlib.tablo.analit2.warnagr.WarnAGREventProviderTImpl;
import com.mwlib.tablo.analit2.winplan.WinPlanEventProviderTImpl;


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
 * Date: 13.07.15
 * Time: 16:22
 * CommonServletCons
 */
public class CommonServletCons extends HttpServlet {

          private CascadeUpdater updater;

            public void init() throws ServletException {

                boolean test=false;
                try
                {

                    List<ICascadeUpdater> updaters=new LinkedList<ICascadeUpdater>();
                    test = ServletUtils.setTestByParams(getServletConfig().getInitParameter("test"), test);
                    String period = ServletUtils.setInitParamByParams(getServletConfig().getInitParameter("period"), String.valueOf(TablesTypes.DEFPERIOD));
                    String dsName = ServletUtils.setName(getServletConfig().getInitParameter(TablesTypes.DS_NAME), DbUtil.DS_JAVA_CACHE_NAME);
                    String dsOraName = ServletUtils.setName(getServletConfig().getInitParameter(TablesTypes.DS_ORA_NAME), DbUtil.DS_ORA_NAME);

                    final List<BaseTableDesc> tblDesc=new LinkedList<>();
                    {
                        tblDesc.add(PlacesEventProviderTImpl.createBaseTableDesc(PlacesEventProviderTImpl.getDesc(), test));
                        tblDesc.add(ConsolidateEventProviderTImpl2.createBaseTableDesc(ConsolidateEventProviderTImpl2.getDesc(), test));
                        tblDesc.add(DelayEventProviderTImpl.createBaseTableDesc(DelayEventProviderTImpl.getDesc(), test));
                        tblDesc.add(Ref12EventProviderTImpl.createBaseTableDesc(Ref12EventProviderTImpl.getDesc(), test));
                        tblDesc.add(WarnVEventProviderTImpl.createBaseTableDesc(WarnVEventProviderTImpl.getDesc(), test));
                        tblDesc.add(WinRepEventProviderTImpl.createBaseTableDesc(WinRepEventProviderTImpl.getDesc(), test));
                        tblDesc.add(WinPlanEventProviderTImpl.createBaseTableDesc(WinPlanEventProviderTImpl.getDesc(), test));
                        tblDesc.add(WarnActEventProviderTImpl.createBaseTableDesc(WarnActEventProviderTImpl.getDesc(), test));
                        tblDesc.add(OIndxEventProviderTImpl.createBaseTableDesc(OIndxEventProviderTImpl.getDesc(), test));
                        tblDesc.add(WarnAGREventProviderTImpl.createBaseTableDesc(WarnAGREventProviderTImpl.getDesc(), test));
                        tblDesc.add(RSMEventProviderTImpl.createBaseTableDesc(RSMEventProviderTImpl.getDesc(), test));
                        tblDesc.add(LOCEventProviderTImpl.createBaseTableDesc(LOCEventProviderTImpl.getDesc(), test));
                    }

                    final EventTypeDistributer metaProvider = new EventTypeDistributer(new Type2NameMapperAuto(tblDesc.toArray(new BaseTableDesc[tblDesc.size()])), test);

                    ITypes2NameMapper mapper = TableDescSwitcher.getInstance().getMapper();
                    final String[] loaderTypes = mapper.getNames();

                    final ICliManager cliManagerInstance = ICliProviderFactoryImpl.getCliManagerInstance();

                    {
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

                        updaters.add(new ServerUpdaterT2(PlacesEventProviderTImpl.getConsolidateProvider(dsName,dsOraName, newLoaderTypes.toArray(new String[newLoaderTypes.size()]), test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (PlacesEventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );

                        updaters.add(new ServerUpdaterT2(Ref12EventProviderTImpl.getConsolidateProvider(dsName, newLoaderTypes.toArray(new String[newLoaderTypes.size()]), test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (Ref12EventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );


                        updaters.add(new ServerUpdaterT2(WarnVEventProviderTImpl.getConsolidateProvider(dsName, newLoaderTypes.toArray(new String[newLoaderTypes.size()]), test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (WarnVEventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );

                        updaters.add(new ServerUpdaterT2(WinRepEventProviderTImpl.getConsolidateProvider(dsName, newLoaderTypes.toArray(new String[newLoaderTypes.size()]), test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (WinRepEventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );


                        updaters.add(new ServerUpdaterT2(WinPlanEventProviderTImpl.getConsolidateProvider(dsName, newLoaderTypes.toArray(new String[newLoaderTypes.size()]), test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (WinPlanEventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );


                        updaters.add(new ServerUpdaterT2(WarnActEventProviderTImpl.getConsolidateProvider(dsName, newLoaderTypes.toArray(new String[newLoaderTypes.size()]), test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (WarnActEventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );

                    }


                    {
                        updaters.add(new ServerUpdaterT2(LOCEventProviderTImpl.getConsolidateProvider(dsName, loaderTypes, test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (LOCEventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );
                    }


//                    if (!test)
                    {
                        updaters.add(new ServerUpdaterT2(RSMEventProviderTImpl.getConsolidateProvider(dsOraName, loaderTypes, test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (RSMEventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );
                    }

                    {
                        updaters.add(new ServerUpdaterT2(ConsolidateEventProviderTImpl2.getConsolidateProvider(dsName, loaderTypes, test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (ConsolidateEventProviderTImpl2.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );
                    }


                    {
                        updaters.add(new ServerUpdaterT2(DelayEventProviderTImpl.getConsolidateProvider(dsName, loaderTypes, test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (DelayEventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );
                    }

                    {
                        updaters.add(new ServerUpdaterT2(OIndxEventProviderTImpl.getConsolidateProvider(dsName, loaderTypes, test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (OIndxEventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );
                    }

                    {
                        updaters.add(new ServerUpdaterT2(WarnAGREventProviderTImpl.getConsolidateProvider(dsName, loaderTypes, test,metaProvider), cliManagerInstance,
                                new DefaultCacheFactory()
                                {
                                    public ICache createCache(Map<String, Object> params)
                                    {
                                        if (WarnAGREventProviderTImpl.getConsTableType().equals(params.get(ICache.CACHENAME)))
                                            return super.createCache(params);
                                        else
                                            return null;
                                    }
                                })
                        );
                    }


                    updater = new CascadeUpdater(updaters);
                    try
                    {
                        if (period!=null && period.length()>0)
                            updater.setPeriod(Integer.parseInt(period));
                    } catch (NumberFormatException e) {
                        //
                    }
                    new Thread(updater).start();//запуск апдейтера кэшей данных.
                    System.out.println("CommonServletCons was startted in  mode test=" + test);
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
