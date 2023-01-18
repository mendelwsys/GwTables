package com.mycompany.client.test.staf;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.11.14
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
public class Garbage
{

/*
       private PortalLayout buildPortlets() {
//        Проблемы:
//        1. не привязывается почему-то внутренняя область к внешним границам
//        2. странное поведение границ вертикальных
        PortalLayout portalLayout = new PortalLayout() {
            public Canvas getDropPortlet(Canvas dragTarget, Integer colNum, Integer rowNum, Integer dropPosition) {
                if (dragTarget instanceof TreeGrid) {
                    Record[] dragData = ((ListGrid) dragTarget).getDragData();
                    for (Record record : dragData) {
                        Object operation = record.getAttributeAsObject("Operation");
                        if (operation instanceof IOperation) {
                            IOperation operation1 = (IOperation) operation;
                            if (IOperation.TypeOperation.addEventPortlet.equals(operation1.getTypeOperation()))
                                return operation1.operate(dragTarget);
                        }
                    }
                    return null;
                } else if (dragTarget instanceof PartsListGrid) {
                    Portlet portlet = new Portlet();
                    Record[] dragData = ((PartsListGrid) dragTarget).getDragData();
                    if (dragData != null && dragData.length > 0) {
                        portlet.setTitle(dragData[0].getAttribute("partName"));
                        portlet.setShowCloseConfirmationMessage(false);
                        String wtype = dragData[0].getAttribute("type");
                        if (TablesTypes.WARNINGS.equalsIgnoreCase(wtype))
                            portlet.addItem(createTable(portlet, TablesTypes.WARNINGS, false));
                        else if (TablesTypes.WINDOWS.equalsIgnoreCase(wtype))
                            portlet.addItem(createTable(portlet, TablesTypes.WINDOWS, false));
                        else if (TablesTypes.REFUSES.equalsIgnoreCase(wtype))
                            portlet.addItem(createTable(portlet, TablesTypes.REFUSES, false));
                        else if (TablesTypes.VIOLATIONS.equalsIgnoreCase(wtype))
                            portlet.addItem(createTable(portlet, TablesTypes.VIOLATIONS, false));

                    }
                    return portlet;
                }
                if (dragTarget instanceof MyHeaderControl) {
                    MyHeaderControl dragTarget1 = (MyHeaderControl) dragTarget;
                    Canvas grid = dragTarget1.getGrid();
                    if (grid instanceof ListGridWithDesc) {
                        IOperation operation = dragTarget1.getOperation();
                        if (operation != null) {
                            //Удаляем иконку
                            Window window = dragTarget1.getTarget();
                            HLayout header = window.getHeader();
                            LinkedList<Canvas> ll = new LinkedList<Canvas>(Arrays.asList(header.getMembers()));
                            ll.remove(dragTarget1);
                            header.setMembers(ll.toArray(new Canvas[ll.size()]));

                            //Удаляем операцию
                            ListGridWithDesc grid1 = (ListGridWithDesc) grid;
                            operation.onRemove(grid1, window);
                            grid1.getFiltersOperations().remove(operation);
                            grid1.applyClientFilters();





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

public static class PartsListGrid extends ListGrid {

        public PartsListGrid() {

//            setCellHeight(24);
//            setImageSize(16);
//            setShowEdges(true);
//            setBorder("0px");
//            setBodyStyleName("normal");
//            setAlternateRecordStyles(true);
//            setShowHeader(false);
//            setLeaveScrollbarGap(false);

//            ImgProperties imgProperties = new ImgProperties();
//            imgProperties.setSrc("pieces/24/cubes_all.png");
//            imgProperties.setWidth(24);
//            imgProperties.setHeight(24);
//
//            setTrackerImage(imgProperties);


            ListGridField partNameField = new ListGridField("partName", "Имя");
            partNameField.setType(ListGridFieldType.TEXT);

            ListGridField partNumField = new ListGridField("partNum", "Кол-во");
            partNumField.setType(ListGridFieldType.TEXT);

            ListGridField partSrcField = new ListGridField("partSrc", "Состояние");
            partSrcField.setType(ListGridFieldType.TEXT);


            setFields(partNameField, partNumField, partSrcField);


//            setOverflow(Overflow.VISIBLE);
//            setShowEdges(true);
            setCanDragResize(true);
            setResizeFrom("R", "T", "B");
        }
    }

//    public static ListGrid createTable(final Portlet portlet, String tblType, String headerURL, final String dataURL, boolean dynamicUpdate)
//    {
//        final ListGridWithDesc newGrid = new ListGridWithDesc() {
//            protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
//                if (record instanceof MyRecord) {
//                    String rowStyle = ((MyRecord) record).getRowStyle();
//                    if (rowStyle != null)
//                        return rowStyle;
//                }
//                return super.getCellCSSText(record, rowNum, colNum);
//            }
//        };
//        newGrid.setWidth100();
//        newGrid.setHeight100();
//
//        newGrid.setShowAllRecords(true);
//        newGrid.setHeaderHeight(35);
//
//        newGrid.setWrapCells(true);
//        newGrid.setCellHeight(35);
//
//
//        newGrid.setCanAcceptDrop(true);
//        newGrid.setCanAcceptDroppedRecords(true);
//
//
//        //final MyDSCallback dataCallBack = gridMetaProvider.initGrid(new BGridConstructor(newGrid), tblType, headerURL, dataURL);
//
////        DataSource dataSource = new DataSource();
////        newGrid.setDataSource(dataSource);
//
//        final Pair<DSCallback, MyDSCallback> dataCallBack = gridMetaProvider.initGrid2(new BGridConstructor(newGrid), headerURL, dataURL, 3000);
//
//        newGrid.addDropHandler(new DropHandler() {
//            public void onDrop(DropEvent event) {
//                Canvas dragTarget = EventHandler.getDragTarget();
//                if (dragTarget instanceof TreeGrid) {
//                    Record[] dragData = ((TreeGrid) dragTarget).getDragData();
//                    for (Record record : dragData) {
//                        Object operation1 = record.getAttributeAsObject("Operation");
//                        IOperation operation = null;
//                        if (operation1 instanceof IOperation)
//                            operation = (IOperation) operation1;
//                        else if (operation1 instanceof IOperationFactory) {
//                            IOperationFactory factory = (IOperationFactory) operation1;
//                            operation = factory.getOperation();
//                        }
//
//                        if (operation != null && newGrid.addFilter(operation)) {
//
//                            HeaderControl pinUp = operation.createHeaderControl(newGrid, portlet);
//                            if (pinUp != null) {
//                                LinkedList<Canvas> ll = new LinkedList<Canvas>(Arrays.asList(portlet.getHeader().getMembers()));
//                                if (ll.size() >= 1)
//                                    ll.add(1, pinUp);
//                                else
//                                    ll.add(pinUp);
//                                portlet.getHeader().setMembers(ll.toArray(new Canvas[ll.size()]));
//                            }
//                            newGrid.applyClientFilters();
//                        }
//                    }
//                }
//                event.cancel();
//            }
//        });
//
//
////        DSRequest request = new DSRequest(DSOperationType.CUSTOM,"doLongOperation");
//
//        final Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);
//        criteria.addCriteria(TablesTypes.ID_TM, dataCallBack.second.getTimeStamp());
//        criteria.addCriteria(TablesTypes.ID_TN, dataCallBack.second.getTimeStampN());
//        {
//            DSRequest request = new DSRequest();
//            request.setShowPrompt(false);
//
//            String idHeader = headerURL.replace(".", "_");
//            idHeader = idHeader.replace("/", "$");
//
//            DataSource.get(idHeader).fetchData
//            (
//                    criteria, //Среди прочих параметров передается идентифкатор таблицы
//                    dataCallBack.first,
//                    request
//            );
//        }
//
//
////        final int id=RPCManager.getCurrentTransactionIdAsInt();
////        criteria.addCriteria("txid", id);
//
//        Timer t=new Timer()
//        {
//            @Override
//            public void run()
//            {
//
//                        {
//                                DSRequest request = new DSRequest();
//                                request.setShowPrompt(false);
//
//                                String dataId = dataURL.replace(".", "_");
//                                dataId = dataId.replace("/", "$");
//                                criteria.addCriteria(TablesTypes.ID_REQN, String.valueOf(dataCallBack.second.getSrvCnt()));
//                                String tblId = dataCallBack.second.getTblId();
//                                if (tblId!=null)
//                                    criteria.addCriteria(TablesTypes.TBLID,tblId);
//
//                                DataSource.get(dataId).fetchData
//                                        (
//                                                criteria, //Среди прочих параметров передается идентифкатор таблицы
//                                                dataCallBack.second,
//                                                request
//                                        );
//
//                        }
//
//            }
//        };
//
//        if (dynamicUpdate)
//            dataCallBack.second.setTimer(t);
//        t.schedule(2000);
//        return newGrid;
//    }


    private static Record[] getDataList() {


        List<Record> ll = new LinkedList<Record>();

        Map prop = new HashMap();

        prop.put("partName", "Предупреждения");
        prop.put("partNum", "212");
        prop.put("partSrc", "среднее");
        prop.put("type", TablesTypes.WARNINGS);

        ll.add(new Record(prop));

        prop.put("partSrc", "Норм.");
        prop.put("partName", "Отказы");
        prop.put("partNum", "23");
        prop.put("type", TablesTypes.REFUSES);
        ll.add(new Record(prop));

        prop.put("partSrc", "Норм.");
        prop.put("partName", "Окна");
        prop.put("partNum", "42");
        prop.put("type", TablesTypes.WINDOWS);
        ll.add(new Record(prop));

        return ll.toArray(new Record[ll.size()]);
    }


    public Runnable DemoApp1()
    {
        return new Runnable() {
            public void run() {

                com.google.gwt.user.client.Window.setTitle("АРМ Диспетчера (TeD11)");

                GWT.create(CarTileMetaFactory.class);


                HLayout mainLayout = new HLayout();

                mainLayout.setID(TablesTypes.t_MY_ROOT_PANEL);
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
                Img thinStripCtrl = makeThinStripCtrl(toolStrip);
                mainLayout.addMember(thinStripCtrl);
                notSwitchPageCanvas = mainLayout.getMembers();

                mainLayout.addMember(component);
                mainLayout.draw();

//                   Canvas winT = createWinT("Тест графика", 100, 100, 50);
//                   winT.draw();

            }
        };
    }

    public Runnable DemoApp2() {
        return new Runnable() {
            public void run() {

                com.google.gwt.user.client.Window.setTitle("Конструирование страницы Лента событий");

                GWT.create(CarTileMetaFactory.class);

                HLayout mainLayout = new HLayout();
                //HStack mainLayout = new HStack();

                mainLayout.setID(TablesTypes.t_MY_ROOT_PANEL);
                mainLayout.setShowEdges(false);
                mainLayout.setHeight100();
                mainLayout.setWidth100();


//             mainLayout.setDragAppearance(DragAppearance.TARGET);
//             mainLayout.setOverflow(Overflow.HIDDEN);
//             mainLayout.setCanDragResize(true);
//             mainLayout.setResizeFrom("L");

//             PortalLayout component = buildPortlets();
//             mainLayout.addMember(makeToolStrip(mainLayout,component));
//             mainLayout.addMember(component);
                new TestBuilder1().setComponents(mainLayout);
                mainLayout.draw();

            }
        };
    }


    public Runnable DemoApp3() {
        return new Runnable() {
            public void run() {

                com.google.gwt.user.client.Window.setTitle("ОПД T04");

                GWT.create(CarTileMetaFactory.class);

                HLayout mainLayout = new HLayout();
                //HStack mainLayout = new HStack();

                mainLayout.setID(TablesTypes.t_MY_ROOT_PANEL);
                mainLayout.setShowEdges(false);
                mainLayout.setHeight100();
                mainLayout.setWidth100();


//             mainLayout.setDragAppearance(DragAppearance.TARGET);
//             mainLayout.setOverflow(Overflow.HIDDEN);
//             mainLayout.setCanDragResize(true);
//             mainLayout.setResizeFrom("L");

//             PortalLayout component = buildPortlets();
//             mainLayout.addMember(makeToolStrip(mainLayout,component));
//             mainLayout.addMember(component);
                new TestBuilderImpl2().setComponents(mainLayout);
                mainLayout.draw();

            }
        };
    }

//    public Runnable DemoApp4() {
//        return new Runnable() {
//            public void run() {
//
//                com.google.gwt.user.client.Window.setTitle("ОПД T04");
//
//                GWT.create(CarTileMetaFactory.class);
//
//                HLayout mainLayout = new HLayout();
//
//                mainLayout.setID(TablesTypes.t_MY_ROOT_PANEL);
//                mainLayout.setShowEdges(false);
//                mainLayout.setHeight100();
//                mainLayout.setWidth100();
//
//                new TestGridGroup().setComponents(mainLayout);
//                mainLayout.draw();
//
//            }
//        };
//    }

    public Runnable DemoApp5() {
        return new Runnable() {
            public void run() {

                com.google.gwt.user.client.Window.setTitle("ОПД T15");

                GWT.create(CarTileMetaFactory.class);

                HLayout mainLayout = new HLayout();

                mainLayout.setID(TablesTypes.t_MY_ROOT_PANEL);
                mainLayout.setShowEdges(false);
                mainLayout.setHeight100();
                mainLayout.setWidth100();

                //new TXML2().setComponents(mainLayout);
                //new TestGridApp2(null,null).setComponents(mainLayout);
                new TestGridApp2(null,"/transport/dataCons").setComponents(mainLayout);
                mainLayout.draw();

            }
        };
    }


    public Runnable DemoApp6() {
        return new Runnable() {
            public void run() {

                com.google.gwt.user.client.Window.setTitle("Проверка комонентов 00");

                HLayout mainLayout = new HLayout();

                mainLayout.setID(TablesTypes.t_MY_ROOT_PANEL);
                mainLayout.setShowEdges(false);
                mainLayout.setHeight100();
                mainLayout.setWidth100();

                new CheckSubGridApp().setComponents(mainLayout);
                mainLayout.draw();

            }
        };
    }

    public Runnable DemoApp7() {
        return new Runnable() {
            public void run() {

                com.google.gwt.user.client.Window.setTitle("Проверка грида с фильтром 01");

                HLayout mainLayout = new HLayout();

                mainLayout.setID(TablesTypes.t_MY_ROOT_PANEL);
                mainLayout.setShowEdges(false);
                mainLayout.setHeight100();
                mainLayout.setWidth100();

                new TestGridWithFilters().setComponents(mainLayout);
                mainLayout.draw();
            }
        };
    }

    public Runnable DemoApp8() {
        return new Runnable() {
            public void run() {

                com.google.gwt.user.client.Window.setTitle("Проверка грида с фильтром 01");

                HLayout mainLayout = new HLayout();

                mainLayout.setID(TablesTypes.t_MY_ROOT_PANEL);
                mainLayout.setShowEdges(false);
                mainLayout.setHeight100();
                mainLayout.setWidth100();

                new CheckTree().setComponents(mainLayout);
                mainLayout.draw();
            }
        };
    }

     */
}
