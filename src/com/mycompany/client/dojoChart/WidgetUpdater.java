package com.mycompany.client.dojoChart;

import com.mycompany.client.operations.ICliFilter;
import com.mycompany.client.updaters.DataDSCallback;
import com.mycompany.client.updaters.MetaAnalitDSCallBack;
import com.mycompany.client.utils.DSRegisterImpl;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.FieldException;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.*;
import com.smartgwt.client.data.*;
//
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 01.02.15
 * Time: 19:44
 * Апдейтер для графиков
 */
public class WidgetUpdater extends DSRegisterImpl
{
    ICliFilter sumFilter =new ICliFilter() //Помимо данных идут еще консолидационные данные, которые получаются на сервере, а не на клиенте
    {
        @Override
        public void setCriteria(Criteria cr) {

        }

        @Override
        public Record[] filter(Record[] records)
        {
            List<Record> rv = new LinkedList<Record>();
            for (Record record : records)
            {
                String dor_code=record.getAttribute(TablesTypes.DOR_CODE);
                String vid_id = record.getAttribute("VID_ID");
                String pred_id=record.getAttribute("PRED_ID");

                if (dor_code!=null && dor_code.equals(TablesTypes.DOR_CODE_4_DELAY_TRAINS_SUM_TOTAL))
                {
                }
                else
                if (dor_code!=null && vid_id!=null && vid_id.equals("-1"))
                {
                }
                else if (dor_code!=null && pred_id!=null && pred_id.contains(TablesTypes.HIDE_ATTR))
                {
                }
                else
                    rv.add(record);
            }
            return rv.toArray(new Record[rv.size()]);
        }

        @Override
        public Criteria getCriteria() {
            return null;
        }
    };



    public Pair<DSCallback, MyDSCallback> initChartUpdater(final CalcManager manager,String headerURL, final String dataURL, int period)
    {

        DSCallback headerCallBack = initMetaDataUpdater(manager, headerURL);
        MyDSCallback dataCallBack = initDataUpdater(manager, period);
        dataCallBack.setFilters(Arrays.asList(sumFilter));

        return new Pair<DSCallback, MyDSCallback>(headerCallBack,dataCallBack);
    }

    public DSCallback initMetaDataUpdater(final CalcManager manager, String headerURL)
    {
        return new MetaAnalitDSCallBack(headerURL,null,this)
        {
            @Override
            protected void updateByDesc(IAnalisysDesc desc) throws FieldException
            {
                manager.setIAnalisysDesc(desc);
            }
        };
   }

