package com.mycompany.client.apps.App.api;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.MyHeaderControl;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.SimpleOperationP;
import com.mycompany.client.integration.*;
import com.mycompany.client.integration.commands.ICommand;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.security.IUser;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.List;

/**
 * Created by Anton.Pozdnev on 02.03.2015.
 */
public class GISSelectEventOperation extends SimpleOperationP implements IMessageReceivedHandler {


    protected MyHeaderControl pinUp;
    private IMessageDispatcher dispatcher = null;

    public GISSelectEventOperation() {
        super(-1, -1, null);
        setupMessageDispatcher();
    }


    private void setupMessageDispatcher() {
        dispatcher = new MessageDispatcher(this);
        IIntegrationSettings settings = setupSettings();
        dispatcher.setIntegrationSettings(settings);
        dispatcher.setMessageReceivedHandler(this);
    }

    private IIntegrationSettings setupSettings() {
        IIntegrationSettings settings = new IntegrationSettings();
        settings.addAcceptedMessageType(IMessageTypes.MESSAGE_POSITION_ON_EVENTS, new ICommand() {
            @Override
            public void execute(JSONObject obj) {

            }

            @Override
            public String getDescription() {
                return "Позиционирование на событии";
            }
        });


        return settings;
    }

    public GISSelectEventOperation(int operationId, int parentOperationId, String viewName) {
        super(operationId, parentOperationId, viewName);
        setupMessageDispatcher();
    }

    public GISSelectEventOperation(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
        setupMessageDispatcher();
    }

//    @Override
//    public DescOperation getDescOperation(DescOperation descOperation) {
//        descOperation = super.getDescOperation(descOperation);
//        descOperation.apiName = GISSelectEventOperation.class.getName();
//
//        return descOperation;
//    }

    @Override
    public boolean isViewAsFolder() {
        return super.isViewAsFolder();
    }

    @Override
    public void setViewAsFolder(boolean viewAsFolder) {
        super.setViewAsFolder(viewAsFolder);
    }

    @Override
    public String getFolderName() {
        return super.getFolderName();
    }

    @Override
    public void setFolderName(String folderName) {
        super.setFolderName(folderName);
    }

    @Override
    public int getParentOperationId() {
        return super.getParentOperationId();
    }

    @Override
    public void setParentOperationId(int parentOperationId) {
        super.setParentOperationId(parentOperationId);
    }

    @Override
    public void setOperationId(int operationId) {
        super.setOperationId(operationId);
    }

    @Override
    public int getOperationId() {
        return super.getOperationId();
    }

    @Override
    public String getViewName() {
        return super.getViewName();
    }

    @Override
    public void setViewName(String viewName) {
        super.setViewName(viewName);
    }

    @Override
    public TypeOperation getTypeOperation() {
        return super.getTypeOperation();
    }

    @Override
    public void setTypeOperation(TypeOperation type) {
        super.setTypeOperation(type);
    }

    @Override
    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        if (dragTarget instanceof Window)
        {
            Canvas[] items = ((Window) dragTarget).getItems();
            if (items!=null && items.length>0)
                return operate(items[0], null);
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

            newGrid.setAnimateRollOver(false);
            newGrid.setRolloverHandler(new ListGridWithDesc.IRolloverHandler() {

                ListGrid lg = null;
                private HLayout rollOverCanvas;
                private ListGridRecord rollOverRecord;

                @Override
                public Canvas handleRollover(Integer row, Integer col) {
                    rollOverRecord = lg.getRecord(row);

                    if (rollOverCanvas == null) {
                        rollOverCanvas = new HLayout(3);
                        rollOverCanvas.setSnapTo("TR");
                        rollOverCanvas.setWidth(50);
                        rollOverCanvas.setHeight(22);
                        rollOverCanvas.setAlign(VerticalAlignment.CENTER);
                        ImgButton editImg = new ImgButton();
                        editImg.setShowDown(false);
                        editImg.setShowRollOver(false);
                        editImg.setLayoutAlign(Alignment.CENTER);
                        editImg.setValign(VerticalAlignment.CENTER);
                        editImg.setLayoutAlign(VerticalAlignment.CENTER);
                        editImg.setSrc("target.png");
                        editImg.setPrompt("Показать на карте");
                        editImg.setHeight(16);
                        editImg.setWidth(16);


                        editImg.addClickHandler(new ClickHandler() {

                            public void onClick(ClickEvent event) {
                               // Map m = rollOverRecord.toMap();
                              //  GWT.log("" + m);

                                try {
                                    String eventIds [] = (""+rollOverRecord.toMap().get(TablesTypes.KEY_FNAME)).split("##");

                                    dispatcher.positionOnEvents(new String[]{""+rollOverRecord.toMap().get(TablesTypes.KEY_FNAME)});
                                } catch (GISIntegrationException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        rollOverCanvas.addMember(editImg);

                    }
                    // rollOverCanvas.setHeight(lg.getDrawnRowHeight(row));

                    return rollOverCanvas;


                }

                @Override
                public void setListGrid(ListGrid lg) {
                    this.lg = lg;
                }

                @Override
                public ListGrid getListGrid() {
                    return lg;
                }
            });
            newGrid.setShowRollOverCanvas(true);
            // Disable the rollUnderCanvas because we're not using it.
//todo NEW            newGrid.setShowRollUnderCanvas(false);

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

//    private void setDescOperation(ListGridWithDesc newGrid)
//    {
//        List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
//        subOperation.add(this.getDescOperation(new DescOperation()));
//    }

    @Override
    public Canvas getInputFrom(Canvas warnGrid) {
        return super.getInputFrom(warnGrid);
    }

    @Override
    public HeaderControl createHeaderControl(final Canvas canvas, Window target) {
        pinUp = new MyHeaderControl(
                HeaderControl.PLUS,
                new com.smartgwt.client.widgets.events.ClickHandler() {
                    public void onClick(ClickEvent event) {

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
            newGrid.setRolloverHandler(null);
            List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
            int ix=findIndexInDesc(subOperation);
            if (ix>=0)
                  subOperation.remove(ix);
        }
        return super.onRemove(warnGrid, target);
    }


//    protected int findIndexInDesc(List<DescOperation> subOperation)
//    {
//        String apiName=GISSelectEventOperation.class.getName();
//        for (int i = 0, subOperationSize = subOperation.size(); i < subOperationSize; i++)
//        {
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
        return new GISSelectEventOperation();
    }

    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation) {
        GISSelectEventOperation newOperation = (GISSelectEventOperation) super.createOperation(descOperation, operation);

        return newOperation;
    }

    @Override
    public void setStringParam(String param) {
        super.setStringParam(param);
    }

    @Override
    public String getStringParam() {
        return super.getStringParam();
    }

    @Override
    public void setParams(List params) {
        super.setParams(params);
    }

    @Override
    public List getParams() {
        return super.getParams();
    }


    @Override
    public void onUserAuthMessage(IUser u) {

    }

    @Override
    public void onPositionOnEventsMessage(JSONObject response) {

    }

    @Override
    public void onGetSelectedObjectsMessage(JSONObject response) {

    }

    @Override
    public void onPositionOnObjectMessage(JSONObject response) {

    }

    @Override
    public void onError(ICommand command, JavaScriptObject objm, String errorText, String error) {

    }
}
