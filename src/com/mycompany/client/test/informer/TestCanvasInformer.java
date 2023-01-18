package com.mycompany.client.test.informer;

import com.google.gwt.core.client.GWT;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.test.Demo.EventTileMetaFactory;

import com.mycompany.common.TablesTypes;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 24.03.15
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public class TestCanvasInformer  implements Runnable {
            @Override
            public void run()
            {

                com.google.gwt.user.client.Window.setTitle("Tiles for informers");
                GWT.create(EventTileMetaFactory.class);

                HLayout mainLayout = new HLayout();
                mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
                mainLayout.setShowEdges(false);
                mainLayout.setHeight100();
                mainLayout.setWidth100();


                Canvas informerCandidate = new Canvas();
                //informerCandidate.setBorder("1px solid blue");
                informerCandidate.setBackgroundColor("#22AAFF");
                informerCandidate.setWidth(150);
                informerCandidate.setHeight(150);


                informerCandidate.setCanDrag(true);
                informerCandidate.setCanDragReposition(true);
                informerCandidate.setKeepInParentRect(true);
                informerCandidate.setCanDragResize(true);
                informerCandidate.setShowShadow(true);


                Canvas anim = new Canvas();
                anim.setOverflow(Overflow.HIDDEN);
                anim.setBorder("1px solid #6a6a6a");
                anim.setBackgroundColor("#C3D9FF");
                anim.setSmoothFade(true);
                anim.setLeft(1);
                anim.setTop(2);
//                anim.setAutoHeight();
//                anim.setAutoWidth();
                anim.setWidth(70);
                anim.setHeight(17);
                anim.setCanDrag(true);
                anim.setCanDragReposition(true);
                anim.setContents("<div align=\"center\"><b>Событие</b></div>");



                final Img img = new Img("warn/exclamation.png");
                img.setWidth(16);
                img.setHeight(16);
                img.setPrompt("Предупреждение");
//                img.setLayoutAlign(Alignment.CENTER);
                img.setCanDragReposition(true);
                img.setKeepInParentRect(true);
                img.setTop(50);
                img.setLeft(50);

                informerCandidate.addChild(img);
                informerCandidate.addChild(anim);

                 mainLayout.addChild(informerCandidate);


                mainLayout.draw();
            }
}
