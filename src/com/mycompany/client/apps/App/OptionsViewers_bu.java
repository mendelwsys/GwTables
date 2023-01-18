package com.mycompany.client.apps.App;

import com.mycompany.client.CliFilterByCriteria;
import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.api.NewFilterOperation;
import com.mycompany.client.apps.OperationNode;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.utils.PortalLayoutUtils;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.LogicalOperator;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.menu.MenuButton;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.12.14
 * Time: 12:45
 * TODO Диалоговые компоненты настройки интерфейса пользователя
 * Доделать:Выбор пиктограммы из файла(возможно после подсоединения к пользовательской БД),Загрузку из конфига,
 * в частности списка пиктограмм.
 *
 */
public class OptionsViewers_bu
{



    public static Window createViewFilterOptions
            (
                    final TreeGrid windowsTree, final ListGridWithDesc gridWithDesc,
                    final NewFilterOperation[] operationHolder
            )
    {


        final boolean justInit=operationHolder[0].isJustInit();


        final Window winModal = createEmptyWindow(AppConst.CUSTOM_FILTER_OPTIONS_HEADER);
        winModal.setAutoSize(true);

        DynamicForm form = createEmptyForm();
        form.setNumCols(4);
        form.setPadding(10);


        final TextItem textName = new TextItem();
        textName.setTitle("Название");
        textName.setValue(operationHolder[0].getViewName());

        final TextItem textFolder = new TextItem();
        textFolder.setTitle("Папка");


        Tree tree = windowsTree.getData();
        String nameP=tree.getNameProperty();

        try
        {
            String parentNodePath;
            TreeNode theNode = tree.findById(String.valueOf(operationHolder[0].getOperationId()));
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
                textFolder.setValue(operationHolder[0].getFolderName());
        }
        finally
        {
            tree.setNameProperty(nameP);
        }

        final FilterBuilder filterBuilder = new FilterBuilder();


        filterBuilder.setAllowEmpty(false);
        filterBuilder.setFieldDataSource(gridWithDesc.getFieldsMetaDS());
        filterBuilder.setDataSource(gridWithDesc.getFilterDS());
        filterBuilder.clearCriteria();

        Criteria criteria = operationHolder[0].getFilterByCriteria().getCriteria();
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

                //filterBuilder.setCriteria(aCriteria);
            }
        }


        final IButton applyButton = new IButton(AppConst.APPLY_BUTTON_FILTER_OPTIONS);
        applyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                CliFilterByCriteria filter = operationHolder[0].getFilterByCriteria();
                DescOperation oldDescOperation = operationHolder[0].getOldDescOperation();

                filter.setCriteria(filterBuilder.getCriteria());

                NodesHolder.updateGridDescriptorByFilters(gridWithDesc, oldDescOperation, operationHolder[0]);
                NodesHolder.applyCliFilter(gridWithDesc, filter,filter);
                operationHolder[0].setFolderName((String) textFolder.getValue());
                operationHolder[0].setViewName((String) textName.getValue());
                if (justInit)
                    operationHolder[0].setOperationId(-1);

                winModal.destroy();
            }
        });

        final IButton saveButton = new IButton(AppConst.SAVE_BUTTON_FILTER_OPTIONS);
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {

                String pathVal= (String) textFolder.getValue();
                if (pathVal==null) pathVal="";
                String name= (String) textName.getValue();

                Tree tree = windowsTree.getData();
                DataSource dsTree = windowsTree.getDataSource();

                String nameP=tree.getNameProperty();
                String fullPath="";

                CliFilterByCriteria oldFilter = operationHolder[0].getFilterByCriteria();
                DescOperation oldDescOperation = operationHolder[0].getOldDescOperation();
                try
                {
                    boolean  reWritePath=false;
                    boolean  notFoundNode=true;

                    tree.setNameProperty("Name");


                    if (!justInit)
                    {
                        TreeNode operationNode = tree.findById(String.valueOf(operationHolder[0].getOperationId()));
                        if (operationNode!=null)
                        {
                            notFoundNode=false;
                            String path=tree.getPath(operationNode);
                            if (reWritePath=path.equals(fullPath+pathVal+"/"+name))
                            {
                                operationHolder[0].setViewName(name);
                                operationHolder[0].setOperationId(operationNode.getAttributeAsInt(OperationNode.OPERATION_ID));
                                operationHolder[0].setParentOperationId(operationNode.getAttributeAsInt(OperationNode.PARENT_OPERATION_ID));
                                OperationNode.addOperation(operationNode, operationHolder[0]);
                            }
                        }
                    }

                    if (!reWritePath)
                    {
                        int parentOperationId = 1;
                        int nextOperationId = -1;

                        String[] paths=pathVal.split("/");
                        nextOperationId=getNextNodeID(tree.getAllNodes());

                        for (int i = 0, pathsLength = paths.length; i < pathsLength; i++)
                        {
                            if (paths[i].length()!=0)
                            {
                                TreeNode operationNode=tree.find(fullPath+paths[i]+"/");
                                if (operationNode!=null)
                                    parentOperationId=operationNode.getAttributeAsInt(OperationNode.OPERATION_ID);
                                else
                                {
                                    OperationNode node = new OperationNode(new SimpleOperation(nextOperationId, parentOperationId, paths[i], IOperation.TypeOperation.NON));
                                    NodesHolder.setTreeDs(dsTree, node);
                                    parentOperationId=nextOperationId;
                                    nextOperationId++;
                                }
                            }
                            fullPath+=paths[i]+"/";
                        }


                        if (justInit || notFoundNode)
                        { //Создаем нод
                            operationHolder[0].setViewName(name);
                            operationHolder[0].setFolderName(fullPath);
                            operationHolder[0].setParentOperationId(parentOperationId);
                            operationHolder[0].setOperationId(nextOperationId);
                            OperationNode node = new OperationNode(operationHolder[0]);
                            NodesHolder.setTreeDs(dsTree, node);
                        }
                        else
                        {//копируем нод
                            operationHolder[0]= (NewFilterOperation) (operationHolder[0]).copy();
                            operationHolder[0].setViewName(name);
                            operationHolder[0].setFolderName(fullPath);
                            operationHolder[0].setParentOperationId(parentOperationId);
                            operationHolder[0].setOperationId(nextOperationId);
                            OperationNode node = new OperationNode(operationHolder[0]);
                            NodesHolder.setTreeDs(dsTree, node);
                        }
                    }
                }
                finally
                {
                    tree.setNameProperty(nameP);
                }

                CliFilterByCriteria newfilter = operationHolder[0].getFilterByCriteria();
                newfilter.setCriteria(filterBuilder.getCriteria());

                NodesHolder.updateGridDescriptorByFilters(gridWithDesc, oldDescOperation, operationHolder[0]);
                NodesHolder.applyCliFilter(gridWithDesc, newfilter,oldFilter);

                winModal.destroy();
            }
        });

        final IButton cancelButton = new IButton(AppConst.CANCEL_BUTTON_FILTER_OPTIONS);
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                if (justInit)   //удаление по кнопке отмена
                    NodesHolder.removeHeaderCtrl(gridWithDesc.getTarget(),operationHolder[0].getPinUp());
                winModal.destroy();
            }
        });

        HLayout hLayout= new HLayout();
        hLayout.addMembers(applyButton,saveButton, cancelButton);


        hLayout.setAlign(Alignment.RIGHT);
        hLayout.setMargin(5);


        form.setFields(textName,textFolder);

        winModal.addItem(form);
        winModal.addItem(filterBuilder);
        winModal.addItem(hLayout);

        return winModal;
    }



    public static int getNextNodeID(TreeNode[] data) {
        int maxoperationid=-1;
        for (TreeNode treeNode : data)
        {
            int operationId = treeNode.getAttributeAsInt(OperationNode.OPERATION_ID);
            if (maxoperationid< operationId) maxoperationid= operationId;
        }
        return maxoperationid+1;
    }


    public static Window createViewPortalColumnOptions(final MyPortalLayout portalLayout, final int column)
    {
        final Window winModal = createEmptyWindow(AppConst.CUSTOM_PAGE_OPTIONS_HEADER);
        winModal.setAutoSize(true);

        DynamicForm form = createEmptyForm();
        form.setNumCols(4);
        form.setPadding(10);



        final MenuButton mb = PortalLayoutUtils.getMenuButton(portalLayout, column);

        final TextItem textItem = new TextItem();
        textItem.setTitle(AppConst.HEADER_PORTAL_LAYOUT_TITLE_OPTIONS);

        textItem.setValue(mb.getTitle());


        final  SelectItem pictItem = new SelectItem();
        pictItem.setTitle(AppConst.HEADER_PORTAL_LAYOUT_PICT_OPTIONS);


        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();

        valueMap.put("headerIcon","Исходная");
        valueMap.put("application_cascade", "Каскад");
        valueMap.put("application_double", "Двойной");
        valueMap.put("application_edit", "Редактирование");
        valueMap.put("application_delete", "Удаление");


        pictItem.setValueMap(valueMap);
        pictItem.setImageURLPrefix(AppConst.TOOLSTRIPICONS_PREFIX);
        pictItem.setImageURLSuffix(AppConst.IMAGE_URL_SUFFIX);

        LinkedHashMap<String, String> valueIcons = new LinkedHashMap<String, String>();

        valueIcons.put("headerIcon","headerIcon");
        valueIcons.put("application_cascade","application_cascade");
        valueIcons.put("application_double", "application_double");
        valueIcons.put("application_edit", "application_edit");
        valueIcons.put("application_delete","application_delete");

        pictItem.setValueIcons(valueIcons);

        String icon=portalLayout.getSwitchButton().getIcon();
        for (String s : valueIcons.keySet())
        {
            if (icon.contains(s))
            {
                pictItem.setDefaultValue(s);
                break;
            }
        }

        form.setFields(textItem,pictItem);

//        final FileItem pictFile = new FileItem ();
//        pictFile.setTitle(AppConst.HEADER_PORTAL_LAYOUT_PICT_FILE_OPTIONS);

        final IButton buttonItem = new IButton(AppConst.APPLY_BUTTON_PORTAL_LAYOUT_OPTIONS);
        buttonItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                String value=(String)pictItem.getValue();
                final ToolStripButton switchButton = portalLayout.getSwitchButton();

                if (value!=null && value.length()>0)
                    switchButton.setIcon(AppConst.TOOLSTRIPICONS_PREFIX + value + AppConst.IMAGE_URL_SUFFIX);

                final String title = (String) textItem.getValue();
                if (title!=null)
                {
                    final MenuButton menuButton = PortalLayoutUtils.getMenuButton(portalLayout, column);
                    menuButton.setTitle(title);
                    menuButton.setPrompt(title);
                    switchButton.setPrompt(title);
                }
                winModal.destroy();
            }
        });

        final IButton cancelItem = new IButton(AppConst.CANCEL_BUTTON_PORTAL_LAYOUT_OPTIONS);
        cancelItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                winModal.destroy();
            }
        });


        HLayout hLayout= new HLayout();

        final GUIStateDesc guiStateDesc = App01.GUI_STATE_DESC;
        if (guiStateDesc.getNumPages()>1 || portalLayout.getNumColumns()>1)
        {
            IButton deleteItem = new IButton(AppConst.DELETE_BUTTON_PORTAL_LAYOUT_OPTIONS);
            deleteItem.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                   SC.ask(AppConst.SAY_DELETE_PAGE +mb.getTitle(),new BooleanCallback()
                   {
                       @Override
                       public void execute(Boolean value)
                       {
                           if (value)
                           {
                               portalLayout.removeColumn(column);//TODO !!!проверить удаление всех внутренних портлетов!!!!
                               if (portalLayout.getNumColumns()==0)
                                   guiStateDesc.destroyCurrentPage();
                               else
                                   portalLayout.setColumnMenu();
                               winModal.destroy();
                           }
                       }
                   });

                }
            });
            hLayout.addMembers(buttonItem, cancelItem,deleteItem);
        }
        else
            hLayout.addMembers(buttonItem, cancelItem);


        hLayout.setAlign(Alignment.RIGHT);
        hLayout.setMargin(5);


        winModal.addItem(form);
        winModal.addItem(hLayout);

        return winModal;
    }

    public static DynamicForm createEmptyForm() {
        DynamicForm form = new DynamicForm();
        form.setHeight100();
        form.setWidth100();
        form.setPadding(5);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);
        return form;
    }

    public  static Window createEmptyWindow(String header) {
        final Window winModal = new Window();
        winModal.setWidth(360);
        winModal.setHeight(165);
        winModal.setTitle(header);
        winModal.setShowMinimizeButton(false);
        winModal.setIsModal(true);
        winModal.setShowModalMask(true);
        winModal.centerInPage();
        winModal.addCloseClickHandler(new CloseClickHandler()
        {
            public void onCloseClick(CloseClickEvent event)
            {
                winModal.destroy();
            }
        });
        return winModal;
    }

}
