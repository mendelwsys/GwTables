package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.test.informer.delays.DelayInformer;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:59
 * Операция создания нового Информера
 */
public class NewDelayInformer extends CreateInformerOperation
{
    public NewDelayInformer(int operationId, int parentOperationId, String viewName, TypeOperation type)
    {
        super(operationId, parentOperationId, viewName, type);
    }

    protected NewDelayInformer()
    {
    }

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new NewDelayInformer();
    }


    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        Canvas c = super.operate(dragTarget, ctx);
        Canvas parentLayout=App01.GUI_STATE_DESC.getMainLayout();

        new DelayInformer().initInformer(parentLayout, this.getDescOperation(new DescOperation()));
        return null;
    }

   /* @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        NewDelayInformer retOperation= (NewDelayInformer) super.createOperation(descOperation,operation);
      //  retOperation.dor_kod=(Integer)descOperation.get(DOR_KOD);
       // retOperation.crd_left=(Integer)descOperation.get(CRD_LEFT);
       // retOperation.crd_top=(Integer)descOperation.get(CRD_TOP);
        return retOperation;
    }



    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);


        return descOperation;
    }*/

}
