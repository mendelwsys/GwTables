package com.mycompany.client.apps.App.api;

import com.mycompany.client.GridUtils;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.LentaGridConstructor;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.SimpleNewPortlet;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.OperationCtx;
import com.mycompany.client.test.aggregates.AggregatesSummariesBuilderDialog;
import com.mycompany.common.DescOperation;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.ExpansionComponentPoolingMode;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordCollapseEvent;
import com.smartgwt.client.widgets.grid.events.RecordCollapseHandler;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 17:28
 * Стандарная операция создания таблицы ленты событий
 */
public class CreateLentaTable extends SimpleNewPortlet
{


    private String tableType;
    private String fieldOrder;
    private String fieldHidden;

    protected Boolean isMultiGroup;

    protected Boolean isMultiSort;
    protected String groupOrder;

    protected String sortOrderFields;
    protected String sortOrderDirection;

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

    protected CreateLentaTable()
    {
    }

    public CreateLentaTable(int operationId, int parentOperationId, String viewName, TypeOperation type, String tableType)
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
            canvas= (Canvas) ctx.getDst();

        if (canvas instanceof Window)
        {
            final String dataURL = getDataURL();
            final String headerURL = getHeaderURL();

            String[] tableTypes=new String[]{TablesTypes.VIP_GID,TablesTypes.REFUSES,TablesTypes.VIOLATIONS,TablesTypes.WINDOWS,TablesTypes.WARNINGS};
//            String[] tableTypes=new String[]{TablesTypes.VIP_GID,TablesTypes.VIOLATIONS};
//            String[] tableTypes=new String[]{TablesTypes.VIP_GID,TablesTypes.WINDOWS};

            Criteria criteria = new Criteria();
            criteria.addCriteria(TablesTypes.TTYPE,tableTypes);
            criteria.addCriteria(TablesTypes.JT2ID,"1");

            final Map<String,List<ListGridField>> type2Fields= new HashMap<String,List<ListGridField>>();


            final ListGridWithDesc newGrid = GridUtils.createGridTable(

                    new LentaGridConstructor(type2Fields),
                    new GridUtils.DefaultGridFactory()
                    {
                        protected ListGridWithDesc _createGrid()
                        {

                            final Map<Object,ListGridWithDesc> id2SubGrid= new HashMap<Object,ListGridWithDesc>();

                            final ListGridWithDesc gridWithDesc = new ListGridWithDesc()
                            {



                                public void destroy()
                                {

                                    for (Object key : id2SubGrid.keySet())
                                    {
                                        ListGridWithDesc subGrid = id2SubGrid.remove(key);
                                        if (subGrid!=null)
                                        {
                                            subGrid.setData();
                                            subGrid.setFields();
                                            subGrid.markForDestroy();
                                        }
                                    }
//                                    for (ListGridWithDesc gridWithDesc : pool)
//                                    {
//                                        gridWithDesc.setData();
//                                        gridWithDesc.setFields();
//                                        gridWithDesc.markForDestroy();
//                                    }
                                    super.destroy();
                                }

                                @Override
                                protected MenuItem[] getHeaderContextMenuItems(final Integer fieldNum) {
                                    final MenuItem[] items = super.getHeaderContextMenuItems(fieldNum);
                                    if (!this.isGrouped()) return items;
                                    MenuItem customItem = new MenuItem("Настройка агрегатов...");
                                    final ListGrid lg = this;
                                    customItem.addClickHandler(new ClickHandler() {
                                        public void onClick(MenuItemClickEvent event) {
                                            //SC.say("Hello Column : " + fieldNum);
                                            Window w = AggregatesSummariesBuilderDialog.createAggregatesEditorWindow(lg);
                                            w.show();
                                        }
                                    });
                                    MenuItem[] newItems = new MenuItem[items.length + 1];
                                    for (int i = 0; i < items.length; i++) {
                                        MenuItem item = items[i];
                                        newItems[i] = item;
                                    }
                                    newItems[items.length] = customItem;
                                    return newItems;
                                }

                                @Override
                               protected Canvas getExpansionComponent(final ListGridRecord record)
                               {
                                   final String tableType=record.getAttribute(TablesTypes.EVTYPE);
                                   final Object id=record.getAttribute(TablesTypes.KEY_FNAME);

                                       ListGridWithDesc subGrid;
//                                       if (pool.size()>0)
//                                            subGrid=pool.remove(0);
//                                       else
                                       {
                                            subGrid =new ListGridWithDesc();
                                            subGrid.setHeight(150);

                                            subGrid.setHeaderHeight(35);
                                            subGrid.setWrapCells(true);
                                            subGrid.setCellHeight(45);
                                       }
                                       id2SubGrid.put(id,subGrid);
                                       final List<ListGridField> listGridFields = type2Fields.get(tableType);
                                       subGrid.setFields(listGridFields.toArray(new ListGridField[listGridFields.size()]));
                                       subGrid.setData(record);

                                   return subGrid;
                               }


                                public String getCellCSSText(ListGridRecord record, int rowNum, int colNum)
                                {
//                                    if (record instanceof MyRecord)
//                                    {
//                                        String rowStyle = ((MyRecord) record).getRowStyle();
//                                        if (rowStyle != null)
//                                            return rowStyle;
//                                    }
                                    if (record!=null)
                                    {
                                        String v = record.getAttribute(TablesTypes.ROW_STYLE);
                                        if (v != null)
                                            return v;
                                    }


                                    return super.getCellCSSText(record, rowNum, colNum);
                                }
                            };


                            gridWithDesc.addRecordCollapseHandler(new RecordCollapseHandler() {
                                @Override
                                public void onRecordCollapse(RecordCollapseEvent event)
                                {
                                    ListGridRecord record = event.getRecord();
                                    Object id=record.getAttribute(TablesTypes.KEY_FNAME);
                                    ListGridWithDesc subGrid = id2SubGrid.remove(id);
                                    if (subGrid!=null)
                                    {
                                        subGrid.setData();
                                        subGrid.setFields();
//                                        if (pool.size()<7)
//                                            pool.add(subGrid);
//                                        else
                                            subGrid.markForDestroy();
                                    }
                                }
                            });

                            gridWithDesc.setCanExpandRecords(true);
                            gridWithDesc.setExpansionComponentPoolingMode(ExpansionComponentPoolingMode.NONE);

                            return gridWithDesc;
                        }
                    },
                    NodesHolder.gridMetaProvider, criteria, headerURL, dataURL, false, true);

            if (isMultiGroup!=null) newGrid.setCanMultiGroup(isMultiGroup);
            if (isMultiSort!=null) newGrid.setCanMultiSort(isMultiSort);

            newGrid.setViewName(this.getViewName());
            newGrid.setTarget((Window) canvas); //TODO Этот вопрос проверить, наверняка есть способ лучше, определение обрамляющего окна

            newGrid.addDropHandler(
                    new EventTableDropHandler(newGrid));

            //TODO Здесь к гриду еще прицепить фильтр, после этого сделать класс общий для всех
            newGrid.setGroupByMaxRecords(10000);
            newGrid.setWidth100();
            newGrid.setHeight100();

            newGrid.getTarget().addItem(newGrid);
            newGrid.setDescOperation(this.getDescOperation(new DescOperation()));

            if (ctx!=null)
                ctx.getChildList().add(new OperationCtx(newGrid,null));

        }

