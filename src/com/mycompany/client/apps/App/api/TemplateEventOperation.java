package com.mycompany.client.apps.App.api;


import com.google.gwt.event.shared.HandlerRegistration;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.MyHeaderControl;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.SimpleOperationP;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 28.03.15
 * Time: 14:14
 * TODO Шаблон создания операции, создан для использования как copy&paste
 */

public class TemplateEventOperation extends SimpleOperationP
{

    protected MyHeaderControl pinUp;

    protected TemplateEventOperation()
    {
       this(-1, -1, null);
    }

    public TemplateEventOperation(int operationId, int parentOperationId, String viewName) {
        super(operationId, parentOperationId, viewName);
    }

    public TemplateEventOperation(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    @Override
    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        if (dragTarget instanceof Window)
        {
            Canvas[] items = ((Window) dragTarget).getItems();
            if (items!=null && items.length>0)
                return operate(items[0], ctx);
        }
        else if (dragTarget instanceof ListGridWithDesc)
        {
            final ListGridWithDesc newGrid = (ListGridWithDesc) dragTarget;

            if (newGrid.isMetaWasSet())
            {
                List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
                int ix=findIndexInDesc(subOperation);
                if (ix>=0)
                    return null;
            }
            final Window target = newGrid.getTarget();

            final HeaderControl pinUp = createHeaderControl(newGrid, target);
            if (target.isDrawn())
                NodesHolder.addHeaderCtrl(target, pinUp);
            else {
                final HandlerRegistration[] handlerRegistrations = new HandlerRegistration[1];
                handlerRegistrations[0] = target.addDrawHandler(new DrawHandler() {
                    @Override
                    public void onDraw(DrawEvent event) {
                        NodesHolder.addHeaderCtrl(target, pinUp);
                        handlerRegistrations[0].removeHandler();//удалить хандлер после обработки
                    }
                });
            }
 //--------------------------------------------
 //TODO Здесь должен быть бизнес код операции
 //--------------------------------------------
            if (newGrid.isMetaWasSet())
                setDescOperation(newGrid);
            else
                new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
                {

                    @Override
                    public boolean operate()
                    {
                        if (newGrid.isMetaWasSet())
                        {
                            setDescOperation(newGrid);
                            return true;
                        }
                        return false;
                    }
                });
        }
        return super.operate(dragTarget, ctx);
    }


    @Override
    public HeaderControl createHeaderControl(final Canvas canvas, Window target) {
        pinUp = new MyHeaderControl(
                HeaderControl.HELP,// TODO Заменить на свою картинку
                new ClickHandler()
                {
                    public void onClick(ClickEvent event) {
//TODO Здесь добавляется обработчик нажатия гнобочки
                    }
                });
        pinUp.setGrid(canvas);
        pinUp.setOperation(this);
        pinUp.setTarget(target);
        pinUp.setCanDrag(true);
        pinUp.setCanDrop(true);
        pinUp.setTooltip(this.getViewName());
        return pinUp;
    }

    @Override
    public Canvas onRemove(Canvas warnGrid, Window target) {
        if (warnGrid instanceof ListGridWithDesc)
        {
            ListGridWithDesc newGrid = (ListGridWithDesc) warnGrid;
            List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
            int ix=findIndexInDesc(subOperation);
            if (ix>=0)
                  subOperation.remove(ix);
        }
       return super.onRemove(warnGrid, target);
    }

    @Override
    protected IOperation getEmptyObject(DescOperation descOperation) {
        return new TemplateEventOperation();
    }



//    protected int findIndexInDesc(List<DescOperation> subOperation)
//    {
//        String apiName=TemplateEventOperation.class.getName();
//        for (int i = 0, subOperationSize = subOperation.size(); i < subOperationSize; i++)
//        {
//            DescOperation operation = subOperation.get(i);
//            if (apiName.equals(operation.apiName))
//                return i;
//        }
//        return -1;
//    }

}
