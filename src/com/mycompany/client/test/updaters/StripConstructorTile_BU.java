package com.mycompany.client.test.updaters;

import com.mycompany.client.test.Demo.EventTile;
import com.mycompany.client.test.Demo.MyRecord;
import com.mycompany.client.updaters.IGridConstructor;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.DiagramDesc;
import com.mycompany.common.StripCNST;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.DataBoundComponent;
import com.smartgwt.client.widgets.events.DragRepositionMoveEvent;
import com.smartgwt.client.widgets.events.DragRepositionMoveHandler;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.viewer.DetailFormatter;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 04.09.14
 * Time: 13:48
 * Апдейтер грида для визуализации ленты событий
 */
public class StripConstructorTile_BU implements IGridConstructor
{

    public DataSource getFieldsMetaDS() {
        return fieldMetaDS;
    }

    public DataSource getFilterDS() {
        return filterDS;
    }

    private DataSource fieldMetaDS = new DataSource();
    private DataSource filterDS = new DataSource();

    public boolean isMetaWasSet() {
        return metaWasSet;
    }

    public void setMetaWasSet(boolean metaWasSet) {
        this.metaWasSet = metaWasSet;
    }

    boolean metaWasSet=false;

    protected String addDataUrlId;
    @Override
    public void setAddIdDataSource(String addDataUrlId)
    {
        this.addDataUrlId=addDataUrlId;
    }
    @Override
    public String getAddIdDataSource()
    {
        return this.addDataUrlId;
    }

    public MyRecord toMyRecord(Record record) {
        Map recordProperties = record.toMap();
        Object v = recordProperties.remove(TablesTypes.ROW_STYLE);
        MyRecord myRecord = new MyRecord();
        for (Object o : recordProperties.keySet()) {
            Object value = recordProperties.get(o);


            if (value instanceof Map) {
                Map value1 = (Map) value;
                myRecord.setAttribute((String) o, value1.get("link"));
                myRecord.setLinkText((String) value1.get("linkText"));
            } else
                myRecord.setAttribute((String) o, value);
            //myRecord.setLinkText();

//            grid.getField()
//            if (record.getAttribute())
//            myRecord.setLinkText();
        }

        if (v != null)
            myRecord.setRowStyle((String) v);
        return myRecord;
    }



    private TileGrid grid;


    @Override
    public ListGridField[] getAllFields()
    {
        return new ListGridField[0];
    }

    public DataBoundComponent getDataBoundComponent()
    {
        return getGrid();
    }

    public TileGrid getGrid() {
        return grid;
    }


     boolean isShowText =false;

    protected void setGridAttributes(Record gridOptions)
    {
        int headerHeight = gridOptions.getAttributeAsInt("headerHeight");

        TileGrid grid = getGrid();
//        grid.setBorder("2px solid blue");

        grid.setTileConstructor(EventTile.class.getName());

//        grid.setTileVMargin(1);
//        grid.setTileHMargin(1);
        grid.setTileMargin(0);

        grid.setCanDrag(true);
        grid.setCanDragReposition(true);
        grid.setCanReorderTiles(true);
        grid.addDragRepositionMoveHandler(new DragRepositionMoveHandler()
        {
            public void onDragRepositionMove(DragRepositionMoveEvent event) {
                int x = event.getX();
                int y = event.getY();
                //TileGrid grid1 = getGrid();

                if (x < 15 && y < 15)
                {
                        String html = Canvas.imgHTML("win/time_go.png");
                        EventHandler.setDragTracker(html);
//                    grid1.setDragAppearance(DragAppearance.OUTLINE);
                }
                else
                {
                    //String html = Canvas.imgHTML("img/time_go.png");
                    EventHandler.setDragTracker(
                            "<div style=\"width: 10px; height: 10px; background: black\">\n" +
                                    "</div>\n");

//                    grid1.setDragAppearance(DragAppearance.TRACKER);
                }
            }
        });
        grid.setCanDrop(true);

        grid.setTileWidth(180);
        grid.setTileHeight(80);
        grid.setWidth(230);
    }



