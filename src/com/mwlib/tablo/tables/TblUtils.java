package com.mwlib.tablo.tables;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mwlib.tablo.*;
import com.mwlib.tablo.cache.CliProvider;
import com.mwlib.tablo.cache.ICliUpdater;
import com.mycompany.common.JT2ID;
import com.mycompany.common.TablesTypes;

import com.mwlib.tablo.db.BaseTableDesc;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.10.14
 * Time: 12:28
 * Утитлиты таблиц.
 */
public class TblUtils
{
//TODO 13.01.2015 (Для удаления)
//    public static DataSender2 createDataSender(Map params) throws Exception
//    {
//        ICliProviderFactory providerFactoryInstance = ICliProviderFactoryImpl.getProviderFactoryInstance();
//        ICliProvider provider = providerFactoryInstance.getProvider(params); //TODO по идее здесь провайдер уже относится только к запрашиваемой таблице
//        Map<Object, long[]> newKeys = provider.getNewDataKeys(params);
//        System.out.println("size of new keys:" + newKeys.size());
//
//        boolean isFirstRequest=(provider.getCliCurrCnt()==CliProvider.START_CLI_POS_CNT);
//
////        if (false)
////        {
////            int k=10;
////            Map<Object, long[]> _newKeys = new HashMap<Object, long[]>();
////            for (Object key : newKeys.keySet()) {
////                    if (k ==0)
////                        break;
////                _newKeys.put(key,newKeys.get(key));
////                k--;
////            }
////            newKeys=_newKeys;
////        }
//
//        Map[] chs = new Map[newKeys.size()]; //!!!TODO - обязательно заменить на массивы!!!! Очень много лишней информации прет!!!!
////        Map<String, ColumnHeadBean> mapMeta = new HashMap<String, ColumnHeadBean>();
////        {
////            ColumnHeadBean[] meta = provider.getMeta();
////            for (ColumnHeadBean aMeta : meta)
////                mapMeta.put(aMeta.getName(), aMeta);
////        }
//
//        int ix = 0;
//        Set<String> sendMask=new HashSet();
//        for (Object key : newKeys.keySet())
//        {
//            sendMask.clear();
//
//            chs[ix] = new HashMap();
//            Object[] o = provider.getTupleByKey(key);
//            if (o!=null)
//            {
//                long [] changed=newKeys.get(key);
//                for (int ixCol = 0; ixCol < o.length; ixCol++)
//                {
//                    String colNameByIx = provider.getColNameByIx(ixCol);
//
//                    if (isFirstRequest)
//                       sendMask.add(colNameByIx);
//                    else
//                    {
//                        long mask=1l<<(ixCol%Long.SIZE);
//                        if ((changed[ixCol/Long.SIZE]&mask)!=0)
//                            sendMask.add(colNameByIx);
//                    }
//
//
//                    if (!isFirstRequest || o[ixCol]!=null)
//                        chs[ix].put(colNameByIx, o[ixCol]);
//                }
//                chs[ix] = provider.getTableDesc(params).translateTuple(chs[ix], provider.getMeta(), sendMask);
//            }
//            else //Передадим в кортеже ключ который надо удалить
//               chs[ix].put(TablesTypes.KEY_FNAME,key);
//
//            if (o==null || newKeys.get(key) == null)
//                chs[ix].put(TablesTypes.ACTUAL, 0);//
//
//            ix++;
//        }
//
//        DataSender2 dataSender2 = new DataSender2();
//        dataSender2.setTuples(chs);
//        dataSender2.setCliCnt(provider.getCliCnt());
//        dataSender2.setTblId(provider.getTblId());
//        return dataSender2;
//    }



//TODO 13.01.2015 (Для удаления)
//    public static DataSender2 createDataSender2(Map params) throws Exception
//    {
//        ICliProviderFactory providerFactoryInstance = IDerbyCliProviderFactoryImpl.getProviderFactoryInstance(); //TODO после отладки необходимо сделать рефакторинг для комбинации таблиц
//        ICliProvider provider = providerFactoryInstance.getProvider(params); //TODO по идее здесь провайдер уже относится только к запрашиваемой таблице
//
//        Map<Object, long[]> newKeys = provider.getNewDataKeys(params);
//        System.out.println("size of new keys:" + newKeys.size());
//
//        List<Map> chs = new LinkedList<Map>(); //!!!TODO - обязательно заменить на массивы!!!! Очень много лишней информации прет!!!!
//        for (Object key : newKeys.keySet())
//        { //Сначала удаленные
//            if (newKeys.get(key)==null)
//            {
//               Map tuple=new HashMap();
//                   tuple.put(TablesTypes.KEY_FNAME, key);
//                   tuple.put(TablesTypes.ACTUAL, 0);//
//               chs.add(tuple);
//            }
//        }
//
//        { //Апосля все остальные (TODO Теоретически можно было бы посылать согласно картам, но тогда может возникнуть не консистентоность тапла, псокольку мы подняли уже измененые таплы, а в маске к данному ключу может еще не дойти)
//
//            Object[][] objects=provider.getTuplesByParameters(params);
//            for (Object[] o : objects)
//            {
//                Map tuple=new HashMap();
//                for (int ixCol = 0; ixCol < o.length; ixCol++)
//                {
//                    String colNameByIx = provider.getColNameByIx(ixCol);
//                    tuple.put(colNameByIx, o[ixCol]);
//                }
//                chs.add(provider.getTableDesc(params).translateTuple(tuple, provider.getMeta(), null));
//            }
//        }
//
//        DataSender2 dataSender2 = new DataSender2();
//        dataSender2.setTuples(chs.toArray(new Map[chs.size()]));
//        dataSender2.setCliCnt(provider.getCliCnt());
//        dataSender2.setTblId(provider.getTblId());
//        return dataSender2;
//    }


