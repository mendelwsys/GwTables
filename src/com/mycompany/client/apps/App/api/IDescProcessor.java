package com.mycompany.client.apps.App.api;

import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 18:37
 * Интерфейс обработчика дескриптора операций, после получения операции необходимо ее выполнить
 */
public interface IDescProcessor
{


    IOperation createOperation(DescOperation descOperation);
    IOperation createOperation(DescOperation descOperation, IOperation operation);
    DescOperation getDescOperation(DescOperation descOperation);
}
