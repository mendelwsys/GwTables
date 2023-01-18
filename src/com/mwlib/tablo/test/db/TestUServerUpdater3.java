package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.*;
import com.mwlib.tablo.db.desc.RefuseDesc;
import com.mwlib.tablo.db.desc.ViolationDesc;
import com.mwlib.tablo.db.desc.WarningDesc;
import com.mwlib.tablo.db.desc.WindowsDesc;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.ICliProviderFactoryImpl;
import com.mwlib.tablo.cache.DefaultCacheFactory;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICacheFactory;


import com.mwlib.tablo.derby.DerbyCache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 07.05.15
 * Time: 17:33
 * Проверка кеша на конситентость при работе с двумя провайдерами данных, по идее кеш всегда должен содржать данные из первого провайдера
 * и никогда !!ТОЛЬКО!! данные из нулевого.
 */
public class TestUServerUpdater3
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

            EventTypeDistributer metaProvider1 = new EventTypeDistributer(mapper, test);
            EventTypeDistributer metaProvider2 = new EventTypeDistributer(mapper, test);
//            IEventProvider[] eventProviders = new IEventProvider[]{new PlacesEventProvider(metaProvider1),new EventProviderTImpl(metaProvider2)};
            IEventProvider[] eventProviders = new IEventProvider[]{new EventProviderTImpl(metaProvider2)};

            ServerUpdaterU3.ProvidersWrapper[] eventProviderWrappers = new ServerUpdaterU3.ProvidersWrapper[eventProviders.length];
            for (int i = 0, eventProvidersLength = eventProviders.length; i < eventProvidersLength; i++)
            {
                eventProviderWrappers[i]=new ServerUpdaterU3.ProvidersWrapper(eventProviders[i]);
                eventProviderWrappers[i].cacheFactory= new DefaultCacheFactory();
            }
            eventProviderWrappers[eventProviders.length-1].setMaster(true);

            final List<DerbyCache> ll=new LinkedList<DerbyCache>();

            ServerUpdaterU3 updater01 = new ServerUpdaterU3(eventProviderWrappers, ICliProviderFactoryImpl.getCliManagerInstance(),new ICacheFactory()
            {
                @Override
                public ICache createCache(Map<String, Object> params)
                {
                    final DerbyCache derbyCache;
                    ll.add(derbyCache = new DerbyCache(params));
                    return derbyCache;
                }
            })
            {
                protected void doSomeJob()
                {
                    final int sizeUpdate = suspectedUpdateKeys.size();
                    final int sizeDelete = suspectedDelKeys.size();
                    final Map<Object,Pair<Map,Integer>> sizeVal = suspectedValKeys;
                    if (sizeUpdate >0 || sizeDelete >0 || sizeVal.size()>0)
                    {
                        System.out.println("\n\n\n\n??????????????????????? Check Begin consistency RESULSTS ???????????????????????");
                        System.out.println("sizeVal = " + sizeVal+" sizeDelete= "+ sizeDelete+" sizeUpdate="+ sizeUpdate);
                        System.out.println("??????????????????????? Check END consistency RESULSTS ???????????????????????\n\n\n\n");
                    }
                }

//                protected void doSomeJob()
//                {
//                    System.out.print("Start check consistency ...");
//                    long lg=System.currentTimeMillis();
//
//                    for (DerbyCache derbyCache : ll)
//                    {
//                        try
//                        {
//                            ColumnHeadBean[] meta = derbyCache.getMeta();
//
//                            int ix=-1;
//                            int ix2=-1;
//                            for (int i = 0, metaLength = meta.length; i < metaLength; i++)
//                            {
//                                ColumnHeadBean columnHeadBean = meta[i];
//                                if (columnHeadBean.getName().equals(ServerUpdaterU2.PROV + "_0"))
//                                {
//                                    ix2 = i;
//                                }
//                                else
//                                if (columnHeadBean.getName().equals(ServerUpdaterU2.PROV + "_1"))
//                                {
//                                    ix = i;
//                                }
//                                if (ix>=0 && ix2>=0)
//                                    break;
//                            }
//
//                            if (ix>=0)
//                            {
//                                Object[][] res=derbyCache.getTuplesByParameters(new HashMap());
//                                for (Object[] re : res)
//                                {
//                                    if (!new Integer(1).equals(re[ix]))
//                                        System.out.print("!!!Found error in cache!!! .... ");
////                                    else if (ix2>=0)
////                                    {
////                                        if (!new Integer(1).equals(re[ix2]))
////                                            System.out.print ("!!!Found event without place!!! ");
////                                    }
//                                }
//
//                            }
//
//
//                        }
//                        catch (CacheException cacheExcpetion)
//                        {
//                            cacheExcpetion.printStackTrace();
//                        }
//                    }
//                    System.out.println(" time of check consistency = "+(System.currentTimeMillis()-lg)/1000+" sec");
//                }
            };
            new Thread(updater01).start();
            Thread.sleep(1000);
        }

    }
}
