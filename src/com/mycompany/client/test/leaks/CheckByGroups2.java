package com.mycompany.client.test.leaks;

import com.mycompany.client.GridUtils;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.apps.App.VAGTORGridConstructor;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.*;
import com.mycompany.common.DescOperation;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.06.15
 * Time: 19:00
 * Тестирование получения данных с сервера через стандартный провайдер (тестируется консистентонсть данных)
 */
public class CheckByGroups2
        implements TestBuilder,Runnable
{


    int ix = 1;
    int clix = 1;
    int checkCons = 0;
    int cnt=0;
    ErrorReporter res;

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


        initPL(tblType, portalLayout);

//

        mainLayout.addMembers(portalLayout);
    }


    Portlet portlet;
    ListGridWithDesc grid;


    private void initPL(final String tblType, final PortalLayout portalLayout)
    {
        String dataURL = "transport/tdata2";
        String headerURL = "theadDesc.jsp";


        Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);
        setDorCriteria(criteria);


        if (!TablesTypes.VAGTOR.equals(tblType))
        {
            grid = GridUtils.createGridTable(new BGridConstructor(), new GridUtils.DefaultGridFactory() {
                        public ListGridWithDesc createGrid() {
                            ListGridWithDesc _grid = super.createGrid();
                            _grid.setDescOperation(new DescOperation());
                            return _grid;
                        }

                    }, new GridMetaProviderBase(), criteria, headerURL, dataURL, true, false);
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

                if (clix%3==0)
                {
                    destroy(portalLayout);

                    new PostponeOperationProvider
                    (
                       new PostponeOperationProvider.IPostponeOperation()
                       {
                        @Override
                        public boolean operate()
                        {
                            initPL(tblType, portalLayout);
                            return true;
                        }
                    });
                }
                clix++;
            }
        }
        );
        grid.setWidth100();
        grid.setHeight100();
        portlet = new Portlet();
        portlet.addItem(grid);
        portalLayout.addPortlet(portlet);
    }

    private void destroy(PortalLayout portalLayout)
    {
        grid.getCtrl().stopUpdateData();
        grid.setCtrl(null);
        grid.ungroup();
        portlet.removeItem(grid);
        portalLayout.removePortlet(portlet);
        RecordList data = new RecordList(new Record[0]);


        RecordList wasData = grid.getCacheData();

        grid.setCacheData(data);
        grid.setData(data);

        wasData.destroy();



        grid.clear();
        grid.destroy();
        portlet.destroy();

        grid=null;
        portlet=null;


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

}
