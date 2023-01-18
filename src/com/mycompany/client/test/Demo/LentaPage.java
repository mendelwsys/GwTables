package com.mycompany.client.test.Demo;

import com.google.gwt.user.client.Timer;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.apps.toolstrip.MyToolStrip;
import com.mycompany.client.test.Demo.lentaViews.MyPortalLayout;
import com.mycompany.client.test.Demo.lentaViews.MyPortlet;
import com.mycompany.client.test.Demo.lentaViews.PortalColumn;
import com.mycompany.client.test.t1.BoxAnimation;
import com.mycompany.client.test.t1.MyTileGrid;
import com.mycompany.client.test.updaters.StripConstructorTile;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.StripCNST;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.07.14
 * Time: 12:55
 * Описание ленты событий
 */
public class LentaPage
{
    private MyToolStrip toolStrip;
    private MyTileGrid eventGrid;


    private class GridHandlers
    {
        final Integer[] xyz =new Integer[]{null,null,null};

        DragStartHandler dragStartHandler = new DragStartHandler() {
            public void onDragStart(DragStartEvent event)
            {
                xyz[0] = null;
                xyz[1] = null;
                xyz[2] = null;

                event.getSource();


            }
        };
        DragRepositionMoveHandler repositionMoveHandler = new DragRepositionMoveHandler() {
            public void onDragRepositionMove(DragRepositionMoveEvent event)
            {
                if (!toolStrip.isVisible())
                {
                    int x = event.getX();
                    int y = event.getY();

                    if (xyz[0] == null && xyz[1] == null)
                    {
                        xyz[0] = x;
                        xyz[1] = y;
                    } else if (xyz[0] != null && xyz[1] != null) {
                        if (Math.abs(xyz[0] - x) >= 5 || Math.abs(xyz[1] - y) >= 5) {
                            if ((y - xyz[1]) >= 5 && Math.abs(1.0 * (xyz[0] - x)) / (1.0 * (y - xyz[1])) < 0.5) {
                                String html = Canvas.imgHTML("stripp/opener_opened.png");
                                EventHandler.setDragTracker(html);
                                xyz[2] = 1;
                            } else {
                                EventHandler.setDragTracker(
                                        "<div style=\"width: 10px; height: 10px; background: black\">\n" +
                                                "</div>\n");
                                xyz[2] = null;
                            }
                            xyz[0] = x;
                            xyz[1] = y;
                        }
                    }
                }
            }
        };

        public void setToolStrip(Canvas toolStrip) {
            this.toolStrip = toolStrip;
        }

        private Canvas toolStrip;

        DropHandler dropHandler = new DropHandler() {
            public void onDrop(DropEvent event) {
                if (toolStrip!=null && xyz[2] != null && xyz[2] == 1 && !toolStrip.isVisible()) {
                    xyz[2] = null;
                    toolStrip.animateShow(AnimationEffect.SLIDE);
                }
            }
        };

    }


    private class ClickWrapper implements ClickHandler
    {
        public void setHandler(ClickHandler handler)
        {
            this.handler = handler;
        }

        ClickHandler handler;

        public ClickWrapper()
        {
        }

        public void onClick(ClickEvent event) {
            if (handler!=null)
                handler.onClick(event);
        }
    }


    private GridHandlers eventGridHeadHandlers = new GridHandlers();
    private GridHandlers userGridHeadHandlers = new GridHandlers();

    public ListGrid initUserGridWithData()
    {
        ListGrid rv = new ListGrid();
        rv.setHeight(220);
        ListGridField[] fields = new ListGridField[]
                {
                        new ListGridField("user","ФИО"),
                        new ListGridField("cntctrl","Событий"),
                        new ListGridField("duties","Должность")
                };
        rv.setFields(fields);

        List<Record> listGridRecord = new LinkedList<Record>();

        Map map=new HashMap();
        map.put("user","Иванов И.И.");
        map.put("duties","Диспетчер");
        map.put("cntctrl","0");
        listGridRecord.add(new Record(map));



        map.put("user","Петров В.С.");
        map.put("duties","Диспетчер");
        map.put("cntctrl","0");
        listGridRecord.add(new Record(map));

        map.put("user","Сидоров С.П.");
        map.put("duties","Диспетчер");
        map.put("cntctrl","0");
        listGridRecord.add(new Record(map));

        rv.setData(listGridRecord.toArray(new Record[listGridRecord.size()]));
        return rv;
    }




