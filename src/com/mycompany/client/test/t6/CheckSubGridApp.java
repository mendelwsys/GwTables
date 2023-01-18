package com.mycompany.client.test.t6;

import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.utils.JScriptUtils;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.*;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 16.11.14
 * Time: 19:58
 * Проверка обработкчиков событий для грида
 */
public class CheckSubGridApp implements TestBuilder

{
    static Window window;
    public static Window createWin(String title, boolean autoSizing, int width, int height, int offsetLeft, int top)
    {
        Map<String,Object> map= new HashMap<String,Object>();

        map.put("ASD0",1);
        map.put("ASD1",1.1);
        map.put("ASD2","QWERTY");
        map.put("ASD3",null);

        Label label = new Label
                    (
                            JScriptUtils.getFilterString2(map) +"  4444"
                            //new TTest().checkJson()

//                            "<b>Severity 1</b> - Critical problem<br/>System is unavailable in production or is corrupting data, and the error severely impacts the user's operations.<br/><br/>"
//                            + "<b>Severity 2</b> - Major problem<br/>An important function of the system is not available in production, and the user's operations are restricted.<br/><br/>"
//                            + "<b>Severity 3</b> - Minor problem<br/>Inability to use a function of the system occurs, but it does not seriously affect the user's operations."


                    );
            label.setWidth100();
            label.setHeight100();
            label.setPadding(5);
            label.setValign(VerticalAlignment.TOP);

            if (window!=null)
            {
                window.close();
                window.destroy();
            }

            window = new Window();
            window.setAutoSize(autoSizing);
            window.setTitle(title);
            window.setWidth(width);
            window.setHeight(height);
            window.setLeft(offsetLeft);
            window.setTop(top);
            window.setCanDragReposition(true);
            window.setCanDragResize(true);
            window.addItem(label);

            return window;
        }


    @Override
    public void setComponents(final Layout mainLayout)
    {
//        Canvas canvas = new Canvas();

        final ListGrid countryGrid = new ListGrid()
        {
//            Window window;
//
//             protected Canvas getCellHoverComponent(Record record, Integer rowNum, Integer colNum)
//             {
//                 Label label = new Label(
//                         "<b>Severity 1</b> - Critical problem<br/>System is unavailable in production or is corrupting data, and the error severely impacts the user's operations.<br/><br/>"
//                                 + "<b>Severity 2</b> - Major problem<br/>An important function of the system is not available in production, and the user's operations are restricted.<br/><br/>"
//                                 + "<b>Severity 3</b> - Minor problem<br/>Inability to use a function of the system occurs, but it does not seriously affect the user's operations.");
//                 label.setWidth100();
//                 label.setHeight100();
//                 label.setPadding(5);
//                 label.setValign(VerticalAlignment.TOP);
//
//                 if (window==null)
//                 {
//                    window = new Window();
//                     window.setAutoSize(false);
//                     window.setTitle("XXX");
//                     window.setWidth(200);
//                     window.setHeight(200);
//                     window.setCanDragReposition(true);
//                     window.setCanDragResize(true);
//                     window.addItem(label);
//                 }
//
//                return window;
//            }
        };
        countryGrid.setWidth100();
        countryGrid.setHeight100();
        countryGrid.setShowAllRecords(true);

//        ListGridField countryCodeField = new ListGridField("countryCode", "Flag", 50);
//        countryCodeField.setAlign(Alignment.CENTER);
//        countryCodeField.setType(ListGridFieldType.IMAGE);
//        countryCodeField.setImageURLPrefix("flags/16/");
//        countryCodeField.setImageURLSuffix(".png");

        ListGridField nameField = new ListGridField("countryName", "Country");
        ListGridField capitalField = new ListGridField("capital", "Capital");
        ListGridField continentField = new ListGridField("continent", "Continent");
//        countryGrid.setFields(countryCodeField, nameField, capitalField, continentField);

        countryGrid.setFields(nameField, capitalField, continentField);

        countryGrid.setData(CountrySampleData.getRecords());

//        final Label label = new Label("click a value in the grid");
//        label.setWidth(300);
//        label.setTop(250);
//        label.setAlign(Alignment.CENTER);
//        label.setBorder("1px solid #808080");
//        canvas.addChild(label);



        countryGrid.addCellClickHandler(new CellClickHandler()
        {


            public void onCellClick(CellClickEvent event) {

                ListGridRecord record =  event.getRecord();
                int colNum = event.getColNum();
                ListGridField field = countryGrid.getField(colNum);
                String fieldName = countryGrid.getFieldName(colNum);
                String fieldTitle = field.getTitle();


                int rowNum=event.getRowNum();

                int top=countryGrid.getRowPageTop(rowNum);
                int left=countryGrid.getColumnPageLeft(colNum);


//                Canvas canv=countryGrid.getRecordComponent(rowNum,colNum);

                Window win = createWin("Auto-sizing window", true, 500, 200, left, top);
//                    win = createWin("Auto-sizing window", true, 300, 200, event.getX(), event.getY());
                mainLayout.addChild(win);
            }
        });

        countryGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
            public void onCellDoubleClick(CellDoubleClickEvent event) {
                sayCellEvent(countryGrid, "Double-clicked", event.getRecord(), event.getColNum());
            }
        });

        countryGrid.addCellContextClickHandler(new CellContextClickHandler() {
            public void onCellContextClick(CellContextClickEvent event) {
                sayCellEvent(countryGrid, "Context-clicked", event.getRecord(), event.getColNum());
                event.cancel();
            }
        });


        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();
        portalLayout.setShowColumnMenus(false);

        Portlet portlet = new Portlet();
        portlet.setShowCloseConfirmationMessage(false);
        portlet.addItem(countryGrid);

        portalLayout.addPortlet(portlet);
        mainLayout.addMembers(portalLayout);
    }


     private static void sayCellEvent(ListGrid countryGrid, String eventText, ListGridRecord record, int colNum) {
        ListGridField field = countryGrid.getField(colNum);
        String title = field.getTitle();
        String fieldName = countryGrid.getFieldName(colNum);
        SC.say(eventText + " <b>" +
                title + ":" + record.getAttribute(fieldName) +
                "</b> (Country:" + record.getAttribute("countryName") + ")");
    }

}
