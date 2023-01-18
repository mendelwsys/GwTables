package com.mycompany.client.test.tgrid;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.GridUtils;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.GridMetaProviderBase;
import com.mycompany.common.DescOperation;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.06.15
 * Time: 19:00
 * Тестирование получения данных с сервера через провайдер SQL Запросов (На сервер пришлось производить модификации провайдеров для подкачки данных)
 */
public class T2
        implements TestBuilder,Runnable
{


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


    @Override
    public void setComponents(Layout mainLayout)
    {
        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();
        portalLayout.setShowColumnMenus(false);

        final String dataURL = "transport/tdata3";
        final String headerURL = "theadDesc.jsp";
        {
            Portlet portlet = new Portlet();

            Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.PLACEPOLG);
            final String inTableName = TablesTypes.WARNINGS + "_" + TablesTypes.PLACES;

            JavaScriptObject js = JSOHelper.convertToJavaScriptArray(new String[]{inTableName});
            criteria.addCriteria(TablesTypes.TTYPE+"_ADD",JSON.encode(js));

            final ListGrid grid = GridUtils.createGridTable(new BGridConstructor(),new GridUtils.DefaultGridFactory()
                    {
                        public ListGridWithDesc createGrid()
                        {
                            ListGridWithDesc _grid = super.createGrid();
                            _grid.setDescOperation(new DescOperation());
                            return _grid;
                        }

                    },new GridMetaProviderBase(), criteria, headerURL, dataURL, true,false);
            grid.setGroupByMaxRecords(10000);
            grid.setWidth100();
            grid.setHeight100();

            portlet.addItem(grid);
            portalLayout.addPortlet(portlet);
        }

        mainLayout.addMembers(portalLayout);
    }
}
