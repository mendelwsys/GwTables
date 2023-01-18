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
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.HilitesChangedEvent;
import com.smartgwt.client.widgets.grid.events.HilitesChangedHandler;

import java.util.List;

/**
 * Created by Anton.Pozdnev on 20.04.2015.
 */
public class CreateStyleOperation extends SimpleOperationP {

    String serialized_hilities = null;
    String applyToTable = null;
    protected MyHeaderControl pinUp;
    ListGridWithDesc grid = null;
    final HandlerRegistration[] handlerRegistrations = new HandlerRegistration[1];

    public CreateStyleOperation() {
        super(-1, -1, null);
    }

    public CreateStyleOperation(int operationId, int parentOperationId, String viewName) {
        super(operationId, parentOperationId, viewName);
    }

    public CreateStyleOperation(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    @Override
    public Canvas operate(Canvas dragTarget, IOperationContext ctx) {
        if (dragTarget instanceof Window) {
            Canvas[] items = ((Window) dragTarget).getItems();
            if (items != null && items.length > 0)
                return operate(items[0], null);
        } else if (dragTarget instanceof ListGridWithDesc) {


            final ListGridWithDesc newGrid = (ListGridWithDesc) dragTarget;
            // if (applyToTable == null) applyToTable = newGrid.getDesc().;
            // if (!applyToTable.equals(newGrid.getDesc().getType())) return null;
            if (newGrid.isMetaWasSet()) {
                List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
                int ix = findIndexInDesc(subOperation);
                if (ix >= 0)
                    return null;
            }

            final Window target = newGrid.getTarget();


            pinUp = (MyHeaderControl) createHeaderControl(newGrid, target);
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

            if (serialized_hilities != null) newGrid.setHiliteState(serialized_hilities);

            handlerRegistrations[0] = newGrid.addHilitesChangedHandler(new HilitesChangedHandler() {
                @Override
                public void onHilitesChanged(HilitesChangedEvent event) {
                    setStyleToDescOperation(((ListGrid) event.getSource()).getHiliteState());

                }
            });
//            newGrid.setCanEditHilites(true);
            grid = newGrid;
            if (newGrid.isMetaWasSet())
                setDescOperation(newGrid);
            else
                new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation() {

                    @Override
                    public boolean operate() {
                        if (newGrid.isMetaWasSet()) {
                            setDescOperation(newGrid);
                            return true;
                        }
                        return false;
                    }
                });


        }


        return super.operate(dragTarget, ctx);
    }

    private void setStyleToDescOperation(String hiliteState) {
        DescOperation descOperation;

        br:
        {
            List<DescOperation> subOperation = grid.getDescOperation().getSubOperation();
            String apiName = CreateStyleOperation.class.getName();
            for (DescOperation operation : subOperation) {
                if (apiName.equals(operation.apiName)) {
                    descOperation = operation;
                    break br;
                }
            }
            subOperation.add(descOperation = this.getDescOperation(new DescOperation()));
        }
        descOperation.put(HILITIES, hiliteState);
    }


    public Canvas getInputFrom(final Canvas _gGrid) {

        if (_gGrid instanceof ListGridWithDesc) {

            final ListGridWithDesc gGrid = (ListGridWithDesc) _gGrid;

            gGrid.editHilites();

            return null;

        } else {
            throw new UnsupportedOperationException("Can't get from with canvas type:" + _gGrid.getClass().getName());
        }

//                TextItem pchName = new TextItem();
//                pchName.setTitle("Данные дорог");
//                pchName.setRequired(true);
//
//                pchName.setValue(getStringParam());
//
//                ButtonItem button = new ButtonItem("Apply", "Применить");
//                button.addClickHandler(new ClickHandler() {
//                    public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
//                        setStringParam((String) form.getValues().values().iterator().next());
//                        if (_gGrid instanceof ListGridWithDesc) {
//                            ListGridWithDesc warnGrid = (ListGridWithDesc) _gGrid;
//                            warnGrid.applyFilters();
//                        }
//                    }
//                });
//                form.setFields(pchName, button);

    }

//    private void setDescOperation(ListGridWithDesc newGrid) {
//        List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
//        subOperation.add(this.getDescOperation(new DescOperation()));
//    }


    @Override
    public HeaderControl createHeaderControl(final Canvas canvas, Window target) {
        pinUp = new MyHeaderControl(
                HeaderControl.PERSON,
                new com.smartgwt.client.widgets.events.ClickHandler() {
                    public void onClick(ClickEvent event) {
                        Canvas form = getInputFrom(canvas);
                        //  Canvas rootCanvas = Canvas.getById(AppConst.t_MY_ROOT_PANEL);
                        //  rootCanvas.addChild(form);
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
        if (warnGrid instanceof ListGridWithDesc) {
            ListGridWithDesc newGrid = (ListGridWithDesc) warnGrid;
            ((ListGridWithDesc) warnGrid).setHilites(null);
            if (handlerRegistrations[0] != null) handlerRegistrations[0].removeHandler();
            List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
            int ix = findIndexInDesc(subOperation);
            if (ix >= 0)
                subOperation.remove(ix);
        }
        return super.onRemove(warnGrid, target);
    }


//    protected int findIndexInDesc(List<DescOperation> subOperation) {
//        String apiName = CreateStyleOperation.class.getName();
//        for (int i = 0, subOperationSize = subOperation.size(); i < subOperationSize; i++) {
//            DescOperation operation = subOperation.get(i);
//            if (apiName.equals(operation.apiName))
//                return i;
//        }
//        return -1;
//    }


    @Override
    public Object copy() {
        return super.copy();
    }

    @Override
    public IOperation createOperation(DescOperation descOperation) {

        return super.createOperation(descOperation);
    }

    @Override
    protected String getNameOperation(IOperation operation) {
        return super.getNameOperation(operation);
    }

    @Override
    protected IOperation getEmptyObject(DescOperation descOperation) {
        return new CreateStyleOperation();
    }

    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation) {
        CreateStyleOperation newOperation = (CreateStyleOperation) super.createOperation(descOperation, operation);
        newOperation.serialized_hilities = (String) descOperation.get(HILITIES);
        return newOperation;
    }

    public final static String HILITIES = "hilities";

    public DescOperation getDescOperation(DescOperation descOperation) {
        descOperation = super.getDescOperation(descOperation);
        descOperation.put(HILITIES, serialized_hilities);

        return descOperation;
    }

}
