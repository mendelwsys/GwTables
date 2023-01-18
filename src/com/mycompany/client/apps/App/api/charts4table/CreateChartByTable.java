package com.mycompany.client.apps.App.api.charts4table;


import com.mycompany.client.CliFilterByCriteria;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.MyHeaderControl;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.client.apps.SimpleOperationP;
import com.mycompany.client.dojoChart.*;
import com.mycompany.client.operations.ICliFilter;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.OperationCtx;
import com.mycompany.client.test.aggregates.AggregatesUtils;
import com.mycompany.client.updaters.DataDSCallback;
import com.mycompany.client.utils.IClickListener;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridField;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 28.03.15
 * Time: 14:14
 * Создание графика по таблице
 */

public class CreateChartByTable extends SimpleOperationP
{

    protected MyHeaderControl pinUp;


    public CreateChartByTable()
    {
       this(-1, -1, null);
    }

    public CreateChartByTable(int operationId, int parentOperationId, String viewName) {
        super(operationId, parentOperationId, viewName);
    }

    public CreateChartByTable(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
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

            //final Window target = newGrid.getTarget();
//            final HeaderControl pinUp = createHeaderControl(newGrid, target);
//            if (target.isDrawn())
//                NodesHolder.addHeaderCtrl(target, pinUp);
//            else {
//                final HandlerRegistration[] handlerRegistrations = new HandlerRegistration[1];
//                handlerRegistrations[0] = target.addDrawHandler(new DrawHandler() {
//                    @Override
//                    public void onDraw(DrawEvent event) {
//                        NodesHolder.addHeaderCtrl(target, pinUp);
//                        handlerRegistrations[0].removeHandler();//удалить хандлер после обработки
//                    }
//                });
//            }


            if (newGrid.isMetaWasSet())
                _createNewChart(newGrid);
            else
                new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
                {

                    @Override
                    public boolean operate()
                    {
                        if (newGrid.isMetaWasSet())
                        {
                            _createNewChart(newGrid);
                            return true;
                        }
                        return false;
                    }
                });
        }

        return super.operate(dragTarget,ctx);
    }

    /**
     * получить список описателей по полям в гриде
     * @param newGrid - грид
     * @param fields - поля по которым будет произведена групперовка
     * @return - список описателей  уровней графиков
     */
    private List<IChartLevelDef> getChartLevelDef(ListGridWithDesc newGrid, String[] fields)
    {
        List<IChartLevelDef> levelDefs= new LinkedList<IChartLevelDef>();
        for (String fieldName : fields)
        {
            ListGridField field = newGrid.getField(fieldName);
            levelDefs.add(new ChartByTableLevelDef(TablesTypes.KEY_FNAME,field.getName()));
        }

        ListGridField field = newGrid.getField(fields[fields.length-1]);
        levelDefs.add(new ChartByTableLevelDef(TablesTypes.KEY_FNAME,false,field.getName())
        {
            @Override
            public Object calcGrpValue(Record record)
            {
                return record.getAttributeAsObject(TablesTypes.KEY_FNAME);
            }
        });

        levelDefs.add(new ChartByTableLevelDef(TablesTypes.KEY_FNAME,false,null));

        return levelDefs;
    }


    @Override
    public Canvas onRemove(Canvas warnGrid, Window target) {  //TODO Обобщить этот метод
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
        return new CreateChartByTable();
    }


