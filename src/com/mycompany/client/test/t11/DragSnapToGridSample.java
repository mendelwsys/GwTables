package com.mycompany.client.test.t11;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.03.15
 * Time: 12:28
 * To change this template use File | Settings | File Templates.
 */
    import com.mycompany.client.apps.App.OptionsViewers;
    import com.mycompany.common.StripCNST;
    import com.smartgwt.client.data.Record;
    import com.smartgwt.client.types.DragAppearance;
    import com.smartgwt.client.widgets.Canvas;
    import com.smartgwt.client.widgets.Window;
    import com.smartgwt.client.widgets.layout.VStack;
    import com.smartgwt.client.widgets.viewer.DetailViewer;
    import com.smartgwt.client.widgets.viewer.DetailViewerField;


public class DragSnapToGridSample implements Runnable {

        @Override
        public void run()
        {

            final Window winModal = OptionsViewers.createEmptyWindow("TECT");
            winModal.setAutoSize(false);
            winModal.setCanDragResize(true);


            final Canvas gridCanvas = new Canvas();
            gridCanvas.setBorder("1px solid blue");
            gridCanvas.setWidth100();
            gridCanvas.setHeight100();

//            gridCanvas.setWidth(500);
//            gridCanvas.setHeight(500);
//            gridCanvas.setChildrenSnapResizeToGrid(true);
////            gridCanvas.setChildrenSnapToGrid(true);
//            gridCanvas.setOverflow(Overflow.HIDDEN);

            {
                VStack informer = new VStack();
                //informer.setBorder("1px solid red");
                informer.setShowEdges(true);
                informer.setBackgroundColor("#22FF33");


                informer.setCanDragReposition(true);
                informer.setCanDragResize(true);
                informer.setDragAppearance(DragAppearance.TARGET);
                informer.setKeepInParentRect(true);
                informer.setDropLineThickness(4);
//
//            {
//                final Img img = new Img("win/time.png");
//                img.setWidth(16);
//                img.setHeight(16);
//                img.setLayoutAlign(Alignment.CENTER);
//                informer.addMember(img);
//            }
//
//
//            {
//                final Img img = new Img("info/semafor_g.gif");
//                img.setWidth(16);
//                img.setHeight(16);
//                img.setLayoutAlign(Alignment.CENTER);
//                informer.addMember(img);
//            }
//
//            {
//                Label label1 = new Label();
//                label1.setShowEdges(false);
//                label1.setMargin(0);
//                label1.setContents("Ок");
//                label1.setLayoutAlign(Alignment.CENTER);
//                label1.setWidth(1);
//                label1.setHeight(1);
//                informer.addMember(label1);
//            }
//

                Canvas detailViewer1 = setInformer("info/semafor_r.gif", "ПЧ-1", "MyRedStyle");
                gridCanvas.addChild(detailViewer1);


                Canvas detailViewer2 = setInformer("info/semafor_g.gif","ПЧ-2", "MyGreenStyle");
                detailViewer2.setLeft(80);
                gridCanvas.addChild(detailViewer2);


//                informer.setAutoWidth();
//                informer.setAutoHeight();
//                gridCanvas.addChild(informer);
            }

//        {
//            Label label1 = new Label();
//            label1.setWidth(40);
//            label1.setHeight(40);
//            label1.setMargin(0);
//
//            label1.setAlign(Alignment.CENTER);
////            label1.setContents("Drag or Resize me");
//            label1.setBackgroundColor("#22AAFF");
//
//            label1.setShowEdges(true);
//            label1.setCanDragReposition(true);
//            label1.setCanDragResize(true);
//            label1.setDragAppearance(DragAppearance.OUTLINE);
//            label1.setKeepInParentRect(true);
//            label1.setIcon("info/semafor_g.gif");
//
//            gridCanvas.addChild(label1);
////            winModal.addItem(label1);
////            vl.addMember(label1);
//        }

            winModal.addItem(gridCanvas);
//

//            {
//                DropBox label1 = new DropBox();
//                label1.setWidth(16);
//                label1.setBackgroundColor("#00FF00");
//                label1.setLeft(90);
//                gridCanvas.addChild(label1);
//
//                final Img img = new Img("info/semafor_g.gif");
//                img.setWidth(14);
//                img.setHeight(14);
//                img.setLayoutAlign(Alignment.CENTER);
//
//                label1.addMember(img);
//
//            }

//            DynamicForm gridForm = new DynamicForm();
//            gridForm.setWidth(400);
//            gridForm.setNumCols(4);
//
//            CheckboxItem snapDrag = new CheckboxItem();
//            snapDrag.setValue(true);
//            snapDrag.setTitle("Enable Snap-To-Grid Move");
//            snapDrag.addChangedHandler(new ChangedHandler() {
//
//                @Override
//                public void onChanged(ChangedEvent event) {
//                    gridCanvas.setProperty("childrenSnapToGrid", !gridCanvas.getChildrenSnapToGrid());
//                }
//
//            });
//            CheckboxItem snapResize = new CheckboxItem();
//            snapResize.setValue(true);
//            snapResize.setTitle("Enable Snap To Grid Resize");
//            snapResize.addChangedHandler(new ChangedHandler() {
//
//                @Override
//                public void onChanged(ChangedEvent event) {
//                    gridCanvas.setProperty("childrenSnapResizeToGrid", !gridCanvas.getChildrenSnapResizeToGrid());
//                }
//
//            });
//
//            RadioGroupItem radioGroupHGap = new RadioGroupItem();
//            radioGroupHGap.setTitle("Horizontal snap-to gap");
//            LinkedHashMap<Integer,String> hGapMap = new LinkedHashMap<Integer,String>();
//            hGapMap.put(10, "10 pixels");
//            hGapMap.put(20, "20 pixels");
//            hGapMap.put(50, "50 pixels");
//            radioGroupHGap.setValueMap(hGapMap);
//            radioGroupHGap.setDefaultValue(20);
//            radioGroupHGap.addChangedHandler(new ChangedHandler() {
//
//                @Override
//                public void onChanged(ChangedEvent event) {
//                    gridCanvas.setProperty("snapHGap", Integer.parseInt(String.valueOf(event.getValue())));
//                }
//
//            });
//            RadioGroupItem radioGroupVGap = new RadioGroupItem();
//            radioGroupVGap.setTitle("Vertical snap-to gap");
//            LinkedHashMap<Integer,String> vGapMap = new LinkedHashMap<Integer,String>();
//            vGapMap.put(10, "10 pixels");
//            vGapMap.put(20, "20 pixels");
//            vGapMap.put(50, "50 pixels");
//            radioGroupVGap.setValueMap(vGapMap);
//            radioGroupVGap.setDefaultValue(20);
//            radioGroupVGap.addChangedHandler(new ChangedHandler() {
//
//                @Override
//                public void onChanged(ChangedEvent event) {
//                    gridCanvas.setProperty("snapVGap", Integer.parseInt(String.valueOf(event.getValue())));
//                }
//
//            });
//            gridForm.setFields(snapDrag,snapResize,radioGroupHGap,radioGroupVGap);
//

//            vl.draw();


            winModal.draw();

        }

