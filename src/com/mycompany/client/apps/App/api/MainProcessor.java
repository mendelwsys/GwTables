package com.mycompany.client.apps.App.api;

import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.ISyncHandler;
import com.mycompany.client.apps.App.MyPortalLayout;
import com.mycompany.client.apps.App.api.charts4table.CreateChartByTable;
import com.mycompany.client.apps.OperationNode;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.dojoChart.BaseDOJOChart;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.test.informer.DetailViewerWithDesc;
import com.mycompany.client.test.informer.DetailViewerWithDesc2;
import com.mycompany.client.utils.PortalLayoutUtils;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalPosition;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.menu.MenuButton;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 15.12.14
 * Time: 17:46
 * Основной интерпретатор команд
 */
public class MainProcessor
{

    Map<String,IDescProcessor> map2Operation= new HashMap<String,IDescProcessor>();

    {
        map2Operation.put(CreateEventTable.class.getName(),new CreateEventTable());
        map2Operation.put(CreateEventTableH.class.getName(),new CreateEventTableH());
        map2Operation.put(CreateReportTable.class.getName(),new CreateReportTable());
        map2Operation.put(CreatePlacesTable.class.getName(),new CreatePlacesTable());
        map2Operation.put(CreateDelayReportTable.class.getName(),new CreateDelayReportTable());
        map2Operation.put(DorOperationFactory.class.getName(),new DorOperationFactory());
        map2Operation.put(NewFilterOperation.class.getName(),new NewFilterOperation());
        map2Operation.put(NewGroupFilterOperation.class.getName(),new NewGroupFilterOperation());
        map2Operation.put(AddNodeOperation.class.getName(),new AddNodeOperation());
        map2Operation.put(RunDescOperation.class.getName(),new RunDescOperation());
        map2Operation.put(SimpleOperation.class.getName(),new SimpleOperation());
        map2Operation.put(AddColumnOperation.class.getName(),new AddColumnOperation());
        map2Operation.put(AddPageOperation.class.getName(),new AddPageOperation());
        map2Operation.put(AddPagesOperation.class.getName(),new AddPagesOperation());
        map2Operation.put(AddPagesOperation.class.getName(),new AddPagesOperation());
        map2Operation.put(CreateChatView.class.getName(),new CreateChatView());
        map2Operation.put(CreateLentaTable.class.getName(),new CreateLentaTable());
        map2Operation.put(GISSelectEventOperation.class.getName(),new GISSelectEventOperation());
        map2Operation.put(ExcelExportOperation.class.getName(), new ExcelExportOperation());
        map2Operation.put(NewWarnInformer.class.getName(),new NewWarnInformer());
        map2Operation.put(NewWarnInformer2.class.getName(),new NewWarnInformer2());
        map2Operation.put(NewWarnInformer3.class.getName(), new NewWarnInformer3());
        map2Operation.put(NewRefInformer.class.getName(),new NewRefInformer());
        map2Operation.put(NewRefInformer2.class.getName(),new NewRefInformer2());
        map2Operation.put(NewRefInformer3.class.getName(), new NewRefInformer3());
        map2Operation.put(NewDelayInformer.class.getName(),new NewDelayInformer());
        map2Operation.put(NewDelayInformer2.class.getName(),new NewDelayInformer2());
        map2Operation.put(NewDelayInformer3.class.getName(), new NewDelayInformer3());
        map2Operation.put(NewViolInformer.class.getName(),new NewViolInformer());
        map2Operation.put(NewViolInformer2.class.getName(),new NewViolInformer2());
        map2Operation.put(NewViolInformer3.class.getName(), new NewViolInformer3());

        map2Operation.put(CreateChartByTable.class.getName(),new CreateChartByTable());
        map2Operation.put(CreateStyleOperation.class.getName(), new CreateStyleOperation());
        map2Operation.put(CreateVAGTORTable.class.getName(), new CreateVAGTORTable());
        map2Operation.put(ExtendGridByPlaces.class.getName(),new ExtendGridByPlaces());
        map2Operation.put(DrillRepOperation.class.getName(),new DrillRepOperation());

        map2Operation.put(CreateRef12Table.class.getName(),new CreateRef12Table());
        map2Operation.put(CreateWarnVTable.class.getName(),new CreateWarnVTable());

        map2Operation.put(CreateWinRepTable.class.getName(),new CreateWinRepTable());
        map2Operation.put(CreateWinPlanTable.class.getName(),new CreateWinPlanTable());

        map2Operation.put(CreateWarnACTTable.class.getName(),new CreateWarnACTTable());

        map2Operation.put(CreateOIndxTable.class.getName(),new CreateOIndxTable());

        map2Operation.put(CreateWarnAGRReportTable.class.getName(),new CreateWarnAGRReportTable());

        map2Operation.put(CreateRSMReportTable.class.getName(),new CreateRSMReportTable());

        map2Operation.put(CreateLOCReportTable.class.getName(),new CreateLOCReportTable());

        map2Operation.put(NewPolgFilterOperation.class.getName(),new NewPolgFilterOperation());

    }

