package com.mycompany.client.test.staf;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.07.14
 * Time: 12:55
 * Описание ленты событий
 */
public class LentaPage_BU1
{

//    /*
//        TODO Далее редактирование будет здесь надо написать новую инициализационную процедуру
//        TODO Или модифицировать старую что бы лента событий у нас была выстроена в одну линию
//        TODO Желательно так же пиктограмцы придумать для событий разного типа.
//    */
//    //private ListGrid listGrid;
//    public void makePage(final HLayout hLayout)
//    {
//
//        final PortalLayout portalLayout = new PortalLayout();
//        portalLayout.setOverflow(Overflow.VISIBLE);
//        portalLayout.setColumnOverflow(Overflow.VISIBLE);
//        portalLayout.setPreventColumnUnderflow(false);
//        portalLayout.setNumColumns(3);
//        portalLayout.setShowColumnMenus(true);
//        portalLayout.setColumnBorder("0");
//
//        ListGridWithDesc listGrid = new ListGridWithDesc();
////        listGrid.setHeight(20);
//        // autosize to fit the list, instead of scrolling
//        listGrid.setOverflow(Overflow.HIDDEN);
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
//        listGrid.setWidth100();
//        listGrid.setHeight100();
//        listGrid.setShowAllRecords(true);
////        listGrid.setHeaderHeight(35);
//
//        listGrid.setWrapCells(true);
//        listGrid.setCellHeight(35);
//
//
////        listGrid.setCanAcceptDrop(true);
////        listGrid.setCanAcceptDroppedRecords(true);
//
//
//        final String headerURL = "thead.jsp";
//        final String dataURL = "data.jsp";
//        final MyDSCallback dataCallBack = DemoApp01.gridMetaProvider.initGrid(new StripConstructorT(listGrid), TablesTypes.LENTA, headerURL, dataURL);
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
//        TileGrid tl = new TileGrid();
//        tl.addTile();
//
//        hLayout.addMembers(listGrid);
//    }
}
