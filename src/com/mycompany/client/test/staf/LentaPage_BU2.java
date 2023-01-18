package com.mycompany.client.test.staf;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.07.14
 * Time: 12:55
 * Описание ленты событий
 */

public class LentaPage_BU2
{

//    /*
//        TODO Далее редактирование будет здесь надо написать новую инициализационную процедуру
//        TODO Или модифицировать старую что бы лента событий у нас была выстроена в одну линию
//        TODO Желательно так же пиктограмцы придумать для событий разного типа.
//    */
//    public void makePage(final HLayout hLayout)
//    {
//
////        final DeskTopLayout portalLayout = new DeskTopLayout();
////        portalLayout.setOverflow(Overflow.VISIBLE);
////        portalLayout.setColumnOverflow(Overflow.VISIBLE);
////        portalLayout.setPreventColumnUnderflow(false);
////        portalLayout.setNumColumns(3);
////        portalLayout.setShowColumnMenus(true);
////        portalLayout.setColumnBorder("0");
//
//
////        final ListGridWithDesc.IHoverGetter hoverGetter = new ListGridWithDesc.IHoverGetter()
////        {
////            public Canvas getCellHoverComponent(ListGrid grid, Record record, Integer rowNum, Integer colNum)
////            {
////
////                DetailViewer detailViewer = new DetailViewer();
////                detailViewer.setWidth(200);
////                //detailViewer.setHeaderStyle("height:0px");
////
////                DetailViewerField fEventName = new DetailViewerField("eventName", "1");
////
////                DetailViewerField fDorName = new DetailViewerField("period", "2");
////                DetailViewerField fPlace = new DetailViewerField("place", "3");
////                DetailViewerField fComment = new DetailViewerField("Comment", "4");
////
////                detailViewer.setFields(fEventName, fDorName, fPlace, fComment);
////
////                String eventName=record.getAttribute(StripCNST.EVENT);
//////                String dorName=record.getAttribute(StripCNST.DOR_NAME);
//////                String predId=record.getAttribute(StripCNST.PRED_ID);
////
//////                String ND=record.getAttribute(StripCNST.ND);
//////                String KD=record.getAttribute(StripCNST.KD);
////                String Place=record.getAttribute(StripCNST.PLACE);
//////                String Comment=record.getAttribute(StripCNST.COMMENT);
////
////
////                Record r=new Record();
////                r.setAttribute("eventName",eventName+"<br><b>");
////                r.setAttribute("place",Place);
////
//////                r.setAttribute("eventName",eventName+"<br><b>"+dorName+","+predId);
//////                r.setAttribute("period",ND+" "+KD);
//////                r.setAttribute("place",Place);
//////                r.setAttribute("Comment",Comment);
////
////                detailViewer.setData(new Record[]{r});
////                return detailViewer;
////            }
////        };
//
//
//
//        TileGrid listGrid = new TileGrid();
//
////        listGrid.addSelectionChangedHandler(new SelectionChangedHandler()
////        {
////            Canvas anim;
////            public void onSelectionChanged(SelectionChangedEvent event) {
////                Record record = event.getRecord();
////                String attr=record.getAttribute(StripCNST.EVENT);
////
////                {
////                    if (anim!=null)
////                    {
////                        hLayout.removeChild(anim);
////                        anim.destroy();
////                    }
////                    anim = hoverGetter.getCellHoverComponent(null,record,-1,-1);
////
//////                    anim = new Canvas();
//////                    anim.setOverflow(Overflow.HIDDEN);
//////                    anim.setBorder("1px solid #6a6a6a");
//////                    anim.setBackgroundColor("#C3D9FF");
//////                    anim.setCanDragReposition(true);
//////                    anim.setCanDragResize(true);
//////                    anim.setDragAppearance(DragAppearance.TARGET);
//////                    anim.setSmoothFade(true);
//////                    //anim.setContents("1<br>2<br>3<br><b>Animated Object</b> (drag to move or resize)<br>3<br>2<br>1");
//////                    anim.setContents("<b>"+attr+"</b>");
////                    anim.setLeft(event.getX());
////                    anim.setTop(event.getY());
////                    anim.setWidth(100);
////                    anim.setHeight(100);
////
////                    hLayout.addChild(anim);
////                }
////            }
////        });
//
////        listGrid.setHeight(20);
//        // autosize to fit the list, instead of scrolling
//        //listGrid.setOverflow(Overflow.HIDDEN);
//        //listGrid.setBodyOverflow(Overflow.VISIBLE);
//
//        // disable normal row selection behaviors
////        listGrid.setSelectionType(SelectionStyle.NONE);
//
////        ListGridField portletNameField = new ListGridField("portletName");
////        portletNameField.setCellFormatter(new CellFormatter()
////        {
////            public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
//////                if (colNum==0)
//////                {
////                    String s = Canvas.imgHTML("[SKIN]actions/view.png") + " " + value;
//////                    System.out.println("s = " + s);
////                    return s;
//////                }
//////                else
//////                    return " "+value;
////            }
////        });
////        listGrid.setFields(portletNameField);
//
//
//        listGrid.setHeight100();
//        listGrid.setShowAllRecords(true);
//
//        final String headerURL = "thead.jsp";
//        final String dataURL = "data.jsp";
//        final MyDSCallback dataCallBack = DemoApp01.gridMetaProvider.initGrid
//        (
//                new StripConstructorTile(listGrid) {
//                    public void setHeaderGrid(Record gridOptions) throws SetGridException {
//                        super.setHeaderGrid(gridOptions);
//
//                        hLayout.getMember(0).hide();
//
////                        Window wnd = new Window();
////                        wnd.setTitle("События в системе");
////                        wnd.setCanDragReposition(true);
////                        wnd.setCanDragResize(true);
////                        wnd.setAutoSize(true);
//
//                        TileGrid grid = getGrid();
//
//
//                        final VStack vl = new VStack();
//                        vl.setAnimateHideTime(600);
//
//
//                        final ToolStrip toolStrip = new ToolStrip();
//                        toolStrip.setHeight(10);
//                        toolStrip.setWidth(230);
//                        {
//                            ToolStripButton iconButton = new ToolStripButton();
//                            iconButton.addClickHandler(new ClickHandler() {
//                                public void onClick(ClickEvent event) {
//                                    vl.animateHide(AnimationEffect.SLIDE);
//                                }
//                            });
//                            iconButton.setIcon("[SKIN]DateChooser/arrow_left.png");
//                            iconButton.setPrompt("Закрыть");
//                            toolStrip.addButton(iconButton);
//                        }
//                        {
//                            ToolStripButton iconButton = new ToolStripButton();
//                            iconButton.addClickHandler(new ClickHandler() {
//                                public void onClick(ClickEvent event) {
//
//                                }
//                            });
//                            //
//                            iconButton.setIcon("[SKIN]DateChooser/arrow_right.png");
//                            //iconButton.setIcon("stripp/arrow_right.gif");
//                            iconButton.setPrompt("Диспетчеры");
//                            toolStrip.addButton(iconButton);
//                        }
//
//
//                        vl.addMembers(toolStrip, grid);
//                        hLayout.addMembers(new Canvas[]{vl}, 1);
//                    }
//                }, TablesTypes.LENTA, headerURL, dataURL);
//
//        Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.LENTA);
//        criteria.addCriteria(TablesTypes.ID_TM, dataCallBack.getTimeStamp());
//        criteria.addCriteria(TablesTypes.ID_TN, dataCallBack.getTimeStampN());
//
//        DSRequest request = new DSRequest();
//        request.setShowPrompt(false);
//
//        DataSource.get(dataURL.replace(".", "_")).fetchData
//        (
//            criteria, //Среди прочих параметров передается идентифкатор таблицы
//            dataCallBack,
//            request
//        );
//
//
//        final DeskTopLayout portalLayout = new DeskTopLayout(3);
//        portalLayout.setWidth100();
//        portalLayout.setHeight100();
//
//        // create portlets...
//        for (int i = 1; i <= 2; i++) {
//            MyPortlet portlet = new MyPortlet();
//            portlet.setTitle("Событие "+i);
//
//            Label label = new Label();
//            label.setAlign(Alignment.CENTER);
//            label.setLayoutAlign(VerticalAlignment.CENTER);
//            label.setContents("Событие для отображение");
//            //label.setBackgroundColor(colors[Random.nextInt(colors.length - 1)]);
//            portlet.addItem(label);
//            portalLayout.addPortlet(portlet);
//        }
//        hLayout.addMembers(portalLayout);
//    }
}
