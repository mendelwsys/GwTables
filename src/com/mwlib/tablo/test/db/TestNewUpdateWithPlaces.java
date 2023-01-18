package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
import com.mwlib.tablo.db.desc.*;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICacheFactory;


import com.mwlib.tablo.derby.DerbyCache;
import com.mwlib.tablo.derby.DerbyDefaultCacheFactory;
import com.mwlib.tablo.test.CliProviderFactoryImpl2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 30.05.15
 * Time: 23:36
 * To change this template use File | Settings | File Templates.
 */
public class TestNewUpdateWithPlaces
{
    static Connection connection;
    static {
        DbUtil.name2Connector.put(DbUtil.DS_JAVA_CACHE_NAME,new DbUtil.IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                connection = DriverManager.getConnection("jdbc:derby:C:/PapaWK/Projects/JavaProj/SGWTVisual2/db/udb4;create=true");
                return connection;
            }
        });

        DbUtil.name2Connector.put(DbUtil.DS_ORA_NAME,new DbUtil.IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                return DriverManager.getConnection("jdbc:oracle:thin:@ora_host:ora_port:ora_test", "user", "user");
            }
        });
    }

    public static void main(String[] args) throws Exception
    {
        boolean test=false;

        Directory.initDictionary(test);


        Type2NameMapperAuto mapper = new Type2NameMapperAuto(new BaseTableDesc[]{
                new DelayABGDDesc(test),new ZMDesc(test),new KMODesc(test),
                new LostTrDesc(test),new VagInTORDesc(test),new VIPGidDesc(test),new DelayGIDTDesc(test),
                new WarningInTimeDesc(test),new TechDesc(test),new ViolationDesc(test),
                new RefuseDesc(test),new WindowsDesc(test),
                new WindowsDesc(test,TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),
                new WarningDesc(test),new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79}),
                new PlacesDesc(test)
        });
        EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);

        final List<ICascadeUpdater> updaters=new LinkedList<ICascadeUpdater>();
        {
            EventProviderTImpl eventProvider = new EventProviderTImpl(metaProvider);
            updaters.add(new ServerUpdaterUExt(eventProvider, CliProviderFactoryImpl2.getCliManagerInstance(),new DerbyDefaultCacheFactory()));
        }

        {
            IEventProvider eventProvider = new PlacesEventProvider(metaProvider);
            updaters.add(new ServerUpdaterUExt(eventProvider, CliProviderFactoryImpl2.getCliManagerInstance(),new ICacheFactory()
            {
                @Override
                public ICache createCache(Map<String, Object> params)
                {
                    final Object cacheName = params.get(ICache.CACHENAME);

                  if (TablesTypes.PLACES.equals(cacheName))
                        return null;

                    params.put(ICache.CACHENAME, cacheName +"_"+TablesTypes.PLACES);
                    return new DerbyCache(params)
                    {
                        protected String[] getIXColNames()
                        {
                            return new String[]{TablesTypes.DATA_OBJ_ID,TablesTypes.OBJ_OSN_ID};
                        }

                    };
                }
            })
            {
                protected ColumnHeadBean[] getCacheColumns(String eventName)
                {
                    return metaProvider.getColumnsByEventName(TablesTypes.PLACES);
                }

            });
        }

        new Thread(new CascadeUpdater(updaters)).start();

        Thread.sleep(1000);
    }

}
