package com.mycompany.client.apps.App;

import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:59
 * Операция создания нового портлета по умолчанию
 */
public class SimpleNewPortlet extends SimpleOperation
{
    public SimpleNewPortlet(int operationId, int parentOperationId, String viewName, TypeOperation type)
    {
        super(operationId, parentOperationId, viewName, type);
    }

    protected SimpleNewPortlet()
    {
    }

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new SimpleNewPortlet();
    }


    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        Portlet portlet = new Portlet();
        portlet.setTitle(this.getViewName());
        portlet.setShowCloseConfirmationMessage(false);

        return portlet;
    }
}
