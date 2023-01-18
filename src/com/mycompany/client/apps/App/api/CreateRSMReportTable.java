package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.reps.IReportCreator;
import com.mycompany.client.apps.App.reps.RSMGrid;
import com.mycompany.client.apps.App.reps.WarnAGRGrid;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 18:26
 * Отчет по задержкам поездов
 */
public class CreateRSMReportTable extends CreateBaseReport
{
    public CreateRSMReportTable(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected CreateRSMReportTable()
    {
    }


    protected IReportCreator getReportCreator()
    {
        return new RSMGrid(NodesHolder.gridMetaProvider);
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateRSMReportTable();
    }

}
