package com.mycompany.client;

import com.mycompany.client.test.aggregates.AggregatesSummariesBuilderDialog;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.updaters.IGridConstructor;
import com.mycompany.client.updaters.IGridFactory;
import com.mycompany.client.utils.IGridMetaProvider;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 20.11.14
 * Time: 12:53
 * Утилиты создания грида
 */
public class GridUtils
{


    public static ListGridWithDesc createGridTable(
            IGridMetaProvider gridMetaProvider, String tblType,
            String headerURL, final String dataURL, final boolean dynamicUpdate, final boolean noData
    )
    {
        Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);

        return GridUtils.createGridTable(gridMetaProvider, criteria, headerURL, dataURL, dynamicUpdate, noData);
    }



    public static ListGridWithDesc createGridTable(IGridConstructor gridConstructor,
                                           IGridMetaProvider gridMetaProvider, String tblType,
                                           String headerURL, final String dataURL, final boolean dynamicUpdate, final boolean noData
    )
    {
        Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);
        return GridUtils.createGridTable(gridConstructor, gridMetaProvider, criteria, headerURL, dataURL, dynamicUpdate, noData);
    }

    public static ListGridWithDesc createGridTable(
            IGridMetaProvider gridMetaProvider, final Criteria criteria,
            String headerURL, final String dataURL, final boolean dynamicUpdate, final boolean noData
    )
    {
        return GridUtils.createGridTable(new BGridConstructor(), gridMetaProvider, criteria, headerURL, dataURL, dynamicUpdate, noData);
    }

    public static ListGridWithDesc createGridTable(IGridConstructor gridConstructor,
                                                   IGridMetaProvider gridMetaProvider, final Criteria criteria,
                                                   String headerURL, final String dataURL, final boolean dynamicUpdate, final boolean noData)
    {
        return GridUtils.createGridTable(gridConstructor,new DefaultGridFactory(),gridMetaProvider, criteria, headerURL, dataURL, dynamicUpdate, noData);
    }

    public static ListGridWithDesc createGridTable(IGridConstructor gridConstructor,IGridFactory gridFactory,
                                                   IGridMetaProvider gridMetaProvider, final Criteria criteria,
                                                   String headerURL, final String dataURL, final boolean dynamicUpdate, final boolean noData)
    {
        final ListGridWithDesc newGrid = gridFactory.createGrid();

//        newGrid.setGroupTitleField("ID_Z");

        gridConstructor.setDataBoundComponent(newGrid);
        gridConstructor.setAddIdDataSource("$" + criteria.getAttribute(TablesTypes.TTYPE));

        Pair<DSCallback, MyDSCallback> dataCallBacks = gridMetaProvider.initGrid2(gridConstructor, headerURL, dataURL, IDataFlowCtrl.DEF_DELAY_DATA_MILLIS);
        GridCtrl ctrl = new GridCtrl(gridConstructor.getAddIdDataSource(), dataCallBacks, criteria, headerURL, dataURL);
        newGrid.setCtrl(ctrl);
        ctrl.updateMetaAndData(dynamicUpdate,noData);

        return newGrid;
    }


    public static class DefaultGridFactory implements IGridFactory
    {

        @Override
        public ListGridWithDesc createGrid()
        {
            final ListGridWithDesc newGrid = _createGrid();
            initGrid(newGrid);
            return newGrid;
        }





        protected ListGridWithDesc _createGrid()
        {
            return new ListGridWithDesc()
            {

                public String getCellCSSText(ListGridRecord record, int rowNum, int colNum)
                {


                    if (record!=null)
                    {
                        String baseStyle = super.getCellCSSText(record, rowNum, colNum);
                        if (baseStyle != null) return baseStyle;
                        String v = record.getAttribute(TablesTypes.ROW_STYLE);
                        if (v != null)
                            return v;
                    }
                    return super.getCellCSSText(record, rowNum, colNum);
                }

                @Override
                protected MenuItem[] getHeaderContextMenuItems(final Integer fieldNum) {
                    final MenuItem[] items = super.getHeaderContextMenuItems(fieldNum);
                    if (!this.isGrouped()) return items;
                    MenuItem customItem = new MenuItem("Настройка агрегатов...");
                    final ListGrid lg = this;
                    customItem.addClickHandler(new ClickHandler() {
                        public void onClick(MenuItemClickEvent event) {
                            //SC.say("Hello Column : " + fieldNum);
                            Window w = AggregatesSummariesBuilderDialog.createAggregatesEditorWindow(lg);
                            w.show();
                        }
                    });
                    MenuItem[] newItems = new MenuItem[items.length + 1];
                    for (int i = 0; i < items.length; i++) {
                        MenuItem item = items[i];
                        newItems[i] = item;
                    }
                    newItems[items.length] = customItem;
                    return newItems;
                }
            };


        }


        protected void initGrid(ListGridWithDesc newGrid)
        {
            newGrid.setWidth100();
            newGrid.setHeight100();
            //   newGrid.setShowGroupTitleColumn(false);

//        newGrid.setShowAllRecords(true);
            newGrid.setShowAllRecords(false);

//        newGrid.setDrawAheadRatio(2);
            //newGrid.setDrawAllMaxCells();


            newGrid.setHeaderHeight(35);
            newGrid.setWrapCells(true);
            newGrid.setCellHeight(35);

            newGrid.setCanAcceptDrop(true);
            newGrid.setCanAcceptDroppedRecords(true);


            newGrid.setCanMultiGroup(true);
            newGrid.setCanMultiSort(true);


//            newGrid.setCanAddFormulaFields(true);
//            newGrid.setCanAddSummaryFields(true);
        }

    }



