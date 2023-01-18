package com.mycompany.client.test.consistency;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.GridCtrl;
import com.mycompany.client.GridUtils;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.apps.App.VAGTORGridConstructor;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.updaters.DataDSCallback;
import com.mycompany.client.utils.*;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.06.15
 * Time: 19:00
 * Тестирование получения данных с сервера через стандартный провайдер (тестируется консистентонсть данных)
 */
public class CheckDataUpdates
        implements TestBuilder,Runnable
{


    private native JavaScriptObject getGlobalVariables() /*-{
	var keys = $wnd.isc.getKeys ( $wnd );
	return keys;
}-*/;

    private native JavaScriptObject getElementById(String id) /*-{
	return document.getElementById(id);
}-*/;


    private native void delVariable(JavaScriptObject obj) /*-{
        delete obj;
}-*/;

    @Override
    public void run() {

        final HLayout mainLayout = new HLayout();
        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);

        this.setComponents(mainLayout);
        mainLayout.draw();
    }


    TextAreaItem textAreaItem;

    @Override
    public void setComponents(Layout mainLayout)
    {

        final String tblType = TablesTypes.VAGTOR;

        final DynamicForm form = OptionsViewers.createEmptyForm();

        form.setLayoutAlign(VerticalAlignment.TOP);
        textAreaItem= new TextAreaItem();
        textAreaItem.setWidth("100%");
        textAreaItem.setTitle("");
        form.setFields(textAreaItem);

        Portlet portlet0 = new Portlet();
        portlet0.addItem(form);
        portlet0.setHeight("20%");
        portlet0.setTitle("Статус теста");

        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();
        portalLayout.setShowColumnMenus(false);

        portalLayout.addPortlet(portlet0);


        final String dataURL = "transport/tdata2";
        final String headerURL = "theadDesc.jsp";
        {
            Portlet portlet = new Portlet();

            Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);
            setDorCriteria(criteria);



            final ListGridWithDesc grid;

            if (!TablesTypes.VAGTOR.equals(tblType))
            {
                grid = GridUtils.createGridTable(new BGridConstructor(),new GridUtils.DefaultGridFactory()
                        {
                            public ListGridWithDesc createGrid()
                            {
                                ListGridWithDesc _grid = super.createGrid();
                                _grid.setDescOperation(new DescOperation());
                                return _grid;
                            }

                        },new GridMetaProviderBase(), criteria, headerURL, dataURL, true,false);
            }
            else
            {
                grid = GridUtils.createGridTable(new VAGTORGridConstructor(),new GridUtils.DefaultGridFactory()
                        {
                            public ListGridWithDesc createGrid()
                            {
                                ListGridWithDesc _grid = super.createGrid();
                                _grid.setDescOperation(new DescOperation());

                                _grid.setShowGroupSummaryInHeader(true);
                                _grid.setGroupTitleField(TablesTypes.DOR_NAME);
                                _grid.setGroupStartOpen(GroupStartOpen.NONE);
                                _grid.setShowGroupTitleColumn(false);
                                _grid.setGroupByMaxRecords(30000);
//                                _grid.setGroupByAsyncThreshold(30000);

                                _grid.setShowGroupSummary(true);
                                return _grid;
                            }

                        },new GridMetaProviderBase(), criteria, headerURL, dataURL, true,false);
            }





            grid.getCtrl().addAfterUpdater(new DSCallback() {
                int ix = 1;
                int clix = 1;
                int checkCons = 0;


                @Override
                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                {

                    if (res == null)
                        textAreaItem.setValue("Check  status: Count of consistency Checks:" + checkCons + " Count of data gets:" + (ix - 1) + " data requests " + clix);
                    else {
                        String value = "Check Consistency Error?: " + res.errCode + " " + res.key + " " + res.attribute + " cnt=" + cnt;
                        if (res.params!=null && res.params.length>0)
                            for (int i = 0; i < res.params.length; i++)
                                value+=" "+res.params[i];
                        textAreaItem.setValue(value);
                    }



                    Record[] data1 = dsResponse.getData();


                    if (ix % 5 == 0 )//|| (res != null && data1 != null && data1.length > 0))
                    {
                        grid.getCtrl().stopUpdateData();
                        new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation() {

                            @Override
                            public boolean operate() {
                                getDataFromByType(tblType, grid);
                                return true;
                            }
                        });
                        ix++;
                        checkCons++;
                    } else {
                        if (data1 != null && data1.length > 0 || res != null)
                            ix++;
                    }

                    clix++;
                }
            }
            );

            grid.setWidth100();
            grid.setHeight100();





            portlet.addItem(grid);
            portalLayout.addPortlet(portlet);

        }

