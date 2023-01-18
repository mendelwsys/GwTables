package com.mycompany.client.test.TestReports;

import com.google.gwt.core.client.GWT;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.apps.App.reps.IReportCreator;
import com.mycompany.client.apps.App.reps.PlacesGrid;
import com.mycompany.client.test.Demo.EventTileMetaFactory;
import com.mycompany.client.test.reps.InfraGrid_OT;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.types.ListGridComponent;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.grid.HeaderSpan;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.03.15
 * Time: 18:25
 * Просто тест для того что бы помотреть грид отчетов, добавлена возможность просмотра дерева спанов заголовка
 */
public class RepTest implements Runnable{
    @Override
    public void run()
    {
        com.google.gwt.user.client.Window.setTitle("Test Reports Table (TD07)");
        GWT.create(EventTileMetaFactory.class);

        HLayout mainLayout = new HLayout();
        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();

        final Window winModal = new Window();
        winModal.setWidth(1120);
        winModal.setHeight(560);
        winModal.setCanDragResize(true);
        winModal.setCanDragReposition(true);

        winModal.setTitle("Тест");
        winModal.centerInPage();
        winModal.addCloseClickHandler(new CloseClickHandler()
        {
            public void onCloseClick(CloseClickEvent event)
            {
                winModal.destroy();
            }
        });


        ListGridWithDesc newGrid = new ListGridWithDesc()
        {

            private TreeNode[] getNodes(HeaderSpan rootSpan) {

                HeaderSpan[] spans = rootSpan.getSpans();
                List<TreeNode> ll = new LinkedList<TreeNode>();
                for (HeaderSpan span : spans)
                {
                    TreeNode node = new TreeNode(span.getName());
//                    node.setIsFolder(true);
                    node.setTitle(span.getTitle());
                    final TreeNode[] nodes = getNodes(span);
                    if (nodes!=null && nodes.length>0)
                       node.setChildren(nodes);
                    ll.add(node);
                }
                return ll.toArray(new TreeNode[ll.size()]);
            }

            HeaderSpan rootSpan = new HeaderSpan();
            public void setHeaderSpans(HeaderSpan... headerSpans)
            {
                super.setHeaderSpans(headerSpans);
                rootSpan.setSpans(headerSpans);
            }
             protected MenuItem[] getHeaderContextMenuItems(final Integer fieldNum) {
                 MenuItem[] rv = super.getHeaderContextMenuItems(fieldNum);

                 MenuItem[] rrv=new MenuItem[rv.length+1];
                 for (int i = 0, rvLength = rv.length; i < rvLength; i++) {
                     rrv[i]= rv[i];
                 }
                 rrv[rv.length]=new MenuItem("Заголовок"); //TODO Здесь планировался редактор заголовков, что бы потом заголовки можно было отображать в гриде
                 rrv[rv.length].addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler()
                 {
                     @Override
                     public void onClick(MenuItemClickEvent event) {
                         Window win = OptionsViewers.createEmptyWindow("Дерево заголовка");
                         win.setHeight(500);

                        ToolStrip gridEditControls = new ToolStrip();
                        gridEditControls.setWidth100();
                        gridEditControls.setHeight(24);

                        Label totalsLabel = new Label();
                        totalsLabel.setPadding(5);

                        LayoutSpacer spacer = new LayoutSpacer();
                        spacer.setWidth("*");

                        ToolStripButton editButton = new ToolStripButton();
                        editButton.setIcon("[SKIN]/actions/edit.png");
                        editButton.setPrompt("Edit selected record");
                        editButton.addClickHandler(new ClickHandler() {

                            @Override
                            public void onClick(ClickEvent event) {

                            }
                        });

                        ToolStripButton removeButton = new ToolStripButton();
                        removeButton.setIcon("[SKIN]/actions/remove.png");
                        removeButton.setPrompt("Remove selected record");
                        removeButton.addClickHandler(new ClickHandler() {

                            @Override
                            public void onClick(ClickEvent event) {
                            }
                        });

                        gridEditControls.setMembers(totalsLabel, spacer, editButton, removeButton);


                         TreeNode[] nodes = getNodes(rootSpan);
                         TreeNode root = new TreeNode(rootSpan.getName());
                         root.setTitle("R");
                         root.setChildren(nodes);

                         Tree tree = new Tree();
                         tree.setModelType(TreeModelType.CHILDREN);
                         tree.setRoot(root);

                         final TreeGrid treeGrid = new TreeGrid()
                         {
//                               @Override
//                              public Boolean willAcceptDrop(){
//                                  return new Boolean(true);
//                              }
                         };
                         treeGrid.setData(tree);
                         treeGrid.setWidth100();
                         treeGrid.setHeight100();


                         treeGrid.setCanAcceptDrop(true);
                         treeGrid.setCanAcceptDroppedRecords(true);
                         treeGrid.setCanEdit(true);
                         treeGrid.setCanRemoveRecords(true);
                         treeGrid.setCanReparentNodes(true);
                         treeGrid.setCanReorderRecords(true);


                         treeGrid.setGridComponents(new Object[] {
                            ListGridComponent.HEADER,
                            ListGridComponent.BODY,
                            gridEditControls
                        });


                         win.addItem(treeGrid);
                         win.show();
                     }
                 });

                 return rrv;
             }
        };
        //new DelayGrid(NodesHolder.gridMetaProvider).setGrid(winModal, newGrid);//TODO Здесь поставляется грид отчетов
//        new DelayGrid_OT(NodesHolder.gridMetaProvider).setGrid(winModal, newGrid);//TODO Здесь поставляется грид отчетов
//        new InfraGrid_OT(NodesHolder.gridMetaProvider).setGrid(winModal, newGrid);

        final IReportCreator placesGrid = new PlacesGrid(NodesHolder.gridMetaProvider);
        placesGrid.setGrid(winModal, newGrid);

        mainLayout.addChild(winModal);



       mainLayout.draw();

    }
}
