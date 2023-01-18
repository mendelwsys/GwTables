package com.mycompany.client.test.filters;

import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.smartgwt.client.data.*;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.*;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.12.14
 * Time: 13:26
 * To change this template use File | Settings | File Templates.
 */
public class TestFilterBuilder implements Runnable
{
    @Override
    public void run() {
        ListGridRecord testData[] = new ListGridRecord[200];

        for(int i=0;i<200;i++)
        {
            ListGridRecord record = new ListGridRecord();
            record.setAttribute("name", "field" + i);
            record.setAttribute("title", "Field " + i);
            record.setAttribute("type", "text");
            //record.setAttribute("type", "text");
            testData[i] = record;
        }

        DataSource bigFilterDS = new DataSource();
        bigFilterDS.setClientOnly(true);

        DataSourceTextField nameField = new DataSourceTextField("name");
        DataSourceTextField titleField = new DataSourceTextField("title");
        DataSourceTextField typeField = new DataSourceTextField("type");

        bigFilterDS.setFields(nameField, titleField, typeField);
        bigFilterDS.setTestData(testData);

        FilterBuilder filterBuilder = new FilterBuilder();
        filterBuilder.setFieldDataSource(bigFilterDS);


        AdvancedCriteria criteria = new AdvancedCriteria(OperatorId.AND, new AdvancedCriteria[] {
                new AdvancedCriteria("field2", OperatorId.ISTARTS_WITH, "C"),
                new AdvancedCriteria(OperatorId.OR, new AdvancedCriteria[] {
                    new AdvancedCriteria("field73", OperatorId.NOT_EQUAL_FIELD, "field191"),
                    new AdvancedCriteria("field130", OperatorId.ICONTAINS, "B")
                })
        });

        filterBuilder.setTopOperatorAppearance(TopOperatorAppearance.NONE);


        filterBuilder.setCriteria(criteria);

        SelectItem formItemProperties = new SelectItem();
        formItemProperties.setShowPickerIcon(false);
//        filterBuilder.setFieldPickerProperties(formItemProperties);


        final Window winModal = OptionsViewers.createEmptyWindow(AppConst.CUSTOM_FILTER_OPTIONS_HEADER);
//        winModal.setWidth(460);
        winModal.setAutoSize(true);

//        DynamicForm form = new DynamicForm();
//        form.setHeight100();
//        form.setWidth100();
//        form.setPadding(5);
//        form.setLayoutAlign(VerticalAlignment.BOTTOM);

        final DynamicForm form = new DynamicForm();
//        form.setGroupTitle(null);
//        form.setIsGroup(true);
//        form.setWidth("400");
//        form.setAutoWidth();
//        form.setHeight(180);
        form.setNumCols(4);
//        form.setColWidths(60, "*");
        //form.setBorder("1px solid blue");
        form.setPadding(10);
//        form.setCanDragResize(true);
//        form.setResizeFrom("R");

//        form.setTitleOrientation(TitleOrientation.TOP);
        final TextItem textName = new TextItem();
        textName.setTitle("Название");
        textName.setValue("Фильтр");
//        textName.setWidth(400);


        final TextItem textFolder = new TextItem();
        textFolder.setTitle("Папка");
        textFolder.setValue("Папка");
//        textFolder.setWidth(400);


        final IButton applyButton = new IButton(AppConst.APPLY_BUTTON_FILTER_OPTIONS);
        final IButton saveButton = new IButton(AppConst.SAVE_BUTTON_FILTER_OPTIONS);
        final IButton cancelButton = new IButton(AppConst.CANCEL_BUTTON_FILTER_OPTIONS);

        HLayout hLayout= new HLayout();
        hLayout.addMembers(applyButton,saveButton, cancelButton);


        hLayout.setAlign(Alignment.RIGHT);
        hLayout.setMargin(5);

        form.setFields(textName,textFolder);

        VLayout layout=new VLayout();

        layout.addMember(form);
        layout.addMember(filterBuilder);
        layout.addMember(hLayout);

        winModal.addItem(layout);
        winModal.draw();
    }
}
