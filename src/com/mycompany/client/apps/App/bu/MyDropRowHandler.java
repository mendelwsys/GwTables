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
 *
 */
public class MyDropRowHandler implements DropHandler
{

    private MyPortalColumn column;
    private MyPortalRow row;

    public MyDropRowHandler(MyPortalColumn column,MyPortalRow row)
    {

        this.column = column;
        this.row = row;
    }

    public void onDrop(DropEvent event)
    {
        Canvas dragTarget = EventHandler.getDragTarget();
        if (dragTarget instanceof TreeGrid)
        {
            Record[] dragData = ((ListGrid) dragTarget).getDragData();
            for (Record record : dragData)
            {
                Object operation = record.getAttributeAsObject("Operation");
                if (operation instanceof IOperation)
                {
                    IOperation operation1 = (IOperation) operation;
                    if (IOperation.TypeOperation.addEventPortlet.equals(operation1.getTypeOperation()))
                    {
                        row.addMember(operation1.operate(dragTarget, null));
                        event.cancel();
                    }
                }
            }
        }
    }
}
