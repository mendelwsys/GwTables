package com.mycompany.client.test.leaks;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.*;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.apps.App.VAGTORGridConstructor;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.updaters.DataDSCallback;
import com.mycompany.client.utils.*;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.06.15
 * Time: 19:00
 * Тестирование получения данных с сервера через стандартный провайдер (тестируется консистентонсть данных)
 */
public class CheckByGroups
        implements TestBuilder,Runnable
{


    private native int getDocLength() /*-{
	return $doc.getElementsByTagName('*').length;
}-*/;


    private native JavaScriptObject getGlobalVariables() /*-{
	var keys = $wnd.isc.getKeys ( $wnd );
	return keys;
}-*/;

    private native JavaScriptObject getElementById(String id) /*-{
	return document.getElementById(id);
}-*/;



    private native JavaScriptObject getValueByKey(String key) /*-{
	    return $wnd[key];
}-*/;

    private native JavaScriptObject getValueByKey2(JavaScriptObject obj,String key) /*-{
	    return obj[key];
}-*/;

 private native void Collect() /*-{
	   $wnd.CollectGarbage()
}-*/;

    private native void setNullValueByKey(String key) /*-{
	    $wnd[key]=null;
}-*/;


    private native void delVariable(JavaScriptObject obj) /*-{
        delete obj;
}-*/;

    @Override
    public void run() {

        final HLayout mainLayout = new HLayout();
        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);

        this.setComponents(mainLayout);
        mainLayout.draw();
    }


    TextAreaItem textAreaItem;

    @Override
    public void setComponents(Layout mainLayout)
    {

        final String tblType = TablesTypes.VAGTOR;

        final DynamicForm form = OptionsViewers.createEmptyForm();
        form.setLayoutAlign(VerticalAlignment.TOP);
        textAreaItem= new TextAreaItem();
        textAreaItem.setWidth("100%");
        textAreaItem.setTitle("");
        form.setFields(textAreaItem);

        Portlet portlet0 = new Portlet();
        portlet0.addItem(form);
        portlet0.setHeight("20%");
        portlet0.setTitle("Статус теста");

        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();
        portalLayout.setShowColumnMenus(false);

        portalLayout.addPortlet(portlet0);


        final String dataURL = "transport/tdata2";
        final String headerURL = "theadDesc.jsp";
        {
            Portlet portlet = new Portlet();

            Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);
            setDorCriteria(criteria);



            final ListGridWithDesc grid;

//            if (!TablesTypes.VAGTOR.equals(tblType))
            {
                grid = GridUtils.createGridTable(new BGridConstructor(),new GridUtils.DefaultGridFactory()
                        {
                            public ListGridWithDesc createGrid()
                            {
                                ListGridWithDesc _grid = super.createGrid();
                                _grid.setDescOperation(new DescOperation());

                                _grid.setShowGroupSummaryInHeader(false);
                                _grid.setGroupTitleField(TablesTypes.DOR_NAME);
                                _grid.setGroupStartOpen(GroupStartOpen.NONE);
                                _grid.setShowGroupTitleColumn(false);
                                _grid.setGroupByMaxRecords(30000);
                                _grid.setShowGroupSummary(false);


                                return _grid;

                            }

                        },new GridMetaProviderBase(), criteria, headerURL, dataURL, true,false);
            }
//            else
//            {
//                grid = GridUtils.createGridTable(new VAGTORGridConstructor(),new GridUtils.DefaultGridFactory()
//                        {
//                            public ListGridWithDesc createGrid()
//                            {
//                                ListGridWithDesc _grid = super.createGrid();
//                                _grid.setDescOperation(new DescOperation());
//
//                                _grid.setShowGroupSummaryInHeader(true);
//                                _grid.setGroupTitleField(TablesTypes.DOR_NAME);
//                                _grid.setGroupStartOpen(GroupStartOpen.NONE);
//                                _grid.setShowGroupTitleColumn(false);
//                                _grid.setGroupByMaxRecords(30000);
////                                _grid.setGroupByAsyncThreshold(30000);
//
//                                _grid.setShowGroupSummary(true);
//                                return _grid;
//                            }
//
//                        },new GridMetaProviderBase(), criteria, headerURL, dataURL, true,false);
//            }





            grid.getCtrl().addAfterUpdater(new DSCallback()
            {
                @Override
                public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
                {

                    if (groupByFields==null)
                        groupByFields = new String[]{TablesTypes.DOR_NAME};

                    grid.getCtrl().stopUpdateData();

                    new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation() {

                        @Override
                        public boolean operate() {
                            checkGrid(grid);
                            return clix>400;
                        }
                    },1500);

                }
            }
            );

            grid.setWidth100();
            grid.setHeight100();





            portlet.addItem(grid);
            portalLayout.addPortlet(portlet);

        }

