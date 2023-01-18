package com.mycompany.client.apps.App.api;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.ReportListGridWithDesc;
import com.mycompany.client.apps.App.ISyncHandler;
import com.mycompany.client.apps.App.SimpleNewPortlet;
import com.mycompany.client.apps.App.reps.IReportCreator;
import com.mycompany.client.apps.App.reps.ReportsTableDropHandler;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 08.07.15
 * Time: 18:59
 * Базовый класс для некоторых сводных таблиц.
 */
abstract public class CreateBaseReport extends SimpleNewPortlet
{
    protected String fieldOrder;
    protected String fieldHidden;

    public CreateBaseReport(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected CreateBaseReport()
    {
    }





    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        Canvas canvas = super.operate(dragTarget, ctx);
        if (canvas instanceof Portlet)
        {
            Portlet portlet = (Portlet) canvas;
            portlet.setShowCloseConfirmationMessage(false);
            portlet.setDestroyOnClose(true);
        }
        if (canvas instanceof Window)
        {
            final ListGridWithDesc newGrid = new ReportListGridWithDesc();
            newGrid.setShowRollOverCanvas(true);
            newGrid.setUseCellRollOvers(true);

            final IReportCreator reportCreator =getReportCreator();
            this.syncHandler = new ISyncHandler() {
                @Override
                public boolean isCompletely() {
                    return reportCreator.isCompletely();
                }
            };

            newGrid.setRolloverHandler(new ListGridWithDesc.IRolloverHandler()
            {
                MyImgButton editImg1;
                ImgButton editImg2;
                ListGrid lg = null;

                @Override
                public Canvas handleRollover(Integer rowNum, Integer colNum)
                {
                    if (colNum!=null && reportCreator.isGroupClickAble(rowNum,colNum) && reportCreator.isCellClickAble(rowNum,colNum))
                    {
//                            if ()
                            {
                                ListGridRecord rollOverRecord=lg.getRecord(rowNum);
                                String grp = rollOverRecord.getAttribute("isGroupSummary");
                                if (grp!=null)
                                {
                                    if (editImg1==null)
                                    {
                                        editImg1=new MyImgButton();
                                        editImg1.setSrc("vthumb_grip.png");
                                        editImg1.setShowDown(false);
                                        editImg1.setShowRollOver(false);
                                        editImg1.setPrompt("Окно");
                                        editImg1.setSnapTo("R");
                                        editImg1.setWidth(10);
                                        editImg1.setHeight(10);

                                        editImg1.addClickHandler(new ClickHandler() {
                                            @Override
                                            public void onClick(ClickEvent event) {
                                                     reportCreator.openEventsOnGroupClick(editImg1.getRowNum(), editImg1.getColNum(), null);
                                            }
                                        });

                                    }
                                    editImg1.setColNum(colNum);
                                    editImg1.setRowNum(rowNum);
                                    editImg1.setVisible(true);

                                    if (editImg2!=null)
                                    {
                                        editImg2.setVisible(false);
                                        lg.getMember(1).removeChild(editImg2);
                                    }
                                    return editImg1;
                                }
                                else
                                {
                                    if (editImg2==null)
                                    {
                                        editImg2=new ImgButton();
                                        editImg2.setSrc("vthumb_start.png");
                                        editImg2.setShowDown(false);
                                        editImg2.setShowRollOver(false);
                                        editImg2.setSnapTo("B");
                                        editImg2.setCursor(Cursor.DEFAULT);
                                        editImg2.setWidth(16);
                                        editImg2.setHeight(2);
                                    }
                                    editImg2.setVisible(true);
                                    if (editImg1!=null)
                                    {
                                        editImg1.setVisible(false);
                                        lg.getMember(1).removeChild(editImg1);
                                    }

                                    return editImg2;
                                }
                            }

                    }
                    if (editImg1!=null)
                    {
                        editImg1.setVisible(false);
                        lg.getMember(1).removeChild(editImg1);
                    }

                    if (editImg2!=null)
                    {
                        editImg2.setVisible(false);
                        lg.getMember(1).removeChild(editImg2);
                    }
                    return null;
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

            initReportGrid((Window) canvas, newGrid,reportCreator);
            newGrid.setDescOperation(this.getDescOperation(new DescOperation()));
        }

        return canvas;
    }


    private ISyncHandler syncHandler;

    public ISyncHandler getSyncHandler()
    {
        return syncHandler;
    }

    protected void initReportGrid(Window canvas, ListGridWithDesc newGrid,final IReportCreator reportCreator)
    {

//        newGrid.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                event.getSource();
//            }
//        });

        newGrid.addCellClickHandler(new CellClickHandler() {
            @Override
            public void onCellClick(CellClickEvent event) {

                String grp = event.getRecord().getAttribute("isGroupSummary");
                if (grp ==null)
                {
                    reportCreator.onCellClickEvent(event.getRowNum(),event.getColNum(), null);
                }
            }
        });
        newGrid.setCanAcceptDrop(true);
        newGrid.setCanAcceptDroppedRecords(true);
        newGrid.addDropHandler(new ReportsTableDropHandler(newGrid));
//        reportCreator = getReportCreator();
        reportCreator.setGrid(canvas, newGrid);

    }

    abstract protected IReportCreator getReportCreator();

    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        final CreateBaseReport retOperation = (CreateBaseReport) super.createOperation(descOperation, operation);
        retOperation.fieldOrder=(String)descOperation.get(ListGridWithDesc.FIELD_ORDER);
        retOperation.fieldHidden=(String)descOperation.get(ListGridWithDesc.FIELD_HIDDEN);
        return retOperation;
    }

    abstract protected IOperation getEmptyObject(DescOperation descOperation);
//    {
//        throw new UnsupportedOperationException("Can't create BaseReprot class, you should redefine getEmptyObject operation")
//    }


    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);
        descOperation.put(ListGridWithDesc.FIELD_HIDDEN, fieldHidden);
        descOperation.put(ListGridWithDesc.FIELD_ORDER, fieldOrder);
        return descOperation;
    }


    private class MyImgButton extends ImgButton
    {

        public Integer getRowNum() {
            return rowNum;
        }

        public void setRowNum(Integer rowNum) {
            this.rowNum = rowNum;
        }

        public Integer getColNum() {
            return colNum;
        }

        public void setColNum(Integer colNum) {
            this.colNum = colNum;
        }

        Integer rowNum;
        Integer colNum;

        private MyImgButton() {
        }



    }

}
