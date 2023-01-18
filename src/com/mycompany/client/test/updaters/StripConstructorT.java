package com.mycompany.client.test.updaters;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.updaters.BGridConstructor;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.StripCNST;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.*;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 04.09.14
 * Time: 13:48
 * Апдейтер грида для визуализации ленты событий
 */
public class StripConstructorT extends BGridConstructor
{

    protected void setOptions(Record gridOptions)
    {
        super.setOptions(gridOptions);
        //getGrid().setShowHeader(false);
        getGrid().setWidth(220);
    }

    boolean isShowText =true;

    public void setHeaderGrid(Record gridOptions) throws SetGridException
    {
        setOptions(gridOptions);
        ListGridField[] fields = extractFields(gridOptions);

        for (ListGridField field : fields)
        {
            if (
                    field.getName().equals(StripCNST.EVENTID)
                    ||
                    field.getName().equals(StripCNST.EVENT)
                    ||
                    field.getName().equals(StripCNST.SERV)
                    ||
                    field.getName().equals(StripCNST.PLACE)
                )
            {
                if (field.getName().equals(StripCNST.SERV))
                {
                    field.setCellFormatter(new CellFormatter()
                    {
                        public String format(Object value, ListGridRecord record, int rowNum, int colNum)
                        {
                            if (value==null)
                                value="-";
                            String val = " ( " + value + " )";
                            if ( val.contains(StripCNST.WAY_NM))
                                return Canvas.imgHTML("service/link.png") + (isShowText ?val:"");
                            else if ( val.contains(StripCNST.EL_NAME))
                                return Canvas.imgHTML("service/lightning.png") + (isShowText ?val:"");
                            else if ( val.contains(StripCNST.CTRL_NAME))
                                return Canvas.imgHTML("service/ipod_cast.png") + (isShowText ?val:"");
                            else if ( val.contains(StripCNST.VAG_NAME))
                                return Canvas.imgHTML("service/lorry.png") + (isShowText ?val:"");
                            else
                                return val;
                        }
                    });
                }
                else if (field.getName().equals(StripCNST.EVENT))
                {
                    field.setCellFormatter(new CellFormatter()
                    {
                        public String format(Object value, ListGridRecord record, int rowNum, int colNum)
                        {

                            if (value==null)
                                value="-";
                            String val = " ( " + value + " )";


                            if ( val.contains(StripCNST.WIN_NAME))
                                return Canvas.imgHTML("win/time.png") + (isShowText ?val:"");
                            else if ( val.contains(StripCNST.REFUSE_NAME))
                                return Canvas.imgHTML("ref/remove.png") + (isShowText ?val:"");
                            else if ( val.contains(StripCNST.WARN_NAME))
                                return Canvas.imgHTML("warn/exclamation.png") + (isShowText ?val:"");
                            else if ( val.contains(StripCNST.VIOL_NAME))
                                return Canvas.imgHTML("viol/error.png") + (isShowText ?val:"");
                            else
                                return val;



                        }
                    });
                }
                field.setHidden(false);
//TODO Важность события, сделать подсветкой
//
//TODO Далее развернуть события при подводе к нему курсора как показано в примере
//TODO По нажатию распределять по фильтрам -> Диспетчерам занесенным в БД.
                final Menu menu = new Menu();
                    menu.setShowShadow(true);
                    menu.setShadowDepth(10);

                    final MenuItem removeItem = new MenuItem("Скрыть заголовок");//, "icons/16/document_plain_new.png", "Ctrl+N");
                    final MenuItem addItem = new MenuItem("Показать заголовок");//, "icons/16/document_plain_new.png", "Ctrl+N");
                    final MenuItem iconsOnlyItem = new MenuItem("Только иконки");//, "icons/16/document_plain_new.png", "Ctrl+N");

                    removeItem.addClickHandler(
                            new ClickHandler() {
                                public void onClick(MenuItemClickEvent event)
                                {
                                    boolean isShown = !getGrid().getShowHeader();
                                    getGrid().setShowHeader(isShown);
                                    menu.removeItem(removeItem);
                                    menu.addItem(addItem);
                                    //removeItem.setTitle(isShown?"Скрыть заголовок":"Показать заголовок");
                                }
                            });

                    iconsOnlyItem.addClickHandler(
                            new ClickHandler()
                            {
                                public void onClick(MenuItemClickEvent event)
                                {
                                    isShowText = !isShowText;
                                    getGrid().redraw();
                                }
                            });

                    menu.setItems(removeItem, iconsOnlyItem);


                    addItem.addClickHandler(
                            new ClickHandler() {
                                public void onClick(MenuItemClickEvent event)
                                {
                                    boolean isShown = !getGrid().getShowHeader();
                                    getGrid().setShowHeader(isShown);
                                    menu.removeItem(addItem);
                                    menu.addItem(removeItem,0);
                                }
                            });



                getGrid().setContextMenu(menu);

                getGrid().setCanHover(true);
                getGrid().setShowHoverComponents(true);

//                getGrid().setHoverCustomizer(new HoverCustomizer() {
//                    @Override
//                    public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum)
//                    {
//                        String eventName=record.getAttribute(StripCNST.EVENT);
//                        String dorName=record.getAttribute(StripCNST.DOR_NAME);
//                        String predId=record.getAttribute(StripCNST.PRED_ID);
//
//                        String ND=record.getAttribute(StripCNST.ND);
//                        String KD=record.getAttribute(StripCNST.KD);
//
//
//                        String PLACE=record.getAttribute(StripCNST.PLACE);
//
//                        String COMMENT=record.getAttribute(StripCNST.COMMENT);
//
//
//                        return
//                        "<table>\n" +
//                                "<tr>\n" +
//                                "<td>"+eventName+"</td>\n" +
//                                "</tr>\n" +
//                                "<tr>\n" +
//                                "<td>"+dorName+","+predId+"</td>\n" +
//                                "</tr>\n" +
//                                "<tr>\n" +
//                                "<td>"+ND+"-"+KD+"</td>\n" +
//                                "</tr>\n" +
//
////                                "<tr>\n" +
////                                "<td>"+PLACE+"</td>\n" +
////                                "</tr>\n" +
////
////                                "<tr>\n" +
////                                "<td>"+COMMENT+"</td>\n" +
////                                "</tr>\n" +
//
//                                "</table>";
//
//                    }
//                });


                ListGridWithDesc.IHoverGetter hoverGetter = new ListGridWithDesc.IHoverGetter()
                {
                    public Canvas getCellHoverComponent(ListGrid grid, Record record, Integer rowNum, Integer colNum)
                    {

                        DetailViewer detailViewer = new DetailViewer();
                        detailViewer.setWidth(200);
                        //detailViewer.setHeaderStyle("height:0px");

                        DetailViewerField fEventName = new DetailViewerField("eventName", "1");

                        DetailViewerField fDorName = new DetailViewerField("period", "2");
                        DetailViewerField fPlace = new DetailViewerField("place", "3");
                        DetailViewerField fComment = new DetailViewerField("Comment", "4");

                        detailViewer.setFields(fEventName, fDorName, fPlace, fComment);

                        String eventName=record.getAttribute(StripCNST.EVENT);
                        String dorName=record.getAttribute(StripCNST.DOR_NAME);
                        String predId=record.getAttribute(StripCNST.PRED_ID);

                        String ND=record.getAttribute(StripCNST.ND);
                        String KD=record.getAttribute(StripCNST.KD);
                        String Place=record.getAttribute(StripCNST.PLACE);
                        String Comment=record.getAttribute(StripCNST.COMMENT);


                        Record r=new Record();
                        r.setAttribute("eventName",eventName+"<br><b>"+dorName+","+predId);
                        r.setAttribute("period",ND+" "+KD);
                        r.setAttribute("place",Place);
                        r.setAttribute("Comment",Comment);

                        detailViewer.setData(new Record[]{r});
                        return detailViewer;
                    }
                };
                getGrid().setHoverGetter(hoverGetter);

//TODO Далее необходимо предусмотреть возможность кастомизации иконок,  именно выбор и загрузка новых в систему
//TODO и соотношение их с событиями и службами.


            }
            else
                field.setHidden(true);
        }
        getGrid().setFields(fields);
    }


    public StripConstructorT(ListGridWithDesc grid) {
        super(grid);
    }
}
