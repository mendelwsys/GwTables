package com.mycompany.client.apps.App;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.MyHeaderControl;
import com.mycompany.client.apps.App.api.*;
import com.mycompany.client.apps.App.api.charts4table.CreateChartByTable;
import com.mycompany.client.apps.FiltersAndGroups;
import com.mycompany.client.apps.OperationNode;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.dojoChart.IPortalLayoutCtrl;
import com.mycompany.client.operations.ICliFilter;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.IOperationFactory;
import com.mycompany.client.utils.GridMetaProviderFactory;
import com.mycompany.client.utils.IGridMetaProvider;
import com.mycompany.client.utils.IGridMetaProviderFactory;
import com.mycompany.client.utils.PortalLayoutUtils;
import com.mycompany.common.DescOperation;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.DragStopEvent;
import com.smartgwt.client.widgets.events.DragStopHandler;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RemoveRecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderDropEvent;
import com.smartgwt.client.widgets.tree.events.FolderDropHandler;
import com.smartgwt.client.widgets.tree.events.NodeClickEvent;
import com.smartgwt.client.widgets.tree.events.NodeClickHandler;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 14:11
 * TODO ?????? ???????????? ?????????????? ???? xml
 */
public class NodesHolder {


    public static Canvas onRemoveCtrlHandler(MyHeaderControl dragTarget1, Canvas grid) {
        if (grid instanceof ListGridWithDesc) {
            IOperation operation = dragTarget1.getOperation();
            if (operation != null)
            {
                //?????????????? ????????????
                Window window = dragTarget1.getTarget();
                removeHeaderCtrl(window, dragTarget1);

                //?????????????? ????????????????
                ListGridWithDesc grid1 = (ListGridWithDesc) grid;
                dragTarget1.onRemoveCtrl();
                return operation.onRemove(grid1, window);//TODO ???????????? ?????????? ???????????? ?????????????????????? ?????? ?????????????? ???? ???????????????? ??????????????
//                        grid1.getFiltersOperations().remove(operation);
//TODO ?????????????????? ??????????????????                         grid1.applyClientFilters();
            }
        }
        return null;
    }


    public static Canvas layOutDragHandler(MyPortalLayout layout, Canvas dragTarget, Integer colNum, Integer rowNum, Record[] dragData) {

        for (Record record : dragData)
        {
            Object operation = OperationNode.getOperation(record);

//            final Map params = new HashMap();
//            params.put("colNum",colNum);
//            params.put("rowNum",rowNum);
//            IOperationContext ctx = new OperationCtx(dragTarget, layout, params);

            IOperationContext ctx = null;

            if (operation instanceof IOperation)
            {
                IOperation operation1 = (IOperation) operation;
                if ( IOperation.TypeOperation.addEventPortlet.equals(operation1.getTypeOperation()))
                    return operation1.operate(dragTarget, ctx);
                else if (IOperation.TypeOperation.addChart.equals(operation1.getTypeOperation()))
                {
                    if (operation1 instanceof IPortalLayoutCtrl)
                    {
                        ((IPortalLayoutCtrl)operation1).setPortletLayOut(layout,colNum,rowNum);
                        return operation1.operate(dragTarget, ctx);
                    }
                }
                else if (IOperation.TypeOperation.addInformer.equals(operation1.getTypeOperation()))
                {
                    ((CreateInformerOperation) operation1).id = 0L;
                    return operation1.operate(dragTarget, ctx);
                }
                else if (IOperation.TypeOperation.addMenu.equals(operation1.getTypeOperation()))
                {
                    layout.setShowColumnMenus(true);
                    Canvas headerLayout = PortalLayoutUtils.getHeaderLayout(layout, colNum);
                    if (headerLayout!=null)
                        headerLayout.setVisible(true);
                    return operation1.operate(dragTarget, ctx);
                }
                else if (IOperation.TypeOperation.addPage.equals(operation1.getTypeOperation()))
                {
                    layout.addColumn(layout.getNumColumns());
                    layout.setColumnMenu();
                }
            }
        }
        return null;
    }

    public static void updateGridDescriptorByFilters(ListGridWithDesc gridWithDesc, DescOperation oldDescOperation, NewFilterOperation newFilterOperation) {
        DescOperation newDescOperation= newFilterOperation.getDescOperation(new DescOperation());
        DescOperation gridDescOperation = gridWithDesc.getDescOperation();
        if (oldDescOperation!=null)
            gridDescOperation.getSubOperation().remove(oldDescOperation);
        gridDescOperation.getSubOperation().add(newDescOperation);
    }


