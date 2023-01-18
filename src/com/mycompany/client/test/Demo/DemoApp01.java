package com.mycompany.client.test.Demo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.mycompany.client.*;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.toolstrip.MyToolStrip;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.IOperationFactory;
import com.mycompany.client.test.t0.TestDataSource;
import com.mycompany.client.updaters.IGridConstructor;
import com.mycompany.client.utils.*;
import com.mycompany.common.DiagramDesc;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.*;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.*;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.*;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.11.14
 * Time: 17:24
 *
 */
public class DemoApp01 implements Runnable
{

//    public static IGridMetaProviderFactory dataProviderFactory = GridMetaProviderFactory.getInstance();
    public static GridMetaProviderExt gridMetaProvider;

    static class GridMetaProviderExt extends GridMetaProviderBase
    {

        public MyDSCallback initGrid(final IGridConstructor gridConstructor,String tblType, String headerURL,String dataURL)
        {
            gridConstructor.getDataBoundComponent().setAutoFetchData(false);//Не удалось скрыть приходится устанавливать этот флаг до того как запросить данные по хидерам
            //Сначала загружаемм опиcание столбцов
            String idHeader = headerURL.replace(".", "_");
            idHeader = idHeader.replace("/", "$");

            String dataId = dataURL.replace(".", "_");
            dataId=dataId.replace("/", "$");


            DataSource headerSource;
            if ((headerSource=DataSource.get(idHeader))==null)
            {
                headerSource = getNewDataSource(headerURL, true);
                headerSource.setID(idHeader);
                headerSource.setDataFormat(DSDataFormat.JSON);
            }

            DSRequest request = new DSRequest();
            request.setShowPrompt(false);

            DSCallback headerCallBack = new DSCallback() {
                public void execute(DSResponse response, Object rawData, DSRequest request) {
                    try {
                        gridConstructor.setHeaderGrid(response.getData());
                    } catch (SetGridException e) {
                        e.printStackTrace();  //TODO Что делать на это исключение
                    }
                }
            };

            headerSource.fetchData
            (
                    new Criteria(TablesTypes.TTYPE, tblType), //Среди прочих параметров передавать идентифкатор таблицы
                    headerCallBack
                    ,request
            );




            DataSource dataSource;
            if ((dataSource=DataSource.get(dataId))==null)
            {
                dataSource= getNewDataSource(dataURL,false);
                dataSource.setID(dataId);
                dataSource.setDataFormat(DSDataFormat.JSON);
            }

            return new MyDSCallback()
            {
                public void execute(DSResponse response, Object rawData, DSRequest request)
                {
                    try {



                        JavaScriptObject jsObject = (JavaScriptObject) response.getAttributeAsObject(DSRegisterImpl.TRANS);
                        Object[] objs= JSOHelper.convertToArray(jsObject);

                        if (objs.length==0)
                            return;

                        Record data1=new Record((Map)objs[0]);

                        this.timeStamp = data1.getAttributeAsLong("updateStamp");
                        this.timeStampN = data1.getAttributeAsInt("updateStampN");
                        Integer cliCnt=data1.getAttributeAsInt("cliCnt");
                        this.tblId=data1.getAttributeAsString(TablesTypes.TBLID);


                        Record[] data = response.getData();

//                        this.timeStamp=data[0].getAttributeAsLong("updateStamp");
//                        this.timeStampN=data[0].getAttributeAsInt("updateStampN");
//                        gridConstructor.setDiagramDesc(data[0].getAttributeAsMap("desc"));
//                        data = data[0].getAttributeAsRecordArray("tuples");

                        boolean resetAll = false;
                        if (cliCnt!=null)
                        {
                            resetAll=(cliCnt-this.cliCnt <=0);
                            this.cliCnt =cliCnt;
                        }

                        gridConstructor.updateDataGrid(data, resetAll);

                        Timer timer1 = getTimer();
                        if (timer1!=null)
                            timer1.schedule(this.period);
                    } catch (SetGridException e)
                    {
                        e.printStackTrace(); //TODO Что делать на это исключение
                    }
                }
            };

        }

    }


    static class GridMetaProviderTest extends GridMetaProviderExt
    {
        public DataSource getNewDataSource(String url,boolean meta)
        {
            if (meta)
                return new TestDataSource(url);
            else
            {
                TestDataSource testDataSource = new TestDataSource(url);
                testDataSource.setForHead(false);
                return testDataSource;
            }
        }
    }



