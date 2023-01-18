package com.mwlib.tablo.test;

import com.mwlib.tablo.ICliProvider;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.UpdateContainer;
import com.mwlib.tablo.cache.Cache;
import com.mwlib.tablo.cache.CliProvider;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.WrongParam;
import com.mwlib.tablo.test.tables.BaseTable;
import com.mwlib.tablo.test.tables.WindowsT;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.09.14
 * Time: 17:49
 * Этот класс и будет запускать обновления таблиц и выдавть клиентские провайдеры,
 * а так же очищать клиентские объекты если таковые долго не вызываеются
 */
public class ICliProviderFactoryImpl0
        implements ICliProviderFactory
{
    //private ConcurrentHashMap<String,ICliProvider> allocatedProvider = new ConcurrentHashMap<>();
    public static ICliProviderFactoryImpl0 cliProviderFactory=new ICliProviderFactoryImpl0();



    public static ICliProviderFactory getInstance()
    {
        return cliProviderFactory;
    }



    private BaseTable[] tables;
    private boolean terminate =false;
    private Map<String,Integer> tname2ix= new ConcurrentHashMap<>();
    private ICache[] caches;

    private Thread updatethread;


    private boolean test=true;

    private Map<String,Map<String, CliProvider>> sess2Providers =new ConcurrentHashMap<String,Map<String,CliProvider>>();

    private ICliProviderFactoryImpl0()
    {
        try
        {
            tables = new BaseTable[]
                    {
                            WindowsT.getInstance(test)
                    };

           caches= new ICache[tables.length];
           updatethread=new Thread()
           {

               Map[] data;

               @Override
               public void run()
               {
                   int k=0;

                   while(!terminate)
                   {
                       long ln=System.currentTimeMillis();

                       for (int i = 0, tablesLength = tables.length; i < tablesLength; i++)
                       { //Это по идее главный цикл обновления кэшей таблиц.
                           String tableType = tables[i].getTableType();
                           try
                           {
                               if (caches[i]==null)
                               {
                                   caches[i]=new Cache();

                                   ColumnHeadBean[] meta = tables[i].getMeta();
                                   caches[i].setMeta(meta);
                                   caches[i].setKeyGenerator(new SimpleKeyGenerator(new String[]{TablesTypes.KEY_FNAME},caches[i]));

                                   tname2ix.put(tableType,i);
                               }
                               //TODO в тестовом варианте необходимо как-то смоделировать обновления, что бы отладить
                               //передачу данных на клиента

                               Map[] data1=null;
                               if (!test  || data==null )
                                data1=tables[i].getData(new HashMap(), new HashMap());
                               if (data==null)
                                   data=data1;

                               else if (test)
                               {
                                   data1=new Map[3];
                                   for (int j=0;j<3;j++)
                                   {
                                       int ix=(int)Math.round(Math.random()*(data.length-1));
                                       data1[j]=data[ix];
                                   }
                               }

                               Map<Object, long[]> res = caches[i].update(data1, true).dataRef;
                               for (Map<String, CliProvider> cliProviderMap : sess2Providers.values())
                               {

                                       CliProvider cliProvider = cliProviderMap.get(tableType);
                                        if (cliProvider!=null)
                                            cliProvider.updateData(new UpdateContainer(res)); //Проадейтить по всем сессиям
                               }
                           }
                           catch (Exception e)
                           {
                               System.err.println("Error update for table:"+ tableType);
                               e.printStackTrace();
                           }
                       }

                       ln=System.currentTimeMillis()-ln;

                       try
                       {
                           if (!test)
                               sleep(Math.max(3*1000-ln,100));
                           else
//                           if (k<4)
                               sleep(10*1000);//10 second
//                           else
//                               break;
                       }
                       catch (InterruptedException e)
                       {//
                       }
                       k++;
                   }
               }
           };
           updatethread.start();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public ICliProvider[] getProvider(Map parameters) throws WrongParam
    {
        String cliId = getParamter(parameters, CLIID);
        if (cliId==null)
            throw  new WrongParam("client id can't be null");

        String tname=getParamter(parameters,TablesTypes.TTYPE);
        if (tname==null)
            throw new WrongParam("table Name can't be null");

        Map<String,CliProvider> cliProviderMap = sess2Providers.get(cliId);

        if (cliProviderMap==null)
        {
            cliProviderMap=new ConcurrentHashMap<>();
            sess2Providers.put(cliId,cliProviderMap);
        }

        CliProvider rv =cliProviderMap.get(tname);
        if (rv==null)
        {
            Integer ix = tname2ix.get(tname);
            if (ix==null)
                throw new WrongParam("table "+tname+" unknown");
            //cliProviderMap.put(tname,rv=new CliProvider(new Pair<ICache,BaseTableDesc>(caches[ix],null)));
        }
        return new ICliProvider[]{rv};
    }

    @Override
    public void addNotSessionCliIds(String clId) {
    }

    @Override
    public void removeNotSessionCliIds(String clId) {
    }

    private String getParamter(Map parameters, String cliid) {
        String[] arCliId=(String[])parameters.get(cliid);
        String cliId=null;
        if (arCliId!=null && arCliId.length==1)
            cliId=arCliId[0];
        return cliId;
    }

    public void terminate() {
        terminate=true;
    }

}