    public static Criteria reformatCriteria(DataSource ds, Criteria criteria)
    {
        boolean isChanged=false;
        Criterion[] crits = ((AdvancedCriteria) criteria).getCriteria();
        DataSourceField[] flds = ds.getFields();


        Map<FieldType,DateTimeFormat> dm=new HashMap<FieldType,DateTimeFormat>();

        dm.put(FieldType.DATETIME,DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));//???????? ???? ?????????????? ?? ?????????? Z, ?????????? ???????????????? ?????? ?????????????????????? ?????????????? ???????? ????????, ??.??. Z
        // ?????????????? ?? ?????? ?????? ?????????? ?????? ?????????????????????????? ???????????????? ?? ???????????? ?????????????????? ?? UTC, ?????? ???????? ???????? ???? ?????????????????? ?????????????????????????? ?? UTC. //TODO ?????????? ???????? ???????????????????????? ?? ?????????? ?????????????? ?????????????? ?????????????? ?? ????
        // (???????????? ?? ?????? ?????? ?????????????? ?? ???????????? ???????????? ???????????????????????? ???? ?????????????? ???????????????????? ????????????????, ?? ?????? ?????????????????? ???????????? ???????? ?????????? ?? ???? ?????????????????? ?????? ???????????? ???????? TZ ?????????????? ?????? ???????????????????? ???? ??????????????)
        dm.put(FieldType.DATE,DateTimeFormat.getFormat("yyyy-MM-dd"));
        dm.put(FieldType.TIME,DateTimeFormat.getFormat("HH:mm:ss"));


