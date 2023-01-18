package com.mycompany.client.dojoChart;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.apps.App.api.CreateChatView;
import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.client.utils.IClickListener;
import com.mycompany.client.utils.ListenerCtrl;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 03.02.15
 * Time: 11:37
 *  TODO Для удаления от 04.03.2015
 */
public class CalcManager_bu
{

    public IChartLevelDef[] getDefs() {
        return defIs;
    }

    protected IChartLevelDef[] defIs;

    public CalcManager_bu(IChartLevelDef[] defIs)
    {
        this.defIs = defIs;
    }

    public void addFunctions(IGetRecordVal[] recordVals)
    {
        for (int i = 0; i < defIs.length; i++)
            defIs[i].setGroupF(recordVals[i]);
    }

    private ValDef rootDef;
    public ValDef getRootValue()
    {
        if (rootDef==null) reCreateRootValue();
        return rootDef;
    }

    public ValDef reCreateRootValue()
    {
        return rootDef=new ValDef(getDefs()[0],null,null,null);
    }

    public ChartWrapper getChartWrapper(BaseDOJOChart chart)
    {
        return getChartWrapper(chart.getGraphId());
    }


    public ChartWrapper getChartWrapper(Object graphId)
    {
        return id2View.get(graphId);
    }


    public boolean backLevel(Object graphId)
    {
        ChartWrapper wrapper=id2View.get(graphId);
        return wrapper != null && wrapper.backLevel();
    }

    public boolean hasBackLevel(Object graphId)
    {
        ChartWrapper wrapper=id2View.get(graphId);
        return wrapper != null && wrapper.getCurrentNode().getParentDef()!=null;
    }


    public class ChartWrapper extends ListenerCtrl<ChartWrapper>
    {
        public void onSetState()
        {
            this.clickIndex(this);
        }

        public BaseDOJOChart getChart() {
            return chart;
        }

        private BaseDOJOChart chart;
        private int ixListener;

        public String getTitle() {
            return title;
        }

        public void setBaseTitle(String title)
        {
            Object id=chart.getGraphId();
            IGetRecordVal funct;
            for (IChartLevelDef defI : defIs)
            {
                funct = defI.getGroupF(id);
                if (funct != null)
                    funct.setBaseTitle(title);
            }
            this.title = title;
        }

        public String getColName() {
            return columnMeta.getColId();
        }

        private String title;
        private ColumnMeta columnMeta;
        private IOperationContext ctx;

        public ValDef getCurrentNode() {
            return currentNode;
        }

        public ColumnMeta getColumnMeta() {
            return columnMeta;
        }

        private ValDef currentNode;
        private Object[] initPath;

        public void setInitPath(Object[] initPath)
        {
            this.initPath=initPath;
            currentNode=getRootValue();
        }

        public Object[] getInitPath()
        {
            Object[] rv = this.initPath;
            this.initPath=null; //Сброс начального знчения
            return rv;
        }


        ChartWrapper(BaseDOJOChart _chart, String title, ColumnMeta columnMeta,ValDef currentNode,IOperationContext ctx)
        {
            this.chart = _chart;
            this.title = title;
            this.columnMeta = columnMeta;
            this.ctx = ctx;
            this.currentNode = (currentNode==null?getRootValue():currentNode);

            this.ixListener =chart.getListenerCtrl().addIndexListener(new IClickListener<Integer>() {
                @Override
                public void clickIndex(Integer index)
                {
                    if (index!=null)
                    {
                        Object key = chart.getKeyByIndex(index);
                        ValDef newNode = ChartWrapper.this.currentNode.getChildDef().get(key);

                        if (newNode != null)
                        {
                            setDescOperationByNode(CreateChatView.NODE_PATH, newNode, chart.getDescOperation());
                            ChartWrapper.this.currentNode = newNode;
                            ChartWrapper.this.clickIndex(ChartWrapper.this);
                            drawByCurrentNode();
                        }
                    }
                }
            });
        }

