package com.mycompany.client.apps.App;

import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.GroupByCompleteEvent;
import com.smartgwt.client.widgets.grid.events.GroupByCompleteHandler;

import java.util.Arrays;

/**
 * Created by Anton.Pozdnev on 17.04.2015.
 */
public class VAGTORGridConstructor extends BGridConstructor {
    boolean first_updated = false;
//    boolean is_grouping = false;
    static final String[] fields = {"THIS_DATE", "LAST_DATE", "EIGHT_MORE", "EIGHT_LESS", "THIS_DATE_VSP", "LAST_DATE_VSP","STAN_ID"};

    static {

        Arrays.sort(fields);

    }

    @Override
    public void updateDataGrid(Record[] data, boolean resetAll) throws SetGridException {


        super.updateDataGrid(data, resetAll);
        // this.getGrid().
        if (data == null || data.length == 0) return;
        final ListGrid lg = this.getGrid();
        if (!first_updated) {
            first_updated = true;
            ListGridField[] gridFields = lg.getAllFields();
            for (int i = 0; i < gridFields.length; i++) {


                if (Arrays.binarySearch(fields, gridFields[i].getName()) < 0) {
                    gridFields[i].setShowGroupSummary(false);
                    gridFields[i].setShowGridSummary(false);
                    gridFields[i].setIncludeInRecordSummary(false);


                }
                else if (gridFields[i].getName().equals("STAN_ID"))
                {
                    gridFields[i].setShowGroupSummary(true);
                    gridFields[i].setSummaryFunction(SummaryFunctionType.MIN);
                }
                else {
                    gridFields[i].setShowGroupSummary(true);
                    gridFields[i].setSummaryFunction(SummaryFunctionType.SUM);

                }


            }

//             this.getGrid().setGroupByField();
//TODO удалить группировку
   this.getGrid().groupBy(TablesTypes.DOR_NAME, "STAN");
            // final ListGridWithDesc thisGrid = this.getGrid();

//-------------------------------------------------------------------------------------------
//            this.getGrid().addGroupByCompleteHandler(new GroupByCompleteHandler() {
//                @Override
//                public void onGroupByComplete(GroupByCompleteEvent event) {
//                    //thisGrid.setShowGroupSummaryInHeader(true);
//                    is_grouping = false;
//                }
//            });
        }

//        synchronized (this) {  //TODO Спросить  Антона зачем он так сделал?!
//            if (!is_grouping) {
//                is_grouping = true;
//                if (lg.getGroupByFields() == null || lg.getGroupByFields().length == 0)
//                    lg.groupBy(TablesTypes.DOR_NAME, "STAN");
//                else
//                    lg.groupBy(lg.getGroupByFields());
//            }
//        }
//-------------------------------------------------------------------------------------------

    }
}
