package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.test.informer.delays.DelayInformer3;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;

/**
 * Created by Anton.Pozdnev on 13.08.2015.
 */
public class NewDelayInformer3 extends CreateInformerOperation {
    public NewDelayInformer3(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected NewDelayInformer3() {
    }

    protected IOperation getEmptyObject(DescOperation descOperation) {
        return new NewDelayInformer3();
    }


    public Canvas operate(Canvas dragTarget, IOperationContext ctx) {
        Canvas c = super.operate(dragTarget, ctx);
        Canvas parentLayout = App01.GUI_STATE_DESC.getMainLayout();

        new DelayInformer3().initInformer(parentLayout, this.getDescOperation(new DescOperation()));
        return null;
    }

}