//        final Pair<DSCallback, MyDSCallback> dataCallBack = gridMetaProvider.initGrid2(gridConstructor, headerURL, dataURL, 3000);
//
//        //setDropHandler(portlet, newGrid);
//
//        criteria.addCriteria(TablesTypes.ID_TM, dataCallBack.second.getTimeStamp());
//        criteria.addCriteria(TablesTypes.ID_TN, dataCallBack.second.getTimeStampN());
//        {
//            DSRequest request = new DSRequest();
//            request.setShowPrompt(false);
//
//            String idHeader = headerURL.replace(".", "_");
//            idHeader = idHeader.replace("/", "$");
//
//            DataSource.get(idHeader).fetchData
//            (
//                    criteria, //Среди прочих параметров передается идентифкатор таблицы
//                    new DSCallback()
//                    {
//                        @Override
//                        public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
//                        {
//                            dataCallBack.first.execute(dsResponse,data,dsRequest);
//                            if (!noData)
//                                startDataUpdater(dataURL, dataCallBack.second, criteria, dynamicUpdate);
//                        }
//                    },
//                    request
//            );
//        }


//    public static void startDataUpdater(final String dataURL, final MyDSCallback dataCallBack, final Criteria criteria, boolean dynamicUpdate) {
//
//        Timer t=new Timer()
//        {
//            @Override
//            public void run()
//            {
//
//                DSRequest request = new DSRequest();
//                request.setShowPrompt(false);
//
//                String dataId = dataURL.replace(".", "_");
//                dataId = dataId.replace("/", "$");
//                criteria.addCriteria(TablesTypes.ID_REQN, String.valueOf(dataCallBack.getSrvCnt()));
//
////                criteria.addCriteria(TablesTypes.FILTERDATAEXPR," DOR_KOD=28 ");
//
//                String tblId = dataCallBack.getTblId();
//                if (tblId!=null)
//                    criteria.addCriteria(TablesTypes.TBLID,tblId);
//
//                DataSource.get(dataId).fetchData
//                (
//                        criteria, //Среди прочих параметров передается идентифкатор таблицы
//                        dataCallBack,
//                        request
//                );
//
//            }
//        };
//        if (dynamicUpdate)
//            dataCallBack.setTimer(t);
//        t.schedule(2000);
//    }

}

