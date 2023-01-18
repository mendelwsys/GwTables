package com.mycompany.client.test.informer;

import com.google.gwt.core.client.GWT;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.test.Demo.EventTileMetaFactory;
import com.mycompany.client.test.Demo.LentaPage;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 20.03.15
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */
public class ReVokeLenta implements Runnable{
    @Override
    public void run() {

        com.google.gwt.user.client.Window.setTitle("Tiles for informers");
        GWT.create(EventTileMetaFactory.class);

        HLayout mainLayout = new HLayout();
        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();

//        Record r=new Record();
//        String eventName= StripCNST.WIN_NAME;
//        String dorName="ОКТ";
//        String predId="ПЧ-1";
//        String Comment="Передержка";
//
//        String imgSrc="info/semafor_r.gif";
//
//        r.setAttribute("eventName", Canvas.imgHTML(imgSrc));
//        r.setAttribute("period",eventName+"<br><b>"+dorName+","+predId);
//        r.setAttribute("eventName",Canvas.imgHTML(imgSrc));
//        r.setAttribute("period",eventName+"<br><b>"+dorName+","+predId);
//        r.setAttribute("place","ПСКОВ-КЕБ");
//        r.setAttribute("Comment",Comment);

//        EventTile detailViewer = new EventTile();

        //detailViewer.setFields(new ListGridField[0]);
//        detailViewer.setCanDragReposition(true);
//        detailViewer.setDragAppearance(DragAppearance.TARGET);
//        detailViewer.setKeepInParentRect(true);

//        mainLayout.addChild(detailViewer);
       new LentaPage().makePage(mainLayout);

        mainLayout.draw();

    }
}
