package com.mwlib.tablo.test.hist;

import com.mwlib.tablo.db.*;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.IDerbyCliProviderFactoryImpl;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICacheFactory;

import com.mwlib.tablo.db.desc.WarningDesc;
import com.mwlib.tablo.db.desc.WindowsDesc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 19.11.14
 * Time: 14:09
 * Теста для серверного апдейта
 */
public class TestNewCachesUpdater
{
    public static void main(String[] args) throws Exception
    {

        DbUtil.name2Connector.put(DbUtil.DS_JAVA_CACHE_NAME,new DbUtil.IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                return DriverManager.getConnection("jdbc:derby:C:/PapaWK/Projects/JavaProj/SGWTVisual2/db/udb4;create=true");
            }
        });

        DbUtil.name2Connector.put(DbUtil.DS_ORA_NAME,new DbUtil.IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                return DriverManager.getConnection("jdbc:oracle:thin:@ora_host:ora_port:ora02", "user", "user");

            }
        });



        boolean test=false;
        Directory.initDictionary(test);
        {
            Type2NameMapperAuto mapper = new Type2NameMapperAuto(
              new BaseTableDesc[]{
                    //new DelayABGDDesc(test),new ZMDesc(test),new KMODesc(test),
                    //new LostTrDesc(test),new VagInTORDesc(test),new VIPGidDesc(test),new DelayGIDTDesc(test),
                    //new WarningInTimeDesc(test),
                    //new TechDesc(test),
//                      new ViolationDesc(test),new RefuseDesc(test),
                      new WindowsDesc(test),
                      new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                    new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test),
                      new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})
              }
            );

            Map<String,Object> params = new HashMap<String,Object>();
//            params.put(TablesTypes.DS_CACHE_NAME,dsCacheName);
//            params.put(TablesTypes.TESTMODEVAL,testMode);
//            params.put(TablesTypes.TESTTABLE,testTable);


            EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
            EventProviderTImpl eventProvider = new EventProviderTImpl(metaProvider,DbUtil.DS_ORA_NAME);
            ServerUpdaterUNEW updater = new ServerUpdaterUNEW(eventProvider, IDerbyCliProviderFactoryImpl.getCliManagerInstance(), new ICacheFactory()
            {
                public ICache createCache(Map<String, Object> params)
                {
                    final Object cacheName = params.get(ICache.CACHENAME);
                    if (TablesTypes.PLACEPOLG.equals(cacheName))
                        return null;
                    return new DerbyCacheH(params);
                }
            }
                    ,params)
            {
                protected void doSomeJob()
                {
                    this.setTerminate();
                }

            };
            new Thread(updater).start();
            Thread.sleep(1000);
        }

    }
}
