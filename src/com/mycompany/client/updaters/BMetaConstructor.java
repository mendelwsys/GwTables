package com.mycompany.client.updaters;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 04.09.14
 * Time: 12:53
 * Апдейтер данных грида
 */
public class BMetaConstructor implements IMetaTableConstructor
{

    protected ListGridField[] fields;
    public void setListGridFields(ListGridField[] fields)
    {
        this.fields=fields;
        setMetaInfo(fields);
    }

    public DataSource getFieldsMetaDS() {
        return fieldMetaDS;
    }

    public DataSource getFilterDS() {
        return filterDS;
    }

    private DataSource fieldMetaDS = new DataSource();
    private DataSource filterDS = new DataSource();



    protected void setMetaInfo(ListGridField[] fields)
    {
        fieldMetaDS = new DataSource();
        fieldMetaDS.setClientOnly(true);

        filterDS = new DataSource();
        filterDS.setClientOnly(true);


        DataSourceTextField nameField = new DataSourceTextField("name");
        DataSourceTextField titleField = new DataSourceTextField("title");
        DataSourceTextField typeField = new DataSourceTextField("type");
//        DataSourceTextField editorType = new DataSourceTextField("editorType");
//        DataSourceField editorProperties = new DataSourceField("editorProperties",FieldType.BINARY);


//        fieldMetaDS.setFields(nameField, titleField, typeField,editorType,editorProperties);

        fieldMetaDS.setFields(nameField, titleField, typeField);

        ListGridRecord fieldMetaData[] = new ListGridRecord[fields.length+2];

        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++)
        {
            ListGridField field = fields[i];
            addField(field, fieldMetaData, i);
        }

        {
            ListGridField field = new ListGridField(TablesTypes.TIMESTAMP_FIELD, "Тек.Время");
            field.setType(ListGridFieldType.DATETIME);
            addField(field, fieldMetaData, fields.length);
        }

        {
            ListGridField field = new ListGridField(TablesTypes.LINKTEXT, "Текст ссылки");
            field.setType(ListGridFieldType.TEXT);
            addField(field, fieldMetaData, fields.length+1);
        }


        fieldMetaDS.setCacheData(fieldMetaData);
    }

    private void addField(ListGridField field, ListGridRecord[] fieldMetaData, int i) {
        final ListGridFieldType type = field.getType();
        if (type!=null)
        {
            ListGridRecord record = new ListGridRecord();
            record.setAttribute("name", field.getName());
            record.setAttribute("title", field.getTitle());

            record.setAttribute("type", type.toString());

//        TODO проверка фильтра (НЕ РАБОТАЕТ !@#$!!!!)
//                if ("ND".equals(field.getName()))
//                {
//                    RelativeDateItem rangeItem = new RelativeDateItem();
//                    record.setAttribute("editorType", rangeItem.getAttribute("editorType"));
//                    record.setAttribute("editorProperties", rangeItem.getConfig());
//                }
//          TODO проверка фильтра  (НЕ РАБОТАЕТ !@#$!!!!)


            fieldMetaData[i] = record;
            DataSourceField dsField = new DataSourceField(field.getName(), FieldType.valueOf(type.name()));
            dsField.setValidOperators(OperatorId.EQUALS,OperatorId.NOT_EQUAL,
                    OperatorId.CONTAINS,OperatorId.ICONTAINS,
                    OperatorId.STARTS_WITH,OperatorId.ISTARTS_WITH,
                    OperatorId.ENDS_WITH,OperatorId.IENDS_WITH,
                    OperatorId.BETWEEN,OperatorId.LESS_THAN,OperatorId.LESS_OR_EQUAL,OperatorId.GREATER_THAN,
                    OperatorId.GREATER_OR_EQUAL,OperatorId.NOT_NULL,OperatorId.NOT,OperatorId.IS_NULL,
                    OperatorId.LESS_OR_EQUAL_FIELD,OperatorId.LESS_THAN_FIELD,
                    OperatorId.EQUALS_FIELD,OperatorId.NOT_EQUAL_FIELD,
                    OperatorId.GREATER_OR_EQUAL_FIELD,OperatorId.GREATER_THAN_FIELD,
                    OperatorId.IN_SET,OperatorId.NOT_IN_SET

            );

    //TODO проверка фильтра (РАБОТАЕТ для любого типа, полезно например для ограничения мно-в ввода)
//                if ("ND".equals(field.getName()))
//                {
//                    DateRangeItem rangeItem = new DateRangeItem();
//                    rangeItem.setWidth("*");

//                    DateRangeItem rangeItem = new DateRangeItem();
//                    rangeItem.setWidth("*");

//                    DateItem rangeItem = new DateItem();

//                    MiniDateRangeItem rangeItem = new MiniDateRangeItem();
//                     RelativeDateItem rangeItem = new RelativeDateItem();

//                     ComboBoxItem rangeItem = new ComboBoxItem();
//                     rangeItem.setValueMap("Cat", "Dog", "Giraffe", "Goat", "Marmoset", "Mouse");
//                    rangeItem.setShowTitle(false);
//                    dsField.setEditorProperties(rangeItem);
                //dsField.getAttributes();
//                }
//TODO проверка фильтра (РАБОТАЕТ)


            filterDS.addField(dsField);
        }
    }


    protected void setOptions(Record gridOptions)
    {
    }

    public ListGridField[] getAllFields()
    {
        return fields;
    }

    public boolean isMetaWasSet() {
        return metaWasSet;
    }

    public void setMetaWasSet(boolean metaWasSet) {
        this.metaWasSet = metaWasSet;
    }

    boolean metaWasSet=false;


    public void setHeaderGrid(Record[] gridOptions) throws SetGridException
    {
        setOptions(gridOptions[0]);

        ListGridField[] fields = extractFields(gridOptions[0]);

        setListGridFields(fields);
    }


    protected ListGridField[] extractFields(Record gridOptions)
    {
        Record[] records = gridOptions.getAttributeAsRecordArray("chs");

        ListGridField[] fields = new ListGridField[records.length];

        for (int i = 0, recordsLength = records.length; i < recordsLength; i++)
        {
            Record record = records[i];
            //Формируем заголовки
            ListGridField field = new ListGridField();
//            {
//                public String getTarget()
//                {
//                    return super.getTarget();
//                }
//            };
            field.setName(record.getAttribute("name"));

            field.setTitle(record.getAttribute("title"));
            ListGridFieldType type = ListGridFieldType.valueOf(record.getAttribute("type"));
            field.setType(type);
            field.setHidden(!record.getAttributeAsBoolean("visible"));
            field.setAutoFitWidth(record.getAttributeAsBoolean("autofit"));

            String salignment = record.getAttribute("alignment");
            if (salignment != null && salignment.length() > 0)
                field.setAlign(Alignment.valueOf(salignment));

            if (type.equals(ListGridFieldType.LINK))
            {
                field.setLinkText(record.getAttribute(TablesTypes.LINKTEXT));
                if (!App01.isDefOpenMode())
                    field.setTarget("javascript");
            }
            fields[i] = field;
        }
        return fields;
    }

    protected String addIdDataSource;
    @Override
    public void setAddIdDataSource(String addDataUrlId)
    {
        this.addIdDataSource =addDataUrlId;
    }

    @Override
    public String getAddIdDataSource()
    {
        return this.addIdDataSource;
    }
}
