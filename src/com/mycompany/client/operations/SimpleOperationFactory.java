package com.mycompany.client.operations;

import com.smartgwt.client.widgets.Canvas;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 09.06.14
 * Time: 16:22
 * Простая имплементация фабрики
 */
public abstract class SimpleOperationFactory implements IOperationFactory
{
    IOperation defOperation;
    public SimpleOperationFactory()
    {
        defOperation= this._getOperation();
    }

    abstract protected IOperation _getOperation();

    public IOperation getOperation() {

        IOperation operation = _getOperation();
        if (defOperation!=null && defOperation instanceof IOperationParam && operation instanceof IOperationParam)
           ((IOperationParam)operation).setParams(new LinkedList(((IOperationParam)defOperation).getParams()));
        return operation;
    }

//    public Canvas getInputFrom(Canvas _warnGrid)
//    {
//        return defOperation.getInputFrom(_warnGrid);
//    }
}
