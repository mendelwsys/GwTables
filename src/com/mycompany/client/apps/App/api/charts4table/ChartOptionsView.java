package com.mycompany.client.apps.App.api.charts4table;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.dojoChart.*;
import com.mycompany.client.test.aggregates.AggregatesUtils;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.04.15
 * Time: 16:09
 * Утилиты для иницциализации окна графиков над таблицей
 */
public class ChartOptionsView
{
    public static Window createOptionView(String def_title,final BaseCalcManager manager,final BaseDOJOChart dojoChartPane,final IChartCtrl chartCtrl,final ListGridWithDesc newGrid)
    {

        final Window winModal = OptionsViewers.createEmptyWindow(AppConst.CUSTOM_FILTER_OPTIONS_HEADER);
        winModal.setAutoSize(true);

        DynamicForm form = OptionsViewers.createEmptyForm();
        form.setNumCols(4);
        form.setPadding(10);

        final TextItem textName = new TextItem();
        textName.setTitle("Заголовок графика");

        final SelectItem selectField = new SelectItem();
        selectField.setTitle("Поле для отображения");
        final LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();


        ListGridField[] flds = newGrid.getFields();
        for (ListGridField fld : flds)
        {
            String functionName = fld.getAttribute("originalFunction");
            if (functionName==null)
                functionName=fld.getAttribute("summaryFunction");
            if (functionName!=null)
            {  //Ограниечение по типу поля или по применяемой функции
                if (
                        fld.getType().equals(ListGridFieldType.FLOAT) || fld.getType().equals(ListGridFieldType.INTEGER)
                        ||
                        SummaryFunctionType.COUNT.getValue().equals(functionName)
                   )
                fieldMap.put(fld.getName(),fld.getTitle()+" ("+ AggregatesUtils.getSummaryFunction(functionName)+") ");
            }
        }
        selectField.setValueMap(fieldMap);
        selectField.setAllowEmptyValue(true);



        final SelectItem selectChart = new SelectItem();
        selectChart.setTitle("Тип диаграммы");
        final LinkedHashMap typeMap = new LinkedHashMap();

        ChartType[] allTypes = ChartType.getAllTypes();
        for (ChartType allType : allTypes)
            typeMap.put(allType,ChartType.getRusVal(allType));
        selectChart.setValueMap(typeMap);

        try
        {
            if (dojoChartPane!=null)
            {
                BaseChartWrapper wrapper = manager.getChartWrapper(dojoChartPane.getGraphId());
                if (wrapper!=null)
                {
                    textName.setValue(wrapper.getTitle());
                    selectChart.setDefaultValue(wrapper.getChart().getChartType());

                    ListGridField fld = ((CreateChartByTable.CreateChartByTableManager.ChartWrapperByTable) wrapper).getFld();
                    if (fld!=null)
                        selectField.setDefaultValue(fld.getName());
                }
            }
            else
            {
                if (allTypes!=null && allTypes.length>0)
                    selectChart.setDefaultValue(allTypes[0]);
                if (def_title!=null)
                    textName.setValue(def_title);
            }
        } catch (NumberFormatException e) {
            //
        }

        final IButton applyButton = new IButton(AppConst.APPLY_BUTTON_FILTER_OPTIONS);
        applyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                ChartType chartType= (ChartType) selectChart.getValue();
                Object select_value=selectField.getValue();

                String title="";
                {
                    final Object value = textName.getValue();
                    if (value!=null)
                        title=value.toString();
                }
                    chartCtrl.apply(chartType,select_value,title);
                    winModal.destroy();
            }
        });


        final IButton cancelButton = new IButton(AppConst.CANCEL_BUTTON_FILTER_OPTIONS);
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                winModal.destroy();
            }
        });


        HLayout hLayout= new HLayout();


        hLayout.addMembers(applyButton, cancelButton);


        hLayout.setAlign(Alignment.RIGHT);
        hLayout.setMargin(5);


        if (fieldMap.size()>0)
            form.setFields(selectField,textName,selectChart);
        else
            form.setFields(textName,selectChart);


        winModal.addItem(form);
        winModal.addItem(hLayout);


            return winModal;
        }

}
