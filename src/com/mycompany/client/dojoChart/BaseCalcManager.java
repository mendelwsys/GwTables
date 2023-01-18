package com.mycompany.client.dojoChart;

import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.common.DescOperation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 03.02.15
 * Time: 11:37
 *
 */
abstract public class BaseCalcManager
{

    public IChartLevelDef[] getDefs() {
        return defIs;
    }

    protected IChartLevelDef[] defIs;

    public BaseCalcManager(IChartLevelDef[] defIs)
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

    public BaseChartWrapper getChartWrapper(BaseDOJOChart chart)
    {
        return getChartWrapper(chart.getGraphId());
    }


    public BaseChartWrapper getChartWrapper(Object graphId)
    {
        return id2View.get(graphId);
    }


    public boolean backLevel(Object graphId)
    {
        BaseChartWrapper wrapper=id2View.get(graphId);
        return wrapper != null && wrapper.backLevel();
    }

    public boolean hasBackLevel(Object graphId)
    {
        BaseChartWrapper wrapper=id2View.get(graphId);
        return wrapper != null && wrapper.getCurrentNode().getParentDef()!=null;
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


    protected Map<Object,BaseChartWrapper> id2View = new HashMap<Object,BaseChartWrapper>();


    public BaseChartWrapper addChart(final BaseDOJOChart chart, String title, final IOperationContext ctx)
    {
        return addChart(chart,title,null,ctx);
    }

    public BaseChartWrapper addChart(final BaseDOJOChart chart, String title, ValDef currentNode,final IOperationContext ctx)
    {
        final Object graphId = chart.getGraphId();
        final BaseChartWrapper rv;
        id2View.put(graphId, rv = createWrapper(chart, title,currentNode,ctx));

        IGetRecordVal[] funct = createCalcFunctions(rv, title,graphId);
        for (int i = 0; i < defIs.length; i++)
            defIs[i].setGroupF(funct[i]);
        return rv;
    }

//    public  void reInitCalcFunction(BaseChartWrapper wrapper)
//    {
//        IGetRecordVal[] funct = createCalcFunctions(wrapper, wrapper.getTitle(),wrapper.getChart().getGraphId());
//        for (int i = 0; i < defIs.length; i++)
//            defIs[i].setGroupF(funct[i]);
//    }

    protected BaseChartWrapper createWrapper(BaseDOJOChart chart, String title, ValDef currentNode, IOperationContext ctx)
    {
        return new BaseChartWrapper(this,chart, title,currentNode,ctx);
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

    protected void setDescOperationByNode(String prefix, ValDef currentNode, DescOperation descOperation)
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
        BaseChartWrapper chartWrapper=id2View.get(key);
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
                BaseChartWrapper chartWrapper=id2View.get(key);
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
                BaseChartWrapper chartWrapper=id2View.get(key);
                chartWrapper.currentNode=getValDefByPath(getRootValue(), path, 0);
                chartWrapper.onSetState();
                chartWrapper.drawByCurrentNode();
            }
        }
    }

    public BaseChartWrapper removeChart(BaseDOJOChart chart)
    {
        return removeChart(chart.getGraphId());
    }

    public BaseChartWrapper removeChart(Object graphId)
    {
        BaseChartWrapper chartWrapper = id2View.remove(graphId);
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

    /**
     * Получить список функция для вычисления значений разных уровней
     * @param wrapper - обертка графика
     * @param baseTitle - заголовок графика базовый (к которому присоединяется параметрический заголовок графика)
     * @param graphId - идентификатор графика
     * @return - набор функций, по олдному для каждого уровня
     */
    abstract protected IGetRecordVal[] createCalcFunctions(BaseChartWrapper wrapper, String baseTitle, Object graphId);

}