    private void _makePage_bu(final HLayout hLayout)
    {

        //TODO toolStrip - Добавление икон на туллстрип, для уменьшения или увеличения их
        final MyPortalLayout portalLayout = new MyPortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();



        ClickWrapper handler = new ClickWrapper();

        final VLayout eventLayout = eventBlock(eventGrid, handler);
        final VLayout userLayout = userBlock(eventLayout, handler);




        eventGrid.setTileDropHandler(new DropHandler() {
            @Override
            public void onDrop(final DropEvent event) {
                Canvas grid = EventHandler.getDragTarget();
                Canvas eventTile = EventHandler.getTarget();

                if ((grid instanceof ListGrid) && (eventTile instanceof EventTile)) {
                    Record[] userRecord = ((ListGrid) grid).getDragData();
                    Record eventRecord = ((EventTile) eventTile).getRecord();
                    addEvent2User(userRecord, eventRecord, portalLayout, event.getX(), event.getY());
                    event.cancel();
                }
                else if ((grid instanceof MyTileGrid) && (eventTile instanceof EventTile))
                {
                    grid.markForRedraw();
                }
            }
        });



        {
            ToolStripButton showEventButton = new ToolStripButton();
            showEventButton.setIcon("stripp/arrow_right.png");
            showEventButton.setPrompt("События");
            DemoApp01.GUI_STATE_DESC.addToolStripButton(showEventButton);

            showEventButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    if (!eventLayout.isVisible())
                        eventLayout.animateShow(AnimationEffect.SLIDE);
                    else {
                        Canvas member = eventLayout.getMember(0);
                        if (!member.isVisible())
                            member.animateShow(AnimationEffect.SLIDE);
                    }

                }
            });
        }

        hLayout.addMembers(eventLayout,userLayout,portalLayout);
    }


    private void _makePage(final HLayout hLayout)
    {
        final Window winModal = new Window();
        winModal.setWidth100();
        winModal.setHeight100();
        winModal.setTitle("XXXX");
        winModal.centerInPage();
        winModal.addCloseClickHandler(new CloseClickHandler()
        {
            public void onCloseClick(CloseClickEvent event)
            {
                winModal.destroy();
            }
        });

        eventGrid.setWidth100();
        winModal.addItem(eventGrid);

        hLayout.addMembers(winModal);
    }



    private VLayout eventBlock(TileGrid eventGrid, ClickHandler usersHandler)
    {
        final VLayout eventLayout = new VLayout();
        eventLayout.setAnimateHideTime(600);


        final ToolStrip toolStrip = new ToolStrip();
        eventGridHeadHandlers.setToolStrip(toolStrip);


        toolStrip.setAnimateHideTime(600);
        toolStrip.setWidth(230);

        {
            ToolStripButton iconButton = new ToolStripButton();
            iconButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event)
                {
                    eventLayout.animateHide(AnimationEffect.SLIDE);
                }
            });
            iconButton.setIcon("stripp/close.png");
            iconButton.setPrompt("Закрыть");
            toolStrip.addButton(iconButton);
        }


        {
            ToolStripButton iconButton = new ToolStripButton();
            iconButton.addClickHandler(new ClickHandler()
            {
                public void onClick(ClickEvent event)
                {
                    toolStrip.animateHide(AnimationEffect.SLIDE);
                }
            });
            //
            iconButton.setIcon("stripp/opener_closed.png");
            iconButton.setPrompt("Скрыть заголовок");
            toolStrip.addButton(iconButton);
        }

        {
            ToolStripButton iconButton = new ToolStripButton();
            iconButton.addClickHandler(usersHandler);
            iconButton.setIcon("stripp/arrow_right.png");
            iconButton.setPrompt("Диспетчеры");
            toolStrip.addButton(iconButton);
        }

        eventGrid.addDragStartHandler(eventGridHeadHandlers.dragStartHandler);
        eventGrid.addDragRepositionMoveHandler(eventGridHeadHandlers.repositionMoveHandler);
        eventGrid.addDropHandler(eventGridHeadHandlers.dropHandler);

        eventLayout.addMembers(toolStrip, eventGrid);
        eventGrid.setHeight100();
        return eventLayout;
    }


    private VLayout userBlock(final Layout eventLayout, ClickWrapper handler)
    {

        ListGrid userGrid= initUserGridWithData();

        userGrid.setCanDrag(true);
        userGrid.setCanDrop(true);
        userGrid.setCanDragReposition(true);
        userGrid.setCanDragRecordsOut(true);


        userGrid.addDragStartHandler(userGridHeadHandlers.dragStartHandler);
        userGrid.addDragRepositionMoveHandler(userGridHeadHandlers.repositionMoveHandler);
        userGrid.addDragRepositionStopHandler(new DragRepositionStopHandler() {
            public void onDragRepositionStop(DragRepositionStopEvent event) {
                userGridHeadHandlers.dropHandler.onDrop(null);
            }
        });


        userGrid.setWidth(230);
        userGrid.setHeight100();

        final VLayout vl = new VLayout();
        vl.setAnimateHideTime(600);


        final ToolStrip toolStrip = new ToolStrip();
        userGridHeadHandlers.setToolStrip(toolStrip);
        toolStrip.setAnimateHideTime(600);
        toolStrip.setWidth(230);
        {
            ToolStripButton iconButton = new ToolStripButton();
            iconButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event)
                {
                    vl.animateHide(AnimationEffect.SLIDE);
                }
            });
            iconButton.setIcon("stripp/close.png");
            iconButton.setPrompt("Закрыть");
            toolStrip.addButton(iconButton);
        }

        {
            ToolStripButton iconButton = new ToolStripButton();
            iconButton.addClickHandler(new ClickHandler()
            {
                public void onClick(ClickEvent event)
                {
                    toolStrip.animateHide(AnimationEffect.SLIDE);
                }
            });
            //
            iconButton.setIcon("stripp/opener_closed.png");
            iconButton.setPrompt("Скрыть заголовок");
            toolStrip.addButton(iconButton);
        }

        {
            ToolStripButton iconButton = new ToolStripButton();
            iconButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event)
                {
                    if (!eventLayout.isVisible())
                        eventLayout.animateShow(AnimationEffect.SLIDE);
                    else
                    {
                        Canvas header = eventLayout.getMember(0);
                        if (!header.isVisible())
                            header.animateShow(AnimationEffect.SLIDE);
                    }
                }
            });

            //
            iconButton.setIcon("stripp/arrow_left.png");
            iconButton.setPrompt("События");
            toolStrip.addButton(iconButton);
        }





        vl.addMembers(toolStrip, userGrid);
        userGrid.setHeight100();

        handler.setHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (!vl.isVisible())
                    vl.animateShow(AnimationEffect.SLIDE);
                else
                {
                    Canvas header = vl.getMember(0);
                    if (!header.isVisible())
                        header.animateShow(AnimationEffect.SLIDE);
                }
            }
        });

