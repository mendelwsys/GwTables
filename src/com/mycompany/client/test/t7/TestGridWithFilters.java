package com.mycompany.client.test.t7;

import com.mycompany.client.GridUtils;
import com.mycompany.client.test.Demo.DemoApp01;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.09.14
 * Time: 11:23
 * Тестируем грид с фильтром
 */


public class TestGridWithFilters implements TestBuilder
{

    @Override
    public void setComponents(Layout mainLayout)
    {
        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();
        portalLayout.setShowColumnMenus(false);

        final String dataURL = "transport/tdata2";
        final String headerURL = "theadDesc.jsp";

        {
            Portlet portlet = new Portlet();

//            Criteria criteria = new AdvancedCriteria(OperatorId.OR, new Criterion[]
//                    {
//                            new Criterion("title", OperatorId.ICONTAINS, "Manager"),
//                            new Criterion("reports", OperatorId.NOT_NULL)
//                    });

            //Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.WINDOWS_CURR);
            Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.WINDOWS);
//            criteria.addCriteria(TablesTypes.TTYPE, TablesTypes.WINDOWS_CURR);
            criteria.addCriteria(TablesTypes.FILTERDATAEXPR," DOR_KOD=28 ");

            final ListGrid grid = GridUtils.createGridTable(DemoApp01.gridMetaProvider, criteria, headerURL, dataURL, true, false);
            grid.setGroupByMaxRecords(10000);
            grid.setWidth100();
            grid.setHeight100();

//            final FilterBuilder filterBuilder = new FilterBuilder();
//                    filterBuilder.setDataSource(worldDS);
//                    filterBuilder.setTopOperatorAppearance(TopOperatorAppearance.RADIO);
//
//            filterBuilder.getCriteria()


            portlet.addItem(grid);

//            portalLayout.setAutoChildProperties();

            portalLayout.addPortlet(portlet);
        }

        mainLayout.addMembers(portalLayout);
    }
}