        for (DataSourceField fld : flds)
        {
            if (
                    FieldType.DATETIME.equals(fld.getType()) ||
                    FieldType.DATE.equals(fld.getType()) ||
                    FieldType.TIME.equals(fld.getType())
               )
            {
                for (Criterion crit : crits)
                {
                    isChanged|= searchAndReplace(dm, fld, crit);
                }
            }
        }
        if (isChanged)
            criteria=new AdvancedCriteria(((AdvancedCriteria) criteria).getOperator(),crits);
        return criteria;
    }

    private static boolean searchAndReplace(Map<FieldType, DateTimeFormat> dm, DataSourceField fld, Criterion crit)
    {
        boolean isChanged=false;
        if (crit.getFieldName()!=null)
        {
            if (crit.getFieldName().equals(fld.getName()))
            {
               Object obj=crit.getAttributeAsObject("value");
                if (obj instanceof String)
                {//???????????????? ?????????????????? ?? ????????
                    try
                    {
                        Date date= dm.get(fld.getType()).parse(obj.toString());
                        crit.setAttribute("value",date);
                        isChanged=true;
                    }
                    catch (IllegalArgumentException e)
                    {
                        //
                    }
                }
            }
        }
        else
        {
            Criterion[] crits = crit.getCriteria();
            for (Criterion criterion : crits)
                isChanged|= searchAndReplace(dm, fld, criterion);
        }
        return isChanged;
    }

    public static void applyCliFilter(final ListGridWithDesc gridWithDesc, final ICliFilter newFilter,final ICliFilter oldFilter)
    {
        final IDataFlowCtrl ctrl=gridWithDesc.getCtrl();
        ctrl.addAfterUpdater(new DSCallback()
        {
            @Override
            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
            {
                gridWithDesc.replaceCliFilter(newFilter,oldFilter);

//TODO                gridWithDesc.setVisible(true);
                ctrl.removeAfterUpdater(this);
            }
        });
        gridWithDesc.replaceCliFilter(newFilter,oldFilter);
    }



    public static IGridMetaProviderFactory dataProviderFactory = GridMetaProviderFactory.getInstance();
    public static IGridMetaProvider gridMetaProvider;
    static {
        gridMetaProvider = dataProviderFactory.createGridMetaProvider(new String[][]{{IGridMetaProviderFactory.TEST_CLIENT, "true1"}});
    }


    public static TreeGrid buildTree()
    {
        final TreeGrid treeGrid = FiltersAndGroups.buildTree(getNodesData(),AppConst.TREE_HEADER, FieldType.INTEGER,"mainTree");

        treeGrid.setCanAcceptDrop(true);
        treeGrid.setCanAcceptDroppedRecords(true);
        treeGrid.setCanEdit(true);
        treeGrid.setCanRemoveRecords(true);
        treeGrid.setCanReparentNodes(true);
        treeGrid.setCanReorderRecords(true);

        treeGrid.addEditCompleteHandler(new EditCompleteHandler() {
            @Override
            public void onEditComplete(EditCompleteEvent event) {
                Object objOpId=event.getNewValues().get(OperationNode.OPERATION_ID);

                TreeNode node = treeGrid.getTree().findById(objOpId.toString());
                if (node instanceof OperationNode)
                {
                    Object objOperation= OperationNode.getOperation( node);
                    if (objOperation instanceof IOperation)
                        ((IOperation)objOperation).setViewName((String)event.getNewValues().get(OperationNode.NAME_NODE));
                }
            }
        });

        treeGrid.addDropHandler(new DropHandler()
        {
            @Override
            public void onDrop(DropEvent event)
            {
                Canvas dragTarget = EventHandler.getDragTarget();
                TreeNode folder = treeGrid.getDropFolder();

                if (dragTarget instanceof TreeGrid )
                {
                    Record[] dragData = ((TreeGrid) dragTarget).getDragData();
                    for (Record record : dragData)
                    {
                        Object objectOperation=OperationNode.getOperation(record);
                        if (objectOperation instanceof IOperation)
                        {
                            IOperation operation = (IOperation) objectOperation;
                            if (operation.getTypeOperation().equals(IOperation.TypeOperation.addNode))
                            {
                                IOperation parentOperation = (IOperation) OperationNode.getOperation(folder);//.getAttributeAsObject(OperationNode.OPERATION);
                                Tree tree = treeGrid.getData();
                                DataSource ds = treeGrid.getDataSource();
                                int nextOperationId = OptionsViewers.getNextNodeID(tree.getAllNodes());
                                String viewName = "?????????? ??????????";
                                if (nextOperationId>0)
                                    viewName+=" (" + nextOperationId + ")";
                                SimpleOperation operation1 = new SimpleOperation(nextOperationId, parentOperation.getOperationId(), viewName, IOperation.TypeOperation.NON);
                                operation1.setViewAsFolder(true);
                                OperationNode node = new OperationNode(operation1);
                                //node.setAttribute(SimpleOperation.ISFOLDER,true);
                                setTreeDs(ds, node);

                            }
                            else
                                return;
                        }
                        else
                            return;
                    }
                }
                else if (dragTarget instanceof  Window)
                {
                    Window closingWindow = (Window) dragTarget;
                    Canvas grid = closingWindow.getItems()[0];
                    if (grid instanceof ListGridWithDesc)
                    {
                        IOperation parentOperation = (IOperation)OperationNode.getOperation(folder);
                        Tree tree = treeGrid.getData();
                        int nextOperationId = OptionsViewers.getNextNodeID(tree.getAllNodes());

                        final DescOperation descOperation=((ListGridWithDesc) grid).getDescOperation();

                        IOperation operation = new RunDescOperation(descOperation);
                        operation.setOperationId(nextOperationId);
                        operation.setParentOperationId(parentOperation.getOperationId());
                        operation.setViewName(closingWindow.getTitle());
                        operation.setTypeOperation(IOperation.TypeOperation.addEventPortlet);
                        OperationNode node = new OperationNode(operation);

//                        final MainProcessor processor = App01.GUI_STATE_DESC.getProcessor();
//                        OperationHolder operationHolder = processor.preProcessAll(descOperation);
//                        OperationNode node = new OperationNode(new SimpleNewPortlet(nextOperationId, parentOperation.getOperationId(), closingWindow.getTitle(), IOperation.TypeOperation.addEventPortlet)
//                        {
//
//                            public Canvas operate(Canvas dragTarget)
//                            {
//
//                                OperationHolder operationHolder = processor.preProcessAll(descOperation);
//                                return processor.operateAll(dragTarget, operationHolder);
//                            }
//                        });
                        setTreeDs(treeGrid.getDataSource(), node);
                        closingWindow.close();  //??????, ???? ???????????? ????????????, ?????????????????????? ???????? ???? ????????????????, TODO ???????????? ???????? ???????????????????? ?????? ?????????????????? ??????????????
                    }
                }
                event.cancel();
            }

        });


        treeGrid.addFolderDropHandler(new FolderDropHandler() {
            @Override
            public void onFolderDrop(FolderDropEvent event) {
                TreeNode folderNode = event.getFolder();
                TreeNode[] nodes=event.getNodes();
                for (TreeNode node : nodes) {
                    if (node instanceof OperationNode)
                    {
                        Object objOperation = OperationNode.getOperation(node);
                        if (objOperation instanceof IOperation)
                            setPath2Operation(folderNode, (IOperation) objOperation, treeGrid.getTree());
                    }
                }
            }
        });

        treeGrid.addDragStopHandler(new DragStopHandler()
        {
            @Override
            public void onDragStop(DragStopEvent event)
            {
                 Canvas dragTarget = EventHandler.getDragTarget();
                 TreeNode folderNode = treeGrid.getDropFolder();
                 if (dragTarget instanceof TreeGrid)
                 {
                     Record[] dragData = ((TreeGrid) dragTarget).getDragData();
                     for (Record record : dragData)
                     {
                         Object objectOperation=OperationNode.getOperation(record);
                         if (objectOperation instanceof IOperation)
                             setPath2Operation(folderNode, (IOperation) objectOperation, treeGrid.getTree());
                     }
                 }
            }
        });

        final HandlerRegistration[] regstr=new HandlerRegistration[1];
        regstr[0] = treeGrid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
            @Override
            public void onRemoveRecordClick(RemoveRecordClickEvent event) {
                int rowNum = event.getRowNum();
                Record node = treeGrid.getRecord(rowNum);
                DataSource ds = treeGrid.getDataSource();
//                RecordList rl = new RecordList(ds.getCacheData());
//                while (rl.remove(node)) ;
//                ds.setCacheData(rl.toArray());
//                regstr[0].removeHandler();//?????? ???????? ????????????????
            }
        });


