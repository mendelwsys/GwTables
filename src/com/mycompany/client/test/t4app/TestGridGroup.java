package com.mycompany.client.test.t4app;

import com.google.gwt.i18n.client.NumberFormat;
import com.mycompany.client.test.TestBuilder;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.RecordSummaryFunctionType;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.widgets.grid.*;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 16.10.14
 * Time: 18:03
 * проверка возможности перехода из консолидированных данных в
 */
public class TestGridGroup implements TestBuilder{
    @Override
    public void setComponents(Layout mainLayout)
    {

        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();

        DataSource dataSource = OrderItemLocalDS.getInstance();

        ListGridField orderIdField = new ListGridField("orderID");
        orderIdField.setIncludeInRecordSummary(false);
        orderIdField.setSummaryFunction(SummaryFunctionType.COUNT);

        ListGridField itemDescriptionField = new ListGridField("itemDescription");

        ListGridField categoryField = new ListGridField("category");
        categoryField.setShowGridSummary(true);
        categoryField.setSummaryFunction(new SummaryFunction() {
            public Object getSummaryValue(Record[] records, ListGridField field) {
                Set<String> uniqueCategories = new HashSet<String>();

                for (int i = 0; i < records.length; i++) {
                    // convert each supplied Record to an OrderItem if it's not one already
                    OrderItem item = records[i] instanceof OrderItem ? (OrderItem)records[i] :
                                                                    new OrderItem(records[i]);
                    uniqueCategories.add(item.getCategory());
                }
                return uniqueCategories.size() + " Categories";
            }
        });

        ListGridField shipDateField = new ListGridField("shipDate");
        shipDateField.setShowGroupSummary(true);
        shipDateField.setShowGridSummary(false);
        shipDateField.setSummaryFunction(SummaryFunctionType.MAX);

        ListGridField quantityField = new ListGridField("quantity");
        quantityField.setIncludeInRecordSummary(false);
        quantityField.setShowGroupSummary(false);
        quantityField.setShowGridSummary(false);

        ListGridField unitPriceField = new ListGridField("unitPrice");
        unitPriceField.setAlign(Alignment.RIGHT);
        unitPriceField.setCellFormatter(new CellFormatter() {
            public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
                if (value == null) return null;
                try {
                    NumberFormat nf = NumberFormat.getFormat("#,##0.00");
                    return "$" + nf.format(((Number) value).doubleValue());
                } catch (Exception e) {
                    return value.toString();
                }
            }
        });
        unitPriceField.setShowGroupSummary(false);
        unitPriceField.setShowGridSummary(false);

        ListGridSummaryField totalField = new ListGridSummaryField("total", "Total");
        totalField.setAlign(Alignment.RIGHT);
        totalField.setRecordSummaryFunction(RecordSummaryFunctionType.MULTIPLIER);
        totalField.setSummaryFunction(SummaryFunctionType.SUM);
        totalField.setShowGridSummary(true);
        totalField.setShowGroupSummary(true);
        totalField.setCellFormatter(new CellFormatter() {
            public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
                if (value == null) return null;
                try {
                    NumberFormat nf = NumberFormat.getFormat("#,##0.00");

                    return "<a href=\"http://www.lenta.ru\">$" + nf.format(((Number) value).doubleValue())+"</a>";
                } catch (Exception e) {
                    return value.toString();
                }
            }
        });

        final ListGrid listGrid = new ListGrid();

        listGrid.setWidth100();
        listGrid.setHeight100();

        listGrid.setAutoFetchData(true);

        listGrid.setShowAllRecords(true);
        listGrid.setDataSource(dataSource);
        listGrid.setCanEdit(true);
        listGrid.setGroupByField("category");
        listGrid.setGroupStartOpen(GroupStartOpen.ALL);
//        listGrid.setShowGridSummary(true);
        listGrid.setShowGroupSummary(true);
//        listGrid.setShowGroupTitleColumn(true);

        listGrid.setShowGroupSummaryInHeader(true);

        listGrid.setFields(orderIdField, itemDescriptionField, categoryField, shipDateField, quantityField, unitPriceField, totalField);


        Portlet portlet = new Portlet();
        portlet.addItem(listGrid);
        portalLayout.addPortlet(portlet);

        mainLayout.addMember(portlet);
    }
}
