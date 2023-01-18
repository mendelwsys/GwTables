package com.mycompany.client.test.Demo.lentaViews;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.09.14
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */

import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.LayoutPolicy;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;

/**
  * MyPortlet class definition
  */
 public class MyPortlet extends Window {

     public MyPortlet() {

         setShowShadow(false);

         // enable predefined component animation
         setAnimateMinimize(true);

         // Window is draggable with "outline" appearance by default.
         // "target" is the solid appearance.
         setDragAppearance(DragAppearance.OUTLINE);
         setCanDrop(true);
//         setCanAcceptDrop(true);

         // customize the appearance and order of the controls in the window header
         setHeaderControls(HeaderControls.MINIMIZE_BUTTON, HeaderControls.HEADER_LABEL, new HeaderControl(HeaderControl.SETTINGS), new HeaderControl(HeaderControl.HELP), HeaderControls.CLOSE_BUTTON);

         // show either a shadow, or translucency, when dragging a portlet
         // (could do both at the same time, but these are not visually compatible effects)
         // setShowDragShadow(true);
         setDragOpacity(30);

         // these settings enable the portlet to autosize its height only to fit its contents
         // (since width is determined from the containing layout, not the portlet contents)
         setVPolicy(LayoutPolicy.NONE);
         setOverflow(Overflow.VISIBLE);
     }
 }
