package com.mycompany.client.test.Demo;


import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.client.operations.IOperation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public abstract class DefNewPortlet extends SimpleNewPortlet
{
    public DefNewPortlet(int operationId, int parentOperationId, String viewName, IOperation.TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    public abstract String getTableType();
    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        Portlet portlet=(Portlet)super.operate(dragTarget, ctx);
        final ListGrid warnings = DemoApp01.createTable(portlet, getTableType(), false);
        portlet.addItem(warnings);
        return portlet;
    }

}