//

        mainLayout.addMembers(portalLayout);
    }

    int clix = 1;
    String[] groupByFields;

//    List<Tree> ll= new LinkedList<Tree>();

    void checkGrid(ListGridWithDesc grid)
    {


        JavaScriptObject rres = getGlobalVariables();
        Object [] keys = JSOHelper.convertToArray(rres);
        textAreaItem.setValue("Check  status:  data groups " + clix+ " ksz:"+keys.length+" docLn;"+getDocLength());

        if (false)
        {
            if (clix%2==0)
            {


                Tree tree=grid.getGroupTree();
    //                        RecordList rl = grid.getCacheData();

                grid.ungroup();

    //                        Set<Object>  rr=new HashSet<Object>();
    //                        int ln=rl.getLength();
    //                        for (int i=0;i<ln;i++)
    //                        {
    //                            Record r=rl.get(i);
    //                            rr.add(r.getJsObj());
    //                        }

    //                        Set<Object>  rrN=new HashSet<Object>();

    //                    for (TreeNode node : nodes)
    //                    {
    //                            JavaScriptObject obj1 = (JavaScriptObject) node.getAttributeAsObject("_groupTree_isc_GridUtils_DefaultGridFactory$1_0");
    //                            if (obj1!=null)
    //                                node.setAttribute("_groupTree_isc_GridUtils_DefaultGridFactory$1_0",(Object)null);
    //                    }
                String id = tree.getID();
                JavaScriptObject rv=null;
                if (id!=null)
                    rv = getValueByKey(id);

                TreeNode[] nodes;
                while((nodes = tree.getAllNodes())!=null && nodes.length>0)
                    cleanTree(tree, nodes);

                if (rv!=null)
                {
                    delVariable(rv);
                    setNullValueByKey(id);
                }





    //                        final RecordList cacheData = grid.getCacheData();
    //                        grid.setData(new RecordList(new Record[0]));
    //                        grid.setCacheData(new RecordList(new Record[0]));
    //                        cacheData.destroy();
    //                        grid.getCtrl().setFullDataUpdate();

            }
            else if (!grid.isGrouped())
            {
                grid.groupBy(groupByFields);
            }
        }

//        Tree tree=grid.getGroupTree();
        grid.groupBy(groupByFields);
//        if (tree!=null && tree!=grid.getGroupTree())
        {
//            JavaScriptObject ns = tree.getAttributeAsJavaScriptObject("ns");
//            if (ns!=null)
//            {
//                JSOHelper.setAttribute(tree.getJsObj(),"ns",(JavaScriptObject)null);
//            }
//            TreeNode[] nodes;
//            while((nodes = tree.getAllNodes())!=null && nodes.length>0)
//                cleanTree(tree, nodes);
        }

//        if (clix==2)
//            grid.setNullRedraw();

//                    if (clix>50)
//                    {
//                        grid.getCtrl().stopUpdateData();
//                        RecordList rl = grid.getCacheData();
//                        grid.setCacheData(new RecordList());
//                        grid.setData(new RecordList());
//                        rl.destroy();
//                        grid.hide();
//                        grid.markForDestroy();
//                    }

//                    if (ix % 5 == 0 )//|| (res != null && data1 != null && data1.length > 0))
//                    {
//                        grid.getCtrl().stopUpdateData();
//                        ix++;
//                        checkCons++;
//                    } else {
//                        if (data1 != null && data1.length > 0 || res != null)
//                            ix++;
//                    }
//                    SC.say(com.smartgwt.client.Version.getVersion());

        clix++;
    }

    private void _cleanTree(Tree tree, TreeNode[] nodes) {




        tree.removeList(nodes);
        for (TreeNode node : nodes)
        {
                JavaScriptObject obj = (JavaScriptObject) node.getAttributeAsObject("groupMembers");
                if (obj!=null && JSOHelper.isArray(obj))
                {
                    int ln1=JSOHelper.getArrayLength(obj);
                    for (int i1=0;i1<ln1;i1++)
                        JSOHelper.setArrayValue(obj,i1,(Object)null);
                    node.setAttribute("groupMembers",new Object[0]);
                    delVariable(obj);
                }

                JavaScriptObject obj1 = (JavaScriptObject) node.getAttributeAsObject("_groupTree_isc_GridUtils_DefaultGridFactory$1_0");
                if (obj1!=null)
                {
                    node.setAttribute("_groupTree_isc_GridUtils_DefaultGridFactory$1_0",JavaScriptObject.createObject());
                }
        }
        tree.destroy();
    }


    private void __cleanTree(Tree tree, TreeNode[] nodes) {
        tree.removeList(nodes);
        for (TreeNode node : nodes)
        {
                JavaScriptObject obj = (JavaScriptObject) node.getAttributeAsObject("groupMembers");
                if (JSOHelper.isArray(obj))
                {
                    int ln1=JSOHelper.getArrayLength(obj);
                    for (int i1=0;i1<ln1;i1++)
                        JSOHelper.setArrayValue(obj,i1,(Object)null);
                    node.setAttribute("groupMembers",new Object[0]);
                }
                else  if (obj!=null)
                {
                    node.setAttribute("groupMembers",(Object)null);
                }
                if (obj!=null)
                    delVariable(obj);


                JavaScriptObject obj1 = (JavaScriptObject) node.getAttributeAsObject("_groupTree_isc_GridUtils_DefaultGridFactory$1_0");
                if (obj1!=null)
                    node.setAttribute("_groupTree_isc_GridUtils_DefaultGridFactory$1_0",(Object)null);

               delVariable(node.getJsObj());



        }
        tree.destroy();
    }


    private void cleanTree(Tree tree, TreeNode[] nodes) {

        for (TreeNode node : nodes)
        {
            TreeNode[] subnodes = tree.getChildren(node);
            if (subnodes==null || subnodes.length==0)
            {
                tree.remove(node);
                JavaScriptObject obj1 = (JavaScriptObject) node.getAttributeAsObject("_groupTree_isc_GridUtils_DefaultGridFactory$1_0");
                if (obj1!=null)
                    node.setAttribute("_groupTree_isc_GridUtils_DefaultGridFactory$1_0", (Object) null);
                delVariable(node.getJsObj());
            }
            else
            {
                cleanTree(tree, subnodes);
                tree.remove(node);

                JavaScriptObject obj1 = (JavaScriptObject) node.getAttributeAsObject("_groupTree_isc_GridUtils_DefaultGridFactory$1_0");
                if (obj1!=null)
                    node.setAttribute("_groupTree_isc_GridUtils_DefaultGridFactory$1_0",(Object)null);

                delVariable(node.getJsObj());
            }
        }
    }



    private void setDorCriteria(Criteria criteria)
    {
//        IServerFilter serverFilter = new CommonServerFilter(TablesTypes.FILTERDATAEXPR);
//        final AdvancedCriteria serverCriteria = new AdvancedCriteria();
//        serverCriteria.addCriteria(new Criterion(TablesTypes.DOR_CODE, OperatorId.EQUALS, new Integer(1)));
//        serverCriteria.appendToCriterionList(new Criterion(TablesTypes.DOR_CODE, OperatorId.EQUALS, new Integer(10)));
//        serverCriteria.setOperator(OperatorId.OR);
//        serverFilter.setCriteria(serverCriteria);
//        serverFilter.set2Criteria(criteria);
    }

}
