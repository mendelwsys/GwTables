package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.reps.IReportCreator;
import com.mycompany.client.apps.App.reps.LOCGrid;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 18:26
 * Отчет по задержкам поездов
 */
public class CreateLOCReportTable extends CreateBaseReport
{
    public CreateLOCReportTable(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected CreateLOCReportTable()
    {
    }


    protected IReportCreator getReportCreator()
    {
        return new LOCGrid(NodesHolder.gridMetaProvider);
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateLOCReportTable();
    }

}