//        userGrid.addDragCompleteHandler(new DragCompleteHandler() {
//            @Override
//            public void onDragComplete(DragCompleteEvent event) {
//                srs=event.get;
//            }
//        });


        return vl;
    }

    private void addEvent2User(Record[] userRecord, final Record eventRecord, MyPortalLayout portalLayout, final  int eventX, final int eventY)
    {
        DetailViewer pCanvas=new DetailViewer();
        {
            DetailViewerField[] fields = new DetailViewerField[]{
                    new DetailViewerField(
                    StripCNST.EVENTID,
                    "ID"
                    ),
                    new DetailViewerField(
                    StripCNST.EVENT,
                    "Событие"
                    ),
                    new DetailViewerField(
                    StripCNST.SERV,
                    "Сервис"
                    ),
                    new DetailViewerField(
                    StripCNST.PLACE,
                    "Место"
                    )
            };

            pCanvas.setFields(fields);
            pCanvas.setData(new Record[]{eventRecord});
        }

        if (userRecord.length == 1)
        {
            String fio = userRecord[0].getAttribute("user");

            final HLayout hLayout = new HLayout();
//            hLayout.setHPolicy(LayoutPolicy.NONE);
//            hLayout.setOverflow(Overflow.VISIBLE);
            hLayout.setWidth100();
            hLayout.setCanAcceptDrop(true);

            hLayout.setMembersMargin(6);

            Canvas dropLineProperties = new Canvas();
            dropLineProperties.setBackgroundColor("aqua");
            hLayout.setDropLineProperties(dropLineProperties);


            hLayout.setShowDragPlaceHolder(true);

            Canvas placeHolderProperties = new Canvas();
            placeHolderProperties.setBorder("2px solid #8289A6");

            hLayout.setPlaceHolderProperties(placeHolderProperties);

            hLayout.setDropLineThickness(4);



            final MyPortlet newPortlet=new MyPortlet();

            //newPortlet.setShowHeaderBackground(true);

            newPortlet.setTitle(eventRecord.getAttribute(StripCNST.EVENTID)+" "+eventRecord.getAttribute(StripCNST.EVENT)+" ("+eventRecord.getAttribute(StripCNST.SERV)+")");
            newPortlet.setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.MINIMIZE_BUTTON,HeaderControls.CLOSE_BUTTON);


            newPortlet.addItem(pCanvas);
            newPortlet.setVisible(false);

            Canvas[] members=portalLayout.getMembers();
            PortalColumn member;
            br:
            {

                for (int i = 0, membersLength = members.length; i < membersLength; i++)
                {
                    member = (PortalColumn) members[i];
                    String has = member.getHeader();
                    if ((has!=null && has.equalsIgnoreCase(fio)))
                    {
//                        final MyPortalColumn mmember =member;
//                        newPortlet.addDrawHandler(new DrawHandler() {
//                            @Override
//                            public void onDraw(DrawEvent event) {
                        if (member.getMembers().length%2==0)
                            newPortlet.setBackgroundColor("#FF6622");
                        if (member.getMembers().length%3==0)
                            newPortlet.setBackgroundColor("#22AAFF");
//                            }
//                        });


                        hLayout.addMember(newPortlet);
                        member.addMember(hLayout, DemoApp01.GUI_STATE_DESC.getNotSwitchPageCanvas().length);

                        eventGrid.removeData(eventRecord);

                        BoxAnimation.animate(new Rectangle(eventX, eventY, 50, 50), newPortlet, member, 750);
                        break br;
                    }
                }

                portalLayout.addMember(member = new PortalColumn(fio));
                portalLayout.redraw();

                final PortalColumn mmember =member;
                Timer timer=new Timer()
                {
                    @Override
                    public void run()
                    {
                        hLayout.addMember(newPortlet);
                        //mmember.addMember(newPortlet,HelloWorld.notSwitchPageCanvas.length);
                        mmember.addMember(hLayout, DemoApp01.GUI_STATE_DESC.getNotSwitchPageCanvas().length);
                        eventGrid.removeData(eventRecord);
                        BoxAnimation.animate(new Rectangle(eventX, eventY,50,50),newPortlet,mmember,750);

                    }
                };
                timer.schedule(100);
            }

        }
    }



    /*
        TODO Далее редактирование будет здесь надо написать новую инициализационную процедуру
        TODO Или модифицировать старую что бы лента событий у нас была выстроена в одну линию
        TODO Желательно так же пиктограмцы придумать для событий разного типа.
    */
    public void makePage(final HLayout hLayout)
    {

        toolStrip=(MyToolStrip)hLayout.getMember(0);

        eventGrid = new MyTileGrid();

        eventGrid.setTileConstructor(EventTile.class.getName());
//        eventGrid.setTileMargin(0); TODO Не сплошным лесом
        eventGrid.setCanDrag(true);
        eventGrid.setCanDrop(true);
        eventGrid.setCanDragReposition(true);
        eventGrid.setCanReorderTiles(true);

//        eventGrid.setCanDragTilesOut(true);

        eventGrid.setTileWidth(180);
        eventGrid.setTileHeight(80);

        eventGrid.setWidth(230);


        eventGrid.setHeight100();
        //eventGrid.setShowAllRecords(true);


        final String headerURL = "thead.jsp";
        final String dataURL = "data.jsp";


        String dataId = dataURL.replace(".", "_");
        dataId = dataId.replace("/", "$");
        final String idDataSource=dataId;

        final MyDSCallback dataCallBack = DemoApp01.gridMetaProvider.
                initGrid
        (
                new StripConstructorTile(eventGrid)
                {
                    public void setHeaderGrid(Record[] gridOptions) throws SetGridException
                    {
                        super.setHeaderGrid(gridOptions);
                        _makePage(hLayout);//Установка грида по получению метаиноформации
                    }
                }, TablesTypes.LENTA, headerURL, dataURL
        );

        Criteria criteria = new Criteria(TablesTypes.TTYPE, TablesTypes.LENTA);
        criteria.addCriteria(TablesTypes.ID_TM, dataCallBack.getTimeStamp());
        criteria.addCriteria(TablesTypes.ID_TN, dataCallBack.getTimeStampN());
        DSRequest request = new DSRequest();
        request.setShowPrompt(false);

        DataSource.get(dataId).fetchData
        (
            criteria, //Среди прочих параметров передается идентифкатор таблицы
            dataCallBack,
            request
        );
    }
}