        return canvas;
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateLentaTable();
    }

    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        CreateLentaTable retOperation= (CreateLentaTable) super.createOperation(descOperation,operation);
        retOperation.tableType=(String)descOperation.get(EVENTS_NAME);
        retOperation.fieldOrder=(String)descOperation.get(ListGridWithDesc.FIELD_ORDER);
        retOperation.fieldHidden=(String)descOperation.get(ListGridWithDesc.FIELD_HIDDEN);

        retOperation.isMultiGroup=(Boolean)descOperation.get(ListGridWithDesc.M_GROUP);
        retOperation.isMultiSort=(Boolean)descOperation.get(ListGridWithDesc.M_SORT);
        retOperation.groupOrder=(String)descOperation.get(ListGridWithDesc.M_GROUP_ORDER);
        retOperation.sortOrderFields=(String)descOperation.get(ListGridWithDesc.M_SORT_ORDER);
        retOperation.sortOrderDirection=(String)descOperation.get(ListGridWithDesc.M_SORT_ORDER_DIRECTION);
        retOperation.aggregates = (String) descOperation.get(ListGridWithDesc.FIELD_AGGREGATES);
        retOperation.fieldFormats = (String) descOperation.get(ListGridWithDesc.FIELD_FORMATS);

        return retOperation;
    }

    protected static String EVENTS_NAME ="EVENTS_NAME";

    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);
        descOperation.put(EVENTS_NAME, getEventsName());
        descOperation.put(ListGridWithDesc.FIELD_HIDDEN, fieldHidden);
        descOperation.put(ListGridWithDesc.FIELD_ORDER, fieldOrder);


        descOperation.put(ListGridWithDesc.M_GROUP, isMultiGroup);

        descOperation.put(ListGridWithDesc.M_SORT,isMultiSort);
        descOperation.put(ListGridWithDesc.M_GROUP_ORDER,groupOrder);

        descOperation.put(ListGridWithDesc.M_SORT_ORDER,sortOrderFields);
        descOperation.put(ListGridWithDesc.M_SORT_ORDER_DIRECTION,sortOrderDirection);
        descOperation.put(ListGridWithDesc.FIELD_AGGREGATES, aggregates);
        descOperation.put(ListGridWithDesc.FIELD_FORMATS, fieldFormats);

        return descOperation;
    }
}

