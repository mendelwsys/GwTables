package com.mycompany.client;

import com.google.gwt.user.client.Timer;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 28.11.14
 * Time: 12:49
 * Класс для управления подкачкой данных
 */
public class GridCtrl implements IDataFlowCtrl {


    CtrlAfterUpdater afterUpdater = new CtrlAfterUpdater();
    private String addDataSourceId;

    @Override
    public void removeAfterUpdater(DSCallback updater)
    {
        afterUpdater.getCallBacks().remove(updater);
    }

    @Override
    public void addAfterUpdater(DSCallback updater)
    {
        afterUpdater.getCallBacks().add(updater);
    }

    private class CtrlAfterUpdater implements DSCallback
    {
        public List<DSCallback> getCallBacks()
        {
            return callBacks;
        }

        List<DSCallback> callBacks=new LinkedList<DSCallback>();

        @Override
        public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
        {
            List<DSCallback> callBacks=new LinkedList(this.callBacks);
            for (int i = 0, dsCallbacksSize = callBacks.size(); i < dsCallbacksSize; i++)
            {
                DSCallback callBack = callBacks.get(i);
                callBack.execute(dsResponse, data, dsRequest);
            }
        }
    }


    @Override
    public void setFullDataUpdate()
    {
        dataCallBacks.second.resetSrvCnt();
    }
    @Override
    public Criteria getCriteria()
    {

        return criteria;
    }

    @Override
    public void setCriteria(Criteria criteria)
    {
        this.criteria = criteria;
    }

    protected Criteria criteria;
    protected String dataURL;
    protected String headerURL;
    private Pair<DSCallback, MyDSCallback> dataCallBacks;


    @Override
    public int getPeriod() {
        return dataCallBacks.second.getPeriod();
    }

    @Override
    public void setPeriod(int period)
    {
        dataCallBacks.second.setPeriod(period);
    }

//    public GridCtrl(IGridConstructor gridConstructor,IGridMetaProvider gridMetaProvider, Criteria criteria, String headerURL, String dataURL, int period)
//    {
//
//        this.criteria = criteria;
//        this.dataURL = dataURL;
//        this.headerURL = headerURL;
//
//        addDataSourceId =gridConstructor.getAddIdDataSource();
//        if (addDataSourceId ==null)
//            addDataSourceId ="";
//
//        this.dataCallBacks = gridMetaProvider.initGrid2(gridConstructor, headerURL, dataURL, period);
//
//        this.criteria.addCriteria(TablesTypes.ID_TM, dataCallBacks.second.getTimeStamp());
//        this.criteria.addCriteria(TablesTypes.ID_TN, dataCallBacks.second.getTimeStampN());
//    }

    public GridCtrl(String addDataSourceId,Pair<DSCallback, MyDSCallback> dataCallBacks,Criteria criteria, String headerURL, String dataURL)
    {

        this.criteria = criteria;
        this.dataURL = dataURL;
        this.headerURL = headerURL;

        if (addDataSourceId ==null)
            addDataSourceId ="";
        this.addDataSourceId=addDataSourceId;


        this.dataCallBacks = dataCallBacks;

        this.criteria.addCriteria(TablesTypes.ID_TM, dataCallBacks.second.getTimeStamp());
        this.criteria.addCriteria(TablesTypes.ID_TN, dataCallBacks.second.getTimeStampN());

    }



    @Override
    public void stopUpdateData()
    {
        setTimer(null);
        dataCallBacks.second.setTimer(null);

    }

    @Override
    public void startUpdateData(boolean dynamicUpdate)
    {
        startUpdateData(dynamicUpdate, null);
    }

    @Override
    public void startUpdateData(boolean dynamicUpdate, final DSCallback afterDataUpdate)
    {
        startUpdateData(dynamicUpdate, afterDataUpdate, DEF_DELAY_DATA_MILLIS);
    }

    @Override
    public void startUpdateData(boolean dynamicUpdate, final DSCallback afterDataUpdate, int delayMillis)
    {

        if (afterDataUpdate!=null)
            addAfterUpdater(afterDataUpdate);

        Timer t = getTimer();
        if (t ==null)
        {
            t=new Timer()
            {
                @Override
                public void run()
                {
                    updateData();
                }
            };
            if (dynamicUpdate)
                setTimer(t);
            t.schedule(delayMillis);
        }

    }

