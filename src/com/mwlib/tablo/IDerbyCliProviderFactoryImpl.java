package com.mwlib.tablo;

import com.mwlib.tablo.cache.CliProvider;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.derby.DerbyCliProvider;
import com.mycompany.common.Pair;
import com.mwlib.tablo.db.BaseTableDesc;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 19.11.14
 * Time: 13:02
 * Используется провайдер дерби (в дальнейшем будет универсальная фактори которая подымает по конфиг файлу или возможно из клиентских параметров)
 */
public class IDerbyCliProviderFactoryImpl extends ICliProviderFactoryImpl
{
    private static ICliProviderFactoryImpl cliProviderFactory=new IDerbyCliProviderFactoryImpl();
    public static ICliProviderFactory getProviderFactoryInstance()
    {
        return cliProviderFactory;
    }

    public static ICliManager getCliManagerInstance()
    {
        return cliProviderFactory;
    }


    protected CliProvider subscribe2DataUpdaters(String cliId, String tType, String tblId, Pair<ICache, BaseTableDesc> cachePair, Map<String, CliProvider> rv)
    {
        CliProvider cliProvider;
        rv.put(tblId,cliProvider = new DerbyCliProvider(cachePair, cliId, tblId, tType));
        return cliProvider;
    }

}