    public OperationHolder preProcessAll(DescOperation descOperation)
    {
        IDescProcessor factoryOperation = map2Operation.get(descOperation.apiName);
        IOperation newOperation = factoryOperation.createOperation(descOperation);
        OperationHolder operationHolder = new OperationHolder(newOperation, descOperation);
        if (descOperation.getSubOperation()!=null)
        {
            List<DescOperation> subOperation = descOperation.getSubOperation();
            for (DescOperation subDesc : subOperation)
                operationHolder.getSubHolders().add(preProcessAll(subDesc));
        }
        return operationHolder;
    }


    public OperationHolder preProcessIt(DescOperation descOperation)
    {
        IDescProcessor factoryOperation = map2Operation.get(descOperation.apiName);
        IOperation newOperation = factoryOperation.createOperation(descOperation);
        return new OperationHolder(newOperation, descOperation);
    }



    public List<OperationHolder> preProcessAll(List<DescOperation> descOperation)
    {
        List<OperationHolder> operationHolders = new LinkedList<OperationHolder>();
        if (descOperation!=null)
            for (DescOperation subDesc : descOperation)
                operationHolders.add(preProcessAll(subDesc));
        return operationHolders;
    }


//    public Canvas operateAll(Canvas dragTarget, OperationHolder operationHolder, final IOperationContext ctx)
//    {
//        final Canvas gridOperation;
//        final IOperation operation;
//        try {
//            currentOperationHolder =operationHolder;
//            operation= operationHolder.getOperation();
//            gridOperation = operation.operate(dragTarget, ctx);
//        }
//        finally
//        {
//            currentOperationHolder =null;
//        }
//
//        final List<OperationHolder> subHolders = operationHolder.getSubHolders();
//        if (subHolders !=null)
//            for (OperationHolder subHolder : subHolders)
//                operateAll(gridOperation,subHolder, ctx);
//        return gridOperation;
//    }
//    public Canvas operateAll(Canvas dragTarget, List<OperationHolder> operationHolders, IOperationContext ctx)
//    {
//        if (operationHolders !=null)
//            for (OperationHolder subHolder : operationHolders)
//                dragTarget=operateAll(dragTarget,subHolder, ctx);
//        return dragTarget;
//    }

