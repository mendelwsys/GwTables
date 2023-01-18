package com.mycompany.client.test.t10;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.03.15
 * Time: 12:13
 * To change this template use File | Settings | File Templates.
 */

    import com.smartgwt.client.types.Alignment;
    import com.smartgwt.client.types.DragAppearance;
    import com.smartgwt.client.widgets.Canvas;
    import com.smartgwt.client.widgets.Img;
    import com.smartgwt.client.widgets.layout.HStack;
    import com.smartgwt.client.widgets.layout.VStack;

    import com.google.gwt.core.client.EntryPoint;

    public class DragTypesSample implements Runnable {

        @Override
        public void run()
        {
            DropBox gray = new DropBox("edges/gray/6.png");
            gray.addMember(new DragPiece("cube_blue.png", "b"));
            gray.addMember(new DragPiece("cube_green.png", "g"));
            gray.addMember(new DragPiece("cube_yellow.png", "y"));
            gray.setDropTypes("b", "g", "y");

            DropBox blue = new DropBox("edges/blue/6.png");
            blue.addMember(new DragPiece("cube_blue.png", "b"));
            blue.addMember(new DragPiece("cube_green.png", "g"));
            blue.addMember(new DragPiece("cube_yellow.png", "y"));
            blue.setDropTypes("b");

            DropBox green = new DropBox("edges/green/6.png");
            green.addMember(new DragPiece("cube_blue.png", "b"));
            green.addMember(new DragPiece("cube_green.png", "g"));
            green.addMember(new DragPiece("cube_yellow.png", "y"));
            green.setDropTypes("g");

            HStack hStack = new HStack(20);
            hStack.addMember(gray);
            hStack.addMember(blue);
            hStack.addMember(green);

            hStack.draw();
        }



        private class DropBox extends VStack {
            public DropBox() {
                setShowEdges(true);
                setMembersMargin(10);
                setLayoutMargin(10);
                setCanAcceptDrop(true);
                setAnimateMembers(true);
                setDropLineThickness(4);
                setAutoHeight();
            }

            public DropBox(String edgeImage) {
                this();
                setEdgeImage(edgeImage);
            }
        }

        private class DragPiece extends Img {
            public DragPiece() {
                setWidth(48);
                setHeight(48);
                setLayoutAlign(Alignment.CENTER);
                setCanDragReposition(true);
                setCanDrop(true);
                setDragAppearance(DragAppearance.TARGET);
                setAppImgDir("pieces/48/");
            }

            public DragPiece(String src, String dragType) {
                this();
                setSrc(src);
                setDragType(dragType);
            }
        }

    }

