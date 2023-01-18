package com.mycompany.client.apps.App.api;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.reps.DelayGrid;
import com.mycompany.client.apps.App.reps.IReportCreator;
import com.mycompany.client.apps.App.reps.PlacesGrid;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 18:26
 * Создать таблицу отчетов
 */
public class CreatePlacesTable extends CreateBaseReport {

    public CreatePlacesTable(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected CreatePlacesTable()
    {
    }

    protected IReportCreator getReportCreator()
    {
        return new PlacesGrid(NodesHolder.gridMetaProvider);
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreatePlacesTable();
    }




}