//        treeGrid.addRemoveRecordClickHandler(new RemoveRecordClickHandler() {
//            @Override
//            public void onRemoveRecordClick(RemoveRecordClickEvent event)
//            {
//                int rowNum=event.getRowNum();
//                Record node=treeGrid.getRecord(rowNum);
//                if (node instanceof OperationNode)
//                {
//                    Object objOperation = OperationNode.removerOperation(node);
//                    if (objOperation instanceof IOperation)
//                    {
//                        Tree tree = treeGrid.getTree();
//                        setPath2Operation(tree.getParent((OperationNode) node), (IOperation) objOperation, tree);//?????? ???????????????????? ?????? ???????? ???????????????????????????? ??????????????, ?????????????? ???????????????????? ???? ???????????? ???? ????????????
//                    }
//                }
//            }
//        });

/*
        treeGrid.addNodeClickHandler(new NodeClickHandler() {
            @Override
            public void onNodeClick(NodeClickEvent event) {
                Object obj = OperationNode.getOperation(event.getNode());
                if (obj instanceof SimpleOperation && ((SimpleOperation) obj).getOperationId() == 3151) {
                    String url = GWT.getHostPageBaseURL() + "desktop/Informers32.exe";
                    com.google.gwt.user.client.Window.open(url, null, null);
                } else if (obj instanceof SimpleOperation && ((SimpleOperation) obj).getOperationId() == 3152) {
                    String url = GWT.getHostPageBaseURL() + "desktop/Informers64.exe";
                    com.google.gwt.user.client.Window.open(url, null, null);
                }
            }
        });
*/

        App01.GUI_STATE_DESC.setMainTreeGrid(treeGrid);
        return treeGrid;
    }


