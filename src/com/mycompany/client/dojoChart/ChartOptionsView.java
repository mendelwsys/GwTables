package com.mycompany.client.dojoChart;

import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.common.analit2.NNode2;
import com.mycompany.common.analit2.UtilsData;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 05.02.15
 * Time: 16:09
 * Утилиты для иницциализации окна графиков
 */
public class ChartOptionsView
{





    public static Window createOptionView(final CalcManager manager, final BaseDOJOChart dojoChartPane,final IChartCtrl chartCtrl)
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

        final SelectItem selectChart = new SelectItem();
        selectChart.setTitle("Тип диаграммы");
        final LinkedHashMap typeMap = new LinkedHashMap();

        ChartType[] allTypes = ChartType.getAllTypes();
        for (ChartType allType : allTypes)
            typeMap.put(allType,ChartType.getRusVal(allType));
        selectChart.setValueMap(typeMap);





        final DescExt descExt=manager.getIAnalisysDescExt();
        final Map<Integer, String> num2key = descExt.getNumber2Key();
        final Map<String, NNode2> key2Node = descExt.getKey2NNode();

        String def_key=null;
        String def_title=null;
        for (Integer num : num2key.keySet())
        {
            String key=num2key.get(num);

            if (def_key==null)
                def_key=key;


            NNode2 node2=key2Node.get(key);
            final LinkedList<String> names = new LinkedList<String>();
            UtilsData.getValuesByNodeTree(node2, names);
            fieldMap.put(key,UtilsData.getNameByList(names,"->"));
        }

        selectField.setValueMap(fieldMap);

        if (def_key!=null)
            def_title=getShortTitle(def_key, fieldMap);


        try {
            if (dojoChartPane!=null)
            {
                BaseChartWrapper wrapper = manager.getChartWrapper(dojoChartPane.getGraphId());
                if (wrapper!=null)
                {
                    String key=num2key.get(Integer.parseInt(((CalcManager.ChartWrapper)wrapper).getColName()));
                    textName.setValue(wrapper.getTitle());
                    def_key=key;
                    selectChart.setDefaultValue(wrapper.getChart().getChartType());
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

        if (def_key!=null)
            selectField.setDefaultValue(def_key);


        final IButton applyButton = new IButton(AppConst.APPLY_BUTTON_FILTER_OPTIONS);
        applyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                ChartType chartType= (ChartType) selectChart.getValue();
                Object select_value=selectField.getValue();

                String title;
                {
                    final Object value = textName.getValue();
                    if (value!=null)
                        title=value.toString();
                    else
                        title = getShortTitle(select_value, fieldMap);
                }

                if (select_value!=null && chartType!=null)
                {
                    chartCtrl.apply(chartType, select_value, title);
                    winModal.destroy();
                }
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


        //selectItem.;
        form.setFields(selectField,textName,selectChart);


        winModal.addItem(form);
        winModal.addItem(hLayout);


            return winModal;
        }


    private static String getShortTitle(Object select_value, LinkedHashMap<String, String> valueMap) {
        String title;
        final String long_title = valueMap.get(select_value);
        int index=long_title.indexOf("->");
        if (index>0)
            title=long_title.substring(0,index);
        else
            title=long_title;
        return title;
    }


}
