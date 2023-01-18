package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
import com.mwlib.tablo.db.desc.*;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.ICliProviderFactoryImpl;
import com.mwlib.tablo.cache.ICache;


import com.mwlib.tablo.derby.DerbyCache;
import com.mwlib.tablo.derby.DerbyDefaultCacheFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 15.05.15
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public class ImportTables
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
                return DriverManager.getConnection("jdbc:oracle:thin:@ora_host:ora_port:ora00", "user", "user");
            }
        });

    }

    public static void main(String[] args) throws Exception
    {

        boolean test=false;
        Directory.initDictionary(test);
        {
            Type2NameMapperAuto mapper = new Type2NameMapperAuto
                    (
              new BaseTableDesc[]{
                    new DelayABGDDesc(test),new ZMDesc(test),new KMODesc(test),
                    new LostTrDesc(test),new VagInTORDesc(test),new VIPGidDesc(test),new DelayGIDTDesc(test),
                    new WarningInTimeDesc(test),
                    new TechDesc(test),
                    new ViolationDesc(test),new RefuseDesc(test),new WindowsDesc(test), new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                    new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test),
                    new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})}
            );



            {
                EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
                IEventProvider eventProvider = new PlacesEventProvider(metaProvider);

                Map<String, Object> outParams_update = new HashMap<String, Object>();
                Map<String, ParamVal> valMap_upd = new HashMap<String, ParamVal>();
                Timestamp maxTimeStamp_update = EventProvider.getMaxTimeStamp2(outParams_update);
                {
                    maxTimeStamp_update.setTime(maxTimeStamp_update.getTime() - 10000);
                    valMap_upd.put(TablesTypes.MAX_TIMESTAMP, new ParamVal(-1, maxTimeStamp_update, Types.NULL));
                    valMap_upd.put(TablesTypes.CORTIME, new ParamVal(1, maxTimeStamp_update, Types.TIMESTAMP));
                }

                Pair<IMetaProvider, Map[]> rv = eventProvider.getUpdateTable(valMap_upd, outParams_update);

                ColumnHeadBean[] colHeaders = rv.first.getColumnsByEventName(TablesTypes.WINDOWS);


                Map<String, Object> params = new HashMap<String,Object>();
                params.put(ICache.CACHENAME, "TABLO_DATA_PLACES");
                DerbyCache cache = new DerbyCache(params);
                cache.setMeta(colHeaders);

                cache.setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME}, cache));
                cache.update(rv.second,true);
            }


            {
                EventTypeDistributer metaProvider2 = new EventTypeDistributer(mapper, test);
                IEventProvider eventProvider2 = new EventProviderTImpl(metaProvider2);
                ServerUpdaterU_WK updater01 = new ServerUpdaterU_WK(eventProvider2, ICliProviderFactoryImpl.getCliManagerInstance(),new DerbyDefaultCacheFactory())
                {
                    protected void doSomeJob(Timestamp maxTimeStamp_update,Timestamp maxTimeStamp_delete,Timestamp nextMaxTimeStamp_update,Timestamp nextMaxTimeStamp_delete)
                    {
                        setTerminate();

                        try {
                            if (connection!=null && !connection.isClosed())
                            {
                                connection.commit();
                                connection.close();
                            }
                        } catch (SQLException e) {
                            //
                        }


                    }
                };
                new Thread(updater01).start();
            }
            Thread.sleep(1000);
        }
    }
}