    static
    {
        boolean test=false;
        if (test)
            gridMetaProvider = new GridMetaProviderTest();
        else
            gridMetaProvider = new GridMetaProviderExt();
//        dataProviderFactory.createGridMetaProvider(new String[][]{{IGridMetaProviderFactory.TEST_CLIENT, "true1"}});
    }


    static final GUIStateDesc GUI_STATE_DESC = new GUIStateDesc()
    {
        {
            setCurrentPage(GUIStateDesc.tablePage);
        }
    };


    public static Window makeWin(String title, int width, int height, int offsetLeft,
                                 final PortalLayout layout, final Portlet portlet)
    {

        final int GRAPH_W = 600;
        final int GRAPH_H = 200;

        final Window window = new Window();
        window.setOverflow(Overflow.HIDDEN);
        window.setTitle(title);
        window.setWidth(width);
        window.setHeight(height);
        window.setLeft(offsetLeft);
        window.setCanDragReposition(true);
        window.setCanDragResize(true);

        window.setMinWidth(50);
        window.setMinHeight(50);


        Canvas[] items = portlet.getItems();
        ListGridWithDesc lg = null;
        for (Canvas item : items) {
            if (item instanceof ListGridWithDesc)
                lg = (ListGridWithDesc) item;

            portlet.removeItem(item);
            window.addItem(item);
        }

        DiagramDesc desc;
        if (lg != null && (desc = lg.getDesc()) != null) {

            int tw = 50;
            int th = 50;

            Widget chartWidget = GetWidget.getChartWidget(window, tw, th, desc);


            final WidgetCanvas chartWidgetWrapper = new WidgetCanvas(chartWidget);
            chartWidgetWrapper.setOverflow(Overflow.HIDDEN);

            chartWidgetWrapper.setMargin(0);
            chartWidgetWrapper.setPadding(0);

            chartWidgetWrapper.setMinHeight(35);
            chartWidgetWrapper.setMinWidth(25);

            chartWidgetWrapper.setWidth(tw);
            chartWidgetWrapper.setHeight(th);

            chartWidgetWrapper.setVisible(false);
            window.addItem(chartWidgetWrapper);

            final ListGridWithDesc lg1 = lg;
            window.addResizedHandler(new ResizedHandler() {
                public void onResized(ResizedEvent event) {
                    int h = window.getInnerContentHeight();
                    int w = window.getInnerContentWidth();

                    if (h <= GRAPH_H || w <= GRAPH_W) {
                        lg1.setVisible(false);
                        chartWidgetWrapper.setVisible(true);
                    } else {
                        lg1.setVisible(true);
                        chartWidgetWrapper.setVisible(false);
                    }
                }
            });
        }


        ClickHandler clickHandler = new ClickHandler() {
            public void onClick(ClickEvent event) {

                Canvas[] items = window.getItems();
                for (Canvas item : items) {
                    window.removeItem(item);
                    if (item instanceof ListGrid) {
                        portlet.addItem(item);
                        item.setWidth100();
                        item.setHeight100();
                        item.setVisible(true);
                    }
                }
                layout.addPortlet(portlet, 0, 0);
                window.destroy();
            }
        };

        HeaderControl pinDown = new HeaderControl(HeaderControl.PIN_DOWN, clickHandler);
        window.setHeaderControls(HeaderControls.HEADER_LABEL, pinDown, HeaderControls.MINIMIZE_BUTTON, HeaderControls.CLOSE_BUTTON);
        return window;
    }


    public static ListGrid createTable(final Portlet portlet, String tblType, boolean dynamicUpdate)
    {
        final String headerURL = "thead.jsp";
        final String dataURL = "data.jsp";
        ListGridWithDesc gridTable = GridUtils.createGridTable(gridMetaProvider, tblType, headerURL, dataURL, dynamicUpdate, false);
        setDropHandler(portlet,gridTable);
        return gridTable;
    }