//    protected int findIndexInDesc(List<DescOperation> subOperation)
//    {
//        String apiName=CreateChartByTable.class.getName();
//        for (int i = 0, subOperationSize = subOperation.size(); i < subOperationSize; i++)
//        {
//            DescOperation operation = subOperation.get(i);
//            if (apiName.equals(operation.apiName))
//                return i;
//        }
//        return -1;
//    }



    public boolean isJustInit() {
        return justInit;
    }

    private boolean justInit=true;

    protected void _createNewChart(final ListGridWithDesc newGrid)
    {

        final String[] fields=newGrid.getGroupByFields();
        if (fields==null || fields.length==0)
        {
//                ListGridField lgf = new ListGridField("XYZ","XYZ");
//                lgf.setType(ListGridFieldType.TEXT);
//
//
//                final String[][] varNames2NamesInRecord = {{"B", "TIM_OTM"},{"A","TIM_BEG"}};
//                final JavaScriptObject foo = EvalUtils.buildFunction(" var RV=srv.dDay(A,B); return RV>10?'>10':' '+RV;", varNames2NamesInRecord);
//
            //var RV=srv.dDay(F,G); return RV>10?'>10':' '+RV;
//                newGrid.addGridField(lgf);
//
//                IDataFlowCtrl ctrl = newGrid.getCtrl();
//                ctrl.addAfterUpdater(new DataDSCallback(0)
//                {
//                    boolean isInit=false;
//
//                    @Override
//                    protected void updateData(Record[] data, boolean resetAll) throws SetGridException
//                    {
//                        RecordList rl = newGrid.getCacheData();
//                        if (!isInit)
//                        {
//                            if (!resetAll)
//                            {
//
//                                 if (rl!=null)
//                                 {
//                                     Record[] ra = rl.toArray();
//                                     for (Record record : ra)
//                                         record.setAttribute("XYZ", EvalUtils.evalFunction(foo, record, varNames2NamesInRecord));
//                                 }
//                                newGrid.markForRedraw();
//                            }
//                            isInit=true;
//                        }
//                        else if (data!=null && data.length>0)
//                        {
//                            for (Record inRecord : data)
//                            {
//                                String id = inRecord.getAttributeAsString(TablesTypes.KEY_FNAME);
//                                Integer actual = inRecord.getAttributeAsInt(TablesTypes.ACTUAL);
//                                if (actual>0)
//                                {
//
//                                    int inRlIx = rl.findIndex(TablesTypes.KEY_FNAME, id);
//                                    if (inRlIx>=0)
//                                    {
//                                        Record record = rl.get(inRlIx);
//                                        if (record!=null)
//                                            record.setAttribute("XYZ", EvalUtils.evalFunction(foo, record, varNames2NamesInRecord));
//                                    }
//                                }
//                            }
//                            newGrid.markForRedraw();
//                        }
//                    }
//                });
//
//
////                ListGridField[] flds = newGrid.getFields();
////                for (ListGridField fld : flds) {
////                    if (fld.getTitle().equals("TST") && fld.getUserFormula()!=null)
////                        lgf.setUserFormula(fld.getUserFormula());
////                }
////                lgf.setType(ListGridFieldType.SUMMARY);
////                lgf.setRecordSummaryFunction(new RecordSummaryFunction() {
////                    @Override
////                    public Object getSummaryValue(Record record, ListGridField[] fields, ListGridField summaryField)
////                    {
////                        return EvalUtils.evalFunction(foo,record,varNames2NamesInRecord);
////                    }
////                });
////                UserFormula uf=new UserFormula();
//                //lgf.setUserFormula();
//                //lgf.setUserFormula();
            SC.say("Для применения операции сгруппируйте данные в таблице (Меню колонки -> Группировать По ...)");
            return;
        }

//        if (newGrid.isMetaWasSet())
        {
            List<DescOperation> subOperation = newGrid.getDescOperation().getSubOperation();
            int ix=findIndexInDesc(subOperation);
            if (ix>=0)
                return;
        }



        final String[] fieldNames=newGrid.getGroupByFields();
        final Window chartWidgetWrapper = newGrid.getTarget();



        List<IChartLevelDef> levelDefs = getChartLevelDef(newGrid, fieldNames); //Получить уровни агрегирования

        CreateChartByTableManager manager=new CreateChartByTableManager(levelDefs.toArray(new IChartLevelDef[levelDefs.size()]),newGrid)
        {
            @Override
            protected IGetRecordVal[] createCalcFunctions(final BaseChartWrapper _wrapper, String baseTitle, Object graphId)
            {



                IFormatValue formatValue = new IFormatValue()
                {
                    @Override
                    public boolean isPresentableValue(Value value)
                    {
                        Object obj=value.getMean();
                        return (obj!=null && ((Double)obj)>0);
                    }

                    @Override
                    public Object presentationValue(Value value)
                    {
                        final Double mean = (Double) value.getMean();
                        if (mean!=null)
                            return mean;
                        else
                            return 0;
                    }

                    @Override
                    public String formatValue(Value value)
                    {
                        final ListGridField fld=((ChartWrapperByTable)_wrapper).getFld();
//                        final String fldName=(fld!=null)?fld.getName():null;

                        final Object v = presentationValue(value);
                        if (fld!=null)
                        {
                            final String attribute = fld.getAttribute(TablesTypes.AGGREGATE_FIELD_FORMAT_KEY);
                            if (attribute!=null)
                                return AggregatesUtils.getStringFormatByValue(fld, v, attribute);
                            else
                            {
                                int colNum = newGrid.getFieldNum(fld.getName());
                                final Record record = new Record();
                                record.setAttribute(fld.getName(), v);
                                return newGrid.getDefaultFormattedValue(record,0,colNum);
                            }
                        }
                        else
                                return String.valueOf(((Double)v).intValue());
                    }
                };
                if (baseTitle==null)
                    baseTitle="";
                final String title=baseTitle;

                List<IGetRecordVal> getRecordVals=new LinkedList<IGetRecordVal>();

                for (int i = 0, fieldNamesLength = fieldNames.length; i < fieldNamesLength; i++)
                {
                    ListGridField field = newGrid.getField(fieldNames[i]);
                    String addTitle;
                    if (i>0)
                        addTitle = " ($) По " + field.getTitle() + " [$val] ";
                    else
                        addTitle = " По " + field.getTitle() + " [$val] ";

                    getRecordVals.add(new CalcGetRecordVal(graphId, formatValue, title, addTitle, "red"){

                        @Override
                        public ListGridField getFld() {
                            return ((ChartWrapperByTable)_wrapper).getFld();
                        }
                    });
                }
                getRecordVals.add(new CalcGetRecordVal(graphId,formatValue,title," [$val] ","red")
                {
                    @Override
                    public ListGridField getFld() {
                        return ((ChartWrapperByTable)_wrapper).getFld();
                    }
                });

                getRecordVals.add(new GetRecordValByColName(graphId,null, formatValue, title," [ таблица отсутствует]","orange")
                {
                    public Object getRecordsVal(Map<Object, Record> records, ValDef def)
                    {
                        final ListGridField fld=((ChartWrapperByTable)_wrapper).getFld();
                        colId=(fld!=null)?fld.getName():null;
                        if (colId!=null)
                            return super.getRecordsVal(records,def);
                        else
                            return 1.0;
                    }
                });
                return getRecordVals.toArray(new IGetRecordVal[getRecordVals.size()]);
            }


            protected BaseChartWrapper createWrapper(BaseDOJOChart chart, String title, ValDef currentNode, IOperationContext ctx)
            {
                ChartWrapperByTable chartWrapperByTable = new ChartWrapperByTable(chart, title, currentNode, ctx);
                if (keyColumn!=null)
                    chartWrapperByTable.setFldByName(String.valueOf(keyColumn));
                return chartWrapperByTable;
            }
        };//Создать манагер

        manager.modeView=_modeView;
        if (isJustInit())
        {
            Window wnd = ChartOptionsView.createOptionView(chartWidgetWrapper.getTitle(), manager, null, new IChartCtrlImpl(manager),newGrid);
            wnd.show();
        }
        else
        {
            new IChartCtrlImpl(manager).apply(chartType,keyColumn,chartTitle,nodePath);
            nodePath=null;//TODO Сброс что не мешал дальнейшей работе, он переустанавливается уже внутри дескриптора окна при перемещении окна
        }
    }

    private class IChartCtrlImpl implements IChartCtrl
    {
        private BaseDOJOChart dojoChartPane;//Если объект только создается, тогда эта ссылка еще не инициализирована,
        // если же объект уже создан тогда ссылка инициализирована старым объектом

        private CreateChartByTableManager manager;


        private HeaderControl[] oldControl;
        private HeaderControl[] backLevel;
        private NewChartFactoryImpl chartFactory;

        private IChartCtrlImpl(BaseDOJOChart dojoChartPane,
                              HeaderControl[] oldControl,CreateChartByTableManager manager)
        {
            this(manager);
            this.dojoChartPane = dojoChartPane;
            this.oldControl = oldControl;//Необходимо при переустановке типа графика для того что бы удалить созданные контролы
        }


        IChartCtrlImpl(CreateChartByTableManager _manager)
        {
            this.manager=_manager;
            this.chartFactory = new NewChartFactoryImpl(backLevel=new HeaderControl[3], this.manager.newGrid.getTarget())
            {
                @Override
                protected void fillHeaderControls(final HeaderControl[] inHeaderControls, final BaseDOJOChart inDojoChartPane)
                {
                    inHeaderControls[0]= new HeaderControl(HeaderControl.SETTINGS,new ClickHandler() {

                            @Override
                            public void onClick(ClickEvent event)
                            {
                                Window wnd = com.mycompany.client.apps.App.api.charts4table.ChartOptionsView.createOptionView(IChartCtrlImpl.this.manager.newGrid.getTarget().getTitle(), manager, inDojoChartPane,
                                        new IChartCtrlImpl(inDojoChartPane, inHeaderControls, manager),manager.newGrid
                                );
                                wnd.show();
                            }
                        });
                    inHeaderControls[0].setTooltip("Настроить");

                    inHeaderControls[1]= new HeaderControl(HeaderControl.DOUBLE_ARROW_UP,new ClickHandler(){
                            @Override
                            public void onClick(ClickEvent event)
                            {
                                inHeaderControls[1].setDisabled(!manager.backLevel(inDojoChartPane.getGraphId()));
                            }
                        });
                    inHeaderControls[1].setDisabled(true);
                    inHeaderControls[1].setTooltip("Вверх");


                    final MyHeaderControl pinUp = new MyHeaderControl(HeaderControl.DOCUMENT, new ClickHandler()
                    {
                        @Override
                        public void onClick(ClickEvent event) {
                            if (ModeView.Chart.equals(manager.modeView)) {
//                                    Window _chartWidgetWrapper = manager.newGrid.getTarget();
                                BaseChartWrapper wrapper = manager.getChartWrapper(inDojoChartPane);
                                ValDef currentNode = wrapper.getCurrentNode();
                                if (currentNode != null && currentNode.getMetaDef().isAllowEntrance()) {
//                                        _chartWidgetWrapper.removeItem(inDojoChartPane);
//                                        _chartWidgetWrapper.addItem(manager.newGrid);

                                    inDojoChartPane.setVisible(false);
                                    inDojoChartPane.setZIndex(manager.gridZIndex-1);

                                    manager.newGrid.setVisible(false);
                                    manager.newGrid.setZIndex(manager.gridZIndex);
                                    manager.setGridFilterByNode(currentNode);
                                }
                                manager.modeView = ModeView.Grid;
                                inHeaderControls[2].setTooltip("График");
                            } else {
//                                Window _chartWidgetWrapper = manager.newGrid.getTarget();
                                BaseChartWrapper wrapper = manager.getChartWrapper(inDojoChartPane);
                                ValDef currentNode = wrapper.getCurrentNode();
                                if (currentNode != null && currentNode.getMetaDef().isAllowEntrance()) {
//                                        _chartWidgetWrapper.removeItem(manager.newGrid);
//                                        _chartWidgetWrapper.addItem(inDojoChartPane);
                                    manager.newGrid.setZIndex(manager.gridZIndex-1);
                                    manager.newGrid.setVisible(false);
                                    inDojoChartPane.setZIndex(manager.gridZIndex);
                                    inDojoChartPane.setVisible(true);
                                }
                                manager.modeView = ModeView.Chart;
                                inHeaderControls[2].setTooltip("Таблица");
                            }
                            inDojoChartPane.getDescOperation().put(MODE_VIEW,manager.modeView);
                        }
                    })
                    {
                        public void onRemoveCtrl()
                        {
                            if (manager.updater!=null)
                                manager.newGrid.getCtrl().removeAfterUpdater(manager.updater);
                            if (manager.ixListener>=0)
                                manager.newGrid.getFilterChangeListenerCtrl().removeIndexListener(manager.ixListener);
                            manager.setGridFilterByNode(null);
                            inDojoChartPane.setVisible(false);
                            inDojoChartPane.setZIndex(manager.gridZIndex-1);
                            getTarget().removeItem(inDojoChartPane);
                            NodesHolder.removeHeaderCtrl(getTarget(), inHeaderControls);
                            manager.newGrid.setZIndex(manager.gridZIndex);
                            manager.newGrid.setVisible(true);


                        }
                    };
                    pinUp.setGrid(manager.newGrid);
                    pinUp.setOperation(CreateChartByTable.this);
                    pinUp.setTarget(manager.newGrid.getTarget());
                    pinUp.setCanDrag(true);
                    pinUp.setCanDrop(true);

                    inHeaderControls[2]= pinUp;
                    inHeaderControls[2].setTooltip("Таблица");

                }
            };
        }

        @Override
        public void apply(ChartType chartType, Object select_value, String title)
        {
            apply(chartType,select_value,title,null);
        }

        private void apply(ChartType chartType, Object keyColumn, String title,Object[] initPath)
        {
            CreateChartByTable chartByTable=new  CreateChartByTable(CreateChartByTable.this);


           chartByTable.chartType=chartType;
           chartByTable.chartTitle=title;
           chartByTable.keyColumn=keyColumn;

           final BaseDOJOChart[] _dojoChartPane=new BaseDOJOChart[1];
           boolean wasChange=true;

           final Window chartWidgetWrapper = manager.newGrid.getTarget();

           ValDef currentNode=null;
           if (dojoChartPane!=null)
           {
               _dojoChartPane[0]=dojoChartPane;
               if (!chartType.equals(dojoChartPane.getChartType()))
               {
                   BaseChartWrapper wrapper = manager.removeChart(dojoChartPane);
                   currentNode = wrapper.getCurrentNode();

                   chartWidgetWrapper.removeItem(dojoChartPane);
                   dojoChartPane.markForDestroy();


                   NodesHolder.removeHeaderCtrl(chartWidgetWrapper, oldControl);
                   _dojoChartPane[0]= chartFactory.createChart(chartType, 200, 200);
               }
           }
           else
               _dojoChartPane[0]= chartFactory.createChart(chartType, 200, 200);

           BaseChartWrapper  wrapper = manager.getChartWrapper(_dojoChartPane[0]);
           if (wrapper!=null)
           {
               if (wrapper.getTitle()==null || !wrapper.getTitle().equals(title))
                   wrapper.setBaseTitle(title);
               else
                   wasChange=false;
           }
           else
           {
               IOperationContext ctx = new OperationCtx(null, chartWidgetWrapper);
               wrapper = manager.addChart(_dojoChartPane[0],title, currentNode,ctx);
               wrapper.addIndexListener(new IClickListener<BaseChartWrapper>() {
                   @Override
                   public void clickIndex(BaseChartWrapper wrapper)
                   {
                       backLevel[1].setDisabled(!manager.hasBackLevel(_dojoChartPane[0].getGraphId()));
                   }
               });
           }

//            if (((CreateChartByTableManager.ChartWrapperByTable)wrapper).setFldByName(String.valueOf(keyColumn)))
//            {
//                manager.reInitCalcFunction(wrapper);
//                wasChange=true;
//            }

           wasChange|=((CreateChartByTableManager.ChartWrapperByTable)wrapper).setFldByName(String.valueOf(keyColumn));

           if (initPath!=null)
           {
               wrapper.setInitPath(initPath);
               wasChange=true;
           }

            if (wasChange)
            {
               chartWidgetWrapper.setTitle(title);
               manager.getRootValue().setRecalc();
                //
            }

            if (ModeView.Chart.equals(manager.modeView))
            {
//                chartWidgetWrapper.removeItem(manager.newGrid);
                manager.newGrid.setVisible(false);
                manager.newGrid.setZIndex(manager.gridZIndex-1);


                if (_dojoChartPane[0]!=dojoChartPane)
                    chartWidgetWrapper.addItem(_dojoChartPane[0]);
                _dojoChartPane[0].setVisible(true);
                _dojoChartPane[0].setZIndex(manager.gridZIndex);

                manager.modeView=ModeView.Chart;
            }
            else
            {
                if (_dojoChartPane[0]!=dojoChartPane)
                {
                    _dojoChartPane[0].setVisible(false);
                    chartWidgetWrapper.addItem(_dojoChartPane[0]);
                    _dojoChartPane[0].setZIndex(manager.gridZIndex-1);
                }
                manager.modeView=ModeView.Grid;
            }

            {
                chartByTable._modeView=manager.modeView;
                final DescOperation descOperation = chartByTable.getDescOperation(new DescOperation());
                descOperation.put(JUST_INIT,false);
                _dojoChartPane[0].setDescOperation(descOperation);
                _dojoChartPane[0].set2Save(false);

                DescOperation gridDescOperation = manager.newGrid.getDescOperation();
                List<DescOperation> subOperation = gridDescOperation.getSubOperation();
                int ix=findIndexInDesc(subOperation);
                if (ix>=0)
                    subOperation.remove(ix);
                subOperation.add(descOperation);
            }


            if (manager.updater==null)
            {   //Тогда создаем и приципляем апдейтер
                IDataFlowCtrl ctrl = manager.newGrid.getCtrl();
                ctrl.addAfterUpdater(manager.updater = new DataDSCallback(0)
                {
                    Map<Object, Record> cache = new HashMap<Object, Record>();
                    boolean isInit = false;
                    boolean filterWasChanged=false;
                    @Override
                    protected void updateData(Record[] data, boolean resetAll) throws SetGridException
                    {

                        if (!isInit || filterWasChanged)
                        {
                            filterWasChanged=false;
                            if (!resetAll)
                            {
                                RecordList rl = manager.newGrid.getCacheData();
                                if (rl != null)
                                    _updateData(rl.toArray(), true);
                            }

                            if (!isInit)
                            {
                                manager.ixListener=manager.newGrid.getFilterChangeListenerCtrl().addIndexListener(new IClickListener<Pair<ICliFilter,ICliFilter>>() {
                                    @Override
                                    public void clickIndex(Pair<ICliFilter,ICliFilter> index)
                                    {
                                        filterWasChanged=index.first!=null && !(index.first instanceof InternalFilter) || index.second!=null && !(index.second instanceof InternalFilter);
                                    }
                                });
                            }
                            isInit = true;
                        }
                        _updateData(data, resetAll);
                    }

                    private void _updateData(Record[] data, boolean resetAll)
                    {
                        if (data==null)
                            return;

                        ValDef rootDef;
                        CalcManager.StateViews state = manager.getAllCurrentState();
                        if (resetAll) {
                            cache = new HashMap<Object, Record>(); //Сброс кеша данныз
                            rootDef = manager.reCreateRootValue();  //Обновление данных графиков
                        } else
                            rootDef = manager.getRootValue();  //Получить возможные группировки

                        {
                            List<ICliFilter> cli_filters = manager.newGrid.getCliFilters();
                            if (cli_filters!=null)
                            {
                                for (ICliFilter filter : cli_filters)
                                    if (!(filter instanceof InternalFilter))
                                        data=filter.filter(data);
                            }
                        }

                        Set<String> ids= new HashSet<String>();
                        {
                            RecordList rl = manager.newGrid.getCacheData();
                            for (int i=0;i<rl.getLength();i++)
                                ids.add(rl.get(i).getAttributeAsString(TablesTypes.KEY_FNAME));
                        }

                        for (Record newRecord : data)
                        {
                            Integer actual = newRecord.getAttributeAsInt(TablesTypes.ACTUAL);
                            String id = newRecord.getAttributeAsString(TablesTypes.KEY_FNAME);
                            if ((actual == null || actual == 0) && !ids.contains(id))
                                  rootDef.removeRecord(cache.remove(id), manager.getDefs(), 0);
                            else
                            {

                                Record oldRecord = cache.get(id);
                                if (oldRecord == null) {
                                    cache.put(id, newRecord);
                                    rootDef.addRecord(newRecord, manager.getDefs(), 0);
                                } else {
                                    String[] keys = newRecord.getAttributes(); //TODO Посмотреть на предмет серверной оптимизации
                                    for (String key : keys) {
                                        Object value = newRecord.getAttributeAsObject(key);
                                        oldRecord.setAttribute(key, value);
                                    }

                                    rootDef.addRecord(oldRecord, manager.getDefs(), 0);
                                }
                            }
                        }

                        if (rootDef.isRecalc()) {
                            rootDef.reCalcGroups(cache);
                            manager.updateCurrentState(state);
                        } else
                            manager.updateJustLoadedChartCurrentState(state);
                    }
                });
            }
       }
    }

    private CreateChartByTable(CreateChartByTable operation)
    {
        super(operation.getOperationId(), operation.getParentOperationId(),operation.getViewName(), operation.getTypeOperation());
        this.tableType=operation.tableType;
        this.chartTitle=operation.chartTitle;
        this.chartType=operation.chartType;
        this.justInit=operation.justInit;
        this._modeView=operation._modeView;
        this.nodePath=operation.nodePath;
        this.keyColumn=operation.keyColumn;

    }


    private ChartType chartType;
    private String chartTitle;
    private Object[] nodePath;
    private String tableType;

    private Object keyColumn;//Ключевая колонка

    public Object getKeyColumn() {
        return keyColumn;
    }



    public ModeView getModeView() {
        return _modeView;
    }

    private ModeView _modeView=ModeView.Chart;

    public String getEventsName() {
        return tableType;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public String getChartTitle() {
        return chartTitle;
    }


    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        CreateChartByTable retOperation= (CreateChartByTable) super.createOperation(descOperation,operation);
        retOperation.tableType=(String)descOperation.get(EVENTS_NAME);
        retOperation.chartTitle=(String)descOperation.get(CHART_TITLE);
        retOperation.chartType=ChartType.valueOf(String.valueOf(descOperation.get(CHART_TYPE)));
        retOperation._modeView=ModeView.valueOf(String.valueOf(descOperation.get(MODE_VIEW)));
        retOperation.keyColumn=descOperation.get(KEY_COLUMN);
        retOperation.justInit=(Boolean)descOperation.get(JUST_INIT);

        LinkedList ll = new LinkedList();
        for (int i = 0;; i++)
        {
            String key = NODE_PATH + "_" + i;
            Object e = descOperation.get(key);
            if (e==null && !descOperation.contains(key))
                break;
            ll.add(e);
        }
        retOperation.nodePath=ll.toArray(new Object[ll.size()]);

        return retOperation;
    }


    protected static final String KEY_COLUMN = "KEY_COLUMN";  //TODO для расширения
    protected static final String CHART_TITLE = "CHART_TITLE";
    protected static final String CHART_TYPE = "CHART_TYPE";
    protected static String EVENTS_NAME ="EVENTS_NAME";
    protected static final String JUST_INIT = "JUST_INIT";
    public static final String NODE_PATH = "NODE_PATH";
    public static final String MODE_VIEW = "MODE_VIEW";





    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);
        descOperation.put(EVENTS_NAME, getEventsName());
        descOperation.put(CHART_TITLE,getChartTitle());
        descOperation.put(CHART_TYPE,getChartType());
        descOperation.put(MODE_VIEW,getModeView());
        descOperation.put(KEY_COLUMN,getKeyColumn());
        descOperation.put(JUST_INIT,isJustInit());

        return descOperation;
    }

    private class  InternalFilter extends CliFilterByCriteria
    { //Класс сделан для идентифкации того что изменяемы фильтр не надо обрабатывать.

        public InternalFilter(DataSource filterDS, Criteria criteria) {
            super(filterDS, criteria);
        }

//        public InternalFilter(DataSource filterDS) {
//            super(filterDS);
//        }
    }
    abstract class CreateChartByTableManager extends BaseCalcManager
    {
        ListGridWithDesc newGrid;
        int gridZIndex;

        DataDSCallback updater;
        ModeView modeView;
        int ixListener=-1;
        ICliFilter filter;
        private List<ICliFilter> filters=new LinkedList<ICliFilter>();

        protected CreateChartByTableManager(IChartLevelDef[] defIs)
        {
            super(defIs);
        }



        protected void setGridFilterByNode(ValDef newNode)
        {
            AdvancedCriteria cr;
            if (newNode!=null)
            {
                final ValDef parentDef = newNode.getParentDef();
                if (parentDef!=null)
                {
                    parentDef.getMetaDef().appendToCriterionList(cr = new AdvancedCriteria(OperatorId.AND),newNode);
                    ICliFilter filter = new InternalFilter(newGrid.getFilterDS(), cr);
                    NodesHolder.applyCliFilter(newGrid, filter, this.filter);
                    this.filter=filter;
                    setGridVisible();
                    return;
                }
            }
            NodesHolder.applyCliFilter(newGrid, null, this.filter);
            this.filter=null;
            setGridVisible();
        }

        private void setGridVisible() {
            final IDataFlowCtrl ctrl=newGrid.getCtrl();
            ctrl.addAfterUpdater(new DSCallback()
            {
                @Override
                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                {
                    newGrid.setVisible(true);
                    ctrl.removeAfterUpdater(this);
                }
            });
        }

        protected CreateChartByTableManager(IChartLevelDef[] defIs, ListGridWithDesc newGrid)
        {
            this(defIs);
            this.newGrid = newGrid;
            this.filters = newGrid.getCliFilters();
            this.gridZIndex=newGrid.getZIndex();
        }


        public  class ChartWrapperByTable extends BaseChartWrapper
        {
            ListGridField fld;

            public ListGridField getFld()
            {
                return fld;
            }


            public boolean setFldByName(String fldName)
            {
                if (    (fld!=null && fld.getName().equals(fldName))
                        ||
                        (fld==null && fldName==null)
                  )
                  return false;



                if (fldName!=null)
                {
                    final ListGridField newField = CreateChartByTableManager.this.newGrid.getField(fldName);
                    if (newField==this.fld)
                        return false;
                    this.fld = newField;
                }
                else
                    this.fld =null;

                return true;
            }


            public ChartWrapperByTable(BaseDOJOChart chart, String title, ValDef currentNode, IOperationContext ctx)
            {
                super(CreateChartByTableManager.this, chart, title, currentNode, ctx);
            }

            protected void drawByCurrentNode()
            {
                if (ModeView.Chart.equals(modeView))
                {
                    if (!currentNode.getMetaDef().isAllowEntrance())
                    {
//                                chartWidgetWrapper.removeItem(chart);
//                                chartWidgetWrapper.addItem(newGrid);
                        chart.setZIndex(gridZIndex-1);
                        chart.setVisible(false);

                        newGrid.setVisible(false);
                        newGrid.setZIndex(gridZIndex);
                        setGridFilterByNode(currentNode);
                    }
                    else
                    {
                        chart.setZIndex(gridZIndex);
                        newGrid.setZIndex(gridZIndex-1);
                    }
                }
                else
                {
                    chart.setZIndex(gridZIndex-1);
                    newGrid.setZIndex(gridZIndex);
                    newGrid.setVisible(false);
                    setGridFilterByNode(currentNode);
                }

                if (currentNode.getMetaDef().isAllowEntrance())
                    super.drawByCurrentNode();
            }


            public boolean backLevel()
            {
                if (ModeView.Chart.equals(modeView))
                {
                    if (currentNode!=null && !currentNode.getMetaDef().isAllowEntrance())
                    {
//                                chartWidgetWrapper.removeItem(newGrid);
//                                chartWidgetWrapper.addItem(chart);

                        newGrid.setVisible(false);
                        newGrid.setZIndex(gridZIndex-1);
                        chart.setVisible(true);
                        chart.setZIndex(gridZIndex);

                    }
                }
                return super.backLevel();
            }

        }



    }

}



