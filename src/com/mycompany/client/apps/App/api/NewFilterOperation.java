package com.mycompany.client.apps.App.api;

import com.google.gwt.event.shared.HandlerRegistration;
import com.mycompany.client.CliFilterByCriteria;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.MyHeaderControl;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.FilterOptionsView;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.apps.OperationNode;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.data.*;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 10.12.14
 * Time: 17:20
 *
 */
public class NewFilterOperation extends SimpleOperation
{

    static final public String CLI_FILTER="CLI_FILTER";

    protected DescOperation oldDescOperation;

    @Override
    public Canvas operate(Canvas _warnGrid, IOperationContext ctx)
    {
        if (_warnGrid instanceof Window)
        {
            Canvas[] items = ((Window) _warnGrid).getItems();
            if (items!=null && items.length>0)
                return operate(items[0], null);
        }
        else if (_warnGrid instanceof ListGridWithDesc)
        {
            final ListGridWithDesc gridWithDesc = (ListGridWithDesc) _warnGrid;
            if (gridWithDesc.isMetaWasSet())
                _operate(gridWithDesc);
            else
            {
                new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
                {

                    @Override
                    public boolean operate()
                    {
                        if (gridWithDesc.isMetaWasSet())
                        {
                            _operate(gridWithDesc);
                            return true;
                        }
                        return false;
                    }
                });
            }


        }
        return null;
    }

    protected void _operate(ListGridWithDesc gridWithDesc)
    {
        {
            final Window target = gridWithDesc.getTarget();
            final HeaderControl pinUp = createHeaderControl(gridWithDesc, target);
            if (target.isCreated() && target.isDrawn())
                NodesHolder.addHeaderCtrl(target, pinUp);
            else
            {
                final HandlerRegistration[] handlerRegistrations=new HandlerRegistration[2];
                handlerRegistrations[0] = target.addDrawHandler(new DrawHandler() {
                    @Override
                    public void onDraw(DrawEvent event)
                    {
                        NodesHolder.addHeaderCtrl(target, pinUp);
                        handlerRegistrations[0].removeHandler();//удалить хандлер после обработки
                    }
                });

            }
        }

        if (justInit)
        {
            setFilterByCriteria(createFilter(gridWithDesc.getFilterDS(), null)); //Инициализиурем фильтр
            Window filterOptions = filterOptionsView.createViewFilterOptions(App01.GUI_STATE_DESC.getMainTreeGrid(), gridWithDesc, this,createIFilterCtrl());
            filterOptions.show();
        }
        else
        {
            final CliFilterByCriteria l_filterByCriteria = getFilterByCriteria();
            if (l_filterByCriteria !=null)
            {
                l_filterByCriteria.setFilterDS(gridWithDesc.getFilterDS());
                l_filterByCriteria.setCriteria(NodesHolder.reformatCriteria(l_filterByCriteria.getFilterDS(),l_filterByCriteria.getCriteria()));
            }

            NodesHolder.updateGridDescriptorByFilters(gridWithDesc, getOldDescOperation(), this);
            NodesHolder.applyCliFilter(gridWithDesc, l_filterByCriteria, l_filterByCriteria);
        }
    }

    protected FilterOptionsView filterOptionsView= getOptionView();
    protected FilterOptionsView getOptionView()
    {
        return new FilterOptionsView();
    }

    protected FilterOptionsView.IFilterCtrl iFilterCtrl;
    protected FilterOptionsView.IFilterCtrl createIFilterCtrl()
    {
        if (iFilterCtrl==null)
            iFilterCtrl = new IFilterCtrlImpl();
        return iFilterCtrl;
    }

