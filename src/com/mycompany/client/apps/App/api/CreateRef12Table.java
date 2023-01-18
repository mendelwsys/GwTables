package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.reps.IReportCreator;
import com.mycompany.client.apps.App.reps.Ref12Grid;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 18:26
 * Создать таблицу отчетов
 */
public class CreateRef12Table extends CreateBaseReport {

    public CreateRef12Table(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected CreateRef12Table()
    {
    }

    protected IReportCreator getReportCreator()
    {
        return new Ref12Grid(NodesHolder.gridMetaProvider);
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateRef12Table();
    }




}