    private Canvas setInformer(String imgSrc, String predId, String myRedStyle) {

        DetailViewer detailViewer = new DetailViewer();



//                detailViewer.setWidth(200);
//            label1.setBackgroundColor("#22AAFF");

        DetailViewerField fEventName = new DetailViewerField("eventName", "|");

        DetailViewerField fDorName = new DetailViewerField("period", "|")
        {

            public String getAttributeAsString(String property)
            {
                if (property.equals("title"))
                    return super.getAttribute(property);
                else
                    return super.getAttribute(property);
            }
        };
        DetailViewerField fPlace = new DetailViewerField("place", "3");
        DetailViewerField fComment = new DetailViewerField("Comment", "4");



        //detailViewer.setti


        fDorName.setCellStyle(myRedStyle);

        detailViewer.setFields(fEventName, fDorName);//, fPlace, fComment);

        String eventName= StripCNST.WIN_NAME;
        String dorName="ОКТ";
//        String predId="ПЧ-1";
        String Comment="Передержка";

        Record r=new Record();
        r.setAttribute("eventName",Canvas.imgHTML(imgSrc));
//                r.setAttribute("eventName","<img src='images/info/semafor_g.gif' style=\"background-color:#E6E6FA\"/>");
        r.setAttribute("period",eventName+"<br><b>"+dorName+","+predId);
        r.setAttribute("eventName",Canvas.imgHTML(imgSrc));
//                r.setAttribute("eventName","<img src='images/info/semafor_g.gif' style=\"background-color:#E6E6FA\"/>");
        r.setAttribute("period",eventName+"<br><b>"+dorName+","+predId);
//                r.setAttribute("place","<a style=\"background-color:#FF0000\">ПСКОВ-КЕБ</a>");
                r.setAttribute("place","ПСКОВ-КЕБ");
                r.setAttribute("Comment",Comment);


//        Record r1=new Record();
//        r1.setAttribute("eventName",Canvas.imgHTML(imgSrc));
//        r1.setAttribute("period",eventName+"<br><b>"+dorName+","+predId);


        detailViewer.setData(new Record[]{r});

        detailViewer.setCanDragReposition(true);
        detailViewer.setDragAppearance(DragAppearance.TARGET);
        detailViewer.setKeepInParentRect(true);

        return detailViewer;

//                informer.addMember(detailViewer);
    }


    private class DropBox extends VStack {
            public DropBox() {
                setShowEdges(true);
//                setMembersMargin(10);
//                setLayoutMargin(10);
                setCanAcceptDrop(true);
                setAnimateMembers(true);
                setCanDragReposition(true);
                setCanDragResize(true);
                setDragAppearance(DragAppearance.TARGET);
                setKeepInParentRect(true);

                setDropLineThickness(4);
                setAutoHeight();
            }

//            public DropBox(String edgeImage) {
//                this();
//                setEdgeImage(edgeImage);
//            }
       }


    }

