package com.mycompany.client.test.tgrid;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.GridUtils;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.GridMetaProviderBase;
import com.mycompany.common.*;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.06.15
 * Time: 19:00
 * Тестирование получения данных с сервера по прямому SQL Запросу
 * TODO При прямом запросе непонятно как делать подкачку данных (16.06.2015)
 */
public class T1
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

            //TODO Здесь зададим прямой sql запрос //TODO АХРЕНЕТЬ!!!! Упростить !!!!
            {
                REQPARAM reqSql=new REQPARAM();
                final String inTableName = TablesTypes.WARNINGS + "_" + TablesTypes.PLACES;
                reqSql.setInTblTypes(new String[]{inTableName});
                final String PARAM = TablesTypes.DB_TRANSACTION_N + "_" + inTableName;
                reqSql.setRequest("select distinct DATA_OBJ_ID || '##' || trim(char(DATATYPE_ID)) || '##' || trim(char(POLG_ID)) as "+TablesTypes.KEY_FNAME+","+TablesTypes.DATATYPE_ID+",DATA_OBJ_ID,POLG_ID from WARNINGS_PLACES tdp,POLG_OBJ po where po.OBJ_OSN_ID=tdp.OBJ_OSN_ID  and tdp.KEY_DERBY_UPD00>:" + PARAM);

                JavaScriptObject js = JSOHelper.createObject();
                JSOHelper.setAttribute(js,"inTblTypes",reqSql.getInTblTypes());
                JSOHelper.setAttribute(js, "request", reqSql.getRequest());
                Map<String,Map> map = new HashMap<String,Map>();
                map.put(PARAM,new GWTSuccs2<Integer>(-1));

                criteria.addCriteria("REQPARAM",JSON.encode(js));
                criteria.addCriteria("RECPARAM",JSON.encode(JSOHelper.convertMapToJavascriptObject(map)));
            }

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
