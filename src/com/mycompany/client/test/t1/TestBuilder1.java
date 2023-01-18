package com.mycompany.client.test.t1;

import com.google.gwt.user.client.Timer;
import com.mycompany.client.apps.toolstrip.MyToolStrip;
import com.mycompany.client.test.Demo.EventTile;
import com.mycompany.client.test.Demo.lentaViews.MyPortalLayout;
import com.mycompany.client.test.Demo.lentaViews.MyPortlet;
import com.mycompany.client.test.Demo.lentaViews.PortalColumn;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.common.StripCNST;
import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.*;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.viewer.DetailFormatter;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 08.09.14
 * Time: 15:55
 * TODO Осталось отработать взаимодействие в том числе и анимационное пользователя и события
 */
public class TestBuilder1 implements TestBuilder
{

    private class GridHandlers
    {
        final Integer[] xyz =new Integer[]{null,null,null};

        DragStartHandler dragStartHandler = new DragStartHandler() {
            public void onDragStart(DragStartEvent event) {
                xyz[0] = null;
                xyz[1] = null;
                xyz[2] = null;
            }
        };
        DragRepositionMoveHandler repositionMoveHandler = new DragRepositionMoveHandler() {
            public void onDragRepositionMove(DragRepositionMoveEvent event) {
                int x = event.getX();
                int y = event.getY();

                if (xyz[0] == null && xyz[1] == null) {
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

    GridHandlers gridHandlers1 = new GridHandlers();
    GridHandlers gridHandlers2 = new GridHandlers();


    protected void setGridAttributes(TileGrid grid)
    {
        grid.setTileConstructor(EventTile.class.getName());

        grid.setTileMargin(0);

        grid.setCanDrag(true);
        grid.setCanDrop(true);

        grid.setCanDragReposition(true);
        grid.setCanReorderTiles(true);
        grid.setCanDragTilesOut(true);

        grid.setTileWidth(180);
        grid.setTileHeight(80);
        grid.setWidth(230);
    }



    boolean isShowText=false;
    public void setHeaderGrid(TileGrid grid)
    {
        ListGridField[] fields = new ListGridField[]{
                new ListGridField(
                StripCNST.EVENTID,
                StripCNST.EVENTID
                ),
                new ListGridField(
                StripCNST.EVENT,
                StripCNST.EVENT
                ),
                new ListGridField(
                StripCNST.SERV,
                StripCNST.SERV
                ),
                new ListGridField(
                StripCNST.PLACE,
                StripCNST.PLACE
                )
        };

        List<DetailViewerField> df= new LinkedList<DetailViewerField>();

        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++)
        {
            DetailViewerField field=new DetailViewerField(fields[i].getName(), fields[i].getTitle());


            if (
                    field.getName().equals(StripCNST.EVENTID)
                            ||
                            field.getName().equals(StripCNST.EVENT)
                            ||
                            field.getName().equals(StripCNST.SERV)
                            ||
                            field.getName().equals(StripCNST.PLACE)
                    ) {

                if (field.getName().equals(StripCNST.SERV))
                {
                    //field.setCellStyle("background-color: red");

                    field.setDetailFormatter(new DetailFormatter() {
                        public String format(Object value, Record record, DetailViewerField field) {
                            if (value == null)
                                value = "-";
                            String val = " ( " + value + " )";
                            if (val.contains(StripCNST.WAY_NM))
                                return Canvas.imgHTML("service/link.png") + (isShowText ? val : "");
                            else if (val.contains(StripCNST.EL_NAME))
                                return Canvas.imgHTML("service/lightning.png") + (isShowText ? val : "");
                            else if (val.contains(StripCNST.CTRL_NAME))
                                return Canvas.imgHTML("service/ipod_cast.png") + (isShowText ? val : "");
                            else if (val.contains(StripCNST.VAG_NAME))
                                return Canvas.imgHTML("service/lorry.png") + (isShowText ? val : "");
                            else
                                return val;
                        }
                    });
                } else if (field.getName().equals(StripCNST.EVENT)) {
                    field.setDetailFormatter(new DetailFormatter() {
                        public String format(Object value, Record record, DetailViewerField field)
                        {

                            if (value == null)
                                value = "-";
                            String val = " ( " + value + " )";


                            if (val.contains(StripCNST.WIN_NAME))
                                return Canvas.imgHTML("win/time.png") + (isShowText ? val : "");
                            else if (val.contains(StripCNST.REFUSE_NAME))
                                return Canvas.imgHTML("ref/remove.png") + (isShowText ? val : "");
                            else if (val.contains(StripCNST.WARN_NAME))
                                return Canvas.imgHTML("warn/exclamation.png") + (isShowText ? val : "");
                            else if (val.contains(StripCNST.VIOL_NAME))
                                return Canvas.imgHTML("viol/error.png") + (isShowText ? val : "");
                            else
                                return val;
                        }
                    });
                }

                df.add(field);
//                grid.setCanHover(true);
//                grid.setShowHoverComponents(true);
            }
//            else
//                field.setHidden(true);
        }
        grid.setFields(df.toArray(new DetailViewerField[df.size()]));
    }

    public ListGrid getUserGrid()
    {
        ListGrid rv = new ListGrid();
        rv.setHeight(220);
        ListGridField[] fields = new ListGridField[]
                {
                        new ListGridField("user","Фамилия"),
                        new ListGridField("duties","Должность")
                };
        rv.setFields(fields);

        List<Record> listGridRecord = new LinkedList<Record>();

        Map map=new HashMap();
        map.put("user","Иванов И.И.");
        map.put("duties","Диспетчер");
        listGridRecord.add(new Record(map));



        map.put("user","Петров В.С.");
        map.put("duties","Диспетчер");
        listGridRecord.add(new Record(map));

        map.put("user","Сидоров С.П.");
        map.put("duties","Диспетчер");
        listGridRecord.add(new Record(map));

        rv.setData(listGridRecord.toArray(new Record[listGridRecord.size()]));
        return rv;
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


    public void setComponents(Layout hLayout)
    {

        final MyPortalLayout portalLayout = new MyPortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();


        final MyToolStrip toolStrip = new MyToolStrip();

        toolStrip.setVertical(true);
        toolStrip.setHeight100();
        toolStrip.setWidth(30);

        toolStrip.setCanAcceptDrop(true);
        toolStrip.setAnimateHideTime(600);
        {
           ToolStripButton iconButton = new ToolStripButton();
           iconButton.setIcon("deploy.png");
           iconButton.setPrompt("Настройка");
           toolStrip.addButton(iconButton);
        }

        {
            ToolStripButton iconButton = new ToolStripButton();
            iconButton.setIcon("sync.png");
            iconButton.setPrompt("События");
            toolStrip.addButton(iconButton);
        }
        toolStrip.addFill();
        toolStrip.addSeparator();
        toolStrip.setVisible(false);


        ToolStripButton hideButton = new ToolStripButton();
        hideButton.setIcon("stripp/arrow_left.png");
        hideButton.setPrompt("Свернуть");
        toolStrip.addButton(hideButton);


        {
            ToolStripButton iconButton = new ToolStripButton();
            iconButton.setIcon("stripp/arrow_right.png");
            iconButton.setPrompt("Тест");
            toolStrip.addButton(iconButton);
        }


        MyTileGrid eventGrid = new MyTileGrid();

        setGridAttributes(eventGrid);
        setHeaderGrid(eventGrid);

        Map map=new HashMap();

        map.put(StripCNST.EVENTID,122304);
        map.put(StripCNST.EVENT,StripCNST.WIN_NAME);
        map.put(StripCNST.SERV,StripCNST.WAY_NM);
        map.put(StripCNST.PLACE, "ПСКОВ-КЕБ");


        eventGrid.setData(new Record[]{new Record(map)});

        ClickWrapper handler = new ClickWrapper();


        final Img thinStripCtrl = new Img();
        {
            thinStripCtrl.setSrc("[SKIN]Splitbar/vsplit_bg.png");
            thinStripCtrl.setImageType(ImageStyle.TILE);
            thinStripCtrl.setWidth(6);
            thinStripCtrl.setHeight100();
            thinStripCtrl.setPrompt("Панель");
            thinStripCtrl.addMouseOverHandler(new MouseOverHandler() {
                public void onMouseOver(MouseOverEvent event) {
                  thinStripCtrl.setCursor(Cursor.HAND);
                }
            });
            thinStripCtrl.addMouseOutHandler(new MouseOutHandler() {

                public void onMouseOut(MouseOutEvent event) {
                  thinStripCtrl.setCursor(Cursor.DEFAULT);
                }
            });
            thinStripCtrl.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    toolStrip.animateShow(toolStrip, AnimationEffect.SLIDE, "T", "B");
                   thinStripCtrl.setVisible(false);
                }
            });
        }
        hideButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

                toolStrip.animateHide(toolStrip,AnimationEffect.SLIDE,"R","L");
                thinStripCtrl.setVisible(true);
            }
        });


        eventGrid.setTileDropHandler(new DropHandler() {
            @Override
            public void onDrop(final DropEvent event) {
                Canvas userList = EventHandler.getDragTarget();
                Canvas eventList = EventHandler.getTarget();

                if ((userList instanceof ListGrid) && (eventList instanceof EventTile)) {
                    Record[] userRecord = ((ListGrid) userList).getDragData();
                    Record eventRecord = ((EventTile) eventList).getRecord();

                    addEvent2User(userRecord, eventRecord, portalLayout, event.getX(), event.getY());
                }
                event.cancel();
            }
        });


        VLayout eventLayout = eventBolck(eventGrid, handler);

        VLayout v2 = userBlock(eventLayout, handler,portalLayout);

        hLayout.addMembers(toolStrip,thinStripCtrl,eventLayout,v2,portalLayout);
    }

    private void addEvent2User(Record[] userRecord, Record eventRecord, MyPortalLayout portalLayout, final  int eventX, final int eventY) {
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

            final MyPortlet newPortlet=new MyPortlet();
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
                        if (member.getMembers().length%2==0)
                            newPortlet.setBackgroundColor("#FF6622");
                        if (member.getMembers().length%3==0)
                            newPortlet.setBackgroundColor("#22AAFF");

                        member.addMember(newPortlet,1);


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
                        mmember.addMember(newPortlet,1);
                        BoxAnimation.animate(new Rectangle(eventX, eventY,50,50),newPortlet,mmember,750);
                    }
                };
                timer.schedule(100);
            }

        }
    }

    private VLayout eventBolck(TileGrid eventGrid, ClickHandler handler)
    {
        final VLayout eventLayout = new VLayout();
        eventLayout.setAnimateHideTime(600);


        final ToolStrip toolStrip = new ToolStrip();
        gridHandlers1.setToolStrip(toolStrip);


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
            iconButton.addClickHandler(handler);
            iconButton.setIcon("stripp/arrow_right.png");
            iconButton.setPrompt("Диспетчеры");
            toolStrip.addButton(iconButton);
        }

        eventGrid.addDropHandler(gridHandlers1.dropHandler);


        eventLayout.addMembers(toolStrip, eventGrid);
        eventGrid.setHeight100();
        return eventLayout;
    }


    private VLayout userBlock(final Layout eventLayout, ClickWrapper handler,final MyPortalLayout portalLayout)
    {

        ListGrid userGrid=getUserGrid();

        userGrid.setCanDrag(true);
        userGrid.setCanDrop(true);
        userGrid.setCanDragReposition(true);
        userGrid.setCanDragRecordsOut(true);
        userGrid.setCanAcceptDrop(true);
        userGrid.setCanAcceptDroppedRecords(true);

        userGrid.addDropHandler(new DropHandler() {
            @Override
            public void onDrop(DropEvent event) {

                Canvas eventList = EventHandler.getDragTarget();
                Canvas userList = EventHandler.getTarget();


                //if ((userList instanceof ListGrid) && (eventList instanceof CarTile))
                if ((eventList instanceof TileGrid)) {

                    //Record[] userRecord = ((ListGrid) userList).get;
                    Map vals = ((TileGrid) eventList).getDropValues();



                    //String event1=eventRecord.getAttribute(StripCNST.EVENTID);

//                    addEvent2User(userRecord, eventRecord, portalLayout, event.getX(), event.getY());
                }
                event.cancel();

            }
        });

        userGrid.addDragStartHandler(gridHandlers2.dragStartHandler);
        userGrid.addDragRepositionMoveHandler(gridHandlers2.repositionMoveHandler);
        userGrid.addDragRepositionStopHandler(new DragRepositionStopHandler() {
            public void onDragRepositionStop(DragRepositionStopEvent event) {
                gridHandlers2.dropHandler.onDrop(null);
            }
        });


        userGrid.setWidth(230);
        userGrid.setHeight100();

        final VLayout vl = new VLayout();
        vl.setAnimateHideTime(600);


        final ToolStrip toolStrip = new ToolStrip();
        gridHandlers2.setToolStrip(toolStrip);
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

}
