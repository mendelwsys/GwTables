package com.mycompany.client.test.informer;

import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.NSI;
import com.mycompany.client.apps.App.OptionsViewers;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 19.03.15
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
public class SetRoadDlg
{
    public static Window createViewOptionsDlg
            (
                    final DetailViewerInfo viewerInfo
            )
    {


        final Window winModal = OptionsViewers.createEmptyWindow(AppConst.CUSTOM_FILTER_OPTIONS_HEADER);
        winModal.setAutoSize(true);

        winModal.setTitle("Настройки информера");

        DynamicForm form = OptionsViewers.createEmptyForm();
        form.setNumCols(1);
        form.setPadding(10);


        final SelectItem selectRoad = new SelectItem();
        selectRoad.setTitle("Дорога");
        final LinkedHashMap<Integer,String> typeMap = new LinkedHashMap<Integer,String>();
        NSI.fillMapByRoad(typeMap);
        selectRoad.setValueMap(typeMap);

        final Integer dorKod = viewerInfo.getDorKod();
        if (dorKod>0)
            selectRoad.setValue(dorKod);
        else
            selectRoad.setValue(1);

        final CheckboxItem highWarnLevel = new CheckboxItem();
        highWarnLevel.setTitle("Режим оповещений высокого уровня");
        highWarnLevel.setValue(viewerInfo.isHighLevelWarning());
        final IButton applyButton = new IButton(AppConst.SAVE_BUTTON_FILTER_OPTIONS);
        applyButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                Integer select_dor_kod= (Integer) selectRoad.getValue();
                viewerInfo.setDorKod(select_dor_kod);
                viewerInfo.setHighLevelWarning(highWarnLevel.getValueAsBoolean());
                winModal.destroy();
            }
        });

        final IButton cancelButton = new IButton(AppConst.CANCEL_BUTTON_FILTER_OPTIONS);
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                winModal.destroy();
            }
        });

        HLayout hLayout= new HLayout();
        hLayout.addMembers(applyButton,cancelButton);


        hLayout.setAlign(Alignment.RIGHT);
        hLayout.setMargin(5);

        form.setFields(selectRoad, highWarnLevel);
        winModal.addItem(form);
        winModal.addItem(hLayout);

        return winModal;
    }

}