    protected static void setDropHandler(final Window portlet, final ListGridWithDesc newGrid) {
            newGrid.addDropHandler(new DropHandler()
             {
                 public void onDrop(DropEvent event) {
                     Canvas dragTarget = EventHandler.getDragTarget();
                     if (dragTarget instanceof TreeGrid) {
                         Record[] dragData = ((TreeGrid) dragTarget).getDragData();
                         for (Record record : dragData)
                         {
                             Object operation1 = record.getAttributeAsObject("Operation");
                             IOperation operation = null;
                             if (operation1 instanceof IOperation)
                                 operation = (IOperation) operation1;
                             else if (operation1 instanceof IOperationFactory) {
                                 IOperationFactory factory = (IOperationFactory) operation1;
                                 operation = factory.getOperation();
                             }

//TODO Убрано поскольку изменился интерфейс                             if (operation != null && newGrid.addFilter(operation))
//                             {
//
//                                 HeaderControl pinUp = operation.createHeaderControl(newGrid, portlet);
//                                 if (pinUp != null) {
//                                     LinkedList<Canvas> ll = new LinkedList<Canvas>(Arrays.asList(portlet.getHeader().getMembers()));
//                                     if (ll.size() >= 1)
//                                         ll.add(1, pinUp);
//                                     else
//                                         ll.add(pinUp);
//                                     portlet.getHeader().setMembers(ll.toArray(new Canvas[ll.size()]));
//                                 }
//                                 newGrid.applyClientFilters();
//                             }
                         }
                     }
                     event.cancel();
                 }
             });
        }

    private PortalLayout buildPortlets() {
        /*
        Проблемы:
        1. не привязывается почему-то внутренняя область к внешним границам
        2. странное поведение границ вертикальных
        */
        PortalLayout portalLayout = new PortalLayout()
        {
            public Canvas getDropPortlet(Canvas dragTarget, Integer colNum, Integer rowNum, Integer dropPosition)
            {
                if (dragTarget instanceof TreeGrid)
                {
                    Record[] dragData = ((ListGrid) dragTarget).getDragData();
                    for (Record record : dragData) {
                        Object operation = record.getAttributeAsObject("Operation");
                        if (operation instanceof IOperation) {
                            IOperation operation1 = (IOperation) operation;
                            if (IOperation.TypeOperation.addEventPortlet.equals(operation1.getTypeOperation()))
                                return operation1.operate(dragTarget, null);
                        }
                    }
                    return null;
                }
                else if (dragTarget instanceof MyHeaderControl)
                {
                    MyHeaderControl dragTarget1 = (MyHeaderControl) dragTarget;
                    Canvas grid = dragTarget1.getGrid();
                    if (grid instanceof ListGridWithDesc) {
                        IOperation operation = dragTarget1.getOperation();
                        if (operation != null)
                        {
                            //Удаляем иконку
                            Window window = dragTarget1.getTarget();
                            HLayout header = window.getHeader();
                            LinkedList<Canvas> ll = new LinkedList<Canvas>(Arrays.asList(header.getMembers()));
                            ll.remove(dragTarget1);
                            header.setMembers(ll.toArray(new Canvas[ll.size()]));

                            //Удаляем операцию
                            ListGridWithDesc grid1 = (ListGridWithDesc) grid;
                            operation.onRemove(grid1, window);
//TODO изменился интерфейс                            grid1.getFiltersOperations().remove(operation);
//TODO изменился интерфейс                            grid1.applyClientFilters();
                        }
                    }
                    return null;
                } else {
                    // By default, the whole component is wrapped in a MyPortlet
                    return super.getDropPortlet(dragTarget, colNum, rowNum, dropPosition);
                }
            }
        };

        portalLayout.setNumColumns(1);
        portalLayout.setShowColumnMenus(false);


        portalLayout.setWidth100();
        portalLayout.setHeight100();
        portalLayout.setCanResizeColumns(true);
        portalLayout.setCanDragResize(true);
        portalLayout.setCanResizePortlets(true);


        return portalLayout;
    }


