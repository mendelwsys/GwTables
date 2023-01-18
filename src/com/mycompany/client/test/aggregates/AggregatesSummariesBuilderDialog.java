package com.mycompany.client.test.aggregates;

import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.*;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by Anton.Pozdnev on 30.04.2015.
 */
public class AggregatesSummariesBuilderDialog {
    // public static final String letters = "ABCDEFGIJKLMNOPQRSTUVWXYZ";

    public static final String FUNCTION_FIELD = "FUNCTION";
    public static final String FORMAT_FIELD = "FORMAT";
    public static final String FIELD = "FIELD";
    public static final String FIELD_NAME = "FIELD_NAME";
    public static final String FIELD_TYPE = "FIELD_TYPE";



    public static Window createAggregatesEditorWindow(final ListGrid lg) {
        final Window winModal = OptionsViewers.createEmptyWindow(AppConst.AGGREGATES_OPTIONS_HEADER);
        winModal.setCanDragResize(true);
        winModal.setWidth(490);
        winModal.setHeight(400);
        final ListGrid aggregates = new ListGrid() {
            @Override
            public boolean canEditCell(int rowNum, int colNum) {
                return this.getField(colNum).getName().equalsIgnoreCase(FUNCTION_FIELD) || this.getField(colNum).getName().equalsIgnoreCase(FORMAT_FIELD);
            }
        };
        aggregates.setCanEdit(true);
        ListGridField lgf = new ListGridField(FIELD, "Имя поля");
        ListGridField lgf2 = new ListGridField(FUNCTION_FIELD, "Агрегат");
        // ListGridField lgf3 = new ListGridField("KEY", "Ключ поля");
        ListGridField lgf4 = new ListGridField(FIELD_TYPE, "Тип поля");
        ListGridField lgf5 = new ListGridField(FIELD_NAME, "Название поля");
        ListGridField lgf6 = new ListGridField(FORMAT_FIELD, "Формат");
        lgf5.setHidden(true);
        aggregates.setEditByCell(true);
        aggregates.setHeight100();
        aggregates.setWidth100();
        aggregates.setFields(new ListGridField[]{lgf, lgf4, lgf2, lgf6, lgf5});
        aggregates.setEditorCustomizer(new ListGridEditorCustomizer() {
            public FormItem getEditor(ListGridEditorContext context) {
                ListGridField field = context.getEditField();
                if (field.getName().equals(FUNCTION_FIELD)) {
                    SelectItem selectItemMultipleGrid = new SelectItem();
                    selectItemMultipleGrid.setShowTitle(false);
                    LinkedHashSet<SummaryFunctionType> set = FieldTypeToFunctionMapper.getFunctionForType(ListGridFieldType.valueOf(context.getEditedRecord().getAttribute(FIELD_TYPE).toUpperCase()));
                    SummaryFunctionType[] setArray = set.toArray(new SummaryFunctionType[set.size()]);
                    int setSize = set.size() + 1;
                    String[] values = new String[setSize];
                    for (int i = 0; i < setSize; i++) {
                        if (i == 0) {
                            values[i] = "";
                            continue;
                        }
                        values[i] = AggregatesUtils.getSummaryFunction(setArray[i - 1].getValue().toUpperCase());
                    }
                    selectItemMultipleGrid.setValueMap(values);
                    return selectItemMultipleGrid;
                }
                return context.getDefaultProperties();
            }
        });
        fillGridWithData(lg, aggregates);
        winModal.addItem(aggregates);
        final IButton saveButton = new IButton(AppConst.SAVE_BUTTON_OPTIONS);
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Record[] recs = aggregates.getRecords();
                Map<String, String> hm = new HashMap<String, String>();
                ListGridField[] lgfs = lg.getAllFields();
                for (int i = 0, recsSize = recs.length; i < recsSize; i++) {
                    SummaryFunctionType sft = AggregatesUtils.getSummaryFunctionType(recs[i].getAttribute(FUNCTION_FIELD));
                    String fieldName = recs[i].getAttribute(FIELD_NAME);
                    hm.put(fieldName, sft == null ? null : sft.getValue());
                    for (int j = 0, lgfsLength = lgfs.length; j < lgfsLength; j++) {

                        if (lgfs[j].getName().equalsIgnoreCase(fieldName)) {
                            lgfs[j].setAttribute(TablesTypes.AGGREGATE_FIELD_FORMAT_KEY, recs[i].getAttribute(FORMAT_FIELD));
                            break;
                        }
                    }
                }
                AggregatesUtils.setAggregates(lg, hm);
                winModal.destroy();
            }
        });
        saveButton.setMargin(2);
        final IButton cancelButton = new IButton(AppConst.CANCEL_BUTTON_OPTIONS);
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                winModal.destroy();
            }
        });
        cancelButton.setMargin(2);
        HLayout hLayout = new HLayout();
        hLayout.addMembers(saveButton, cancelButton);
        hLayout.setAlign(Alignment.CENTER);
        hLayout.setMinHeight(100);
        hLayout.setMaxHeight(100);
        hLayout.setAutoHeight();
        winModal.addItem(hLayout);
        return winModal;
    }

    private static void fillGridWithData(ListGrid lg, ListGrid aggregates) {
        ListGridField[] fields = lg.getAllFields();
        // final int length = letters.length();
        for (int i = 0, listGridFieldsLength = fields.length; i < listGridFieldsLength; i++) {
            ListGridField field = fields[i];
            if (!field.getName().trim().equalsIgnoreCase("groupTitle")) {
                Record r = new ListGridRecord();
                //  if (i < length)
                //      r.setAttribute("KEY", "" + letters.charAt(i));
                //   else
                //      r.setAttribute("KEY", "" + letters.charAt(i % length) + (i / length));
                r.setAttribute(FIELD, field.getTitle());
                r.setAttribute(FIELD_TYPE, field.getType());
                String function = field.getAttribute(TablesTypes.AGGREGATE_FUNCTIONS_TYPES_KEY);
                if (function == null) function = field.getAttribute(TablesTypes.AGGREGATE_CUSTOM_FUNCTIONS_KEY);
                r.setAttribute(FUNCTION_FIELD, function == null ? "" : AggregatesUtils.getSummaryFunction(function));
                r.setAttribute(FIELD_NAME, field.getName());
                r.setAttribute(FORMAT_FIELD, field.getAttribute(TablesTypes.AGGREGATE_FIELD_FORMAT_KEY) == null ? "" : field.getAttribute(TablesTypes.AGGREGATE_FIELD_FORMAT_KEY));
                aggregates.addData(r);
            }
        }
    }
}
