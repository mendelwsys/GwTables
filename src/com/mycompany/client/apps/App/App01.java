package com.mycompany.client.apps.App;

import com.google.gwt.user.client.Window;
import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.apps.toolstrip.MyToolStrip;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.EdgeName;
import com.smartgwt.client.util.DateUtil;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.11.14
 * Time: 17:24
 */
public class App01 implements Runnable {

    static public GUIStateDesc GUI_STATE_DESC = null;

    public static Map<String, List<String>> map;
    public static final String DEF_OPEN_MODE_NAME="defMode";


    public static boolean isDefOpenMode()
    {
        return (map!=null && map.containsKey(DEF_OPEN_MODE_NAME));
    }

    public final static String DEFAULT_TITLE = "Визуализатор данных ("+ TablesTypes.VERSION+")";
    protected Canvas mainLayout;

    int new_load_ix =0;

    @Override
    public void run()
    {
        Window.setTitle(DEFAULT_TITLE);

        DateUtil.setDefaultDisplayTimezone("00:00"); //TODO Используется что бы исключить временную зону, решение
//TODO  будет принято, когда выясниться в какой временной зоне хранится время в БД (Если что -то не так с датой смотри что бы дата соурс устанавливал типы данных
//TODO  перед запросом данных GridMetaProviderBase(строка 82 dataSource.addField(), напомним что датасоурс должен быть еще не создан)

        reRunApp();


    }

    public void reRunApp()
    {
        if (GUI_STATE_DESC == null)
            GUI_STATE_DESC = new GUIStateDesc();
        if (mainLayout!=null)
            mainLayout.markForDestroy();
        mainLayout=initDefaultDescTop();
        mainLayout.draw();
        GUI_STATE_DESC.loadDescTop(this);
        mainLayout.markForRedraw();
    }

    protected HLayout initDefaultDescTop()
    {
        HLayout mainLayout = new HLayout();
        new_load_ix++;

        AppConst.t_MY_ROOT_PANEL = transformCompId(AppConst.t_MY_ROOT_PANEL);

        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);

        {
            GUI_STATE_DESC.setMainLayout(mainLayout);
            MyToolStrip toolStrip = GUI_STATE_DESC.makeToolStrip();
            Img thinStripCtrl = GUI_STATE_DESC.makeThinStripCtrl();


            mainLayout.addMember(toolStrip);
            mainLayout.addMember(thinStripCtrl);

            GUI_STATE_DESC.add2NotSwitchToolsCanvas(GUIStateDesc.systemPage,toolStrip.getMembers());
            GUI_STATE_DESC.add2NotSwitchPageCanvas(GUIStateDesc.systemPage,mainLayout.getMembers());
        }

        {
            VLayout treeViewer = makeDefTreeViewer();
            int newPageIX = MyPortalLayout.createEmptyPage(GUI_STATE_DESC, treeViewer);
            GUI_STATE_DESC.switchPages(newPageIX);
        }

        GUI_STATE_DESC.getToolStrip().addDropHandler(new DropHandler() {
            @Override
            public void onDrop(DropEvent event) {
                Canvas dragTarget = EventHandler.getDragTarget();
                if (dragTarget instanceof TreeGrid) {
                    Record[] dragData = ((ListGrid) dragTarget).getDragData();
                    for (Record record : dragData) {
                        Object operation = record.getAttributeAsObject("Operation");
                        if (operation instanceof IOperation) {
                            IOperation operation1 = (IOperation) operation;
                            if (IOperation.TypeOperation.addEventPortlet.equals(operation1.getTypeOperation())) {
//                                Canvas treeViewer = MyPortalLayout.getTreeView(GUI_STATE_DESC);
//                                final MyPortalLayout newPage = new MyPortalLayout();
//                                int newPageIX = MyPortalLayout.createEmptyPage(GUI_STATE_DESC, newPage,treeViewer);
//                                newPage.addPortlet(); TODO Здесь сразу автомтоматом вставляем нужный портлет
//                                GUI_STATE_DESC.switchPages(newPageIX);
                            } else if (IOperation.TypeOperation.addPage.equals(operation1.getTypeOperation())) {
                                Canvas treeViewer = MyPortalLayout.getTreeView(GUI_STATE_DESC);
                                int newPageIX = MyPortalLayout.createEmptyPage(GUI_STATE_DESC, treeViewer);
                                GUI_STATE_DESC.switchPages(newPageIX);
                            }
                        }
                    }
                }
                event.cancel();
            }
        });
        return mainLayout;
    }

    public void unLoad()
    {
        mainLayout.hide();
        mainLayout.markForDestroy();
        DescOperation chuserProfile = GUI_STATE_DESC.getChangeUserProfile();
        GUI_STATE_DESC = new GUIStateDesc(GUI_STATE_DESC.getCurrentUserId(),GUI_STATE_DESC.getCurrentProfile(),GUI_STATE_DESC.getUser());
        GUI_STATE_DESC.setChangeUserProfile(chuserProfile);
    }

    private VLayout makeDefTreeViewer()
    {
        TreeGrid treeGrid = NodesHolder.buildTree();
        treeGrid.setCanDragRecordsOut(true);
        treeGrid.setAlign(Alignment.CENTER);


        Canvas oldCanvas=Canvas.getById(AppConst.t_TREE_VIEW_INDICATOR);
        if (oldCanvas!=null)
            oldCanvas.markForDestroy();

        final VLayout treeViewer = new VLayout();


        treeViewer.setWidth(250);
        treeViewer.setHeight100();

        treeViewer.setCanDragResize(true);
        treeViewer.setResizeFrom(EdgeName.R);
        treeViewer.addMember(treeGrid);

        AppConst.t_TREE_VIEW_INDICATOR = transformCompId(AppConst.t_TREE_VIEW_INDICATOR);
        treeViewer.setID(AppConst.t_TREE_VIEW_INDICATOR);

        return treeViewer;
    }

    protected String transformCompId(String t_tree_view_indicator) {
        int ix_=t_tree_view_indicator.indexOf("_");
        if (ix_>=0)
            t_tree_view_indicator=t_tree_view_indicator.substring(0,ix_);
        t_tree_view_indicator+="_"+new_load_ix;
        return t_tree_view_indicator;
    }

}