//

        mainLayout.addMembers(portalLayout);
    }

    private void setDorCriteria(Criteria criteria)
    {
//        IServerFilter serverFilter = new CommonServerFilter(TablesTypes.FILTERDATAEXPR);
//        final AdvancedCriteria serverCriteria = new AdvancedCriteria();
//        serverCriteria.addCriteria(new Criterion(TablesTypes.DOR_CODE, OperatorId.EQUALS, new Integer(1)));
//        serverCriteria.appendToCriterionList(new Criterion(TablesTypes.DOR_CODE, OperatorId.EQUALS, new Integer(10)));
//        serverCriteria.setOperator(OperatorId.OR);
//        serverFilter.setCriteria(serverCriteria);
//        serverFilter.set2Criteria(criteria);
    }

    private GridCtrl ctrl;

    int cnt=0;
    ErrorReporter res;


    public Record toMyRecord2(Record record)
    {
//        ListGridRecord newRecord = new ListGridRecord(record.getJsObj());
        Object _link=record.getAttributeAsObject(TablesTypes.CRDURL);
        if (_link!=null && !(_link instanceof String))
        {
            Map link=record.getAttributeAsMap(TablesTypes.CRDURL);
            record.setAttribute(TablesTypes.CRDURL, link.get("link"));
            record.setAttribute(TablesTypes.LINKTEXT, link.get(TablesTypes.LINKTEXT));
        }
        return record;
    }


    void getDataFromByType(final String tblType,final ListGridWithDesc grid)
    {


        if (ctrl==null)
        {
            DSRegisterImpl register = new DSRegisterImpl();
            final String dataURL = "transport/tdata2";
            final String headerURL = "theadDesc.jsp";
            Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);
            setDorCriteria(criteria);
            final String addDataSourceId = "$" + criteria.getAttribute(TablesTypes.TTYPE);

            DataDSCallback ds = new DataDSCallback(100) {
                @Override
                protected void updateData(Record[] data, boolean resetAll) throws SetGridException {


                    for (int i = 0; i < data.length; i++)
                        data[i]=toMyRecord2(data[i]);

                    res = checkForConsistency(data, grid.getCacheData());
                    if (res!=null)
                        cnt++;
                    else
                        cnt=0;

                    ctrl.setFullDataUpdate();
                    grid.getCtrl().startUpdateData(true);

                }
            };

            register.registerDataSource(headerURL,true,null);
            register.registerDataSource2(dataURL, false, addDataSourceId);

            Pair<DSCallback, MyDSCallback> dataCallBacks=new Pair<DSCallback, MyDSCallback>(new DSCallback()
            {
                @Override
                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                }
            },ds);
            ctrl = new GridCtrl(addDataSourceId, dataCallBacks, criteria, headerURL, dataURL);
            ctrl.updateMetaAndData(false,false);
        }
        else
        {
            ctrl.startUpdateData(false);
        }
    }

    private class ErrorReporter
    {
        int errCode;
        String key;

        String attribute;
        private String[] params=new String[0];

        private ErrorReporter(int errCode, String key,String attribute,String[] params) {
            this.errCode = errCode;
            this.key = key;
            this.attribute=attribute;
            this.params = params;
        }

        private ErrorReporter(int errCode, String key,String attribute)
        {
            this(errCode,key,attribute,null);
        }

    }

    ErrorReporter checkForConsistency(Record[] fullData,RecordList cacheData)
    {
        if (fullData.length!=cacheData.getLength())
            return new ErrorReporter(1,null,null);

        Map<String,Record> mapСacheData= new HashMap<String,Record>();


        int ln=cacheData.getLength();
        for (int i=0;i<ln;i++)
        {
            Record record=cacheData.get(i);
            mapСacheData.put(record.getAttribute(TablesTypes.KEY_FNAME),record);
        }


        for (Record fullRecord : fullData)
        {
            final String key = fullRecord.getAttribute(TablesTypes.KEY_FNAME);
            Record cacheRecord = mapСacheData.get(key);
            if (cacheRecord==null)
                return new ErrorReporter(2,key,null);
            Set<String> fullRecAttrs = new HashSet(Arrays.asList(fullRecord.getAttributes()));
            Set<String> cacheRecAttrs2 = new HashSet(Arrays.asList(cacheRecord.getAttributes()));

            for (String fullRecAttr : fullRecAttrs)
                if (!cacheRecAttrs2.contains(fullRecAttr))
                    return new ErrorReporter(3,key,fullRecAttr);

            for (String recAttr : fullRecAttrs)
            {
                Object attr1 = fullRecord.getAttributeAsObject(recAttr);
                if (attr1 instanceof JavaScriptObject)
                    attr1=JSOHelper.convertToJava((JavaScriptObject)attr1);

                Object attr2 = cacheRecord.getAttributeAsObject(recAttr);
                if (attr2 instanceof JavaScriptObject)
                    attr2=JSOHelper.convertToJava((JavaScriptObject)attr2);

                if (attr1!=attr2 && (attr1==null || !attr1.equals(attr2)))
                    return new ErrorReporter(4,key,recAttr,new String[]{String.valueOf(attr1),String.valueOf(attr2)});
            }
        }
        return null;
    }

}