//    public static List<MyHeaderControl> getHeaderCtrls(Window window)
//    {
//        HLayout header = window.getHeader();
//        Canvas[] headerCtrl = header.getMembers();
//        final List<MyHeaderControl> myHeaderControls= new LinkedList<MyHeaderControl>();
//        for (Canvas canvas : headerCtrl)
//            if (canvas instanceof MyHeaderControl)
//            {
//                MyHeaderControl myHeaderControl = (MyHeaderControl) canvas;
//                myHeaderControls.add(myHeaderControl);
//                header.removeMember(myHeaderControl);
//                myHeaderControl.setTarget(null);
//            }
//        return myHeaderControls;
//    }

    public static void addHeaderCtrl(Window window, Canvas pinUp) {
        if (pinUp != null)
        {
            addHeaderCtrl(window,new Canvas[]{pinUp});
//            final HLayout header = window.getHeader();
//            LinkedList<Canvas> headerControls = new LinkedList<Canvas>(Arrays.asList(header.getMembers()));
//            if (headerControls.size() >= 1)
//                headerControls.add(1, pinUp);
//            else
//                headerControls.add(pinUp);
//            header.setMembers(headerControls.toArray(new Canvas[headerControls.size()]));
        }
    }


    public static void addHeaderCtrl(Window window, Canvas[] pinUp) {
        if (pinUp != null && pinUp.length>0)
        {
            final HLayout header = window.getHeader();
            LinkedList<Canvas> headerControls = new LinkedList<Canvas>(Arrays.asList(header.getMembers()));
            if (headerControls.size() >= 1)
                headerControls.addAll(1, Arrays.asList(pinUp));
            else
                headerControls.addAll(Arrays.asList(pinUp));
            header.setMembers(headerControls.toArray(new Canvas[headerControls.size()]));
        }
    }



    public static void removeHeaderCtrl(Window window, HeaderControl pinUp)
    {
        HLayout header = window.getHeader();
        List<Canvas> headerControls = new LinkedList<Canvas>(Arrays.asList(header.getMembers()));
        headerControls.remove(pinUp);
        header.setMembers(headerControls.toArray(new Canvas[headerControls.size()]));
    }

    public static void removeHeaderCtrl(Window window, HeaderControl[] pinUps)
    {
        HLayout header = window.getHeader();
        LinkedList<Canvas> headerControls = new LinkedList<Canvas>(Arrays.asList(header.getMembers()));

        for (HeaderControl pinUp : pinUps)
            headerControls.remove(pinUp);
        header.setMembers(headerControls.toArray(new Canvas[headerControls.size()]));
    }


    public static void setPath2Operation(TreeNode folderNode, IOperation objOperation, Tree tree)
    {
        String nameP=tree.getNameProperty();
        try {

            tree.setNameProperty("Name");
            String path= tree.getPath(folderNode);
            objOperation.setFolderName(path);
        }
        finally
        {
            tree.setNameProperty(nameP);
        }
    }

    public static void setTreeDs(DataSource ds, TreeNode node)
    {
        int ix=new RecordList(ds.getTestData()).findIndex(OperationNode.OPERATION_ID,node.getAttributeAsInt(OperationNode.OPERATION_ID));
        if (ix<0)
        {
            LinkedList<OperationNode> dn=new LinkedList(Arrays.asList(ds.getCacheData()));

            OperationNode operationNode = (OperationNode) node;
            operationNode.setBuildIn(false);

            dn.add(operationNode);
            ds.setCacheData(dn.toArray(new OperationNode[dn.size()]));

    //        if (ix<0)
    //        ds.addData(node); //TODO ?????? ?????????????? ???????????????? ????????????
    //        else
    //        {
    //          int i=0;
    //          i = i+1;
    //        }
        }
        else
        {
          int i=0;
          i = i+1;
        }
        ds.updateData(node);
    }


    public static TreeNode[] getNodesData()
    {
        int parentWayId=41;
        TreeNode[] _rv=_getNodesData(parentWayId);
        List<TreeNode> rvl = new LinkedList<TreeNode>(Arrays.asList(_rv));
        rvl.addAll(NSI.createAddDataNode(parentWayId));
        return rvl.toArray(new TreeNode[rvl.size()]);
    }

    public static TreeNode[] _getNodesData(int parentWayId)
    {

        return new TreeNode[]
                {

                        new OperationNode(new SimpleOperation(31, 1, AppConst.TOOL_TREE_NAME, IOperation.TypeOperation.NON)),
                            new OperationNode(new SimpleOperation(314, 31, "??????????????", IOperation.TypeOperation.NON)),
                                new OperationNode
                                (
                                        new ExcelExportOperation(321, 314, "Excel", IOperation.TypeOperation.addExcelExport)
                                ),

//                            new OperationNode
//                                    (
//                                            new SimpleOperation(315, 31, "?????????????????? ?????? ???????????????? ??????????", IOperation.TypeOperation.NON) {
//
//                                            }
//                                    )
//                            {
//
//
//                                @Override
//                                public boolean isBuildIn() {
//                                    return true;
//                                }
//
//                                @Override
//                                public Boolean getCanDrag() {
//                                    return false;
//                                }
//
//
//                            },
//                            new OperationNode
//                                    (
//                                            new SimpleOperation(3151, 315, "Windows 32 ????????", IOperation.TypeOperation.NON) {
//
//                                            }
//                                    ) {
//
//
//                                @Override
//                                public boolean isBuildIn() {
//                                    return true;
//                                }
//
//                                @Override
//                                public Boolean getCanDrag() {
//                                    return false;
//                                }
//
//
//                            },
//                            new OperationNode
//                                    (
//                                            new SimpleOperation(3152, 315, "Windows 64 ????????", IOperation.TypeOperation.NON) {
//
//                                            }
//                                    ) {
//
//
//                                @Override
//                                public boolean isBuildIn() {
//                                    return true;
//                                }
//
//                                @Override
//                                public Boolean getCanDrag() {
//                                    return false;
//                                }
//
//
//                            },
                            new OperationNode
                            (
                                    new SimpleOperation(311, 31, AppConst.TOOL_TREE_MENU, IOperation.TypeOperation.addMenu) {
                                        public Canvas operate(Canvas dragTarget, IOperationContext ctx) {
                                            return null;
                                        }
                                    }
                            ),
                            new OperationNode
                            (
                                    new SimpleOperation(312, 31, AppConst.TOOL_TREE_PAGE, IOperation.TypeOperation.addPage) {
                                        public Canvas operate(Canvas dragTarget, IOperationContext ctx)
                                        {
                                            return null;
                                        }
                                    }
                            ),


                        new OperationNode
                        (
                                new SimpleOperation(313, 31, AppConst.TOOL_TREE_NODE, IOperation.TypeOperation.addNode) {
                                    public Canvas operate(Canvas dragTarget, IOperationContext ctx) {
                                        return null;
                                    }
                                }
                        ),

                        //TODO ???????????????????????? ???????? ?????????? ???????????? ???? ?????????????? ???????????????? ???????????? ?????????? ???????? ?????????????? ?????????????????????????? ?? ?????????? ???????????? ?????????????????????? ?? ???? ????????????????
                        //TODO ?? ?????????????????? ???????????? ?????? ?????? ?????????? ???????????????? ???????????? ?? ?????????????????????? ?????? ???????????????????? ???? ???????????????? ????????????????????????
                        new OperationNode(new SimpleOperation(11, 1, "??????????????", IOperation.TypeOperation.NON)),
                            new OperationNode(new SimpleOperation(111, 11, "????????????", IOperation.TypeOperation.NON)),
                                new OperationNode
                                (
                                    new CreatePlacesTable(11131, 111, "?????????????? ??????????????????", IOperation.TypeOperation.addEventPortlet)
                                ),
                                new OperationNode
                                (
                                    new CreateReportTable(1111, 111, "???? ????????????????????????", IOperation.TypeOperation.addEventPortlet)
                                ),

                                new OperationNode
                                (
                                    new CreateDelayReportTable(1114, 111, "???????????????? ??????????????", IOperation.TypeOperation.addEventPortlet)
                                ),

                                new OperationNode
                                (
                                    new CreateRef12Table(1118, 111, "??????(?? ??????. ??????????)", IOperation.TypeOperation.addEventPortlet)
                                ),

                                new OperationNode
                                (
                                    new CreateWarnVTable(1120, 111, "????????????.??????????????. ???? ????????.", IOperation.TypeOperation.addEventPortlet)
                                ),

                                new OperationNode
                                (
                                    new CreateWinRepTable(1124, 111, "?????????? ?? ???????????????????? ????????", IOperation.TypeOperation.addEventPortlet)
                                ),

                                new OperationNode
                                (
                                    new CreateWinPlanTable(1126, 111, "???????? ???????????????????? ????????", IOperation.TypeOperation.addEventPortlet)
                                ),

                                new OperationNode
                                (
                                    new CreateWarnACTTable(1128, 111, "????????. ?? ?????????????????????????? ????????.", IOperation.TypeOperation.addEventPortlet)
                                ),

                                new OperationNode
                                (
                                    new CreateWarnAGRReportTable(1132, 111, "????????????.??????????????./??????????.????????.", IOperation.TypeOperation.addEventPortlet)
                                ),
                                new OperationNode
                                (
                                    new CreateOIndxTable(1130, 111, "?????????????????????? ???????????????????? ????", IOperation.TypeOperation.addEventPortlet)
                                ),
                                new OperationNode
                                (
                                    new CreateRSMReportTable(1134, 111, "?????????????????? ??????. ??????????????", IOperation.TypeOperation.addEventPortlet)
                                ),

                                new OperationNode
                                (
                                    new CreateLOCReportTable(1136, 111, "???????????????? ???????????? ??????????????????????", IOperation.TypeOperation.addEventPortlet)
                                ),


                            new OperationNode(new SimpleOperation(121, 11, "??????????????????", IOperation.TypeOperation.NON)),
                                new OperationNode
                                (
                                    new CreateChatView(1121, 121, "?????????????? ???? ????????????????????????", IOperation.TypeOperation.addChart, TablesTypes.STATEDESC)
                                ),

                            new OperationNode(new SimpleOperation(131, 11, "??????????????????", IOperation.TypeOperation.NON)),
                                new OperationNode
                                (
                                    new NewWarnInformer(1131, 131, "????????????????????????????",IOperation.TypeOperation.addInformer)
                                ),
                                new OperationNode
                                (
                                    new NewRefInformer(1141, 131, "???????????? ??????.??????????????",IOperation.TypeOperation.addInformer)
                                ),
                                new OperationNode
                                (
                                    new NewViolInformer(1151, 131, "?????????????????????????????? ??????????????????",IOperation.TypeOperation.addInformer)
                                ),
                                new OperationNode
                                (
                                    new NewDelayInformer(1161, 131, "???????????????? ??????????????",IOperation.TypeOperation.addInformer)
                                ),

                                new OperationNode
                                (
                                    new NewWarnInformer2(1133, 131, "????????????????????????????(????)",IOperation.TypeOperation.addInformer)
                                ),

                                new OperationNode
                                (
                                    new NewRefInformer2(1143, 131, "???????????? ??????.??????????????(????)",IOperation.TypeOperation.addInformer)
                                ),

                                new OperationNode
                                (
                                    new NewViolInformer2(1153, 131, "?????????????????????????????? ??????????????????(????)",IOperation.TypeOperation.addInformer)
                                ),
                                new OperationNode
                                (
                                    new NewDelayInformer2(1163, 131, "???????????????? ??????????????(????)",IOperation.TypeOperation.addInformer)
                                ),
                        new OperationNode
                                (
                                        new NewWarnInformer3(1164, 131, "????????????????????????????(????)2", IOperation.TypeOperation.addInformer)
                                ),
                        new OperationNode
                                (
                                        new NewRefInformer3(1165, 131, "???????????? ??????.??????????????(????)2", IOperation.TypeOperation.addInformer)
                                ),
                        new OperationNode
                                (
                                        new NewViolInformer3(1166, 131, "?????????????????????????????? ??????????????????(????)2", IOperation.TypeOperation.addInformer)
                                ),
                        new OperationNode
                                (
                                        new NewDelayInformer3(1167, 131, "???????????????? ??????????????(????)2", IOperation.TypeOperation.addInformer)
                                ),
                        new OperationNode(new SimpleOperation(170, 11, "??????????????", IOperation.TypeOperation.NON)),
                            new OperationNode
                            (
                                new CreateEventTableH(1701, 170, "????????(H)", IOperation.TypeOperation.addEventPortlet,TablesTypes.WINDOWS)
                            ),
                            new OperationNode
                            (
                                new CreateEventTableH(1711, 170, "????????????????????????????(H)", IOperation.TypeOperation.addEventPortlet,TablesTypes.WARNINGS)
                            ),


                        new OperationNode
                        (
                            new CreateChartByTable(108, 11, "????????????", IOperation.TypeOperation.addChartByTable)
                        ),

                        new OperationNode
                        (
                            new ExtendGridByPlaces(109, 11, "?????????? ??????????????", IOperation.TypeOperation.extendGridByPlace)
                        ),

                        new OperationNode
                        (
                            new CreateEventTable(112, 11, "????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.WINDOWS)
                        ),

//                        new OperationNode
//                        (
//                            new CreateEventTable(1112, 11, "?????????????? ????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.WINDOWS_CURR)
//                        ),

//                        new OperationNode
//                        (
//                            new CreateEventTable(1122, 11, "?????????????????????????? ????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.WINDOWS_OVERTIME)
//                        ),

                        new OperationNode
                        (
                            new CreateEventTable(113, 11, "????????????????????????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.WARNINGS)
                        ),

//                        new OperationNode
//                        (
//                                new CreateEventTable(117, 11, "????????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.BABKEN_TYPE)
//                        ),

                        new OperationNode
                        (
                            new CreateEventTable(120, 11, "???????????? ??????.??????????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.REFUSES)
                        ),

                        new OperationNode
                        (
                            new CreateEventTable(125, 11, "?????????????????????????????? ??????????????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.VIOLATIONS)
                        ),

                        new OperationNode
                        (
                            new CreateEventTable(130, 11, "?????????????? ??????", IOperation.TypeOperation.addEventPortlet,TablesTypes.VIP_GID)
                        ),

                        new OperationNode
                        (
                            new CreateEventTable(135, 11, "???????????????? ??????", IOperation.TypeOperation.addEventPortlet,TablesTypes.DELAYS_GID)
                            {
                                {
                                    isMultiGroup=true;
                                }
                                protected String getNameOperation(IOperation operation)
                                {
                                    if (operation==this)
                                        return CreateEventTable.class.getName();
                                    else
                                        return super.getNameOperation(operation);
                                }

                            }
                        ),

                        new OperationNode
                        (
                            new CreateEventTable(137, 11, "???? ????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.DELAYS_ABVGD)
                            {
                                {
                                    isMultiGroup=true;
                                }
                                protected String getNameOperation(IOperation operation)
                                {
                                    if (operation==this)
                                        return CreateEventTable.class.getName();
                                    else
                                        return super.getNameOperation(operation);
                                }

                            }
                        ),


                        new OperationNode
                        (
                            new CreateEventTable(140, 11, "??????", IOperation.TypeOperation.addEventPortlet,TablesTypes.KMOTABLE)
                        ),

                        new OperationNode
                        (
                                new CreateEventTable(160, 11, "?????????????????? ??????????????????", IOperation.TypeOperation.addEventPortlet, TablesTypes.ZMTABLE)
                        ),

                        new OperationNode
                        (
                            new CreateEventTable(145, 11, "?????????????????? ????????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.LOST_TRAIN)
                        ),

                        new OperationNode
                                (
                                        new CreateVAGTORTable(147, 11, "???????????? ?? ??????", IOperation.TypeOperation.addEventPortlet, TablesTypes.VAGTOR)
                                ),

                        new OperationNode
                        (
                            new CreateLentaTable(150, 11, "??????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.LENTA)
                        ),

                        new OperationNode
                        (
                            new CreateEventTable(180, 11, "????????????????", IOperation.TypeOperation.addEventPortlet,TablesTypes.UMESSAGE)
                        ),



                        new OperationNode(new SimpleOperation(1000, 1, "?????????? ????????????", IOperation.TypeOperation.NON)),
                        //new OperationNode(new SimpleOperation(1100, 1000, "??????????????", IOperation.TypeOperation.NON)),
                        new OperationNode(new CreateStyleOperation(1110, 1000, "?????????????? ??????????", IOperation.TypeOperation.addStyleToTable)),

                        new OperationNode(new SimpleOperation(parentWayId, 1, "????????????", IOperation.TypeOperation.NON)),
                        new OperationNode(new SimpleOperation(21, 1, "??????????????", IOperation.TypeOperation.NON)),
                            new OperationNode(
                                    new IOperationFactory()
                                    {
                                        @Override
                                        public IOperation getOperation()
                                        {
                                            return new NewFilterOperation(211, 21, "?????????? ????????????", IOperation.TypeOperation.addClientFilter);
                                        }
                                    }
                            ),
                            new OperationNode(
                                    new IOperationFactory()
                                    {
                                        @Override
                                        public IOperation getOperation()
                                        {
                                            return new NewGroupFilterOperation(221, 21, "?????????????????? ????????????", IOperation.TypeOperation.addClientFilter);
                                        }
                                    }
                            ),
                            new OperationNode(
                                    new IOperationFactory()
                                    {
                                        @Override
                                        public IOperation getOperation()
                                        {
                                            return new NewPolgFilterOperation(231, 21, "???????????? ???? ???????????? ??????????????", IOperation.TypeOperation.addClientFilter);
                                        }
                                    }
                            ),

                        new OperationNode(new SimpleOperation(876, 1, "??????", IOperation.TypeOperation.NON)),
                        new OperationNode(new GISSelectEventOperation(987,876, "???????????????????????????????? ???? ??????????????", IOperation.TypeOperation.addGISEventPositioning))
                };


    }

}
