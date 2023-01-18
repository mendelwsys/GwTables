package com.mycompany.client.apps.App.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.GridCtrl;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.SimpleNewPortlet;
import com.mycompany.client.dojoChart.*;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.dojoChart.WidgetUpdater;
import com.mycompany.client.operations.OperationCtx;
import com.mycompany.client.dojoChart.BaseChartWrapper;
import com.mycompany.client.utils.IClickListener;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.ColDef;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.analit2.UtilsData;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 17:28
 * Стандарная операция создания графика
 */
public class CreateChatView extends SimpleNewPortlet   implements IPortalLayoutCtrl
{

    private PortalLayout layout;
    private Integer colNum;
    private Integer rowNum;

    @Override
    public void setPortletLayOut(PortalLayout layout, Integer colNum, Integer rowNum)
    {
        this.layout = layout;
        this.colNum=colNum;
        this.rowNum=rowNum;
    }

//        TODO
//        1. проверить удаление объектов
//
//        2. несколько окон +
//        3. не рисовать нулевые и отрицательные параметры (для круговых диаграмм точно не надо) +
//        4. изменение размера, что-то уходят названия, возможно fullRenderer !!! +-(не решил расстояние от надписей до круга+)
//        5. установка правильно туллтипа, возможно кол-во включить в название +
//
//        6. установка правильного заголовка, общее кол-во включить в заголовок +
//        7. переход от графика к таблице.
//
//        8. установка размера шрифта!!!!, необходимо для табло, необходимо в настроечной панели это делать
//        9. фильтрация при построении иерархии. (Необходимо для рассматривания "малых" событий)



    public String getDataURL() {
        return "transport/dataCons";
    }

    public String getHeaderURL() {
        return "CommonConsHeader.jsp";
    }

    protected GridCtrl initManagerAndCtrl(final CalcManager manager)
    {
        String headerURL=getHeaderURL();
        String dataURL = getDataURL();


        final Criteria criteria = new Criteria(TablesTypes.TTYPE,getEventsName());//
        final WidgetUpdater widgetUpdater = new WidgetUpdater();

        final String addDataSourceId = "$" + criteria.getAttribute(TablesTypes.TTYPE);

        Pair<DSCallback, MyDSCallback> dataCallBacks = widgetUpdater.initChartUpdater(manager, headerURL, dataURL, IDataFlowCtrl.DEF_DELAY_DATA_MILLIS);
        widgetUpdater.registerDataSource(dataURL,false,addDataSourceId);

         return new GridCtrl(addDataSourceId, dataCallBacks, criteria, headerURL, dataURL);
    }


    private String tableType;

    public Object getKeyColumn() {
        return keyColumn;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    private ChartType chartType;
    private Object keyColumn;//Ключевая колонка
    private String chartTitle;
    private Object[] nodePath;


    public boolean isJustInit() {
        return justInit;
    }

    private boolean justInit=true;

    public String getEventsName() {
        return tableType;
    }

    protected CreateChatView()
    {

    }

    public CreateChatView(int operationId, int parentOperationId, String viewName, TypeOperation type, String tableType)
    {
        super(operationId, parentOperationId, viewName, type);
        this.tableType=tableType;
    }

    private CreateChatView(CreateChatView operation)
    {
        super(operation.getOperationId(), operation.getParentOperationId(),operation.getViewName(), operation.getTypeOperation());
        this.tableType=operation.tableType;
        this.chartTitle=operation.chartTitle;
        this.chartType=operation.chartType;
        this.keyColumn=operation.keyColumn;
        this.justInit=operation.justInit;
        this.nodePath=operation.nodePath;

    }


    protected CalcManager manager = new CalcManager(
            new IChartLevelDef[]
            {
                    new ChartLevelDef(TablesTypes.KEY_FNAME,"DOR_KOD", "DOR_NAME"),
                    new ChartLevelDef(TablesTypes.KEY_FNAME,"VID_ID", "VID_NAME"),
                    new ChartLevelDef(TablesTypes.KEY_FNAME,"PRED_ID", "PRED_NAME"),
                    new ChartLevelDef(TablesTypes.KEY_FNAME,null, null) //Группировок нет вернуть значение указанного поля
            });

    protected GridCtrl ctrl;



    protected void initCtrl()
    {
        if (ctrl==null)
        {
            ctrl=initManagerAndCtrl(manager);

            App01.GUI_STATE_DESC.getUnloadListenerCtrl().addIndexListener(
                    new IClickListener<GUIStateDesc>() {
                        @Override
                        public void clickIndex(GUIStateDesc index)
                        {
                            ctrl.stopUpdateData();
                        }
                    }
            );

            ctrl.updateMeta(new DSCallback() {
                @Override
                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                    ctrl.startUpdateData(true);
                }
            });
        }
    }

    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        Canvas canvas = super.operate(dragTarget, ctx);


//        Map params=null;
//        if (ctx!=null)
//            params = ctx.getParams();
//        final Integer colNum=(params!=null)?(Integer)params.get("colNum"):0;
//        final Integer rowNum=(params!=null)?(Integer)params.get("rowNum"):0;

