package com.mycompany.client.test.informer;

import com.mycompany.client.*;
import com.mycompany.client.apps.App.LentaGridConstructor;
import com.mycompany.client.test.fbuilder.TableFunction;
import com.mycompany.client.updaters.*;
import com.mycompany.client.utils.DSRegisterImpl;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.widgets.grid.ListGridField;

import java.util.*;

//

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 01.02.15
 * Time: 19:44
 * франкенштейн какой-то инициализация через грид, поскольку это пока ежинственный объект через который производится сохранение
 */
public class InformerUpdater extends DSRegisterImpl
{

    public Pair<DSCallback, MyDSCallback> initUpdater(Pair<TableFunction, Informer> function2Informer,IMetaTableConstructor metaConstructor, String headerURL, String dataURL, String tblType, int period)
    {

        DSCallback headerCallBack = initMetaDataUpdater(metaConstructor,headerURL,dataURL,tblType);
        MyDSCallback dataCallBack = initDataUpdater(function2Informer,period);
        return new Pair<DSCallback, MyDSCallback>(headerCallBack,dataCallBack);
    }

    public DSCallback initMetaDataUpdater(IMetaTableConstructor metaConstructor,String headerURL, final String dataURL, String tblType)
    {
        final Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);
        metaConstructor.setAddIdDataSource("$" + criteria.getAttribute(TablesTypes.TTYPE));
        return new MetaTableDSCallback(headerURL,dataURL,this,metaConstructor);

    }



    public MyDSCallback initDataUpdater(final Pair<TableFunction, Informer> function2Informer,final int period)
    {
        return new DataDSCallback(period)
        {
            private Map<Object,Record> cache=new HashMap<Object,Record>();
//            int debugIx=0;

            @Override
            protected void updateData(Record[] data, boolean resetAll)
            {
                if (resetAll)
                    cache=new HashMap<Object,Record>(); //Сброс кеша данных    //TODO Необходимо добавить еще один фильтр, который фильтрует приходящие данные и пересчитывает информер

                for (Record newRecord : data)
                {
                    Integer actual = newRecord.getAttributeAsInt(TablesTypes.ACTUAL);
                    String id = newRecord.getAttributeAsString(TablesTypes.KEY_FNAME);
                    if (actual==0 || actual==null)
                        cache.remove(id);
                    else
                    {

                        Record oldRecord = cache.get(id);
                        if (oldRecord==null)
                            cache.put(id,newRecord);
                        else
                        {

                            String[] keys = newRecord.getAttributes();
                            for (String key : keys)
                            {
                                Object newValue = newRecord.getAttributeAsObject(key);
                                oldRecord.setAttribute(key, newValue);
                            }

//                            Map mp=newRecord.toMap();
//                            for (Object key : mp.keySet())
//                            {
//                                Object value = mp.get(key);
//                                oldRecord.setAttribute((String) key, value);
//                            }
//                            cache.put(id,oldRecord);
                        }
                    }
                }

                if (resetAll || (data!=null && data.length>0) || function2Informer.first.isNeed2Recalculate())
                {
                    Record[] recs=cache.values().toArray(new Record[cache.size()]);//TODO можно упростить обработку применяя первичный фильтр к входящему кортежу
                    function2Informer.second.viewValues(function2Informer.first.transmit(recs));
                }
            }
        };
    }

    public static String getDataURL() {
        return "transport/tdata2";
    }

    public static String getHeaderURL() {
        return "theadDesc.jsp";
    }


    public static Pair<GridCtrl,IMetaTableConstructor> initCtrl(Pair<TableFunction, Informer> function2Informer, String tblType)
    {
        String headerURL=getHeaderURL();
        String dataURL = getDataURL();

        final Criteria criteria = new Criteria(TablesTypes.TTYPE,tblType);//

        final IMetaTableConstructor metaConstructor;
        if (tblType.equals(TablesTypes.LENTA))
            metaConstructor=new LentaGridConstructor(new HashMap<String, List<ListGridField>>());
        else
            metaConstructor=new BMetaConstructor();


        final InformerUpdater informerUpdater = new InformerUpdater();
        final String addDataSourceId = "$" + criteria.getAttribute(TablesTypes.TTYPE);

        Pair<DSCallback, MyDSCallback> dataCallBacks = informerUpdater.initUpdater(function2Informer,metaConstructor, headerURL, dataURL, tblType,IDataFlowCtrl.DEF_DELAY_DATA_MILLIS);

        return new Pair<GridCtrl,IMetaTableConstructor>(new GridCtrl(addDataSourceId, dataCallBacks, criteria, headerURL, dataURL),metaConstructor);
    }


//        GridMetaProviderBase gridMetaProvider= new GridMetaProviderBase();
        //return gridMetaProvider.initMetaDataUpdater(metaConstructor, headerURL, dataURL);

        //Обработчик данных
//        return new MyDSCallback(period)
//        {
//            private Map<Object,Record> cache=new HashMap<Object,Record>();
////            int debugIx=0;
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
//                    if (resetAll)
//                        cache=new HashMap<Object,Record>(); //Сброс кеша данныз
//
//                    for (Record record : data)
//                    {
//                        Integer actual = record.getAttributeAsInt(TablesTypes.ACTUAL);
//                        String id = record.getAttributeAsString(TablesTypes.KEY_FNAME);
//                        if (actual==0 || actual==null)
//                            cache.remove(id);
//                        else
//                        {
//
//                            Record oldRecord = cache.get(id);
//                            if (oldRecord==null)
//                                cache.put(id,record);
//                            else
//                            {
//                                Map mp=record.toMap();
//                                for (Object key : mp.keySet())
//                                {
//                                    Object value = mp.get(key);
//                                    oldRecord.setAttribute((String) key, value);
//                                }
//                                cache.put(id,oldRecord);
//                            }
//                        }
//                    }
//
//                    if (data!=null && data.length>0)
//                    {
//                        Record[] recs=cache.values().toArray(new Record[cache.size()]);//TODO можно упростить обработку применяя первичный фильтр к входящему кортежу
//                        function2Informer.second.viewValues(function2Informer.first.transmit(recs));
//                    }
//
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