//            @Override
//            protected IGetRecordVal[] createCalcFunctions(BaseChartWrapper wrapper, String baseTitle, Object graphId)
//            {
//                IFormatValue formatValue = new IFormatValue()
//                {
//
//                    @Override
//                    public boolean isPresentableValue(Value value)
//                    {
//                        Object obj=value.getMean();
//                        return (obj!=null && ((Double)obj)>0);
//                    }
//
//                    @Override
//                    public Object presentationValue(Value value)
//                    {
//                        final Double mean = (Double) value.getMean();
//                        if (mean!=null)
//                            return mean.intValue();
//                        else
//                            return 0;
//                    }
//
//                    @Override
//                    public String formatValue(Value value) {
//                        return String.valueOf(presentationValue(value));
//                    }
//                };
//                if (baseTitle==null)
//                    baseTitle="";
//                final String title=baseTitle;
//
//                List<IGetRecordVal> getRecordVals=new LinkedList<IGetRecordVal>();
//
//                for (int i = 0, fieldNamesLength = fieldNames.length; i < fieldNamesLength; i++)
//                {
//                    ListGridField field = newGrid.getField(fieldNames[i]);
//                    String addTitle;
//                    if (i>0)
//                        addTitle = " ($) По " + field.getTitle() + " [$val] ";
//                    else
//                        addTitle = " По " + field.getTitle() + " [$val] ";
//
//                    getRecordVals.add(new SumGetRecordVal(graphId, formatValue, title, addTitle, "red"));
//                }
//                getRecordVals.add(new SumGetRecordVal(graphId,formatValue,title," [$val] ","red"));
//                getRecordVals.add(new GetConstRecordVal(graphId, formatValue, title," [ таблица отсутствует]","orange",1));
//
//                return getRecordVals.toArray(new IGetRecordVal[getRecordVals.size()]);
//            }
