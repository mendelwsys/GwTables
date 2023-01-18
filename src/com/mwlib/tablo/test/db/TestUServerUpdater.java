package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.EventTypeDistributer;
import com.mwlib.tablo.db.Type2NameMapperAuto;
import com.mwlib.tablo.db.desc.WarningDesc;
import com.mwlib.tablo.db.desc.WindowsDesc;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.ICliProviderFactoryImpl;


import com.mwlib.tablo.db.ServerUpdaterU;
import com.mwlib.tablo.derby.DerbyDefaultCacheFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 19.11.14
 * Time: 14:09
 * Пример теста для серверного апдейта
 */
public class TestUServerUpdater
{
    public static void main(String[] args) throws Exception
    {

        DbUtil.name2Connector.put(DbUtil.DS_JAVA_CACHE_NAME,new DbUtil.IJdbCOnnection()
        {
            @Override
            public Connection getConnection() throws ClassNotFoundException, SQLException
            {
                return DriverManager.getConnection("jdbc:derby:C:/PapaWK/Projects/JavaProj/SGWTVisual2/db/udb3;create=true");
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
                      new WindowsDesc(test), new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                    new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test),
                      new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})}
            );

//            Type2NameMapperAuto mapper = new Type2NameMapperAuto(new BaseTableDesc[]
//            {
//               new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test),new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})
//            });

            EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
            TotalEventProvider eventProvider = new TotalEventProvider(metaProvider);
            ServerUpdaterU updater01 = new ServerUpdaterU(eventProvider, ICliProviderFactoryImpl.getCliManagerInstance(),new DerbyDefaultCacheFactory())
            {
                protected void doSomeJob()
                {
                    this.setTerminate();
                }

            };
//            updater01.run();
            new Thread(updater01).start();
            Thread.sleep(1000);
        }

    }
}
