package com.mycompany.client.test.staf;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.07.14
 * Time: 12:55
 * Описание ленты событий
 */
public class LentaPage_BU
{

//    private ListGrid listGrid;
//    public void makePage(final HLayout hLayout)
//    {
//        final PortalLayout portalLayout = new PortalLayout();
//        portalLayout.setOverflow(Overflow.VISIBLE);
//        portalLayout.setColumnOverflow(Overflow.VISIBLE);
//        portalLayout.setPreventColumnUnderflow(false);
//        portalLayout.setNumColumns(3);
//        portalLayout.setShowColumnMenus(true);
//
//        //portalLayout.addColumn();
//        //Menu menu=portalLayout.getContextMenu();
//
//        portalLayout.setColumnBorder("0");
//
//        listGrid = new ListGrid();
//        listGrid.setAutoDraw(false);
//        listGrid.setWidth(120);
//        listGrid.setHeight(20);
//        // autosize to fit the list, instead of scrolling
//        listGrid.setOverflow(Overflow.VISIBLE);
//        listGrid.setBodyOverflow(Overflow.VISIBLE);
//        listGrid.setLeaveScrollbarGap(false);
//
//        // hide the column headers
//        listGrid.setShowHeader(false);
//
//        // disable normal row selection behaviors
//        listGrid.setSelectionType(SelectionStyle.NONE);
//
//        ListGridRecord records[] = new ListGridRecord[10];
//        records[0] = new ListGridRecord();
//        records[0].setAttribute("portletName", "MyPortlet 1");
//        records[1] = new ListGridRecord();
//        records[1].setAttribute("portletName", "MyPortlet 2");
//        records[2] = new ListGridRecord();
//        records[2].setAttribute("portletName", "MyPortlet 3");
//        records[3] = new ListGridRecord();
//        records[3].setAttribute("portletName", "MyPortlet 4");
//        records[4] = new ListGridRecord();
//        records[4].setAttribute("portletName", "MyPortlet 5");
//        records[5] = new ListGridRecord();
//        records[5].setAttribute("portletName", "MyPortlet 6");
//        records[6] = new ListGridRecord();
//        records[6].setAttribute("portletName", "MyPortlet 7");
//        records[7] = new ListGridRecord();
//        records[7].setAttribute("portletName", "MyPortlet 8");
//        records[8] = new ListGridRecord();
//        records[8].setAttribute("portletName", "MyPortlet 9");
//        records[9] = new ListGridRecord();
//        records[9].setAttribute("portletName", "MyPortlet 10");
//        // fake portlet list for self-contained example
//        // The real list could be included inline, or loaded on the fly from the server.
//        // This data can include whatever attributes you want to use for these portlet,
//        // e.g. feed URLs, icons, update frequency...
//        listGrid.setData(records);
//
//        ListGridField portletNameField = new ListGridField("portletName");
//        portletNameField.setCellFormatter(new CellFormatter()
//        {
//            public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
//                return Canvas.imgHTML("[SKIN]actions/view.png") + " " + value;
//            }
//        });
//        listGrid.setFields(portletNameField);
//
//        // global for convenient single setting of multiple animation times in this example
//        // default 750
//        listGrid.setAnimateTime(750);
//
//        listGrid.addRecordClickHandler(new RecordClickHandler() {
//            public void onRecordClick(RecordClickEvent event) {
//                final ListGridRecord record = (ListGridRecord)event.getRecord();
//                record.setEnabled(false);
//                listGrid.refreshRow(event.getRecordNum());
//                final Portlet newPortlet = new Portlet();
//                newPortlet.setTitle(record.getAttributeAsString("portletName"));
//                newPortlet.setShowShadow(false);
//                // enable predefined component animation
//                newPortlet.setAnimateMinimize(true);
//                // Window is draggable with "outline" appearance by default.
//                // "target" is the solid appearance.
//                newPortlet.setDragAppearance(DragAppearance.OUTLINE);
//                // customize the appearance and order of the controls in the window header
//                // (could do this in load_skin.js instead)
//                newPortlet.setHeaderControls(HeaderControls.HEADER_ICON, HeaderControls.HEADER_LABEL,
//                    HeaderControls.MINIMIZE_BUTTON,  HeaderControls.CLOSE_BUTTON);
//                // show either a shadow, or translucency, when dragging a portlet
//                // (could do both at the same time, but these are not visually compatible effects)
//                newPortlet.setDragOpacity(30);
//                // these settings enable the portlet to autosize its height only to fit its contents
//                // (since width is determined from the containing layout, not the portlet contents)
//                newPortlet.setHeight(140);
//                newPortlet.setOverflow(Overflow.VISIBLE);
//                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
//                map.put("overflow", "VISIBLE");
//                newPortlet.setBodyDefaults(map);
//                Label label = new Label();
//                label.setAutoDraw(false);
//                label.setAlign(Alignment.CENTER);
//                label.setLayoutAlign(Alignment.CENTER);
//                label.setContents(record.getAttributeAsString("portletName")+" contents");
//                // simple fake portlet contents - could put anything here
//                newPortlet.addItem(label);
//                newPortlet.addCloseClickHandler(new CloseClickHandler() {
//                    public void onCloseClick(CloseClickEvent event) {
//                        closePortlet(newPortlet, record);
//                    }
//                });
//                newPortlet.hide();
//
//                int fewestPortlets = 999999;
//                int fewestPortletsColumn = 0;
//                // find the column with the fewest portlets
//                Portlet portletArray[][][] = portalLayout.getPortletArray();
//                for (int i=0; i < portletArray.length; i++) {
//                    int numPortlets = portletArray[i].length;
//                    if (numPortlets < fewestPortlets) {
//                        fewestPortlets = numPortlets;
//                        fewestPortletsColumn = i;
//                    }
//                }
//                portalLayout.addPortlet(newPortlet, fewestPortletsColumn, 0);
//
//
//
//
//
//                // create an outline around the clicked row
//                final Canvas outline = new Canvas();
//                outline.setLeft(listGrid.getPageLeft());
//                outline.setTop(listGrid.getRowPageTop(event.getRecordNum()));
//                outline.setWidth(listGrid.getVisibleWidth());
//                outline.setHeight(listGrid.getDrawnRowHeight(event.getRecordNum()));
//                outline.setBorder("2px solid #8289A6");
//                outline.draw();
//                outline.bringToFront();
//                outline.animateRect(newPortlet.getPageLeft(), newPortlet.getPageTop(),
//                        portalLayout.getColumnWidth(fewestPortletsColumn), newPortlet.getVisibleHeight(),
//                        new AnimationCallback() {
//                            public void execute(boolean earlyFinish)
//                            {
//                                // callback at end of animation - destroy placeholder and outline; show the new portlet
//                                outline.destroy();
//                                newPortlet.show();
//                            }
//                        }, 750);
//            }
//        });
////        HLayout hLayout = new HLayout();
////        hLayout.setWidth("100%");
////        hLayout.setHeight("100%");
////        hLayout.setLayoutMargin(10);
////        hLayout.setMembersMargin(10);
//        hLayout.addMembers(listGrid, portalLayout);
//    }
//
//    private void closePortlet(final Portlet portlet, final ListGridRecord portletRecord)
//    {
//        final int rowNum = listGrid.getDataAsRecordList().indexOf(portletRecord);
//
//        // create an outline around the portlet
//        final Canvas outline = new Canvas();
//        outline.setAutoDraw(false);
//        outline.setBorder("2px solid #8289A6");
//        outline.setRect(portlet.getPageRect());
//        outline.bringToFront();
//
//        // swap the portlet with a blank spacer element
//        // (disabling relayout temporarily to prevent animation during the swap)
//        Canvas portalColumn = portlet.getParentElement();
//        portlet.hide();
//        final LayoutSpacer spacer = new LayoutSpacer();
//        spacer.setRect(portlet.getRect());
//        portalColumn.addChild(spacer);
//
//        // animateHide (shrink) the spacer to collapse this space in the content area
//        spacer.setAnimateHideTime(listGrid.getAnimateTime());
//        spacer.setAnimateHideTime(10);
//
//        // simultaneously animate the portlet outline down to the row in this portletList
//        outline.draw();
//        outline.animateRect(listGrid.getPageLeft(), listGrid.getRowPageTop(rowNum),
//           listGrid.getVisibleWidth(), listGrid.getDrawnRowHeight(rowNum),
//               new AnimationCallback() {
//                   public void execute(boolean earlyFinish) {
//                       // callback at end of animation - destroy outline, portlet, and spacer;
//                       // also enable and refresh the row in the portletList so it does not show the special
//                       // style (and so it can be clicked again)
//                       outline.destroy();
//                       spacer.destroy();
//                       portlet.destroy();
//                       portletRecord.setEnabled(true);
//                       listGrid.refreshRow(rowNum);
//                   }
//               }, 750);
//    }
}
