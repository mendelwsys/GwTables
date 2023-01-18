package com.mycompany.client.test.Demo;

import com.google.gwt.user.client.ui.Widget;
import com.mycompany.client.test.t1.MyTileGrid;
import com.mycompany.common.StripCNST;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.State;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.tile.SimpleTile;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 06.09.14
 * Time: 18:39
 * To change this template use File | Settings | File Templates.
 */
public class EventTile
        extends SimpleTile
        //DynamicForm
{

    public void setAttribute(String attribute, Integer value, boolean allowPostCreate)
    {
        super.setAttribute(attribute,value,allowPostCreate);
    }

    public void setAttribute(String attribute, String value, boolean allowPostCreate)
    {
        super.setAttribute(attribute,value,allowPostCreate);
    }

    public void setBackgroundColor(String backgroundColor)
    {
        super.setBackgroundColor(backgroundColor);
    }

    private Canvas anim;


    public EventTile()
    {
        this.setBorder("1px solid #6a6a6a");
        this.setCanAcceptDrop(true);

//        addDrawHandler(new DrawHandler() {     //TODO Рисует дополнительную информацию
//            public void onDraw(DrawEvent event)
//            {
//
//
//
////                String comment=record.getAttribute(StripCNST.COMMENT); TODO Все нормально записи присутсвуют полностью их= не надо отображать в поля кстати в гриде
//
//                anim = new Canvas();
//                anim.setOverflow(Overflow.HIDDEN);
//                anim.setBorder("1px solid #6a6a6a");
//                anim.setBackgroundColor("#C3D9FF");
//                anim.setSmoothFade(true);
////                anim.setContents("<b>" + evid + "</b>");
//                anim.setLeft(1);
//                anim.setTop(2);
//                anim.setWidth(50);
//                anim.setHeight(17);
//                anim.setCanDrag(true);
//                anim.setCanDragReposition(true);
//
////                correctView(-1);
//
//                EventTile.this.addChild(anim);
//
//            }
//        });


        addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
//                TileRecord record = CarTile.this.getRecord();
//                String evid=record.getAttribute(StripCNST.EVENTID);
            }
        });

        addDropHandler(new DropHandler()
        {
            @Override
            public void onDrop(DropEvent event)
            {
                Widget par = EventTile.this.getParentCanvas();
                if (par instanceof MyTileGrid)
                {
                    final DropHandler tileDropHandler = ((MyTileGrid) par).getTileDropHandler();
                    if (tileDropHandler!=null)
                        tileDropHandler.onDrop(event);
                }
            }
        });
    }

    public void correctView(int i)
    {
        this.setBackgroundColor("#22AAFF");

        Record record = EventTile.this.getRecord();

        String rowStyle = record.getAttribute(TablesTypes.ROW_STYLE);
        if (rowStyle != null && rowStyle.length() > 0) {
            String[] back2color = rowStyle.trim().split(":");
            if (back2color.length == 2) {
                if (back2color[1].endsWith(";"))
                    back2color[1] = back2color[1].substring(0, back2color[1].length() - 1);
                this.setBackgroundColor(back2color[1]);
            }
        }


        String evId = record.getAttribute(StripCNST.EVENTID);
        if (i>=0)
            record.setAttribute(TablesTypes.ORDIX, i);
        if (anim==null)
        {
            anim = new Canvas();
            anim.setOverflow(Overflow.HIDDEN);
            anim.setBorder("1px solid #6a6a6a");
            anim.setBackgroundColor("#C3D9FF");
            anim.setSmoothFade(true);
            anim.setLeft(1);
            anim.setTop(2);
            anim.setWidth(50);
            anim.setHeight(17);
            anim.setCanDrag(true);
            anim.setCanDragReposition(true);
            EventTile.this.addChild(anim);
        }
        anim.setContents("<b>" + evId + "</b>");
    }

}
