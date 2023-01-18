package com.mycompany.client.apps.App.bu;

import com.mycompany.client.operations.IOperation;
import com.mycompany.client.test.myportalview.MyPortalColumn;
import com.mycompany.client.test.myportalview.MyPortalRow;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.tree.TreeGrid;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
public class MyDropColumnHandler implements DropHandler
{

    private MyPortalColumn column;

    public MyDropColumnHandler(MyPortalColumn column)
    {

        this.column = column;
    }

    @Override
    public void onDrop(DropEvent event)
    {
        Canvas dragTarget = EventHandler.getDragTarget();

        if (dragTarget instanceof TreeGrid)
        {
            Record[] dragData = ((ListGrid) dragTarget).getDragData();
            for (Record record : dragData)
            {
                Object operation = record.getAttributeAsObject("Operation");
                if (operation instanceof IOperation) {
                    IOperation operation1 = (IOperation) operation;
                    if (IOperation.TypeOperation.addEventPortlet.equals(operation1.getTypeOperation())) {


                        MyPortalRow portalRow = new MyPortalRow();
                        portalRow.addDropHandler(new MyDropRowHandler(column,portalRow));
                        Canvas portlet = operation1.operate(dragTarget, null);
                        portalRow.addMember(portlet);
                        column.addMember(portalRow);
                        event.cancel();
                    }
                }
            }
        }
    }
}
