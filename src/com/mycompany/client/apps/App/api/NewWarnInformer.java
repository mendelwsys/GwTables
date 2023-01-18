package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.test.informer.warn.WarnInformer;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:59
 * Операция создания нового Информера
 */
public class NewWarnInformer extends CreateInformerOperation
{
    public NewWarnInformer(int operationId, int parentOperationId, String viewName, TypeOperation type)
    {
        super(operationId, parentOperationId, viewName, type);
    }

    protected NewWarnInformer()
    {
    }

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new NewWarnInformer();
    }


    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        Canvas c = super.operate(dragTarget, ctx);
        Canvas parentLayout=App01.GUI_STATE_DESC.getMainLayout();

        new WarnInformer().initInformer(parentLayout,this.getDescOperation(new DescOperation()));

        return null;
    }

   /* @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        NewWarnInformer retOperation= (NewWarnInformer) super.createOperation(descOperation,operation);
      //  retOperation.dor_kod=(Integer)descOperation.get(DOR_KOD);
     //   retOperation.crd_left=(Integer)descOperation.get(CRD_LEFT);
     //   retOperation.crd_top=(Integer)descOperation.get(CRD_TOP);
        return retOperation;
    }

    public static final String DOR_KOD ="DOR_KOD";
    public static final String CRD_LEFT ="CRD_LEFT";
    public static final String CRD_TOP ="CRD_TOP";

    private Integer dor_kod;
    private Integer crd_left;
    private Integer crd_top;

    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);
        descOperation.put(DOR_KOD, dor_kod);
        descOperation.put(CRD_LEFT, crd_left);
        descOperation.put(CRD_TOP, crd_top);

        return descOperation;
    }*/

}