    public Canvas operateAll(Canvas dragTarget, OperationHolder operationHolder, final IOperationContext ctx)
    {
        final Canvas resCanvas;
        IOperation operation;
        try {
            currentOperationHolder =operationHolder;
            operation= operationHolder.getOperation();
            resCanvas = operation.operate(dragTarget, ctx);
        }
        finally
        {
            currentOperationHolder =null;
        }

        final List<OperationHolder> subHolders = operationHolder.getSubHolders();
        if (subHolders !=null && subHolders.size()>0)
        {
            final ISyncHandler handler;
            if ((handler=operation.getSyncHandler())==null || handler.isCompletely())
               operateAll(resCanvas, subHolders, ctx, 0);
            else
            {
                new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
                {

                    @Override
                    public boolean operate()
                    {
                        if (handler.isCompletely())
                        {
                            operateAll(resCanvas, subHolders, ctx, 0);
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
        return resCanvas;
    }

    protected void operateAll(Canvas dragTarget, final List<OperationHolder> operationHolders, final IOperationContext ctx,int ix)
    {
        if (operationHolders !=null)
        {
            for (int i = ix, operationHoldersSize = operationHolders.size(); i < operationHoldersSize; i++)
            {
                OperationHolder subHolder = operationHolders.get(i);
                operateAll(dragTarget, subHolder, ctx);
                IOperation operation = subHolder.getOperation();
                final ISyncHandler handler;
                if ((handler=operation.getSyncHandler())!=null && !handler.isCompletely())
                {
                    final int _ix=i;
                    final Canvas _dragTarget=dragTarget;
                    new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
                    {
                        @Override
                        public boolean operate()
                        {
                            if (handler.isCompletely())
                            {
                                operateAll(_dragTarget, operationHolders, ctx, _ix + 1);
                                return true;
                            }
                            return false;
                        }
                    });
                    break;
                }
            }
        }
    }

    public Canvas operateAll(Canvas dragTarget, final List<OperationHolder> operationHolders, final IOperationContext ctx)
    {
        if (operationHolders !=null)
        {
            for (int i = 0, operationHoldersSize = operationHolders.size(); i < operationHoldersSize; i++)
            {
                OperationHolder subHolder = operationHolders.get(i);
                dragTarget=operateAll(dragTarget, subHolder, ctx);
                IOperation operation = subHolder.getOperation();

                final ISyncHandler handler;
                if ((handler=operation.getSyncHandler())!=null && !handler.isCompletely())
                {
                    final int _ix=i;
                    final Canvas _dragTarget=dragTarget;
                    new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation()
                    {
                        @Override
                        public boolean operate()
                        {
                            if (handler.isCompletely())
                            {
                                operateAll(_dragTarget, operationHolders, ctx, _ix + 1);
                                return true;
                            }
                            return false;
                        }
                    });
                    break;
                }
            }
        }
        return dragTarget;
    }



    public OperationHolder getCurrentOperationHolder()
    {
        return currentOperationHolder;
    }

    private OperationHolder currentOperationHolder;
    public Canvas operateIt(Canvas dragTarget, OperationHolder operationHolder, IOperationContext ctx)
    {
        try {
            currentOperationHolder =operationHolder;
            return operationHolder.getOperation().operate(dragTarget, ctx);
        }
        finally
        {
            currentOperationHolder =null;
        }
    }

    public Canvas operateIt(Canvas dragTarget, List<OperationHolder> operationHolders, IOperationContext ctx)
    {
        if (operationHolders !=null)
            for (OperationHolder holder : operationHolders)
                operateIt(dragTarget,holder, ctx);
        return dragTarget;
    }

    public DescOperation buildDescriptorFromApp(GUIStateDesc stateDesc)
    {
        DescOperation appDesc=new DescOperation();//Это дескриптор, приложения, в принципе можно в нем сохранять
        appDesc.apiName= App01.class.getName();

        //TODO Сначала собираем дерево, оно одинаково на всех вкладках
        final TreeGrid mainTreeGrid = stateDesc.getMainTreeGrid();



        DataSource ds = mainTreeGrid.getDataSource();
        Record[] recs = ds.getCacheData();
        //Дальше анализируем каждую запись на добаленность
        for (Record rec : recs)
        {
            Boolean isBuildIn=rec.getAttributeAsBoolean(OperationNode.ISBUILDIN);
            if (!isBuildIn)
            {
                Object operation = OperationNode.getOperation(rec);
                if (operation instanceof IOperation)
                {

                    int id=((IOperation)operation).getOperationId();
                    TreeNode node = mainTreeGrid.getData().findById(String.valueOf(id));
                    if (node==null)
                        continue;
                }

                if (operation instanceof IDescProcessor)
                {
                    DescOperation descOperation = ((IDescProcessor) operation).getDescOperation(new DescOperation());

                    DescOperation addNodeDescOperation = map2Operation.get(AddNodeOperation.class.getName()).getDescOperation(new DescOperation());
                    addNodeDescOperation.getSubOperation().add(descOperation);
                    appDesc.getSubOperation().add(addNodeDescOperation);
                }
            }
        }
        //TODO Потом собираем вкладки, с каждым открытым окном внутри вкладок

        GUIStateDesc desc = App01.GUI_STATE_DESC;

        int currentPage=desc.getCurrentPage();
        Set<Integer> pages=desc.getOrderedNumPages();

        AddPagesOperation addPagesOperation =new AddPagesOperation();


        List<DescOperation> pageDesc=new LinkedList<DescOperation>();

        int orderedNumber=0;
        for (Integer pageNum : pages)
        {
            AddPageOperation addPageOperation =new AddPageOperation();
            addPageOperation.setPageNum(pageNum);
            addPageOperation.setPageOrderNum(orderedNumber);

            DescOperation addPageDescriptor = null;



            List<Canvas> onPages=stateDesc.getPageCanvas(pageNum);//Отдаем то что есть на странице
            for (Canvas onPage : onPages)
            {

                if (AppConst.t_TREE_VIEW_INDICATOR.equals(onPage.getID()))
                {
                    addPageOperation.setViewTree(onPage.isVisible());
                }
                else
                if (onPage instanceof MyPortalLayout)
                {
                    MyPortalLayout pl = ((MyPortalLayout) onPage);
                    int colNum = pl.getNumColumns();
                    //TODO Добавить пиктограмму на страницу, и все таки еще видимость дерева

                    if (addPageDescriptor==null)
                    {
                        ToolStripButton button = pl.getSwitchButton();
                        addPageOperation.setPageTitle(button.getPrompt());
                        String iconUrl=button.getIcon();
                        addPageOperation.setPageIcon(iconUrl);

                        addPageDescriptor = addPageOperation.getDescOperation(new DescOperation());
                    }

                    for (int i = 0; i < colNum;i++)
                    {

                        AddColumnOperation addColumnOperation =new AddColumnOperation();
                        addColumnOperation.setColNum(i);


                        Canvas headerLayout = PortalLayoutUtils.getHeaderLayout(pl,i);
                        MenuButton mb= PortalLayoutUtils.getMenuButton(headerLayout);
                        addColumnOperation.setShowMenu(headerLayout.isVisible());
                        addColumnOperation.setMenuHeader(mb.getTitle());
                        DescOperation addColumnDescriptor = addColumnOperation.getDescOperation(new DescOperation());

                        Portlet[] portlets = pl.getPortlets();
                        for (Portlet portlet : portlets)
                        {
                            PortalPosition pp = pl.getPortalPosition(portlet);
                            if (pp.getColNum()==i)
                            {
                                Canvas[] canvases = portlet.getItems();
                                for (Canvas canvas : canvases)
                                {
                                    if (canvas instanceof ListGridWithDesc)
                                    {
                                        final ListGridWithDesc gridWithDesc = (ListGridWithDesc) canvas;
                                        DescOperation operation = gridWithDesc.getDescOperation();
                                        operation.put(AddColumnOperation.ROW_NUM,pp.getRowNum());
                                        operation.put(AddColumnOperation.ROW_OFFSET,pp.getPosition());

//                                        gridWithDesc.set



//                                        ListGridField[] flds = gridWithDesc.getAllFields();
//                                        LinkedList<ListGridField> ll = new LinkedList(Arrays.asList(flds));
//                                        ListGridField fld = ll.remove(0);
//                                        ll.add(fld);
//                                        gridWithDesc.setFields(ll.toArray(new ListGridField[ll.size()]));

//                                        for (ListGridField fld : flds)
//                                        {
//                                            //fld.setHidden();
//                                            boolean hidden=fld.getHidden();
//                                            if (hidden)
//                                            {
//                                                int k=0;
//                                                k+=1;
//                                            }
//                                        }



                                        addColumnDescriptor.getSubOperation().add(operation);
                                    }
                                    else if (canvas instanceof BaseDOJOChart)
                                    {
                                        final BaseDOJOChart chart = (BaseDOJOChart) canvas;
                                        if (chart.get2Save())
                                        {
                                            DescOperation operation = chart.getDescOperation();
                                            operation.put(AddColumnOperation.ROW_NUM,pp.getRowNum());
                                            operation.put(AddColumnOperation.ROW_OFFSET,pp.getPosition());
                                            addColumnDescriptor.getSubOperation().add(operation);
                                        }
                                    }
                                }
                            }
                        }
                        addPageDescriptor.getSubOperation().add(addColumnDescriptor);
                    }
                }

            }


            if (pageNum==currentPage)
                addPagesOperation.setCurrOrderPageNum(orderedNumber);

            pageDesc.add(addPageDescriptor);

            orderedNumber++;
        }


        addPagesOperation.setViewToolStrip(stateDesc.getToolStrip().isVisible());
        DescOperation addPagesDescriptor = addPagesOperation.getDescOperation(new DescOperation());
        addPagesDescriptor.getSubOperation().addAll(pageDesc);

        appDesc.getSubOperation().add(addPagesDescriptor);

        //Добавляем информеры
        Layout ml = App01.GUI_STATE_DESC.getMainLayout();
        Canvas[] mlCanvs=ml.getChildren();
        for (Canvas mlCanv : mlCanvs) {
            if (mlCanv instanceof DetailViewerWithDesc) {
                DescOperation descInformer = ((DetailViewerWithDesc) mlCanv).getDescInformer();
                if (descInformer != null)
                    appDesc.getSubOperation().add(descInformer);
            }
            if (mlCanv instanceof DetailViewerWithDesc2) {
                DescOperation descInformer = ((DetailViewerWithDesc2) mlCanv).getDescInformer();
                if (descInformer != null)
                    appDesc.getSubOperation().add(descInformer);
            }
        }


        return appDesc;
    }



}

