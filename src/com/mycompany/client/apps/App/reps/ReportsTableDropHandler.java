package com.mycompany.client.apps.App.reps;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.api.EventTableDropHandler;
import com.mycompany.client.apps.OperationNode;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.IOperationFactory;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.tree.TreeGrid;

/**
 * Created by Anton.Pozdnev on 19.08.2015.
 */
public class ReportsTableDropHandler extends EventTableDropHandler {

    public ReportsTableDropHandler(ListGridWithDesc newGrid) {
        super(newGrid);

    }

    @Override
    public void onDrop(DropEvent event) {
        Canvas dragTarget = EventHandler.getDragTarget();
        if (dragTarget instanceof TreeGrid) {
            Record[] dragData = ((TreeGrid) dragTarget).getDragData();
            for (Record record : dragData) {
                Object operation1 = OperationNode.getOperation(record);
                IOperation operation = null;
                if (operation1 instanceof IOperation)
                    operation = (IOperation) operation1;
                else if (operation1 instanceof IOperationFactory) {
                    IOperationFactory factory = (IOperationFactory) operation1;
                    operation = factory.getOperation();
                }

                if (operation != null) {
                    switch (operation.getTypeOperation()) {
//                                                                 case addFilter:
//                                                                     if (newGrid.addFilter(operation))
//                                                                     {
//
//                                                                         HeaderControl pinUp = operation.createHeaderControl(newGrid, window);
//                                                                         if (pinUp != null)
//                                                                         {
//                                                                             LinkedList<Canvas> ll = new LinkedList<Canvas>(Arrays.asList(window.getHeader().getMembers()));
//                                                                             if (ll.size() >= 1)
//                                                                                 ll.add(1, pinUp);
//                                                                             else
//                                                                                 ll.add(pinUp);
//                                                                             window.getHeader().setMembers(ll.toArray(new Canvas[ll.size()]));
//                                                                         }
//                                                                         newGrid.applyClientFilters();
//                                                                     }
//                                                                 break;
//                                                                 case addServerFilter:


                        case addExcelExport:

                        {
//                                Window target = newGrid.getTarget();
//                                HeaderControl pinUp = operation.createHeaderControl(newGrid, target);
//                                NodesHolder.addHeaderCtrl(target, pinUp);

                            operation.operate(this.getNewGrid(), null);
                            break;
                        }

                    }
                }

            }
        }
        event.cancel();
    }
}
