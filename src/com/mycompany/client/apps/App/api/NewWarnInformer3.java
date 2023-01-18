package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.test.informer.warn.WarnInformer3;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:59
 * Операция создания нового Информера
 */
public class NewWarnInformer3 extends CreateInformerOperation {
    public NewWarnInformer3(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected NewWarnInformer3() {
    }

    protected IOperation getEmptyObject(DescOperation descOperation) {
        return new NewWarnInformer3();
    }


    public Canvas operate(Canvas dragTarget, IOperationContext ctx) {
        Canvas c = super.operate(dragTarget, ctx);
        Canvas parentLayout = App01.GUI_STATE_DESC.getMainLayout();

        new WarnInformer3().initInformer(parentLayout, this.getDescOperation(new DescOperation()));

        return null;
    }

}
