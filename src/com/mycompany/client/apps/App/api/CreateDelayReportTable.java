package com.mycompany.client.apps.App.api;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.reps.DelayGrid;
import com.mycompany.client.apps.App.reps.IReportCreator;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 18:26
 * Отчет по задержкам поездов
 */
public class CreateDelayReportTable extends CreateBaseReport
{
    public CreateDelayReportTable(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected CreateDelayReportTable()
    {
    }


    protected IReportCreator getReportCreator()
    {
        return new DelayGrid(NodesHolder.gridMetaProvider);
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateDelayReportTable();
    }

}
