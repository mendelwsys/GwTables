package com.mycompany.client.apps.App.api;

import com.mycompany.client.GridUtils;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.SimpleNewPortlet;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.OperationCtx;
import com.mycompany.common.DescOperation;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 17:28
 * Стандарная операция создания таблицы
 */
public class CreateEventTable extends SimpleNewPortlet
{

    protected String tableType;
    protected String fieldOrder;
    protected String fieldHidden;
    protected Boolean isMultiGroup;

    protected Boolean isMultiSort;
    protected String groupOrder;

    protected String sortOrderFields;
    protected String sortOrderDirection;
    private String functions;
    private String colNum;
    private String aggregates;
    private String fieldFormats;

    public String getDataURL() {
        return "transport/tdata2";
    }

    public String getHeaderURL() {
        return "theadDesc.jsp";
    }

    public String getEventsName() {
        return tableType;
    }


    protected CreateEventTable()
    {

    }

    public CreateEventTable(int operationId, int parentOperationId, String viewName, IOperation.TypeOperation type, String tableType)
    {
        super(operationId, parentOperationId, viewName, type);
        this.tableType=tableType;
    }

    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        if (ctx==null)
            ctx=this.getOperationCtx();

        Canvas canvas;
        if (ctx==null || ctx.getDst()==null)
        {

            canvas = super.operate(dragTarget, ctx);
            if (canvas instanceof Portlet)
            {
                Portlet portlet = (Portlet) canvas;
                portlet.setShowCloseConfirmationMessage(false);
                portlet.setDestroyOnClose(true);
            }

        }
        else
            canvas= (Canvas) ctx.getDst();//TODO !!! проверить применение контекста и модифицировать что бы процессор работал с контекстом по умолчанию!!!

        if (canvas instanceof Window)
        {
            final String dataURL = getDataURL();
            final String headerURL = getHeaderURL();

            Criteria criteria = new Criteria(TablesTypes.TTYPE, getEventsName());
            final ListGridWithDesc newGrid = createGrid(dataURL, headerURL, criteria);
            newGrid.setViewName(this.getViewName());
            newGrid.setTarget((Window) canvas); //TODO Этот вопрос проверить, наверняка есть способ лучше, определение обрамляющего окна

            newGrid.addDropHandler(
                    new EventTableDropHandler(newGrid));


            //TODO Здесь к гриду еще прицепить фильтр, после этого сделать класс общий для всех


            newGrid.setWidth100();
            newGrid.setHeight100();

            newGrid.getTarget().addItem(newGrid);
            newGrid.setDescOperation(this.getDescOperation(new DescOperation()));

            if (ctx!=null)
                ctx.getChildList().add(new OperationCtx(newGrid,null));

        }

        return canvas;
    }

    protected ListGridWithDesc createGrid(String dataURL, String headerURL, Criteria criteria)
    {
        ListGridWithDesc gridTable = GridUtils.createGridTable(NodesHolder.gridMetaProvider, criteria, headerURL, dataURL, false, true);
        gridTable.setGroupByMaxRecords(10000);
        if (isMultiGroup!=null) gridTable.setCanMultiGroup(isMultiGroup);
        if (isMultiSort!=null) gridTable.setCanMultiSort(isMultiSort);
        return gridTable;
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateEventTable();
    }

    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        CreateEventTable retOperation= (CreateEventTable) super.createOperation(descOperation,operation);
        retOperation.tableType=(String)descOperation.get(ListGridWithDesc.EVENTS_NAME);
        retOperation.fieldOrder=(String)descOperation.get(ListGridWithDesc.FIELD_ORDER);
        retOperation.fieldHidden=(String)descOperation.get(ListGridWithDesc.FIELD_HIDDEN);
        retOperation.isMultiGroup=(Boolean)descOperation.get(ListGridWithDesc.M_GROUP);
        retOperation.isMultiSort=(Boolean)descOperation.get(ListGridWithDesc.M_SORT);
        retOperation.groupOrder=(String)descOperation.get(ListGridWithDesc.M_GROUP_ORDER);
        retOperation.sortOrderFields=(String)descOperation.get(ListGridWithDesc.M_SORT_ORDER);
        retOperation.sortOrderDirection=(String)descOperation.get(ListGridWithDesc.M_SORT_ORDER_DIRECTION);
        retOperation.functions=(String)descOperation.get(ListGridWithDesc.M_FUNCTION);
        retOperation.colNum=(String)descOperation.get(ListGridWithDesc.M_COLNUM);
        retOperation.aggregates = (String) descOperation.get(ListGridWithDesc.FIELD_AGGREGATES);
        retOperation.fieldFormats = (String) descOperation.get(ListGridWithDesc.FIELD_FORMATS);
        return retOperation;
    }


    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);
        descOperation.put(ListGridWithDesc.EVENTS_NAME, getEventsName());
        descOperation.put(ListGridWithDesc.FIELD_HIDDEN, fieldHidden);
        descOperation.put(ListGridWithDesc.FIELD_ORDER, fieldOrder);
        descOperation.put(ListGridWithDesc.M_GROUP, isMultiGroup);

        descOperation.put(ListGridWithDesc.M_SORT,isMultiSort);
        descOperation.put(ListGridWithDesc.M_GROUP_ORDER,groupOrder);

        descOperation.put(ListGridWithDesc.M_SORT_ORDER,sortOrderFields);
        descOperation.put(ListGridWithDesc.M_SORT_ORDER_DIRECTION,sortOrderDirection);
        descOperation.put(ListGridWithDesc.M_FUNCTION,functions);
        descOperation.put(ListGridWithDesc.M_COLNUM,colNum);
        descOperation.put(ListGridWithDesc.FIELD_AGGREGATES, aggregates);
        descOperation.put(ListGridWithDesc.FIELD_FORMATS, fieldFormats);

        return descOperation;
    }
}
