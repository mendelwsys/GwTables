package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.ICliProviderFactoryImpl;

import com.mwlib.tablo.db.desc.RefuseDesc;
import com.mwlib.tablo.db.desc.ViolationDesc;
import com.mwlib.tablo.db.desc.WarningDesc;
import com.mwlib.tablo.db.desc.WindowsDesc;
import com.mwlib.tablo.derby.DerbyDefaultCacheFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 07.05.15
 * Time: 17:33
 * Проверка кеша на конситентость при работе с двумя провайдерами данных, по идее кеш всегда должен содржать данные из первого провайдера
 * и никогда !!ТОЛЬКО!! данные из нулевого.
 */
public class TestUServerUpdater_WK
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
                return DriverManager.getConnection("jdbc:oracle:thin:@ora_host:ora_port:ora_test", "user", "user");
            }
        });



        boolean test=false;
        Directory.initDictionary(test);
        {
            Type2NameMapperAuto mapper = new Type2NameMapperAuto(
              new BaseTableDesc[]{
//                    new DelayABGDDesc(test),new ZMDesc(test),new KMODesc(test),
//                    new LostTrDesc(test),new VagInTORDesc(test),new VIPGidDesc(test),new DelayGIDTDesc(test),
//                    new WarningInTimeDesc(test),
//                    new TechDesc(test),
                    new ViolationDesc(test),new RefuseDesc(test),new WindowsDesc(test), new WindowsDesc(test, TablesTypes.WINDOWS_CURR,new int[]{46,57,60}),
                    new WindowsDesc(test,TablesTypes.WINDOWS_OVERTIME,new int[]{61}),new WarningDesc(test),
                      new WarningDesc(test,TablesTypes.WARNINGS_NP,new int[]{79})}
            );

            EventTypeDistributer metaProvider = new EventTypeDistributer(mapper, test);
            IEventProvider eventProvider = new EventProviderTImpl(metaProvider);


            ServerUpdaterU_WK updater01 = new ServerUpdaterU_WK(eventProvider, ICliProviderFactoryImpl.getCliManagerInstance(),new DerbyDefaultCacheFactory())
            {
                int x=0;
                protected void doSomeJob(Timestamp maxTimeStamp_update,Timestamp maxTimeStamp_delete,Timestamp nextMaxTimeStamp_update,Timestamp nextMaxTimeStamp_delete)
                {
                    final int sizeUpdate = suspectedUpdateKeys.size();
                    final int sizeDelete = suspectedDelKeys.size();
                    final int sizeVal = suspectedValKeys.size();
                    if (sizeUpdate >0 || sizeDelete >0 || sizeVal>0)
                    {
                        x++;
                        System.out.println("\n\n\n\n??????????????????????? Check Begin consistency RESULSTS ???????????????????????");

                        System.out.println("maxTimeStamp_update = " + maxTimeStamp_update+" "+"maxTimeStamp_delete = " + maxTimeStamp_delete+" "+"nextMaxTimeStamp_update = " + nextMaxTimeStamp_update+" "+"nextMaxTimeStamp_delete = " + nextMaxTimeStamp_delete);
                        if (sizeVal>0)
                            System.out.println("suspectedValKeys = " + suspectedValKeys);
                        if (sizeUpdate>0)
                            System.out.println("suspectedValKeys = " + suspectedUpdateKeys);
                        if (sizeDelete>0)
                            System.out.println("suspectedValKeys = " + suspectedDelKeys);
//                        System.out.println("sizeVal = " + sizeVal+" sizeDelete= "+ sizeDelete+" sizeUpdate="+ sizeUpdate);

                        System.out.println("??????????????????????? Check END consistency RESULSTS ???????????????????????\n\n\n\n");
                    }
                    else
                        x=0;
                    if (x>1)
                    {
                        System.out.println("\n\n\n\n!!!!!!!!!!!!!  Check Begin consistency RESULSTS !!!!!!!!!!!!! ");
                        System.out.println("maxTimeStamp_update = " + maxTimeStamp_update+" "+"maxTimeStamp_delete = " + maxTimeStamp_delete+" "+"nextMaxTimeStamp_update = " + nextMaxTimeStamp_update+" "+"nextMaxTimeStamp_delete = " + nextMaxTimeStamp_delete);
                        System.out.println("!!!!!!!!!!!!! Check END consistency RESULSTS !!!!!!!!!!!!!\n\n\n\n");
                    }
                }
            };
            new Thread(updater01).start();
            Thread.sleep(1000);
        }

    }
}