//    protected NewFilterOperation[] thisOperationHolder =new NewFilterOperation[]{this};

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        NewFilterOperation operation = new NewFilterOperation();
        operation.setJustInit(false);
        return operation;
    }


    public Object copy()
    {
        NewFilterOperation operation= new NewFilterOperation();
        return _copyOperation(operation);
    }

    protected Object _copyOperation(NewFilterOperation operation) {
        operation.setViewName(getViewName());
        operation.setFolderName(getFolderName());

        operation.setOperationId(getOperationId());
        operation.setParentOperationId(getParentOperationId());

        operation.setJustInit(false);
        CliFilterByCriteria localFilter = getFilterByCriteria();
        DataSource filterDS = localFilter.getFilterDS();
        Criteria criteria = localFilter.getCriteria();
        operation.setFilterByCriteria(createFilter(filterDS, criteria));//При копировании вообще-то надо копировать критерий???
        return operation;
    }

    protected CliFilterByCriteria createFilter(DataSource filterDS, Criteria criteria) {
        return new CliFilterByCriteria(filterDS, criteria);
    }

    public HeaderControl createHeaderControl(final Canvas canvas, final Window target)
    {


        pinUp=new MyHeaderControl (getHeaderIcon(),
        new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                if (canvas instanceof ListGridWithDesc)
                {
                    ListGridWithDesc gridWithDesc = (ListGridWithDesc) canvas;

                    NewFilterOperation thisOperationHolder= (NewFilterOperation) pinUp.getOperation();
                    final CliFilterByCriteria localFilter = thisOperationHolder.getFilterByCriteria();
                    localFilter.setFilterDS(gridWithDesc.getFilterDS());
//                  filterByCriteria= new CliFilterByCriteria(gridWithDesc.getFilterDS(),filterByCriteria.getCriteria()); //Нельзя просто взять
                    // и перезаписать фильтр поскольку он может быть применен в гриде.
                    Window winFilter = filterOptionsView.createViewFilterOptions(App01.GUI_STATE_DESC.getMainTreeGrid(), (ListGridWithDesc) canvas, thisOperationHolder,thisOperationHolder.createIFilterCtrl());
                    winFilter.show();
                }
            }
        });
        pinUp.setGrid(canvas);
        pinUp.setOperation(this);
        pinUp.setTarget(target);


        pinUp.setTooltip(getViewName());

        pinUp.addHoverHandler(new HoverHandler() {
            @Override
            public void onHover(HoverEvent event) {
                NewFilterOperation thisOperationHolder=(NewFilterOperation) pinUp.getOperation();
                pinUp.setTooltip(thisOperationHolder.getViewName());
            }
        });


        pinUp.setCanDrag(true);
        pinUp.setCanDrop(true);
        return pinUp;
    }

    protected HeaderControl.HeaderIcon getHeaderIcon() {
        return HeaderControl.HOME;
    }


    public Canvas onRemove(Canvas _warnGrid, Window target)
    {
        if (_warnGrid instanceof ListGridWithDesc)
        {
            final ListGridWithDesc gridWithDesc = (ListGridWithDesc) _warnGrid;

            NewFilterOperation thisOperationHolder=(NewFilterOperation) pinUp.getOperation();

            final CliFilterByCriteria localFilter = thisOperationHolder.getFilterByCriteria();
            if (localFilter !=null)
            {
                final IDataFlowCtrl ctrl=gridWithDesc.getCtrl();
                gridWithDesc.removeCliFilter(localFilter);
                gridWithDesc.getDescOperation().getSubOperation().remove(thisOperationHolder.getOldDescOperation());
                ctrl.addAfterUpdater(new DSCallback()
                {
                    @Override
                    public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                    {
                        gridWithDesc.removeCliFilter(localFilter);
                        ctrl.removeAfterUpdater(this);
                    }
                });
            }
        }
        return null;
    }


    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        NewFilterOperation filterOperation = (NewFilterOperation) super.createOperation(descOperation, operation);
        String cliFilterString= (String) descOperation.get(CLI_FILTER);
        AdvancedCriteria criteria = new AdvancedCriteria(JSOHelper.eval(cliFilterString));
        CliFilterByCriteria filterByCriteria = createFilter(null, criteria);
        filterOperation.setFilterByCriteria(filterByCriteria);
        return filterOperation;
    }


    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation=super.getDescOperation(descOperation);
