package com.mycompany.client.test.t1;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.mycompany.client.test.Demo.EventTile;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tile.TileGrid;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 10.09.14
 * Time: 15:05
 * Грид для отображения ленты событий
 */
public class MyTileGrid extends TileGrid
{

public Canvas getTile(int recordNum)
{
    final Canvas tile = super.getTile(recordNum);
    if (tile instanceof EventTile)
        ((EventTile) tile).correctView(-1);
    return tile;
}

public Canvas getTile(Record record)
{
    final Canvas tile = super.getTile(record);
    if (tile instanceof EventTile)
        ((EventTile) tile).correctView(-1);
    return tile;
}

//    public void removeData(Record data)
//    {
//        final int ixStart=data.getAttributeAsInt(TablesTypes.ORDIX);
//        super.removeData(data);
//
//        Timer timer=new Timer()
//        {
//            @Override
//            public void run()
//            {
//                int ln2=MyTileGrid.this.getRecordList().toArray().length;
//                for (int i=ixStart;i<ln2;i++)
//                {
//                    EventTile tile = (EventTile)MyTileGrid.this.getTile(i);
//                    tile.correctView(i);
//                }
//            }
//        };
//        timer.schedule(100);
//
//    }

    public DropHandler getTileDropHandler() {
        return tileDropHandler;
    }

    public void setTileDropHandler(DropHandler tileDropHandler) {
        this.tileDropHandler = tileDropHandler;
    }

    private DropHandler tileDropHandler;




    public MyTileGrid()
    {

//        addDropCompleteHandler(new DropCompleteHandler()
//        {
//            @Override
//            public void onDropComplete(DropCompleteEvent event)
//            {
//
//                final Record[] recs = event.getTransferredRecords();
//
//                Timer timer=new Timer()
//                {
//                    @Override
//                    public void run()
//                    {
//
//                        for (Record rec : recs)
//                        {
//                            int ix = MyTileGrid.this.getRecordIndex(rec);
//                            int wasIx=rec.getAttributeAsInt(TablesTypes.ORDIX);
//
//                            int min=wasIx<=ix?wasIx:ix;
//                            int max=wasIx>=ix?wasIx:ix;
//                            for (int i=min;i<=max;i++)
//                            {
//                                EventTile tile = (EventTile)MyTileGrid.this.getTile(i);
//                                tile.correctView(i);
//                            }
//                        }
//                    }
//                };
//                timer.schedule(100);
//            }
//
//        });

    }

    public MyTileGrid(JavaScriptObject jsObj) {
        super(jsObj);
    }

}
