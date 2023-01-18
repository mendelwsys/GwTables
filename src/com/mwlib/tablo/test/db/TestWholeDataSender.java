package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.ICliProvider;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.ICliProviderFactoryImpl;
import com.mwlib.tablo.analit2.pred.ConsolidateEventProviderTImpl;
import com.mwlib.tablo.cache.DefaultCacheFactory;
import com.mwlib.tablo.cache.WrongParam;

import com.mwlib.tablo.db.desc.TechDesc;
import com.mwlib.tablo.db.desc.WarningDesc;
import com.mwlib.tablo.db.desc.WindowsDesc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.10.14
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
public class TestWholeDataSender
{
    public static final String DEF_SESSIONID = "CONSOLIDATOR1";

    public static void main(String[] args) throws Exception
    {

        boolean test=false;
        {
            //Type2NameMapperAuto mapper = new Type2NameMapperAuto(new BaseTableDesc[]{new WindowsDesc(test), new WarningDesc(test)});

            Type2NameMapperAuto mapper = new Type2NameMapperAuto(new BaseTableDesc[]{new TechDesc(test),new WindowsDesc(test), new WindowsDesc(test,TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                    new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test)});


            EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
            TotalEventProvider eventProvider = new TotalEventProvider(metaProvider);
            ServerUpdaterT updater = new ServerUpdaterT(eventProvider, ICliProviderFactoryImpl.getCliManagerInstance());
            new Thread(updater).start();//запуск апдейтера кэшей данных.
            Thread.sleep(40000);//TODO Как-то засинхранищировать это с наполнением кэшей
        }

        ServerUpdaterT2 updater = new ServerUpdaterT2(ConsolidateEventProviderTImpl.getConsolidateProvider(test), ICliProviderFactoryImpl.getCliManagerInstance(),new DefaultCacheFactory());
        new Thread(updater).start();//запуск апдейтера кэшей данных.
        Thread.sleep(30000);//TODO Как-то засинхранищировать это с наполнением кэшей


//        if (false)
        {//Эмуляция клиенских запросов
            Map mapParms=null;
            int ixReq=0;
            for (;;)
            {
                long ln=System.currentTimeMillis();

                if (mapParms==null)
                {
                    mapParms = new HashMap();
                    mapParms.put(ICliProviderFactory.CLIID, new String[]{DEF_SESSIONID});
                    mapParms.put(AppContext.APPCONTEXT, new AppContext[]{new AppContext()});
                    mapParms.put(TablesTypes.TTYPE, new String[]{TablesTypes.STATEDESC});
                    mapParms.put("CLICNT",0);
                }
                mapParms.put(TablesTypes.ID_REQN, new String[]{String.valueOf(ixReq)});

                Map<Object, long[]> res = new HashMap();
                try
                {
                    ICliProviderFactory providerFactoryInstance = ICliProviderFactoryImpl.getProviderFactoryInstance();
                    providerFactoryInstance.addNotSessionCliIds(DEF_SESSIONID);
                    ICliProvider[] providers = providerFactoryInstance.getProvider(mapParms);
                    ICliProvider provider=providers[0];
                    ixReq++;
                    mapParms.put(TablesTypes.TBLID, new String[]{provider.getTblId()});
                    res = provider.getNewDataKeys(mapParms).dataRef;
                } catch (WrongParam wrongParam)
                {
                    System.out.println("Provider is not ready:"+wrongParam.getMessage());
                }
                long mills = System.currentTimeMillis() - ln;
                System.out.println("Clients data value = " + res.size()+" tms:"+mills);
                Thread.sleep(Math.max(100,3000- mills));
            }
        }


    }
}
