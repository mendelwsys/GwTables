package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.test.informer.ref.ViolInformer;
import com.mycompany.client.test.informer.ref.ViolInformer2;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:59
 * Операция создания нового Информера
 */
public class NewViolInformer2 extends CreateInformerOperation
{
    public NewViolInformer2(int operationId, int parentOperationId, String viewName, TypeOperation type)
    {
        super(operationId, parentOperationId, viewName, type);
    }

    protected NewViolInformer2()
    {
    }

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new NewViolInformer2();
    }


    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        Canvas c = super.operate(dragTarget, ctx);
        Canvas parentLayout=App01.GUI_STATE_DESC.getMainLayout();

        new ViolInformer2().initInformer(parentLayout,this.getDescOperation(new DescOperation()));

        return null;
    }
}
