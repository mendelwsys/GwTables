package com.mycompany.client.apps.App;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.api.NewFilterOperation;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.LogicalOperator;
import com.smartgwt.client.types.ValueEnum;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 17.07.15
 * Time: 18:14
 * GUI для редактирования фильтра
 */
public class FilterOptionsView
{
    public static interface IFilterCtrl
    {

        public static String VIEW_NAME ="VIEW_NAME";
        public static String FOLDER_NAME ="FOLDER_NAME";

        void apply(ListGridWithDesc gridWithDesc,Map<String,Object> params,FilterBuilder filterBuilder,ClickMode mode);
    }

    public enum ClickMode implements ValueEnum {

        APPLY("apply"),
        SAVE("save"),
        CANCEL("cancel");

        private String value;

        ClickMode(String value) {
         this.value=value;
        }

        @Override
        public String getValue() {
            return value;
        }
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

        final FilterBuilder filterBuilder = createFilterBuilder(gridWithDesc, filterOperation);
        final List<IButton> buttons = getButtons(gridWithDesc, ctrl, winModal, form, filterBuilder);

        final HLayout hLayout= new HLayout();
        hLayout.addMembers(buttons.toArray(new IButton[buttons.size()]));
        hLayout.setAlign(Alignment.RIGHT);
        hLayout.setMargin(5);

        winModal.addItem(form);
        winModal.addItem(filterBuilder);
        winModal.addItem(hLayout);

        return winModal;
    }

    protected FilterBuilder createFilterBuilder(ListGridWithDesc gridWithDesc, NewFilterOperation filterOperation)
    {
        final FilterBuilder filterBuilder = new FilterBuilder();
        filterBuilder.setAllowEmpty(false);
        filterBuilder.setFieldDataSource(gridWithDesc.getFieldsMetaDS());
        filterBuilder.setDataSource(gridWithDesc.getFilterDS());
        filterBuilder.clearCriteria();

        initFilterBuilder(filterOperation.getFilterByCriteria().getCriteria(), filterBuilder);
        return filterBuilder;
    }

    protected void initFilterBuilder(Criteria criteria, FilterBuilder filterBuilder) {

        //Criteria criteria = filterOperation.getFilterByCriteria().getCriteria();
        if (criteria!=null)
        {
            if (criteria.isAdvanced())
            {

                AdvancedCriteria aCriteria=((AdvancedCriteria) criteria);
                Criterion[] criteria1 = aCriteria.getCriteria();

//                filterBuilder.setCriteria((AdvancedCriteria) criteria);
//                Criterion[] criteria2=filterBuilder.getCriteria().getCriteria();
//                for (int i = 0; i < criteria1.length; i++)
//                {
//                    Criterion criterion2 = criteria2[i];
//                    Criterion criterion1 = criteria1[i];
//                    criterion2.setAttribute("value",criterion1.getAttribute("value"));
//                }
                for (Criterion criterion : criteria1)
                {
                    //criteria.getAttribute()
                    filterBuilder.addCriterion(criterion);
                }

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
    }

    protected List<FormItem> createFormItems(TreeGrid windowsTree, ListGridWithDesc gridWithDesc, NewFilterOperation filterOperation)
    {
        final boolean justInit=filterOperation.isJustInit();
        List<FormItem> items= new LinkedList<FormItem>();
        {
            final TextItem textName = new TextItem();
            items.add(textName);
            textName.setName(IFilterCtrl.VIEW_NAME);
            textName.setTitle("Название");
            textName.setValue(filterOperation.getViewName());

            final TextItem textFolder = new TextItem();
            items.add(textFolder);
            textFolder.setName(IFilterCtrl.FOLDER_NAME);
            textFolder.setTitle("Папка");


            Tree tree = windowsTree.getData();
            String nameP=tree.getNameProperty();

            try
            {
                String parentNodePath;
                TreeNode theNode = tree.findById(String.valueOf(filterOperation.getOperationId()));
                if (theNode!=null)
                {
                    tree.setNameProperty("Name");
                    parentNodePath=tree.getPath(tree.getParent(theNode));
                    if (justInit)
                        textFolder.setValue(parentNodePath+"/"+gridWithDesc.getViewName()); //+"/"+theNode.getAttribute(OperationNode.NAME_NODE)
                    else
                        textFolder.setValue(parentNodePath);
                }
                else
                    textFolder.setValue(filterOperation.getFolderName());
            }
            finally
            {
                tree.setNameProperty(nameP);
            }
        }
        return items;
    }

    protected Map<String, Object> getParamsByForm(final DynamicForm form,final Window winModal)
    {
        return OptionsViewers.getParamsByForm(form);
    }

    protected List<IButton> getButtons(final ListGridWithDesc gridWithDesc, final IFilterCtrl ctrl, final Window winModal, final DynamicForm form, final FilterBuilder filterBuilder)
    {
        List<IButton> buttons=new LinkedList<IButton>();
        {
            final IButton applyButton = new IButton(AppConst.APPLY_BUTTON_FILTER_OPTIONS);
            applyButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    ctrl.apply(gridWithDesc,getParamsByForm(form,winModal),filterBuilder, ClickMode.APPLY);
                    winModal.destroy();
                }
            });
            buttons.add(applyButton);

            final IButton saveButton = new IButton(AppConst.SAVE_BUTTON_FILTER_OPTIONS);
            saveButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    Map<String,Object> params = getParamsByForm(form,winModal);
                    if (params.get(IFilterCtrl.FOLDER_NAME)==null)
                            params.put(IFilterCtrl.FOLDER_NAME,"");
                    ctrl.apply(gridWithDesc,params,filterBuilder, ClickMode.SAVE);
                    winModal.destroy();
                }
            });
            buttons.add(saveButton);

            final IButton cancelButton = new IButton(AppConst.CANCEL_BUTTON_FILTER_OPTIONS);
            cancelButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    ctrl.apply(gridWithDesc,getParamsByForm(form,winModal),filterBuilder, ClickMode.CANCEL);
                    winModal.destroy();
                }
            });
            buttons.add(cancelButton);
        }
        return buttons;
    }

}
