package com.mycompany.client.test.lenta;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.Timer;
import com.mycompany.client.GridCtrl;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.operations.ICliFilter;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 21.02.15
 * Time: 14:40
 *
 */
public class TestDataUpdate implements Runnable
{

    String[] tableTypes=new String[]{TablesTypes.WINDOWS,TablesTypes.WARNINGS};

    protected void testCtrl()
    {
        String headerURL=getHeaderURL();
        String info_strip = "INFO_STRIP";

        Pair<DSCallback, MyDSCallback> dataCallBacks = initUpdater(headerURL, IDataFlowCtrl.DEF_DELAY_DATA_MILLIS,info_strip);
        Criteria criteria = new Criteria();
        criteria.addCriteria(TablesTypes.TTYPE,tableTypes);
        criteria.addCriteria(TablesTypes.JT2ID,"1");

        final GridCtrl ctrl = new GridCtrl(info_strip, dataCallBacks, criteria, headerURL, getDataURL());
        ctrl.updateMetaAndData(true,false);
    }


    public Pair<DSCallback, MyDSCallback> initUpdater(String headerURL, int period,final String info_strip)
    {
        registerDataSource(headerURL, true, null);

        DSCallback headerCallBack = new DSCallback()
        {
            @Override
            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
            {

                    try
                    {

                        int status=dsResponse.getStatus();
                        if (status!= RPCResponse.STATUS_SUCCESS)
                        {
                            SC.say("ERROR HEADER GETTING: " + status + " " + dsResponse.getAttribute("httpResponseText"));
                            return;
                        }

                        DataSource dataSource= registerDataSource(getDataURL(), false, info_strip);

                        Record[] gridOptions = dsResponse.getData();
                        for (Record gridOption : gridOptions)
                        {
                            Record[] records = gridOption.getAttributeAsRecordArray("chs");
                            if (!dataSource.isCreated())
                                for (Record record : records)
                                {
                                    String tType = record.getAttribute("type");
                                    String name = record.getAttribute("name");
                                    ListGridFieldType lgType=ListGridFieldType.valueOf(tType);
                                    if (
                                            ListGridFieldType.DATETIME.equals(lgType) ||
                                                    ListGridFieldType.DATE.equals(lgType) ||
                                                    ListGridFieldType.TIME.equals(lgType)
                                            ) {
                                        dataSource.addField(new DataSourceField(name, FieldType.valueOf(tType)));
                                    }
                                }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace(); //TODO Что делать на это исключение
                    }
        }};

        //Обработчик данных
        MyDSCallback dataCallBack = new MyDSCallback(period)
        {
            public void execute(DSResponse response, Object rawData, DSRequest request) {
                try {

                    int status=response.getStatus();//getErrors();

                    if (status!= RPCResponse.STATUS_SUCCESS)
                    {
                        SC.say("ERROR DATA GETTING: "+status + " "+response.getAttribute("httpResponseText"));
                        return;
                    }


                    JavaScriptObject jsObject = (JavaScriptObject) response.getAttributeAsObject(TRANS);
                    Object[] objs= JSOHelper.convertToArray(jsObject);


                    Record data1=new Record((Map)objs[0]);

                    this.timeStamp = data1.getAttributeAsLong("updateStamp");
                    this.timeStampN = data1.getAttributeAsInt("updateStampN");
                    Integer cliCnt=data1.getAttributeAsInt("cliCnt");
                    this.tblId=data1.getAttributeAsString(TablesTypes.TBLID);

                    Record[] data = response.getData();

                    filters=getFilters();
                    if (filters!=null)
                        for (ICliFilter filter : filters)
                            data=filter.filter(data);

                    {

                    }



                    boolean resetAll = false;
                    if (cliCnt!=null)
                    {
                        resetAll=(cliCnt-this.cliCnt <=0);
                        this.cliCnt =cliCnt;
                    }

                    Timer timer1 = getTimer();
                    if (timer1 != null)
                        timer1.schedule(this.period);
                }
                catch (Exception e)
                {
                    e.printStackTrace(); //TODO Что делать на это исключение
                }

            }
        };
        return new Pair<DSCallback,MyDSCallback>(headerCallBack,dataCallBack);
    }


    public static final String TRANS = "trans";

    public String getDataURL() {
        return "transport/tdata2";
    }

    public String getHeaderURL() {
        return "theadDesc.jsp";
    }

    public DataSource registerDataSource(String url, boolean meta, String addUrlName) {

        if (addUrlName==null)
            addUrlName="";
        String idDataSource = (url+addUrlName).replace(".", "_");
        idDataSource = idDataSource.replace("/", "$");
        DataSource dataSource=null;
        if (DataSource.get(idDataSource)==null)
        {
            dataSource = getNewDataSource(url, meta);
            dataSource.setID(idDataSource);
            dataSource.setDataFormat(DSDataFormat.JSON);
        }
        return dataSource;
    }

    public DataSource getNewDataSource(String url, boolean meta)
    {
        if (meta)
            return new DataSource(url);
        else
        {
            final DataSource dataSource = new DataSource(url)
            {
                protected void transformResponse(DSResponse dsResponse,
                         DSRequest dsRequest,
                         Object data)
                {
                    JSONArray trans = XMLTools.selectObjects(data, TRANS);
                    dsResponse.setAttribute(TRANS,trans.getJavaScriptObject());
                }
            };
            dataSource.setRecordXPath("tuples");
            return dataSource;
        }
    }

    @Override
    public void run()
    {
        HLayout mainLayout = new HLayout();
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);

        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();

        Portlet portlet = new Portlet();
//        portlet.addItem(windows);
        portalLayout.addPortlet(portlet);

        mainLayout.addMembers(portalLayout);

        testCtrl();

        mainLayout.draw();

    }
}
