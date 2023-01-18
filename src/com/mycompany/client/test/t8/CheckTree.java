package com.mycompany.client.test.t8;

import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.test.t6.CountrySampleData;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 24.11.14
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class CheckTree implements TestBuilder{
    @Override
    public void setComponents(Layout mainLayout) {


        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();
        portalLayout.setShowColumnMenus(false);


        Portlet portlet = new Portlet();

        Criteria criteria =
        new AdvancedCriteria(OperatorId.AND, new Criterion[]
                {
                        new Criterion("capital", OperatorId.CONTAINS, "New"),
                });

        //Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.WINDOWS_CURR);
//        Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.WINDOWS);
//            criteria.addCriteria(TablesTypes.TTYPE, TablesTypes.WINDOWS_CURR);
//        criteria.addCriteria(TablesTypes.FILTERDATAEXPR," DOR_KOD=28 ");


        final ListGrid grid = new ListGrid();
        grid.setWidth(500);
        grid.setHeight(224);
        grid.setShowAllRecords(true);

        ListGridField countryCodeField = new ListGridField("countryCode", "Flag", 40);
        countryCodeField.setAlign(Alignment.CENTER);
        countryCodeField.setType(ListGridFieldType.IMAGE);
        countryCodeField.setImageURLPrefix("flags/16/");
        countryCodeField.setImageURLSuffix(".png");

        ListGridField nameField = new ListGridField("countryName", "Country");
        ListGridField capitalField = new ListGridField("capital", "Capital");
        ListGridField continentField = new ListGridField("continent", "Continent");
        grid.setFields(countryCodeField, nameField, capitalField, continentField);
        grid.setCanResizeFields(true);
        grid.setAutoFetchData(false);
        final CountryDS instance = CountryDS.getInstance();

        portlet.addItem(grid);
        portalLayout.addPortlet(portlet);

        mainLayout.addMembers(portalLayout);

        //фильтрует закешированные данные, можно устанавливать в грид,
        //Если периодически обновлять клиентский кеш установив флаг instance.setCacheAllData(true), а как будет применен коритерий к данным
        //Т.е. я хочу заполнить кеш потом к нему применять обновления, можно создаем клиентский кеш, после этого заполняем его обычным способом, а уже клиентский
        //кеш обновляем фетчем

        instance.setCacheData(CountrySampleData.getNewRecords());

        instance.fetchData(criteria,new DSCallback() {
           @Override
           public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
           {
               Record[] rl = dsResponse.getData();
               rl[0].setAttribute("capital","1ASDFHAA11A");
               grid.setData(rl);
           }
       });




//       grid.fetchData(criteria,new DSCallback() {
//           @Override
//           public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
//           {
//
//               Record[] rl = dsResponse.getData();
//
//               //g=rid.get
////               Record[] rc=instance.getCacheData();
//               rl[0].setAttribute("capital","ASDFHAAA");
////               instance.setCacheData(rc);
//           }
//       });




//        new TreeGrid().filterData(new Criteria());

//        DataSource ds = new DataSource();
//        DataSourceIntegerField reportsToField = new DataSourceIntegerField("ReportsTo", "Manager");
//        reportsToField.setRequired(true);
//        reportsToField.setForeignKey(id + ".EmployeeId");
//        reportsToField.setRootValue("1");
    }
}
