package com.mycompany.client.test.t1;

import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 10.09.14
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public class BoxAnimation
{

    public static void animate(Rectangle fromRect,final Canvas toCanvas,
                final VStack column,int duration)
    {
//                toCanvas.setVisible(false);
//                MyPortalColumn column = portalLayout.addPortlet(newPortlet);
        // create an outline around the clicked button


               final LayoutSpacer placeHolder = new LayoutSpacer();
               placeHolder.setRect(toCanvas.getRect());
               column.addMember(placeHolder,1); // add place holder to column top

               final Canvas outline = new Canvas();
                outline.setLeft(fromRect.getLeft());
                outline.setTop(fromRect.getTop());
                outline.setWidth(fromRect.getWidth());
                outline.setHeight(fromRect.getHeight());
                outline.setBorder("2px solid #8289A6");
                outline.draw();
                outline.bringToFront();

                outline.animateRect(placeHolder.getPageLeft(), placeHolder.getPageTop(),
                        toCanvas.getVisibleWidth(), toCanvas.getViewportHeight(),
                        new AnimationCallback() {
                            public void execute(boolean earlyFinish) {
                                // callback at end of animation - destroy placeholder and outline; show the new portlet
                                column.removeMember(placeHolder);
                                column.removeMember(outline);

                                placeHolder.destroy();
                                outline.destroy();

                                column.redraw();


                                toCanvas.show();
                                //toCanvas.setVisible(true);

//                                Timer timer=new Timer()
//                                {
//                                    @Override
//                                    public void run()
//                                    {
//                                        ((Window)toCanvas).setMinimized(true);
//                                    }
//                                };
//                                timer.schedule(200);
                            }
                        }, duration);
    }

}
