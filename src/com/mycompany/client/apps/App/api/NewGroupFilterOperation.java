package com.mycompany.client.apps.App.api;

import com.mycompany.client.*;
import com.mycompany.client.apps.App.*;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.data.*;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.*;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 17.07.15
 * Time: 17:20
 * Группировочный фильтр
 */
public class NewGroupFilterOperation extends NewFilterOperation
{
    static final public String GRP_FILTER="GRP_FILTER";

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        NewGroupFilterOperation operation = new NewGroupFilterOperation();
        operation.setJustInit(false);
        return operation;


    }

    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        NewFilterOperation filterOperation = (NewFilterOperation) super.createOperation(descOperation, operation);
        RepeatFilterByCriteria filterByCriteria= (RepeatFilterByCriteria) filterOperation.getFilterByCriteria();
        String cliFilterString= (String) descOperation.get(GRP_FILTER);
        filterByCriteria.setGrpCriteria(new AdvancedCriteria(JSOHelper.eval(cliFilterString)));
        return filterOperation;
    }


    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);
        RepeatFilterByCriteria filterByCriteria= (RepeatFilterByCriteria)getFilterByCriteria();
        Criteria cr = filterByCriteria.getGrpCriteria();
        descOperation.put(GRP_FILTER, JSON.encode(cr.getJsObj()));
        return oldDescOperation=descOperation;
    }


    public Object copy()
    {
        NewGroupFilterOperation operation= new NewGroupFilterOperation();
        Object o = _copyOperation(operation);

        RepeatFilterByCriteria localFilter = (RepeatFilterByCriteria) getFilterByCriteria();
        RepeatFilterByCriteria filterByCriteria = (RepeatFilterByCriteria) operation.getFilterByCriteria();
        filterByCriteria.setGrpCriteria(localFilter.getGrpCriteria());

        return o;
    }

    protected CliFilterByCriteria createFilter(DataSource filterDS, Criteria criteria)
    {
        return new RepeatFilterByCriteria(filterDS, criteria);
    }



    protected HeaderControl.HeaderIcon getHeaderIcon() {
        return HeaderControl.CASCADE;
    }


    public NewGroupFilterOperation(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected NewGroupFilterOperation()
    {
        super();
    }

    protected FilterOptionsView.IFilterCtrl createIFilterCtrl()
    {
        if (iFilterCtrl==null)
            iFilterCtrl = new IFilterCtrlImpl2();
        return iFilterCtrl;
    }

    public class  IFilterCtrlImpl2 extends IFilterCtrlImpl
    {
        protected void _apply(ListGridWithDesc gridWithDesc, Map<String, Object> params, FilterBuilder _filterBuilder)
        {
             FilterBuilder grpfilterBuilder= (FilterBuilder) params.get(GRP_FILTER);
             RepeatFilterByCriteria filter = (RepeatFilterByCriteria) getFilterByCriteria();
             filter.setGrpCriteria(grpfilterBuilder.getCriteria());
             super._apply(gridWithDesc, params, _filterBuilder);
        }

        protected void _save(ListGridWithDesc gridWithDesc, Map<String, Object> params, FilterBuilder _filterBuilder)
        {
            FilterBuilder grpfilterBuilder= (FilterBuilder) params.get(GRP_FILTER);
            RepeatFilterByCriteria filter = (RepeatFilterByCriteria) getFilterByCriteria();
            filter.setGrpCriteria(grpfilterBuilder.getCriteria());
            super._save(gridWithDesc, params, _filterBuilder);
        }

    }


    protected FilterOptionsView getOptionView()
    {
        return new FilterOptionsView()
        {
            protected FilterBuilder createFilterBuilder(ListGridWithDesc gridWithDesc, NewFilterOperation filterOperation)
            {
                final FilterBuilder filterBuilder = new FilterBuilder();
                filterBuilder.setTopOperatorAppearance(TopOperatorAppearance.NONE);
                filterBuilder.setShowSubClauseButton(false);
                filterBuilder.setAllowEmpty(false);


                DataSource fieldsMetaDS = gridWithDesc.getFieldsMetaDS();
                filterBuilder.setFieldDataSource(fieldsMetaDS);
                Record[] recs = fieldsMetaDS.getCacheData();

                DataSource filterDS2 = new DataSource();
                filterDS2.setClientOnly(true);


                for (Record record : recs)
                {
                    DataSourceField fld2 = new DataSourceField(record.getAttribute("name"), null, record.getAttribute("title"));
                    filterDS2.addField(fld2);
                    fld2.setValidOperators(OperatorId.EQUALS_FIELD);
                }
                filterBuilder.setDataSource(filterDS2);
                filterBuilder.clearCriteria();
                CliFilterByCriteria filterByCriteria1 = filterOperation.getFilterByCriteria();
                initFilterBuilder(filterByCriteria1.getCriteria(), filterBuilder);


                return filterBuilder;
            }

            protected Map<String, Object> getParamsByForm(final DynamicForm form,final Window winModal)
            {
                Map<String, Object> paramsByForm = super.getParamsByForm(form, winModal);
                paramsByForm.put(GRP_FILTER,winModal.getItems()[1]);
                return paramsByForm;
            }

            public  Window createViewFilterOptions
                    (
                            final TreeGrid windowsTree,
                            final ListGridWithDesc gridWithDesc,
                            final NewFilterOperation filterOperation,
                            final IFilterCtrl ctrl
                    )
            {

                final Window winModal = OptionsViewers.createEmptyWindow(AppConst.CUSTOM_FILTER_OPTIONS_HEADER);
                winModal.setAutoSize(true);


                final DynamicForm form = OptionsViewers.createEmptyForm();
                form.setNumCols(4);
                form.setPadding(10);
                final List<FormItem> items = createFormItems(windowsTree, gridWithDesc, filterOperation);
                form.setFields(items.toArray(new FormItem[items.size()]));


                final FilterBuilder grpFilterBuilder = new FilterBuilder();

                {
                    createMeta4GroupFunctions();
                    grpFilterBuilder.setTopOperatorAppearance(TopOperatorAppearance.NONE);
                    grpFilterBuilder.setShowSubClauseButton(false);
                    grpFilterBuilder.setShowAddButton(false);
                    grpFilterBuilder.setAllowEmpty(false);
                    grpFilterBuilder.setFieldDataSource(fieldMetaDS);
                    grpFilterBuilder.setDataSource(filterDS);
                    grpFilterBuilder.clearCriteria();

                    Criteria grpСriteria=((RepeatFilterByCriteria)(filterOperation.getFilterByCriteria())).getGrpCriteria();
                    initFilterBuilder(grpСriteria, grpFilterBuilder);
                }



                final FilterBuilder filterBuilder = createFilterBuilder(gridWithDesc, filterOperation);
                //todo Установить ограничения на группировочную функцию
                //filterBuilder.set

                final List<IButton> buttons = getButtons(gridWithDesc, ctrl, winModal, form, filterBuilder);

                final HLayout hLayout= new HLayout();
                hLayout.addMembers(buttons.toArray(new IButton[buttons.size()]));
                hLayout.setAlign(Alignment.RIGHT);
                hLayout.setMargin(5);

                winModal.addItem(form);
                winModal.addItem(grpFilterBuilder);
                winModal.addItem(filterBuilder);
                winModal.addItem(hLayout);

                return winModal;
            }

        };
    }

    private static DataSource fieldMetaDS;
    private static DataSource filterDS;

    private static void createMeta4GroupFunctions()
    {
        if (fieldMetaDS!=null && filterDS!=null)
            return;
        createMeta4GroupFunctions( new ListGridField[]{
                new ListGridField("COUNT", "Кол-во"){{setType(ListGridFieldType.INTEGER);}}
                ,
//                new ListGridField("MAX", "Макс"){{setType(ListGridFieldType.FLOAT);}}
        });
    }

    private static void createMeta4GroupFunctions(ListGridField[] fields)
    {
        if (fieldMetaDS!=null && filterDS!=null)
            return;

        fieldMetaDS = new DataSource();
        fieldMetaDS.setClientOnly(true);

        filterDS = new DataSource();
        filterDS.setClientOnly(true);

        DataSourceTextField nameField = new DataSourceTextField("name");
        DataSourceTextField titleField = new DataSourceTextField("title");
        DataSourceTextField typeField = new DataSourceTextField("type");

        fieldMetaDS.setFields(nameField, titleField, typeField);

        ListGridRecord fieldMetaData[] = new ListGridRecord[fields.length];
        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++)
        {
            ListGridField field = fields[i];
            addField(field, fieldMetaData, i);
        }
        fieldMetaDS.setCacheData(fieldMetaData);
    }

    private static void addField(ListGridField field, ListGridRecord[] fieldMetaData, int i)
    {

        final ListGridFieldType type = field.getType();
        if (type!=null)
        {
            ListGridRecord record = new ListGridRecord();
            record.setAttribute("name", field.getName());
            record.setAttribute("title", field.getTitle());
            record.setAttribute("type", type.toString());

            fieldMetaData[i] = record;
            DataSourceField dsField = new DataSourceField(field.getName(), FieldType.valueOf(type.name()));
            dsField.setValidOperators(OperatorId.GREATER_THAN,OperatorId.GREATER_OR_EQUAL,
                    OperatorId.LESS_THAN,OperatorId.LESS_OR_EQUAL,OperatorId.EQUALS,OperatorId.NOT_EQUAL);
            filterDS.addField(dsField);
        }
    }

}