    public static DataSender4 createDataSender(Map params) throws Exception
    {
        ICliProviderFactory providerFactoryInstance = ICliProviderFactoryImpl.getProviderFactoryInstance();
        ICliProvider[] providers = providerFactoryInstance.getProvider(params); //TODO по идее здесь провайдер уже относится только к запрашиваемой таблице

        ICliProvider provider=providers[0];

        Map<Object, long[]> newKeys = provider.getNewDataKeys(params).dataRef;
        System.out.println("size of new keys:" + newKeys.size());

        boolean isFirstRequest=(provider.getCliCurrCnt()== CliProvider.START_CLI_POS_CNT);

//        if (false)
//        {
//            int k=10;
//            Map<Object, long[]> _newKeys = new HashMap<Object, long[]>();
//            for (Object key : newKeys.keySet()) {
//                    if (k ==0)
//                        break;
//                _newKeys.put(key,newKeys.get(key));
//                k--;
//            }
//            newKeys=_newKeys;
//        }

        Map[] chs = new Map[newKeys.size()]; //!!!TODO - обязательно заменить на массивы!!!! Очень много лишней информации прет!!!!
//        Map<String, ColumnHeadBean> mapMeta = new HashMap<String, ColumnHeadBean>();
//        {
//            ColumnHeadBean[] meta = provider.getMeta();
//            for (ColumnHeadBean aMeta : meta)
//                mapMeta.put(aMeta.getName(), aMeta);
//        }

        int ix = 0;
        Set<String> sendMask=new HashSet();
        for (Object key : newKeys.keySet())
        {
            sendMask.clear();

            chs[ix] = new HashMap();
            Object[] o = provider.getTupleByKey(key);
            if (o!=null)
            {
                long [] changed=newKeys.get(key);

                if (changed==null)
                {
                    System.out.println("newKeys generator error changed is null (WAS BUG in newKey performance)");
                }
                else
                {
                    for (int ixCol = 0; ixCol < o.length; ixCol++)
                    {
                        String colNameByIx = provider.getColNameByIx(ixCol);

                        if (isFirstRequest)
                           sendMask.add(colNameByIx);
                        else
                        {
                            long mask=1l<<(ixCol%Long.SIZE);
                            if ((changed[ixCol/Long.SIZE]&mask)!=0)
                                sendMask.add(colNameByIx);
                        }


                        if (!isFirstRequest || o[ixCol]!=null)
                            chs[ix].put(colNameByIx, o[ixCol]);
                    }
                    chs[ix] = provider.getTableDesc(params).translateTuple(chs[ix], provider.getMeta(), sendMask);
                }
            }
            else //Передадим в кортеже ключ который надо удалить
               chs[ix].put(TablesTypes.KEY_FNAME,key);

            if (o==null || newKeys.get(key) == null)
                chs[ix].put(TablesTypes.ACTUAL, 0);//

            ix++;
        }

//        DataSender4 dataSender4 =
//        dataSender4.setTuples(chs);
//        dataSender4.setCliCnt(provider.getCliCnt());
//        dataSender4.setTblId(provider.getTblId());

        return new DataSender4(chs,new TransactSender(provider.getCliCnt(),provider.getTblId(),System.currentTimeMillis()),null);
    }

