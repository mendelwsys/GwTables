package com.mycompany.client.test.Demo;

import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.client.apps.SimpleOperation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
public class SimpleNewPortlet extends SimpleOperation
{
//        public SimpleNewPortlet(int operationId, int parentOperationId, String viewName) {
//            super(operationId, parentOperationId, viewName);
//        }

    public SimpleNewPortlet(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }



    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        Portlet portlet = new Portlet();
        portlet.setTitle(this.getViewName());
        portlet.setShowCloseConfirmationMessage(false);
        return portlet;
    }
}
