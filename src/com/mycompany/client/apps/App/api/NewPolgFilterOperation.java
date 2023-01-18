package com.mycompany.client.apps.App.api;

import com.mycompany.client.CliFilterByCriteria;
import com.mycompany.client.PolgFilterByCriteria;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 09.09.15
 * Time: 16:36
 * операция для создания фильтра по местам событий
 */
public class NewPolgFilterOperation extends  NewFilterOperation
{
    public NewPolgFilterOperation(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        NewPolgFilterOperation operation = new NewPolgFilterOperation();
        operation.setJustInit(false);
        return operation;
    }

    public NewPolgFilterOperation() {
    }

    protected CliFilterByCriteria createFilter(DataSource filterDS, Criteria criteria)
    {
        return new PolgFilterByCriteria(filterDS, criteria);
    }

}