    @Override
    public void run()
    {
        com.google.gwt.user.client.Window.setTitle("АРМ Диспетчера (TD04)");

        GWT.create(EventTileMetaFactory.class);


        HLayout mainLayout = new HLayout();

        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();


        mainLayout.setDragAppearance(DragAppearance.TARGET);

//                mainLayout.setOverflow(Overflow.HIDDEN);
//                mainLayout.setCanDragResize(true);
//                mainLayout.setResizeFrom("L");

//                        mainLayout.setMembersMargin(5);
//                        mainLayout.setLayoutMargin(10);

        PortalLayout component = buildPortlets();
        MyToolStrip toolStrip = makeToolStrip(mainLayout, component);
        mainLayout.addMember(toolStrip);
        Img thinStripCtrl = GUI_STATE_DESC.makeThinStripCtrl();
        GUI_STATE_DESC.add2NotSwitchToolsCanvas(GUIStateDesc.systemPage,toolStrip.getMembers());

        mainLayout.addMember(thinStripCtrl);
        GUI_STATE_DESC.add2NotSwitchPageCanvas(GUIStateDesc.systemPage,mainLayout.getMembers());

        mainLayout.addMember(component);
        mainLayout.draw();

//                   Canvas winT = createWinT("Тест графика", 100, 100, 50);
//                   winT.draw();
    }


    public static MyToolStrip makeToolStrip(final HLayout mainLayout, final PortalLayout layout)
    {

        final MyToolStrip toolStrip = new MyToolStrip();
        toolStrip.setVertical(true);
        toolStrip.setHeight100();
        toolStrip.setWidth(30);

        toolStrip.setCanAcceptDrop(true);

        toolStrip.addDropHandler(new DropHandler()
        {
            public void onDrop(DropEvent event) {
                Canvas tag = EventHandler.getDragTarget();
                if (tag instanceof Portlet) {
                    Portlet portlet = (Portlet) tag;
//                    portlet.close();
//                    portlet.destroy();
                    layout.removePortlet(portlet);
                    //Сделать свободное окно, (???и добавить иконку окна???)
                    Window w = makeWin(portlet.getTitle(), portlet.getWidth(), portlet.getHeight(), 50, layout, portlet);
                    mainLayout.addChild(w);

//                    RootPanel rootPanel = RootPanel.get();
//                    rootPanel.add(w);
                    //Диактивировать стандартный обработчик события
                    event.cancel();
                }
            }
        });


        {
            ToolStripButton iconButton = new ToolStripButton();
            iconButton.setIcon("deploy.png");
            iconButton.setPrompt("Таблицы");
            toolStrip.addButton(iconButton);


//            ScriptInjector.fromUrl().setCallback().inject()

            TreeGrid p1 = TestNodesHolder.buildTree();
            p1.setCanDragRecordsOut(true);
            p1.setAlign(Alignment.CENTER);


//            PartsListGrid p2 =new PartsListGrid();
//            p2.setData(getDataList());
//
//            p2.setCanDragRecordsOut(true);
//            p2.setDragDataAction(DragDataAction.COPY);
//            p2.setAlign(Alignment.CENTER);


            final VLayout v1 = new VLayout();
            v1.setWidth(150);
            v1.setHeight100();

            v1.setCanDragResize(true);
            v1.setResizeFrom("R");


            v1.addMember(p1);
//            v1.addMember(p2);

            iconButton.addClickHandler(new ClickHandler()
            {
                boolean isInl = false;

                public void onClick(ClickEvent event) {
                    GUI_STATE_DESC.switchPages(GUIStateDesc.tablePage);
                    if (!isInl)
                        mainLayout.addMember(v1, GUI_STATE_DESC.getNotSwitchPageCanvas().length);
                    else
                        mainLayout.removeMember(v1);
                    isInl = !isInl;
                }
            });
        }

//        if (false)
        {
            ToolStripButton iconButton = new ToolStripButton();
            iconButton.setIcon("sync.png");
            iconButton.setPrompt("События");
            {
                iconButton.addClickHandler(new ClickHandler() {

                    //                boolean isInl=false;

                    public void onClick(ClickEvent event) {

                        boolean wasBuild = GUI_STATE_DESC.switchPages(GUIStateDesc.queuePage);
                        if (!wasBuild) { //Надо отстроить страницу
                            new LentaPage().makePage(mainLayout);
                        } else { //Страница построена

                            //В правой части

                            //                    if (!isInl)
                            //                    {
                            //
                            //                    }
                            //                    else if (members!=null)
                            //                    {
                            //                        mainLayout.addMembers(members);
                            //                        members=null;
                            //                    }
                        }
                        //                    isInl=!isInl;
                    }


                });
            }
            toolStrip.addButton(iconButton);
        }

        return toolStrip;

    }

}
