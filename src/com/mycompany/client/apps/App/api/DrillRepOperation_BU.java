package com.mycompany.client.apps.App.api;


import com.google.gwt.event.shared.HandlerRegistration;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.MyHeaderControl;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.SimpleOperationP;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.OperationCtx;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Portlet;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 28.03.15
 * Time: 14:14
 * Операция по проваливанию в таблицу отчетности
 */

public class DrillRepOperation_BU extends SimpleOperationP
{

    protected MyHeaderControl pinUp;


    protected DrillRepOperation_BU()
    {
       this(-1, -1, null);
    }

    public DrillRepOperation_BU(int operationId, int parentOperationId, String viewName) {
        super(operationId, parentOperationId, viewName);
    }

    public DrillRepOperation_BU(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }


    private String oldTitle;
    private Canvas[] oldHeaderControls;
    private List oldSubOperations;
    private HandlerRegistration closeHandlerRegistration;

    private boolean corDescriptorOperation =false;
    private DescOperation repDescriptorOperation;

    @Override
    public Canvas operate(final Canvas dragTarget, final IOperationContext ctx)
    {
        if (corDescriptorOperation)
        {
            Window target = (Window) ctx.getDst();
            Canvas[] items = target.getItems();
            if (items!=null && items.length>0)
            {
               final ListGridWithDesc tabGrid = (ListGridWithDesc) items[0];
               final List<DescOperation> subOperation = repDescriptorOperation.getSubOperation();
               final DescOperation descOperation = tabGrid.getDescOperation();
               subOperation.get(0).getSubOperation().add(descOperation);
               tabGrid.setDescOperation(repDescriptorOperation);//Установим правильный описатель грида
               tabGrid.setApiName(descOperation.apiName);
               target.setTitle(tabGrid.getViewName());
            }

            return null;
        }

        if (dragTarget instanceof Window)
        {
            Canvas[] items = ((Window) dragTarget).getItems();
            if (items!=null && items.length>0)
                return operate(items[0], ctx);
            else
            {

                final OperationHolder currentHolderOperation = App01.GUI_STATE_DESC.getProcessor().getCurrentOperationHolder();
                final List<OperationHolder> subHolder = currentHolderOperation.getSubHolders();
                currentHolderOperation.setSubHolders(new LinkedList());//Не позволяем дальнейшее асинхронное исполнение для данного описателя
                new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
                {

                    @Override
                    public boolean operate()
                    {
                       Canvas[] items = ((Window) dragTarget).getItems();
                        if (items!=null && items.length>0)
                        {

                            final DrillRepOperation_BU operation = new DrillRepOperation_BU(-100, -100, "");
                            {
                                operation.corDescriptorOperation =true;
                                final ListGridWithDesc repTable = (ListGridWithDesc) items[0];
                                operation.repDescriptorOperation= repTable.getDescOperation();
                            }
                            subHolder.get(0).getSubHolders().add(new OperationHolder(operation,operation.getDescOperation(new DescOperation()))); //Добавим операцию корректировки описаетля для грида
                            currentHolderOperation.setSubHolders(subHolder);

                            final MainProcessor processor = App01.GUI_STATE_DESC.getProcessor();
                            processor.operateAll(dragTarget, currentHolderOperation, new OperationCtx(null, dragTarget));
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
        else if (dragTarget instanceof ListGridWithDesc)
        {
            final ListGridWithDesc repGrid = (ListGridWithDesc) dragTarget;

            if (repGrid.isMetaWasSet())
            {
                List<DescOperation> subOperation = repGrid.getDescOperation().getSubOperation();
                int ix=findIndexInDesc(subOperation);
                if (ix>=0) //Нужно что бы запретить дублирование операций
                    return null;
            }


            final Window target = repGrid.getTarget();


            {
                oldTitle=target.getTitle();
                final HLayout header = target.getHeader();
                oldHeaderControls = header.getMembers();
                oldSubOperations = new LinkedList(repGrid.getDescOperation().getSubOperation());
            }


            final HeaderControl pinUp = createHeaderControl(repGrid, target);
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

            closeHandlerRegistration = target.addCloseClickHandler(new CloseClickHandler() {
                @Override
                public void onCloseClick(CloseClickEvent event)
                {
                    repGrid.getCtrl().stopUpdateData();
                    repGrid.markForDestroy();
                }
            });


            target.removeItem(repGrid);
            repGrid.setVisible(false);

            if (repGrid.isMetaWasSet())
                setDescOperation(repGrid);
            else
                new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
                {

                    @Override
                    public boolean operate()
                    {
                        if (repGrid.isMetaWasSet())
                        {
                            setDescOperation(repGrid);
                            return true;
                        }
                        return false;
                    }
                });
        }
        return super.operate(dragTarget,ctx);
    }

    @Override
    public HeaderControl createHeaderControl(final Canvas repGrid, final Window target)
    {
        pinUp = new MyHeaderControl(
                HeaderControl.DOUBLE_ARROW_UP,
                new ClickHandler()
                {
                    public void onClick(ClickEvent event)
                    {
                        Canvas[] items = target.getItems();
                        for (Canvas cnv : items)
                            target.removeItem(cnv);

                        final DescOperation descOperation = ((ListGridWithDesc) repGrid).getDescOperation();

                        Map<String,DescOperation> descMapParams = (Map<String, DescOperation>) descOperation.getDescMapParams();
                        if (descMapParams!=null)
                        {
                            ListGridWithDesc tabGrid = (ListGridWithDesc) items[0];
                            List<DescOperation> subOperation = tabGrid.getDescOperation().getSubOperation();
                            final DescOperation drillOperation = subOperation.get(0);
                            List<DescOperation> subOperation1 = drillOperation.getSubOperation();
                            DescOperation tabDesc = subOperation1.get(0);
                            tabGrid.setDescOperation(tabDesc);
                            tabDesc=tabGrid.getDescOperation();//Собрать сведения в дескриптор
                            tabDesc.setSubOperation(new LinkedList());//Сбросить поддескрипторы
                            String tabName = (String) tabDesc.get(ListGridWithDesc.EVENTS_NAME);
                            descMapParams.put(tabName,tabDesc);
                        }


                        for (Canvas cnv : items)
                            if (cnv instanceof ListGridWithDesc)
                            {
                                final ListGridWithDesc tabGrid = (ListGridWithDesc) cnv;
                                tabGrid.getCtrl().stopUpdateData();
                            }
                        for (Canvas cnv : items)
                            cnv.markForDestroy();



                        descOperation.setSubOperation(oldSubOperations);

                        closeHandlerRegistration.removeHandler();
                        target.addItem(repGrid);
                        repGrid.setVisible(true);
                        ((ListGridWithDesc) repGrid).setTarget(target);
                        HLayout header = target.getHeader();
                        header.setMembers(oldHeaderControls);
                        target.setTitle(oldTitle);
                    }
                });
        pinUp.setGrid(repGrid);
        pinUp.setOperation(this);
        pinUp.setTarget(target);
        pinUp.setCanDrag(true);
        pinUp.setCanDrop(true);
        pinUp.setTooltip(this.getViewName());
        return pinUp;
    }

    @Override
    public Canvas onRemove(Canvas _repGrid, Window target)
    {
        Canvas[] items = target.getItems();
        if (items!=null && items.length>0)
        {
            ListGridWithDesc tabGrid = (ListGridWithDesc) items[0];
            List<DescOperation> subOperation = tabGrid.getDescOperation().getSubOperation();
            final DescOperation drillOperation = subOperation.get(0);
            List<DescOperation> subOperation1 = drillOperation.getSubOperation();
            tabGrid.setDescOperation(subOperation1.get(0));
            tabGrid.setApiName(null);
        }

        if (_repGrid instanceof ListGridWithDesc)
        {
            ListGridWithDesc repGrid = (ListGridWithDesc) _repGrid;
            List<DescOperation> subOperation = repGrid.getDescOperation().getSubOperation();
            int ix=findIndexInDesc(subOperation);
            if (ix>=0)
                  subOperation.remove(ix);

            Portlet portlet = new Portlet();
            portlet.setTitle(oldTitle);
            portlet.setShowCloseConfirmationMessage(false);
            portlet.addItem(repGrid);
            repGrid.setVisible(true);
            repGrid.setTarget(portlet);
            return portlet;
        }

        return super.onRemove(_repGrid, target);
    }

    @Override
    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new DrillRepOperation_BU();
    }
}

