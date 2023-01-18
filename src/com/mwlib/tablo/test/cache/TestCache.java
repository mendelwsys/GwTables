package com.mwlib.tablo.test.cache;


import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.cache.Cache;
import com.mwlib.tablo.cache.CliProvider;
import com.mwlib.tablo.test.tables.BaseTable;
import com.mwlib.tablo.test.tables.WindowsT;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.09.14
 * Time: 14:37
 * Серия тестов для тестирования кэша данных и процедур формирования посылок на сервер
 */
public class TestCache
{

    public static final String KEY1 = "1";

    public static void main(String[] args)  throws Exception
    {
//
        Map<String,CliProvider> sess2CliCache =  new ConcurrentHashMap<>();
        sess2CliCache.put(KEY1,new CliProvider(null,"0","",""));



        //BaseTable table = StripT.getInstance(true);

        //BaseTable table = WarningsT.getInstance(true);

        BaseTable table = WindowsT.getInstance(true);


        ColumnHeadBean[] meta = table.getMeta();
        Map[] data = table.getData(new HashMap(),new HashMap());

        Cache cache=new Cache(); //TODO !!!Упорядочить инициализацию!!!
        cache.setMeta(meta);
        cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME},cache));

        cache.update(data, true); //TODO проверить новую реализацию !!!

        double[] ix=new double[]{Math.random(),Math.random(),Math.random(),Math.random(),Math.random(),Math.random(),Math.random(),Math.random(),Math.random(),Math.random()};
        Map[] maps=new Map[ix.length];
        for (int i = 0, ixLength = ix.length; i < ixLength; i++) {
            int ixx = (int) (ix[i] * (data.length-1));
            maps[i] =data[ixx];
        }
        Map<Object, long[]> rv = cache.update(maps, true).dataRef;//получить апдейтнутые кортежи в кеше

        long ln=System.currentTimeMillis();
//        for (CliProvider cliCache : sess2CliCache.values())
//            cliCache.getData(null);
       //cliCache.merge(rv); //TODO Это будет делать клиентский поток, когда будет формировать посылку на браузер колиента
        System.out.println("ln = " + 1.0*(System.currentTimeMillis()-ln)/1000);
//        System.out.println("ln = " + (System.currentTimeMillis()-ln));

    }
}
