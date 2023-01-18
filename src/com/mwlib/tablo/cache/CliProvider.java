package com.mwlib.tablo.cache;

import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.IKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.ICliProvider;
import com.mwlib.tablo.UpdateContainer;
import com.mwlib.tablo.db.BaseTableDesc;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 22.09.14
 * Time: 20:04
 *
 */
public class CliProvider implements ICliUpdater,
        ICliProvider
{

    protected Pair<ICache,BaseTableDesc> dataCache;
    protected  volatile boolean notFullUpdate =false;
    protected AtomicInteger queueVolume= new AtomicInteger(0);

    @Override
    public String[] getTType() {
        return tType;
    }

    private String[] tType;

    @Override
    public String getCliId() {
        return cliId;
    }

    public CliProvider(Pair<ICache,BaseTableDesc> dataCache,String cliId,String tblId,String tType)
    {
        this.dataCache=dataCache;
        this.cliId = cliId;
        this.tblId = tblId;
        this.tType=new String[]{tType};
    }


    public CliProvider(Pair<ICache,BaseTableDesc> dataCache,String cliId,String tblId,String[] tType)
    {
        this.dataCache=dataCache;
        this.cliId = cliId;
        this.tblId = tblId;
        this.tType=tType;
    }

    private CliProvider()
    {
        this.cliId = cliId;
        this.tblId = tblId;
        this.tType=tType;
    }

    @Override
    public Object[][] getTuplesByParameters(Map<String, Object> parameters, UpdateContainer containerParams) throws CacheException {
        return this.dataCache.first.getTuplesByParameters(parameters, containerParams);
    }

    @Override
    public Object[] getTupleByKey(Object key) throws CacheException
    {
          return dataCache.first.getTupleByKey(key);
    }

    @Override
    public Set<Object> getAllDataKeys() throws CacheException
    {
        return dataCache.first.getAllDataKeys();
    }

    @Override
    public String getColNameByIx(int ix) {
        return dataCache.first.getColNameByIx(ix);
    }

    @Override
    public Integer getIxByColName(String colName)
    {
        return dataCache.first.getIxByColName(colName);
    }


    @Override
    public ColumnHeadBean[] getMeta()
    {
        return dataCache.first.getMeta();
    }

    private long[] lg={-1};

    @Override
    public int getCliCnt() {
        return ++cliCnt;
    }


    @Override
    public String getTblId() {
        return tblId;  //To change body of implemented methods use File | Settings | File Templates.
    }




    private int cliCnt = START_CLI_POS_CNT;
    private String cliId;
    private String tblId;
    private int srvCnt = TablesTypes.START_POS;

    @Override
    public UpdateContainer getNewDataKeys(Map<String, Object> parameters) throws CacheException
    {
        boolean notFullUpdate=this.notFullUpdate;
        this.notFullUpdate =true;
        notFullUpdate = isNotFullRequest(parameters, notFullUpdate);

        //1.Произвести слияние очереди
        //2.Сформировать и вернуть изменененные кортежи
        //3.Не забыть про специальны случаи первого запроса и полного обновления (флаг notFullUpdate сброшен при первом запросе)

        if (!notFullUpdate)
        {
            queue.clear();//Сброс очереди передаем все значения!!!
            cliCnt= START_CLI_POS_CNT;//Полностью обновиться для клиента

            Set<Object> keys=getAllDataKeys();
            HashMap<Object, long[]> rv=new HashMap<>();
            for (Object key : keys)
                rv.put(key,lg);
            return new UpdateContainer(rv);
        }
        UpdateContainer keyInData2Updates=queue.poll();

        if (keyInData2Updates!=null)
        {
            keyInData2Updates=new UpdateContainer(keyInData2Updates);//Создаем копию для того что бы не влиять на другие клиентские провайдеры при формировании обновления на клиент

            UpdateContainer data2Updates;
            while ((data2Updates=queue.poll())!=null)
                merge(keyInData2Updates,data2Updates);

            queueVolume.set(0);
            return keyInData2Updates;
        }
        queueVolume.set(0);
        return new UpdateContainer();  //Реализовать это, обесепечив возврат объектов, а как передавать эти объекты на браузер - это уровень транспорта....
    }


    protected void merge(UpdateContainer ixInData2Updates,UpdateContainer update)
    {
        Set<Object> keys = update.dataRef.keySet();
        for (Object key : keys)
        {
            long[] lnIn=ixInData2Updates.dataRef.get(key);
            if (lnIn==null)
            {
                if (!ixInData2Updates.dataRef.containsKey(key))
                    ixInData2Updates.dataRef.put(key, update.dataRef.get(key));
                else //TODO может ли быть такое что у нас ключ удалился потом опять восстановился
                {
//                    ixInData2Updates.put(key, update.get(key));
                    System.out.println("Restore key while merge operation:"+key);
                }
            }
            else
            {
                long[] lnEx= update.dataRef.get(key);
                if (lnEx==null)
                    ixInData2Updates.dataRef.put(key, null);
                else
                {
                    for (int i = 0; i < lnEx.length; i++)
                        lnIn[i]|= lnEx[i];
                    ixInData2Updates.dataRef.put(key, lnIn);
                }
            }
        }

        //Cлияние параметров
        ixInData2Updates.paramRef.putAll(update.paramRef);
    }


    protected boolean isNotFullRequest(Map<String, Object> parameters, boolean fUpdate) throws CacheException
    {

        BaseTableDesc tblDesc = this.getTableDesc(parameters);
        fUpdate=fUpdate && (!tblDesc.shouldResetData(parameters));//Если необходимо сбросить данные, тогда мы должны установить флаг в TODO !!!FALSE!!!!

        String[] sCnts= (String[]) parameters.get(TablesTypes.ID_REQN);
        if (sCnts!=null && sCnts.length>0 && sCnts[0]!=null)
        {
            try
            {
                int srvCnt=Integer.parseInt(sCnts[0]);
                fUpdate=((srvCnt-this.srvCnt)>0) && fUpdate;
                this.srvCnt =srvCnt;
            }
            catch (NumberFormatException e)
            {//

            }
        }
        return fUpdate;
    }

    @Override
    public BaseTableDesc getTableDesc(Map<String, Object> parameters) throws CacheException {
        return dataCache.second;
    }

    @Override
    public String[] getKeyCols()
    {
        IKeyGenerator gen = dataCache.first.getKeyGenerator();
        if (gen!=null)
            return gen.getKeyCols();
        return new String[0];
    }

    @Override
    public int getCliCurrCnt() {
        return cliCnt;
    }


    public int getQueueVolume()
    {
        return queueVolume.get();
    }



    @Override
    public void updateData(UpdateContainer dataRef) throws CacheException
    {
            if (notFullUpdate)
            {
                queue.add(dataRef);
                queueVolume.addAndGet(dataRef.dataRef.size());
            }
    }

    private Queue< UpdateContainer > queue=new ConcurrentLinkedQueue< UpdateContainer >();

    //Здесь реализовать сл. алгоритм разраничения доступа
    //Если при апдейте обнаружено, что есть не переданные данные необходимо слить их в потоке обновления кеща?
    //или сливать их в потоке клиента, при этом после передачи проверять что у нас очередь пуста?

    /*
        TODO Какие вопросы могут быть заданы?
        1. что дешевле, вгребать данные из кеша (занимая при этом процессороное время) или передать их в клиент вместе с масками апдейта?
        2. Стенд и режимы?

    */




    public static void main(String[] args) throws CacheException
    {
        CliProvider cliProvider = new CliProvider();
        cliProvider.notFullUpdate =true;

        long[] lg=new long[]{-1};
        Map map=new HashMap();

        map.put("1",lg);
        map.put("2",lg);
        map.put("3",lg);
        cliProvider.updateData(new UpdateContainer(map));


        map=new HashMap();
        map.put("1",null);
        map.put("6",lg);

        cliProvider.updateData(new UpdateContainer(map));

        map.put("1",lg);
        map.put("4",lg);
        map.put("5",lg);

        cliProvider.updateData(new UpdateContainer(map));

        map=new HashMap();
        UpdateContainer keyInData2Updates=cliProvider.queue.poll();

        if (keyInData2Updates!=null)
        {
            UpdateContainer data2Updates;
            while ((data2Updates=cliProvider.queue.poll())!=null)
                cliProvider.merge(keyInData2Updates, data2Updates);

        }

        System.out.println("keyInData2Updates = " + keyInData2Updates);

    }

}