    public static DataSender4 createDataSender4(Map params) throws Exception
    {
        ICliProviderFactory providerFactoryInstance = IDerbyCliProviderFactoryImpl.getProviderFactoryInstance(); //TODO после отладки необходимо сделать рефакторинг для комбинации таблиц
        ICliProvider[] providers = providerFactoryInstance.getProvider(params); //TODO по идее здесь провайдер уже относится только к запрашиваемой таблице

        ICliProvider provider=providers[0];

        final UpdateContainer updateContainer = provider.getNewDataKeys(params);
        Map<Object, long[]> newKeys = updateContainer.dataRef;
        System.out.println("size of new keys:" + newKeys.size());

        List<Map> chs = new LinkedList<Map>(); //!!!TODO - обязательно заменить на массивы!!!! Очень много лишней информации прет!!!!
        for (Object key : newKeys.keySet())
        { //Сначала удаленные
            if (newKeys.get(key)==null)
            {
               Map tuple=new HashMap();
                   tuple.put(TablesTypes.KEY_FNAME, key);
                   tuple.put(TablesTypes.ACTUAL, 0);//
               chs.add(tuple);
            }
        }

        { //Апосля все остальные (TODO Теоретически можно было бы посылать согласно картам, но тогда может возникнуть не консистентоность тапла, псокольку мы подняли уже измененые таплы, а в маске к данному ключу может еще не дойти)

            Object[][] objects=provider.getTuplesByParameters(params, updateContainer);
            for (Object[] o : objects)
            {
                Map tuple=new HashMap();
                for (int ixCol = 0; ixCol < o.length; ixCol++)
                {
                    String colNameByIx = provider.getColNameByIx(ixCol);
                    tuple.put(colNameByIx, o[ixCol]);
                }
                chs.add(provider.getTableDesc(params).translateTuple(tuple, provider.getMeta(), null));
            }
        }

        return new DataSender4(chs.toArray(new Map[chs.size()]),new TransactSender(provider.getCliCnt(),provider.getTblId(),System.currentTimeMillis()),null);
    }


    public static DataSender4 createDataSender5(Map params) throws Exception
    {
        return createDataSender5(params,IDerbyCliProviderFactoryImpl.getProviderFactoryInstance());
    }


    public static DataSender4 createDataSender5(Map params,ICliProviderFactory providerFactoryInstance) throws Exception
    {
        final long currentTimeStamp = System.currentTimeMillis();
        params.put(TablesTypes.CURRENT_TIMESTAMP, new long[]{currentTimeStamp});

//        boolean transDeleted=!params.containsKey(TablesTypes.NO_TRANS_DELETED);

//        ICliProviderFactory providerFactoryInstance = IDerbyCliProviderFactoryImpl.getProviderFactoryInstance(); //TODO после отладки необходимо сделать рефакторинг для комбинации таблиц
        ICliProvider[] providers = providerFactoryInstance.getProvider(params); //TODO по идее здесь провайдер уже относится только к запрашиваемой таблице


        List<Map> chs = new LinkedList<Map>(); //!!!TODO - обязательно заменить на массивы!!!! Очень много лишней информации прет!!!!

        int cliCnt=-1;

        JT2ID[] tids=new JT2ID[providers.length];

        for (int i = 0; i < providers.length; i++)
        {
            ICliProvider provider= providers[i];

            final UpdateContainer updateContainer = provider.getNewDataKeys(params);
            if (updateContainer!=null)
            {
                Map<Object, long[]> newKeys = updateContainer.dataRef;
                System.out.println("size of new keys:" + newKeys.size());
                for (Object key : newKeys.keySet())
                { //Сначала удаленные
                    if (newKeys.get(key)==null)
                    {
                       Map tuple=new HashMap();
                           tuple.put(TablesTypes.KEY_FNAME, key);
                           tuple.put(TablesTypes.ACTUAL, 0);//
                       chs.add(tuple);
                    }
                }
            }

            { //Апосля все остальные (TODO Теоретически можно было бы посылать согласно картам, но тогда может возникнуть не консистентоность тапла, псокольку мы подняли уже измененые таплы, а в маске к данному ключу может еще не дойти)

                Object[][] objects=provider.getTuplesByParameters(params, updateContainer);
                for (Object[] o : objects)
                {
                    Map tuple=new HashMap();
                    for (int ixCol = 0; ixCol < o.length; ixCol++)
                    {
                        String colNameByIx = provider.getColNameByIx(ixCol);
                        tuple.put(colNameByIx, o[ixCol]);
                    }
                    final BaseTableDesc tableDesc = provider.getTableDesc(params);
                    chs.add(tableDesc.translateTuple(tuple, provider.getMeta(), null));
                }
            }
            cliCnt=Math.max(cliCnt,provider.getCliCnt());

            if (!params.containsKey(TablesTypes.JT2ID))
                return new DataSender4(chs.toArray(new Map[chs.size()]),new TransactSender(cliCnt,provider.getTblId(),currentTimeStamp),null);
            else
                tids[i]=new JT2ID(((ICliUpdater)provider).getTType()[0],provider.getTblId());
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        return new DataSender4(chs.toArray(new Map[chs.size()]),new TransactSender(cliCnt,gson.toJson(tids),currentTimeStamp),null);
    }


}
