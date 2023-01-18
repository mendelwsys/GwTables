package com.mycompany.client.test.t3;

import com.mycompany.client.GridUtils;
import com.mycompany.client.test.Demo.DemoApp01;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.09.14
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */


public class TestBuilderImpl1 implements TestBuilder
{
    @Override
    public void setComponents(Layout mainLayout)
    {
        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();


        final String dataURL = "transport/data";
        final String headerURL = "theadDesc.jsp";

//        {
//            Portlet portlet = new Portlet();
//            final ListGrid windowsE = HelloWorld.createTable(portlet, TablesTypes.WINDOWS+"_E", headerURL, dataURL);
//            windowsE.setWidth100();
//            windowsE.setHeight100();
//
//            portlet.addItem(windowsE);
//            portalLayout.addPortlet(portlet);
//        }

        {
            Portlet portlet = new Portlet();
            final ListGrid windows = GridUtils.createGridTable(DemoApp01.gridMetaProvider, TablesTypes.WINDOWS, headerURL, dataURL, true, false);
            windows.setWidth100();
            windows.setHeight100();

            portlet.addItem(windows);
            portalLayout.addPortlet(portlet);
        }

        mainLayout.addMembers(portalLayout);

    }
}