//        descOperation.apiName=NewFilterOperation.class.getName();
        Criteria cr = getFilterByCriteria().getCriteria();
        descOperation.put(CLI_FILTER, JSON.encode(cr.getJsObj()));
        return oldDescOperation=descOperation;
    }

    public DescOperation getOldDescOperation()
    {
        return oldDescOperation;
    }





    public NewFilterOperation(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);
    }

    protected NewFilterOperation()
    {
        super(-1, -1, null,TypeOperation.addClientFilter);
    }


    public boolean isJustInit() {
        return justInit;
    }

    protected boolean justInit=true;

    public void setJustInit(boolean justInit) {
        this.justInit = justInit;
    }

    public void setFilterByCriteria(CliFilterByCriteria filterByCriteria) {
        this.filterByCriteria = filterByCriteria;
    }

    public CliFilterByCriteria getFilterByCriteria() {
        return filterByCriteria;
    }

    protected CliFilterByCriteria filterByCriteria;

    public MyHeaderControl getPinUp() {
        return pinUp;
    }

    public void setPinUp(MyHeaderControl pinUp) {
        this.pinUp = pinUp;
    }

    protected MyHeaderControl pinUp;



    public class  IFilterCtrlImpl implements FilterOptionsView.IFilterCtrl
    {
        public void apply(ListGridWithDesc gridWithDesc,Map<String,Object> params,FilterBuilder filterBuilder,FilterOptionsView.ClickMode mode)
        {
            try
            {
                if (mode.equals(FilterOptionsView.ClickMode.APPLY))
                    _apply(gridWithDesc, params, filterBuilder);
                else  if (mode.equals(FilterOptionsView.ClickMode.SAVE))
                    _save(gridWithDesc, params, filterBuilder);
                else
                    _cancel(gridWithDesc, params, filterBuilder);
            }
            finally
            {
                justInit=false;
            }
        }

        protected void _cancel(ListGridWithDesc gridWithDesc, Map<String, Object> params, FilterBuilder filterBuilder)
        {
            if (justInit)   //удаление по кнопке отмена
                NodesHolder.removeHeaderCtrl(gridWithDesc.getTarget(),getPinUp());

        }
        protected void _save(ListGridWithDesc gridWithDesc, Map<String, Object> params, FilterBuilder filterBuilder)
        {
            String pathVal= (String) params.get(FOLDER_NAME);
            if (pathVal==null) pathVal="";
            String name= (String) params.get(VIEW_NAME);

            TreeGrid windowsTree = App01.GUI_STATE_DESC.getMainTreeGrid();
            Tree tree = windowsTree.getData();
            DataSource dsTree = windowsTree.getDataSource();

            String nameP=tree.getNameProperty();
            String fullPath="";

            CliFilterByCriteria oldFilter = getFilterByCriteria();
            DescOperation oldDescOperation =getOldDescOperation();

            final NewFilterOperation[] operationHolder= new NewFilterOperation[]{NewFilterOperation.this};

            try
            {
                boolean  reWritePath=false;
                boolean  notFoundNode=true;

                tree.setNameProperty("Name");


                if (!justInit)
                {
                    TreeNode operationNode = tree.findById(String.valueOf(getOperationId()));
                    if (operationNode!=null)
                    {
                        notFoundNode=false;
                        String path=tree.getPath(operationNode);
                        if (reWritePath=path.equals(fullPath+pathVal+"/"+name))
                        {
                            setViewName(name);
                            setOperationId(operationNode.getAttributeAsInt(OperationNode.OPERATION_ID));
                            setParentOperationId(operationNode.getAttributeAsInt(OperationNode.PARENT_OPERATION_ID));
                            OperationNode.addOperation(operationNode, NewFilterOperation.this);
                        }
                    }
                }



                if (!reWritePath)
                {
                    int parentOperationId = 1;
                    int nextOperationId = -1;

                    String[] paths=pathVal.split("/");
                    nextOperationId= OptionsViewers.getNextNodeID(tree.getAllNodes());

                    for (int i = 0, pathsLength = paths.length; i < pathsLength; i++)
                    {
                        if (paths[i].length()!=0)
                        {
                            TreeNode operationNode=tree.find(fullPath+paths[i]+"/");
                            if (operationNode!=null)
                                parentOperationId=operationNode.getAttributeAsInt(OperationNode.OPERATION_ID);
                            else
                            {
                                OperationNode node = new OperationNode(new SimpleOperation(nextOperationId, parentOperationId, paths[i], TypeOperation.NON));
                                NodesHolder.setTreeDs(dsTree, node);
                                parentOperationId=nextOperationId;
                                nextOperationId++;
                            }
                        }
                        fullPath+=paths[i]+"/";
                    }


                    if (justInit || notFoundNode)
                    { //Создаем нод
                        setViewName(name);
                        setFolderName(fullPath);
                        setParentOperationId(parentOperationId);
                        setOperationId(nextOperationId);
                        OperationNode node = new OperationNode(NewFilterOperation.this);
                        NodesHolder.setTreeDs(dsTree, node);
                    }
                    else
                    {//копируем нод
                        operationHolder[0]= (NewFilterOperation) (operationHolder[0]).copy();
                        operationHolder[0].pinUp=pinUp;
                        pinUp.setOperation(operationHolder[0]);
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
        }

        protected void _apply(ListGridWithDesc gridWithDesc, Map<String, Object> params, FilterBuilder filterBuilder) {
            CliFilterByCriteria filter = getFilterByCriteria();
            filter.setCriteria(filterBuilder.getCriteria());

            DescOperation oldDescOperation = getOldDescOperation();
            NodesHolder.updateGridDescriptorByFilters(gridWithDesc, oldDescOperation, NewFilterOperation.this);
            NodesHolder.applyCliFilter(gridWithDesc, filter,filter);
            setViewName((String) params.get(VIEW_NAME));
            setFolderName((String) params.get(FOLDER_NAME));
            if (justInit)
                 setOperationId(-1);
        }
    }

//    protected static class  InMyHeaderControl extends   MyHeaderControl
//    {
//        protected NewFilterOperation filterRef;
//        public InMyHeaderControl(HeaderIcon icon) {
//            super(icon);
//        }
//
//        public InMyHeaderControl(HeaderIcon icon, ClickHandler clickHandler) {
//            super(icon, clickHandler);
//        }
//    }

}
