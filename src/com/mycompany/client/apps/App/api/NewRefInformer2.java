package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.test.informer.ref.RefInformer;
import com.mycompany.client.test.informer.ref.RefInformer2;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:59
 * Операция создания нового Информера
 */
public class NewRefInformer2 extends CreateInformerOperation
{
    public NewRefInformer2(int operationId, int parentOperationId, String viewName, TypeOperation type)
    {
        super(operationId, parentOperationId, viewName, type);
    }

    protected NewRefInformer2()
    {
    }

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new NewRefInformer2();
    }


    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        Canvas c = super.operate(dragTarget, ctx);
        Canvas parentLayout=App01.GUI_STATE_DESC.getMainLayout();

        new RefInformer2().initInformer(parentLayout,this.getDescOperation(new DescOperation()));

        return null;
    }
}
