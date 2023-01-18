package com.mwlib.tablo.test.analit2.delay;

import com.mwlib.utils.db.DbUtil;
import com.mwlib.tablo.ICliProviderFactoryImpl;
import com.mwlib.tablo.analit2.delay.DelayEventProviderTImpl;
import com.mwlib.tablo.cache.DefaultCacheFactory;
import com.mwlib.tablo.db.ITypes2NameMapper;
import com.mwlib.tablo.db.ServerUpdaterT2;
import com.mwlib.tablo.db.TableDescSwitcher;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.03.15
 * Time: 16:57
 * Тестирование провайдров консолидации задержек поездов
 */
public class TestDelayProviders
{
    public static void main(String[] args) throws Exception
    {
        boolean  test = true;
        String dsName =DbUtil.DS_JAVA_CACHE_NAME;


        ITypes2NameMapper mapper = TableDescSwitcher.getInstance().getMapper();
        ServerUpdaterT2 updater = new ServerUpdaterT2(DelayEventProviderTImpl.getConsolidateProvider(dsName, mapper.getNames(), test), ICliProviderFactoryImpl.getCliManagerInstance(),new DefaultCacheFactory());
        new Thread(updater).start();//запуск апдейтера кэшей данных.


    }
}