    @Override
    public void startUpdateMeta(final DSCallback afterHeaderUpdate, int delayMillis)
    {
        Timer t=new Timer()
        {
            @Override
            public void run()
            {
                updateMeta(afterHeaderUpdate);
            }
        };
        t.schedule(delayMillis);
    }


    @Override
    public void updateMetaAndData(final boolean dynamicUpdate, boolean noData)
    {
        updateMetaAndData(dynamicUpdate, DEF_DELAY_HEADER_MILLIS, noData, null, null, DEF_DELAY_DATA_MILLIS);
    }

    @Override
    public void updateMetaAndData(final boolean dynamicUpdate, boolean noData, final DSCallback afterDataUpdate, DSCallback afterHeaderUpdate)
    {
        updateMetaAndData(dynamicUpdate, DEF_DELAY_HEADER_MILLIS, noData, afterDataUpdate, afterHeaderUpdate, DEF_DELAY_DATA_MILLIS);
    }

    @Override
    public void updateMetaAndData(final boolean dynamicUpdate, int delayHeaderMillis, boolean noData, final DSCallback afterDataUpdate, DSCallback afterHeaderUpdate, final int delayDataMillis)
    {
        if (!noData)
          afterHeaderUpdate = new DSCallback() {
            @Override
            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
            {
                startUpdateData(dynamicUpdate,afterDataUpdate,delayDataMillis);
            }
        };
        if (delayHeaderMillis==0)
            updateMeta(afterHeaderUpdate);
        else
            startUpdateMeta(afterHeaderUpdate, delayHeaderMillis);
    }

    @Override
    public void updateMeta(final DSCallback afterHeaderUpdate)
    {
        DSRequest request = new DSRequest();
        request.setShowPrompt(false);


        String idHeaderDataSource = getIdHeaderDataSource();
        final Criteria l_criteria = criteria;
        DataSource.get(idHeaderDataSource).fetchData
        (
                l_criteria, //Среди прочих параметров передается идентифкатор таблицы
                new DSCallback()
                {
                    @Override
                    public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                    {
                        if (dataCallBacks.first!=null)
                            dataCallBacks.first.execute(dsResponse,data,dsRequest);
                        if (afterHeaderUpdate!=null)
                            afterHeaderUpdate.execute(dsResponse,data,dsRequest);
                    }
                },
                request
        );
    }

    private String getIdHeaderDataSource() {
        String idHeader = headerURL.replace(".", "_");
        idHeader = idHeader.replace("/", "$");
        return idHeader;
    }

    @Override
    public void updateData()
    {
        DSRequest request = new DSRequest();
        request.setShowPrompt(false);
        request.setHttpMethod("POST");

        String idDataSource = getIdDataSource();
        final Criteria l_criteria = criteria;
        l_criteria.addCriteria(TablesTypes.ID_REQN, String.valueOf(dataCallBacks.second.getSrvCnt()));

        String tblId = dataCallBacks.second.getTblId();
        if (tblId!=null)
            l_criteria.addCriteria(TablesTypes.TBLID, tblId);
        l_criteria.addCriteria(TablesTypes.LU_TIMESTAMP, dataCallBacks.second.getLastTimeStamp());
        final DataSource dataSource = DataSource.get(idDataSource);
        dataSource.fetchData
                (
                        l_criteria, //Среди прочих параметров передается идентифкатор таблицы
                        new DSCallback() {

                            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                                dataCallBacks.second.execute(dsResponse, data, dsRequest);
                                if (afterUpdater != null)
                                    afterUpdater.execute(dsResponse, data, dsRequest);

                                Timer timer1 = getTimer();
                                if (timer1 != null)
                                    timer1.schedule(getPeriod());
                                else
                                {
                                    String deb_str="Timer is null";
                                    deb_str+=" "+1;
                                }
                            }
                        },
                        request
                );
    }

    protected String getIdDataSource()
    {
        String idDataSource = (dataURL+ addDataSourceId).replace(".", "_");
        idDataSource = idDataSource.replace("/", "$");
        return idDataSource;
    }

    public Timer getTimer()
    {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    @Override
    public boolean isTimer()
    {
        return timer!=null;
    }

    Timer timer;


}
