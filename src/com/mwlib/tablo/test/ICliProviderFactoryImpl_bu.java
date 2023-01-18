package com.mwlib.tablo.test;

import com.mwlib.tablo.*;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;

import com.mwlib.tablo.cache.CliProvider;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICliUpdater;
import com.mwlib.tablo.cache.WrongParam;
import com.mwlib.tablo.db.BaseTableDesc;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.09.14
 * Time: 17:49
 * Этот класс и будет запускать обновления таблиц и выдавть клиентские провайдеры,
 * а так же очищать клиентские объекты если таковые долго не вызываеются
 */
public class ICliProviderFactoryImpl_bu
        implements ICliProviderFactory, ICliManager
{

    private static ICliProviderFactoryImpl_bu cliProviderFactory=new ICliProviderFactoryImpl_bu();
    public static ICliProviderFactory getProviderFactoryInstance()
    {
        return cliProviderFactory;
    }

    public static ICliManager getCliManagerInstance()
    {
        return cliProviderFactory;
    }


    public static final long MAXTIMEOUT = 2*60*1000;
    private Set<String> constCliIds = new HashSet<String>();

    @Override
    public void addNotSessionCliIds(String clId)
    {
        constCliIds.add(clId);
    }

    @Override
    public void removeNotSessionCliIds(String clId)
    {
        constCliIds.remove(clId);
    }



    //private Map<String,Integer> name2Cache= new ConcurrentHashMap<>();
    //private ICache[] caches;
    private Map<String,Pair<ICache,BaseTableDesc>> name2Cache = new ConcurrentHashMap<String,Pair<ICache,BaseTableDesc>>();

    private Map<String,Map<String,Map<String,CliProvider>>> cliId2tType2TblN2Providers =new ConcurrentHashMap<String,Map<String,Map<String,CliProvider>>>();

    protected ICliProviderFactoryImpl_bu()
    {
    }

    @Override
    public ICliProvider[] getProvider(Map parameters) throws WrongParam
    {
        AppContext[] ctx = (AppContext[])parameters.get(AppContext.APPCONTEXT);
        if (ctx==null || ctx.length==0)
            throw  new WrongParam("ctx can't be null");


        String cliId = getParameter(parameters, CLIID);
        if (cliId==null)
            throw  new WrongParam("client id can't be null");

        String tType= getParameter(parameters, TablesTypes.TTYPE);
        if (tType==null)
            throw new WrongParam("table Name can't be null");

        Map<String,Map<String, CliProvider>> tType2TblN2Providers = cliId2tType2TblN2Providers.get(cliId);

        if (tType2TblN2Providers==null)
        {
            tType2TblN2Providers=new ConcurrentHashMap<String,Map<String, CliProvider>>();
            cliId2tType2TblN2Providers.put(cliId,tType2TblN2Providers);
        }


        String tblId=getParameter(parameters, TablesTypes.TBLID);
        if (tblId==null)
            tblId=String.valueOf(ctx[0].getNextTableIndex());

        CliProvider cliProvider;
        {
            Pair<ICache, BaseTableDesc> cachePair = name2Cache.get(tType);
            Map<String,CliProvider> rv =tType2TblN2Providers.get(tType);
            if (rv==null)
            {
                if (cachePair==null)
                    throw new WrongParam("table "+tType+" unknown");
                tType2TblN2Providers.put(tType, rv = new ConcurrentHashMap<String, CliProvider>());
                rv.put(tblId,cliProvider = getCliProvider(cliId, tType, tblId, cachePair));
            }
            else
            {
                cliProvider = rv.get(tblId);
                if (cliProvider ==null)
                    rv.put(tblId,cliProvider = getCliProvider(cliId, tType, tblId, cachePair));
            }
        }
        return new ICliProvider[]{cliProvider};
    }

    protected CliProvider getCliProvider(String cliId, String tType, String tblId, Pair<ICache, BaseTableDesc> cachePair) {
        return new CliProvider(cachePair, cliId, tblId, tType);
    }

    private String getParameter(Map parameters, String cliid) {
        String[] arCliId=(String[])parameters.get(cliid);
        String cliId=null;
        if (arCliId!=null && arCliId.length==1)
            cliId=arCliId[0];
        return cliId;
    }


    @Override
    public ICliUpdater[] getCachesForName(String name)
    {
        List<ICliUpdater> ll= new LinkedList<ICliUpdater>();
        for (Map<String, Map<String,CliProvider>> ttype2TblN2Providers : cliId2tType2TblN2Providers.values())
        {
            Map<String,CliProvider> tblN2Providers=ttype2TblN2Providers.get(name);
            if (tblN2Providers!=null)
                ll.addAll(tblN2Providers.values());
        }
        return ll.toArray(new ICliUpdater[ll.size()]);
    }

    @Override
    public Pair<ICache, BaseTableDesc> putCacheForName(String eventName, Pair<ICache, BaseTableDesc> cache)
    {
        return name2Cache.put(eventName,cache);
    }

    @Override
    public void removeCache(ICliUpdater cache)
    {
        String cliId = cache.getCliId();
        Map<String, Map<String, CliProvider>> tType2TblN2Providers = cliId2tType2TblN2Providers.get(cliId);

        if (tType2TblN2Providers!=null)
        {

            String[] tTypes=cache.getTType();
            for (String tType : tTypes)
            {
                Map<String, CliProvider> tblN2Providers = tType2TblN2Providers.get(tType);
                if (tblN2Providers!=null)
                {
                    String tblId = cache.getTblId();
                    tblN2Providers.remove(tblId);

                    if (tblN2Providers.size()==0)
                        tType2TblN2Providers.remove(tType);
                }
            }
        }
    }

    @Override
    public void removeDeadSessions()
    {
        Set<String> sess = new HashSet<String>(cliId2tType2TblN2Providers.keySet());
        for (String ses : sess)
        {
            if (constCliIds.contains(ses))
                continue;

            HttpSession session = HttpSessionCollector.find(ses);
            if (session==null ||(System.currentTimeMillis()-session.getLastAccessedTime())> MAXTIMEOUT)
            {
                cliId2tType2TblN2Providers.remove(ses);
                if (session!=null)
                {
                    HttpSessionCollector.remove(ses);
                    session.invalidate();
                }
            }
        }
    }
}