    public MyDSCallback initDataUpdater(final BaseCalcManager manager, final int period)
    {
        return new DataDSCallback(period)
        {
            private Map<Object,Record> cache=new HashMap<Object,Record>();

//            int debugIx=0;

            @Override
            protected void updateData(Record[] data, boolean resetAll) throws SetGridException
            {
                    ValDef rootDef;
                    CalcManager.StateViews state = manager.getAllCurrentState();
                    if (resetAll)
                    {
                        cache=new HashMap<Object,Record>(); //Сброс кеша данныз
                        rootDef=manager.reCreateRootValue();  //Обновление данных графиков
                    }
                    else
                        rootDef=manager.getRootValue();  //Получить возможные группировки

//                    if (debugIx>5) //TODO Блок отладки удалений
//                    {
//                        List<Record> delRecords =new LinkedList<Record>();
//                        int ix=0;
//                        for (Record record : cache.values()) {
//                            delRecords.add(record);
//                            if (ix>10)
//                                break;
//                            ix++;
//
//                        }
//
//                        if (delRecords.size()>0)
//                        {
//                            for (Record delRecord : delRecords)
//                                delRecord.setAttribute(TablesTypes.ACTUAL,0);
//                            data = delRecords.toArray(new Record[delRecords.size()]);
//                        }
//                        else
//                            data = new Record[0];
//                    }


                    for (Record newRecord : data)
                    {
                        Integer actual = newRecord.getAttributeAsInt(TablesTypes.ACTUAL);
                        String id = newRecord.getAttributeAsString(TablesTypes.KEY_FNAME);
                        if (actual==0 || actual==null)
                            rootDef.removeRecord(cache.remove(id),manager.getDefs(),0);
                        else
                        {

                            Record oldRecord = cache.get(id);
                            if (oldRecord==null)
                            {
                                cache.put(id,newRecord);
                                rootDef.addRecord(newRecord,manager.getDefs(),0);
                            }
                            else
                            {
                                String[]  keys=newRecord.getAttributes(); //TODO Посмотреть на предмет серверной оптимизации
                                for (String key : keys)
                                {
                                    Object value = newRecord.getAttributeAsObject(key);
                                    oldRecord.setAttribute(key, value);
                                }

//TODO Для удаления 18032015
//                                Map mp=newRecord.toMap();
//                                for (Object key : mp.keySet())
//                                {
//                                    Object value = mp.get(key);
//                                    oldRecord.setAttribute((String) key, value);
//                                }
//                                cache.put(id,oldRecord);

                                rootDef.addRecord(oldRecord,manager.getDefs(),0);
                            }
                        }
                    }

                    if (rootDef.isRecalc())
                    {
                        rootDef.reCalcGroups(cache);
                        manager.updateCurrentState(state);
                    }
                    else
                        manager.updateJustLoadedChartCurrentState(state);
            }
        };
    }


//TODO Для удаления 13032015
//        registerDataSource(headerURL, true, null);
//        //мета-описание таблицы
//        return new MyDSCallback()
//        {
//            public void execute(DSResponse dsResponse, Object rawData, DSRequest request) {
//                try
//                {
//
//                    int status=dsResponse.getStatus();
//
//                    if (status!= RPCResponse.STATUS_SUCCESS)
//                    {
//                        SC.say("ERROR HEADER GETTING: " + status + " " + dsResponse.getAttribute("httpResponseText"));
//                        return;
//                    }
//
//
//
//                    Record[] metaData = dsResponse.getData();
//                    if (metaData!=null && metaData.length==1)
//                    {
//                        IAnalisysDesc desc = new IAnalisysDescImpl(metaData[0].getJsObj());
//                        manager.setIAnalisysDesc(desc);
//                    }
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace(); //TODO Что делать на это исключение
//                }
//            }
//        };

//    public String registerDataSource(String url, boolean meta, String addUrlName) {
//
//        if (addUrlName==null)
//            addUrlName="";
//        String idDataSource = (url+addUrlName).replace(".", "_");
//        idDataSource = idDataSource.replace("/", "$");
//        DataSource dataSource;
//        if (DataSource.get(idDataSource)==null)
//        {
//            dataSource = getNewDataSource(url, meta);
//            dataSource.setID(idDataSource);
//            dataSource.setDataFormat(DSDataFormat.JSON);
//        }
//        return idDataSource;
//    }
//
//
//    public DataSource getNewDataSource(String url, boolean meta)
//    {
//        if (meta)
//            return new DataSource(url);
//        else
//        {
//            final DataSource dataSource = new DataSource(url)
//            {
//                protected void transformResponse(DSResponse dsResponse,
//                         DSRequest dsRequest,
//                         Object data)
//                {
//                    JSONArray trans = XMLTools.selectObjects(data, TRANS);
//                    dsResponse.setAttribute(TRANS,trans.getJavaScriptObject());
//                }
//            };
//            dataSource.setRecordXPath("tuples");
////            dataSource.addField(new DataSourceField("TESTDATE",FieldType.DATETIME));
//
//            return dataSource;
//        }
//    }




//        //Обработчик данных
//        return new MyDSCallback(period)
//        {
//            private Map<Object,Record> cache=new HashMap<Object,Record>();
//
////            int debugIx=0;
//
//            public void execute(DSResponse response, Object rawData, DSRequest request) {
//                try
//                {
//
//                    int status=response.getStatus();//getErrors();
//
//                    if (status!= RPCResponse.STATUS_SUCCESS)
//                    {
//                        SC.say("ERROR DATA GETTING: " + status + " " + response.getAttribute("httpResponseText"));
//                        return;
//                    }
//
//
//                    JavaScriptObject jsObject = (JavaScriptObject) response.getAttributeAsObject(TRANS);
//                    Object[] objs= JSOHelper.convertToArray(jsObject);
//
//
//                    Record data1=new Record((Map)objs[0]);
//
//                    this.timeStamp = data1.getAttributeAsLong("updateStamp");
//                    this.timeStampN = data1.getAttributeAsInt("updateStampN");
//                    Integer cliCnt=data1.getAttributeAsInt("cliCnt");
//                    this.tblId=data1.getAttributeAsString(TablesTypes.TBLID);
//
//
//                    Record[] data = response.getData();
//
//                    filters=getFilters();
//                    if (filters!=null)
//                        for (ICliFilter filter : filters)
//                            data=filter.filter(data);
//
//                    boolean resetAll = false;
//                    if (cliCnt!=null)
//                    {
//                        resetAll=(cliCnt-this.cliCnt <=0);
//                        this.cliCnt =cliCnt;
//                    }
//
//
//                    ValDef rootDef;
//                    CalcManager.StateViews state = manager.getAllCurrentState();
//                    if (resetAll)
//                    {
//                        cache=new HashMap<Object,Record>(); //Сброс кеша данныз
//                        rootDef=manager.reCreateRootValue();  //Обновление данных графиков
//                    }
//                    else
//                        rootDef=manager.getRootValue();  //Получить возможные группировки
//
////                    if (debugIx>5) //TODO Блок отладки удалений
////                    {
////                        List<Record> delRecords =new LinkedList<Record>();
////                        int ix=0;
////                        for (Record record : cache.values()) {
////                            delRecords.add(record);
////                            if (ix>10)
////                                break;
////                            ix++;
////
////                        }
////
////                        if (delRecords.size()>0)
////                        {
////                            for (Record delRecord : delRecords)
////                                delRecord.setAttribute(TablesTypes.ACTUAL,0);
////                            data = delRecords.toArray(new Record[delRecords.size()]);
////                        }
////                        else
////                            data = new Record[0];
////                    }
//
//
//                    for (Record record : data)
//                    {
//                        Integer actual = record.getAttributeAsInt(TablesTypes.ACTUAL);
//                        String id = record.getAttributeAsString(TablesTypes.KEY_FNAME);
//                        if (actual==0 || actual==null)
//                            rootDef.removeRecord(cache.remove(id),manager.getDefs(),0);
//                        else
//                        {
//
//                            Record oldRecord = cache.get(id);
//                            if (oldRecord==null)
//                            {
//                                cache.put(id,record);
//                                rootDef.addRecord(record,manager.getDefs(),0);
//                            }
//                            else
//                            {
//                                Map mp=record.toMap();
//                                for (Object key : mp.keySet())
//                                {
//                                    Object value = mp.get(key);
//                                    oldRecord.setAttribute((String) key, value);
//                                }
//                                cache.put(id,oldRecord);
//                                rootDef.addRecord(oldRecord,manager.getDefs(),0);
//                            }
//                        }
//                    }
//
//                    if (rootDef.isRecalc())
//                    {
//                        rootDef.reCalcGroups(cache);
//                        manager.updateCurrentState(state);
//                    }
//                    else
//                        manager.updateJustLoadedChartCurrentState(state);
//
//                    Timer timer1 = getTimer();
//                    if (timer1 != null)
//                        timer1.schedule(this.period);
////                    debugIx++;
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace(); //TODO Что делать на это исключение
//                }
//            }
//        };

}