        if (canvas instanceof Portlet)
        {
            Portlet portlet = (Portlet) canvas;


            portlet.setShowCloseConfirmationMessage(false);
            portlet.setDestroyOnClose(true);
        }

        if (canvas instanceof Window)
        {

            final Window chartWidgetWrapper=(Window)canvas;

            DescExt descExt = manager.getIAnalisysDescExt();
            if (descExt==null)
            {
                initCtrl();
                final Timer[] t=new Timer[1];
                t[0]=new Timer()
                {
                    @Override
                    public void run()
                    {
                        DescExt descExt = manager.getIAnalisysDescExt();
                        if (descExt==null)
                            t[0].schedule(500);
                        else
                        {
                           _createNewChart(chartWidgetWrapper,colNum,rowNum);
                        }
                    }
                };
                t[0].schedule(500);
            }
            else
                _createNewChart(chartWidgetWrapper,colNum,rowNum);
        }

        if (isJustInit())
            return null;
        else
            return canvas;
    }


    private class MyIChartFactoryImpl extends IChartFactoryImpl
    {
        private final HeaderControl[] backLevel;
        private final Window chartWidgetWrapper;

        public MyIChartFactoryImpl(HeaderControl[] backLevel, Window chartWidgetWrapper) {
            this.backLevel = backLevel;
            this.chartWidgetWrapper = chartWidgetWrapper;
        }

        public BaseDOJOChart createChart(ChartType type, int width, int height)
        {
            BaseDOJOChart dojoChartPane=super.createChart(type,width,height);
            initHandlers(backLevel, chartWidgetWrapper, dojoChartPane);
            chartWidgetWrapper.addItem(dojoChartPane);
            return dojoChartPane;
        }

        private HeaderControl[] initHandlers(final HeaderControl[] backLevel,final Window chartWidgetWrapper, final BaseDOJOChart dojoChartPane)
        {
        backLevel[0] = new HeaderControl(
                HeaderControl.SETTINGS,
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event)
                    {
                        Window wnd = ChartOptionsView.createOptionView(manager, dojoChartPane,new IChartCtrlImpl(dojoChartPane,chartWidgetWrapper,backLevel));
                        wnd.show();
                    }
                });


        backLevel[1] = new HeaderControl(
                HeaderControl.DOUBLE_ARROW_UP,
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event)
                    {
                        backLevel[1].setDisabled(!manager.backLevel(dojoChartPane.getGraphId()));
                    }
                });
        backLevel[1].setDisabled(true);

        if (chartWidgetWrapper.isCreated() && chartWidgetWrapper.isDrawn())
            NodesHolder.addHeaderCtrl(chartWidgetWrapper, backLevel);
         else
        {
            final HandlerRegistration[] rs = new HandlerRegistration[1];
            rs[0] = chartWidgetWrapper.addDrawHandler(new DrawHandler() {
                @Override
                public void onDraw(DrawEvent event) {
                    NodesHolder.addHeaderCtrl(chartWidgetWrapper, backLevel);
                    rs[0].removeHandler();
                }
            });
        }


        return backLevel;
    }
    }


    protected void _createNewChart(Window chartWidgetWrapper,Integer colNum,Integer rowNum)
    {
        if (isJustInit())
        {
            Window wnd = ChartOptionsView.createOptionView(manager, null,new IChartCtrlImpl(chartWidgetWrapper,colNum,rowNum));
            wnd.show();
        }
        else
        {
            new IChartCtrlImpl(chartWidgetWrapper,colNum,rowNum).apply(chartType,keyColumn,chartTitle,nodePath);
            nodePath=null;//Сброс что не мешал дальнейшей работе, он переустанавливается уже внутри дескриптора окна при перемещении окна
        }


    }


    public static ColDef getColDef(String keyCol, Map<String, ColDef> tupleDef)
    {
        int ix=keyCol.lastIndexOf(UtilsData.KEY_DELIMITER);
        if (ix>=0)
            keyCol=keyCol.substring(ix+1);
        return tupleDef.get(keyCol);
    }

    public static String getTableDef(NNode2 node,String keyCol)
    {
        String tblName=node.getTblName();
        if (tblName==null || tblName.length()==0)
        {
            int fromIx=0;
            int ix=keyCol.indexOf(UtilsData.KEY_DELIMITER);
                if (ix==0)
            {
                fromIx=1;
                ix=keyCol.indexOf(UtilsData.KEY_DELIMITER,fromIx);
            }
            if (ix>0)
                return keyCol.substring(fromIx,ix);
            return null;
        }
        else
            return tblName;
    }


     private class IChartCtrlImpl implements IChartCtrl
     {
         private BaseDOJOChart dojoChartPane;
         private Window chartWidgetWrapper;
         private Integer colNum;
         private Integer rowNum;
         private HeaderControl[] oldControl;
         private HeaderControl[] backLevel;
         private MyIChartFactoryImpl chartFactory;

         private IChartCtrlImpl(BaseDOJOChart dojoChartPane,Window chartWidgetWrapper,
                               HeaderControl[] oldControl)
         {
             this.dojoChartPane = dojoChartPane;
             this.chartWidgetWrapper = chartWidgetWrapper;
             this.chartFactory = new MyIChartFactoryImpl(backLevel=new HeaderControl[2], chartWidgetWrapper);
             this.oldControl = oldControl;//Необходимо при переустановке типа графика
         }

         IChartCtrlImpl(Window chartWidgetWrapper,Integer colNum,Integer rowNum)
         {
             this.chartWidgetWrapper = chartWidgetWrapper;

             if (colNum==null) colNum=0;
             if (rowNum==null) rowNum=0;

             this.colNum = colNum;
             this.rowNum = rowNum;
             this.chartFactory = new MyIChartFactoryImpl(backLevel=new HeaderControl[2], chartWidgetWrapper);
         }

         @Override
         public void apply(ChartType chartType, Object select_value, String title)
         {
             apply(chartType,select_value,title,null);
         }

         private void apply(ChartType chartType, Object keyColumn, String title,Object[] initPath)
         {

            CreateChatView chartView=new CreateChatView(CreateChatView.this);
            chartView.chartType=chartType;
            chartView.keyColumn=keyColumn;
            chartView.chartTitle=title;
            chartView.nodePath=nodePath;

            final BaseDOJOChart[] _dojoChartPane=new BaseDOJOChart[1];
            boolean wasChange=true;



            ValDef currentNode=null;
            if (dojoChartPane!=null)
            {
                _dojoChartPane[0]=dojoChartPane;
                if (!chartType.equals(dojoChartPane.getChartType()))
                {
                    BaseChartWrapper wrapper = manager.removeChart(dojoChartPane);
                    currentNode = wrapper.getCurrentNode();

                    Canvas[] items = chartWidgetWrapper.getItems();
                    for (Canvas item : items)
                    {
                        chartWidgetWrapper.removeItem(item);
                        item.markForDestroy();
                    }
                    NodesHolder.removeHeaderCtrl(chartWidgetWrapper, oldControl);
                    _dojoChartPane[0]= chartFactory.createChart(chartType, 200, 200);
                }
            }
            else
                _dojoChartPane[0]= chartFactory.createChart(chartType, 200, 200);

            final DescOperation descOperation = chartView.getDescOperation(new DescOperation());
            descOperation.put(JUST_INIT,false);
            _dojoChartPane[0].setDescOperation(descOperation);


            ColumnMeta columnMeta;
            {
                DescExt descExt = manager.getIAnalisysDescExt();
                Map<Integer, String> num2key=descExt.getNumber2Key();
                Integer colNumber = descExt.getKey2Number().get(keyColumn);
                String keyCol=num2key.get(colNumber);
                final Map<String, ColDef> tupleDef = descExt.getDesc().getTupleDef();

                columnMeta = new ColumnMeta(getTableDef(descExt.getKey2NNode().get(keyCol),keyCol),String.valueOf(colNumber), getColDef(keyCol, tupleDef));
            }

            BaseChartWrapper wrapper = manager.getChartWrapper(_dojoChartPane[0]);

            IOperationContext ctx = new OperationCtx(null, chartWidgetWrapper);

            if (wrapper!=null)
            {
                if (!columnMeta.getColId().equals(((CalcManager.ChartWrapper)wrapper).getColumnMeta().getColId()))
                {
                    wrapper = manager.removeChart(_dojoChartPane[0]);
                    CalcManager.ChartWrapper newWrapper = manager.addChart(wrapper.getChart(), title, columnMeta,ctx);

                    Canvas[] items = chartWidgetWrapper.getItems();
                    for (Canvas item : items)
                    {
                        if (!(item instanceof BaseDOJOChart))
                        {//проверка того что график не в режиме таблицы, если в режиме тогда добавить его.
                            chartWidgetWrapper.removeItem(item);
                            item.markForDestroy();
                            chartWidgetWrapper.addItem(newWrapper.getChart());
                            break;
                        }
                    }
                    newWrapper.copyClickListeners(wrapper);
                }
                else if (wrapper.getTitle()==null || !wrapper.getTitle().equals(title))
                    wrapper.setBaseTitle(title);
                else
                    wasChange=false;
            }
            else
            {
                wrapper = manager.addChart(_dojoChartPane[0],title, columnMeta,currentNode,ctx);
                wrapper.addIndexListener(new IClickListener<CalcManager.ChartWrapper>() {
                    @Override
                    public void clickIndex(CalcManager.ChartWrapper wrapper)
                    {
                        backLevel[1].setDisabled(!manager.hasBackLevel(_dojoChartPane[0].getGraphId()));
                    }
                });
            }

             if (initPath!=null)
             {
                 wrapper.setInitPath(initPath);
                 wasChange=true;
             }


             if (wasChange)
             {
                chartWidgetWrapper.setTitle(title);

                if (SC.isIE())
                {
                   JavaScriptObject js = GUIStateDesc.getUserAgent2();
                    if (js!=null)
                    {
                       String isIE11=JSOHelper.getAttribute(js, "isIE11");
                       if (!("true".equalsIgnoreCase(isIE11)))
                            chartWidgetWrapper.setAnimateMinimize(false);
                    }
                }

                manager.getRootValue().setRecalc();
                if (chartWidgetWrapper instanceof Portlet && ((Portlet)chartWidgetWrapper).getPortalLayout()==null)
                     layout.addPortlet((Portlet)chartWidgetWrapper,colNum,rowNum);
             }
        }
     }

