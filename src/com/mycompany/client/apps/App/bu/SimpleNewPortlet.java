package com.mycompany.client.apps.App.bu;

import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.test.myportalview.MyPortlet;
import com.smartgwt.client.widgets.Canvas;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
public class SimpleNewPortlet extends SimpleOperation
{
    public SimpleNewPortlet(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        MyPortlet portlet = new MyPortlet();
        portlet.setTitle(this.getViewName());
        return portlet;
    }
}
