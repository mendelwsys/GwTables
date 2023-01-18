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

import java.util.Arrays;
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

public class DrillRepOperation extends SimpleOperationP
{

    protected MyHeaderControl pinUp;


    protected DrillRepOperation()
    {
       this(-1, -1, null,null,true);
    }



//    public DrillRepOperation(int operationId, int parentOperationId, String viewName) {
//        super(operationId, parentOperationId, viewName);
//    }

    public DrillRepOperation(int operationId, int parentOperationId, String viewName, TypeOperation type,boolean needToAddTermDesc) {
        super(operationId, parentOperationId, viewName, type);
        this.needToAddTermDesc=needToAddTermDesc;
    }

    public DrillRepOperation(boolean corDescriptorOperation,DescOperation repDescriptorOperation)
    {
        super(-1, -1, null, TypeOperation.NON);
        this.needToAddTermDesc=false;
        this.corDescriptorOperation=corDescriptorOperation;
        this.repDescriptorOperation=repDescriptorOperation;
    }


    private String oldTitle;
    private Canvas[] oldHeaderControls;
    private List oldSubOperations;
    private HandlerRegistration closeHandlerRegistration;

    private boolean corDescriptorOperation=false;
    private DescOperation repDescriptorOperation;

    private boolean needToAddTermDesc=true; //При создании обязательно добавляет последнюю операцию, сделано для того что бы не хранить ее в БД

    @Override
    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {

        if (ctx==null)
            ctx=this.getOperationCtx();

        if (corDescriptorOperation)
        {
            final Window target = (Window) ctx.getDst();
            Canvas[] items = target.getItems();
            if (items!=null && items.length>0)
            {
                final ListGridWithDesc tabGrid = (ListGridWithDesc) items[0];
                if (tabGrid.isMetaWasSet())
                    completeDrillOperation(target, tabGrid);
                else
                    new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation() {
                        @Override
                        public boolean operate()
                        {
                            if (tabGrid.isMetaWasSet())
                            {
                                completeDrillOperation(target, tabGrid);
                                return true;
                            }
                            return false;
                        }
                    });

            }

            return null;
        }

        if (dragTarget instanceof Window)
        {
            Canvas[] items = ((Window) dragTarget).getItems();
            if (items!=null && items.length>0)
            {
                if (needToAddTermDesc)
                {
                    final OperationHolder currentHolderOperation = App01.GUI_STATE_DESC.getProcessor().getCurrentOperationHolder();
                    final List<OperationHolder> subHolder = currentHolderOperation.getSubHolders();


                    final DrillRepOperation operation = new DrillRepOperation(true,((ListGridWithDesc) items[0]).getDescOperation());
                    subHolder.add(new OperationHolder(operation, operation.getDescOperation(new DescOperation()))); //Добавим операцию корректировки описаетля для грида

                    final OperationCtx performCtx = new OperationCtx(null, dragTarget);
                    for (OperationHolder operationHolder : subHolder)
                        operationHolder.getOperation().setOperationCtx(performCtx);
                }
                return operate(items[0], ctx);
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
                oldSubOperations = new LinkedList<Canvas>(repGrid.getDescOperation().getSubOperation());


                if (oldHeaderControls!=null)
                {
                    List<Canvas> changeList = new LinkedList<Canvas>();
                    for (Canvas oldHeaderControl : oldHeaderControls)
                        if (!(oldHeaderControl instanceof MyHeaderControl) || (((MyHeaderControl) oldHeaderControl).getOperation() instanceof ExcelExportOperation))
                            changeList.add(oldHeaderControl);
                    if (changeList.size()!=oldHeaderControls.length)
                        header.setMembers(changeList.toArray(new Canvas[changeList.size()]));
                }
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

    private void completeDrillOperation(Window target, ListGridWithDesc tabGrid) {
        final List<DescOperation> subOperation = repDescriptorOperation.getSubOperation();
        final DescOperation descOperation = tabGrid.getDescOperation();
        subOperation.get(0).getSubOperation().add(descOperation);
        tabGrid.setDescOperation(repDescriptorOperation);//Установим правильный описатель грида
        tabGrid.setApiName(descOperation.apiName);
        target.setTitle(tabGrid.getViewName());

        final HLayout header = target.getHeader();
        Canvas[] controls = header.getMembers();

        for (Canvas control : controls)
            if ((control instanceof MyHeaderControl) && (((MyHeaderControl) control).getOperation() instanceof ExcelExportOperation))
                ((MyHeaderControl) control).setGrid(tabGrid);
    }

    @Override
    public HeaderControl createHeaderControl(final Canvas repGrid, final Window target)
    {
        pinUp = new MyHeaderControl(
                HeaderControl.DOUBLE_ARROW_UP,
                createArrowClickHandler(repGrid, target));
        pinUp.setGrid(repGrid);
        pinUp.setOperation(this);
        pinUp.setTarget(target);
        pinUp.setCanDrag(true);
        pinUp.setCanDrop(true);
        pinUp.setTooltip(this.getViewName());
        return pinUp;
    }

    protected ArrowClickHandler createArrowClickHandler(Canvas repGrid, Window target) {
        return new ArrowClickHandler(target, repGrid);
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
            closeHandlerRegistration.removeHandler();
        }

        if (_repGrid instanceof ListGridWithDesc)
        {
            ListGridWithDesc repGrid = (ListGridWithDesc) _repGrid;

            final DescOperation descOperation = repGrid.getDescOperation();

//            List<DescOperation> subOperation = descOperation.getSubOperation();
//            int ix=findIndexInDesc(subOperation);
//            if (ix>=0)
//                  subOperation.remove(ix);

            int ix=findIndexInDesc(oldSubOperations,ExcelExportOperation.class);
            if (ix>=0)
                  oldSubOperations.remove(ix);

            descOperation.setSubOperation(oldSubOperations);

            final List<Canvas> newHeaderControls=new LinkedList<Canvas>();
            boolean isMyHeaderControl=false;
            {
                for (Canvas oldHeaderControl : oldHeaderControls)
                {
                    if ((oldHeaderControl instanceof MyHeaderControl) && !(((MyHeaderControl) oldHeaderControl).getOperation() instanceof ExcelExportOperation))
                    {
                        isMyHeaderControl=true;
                        newHeaderControls.add(oldHeaderControl);
                    }
                }
            }

            final Portlet portlet = new Portlet();
            //if (isMyHeaderControl)
            { //Необходимо для того что бы сохранить наложенные на грид операции
                final HandlerRegistration[] rs = new HandlerRegistration[1];
                rs[0] = portlet.addDrawHandler(new DrawHandler() {
                    @Override
                    public void onDraw(DrawEvent event)
                    {
                        NodesHolder.addHeaderCtrl(portlet, newHeaderControls.toArray(new Canvas[newHeaderControls.size()]));
                        rs[0].removeHandler();
                    }
                });
            }

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
        return new DrillRepOperation();
    }

    protected class ArrowClickHandler implements ClickHandler {
        private final Window target;
        private final Canvas repGrid;

        public ArrowClickHandler(Window target, Canvas repGrid) {
            this.target = target;
            this.repGrid = repGrid;
        }

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

            {
                for (Canvas oldHeaderControl : oldHeaderControls)
                    if ((oldHeaderControl instanceof MyHeaderControl) && (((MyHeaderControl) oldHeaderControl).getOperation() instanceof ExcelExportOperation))
                        ((MyHeaderControl) oldHeaderControl).setGrid(repGrid);
            }


            header.setMembers(oldHeaderControls);
            target.setTitle(oldTitle);
        }
    }
}