//    protected void _operate(final HeaderControl[] backLevel,final BaseDOJOChart dojoChartPane,CalcManager.DescExt descExt)
//    {
//        Map<Integer, String> n2k = descExt.getNumber2Key();
//        ColDef colDef = ChartOptionsView.getColDef(n2k.get(colix), descExt.getDesc().getTupleDef());
//        CalcManager.ChartWrapper wrapper = manager.addChart(dojoChartPane, "Предупреждения", new ColumnMeta(String.valueOf(colix), colDef));
//        wrapper.addIndexListener(new IClickListener<CalcManager.ChartWrapper>() {
//            @Override
//            public void clickIndex(CalcManager.ChartWrapper wrapper)
//            {
//                backLevel[1].setDisabled(!manager.hasBackLevel(dojoChartPane.getGraphId()));
//            }
//        });
//        manager.getRootValue().setRecalc();
//
//    }

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateChatView();
    }

    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        CreateChatView retOperation= (CreateChatView) super.createOperation(descOperation,operation);
        retOperation.tableType=(String)descOperation.get(EVENTS_NAME);
        retOperation.chartTitle=(String)descOperation.get(CHART_TITLE);
        retOperation.chartType=ChartType.valueOf(String.valueOf(descOperation.get(CHART_TYPE)));
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


    protected static final String KEY_COLUMN = "KEY_COLUMN";
    protected static final String CHART_TITLE = "CHART_TITLE";
    protected static final String CHART_TYPE = "CHART_TYPE";
    protected static String EVENTS_NAME ="EVENTS_NAME";
    protected static final String JUST_INIT = "JUST_INIT";
    public static final String NODE_PATH = "NODE_PATH";

    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);
        descOperation.put(EVENTS_NAME, getEventsName());
        descOperation.put(CHART_TITLE,getChartTitle());
        descOperation.put(CHART_TYPE,getChartType());
        descOperation.put(KEY_COLUMN,getKeyColumn());
        descOperation.put(JUST_INIT,isJustInit());

        return descOperation;
    }


}
