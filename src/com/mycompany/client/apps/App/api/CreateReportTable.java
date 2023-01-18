package com.mycompany.client.apps.App.api;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.reps.IReportCreator;
import com.mycompany.client.apps.App.reps.InfraGrid;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.SimpleNewPortlet;
import com.mycompany.client.apps.App.reps.PlacesGrid;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.layout.Portlet;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 13.12.14
 * Time: 18:26
 * Создать таблицу отчетов
 */
public class CreateReportTable extends CreateBaseReport {

    public CreateReportTable(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected CreateReportTable()
    {
    }


    protected IReportCreator getReportCreator()
    {
        return new InfraGrid(NodesHolder.gridMetaProvider);
    }


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateReportTable();
    }




}
