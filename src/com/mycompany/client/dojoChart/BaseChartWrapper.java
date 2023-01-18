package com.mycompany.client.dojoChart;

import com.mycompany.client.apps.App.api.CreateChatView;
import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.client.utils.IClickListener;
import com.mycompany.client.utils.ListenerCtrl;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 03.03.15
 * Time: 19:22
 * To change this template use File | Settings | File Templates.
 */
public class BaseChartWrapper extends ListenerCtrl<BaseChartWrapper>
{
    private BaseCalcManager manager;

    public void onSetState()
    {
        this.clickIndex(this);
    }

    public BaseDOJOChart getChart() {
        return chart;
    }

    protected BaseDOJOChart chart;
    private int ixListener;

    public String getTitle() {
        return title;
    }

    public void setBaseTitle(String title)
    {
        Object id=chart.getGraphId();
        IGetRecordVal funct;
        for (IChartLevelDef defI : manager.defIs)
        {
            funct = defI.getGroupF(id);
            if (funct != null)
                funct.setBaseTitle(title);
        }
        this.title = title;
    }

    protected String title;
    protected IOperationContext ctx;

    public ValDef getCurrentNode() {
        return currentNode;
    }


    protected ValDef currentNode;
    private Object[] initPath;

    public void setInitPath(Object[] initPath)
    {
        this.initPath=initPath;
        currentNode=manager.getRootValue();
    }

    public Object[] getInitPath()
    {
        Object[] rv = this.initPath;
        this.initPath=null; //Сброс начального знчения
        return rv;
    }


    protected BaseChartWrapper(BaseCalcManager manager,BaseDOJOChart _chart, String title, ValDef currentNode, IOperationContext ctx)
    {
        this.chart = _chart;
        this.title = title;
        this.ctx = ctx;
        this.manager=manager;
        this.currentNode = (currentNode==null?manager.getRootValue():currentNode);

        this.ixListener =chart.getListenerCtrl().addIndexListener(new IClickListener<Integer>() {
            @Override
            public void clickIndex(Integer index)
            {
                if (index!=null)
                {
                    Object key = chart.getKeyByIndex(index);
                    ValDef newNode = BaseChartWrapper.this.currentNode.getChildDef().get(key);
                    if (newNode != null)
                    {
                        BaseChartWrapper.this.manager.setDescOperationByNode(CreateChatView.NODE_PATH, newNode, chart.getDescOperation());
                        BaseChartWrapper.this.currentNode = newNode;
                        BaseChartWrapper.this.clickIndex(BaseChartWrapper.this);
                        drawByCurrentNode();
                    }
                }
            }
        });
    }

    protected void drawByCurrentNode()
    {
        chart.drawDefaultChart(BaseChartWrapper.this.currentNode, null);
    }

    public boolean backLevel()
    {
        ValDef parentDef = currentNode.getParentDef();
        if (parentDef!=null)
        {
            currentNode=parentDef;
            drawByCurrentNode();
        }
        return currentNode.getParentDef()!=null;
    }

    public void onRemove()
    {
        chart.getListenerCtrl().removeIndexListener(ixListener);
    }

}
