package com.mwlib.tablo.test.tpolg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mwlib.tablo.*;
import com.mwlib.tablo.cache.*;
import com.mycompany.common.*;


import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.desc.PlacesPOLGDesc;
import com.mwlib.tablo.derby.DerbyCliProvider;

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
public class CliProviderFactoryImpl3
        implements ICliProviderFactory, ICliManager
{

    private static CliProviderFactoryImpl3 cliProviderFactory=new CliProviderFactoryImpl3();
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



    private Map<String,Pair<ICache,BaseTableDesc>> name2Cache = new ConcurrentHashMap<String,Pair<ICache,BaseTableDesc>>();

    private Map<String,Map<String,Map<String, CliProvider>>> cliId2tType2TblN2Providers =new ConcurrentHashMap<String,Map<String,Map<String,CliProvider>>>();

    protected CliProviderFactoryImpl3()
    {
        name2Cache.put(TablesTypes.PLACEPOLG,new Pair(new Cache(),new PlacesPOLGDesc(false)));
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

        final ICliProvider[] iCliProviders;
        if (parameters.containsKey(TablesTypes.JT2ID)) //Пришлось ввести доп индикатор поскольку лента может содержать только один тип событий
        {
             iCliProviders= __getByNewMode(parameters, ctx[0], cliId);
        }
        else
            iCliProviders=__getByOldMode(parameters, ctx[0], cliId);

        return  iCliProviders;
    }

    private String[] getAddListTypes(Map parameters, String tType)
    {
        String jtbl2Id=getParameter(parameters, TablesTypes.TTYPE+"_ADD");
        if (jtbl2Id!=null)
        {
            Gson gson = new GsonBuilder().serializeNulls().create();
            String[] inTypes=gson.fromJson(jtbl2Id,String[].class);
            List<String> ll=new LinkedList<String>(Arrays.asList(inTypes));
            ll.add(tType);
            return ll.toArray(new String[ll.size()]);
        }
        return new String[]{tType};
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

            if (cachePair==null)
                throw new WrongParam("table "+tType+" unknown");


            Map<String,CliProvider> tblN2ProvidersByMainType =tType2TblN2Providers.get(tType);
            if (tblN2ProvidersByMainType==null || (cliProvider=tblN2ProvidersByMainType.get(tblId))==null)
            {
                String[] tInTypes= getAddListTypes(parameters, tType);

                if (tType.equals(TablesTypes.PLACEPOLG))
                    cliProvider = new PolgCliProvider(cachePair, cliId, tblId, tInTypes,tInTypes[0]);
                else
                    cliProvider = new DerbyCliProvider(cachePair, cliId, tblId, tType);//new CliProvider(cachePair, cliId, tblId, tInTypes);




                for (String inType : tInTypes)
                {
                    Map<String,CliProvider> addTblN2Providers =tType2TblN2Providers.get(inType);
                    if (addTblN2Providers==null)
                        tType2TblN2Providers.put(inType, addTblN2Providers = new ConcurrentHashMap<String, CliProvider>());
                    addTblN2Providers.put(tblId, cliProvider);
                }
            }

//            Map<String,CliProvider> tblN2ProvidersByMainType =tType2TblN2Providers.get(tType);
//            if (tblN2ProvidersByMainType==null)
//            {
//                if (cachePair==null)
//                    throw new WrongParam("table "+tType+" unknown");
//                tType2TblN2Providers.put(tType, tblN2ProvidersByMainType = new ConcurrentHashMap<String, CliProvider>());
//                cliProvider = subscribe2DataUpdaters(cliId, tType, tblId, cachePair, tblN2ProvidersByMainType);
//            }
//            else
//            {
//                cliProvider = tblN2ProvidersByMainType.get(tblId);
//                if (cliProvider ==null)
//                    cliProvider = subscribe2DataUpdaters(cliId, tType, tblId, cachePair, tblN2ProvidersByMainType);
//            }

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

                if (cachePair==null)
                    throw new WrongParam("table "+tType+" unknown");


                Map<String,CliProvider> tblN2ProvidersByMainType =tType2TblN2Providers.get(tType);


                if (tblN2ProvidersByMainType==null || (cliProvider=tblN2ProvidersByMainType.get(tblId))==null)
                {
                    String[] tInTypes= getAddListTypes(parameters, tType);

                    if (tType.equals(TablesTypes.PLACEPOLG))
                        cliProvider = new PolgCliProvider(cachePair, cliId, tblId, tInTypes,tInTypes[0]);
                    else
                        cliProvider = new DerbyCliProvider(cachePair, cliId, tblId, tType);//new CliProvider(cachePair, cliId, tblId, tInTypes);


                    for (String inType : tInTypes)
                    {
                        Map<String,CliProvider> addTblN2Providers =tType2TblN2Providers.get(inType);
                        if (addTblN2Providers==null)
                            tType2TblN2Providers.put(inType, addTblN2Providers = new ConcurrentHashMap<String, CliProvider>());
                        addTblN2Providers.put(tblId, cliProvider);
                    }
                }



//                if (tblN2ProvidersByMainType==null)
//                {
//                    if (cachePair==null)
//                        throw new WrongParam("table "+tType+" unknown");
//                    tType2TblN2Providers.put(tType, tblN2ProvidersByMainType = new ConcurrentHashMap<String, CliProvider>());
//                    cliProvider = subscribe2DataUpdaters(cliId, tType, tblId, cachePair, tblN2ProvidersByMainType);
//                }
//                else
//                {
//                    cliProvider = tblN2ProvidersByMainType.get(tblId);
//                    if (cliProvider ==null)
//                        cliProvider = subscribe2DataUpdaters(cliId, tType, tblId, cachePair, tblN2ProvidersByMainType);
//                }



                rvCliProvider.add(cliProvider);
            }
        }
        return rvCliProvider.toArray(new ICliProvider[rvCliProvider.size()]);
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
