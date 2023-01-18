package com.mycompany.client.apps.App.api;

import com.google.gwt.event.shared.HandlerRegistration;
import com.mycompany.client.*;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.NSI;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.SimpleOperationP;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.types.TopOperatorAppearance;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.*;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 28.11.14
 * Time: 17:29
 * фильтр на основе которого будет сделан критериальный фильтр
 */
public class DorOperationFactory extends SimpleOperationP
{
    public DorOperationFactory(int operationId, int parentOperationId,String viewName)
    {
        super(operationId,parentOperationId,viewName,IOperation.TypeOperation.addData);

    }

    protected DorOperationFactory()
    {
        this(-1, -1, null);
    }


    public Canvas getInputFrom(final Canvas _gGrid)
    {

        if (_gGrid instanceof ListGridWithDesc)
        {

            final ListGridWithDesc gGrid = (ListGridWithDesc) _gGrid;
            IServerFilter filer = gGrid.getServerDataFilter();
            AdvancedCriteria criteria=filer.getCriteria();

            final FilterBuilder filterBuilder = new FilterBuilder();


            DataSource fieldsDs = new DataSource();

            DataSourceTextField nameField = new DataSourceTextField("name");
            DataSourceTextField titleField = new DataSourceTextField("title");
            DataSourceTextField typeField = new DataSourceTextField("type");

            fieldsDs.setFields(nameField, titleField, typeField);
            fieldsDs.setClientOnly(true);
            Record record= new Record();
            record.setAttribute("name", "DOR_KOD");
            record.setAttribute("title", "Код дороги ");
            record.setAttribute("type", FieldType.ENUM.toString());
            fieldsDs.addData(record);

            DataSource ways = NSI.getWayNSI();


            filterBuilder.setFieldDataSource(fieldsDs);

            filterBuilder.setTopOperatorAppearance(TopOperatorAppearance.NONE);

            filterBuilder.setDataSource(ways);
            filterBuilder.setCriteria(criteria);

            //filterBuilder.add;
            //((SelectItem)(((SearchForm)(((VStack)filterBuilder.getMember(2)).getChildren()[1].getChildren()[1])).getItem("operator"))).filterClientPickListData()[20].toMap()


        final Window wnd = new Window();

        IButton filterButton = new IButton("Применить");
        filterButton.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                AdvancedCriteria criteria = filterBuilder.getCriteria();
                final IServerFilter filter = gGrid.getServerDataFilter();
                filter.setCriteria(criteria);

                final IDataFlowCtrl ctrl = gGrid.getCtrl();

                if (ctrl.isTimer())
                {
                    DSCallback dsCallback=new MyDSCallback()
                    {
                        @Override
                        public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                        {

                            gGrid.setData(new Record[0]);
                            gGrid.setCacheData(new RecordList());
                            filter.set2Criteria(ctrl.getCriteria());

                            setFilter2Desc(gGrid, filter.getCriteria());

                            ctrl.setFullDataUpdate();
                            ctrl.removeAfterUpdater(this);
                        }
                    };
                    ctrl.addAfterUpdater(dsCallback);
                }
                else
                {
                    gGrid.setData(new Record[0]);
                    gGrid.setCacheData(new RecordList());
                    filter.set2Criteria(ctrl.getCriteria());
                    ctrl.setFullDataUpdate();
                }

                wnd.destroy();
            }
        });

        IButton cancelButton = new IButton("Отмена");
        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                wnd.destroy();
            }
        });

        HLayout buttons = new HLayout();
        buttons.addMembers(filterButton,cancelButton);
        buttons.setAlign(Alignment.RIGHT);

        VLayout lo = new VLayout();
        lo.addMember(filterBuilder);
        lo.addMember(buttons);


        wnd.setTitle("Данные дорог");
        wnd.addItem(lo);
        wnd.centerInPage();
        wnd.setCanDragReposition(true);
        wnd.setCanDragResize(true);
        wnd.setAutoSize(true);
        wnd.setIsModal(true);

        filterBuilder.getSubClauseButton().setVisible(false);

        return wnd;

        }
        else
        {
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
            final ListGridWithDesc gridWithDesc=(ListGridWithDesc) dragTarget;


            new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation(){

                @Override
                public boolean operate()
                {
                    if (gridWithDesc.isMetaWasSet())
                    {
                        _operate(gridWithDesc);   //Здесь ожидаем пока не придут заголовки, но не более 5-x секунд?
                        return true;
                    }
                    return false;
                }
            });
        }
        return dragTarget;
    }

    private void _operate(ListGridWithDesc newGrid) {
        final Window target = newGrid.getTarget();
        List params = this.getParams();
//        Pair<String,String> pair = (Pair<String,String>) params.get(0);
        Object pair= params.get(0);

        IServerFilter serverFilter = newGrid.getServerDataFilter();

        if (serverFilter==null)
        {
            newGrid.setServerDataFilter(serverFilter = new CommonServerFilter(TablesTypes.FILTERDATAEXPR));
            final HeaderControl pinUp = createHeaderControl(newGrid, target);
            if (pinUp!=null)
            {
                if (target.isDrawn())
                    NodesHolder.addHeaderCtrl(target, pinUp);
                else
                {
                    final HandlerRegistration[] handlerRegistrations=new HandlerRegistration[1];
                    handlerRegistrations[0] = target.addDrawHandler(new DrawHandler() {
                        @Override
                        public void onDraw(DrawEvent event)
                        {
                            NodesHolder.addHeaderCtrl(target, pinUp);
                            handlerRegistrations[0].removeHandler();//удалить хандлер после обработки
                        }
                    });
                }
            }
        }

        AdvancedCriteria cr=null;
        if (pair instanceof Pair)
        {
            if (((Pair)pair).first.equals(TablesTypes.FILTERDATAEXPR))
            {
                final String second = String.valueOf (((Pair) pair).second);
                cr=new AdvancedCriteria(JSOHelper.eval(second));//Оттранслировать критерий
                serverFilter.setCriteria(cr);//Установить фильтр сервера
            }
        }
        else if (pair instanceof Criterion)
        {
            cr = serverFilter.getCriteria();
            Criterion[] criteria1 = cr.getCriteria();
            if (criteria1 !=null && criteria1.length>0)
               //cr.appendToCriterionList(new Criterion(pair.first, OperatorId.EQUALS,pair.second));
                cr.appendToCriterionList((Criterion)pair);
            else
            {
               //cr.addCriteria(pair.first, OperatorId.EQUALS, pair.second);
                cr.addCriteria((Criterion)pair);
            }
            cr.setOperator(OperatorId.OR);
         }

        setFilter2Desc(newGrid, cr);//Здесь добавить фильтр в описатель.
        IDataFlowCtrl ctrl = newGrid.getCtrl();
        serverFilter.set2Criteria(ctrl.getCriteria());
        ctrl.setFullDataUpdate();
        ctrl.startUpdateData(true);
    }

    protected void setFilter2Desc(ListGridWithDesc newGrid, AdvancedCriteria cr)
    {

        DescOperation descOperation;
        String js = cr.toJSON();
        br:
        {
            List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
            String apiName=DorOperationFactory.class.getName();
            for (DescOperation operation : subOperation) {
                if (apiName.equals(operation.apiName))
                {
                    descOperation=operation;
                    break br;
                }
            }
            subOperation.add(descOperation =this.getDescOperation(new DescOperation()));
        }
        descOperation.put(TablesTypes.FILTERDATAEXPR, js);
    }


    protected void removeFilterFromDesc(ListGridWithDesc newGrid)
    {
        List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
        String apiName=DorOperationFactory.class.getName();
        for (int i = 0, subOperationSize = subOperation.size(); i < subOperationSize; i++)
        {
            DescOperation operation = subOperation.get(i);
            if (apiName.equals(operation.apiName)) {
                subOperation.remove(i);
                break;
            }
        }
    }



    public HeaderControl createHeaderControl(final Canvas canvas, final Window target)
    {
        final MyHeaderControl pinUp = new MyHeaderControl
        (
            HeaderControl.TRANSFER,
            new com.smartgwt.client.widgets.events.ClickHandler()
            {
                public void onClick(ClickEvent event)
                {
                    Canvas form = getInputFrom(canvas);
                    Canvas rootCanvas = Canvas.getById(AppConst.t_MY_ROOT_PANEL);
                    rootCanvas.addChild(form);
                }
            }
        );
        pinUp.setGrid(canvas);
        pinUp.setOperation(this);
        pinUp.setTarget(target);

        pinUp.setTooltip("Данные дорог");
        pinUp.setCanDrag(true);
        pinUp.setCanDrop(true);
        return pinUp;
    }

    public Canvas onRemove(Canvas warnGrid, Window target)
    {
        if (warnGrid instanceof ListGridWithDesc)
        {
            final ListGridWithDesc gridWithDesc = (ListGridWithDesc) warnGrid;
            final IDataFlowCtrl ctrl = gridWithDesc .getCtrl();

            DSCallback dsCallback=new MyDSCallback()
            {
                @Override
                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                {
                    ctrl.stopUpdateData(); //Останов опроса сервера,
                    Criteria criteria = new Criteria();
                    final Map values = ctrl.getCriteria().getValues();
                    values.remove(TablesTypes.FILTERDATAEXPR);
                    for (Object o : values.keySet())
                        criteria.setAttribute((String)o,values.get(o));
                    ctrl.setCriteria(criteria);
                    gridWithDesc .setData(new Record[0]);
                    gridWithDesc .setCacheData(new RecordList());
                    gridWithDesc .setServerDataFilter(null);
                    ctrl.removeAfterUpdater(this);
                }
            };


            removeFilterFromDesc(gridWithDesc);//Удаление фильтра из описателя


            ctrl.addAfterUpdater(dsCallback);


        }
        return null;
    }



    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        DorOperationFactory newOperation= (DorOperationFactory) super.createOperation(descOperation,operation);
        Object jsCriteria=descOperation.get(TablesTypes.FILTERDATAEXPR);
        newOperation.getParams().add(new Pair(TablesTypes.FILTERDATAEXPR,jsCriteria));
        return newOperation;
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new DorOperationFactory();
    }

    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);
        descOperation.apiName=DorOperationFactory.class.getName();
//        for (Object param : getParams())
//            descOperation.params.add((Pair)param);
        return descOperation;
    }


}
