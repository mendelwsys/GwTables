package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.EventTypeDistributer;
import com.mwlib.tablo.db.ServerUpdaterT;
import com.mwlib.tablo.db.Type2NameMapperAuto;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.ICliProvider;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.ICliProviderFactoryImpl;

import com.mwlib.tablo.db.desc.WindowsDesc;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 15:04
 *
 */
public class TestUpdater
{
    public static void main(String[] args)
    {


        EventTypeDistributer metaProvider = new EventTypeDistributer(new Type2NameMapperAuto(new BaseTableDesc[]{new WindowsDesc(true)}),true);
        TotalEventProvider eventProvider = new TotalEventProvider(metaProvider);
        eventProvider.addDorKod(new int[]{28});
        ServerUpdaterT updater = new ServerUpdaterT(eventProvider, ICliProviderFactoryImpl.getCliManagerInstance());

//        {
//            updater.run();
//        }
        new Thread(updater).start();//TODO запуск апдейтера
        for (;;)//TODO Заменить на join что ли или так сойдет для теста?
        try
        {
            Thread.sleep(15000);
            ICliProviderFactory provider = ICliProviderFactoryImpl.getProviderFactoryInstance();

            HashMap map = new HashMap();
            map.put(ICliProviderFactory.CLIID,new String[]{"!@#$%^^QWWRWER"});
            map.put(TablesTypes.TTYPE,new String[]{TablesTypes.WINDOWS});

            ICliProvider[] tProviders = provider.getProvider(map);
            Map<Object, long[]> dataKeys = tProviders[0].getNewDataKeys(new HashMap()).dataRef;
            for (Object tupleKey : dataKeys.keySet())
            {

            }
        }
        catch (Exception e)
        {


        }
    }

}