        private void drawByCurrentNode()
        {

//TODO Закоментировано   04032015               ValDef newNode=currentNode;
//            if (newNode.getMetaDef().getColName()==null)
//            {
//
//                String tableType= this.formatValue.getTableType();
//                if (tableType!=null)
//                {
//                    final IOperationContext ctx = (IOperationContext) this.ctx.copy();
//                    Window wnd= (Window) ctx.getDst();
//                    Canvas[] items = wnd.getItems();
//                    for (Canvas item : items)
//                        if (item instanceof ListGridWithDesc)
//                            return;//TODO (Подпорка!!!!) Не допускать повтороной инициализации уже проинициализированного грида
//
//                    chart.setVisible(false);//TODO Да работает, только надо как то устанавливать будем ли мы сохранять график или нет
//                    chart.set2Save(false);
////                    wnd.removeItem(chart);//Посмотреть возможно просто скрывать график, тогда он не будет перегружаться???
//
//                    IOperation operation = new CreateEventTable(-1, -1, "", IOperation.TypeOperation.addEventPortlet, tableType);
//                    operation.operate(null, ctx);
//
//                    final ListGridWithDesc newGrid = (ListGridWithDesc) ctx.getChildList().get(0).getSrc();
//                    wnd.setTitle(title+" "+currentNode.getFullViewText());
//
//                    newGrid.setDescOperation(this.getChart().getDescOperation());
//
//                    IServerFilter serverFilter;
//                    newGrid.setServerDataFilter(serverFilter = new CommonServerFilter(TablesTypes.FILTERDATAEXPR));
//
//                    AdvancedCriteria cr = new AdvancedCriteria(OperatorId.AND);
//
//                    final Object grpValue = newNode.getGrpValue();
//                    if (!(grpValue instanceof Integer))
//                    {
//                  List<Pair<String, Object>> ll = newNode.getParentDef().getParentGroupsVal();
//                        for (Pair<String, Object> colName2groupDef : ll)
//                        {
//                            final Object second = colName2groupDef.second;
//                            if (second==null)
//                                cr.appendToCriterionList(new Criterion(colName2groupDef.first, OperatorId.IS_NULL)); //TODO Обработать это на сервере!!!!
//                            else if (second instanceof Integer)
//                                cr.appendToCriterionList(new Criterion(colName2groupDef.first, OperatorId.EQUALS,(Integer) second));
//                            else
//                                cr.appendToCriterionList(new Criterion(colName2groupDef.first, OperatorId.EQUALS,"'"+String.valueOf(second)+"'")); //TODO Это специфика данной группировки,!!! не общее решение!!!!
//
//                        }
//                        cr.appendToCriterionList(new Criterion(newNode.getParentDef().getMetaDef().getColName(), OperatorId.EQUALS,0));
//                    }
//                    else
//                        cr.appendToCriterionList(new Criterion(newNode.getParentDef().getMetaDef().getColName(), OperatorId.EQUALS,(Integer) grpValue));
//
//
//                    serverFilter.setCriteria(cr);//Установить фильтр сервера
//
//                    final IDataFlowCtrl ctrl = newGrid.getCtrl();
//                    serverFilter.set2Criteria(ctrl.getCriteria());
//
//                    new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
//                    {
//                        @Override
//                        public boolean operate()
//                        {
//                            if (newGrid.isMetaWasSet())
//                            {
//
//                                final ListGridField[] allFields = newGrid.getAllFields();
//                                if (allFields==null || allFields.length==0)
//                                {
//                                    Window wnd= (Window) ctx.getDst();
//                                    wnd.removeItem(newGrid);
//                                    wnd.setTitle(title);
//                                    chart.setVisible(true);
//                                    chart.set2Save(true);
////                                    wnd.addItem(chart);
//                                    chart.drawDefaultChart(ChartWrapper.this.currentNode, null);
//                                }
//                                else
//                                {
//                                    ctrl.setFullDataUpdate();
//                                    ctrl.startUpdateData(true);
//                                }
//                                return true;
//                            }
//                            return false;
//                        }
//                    });
//
//                }
//            }
//            else
            {
                chart.drawDefaultChart(ChartWrapper.this.currentNode, null);
            }
        }

        public boolean backLevel()
        {
            ValDef parentDef = currentNode.getParentDef();
            if (parentDef!=null)
            {
                Window wnd= (Window) ctx.getDst();
                Canvas[] cnvs=wnd.getItems();
                for (Canvas cnv : cnvs)
                    if (!(cnv instanceof BaseDOJOChart))
                    {
                        wnd.removeItem(cnv);//TODO Переделать это на что-то более вменяемое
                        cnv.markForDestroy();

                        wnd.setTitle(title);
                        chart.setVisible(true);

                        chart.set2Save(true);
//                        wnd.addItem(chart);
                        break;
                    }


                currentNode=parentDef;
//                chart.drawDefaultChart(currentNode, null); //TODO Это верно тольк во одном случае если и толко если у нас один не стандартный вьювер на конце
                drawByCurrentNode();
            }
            return currentNode.getParentDef()!=null;
        }

        public void onRemove()
        {
            chart.getListenerCtrl().removeIndexListener(ixListener);
        }

    }

    public class StateViews
    {
        public Map<Object, Object[]> getState() {
            return state;
        }

        Map<Object,Object[]> state;

        public StateViews(Map<Object, Object[]> state) {
            this.state = state;
        }
    }


    Map<Object,ChartWrapper> id2View = new HashMap<Object,ChartWrapper>();


    public ChartWrapper addChart(final BaseDOJOChart chart, String title, ColumnMeta columnMeta,final IOperationContext ctx)
    {
        return addChart(chart,title,columnMeta,null,ctx);
    }


    public ChartWrapper addChart(final BaseDOJOChart chart, String title, ColumnMeta columnMeta,ValDef currentNode,final IOperationContext ctx)
    {

        final Object graphId = chart.getGraphId();
        final ChartWrapper rv;
        id2View.put(graphId, rv = new ChartWrapper(chart, title, columnMeta,currentNode,ctx));

        IGetRecordVal[] funct = createCalcFunctions(title,columnMeta, graphId);
        for (int i = 0; i < defIs.length; i++)
            defIs[i].setGroupF(funct[i]);
        return rv;
    }


