package com.mycompany.client.test.fbuilder;

import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceEnumField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.*;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 10.03.15
 * Time: 19:08
 * To change this template use File | Settings | File Templates.
 */
public class FunctionEditor
{

    DataSource dataSource = new DataSource();
    ListGrid grid;

    private ListGridRecord editRecord;
    private HLayout rollOverCanvas;


    public Window testFunctionTemplate()
    {
        final Window templateWin = OptionsViewers.createEmptyWindow("Шаблоны функций");

        templateWin.setIsModal(false);
//        templateWin.setShowModalMask(false);

        templateWin.setAutoSize(true);
        templateWin.addItem(getFilterViewerGrid(null));
        return templateWin;
    }



    public ListGrid getFilterViewerGrid(DataSource filters)
    {
        dataSource.setDataProtocol(DSProtocol.POSTPARAMS);
        dataSource.setDataFormat(DSDataFormat.JSON);
        dataSource.setDataURL("/tools/filters?storage=function");

        //dataSource.setClientOnly(true);//Этот флаг заставляет источник данных запросить данные только одни раз, все редактирование происходит на клиенте (И это правильно!!!)

        DataSourceIntegerField idField = new DataSourceIntegerField(TablesTypes.KEY_FNAME, "Идентификатор");
        idField.setPrimaryKey(true);
        idField.setHidden(true);

        DataSourceTextField nameField = new DataSourceTextField("name", "Имя функции");
        DataSourceTextField filter = new DataSourceTextField("filter", "Фильтр");
        filter.setHidden(true);

        //table.setValueMap(tableNames);//TODO Инициализация функций из датасорцов

        DataSourceEnumField criteriaField = new DataSourceEnumField("aggregate", "Функция");
//        criteriaField.set

//        Map<Aggregates,String> hm = new HashMap<Aggregates,String>();
//        hm.put(Aggregates.COUNT,"Кол-во");
//        hm.put(Aggregates.SUM,"Сумма");
//        hm.put(Aggregates.MIN,"Мин");
//        hm.put(Aggregates.MAX,"Макс");
//        hm.put(Aggregates.ARG,"Среднее");
//        criteriaField.setValueMap(hm);

        DataSourceTextField colField = new DataSourceTextField("colName", "Колонка");//TODO А здесь должен быть перечень вычислимых колонок


        dataSource.setFields(idField,nameField,filter, criteriaField,colField);

        grid = new ListGrid()
        {
            protected Canvas getRollOverCanvas(Integer rowNum, Integer colNum)
            {

                editRecord = this.getRecord(rowNum);

                if(rollOverCanvas == null)
                {
                    rollOverCanvas = new HLayout(3);
                    rollOverCanvas.setSnapTo("TR");
                    rollOverCanvas.setWidth(50);
                    rollOverCanvas.setHeight(22);

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
                            newRecord.setAttribute("name", "Новая функция");
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
