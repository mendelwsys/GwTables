package com.mwlib.tablo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mwlib.tablo.cache.CliProvider;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICliUpdater;
import com.mwlib.tablo.cache.WrongParam;
import com.mycompany.common.JT2ID;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;

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
public class ICliProviderFactoryImpl
        implements ICliProviderFactory,ICliManager
{

    private static ICliProviderFactoryImpl cliProviderFactory=new ICliProviderFactoryImpl();
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

    private Map<String,Map<String,Map<String, CliProvider>>> cliId2tType2TblN2Providers =new ConcurrentHashMap<String,Map<String,Map<String,CliProvider>>>();

    protected ICliProviderFactoryImpl()
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

        if (parameters.containsKey(TablesTypes.JT2ID)) //Пришлось ввести доп индикатор поскольку лента может содержать только один тип событий
            return __getByNewMode(parameters, ctx[0], cliId);
        else
            return  __getByOldMode(parameters, ctx[0], cliId);
    }

    protected ICliProvider[] __getByOldMode(Map parameters, AppContext appContext, String cliId) throws WrongParam {
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
            tblId=String.valueOf(appContext.getNextTableIndex());

        CliProvider cliProvider;
        {
            Pair<ICache, BaseTableDesc> cachePair = name2Cache.get(tType);
            Map<String,CliProvider> rv =tType2TblN2Providers.get(tType);
            if (rv==null)
            {
                if (cachePair==null)
                    throw new WrongParam("table "+tType+" unknown");
                tType2TblN2Providers.put(tType, rv = new ConcurrentHashMap<String, CliProvider>());
                cliProvider = subscribe2DataUpdaters(cliId, tType, tblId, cachePair, rv);
            }
            else
            {
                cliProvider = rv.get(tblId);
                if (cliProvider ==null)
                    cliProvider = subscribe2DataUpdaters(cliId, tType, tblId, cachePair, rv);
            }
        }
        return new ICliProvider[]{cliProvider};
    }


    protected ICliProvider[] __getByNewMode(Map parameters, AppContext appContext, String cliId) throws WrongParam
    {

        Gson gson = new GsonBuilder().serializeNulls().create();

        JT2ID[] ttype2tblid;

        String jtbl2Id=getParameter(parameters, TablesTypes.TBLID);
        if (jtbl2Id==null || jtbl2Id.length()==0)
        {
            String [] tTypes= (String[]) parameters.get(TablesTypes.TTYPE);
            if (tTypes==null || tTypes.length==0)
                throw new WrongParam("table Name can't be null or empty");

            ttype2tblid=new JT2ID[tTypes.length];
            for (int i = 0; i < tTypes.length; i++)
                ttype2tblid[i]=new JT2ID(tTypes[i],null);
        }
        else
        {
           ttype2tblid=gson.fromJson(jtbl2Id, JT2ID[].class); //TODO Для ускорения можно в принципе и не парсить хранить объекты в сессии

           String [] tTypes= (String[]) parameters.get(TablesTypes.TTYPE);

           if (tTypes!=null && tTypes.length>0)
           {
               Set<String> hs=new HashSet<String>(Arrays.asList(tTypes));

               for (JT2ID jt2ID : ttype2tblid)
                   hs.remove(jt2ID.getTtype());

               if (hs.size()>0)
               {

                   JT2ID[] ttype2tblid_add = new JT2ID[hs.size()];
                   for (int i = 0; i < ttype2tblid_add.length; i++)
                       ttype2tblid_add[i]=new JT2ID(tTypes[i],null);

                   final LinkedList<JT2ID> jt2IDs = new LinkedList<JT2ID>(Arrays.asList(ttype2tblid));
                   jt2IDs.addAll(Arrays.asList(ttype2tblid_add));
                   ttype2tblid=jt2IDs.toArray(new JT2ID[ttype2tblid.length]);
               }
           }

        }





        List<CliProvider> rvCliProvider= new LinkedList<CliProvider>();
        CliProvider cliProvider;
        for (JT2ID jt2ID : ttype2tblid)
        {

            Map<String,Map<String, CliProvider>> tType2TblN2Providers = cliId2tType2TblN2Providers.get(cliId);
            if (tType2TblN2Providers==null)
            {
                tType2TblN2Providers=new ConcurrentHashMap<String,Map<String, CliProvider>>();
                cliId2tType2TblN2Providers.put(cliId,tType2TblN2Providers);
            }

            {
                String tType = jt2ID.getTtype();
                String tblId=jt2ID.getTblid();
                if (tblId==null)
                    tblId=String.valueOf(appContext.getNextTableIndex());

                Pair<ICache, BaseTableDesc> cachePair = name2Cache.get(tType);
                Map<String,CliProvider> rv =tType2TblN2Providers.get(tType);
                if (rv==null)
                {
                    if (cachePair==null)
                        throw new WrongParam("table "+tType+" unknown");
                    tType2TblN2Providers.put(tType, rv = new ConcurrentHashMap<String, CliProvider>());
                    cliProvider = subscribe2DataUpdaters(cliId, tType, tblId, cachePair, rv);
                }
                else
                {
                    cliProvider = rv.get(tblId);
                    if (cliProvider ==null)
                        cliProvider = subscribe2DataUpdaters(cliId, tType, tblId, cachePair, rv);
                }
                rvCliProvider.add(cliProvider);
            }
        }
        return rvCliProvider.toArray(new ICliProvider[rvCliProvider.size()]);
    }

    protected CliProvider subscribe2DataUpdaters(String cliId, String tType, String tblId, Pair<ICache, BaseTableDesc> cachePair, Map<String, CliProvider> rv)
    {
        CliProvider cliProvider;
        rv.put(tblId,cliProvider = new CliProvider(cachePair, cliId, tblId, tType));
        return cliProvider;
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

//            if (tType2TblN2Providers.size()==0) Не надо убирать, сборщик мертвых сессий уберет это....
//                cliId2tType2TblN2Providers.remove(cliId);
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
