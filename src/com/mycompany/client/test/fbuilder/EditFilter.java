package com.mycompany.client.test.fbuilder;

import com.mycompany.client.apps.App.*;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.LogicalOperator;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.03.15
 * Time: 16:43
 * Редактор фильтра
 */
public class EditFilter
{

    public static Window createViewFilterOptions
            (
                    final FilterDet filterDet
            )
    {


        final Window winModal = OptionsViewers.createEmptyWindow(AppConst.CUSTOM_FILTER_OPTIONS_HEADER);
        winModal.setAutoSize(true);

        DynamicForm form = OptionsViewers.createEmptyForm();
        form.setNumCols(4);
        form.setPadding(10);

        final TextItem textName = new TextItem();
        //textName.setTitle("Название фильтра");
        textName.setTitle(filterDet.getViewTableName());
        textName.setValue(filterDet.getName());

//        final TextItem tableName = new TextItem();
//        tableName.setDisabled(true);
//        tableName.setTitle("Название таблицы");
//        tableName.setValue(filterDet.getViewTableName());


        final FilterBuilder filterBuilder = new FilterBuilder();


        filterBuilder.setAllowEmpty(false);
        filterBuilder.setFieldDataSource(filterDet.getFieldsMetaDS());
        filterBuilder.setDataSource(filterDet.getFilterDS());
        filterBuilder.clearCriteria();
        Criteria criteria = filterDet.getCriteria();

        if (criteria!=null)
        {
            if (criteria.isAdvanced())
            {

                AdvancedCriteria aCriteria=((AdvancedCriteria) criteria);
                Criterion[] criteria1 = aCriteria.getCriteria();

                for (Criterion criterion : criteria1)
                    filterBuilder.addCriterion(criterion);

                String op = aCriteria.getAttribute("operator");
                if (op!=null) {
                    try {
                        LogicalOperator topOperator = LogicalOperator.valueOf(op.toUpperCase());
                        filterBuilder.setTopOperator(topOperator);
                    } catch (IllegalArgumentException e) {
                        //
                    }
                }
            }
            else
            {
                AdvancedCriteria aCriteria=new AdvancedCriteria();
                aCriteria.addCriteria(criteria);
                Criterion[] criteria1 = aCriteria.getCriteria();

                for (Criterion criterion : criteria1)
                    filterBuilder.addCriterion(criterion);
            }
        }


        final IButton applyButton = new IButton(AppConst.SAVE_BUTTON_FILTER_OPTIONS);
        applyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                filterDet.setCriteria(filterBuilder.getCriteria());
                filterDet.setName(String.valueOf(textName.getValue()));
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


        //form.setFields(textName,tableName);
        form.setFields(textName);

        winModal.addItem(form);
        winModal.addItem(filterBuilder);
        winModal.addItem(hLayout);

        return winModal;
    }



}
