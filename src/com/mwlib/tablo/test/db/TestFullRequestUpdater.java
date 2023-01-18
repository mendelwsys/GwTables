package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.EventTypeDistributer;
import com.mwlib.tablo.db.IEventProvider;
import com.mwlib.tablo.db.Type2NameMapperAuto;
import com.mwlib.tablo.ICliProviderFactoryImpl;

import com.mwlib.tablo.db.desc.UMsgDesc;
import com.mwlib.tablo.derby.DerbyDefaultCacheFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.10.14
 * Time: 15:04
 *
 */
public class TestFullRequestUpdater
{
    public static void main(String[] args) throws Exception {


        final boolean test = false;

        EventTypeDistributer metaProvider = new EventTypeDistributer(new Type2NameMapperAuto(new BaseTableDesc[]{new UMsgDesc(test)}), test);
        IEventProvider eventProvider = new UMsgEventProvider(metaProvider);
        ServerUpdaterFullRequestProvider updater = new ServerUpdaterFullRequestProvider(eventProvider, ICliProviderFactoryImpl.getCliManagerInstance(),new DerbyDefaultCacheFactory());

        new Thread(updater).start();//TODO запуск апдейтера


        for (;;)
            Thread.sleep(1000);

//        for (;;)//TODO Заменить на join что ли или так сойдет для теста?
//        try
//        {
//            Thread.sleep(15000);
//            ICliProviderFactory provider = ICliProviderFactoryImpl.getProviderFactoryInstance();
//
//            HashMap map = new HashMap();
//            map.put(ICliProviderFactory.CLIID,new String[]{"!@#$%^^QWWRWER"});
//            map.put(TablesTypes.TTYPE,new String[]{TablesTypes.WINDOWS});
//
//            ICliProvider[] tProviders = provider.getProvider(map);
//            Map<Object, long[]> dataKeys = tProviders[0].getNewDataKeys(new HashMap()).dataRef;
//            for (Object tupleKey : dataKeys.keySet())
//            {
//
//            }
//        }
//        catch (Exception e)
//        {
//
//
//        }
    }

}
