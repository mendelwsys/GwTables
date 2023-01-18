package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.reps.IReportCreator;
import com.mycompany.client.apps.App.reps.OIndxGrid;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 18:26
 * Создать таблицу отчетов
 */
public class CreateOIndxTable extends CreateBaseReport {

    public CreateOIndxTable(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected CreateOIndxTable()
    {
    }


    protected IReportCreator getReportCreator()
    {
        return new OIndxGrid(NodesHolder.gridMetaProvider);
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateOIndxTable();
    }




}
