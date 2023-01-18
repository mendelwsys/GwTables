package com.mycompany.client.test.t1;

import com.google.gwt.user.client.Timer;
import com.mycompany.client.test.Demo.EventTile;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.DropCompleteEvent;
import com.smartgwt.client.widgets.events.DropCompleteHandler;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.tile.TileGrid;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 10.09.14
 * Time: 15:05
 * Грид для отображения ленты событий
 */
public class MyTileGrid_BU extends TileGrid
{


    public void removeData(Record data)
    {
        final int ixStart=data.getAttributeAsInt(TablesTypes.ORDIX);
        super.removeData(data);

        Timer timer=new Timer()
        {
            @Override
            public void run()
            {
                int ln2=MyTileGrid_BU.this.getRecordList().toArray().length;
                for (int i=ixStart;i<ln2;i++)
                {
                    EventTile tile = (EventTile)MyTileGrid_BU.this.getTile(i);
                    tile.correctView(i);
                }
            }
        };
        timer.schedule(100);

    }

    public DropHandler getTileDropHandler() {
        return tileDropHandler;
    }

    public void setTileDropHandler(DropHandler tileDropHandler) {
        this.tileDropHandler = tileDropHandler;
    }

    private DropHandler tileDropHandler;




    public MyTileGrid_BU()
    {

        addDropCompleteHandler(new DropCompleteHandler()
        {
            @Override
            public void onDropComplete(DropCompleteEvent event)
            {

                final Record[] recs = event.getTransferredRecords();

                Timer timer=new Timer()
                {
                    @Override
                    public void run()
                    {

                        for (Record rec : recs)
                        {
                            int ix = MyTileGrid_BU.this.getRecordIndex(rec);
                            int wasIx=rec.getAttributeAsInt(TablesTypes.ORDIX);

                            int min=wasIx<=ix?wasIx:ix;
                            int max=wasIx>=ix?wasIx:ix;
                            for (int i=min;i<=max;i++)
                            {
                                EventTile tile = (EventTile)MyTileGrid_BU.this.getTile(i);
                                tile.correctView(i);
                            }
                        }
                    }
                };
                timer.schedule(100);
            }

        });

    }

//    public MyTileGrid(JavaScriptObject jsObj) {
//        super(jsObj);
//    }

}