    /*
    Properties()
     */

    protected ListGridField[] extractFields(Record gridOptions)
    {
        Record[] records = gridOptions.getAttributeAsRecordArray("chs");
        ListGridField[] fields = new ListGridField[records.length];
        for (int i = 0, recordsLength = records.length; i < recordsLength; i++)
        {
            Record record = records[i];
            //Формируем заголовки
            ListGridField field = new ListGridField();
            field.setName(record.getAttribute("name"));

            field.setTitle(record.getAttribute("title"));
            ListGridFieldType type = ListGridFieldType.valueOf(record.getAttribute("type"));
            field.setType(type);
            field.setHidden(!record.getAttributeAsBoolean("visible"));
            field.setAutoFitWidth(record.getAttributeAsBoolean("autofit"));

            String salignment = record.getAttribute("alignment");
            if (salignment != null && salignment.length() > 0)
                field.setAlign(Alignment.valueOf(salignment));

            if (type.equals(ListGridFieldType.LINK))
                field.setLinkText(record.getAttribute("linkText"));
            fields[i] = field;
        }
        return fields;
    }

    public void setDiagramDesc(Map mapDesc) throws SetGridException {

        if (mapDesc != null)
        {

            DiagramDesc ddesc = new DiagramDesc();
            ddesc.setType((String) mapDesc.get("type"));
            ArrayList tuples = (ArrayList) mapDesc.get("tuples");
            ddesc.setTuples((Map[]) tuples.toArray(new Map[tuples.size()]));

            ArrayList columnDesc = (ArrayList) mapDesc.get("columnDesc");
            String[][] arr2title = new String[columnDesc.size()][];
            for (int i = 0, titleSize = columnDesc.size(); i < titleSize; i++) {
                Object o = columnDesc.get(i);
                ArrayList attr2title = (ArrayList) o;
                arr2title[i] = (String[]) attr2title.toArray(new String[attr2title.size()]);
            }
            ddesc.setColumnDesc(arr2title);
            ddesc.setTitle((String) mapDesc.get("title"));
            ddesc.setwType((String) mapDesc.get("wType"));
           // getGrid().setDesc(ddesc);
        }
    }

    public void updateDataGrid(Record[] data, boolean resetAll) throws SetGridException
    {
        RecordList rl = extractData4Update(data);

        getGrid().setData(rl.toArray());
        //getGrid().setCacheData(rl.toArray());
    }

    @Override
    public void setDataBoundComponent(DataBoundComponent grid) {
        this.grid= (TileGrid) grid;
    }


    protected RecordList extractData4Update(Record[] data)
    {
        RecordList rl = getGrid().getDataAsRecordList();
        if (rl==null)
            rl= new RecordList();

        List<Integer> toAddList = new LinkedList<Integer>();
        for (int i = 0; i < data.length; i++)
        {
            Record record = data[i];

            String id = record.getAttributeAsString(TablesTypes.KEY_FNAME);
            int actual = record.getAttributeAsInt(TablesTypes.ACTUAL);

            //((ListGridRecord)record).setLinkText();

            int inRlIx = rl.findIndex(TablesTypes.KEY_FNAME, id);
            if (inRlIx >= 0) {
                if (actual > 0)
                    rl.set(inRlIx, toMyRecord(record));
                else
                    rl.removeAt(inRlIx);
            } else if (actual > 0)
                toAddList.add(i);
        }

        for (Integer i : toAddList)
            rl.add(toMyRecord(data[i]));
        return rl;
    }

    public void setHeaderGrid(Record[] gridOptions) throws SetGridException
    {
        setGridAttributes(gridOptions[0]);
        ListGridField[] fields = extractFields(gridOptions[0]);

        List<DetailViewerField> df= new LinkedList<DetailViewerField>();


        TileGrid grid = getGrid();
        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; i++) {
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


    public StripConstructorTile_BU(TileGrid grid)
    {
        this.grid=grid;
    }
}