    private List getPathByNode(ValDef currentNode)
    {

        ValDef parent=currentNode.getParentDef();
        List rv;
        if (parent!=null)
            rv= getPathByNode(parent);
        else
            return new LinkedList();
        Object grpVal = currentNode.getGrpValue();
        rv.add(grpVal);
        return rv;
    }

    private void setDescOperationByNode(String prefix, ValDef currentNode, DescOperation descOperation)
    {
        if (descOperation!=null)
        {
            List ll = getPathByNode(currentNode);
            for (int i = 0, llSize = ll.size(); i < llSize; i++)
                descOperation.put(prefix+"_"+i,ll.get(i));
        }
    }


    public StateViews getAllCurrentState()
    {
        Map<Object,Object[]> rState= new HashMap<Object,Object[]>();
        for (Object key : id2View.keySet())
        {
            getCurrentStateByKey(rState, key);
        }
        return new StateViews(rState);
    }

    public StateViews getCurrentStateByKey(Object key)
    {
        Map<Object,Object[]> rState= new HashMap<Object,Object[]>();
        getCurrentStateByKey(rState, key);
        return new StateViews(rState);
    }

    private void getCurrentStateByKey(Map<Object, Object[]> rState, Object key)
    {
        ChartWrapper chartWrapper=id2View.get(key);
        Object[] path;
        if ((path=chartWrapper.getInitPath())==null)
        {
            List lPath= getPathByNode(chartWrapper.currentNode);
            path = lPath.toArray(new Object[lPath.size()]);
        }
        rState.put(key,path);
    }


    public ValDef getValDefByPath(ValDef root, Object[] path)
    {
        return getValDefByPath(root,path,0);
    }

    protected ValDef getValDefByPath(ValDef root, Object[] path, int ix)
    {
        if (path.length<=ix)
            return root;

        ValDef nextDef = root.getChildDef().get(path[ix]);
        if (nextDef!=null)
            return getValDefByPath(nextDef, path, ix + 1);
        return root;
    }


    public void updateJustLoadedChartCurrentState(StateViews state)
    {
        final Map<Object, Object[]> states = state.getState();
        for (Object key : id2View.keySet())
        {
            Object[] path= states.get(key);
            if (path!=null)
            {
                ChartWrapper chartWrapper=id2View.get(key);
                if (chartWrapper.chart.isVisible() && chartWrapper.chart.wasLoad() && !chartWrapper.chart.wasInit())
                {
                    chartWrapper.currentNode=getValDefByPath(getRootValue(), path, 0);
                    chartWrapper.onSetState();
                    //chartWrapper.chart.drawDefaultChart(chartWrapper.currentNode, null);
                    chartWrapper.drawByCurrentNode();
                }
            }
        }
    }


    public void updateCurrentState(StateViews state)
    {
        final Map<Object, Object[]> states = state.getState();
        for (Object key : id2View.keySet())
        {
            Object[] path= states.get(key);
            if (path!=null)
            {
                ChartWrapper chartWrapper=id2View.get(key);
                chartWrapper.currentNode=getValDefByPath(getRootValue(), path, 0);
                chartWrapper.onSetState();
                //chartWrapper.chart.drawDefaultChart(chartWrapper.currentNode, null);
                chartWrapper.drawByCurrentNode();
            }
        }
    }

    public ChartWrapper removeChart(BaseDOJOChart chart)
    {
        return removeChart(chart.getGraphId());
    }

    public ChartWrapper removeChart(Object graphId)
    {
        ChartWrapper chartWrapper = id2View.remove(graphId);
        if (chartWrapper !=null)
        {
            for (IChartLevelDef defI : defIs)
                defI.removeGroupF(graphId);

            ValDef rootVal = getRootValue();
            if (rootVal!=null)
                rootVal.removeByGrpId(graphId);

            chartWrapper.onRemove();
            return chartWrapper;
        }

        return null;
    }

    protected IGetRecordVal[] createCalcFunctions(String baseTitle, ColumnMeta columnMeta, Object graphId)
    {
        if (baseTitle==null)
            baseTitle="";
        final String title=baseTitle;


        return new IGetRecordVal[]{new SumGetRecordVal(graphId,columnMeta,title," [$val] дороги ","red"),new SumGetRecordVal(graphId,columnMeta,title," [$val] ($) службы ","blue"),new SumGetRecordVal(graphId,columnMeta,title,"","green")
        {
            public JavaScriptObject getCharTitle(ValDef def)
            {

                JavaScriptObject rv=super.getCharTitle(def);
                JSOHelper.setAttribute(rv, "title", this.baseTitle + " [" + getViewVal(def) + "] (" + def.getParentDef().getViewText() + ") предприятия службы " + def.getViewText());
                return rv;
            }
        },new GetRecordValByColNameEx(graphId, columnMeta, title," [ таблица отсутствует]","orange")};
    }

}


