package com.mycompany.client.apps.App.api;

import com.mycompany.client.GridUtils;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.VAGTORGridConstructor;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.GroupStartOpen;

/**
 * Created by Anton.Pozdnev on 17.04.2015.
 */
public class CreateVAGTORTable extends CreateEventTable {


    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new CreateVAGTORTable();
    }


    protected ListGridWithDesc createGrid(String dataURL, String headerURL, Criteria criteria) {
        ListGridWithDesc gridTable = GridUtils.createGridTable(new VAGTORGridConstructor(), NodesHolder.gridMetaProvider, criteria, headerURL, dataURL, false, true);
//        gridTable.setGroupByField(TablesTypes.DOR_NAME, "STAN");
        if (isMultiGroup != null) gridTable.setCanMultiGroup(isMultiGroup);
        if (isMultiSort != null) gridTable.setCanMultiSort(isMultiSort);

        gridTable.setShowGroupSummaryInHeader(true);
        gridTable.setGroupTitleField(TablesTypes.DOR_NAME);
        gridTable.setGroupStartOpen(GroupStartOpen.NONE);
        gridTable.setShowGroupTitleColumn(false);
        gridTable.setGroupByMaxRecords(30000);
        gridTable.setShowGroupSummary(true);
        return gridTable;
    }

    public CreateVAGTORTable(int operationId, int parentOperationId, String viewName, IOperation.TypeOperation type, String tableType) {
        super(operationId, parentOperationId, viewName, type, tableType);
    }

    public CreateVAGTORTable() {

    }

}
