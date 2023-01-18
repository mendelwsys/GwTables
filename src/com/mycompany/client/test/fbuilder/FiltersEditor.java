package com.mycompany.client.test.fbuilder;

import com.mycompany.client.GridCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.LentaGridConstructor;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.GridMetaProviderBase;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.*;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 10.03.15
 * Time: 11:37
 * показывает список фильтров
 */
public class FiltersEditor implements Runnable
{

    public Window testFiltersTemplate()
    {
        final Window templateWin = OptionsViewers.createEmptyWindow("Шаблоны фильтров");

        templateWin.setIsModal(false);
//        templateWin.setShowModalMask(false);

        templateWin.setAutoSize(true);
        templateWin.addItem(getFilterViewerGrid());
        return templateWin;
    }

    @Override
    public void run() {

        final HLayout mainLayout = new HLayout();
        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);

        Window wnd = new FunctionEditor().testFunctionTemplate();
        mainLayout.addChild(wnd);
        mainLayout.draw();
    }


    public String getDataURL() {
        return "transport/tdata2";
    }

    public String getHeaderURL() {
        return "theadDesc.jsp";
    }

    public void initFilterAndOperate(final FilterDet filterDet, final PostponeOperationProvider.IPostponeOperation operation)
    {
        String tblType=filterDet.getTblType();
        final Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);

        GridMetaProviderBase gridMetaProvider= new GridMetaProviderBase();

        final String headerURL = getHeaderURL();
        final String dataURL = getDataURL();

        ListGridWithDesc gridWithDesc = new ListGridWithDesc();

        final BGridConstructor[] gridConstructor = new BGridConstructor[1];
        if (tblType.equals(TablesTypes.LENTA))
        {
            gridConstructor[0]=new LentaGridConstructor(new HashMap<String, List<ListGridField>>());
            gridConstructor[0].setDataBoundComponent(gridWithDesc);
        }
        else
            gridConstructor[0]=new BGridConstructor(gridWithDesc);

        gridConstructor[0].setAddIdDataSource("$" + criteria.getAttribute(TablesTypes.TTYPE));

        DSCallback metaDataUpdater = gridMetaProvider.initMetaDataUpdater(gridConstructor[0], headerURL, dataURL);

        final GridCtrl ctrl = new GridCtrl(gridConstructor[0].getAddIdDataSource(), new Pair<DSCallback,MyDSCallback>(metaDataUpdater,
                new MyDSCallback()
         {
            @Override
            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
            }
        }), criteria, headerURL, dataURL);

        ctrl.updateMeta(null);

        new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation(){

            @Override
            public boolean operate()
            {
                if (gridConstructor[0].isMetaWasSet())
                {
                    ListGridWithDesc grid = gridConstructor[0].getGrid();
                    filterDet.setFilterDS(grid.getFilterDS());
                    filterDet.setFieldMetaDS(grid.getFieldsMetaDS());
                    operation.operate();

                    return true;
                }
                return false;
            }
        });

    }






    DataSource dataSource = new DataSource();
    ListGrid grid;

    private ListGridRecord editRecord;
    private HLayout rollOverCanvas;

    public ListGrid getFilterViewerGrid()
    {
        dataSource.setDataProtocol(DSProtocol.POSTPARAMS);
        dataSource.setDataFormat(DSDataFormat.JSON);
        dataSource.setDataURL("/tools/filters");

        //dataSource.setClientOnly(true);//Этот флаг заставляет источник данных запросить данные только одни раз, все редактирование происходит на клиенте (И это правильно!!!)

        DataSourceIntegerField idField = new DataSourceIntegerField(TablesTypes.KEY_FNAME, "Идентификатор");
        idField.setPrimaryKey(true);
        idField.setHidden(true);

        DataSourceTextField nameField = new DataSourceTextField("name", "Имя фильтра");
        DataSourceTextField table = new DataSourceTextField("table", "Таблица");
        table.setValueMap(KnownTables.getTableMap());


        DataSourceTextField criteriaField = new DataSourceTextField("criteria", "Критерий");
        criteriaField.setHidden(true);
        dataSource.setFields(idField,nameField,table, criteriaField);

        grid = new ListGrid()
        {
            public boolean canEditCell(int rowNum, int colNum)
            {
                if (colNum==1)
                {
                    Record rc=this.getRecord(rowNum);
                    String filterStr=rc.getAttribute("criteria");
                    return filterStr==null || filterStr.length()==0;
                }
                return true;
            }

            protected Canvas getRollOverCanvas(Integer rowNum, Integer colNum)
            {

                editRecord = this.getRecord(rowNum);

                if(rollOverCanvas == null)
                {
                    rollOverCanvas = new HLayout(3);
                    rollOverCanvas.setSnapTo("TR");
                    rollOverCanvas.setWidth(50);
                    rollOverCanvas.setHeight(22);

                    ImgButton editImg = new ImgButton();
                    editImg.setShowDown(false);
                    editImg.setShowRollOver(false);
                    editImg.setLayoutAlign(Alignment.CENTER);
                    editImg.setSrc("fedit/comment_edit.png");
                    editImg.setPrompt("Редактировать фильтр");
                    editImg.setHeight(16);
                    editImg.setWidth(16);
                    editImg.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event)
                        {

                            final String table = editRecord.getAttribute("table");
                            if (table==null)
                                SC.warn("Выбрете таблицу для фильтра");
                            else
                            {
                                final FilterDet filterDet = new FilterDet(table,KnownTables.getTableMap().get(table),editRecord.getAttribute("criteria"),editRecord.getAttribute("name"))
                                {
                                    public void setCriteria(Criteria criteria) {
                                        super.setCriteria(criteria);
                                        editRecord.setAttribute("criteria",((AdvancedCriteria)criteria).toJSON());
                                    }

                                    public void setName(String name)
                                    {
                                        super.setName(name);
                                        editRecord.setAttribute("name",name);
                                    }


                                };
                                initFilterAndOperate(filterDet, new PostponeOperationProvider.IPostponeOperation()
                                {
                                    @Override
                                    public boolean operate() {
                                        Window wnd = EditFilter.createViewFilterOptions(filterDet);
                                        wnd.show();
                                        return true;

                                    }
                                });
                            }
                        }
                    });

                    ImgButton addImg = new ImgButton();
                    addImg.setShowDown(false);
                    addImg.setShowRollOver(false);
                    addImg.setLayoutAlign(Alignment.CENTER);
                    addImg.setSrc("fedit/add.png");
                    addImg.setPrompt("Добавить фильтр");
                    addImg.setHeight(16);
                    addImg.setWidth(16);
                    addImg.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event)
                        {

                            final Record newRecord = new Record();

                            newRecord.setAttribute(TablesTypes.KEY_FNAME, -1);
                            newRecord.setAttribute("name", "Новый фильтр");
                            grid.addData(newRecord);
                        }
                    });


                    ImgButton delImg = new ImgButton();
                    delImg.setShowDown(false);
                    delImg.setShowRollOver(false);
                    delImg.setLayoutAlign(Alignment.CENTER);
                    delImg.setSrc("fedit/delete.png");
                    delImg.setPrompt("Удалить фильтр");
                    delImg.setHeight(16);
                    delImg.setWidth(16);
                    delImg.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event)
                        {
                            editRecord.setAttribute("remove",1);
                            grid.removeData(editRecord);
                        }
                    });

                    rollOverCanvas.addMember(editImg);
                    rollOverCanvas.addMember(addImg);
                    rollOverCanvas.addMember(delImg);
                }
                return rollOverCanvas;
            }
        };

        grid.setShowRollOverCanvas(true);

        grid.setDataSource(dataSource);
        grid.setWidth100();
        grid.setHeight(150);
        grid.setAutoFetchData(true);
        grid.setCanResizeFields(true);


        //grid.setCanRemoveRecords(true);




        grid.setCanEdit(true);
        grid.setEditEvent(ListGridEditEvent.CLICK);
        grid.setEditByCell(true);

        return grid;
    }
}
