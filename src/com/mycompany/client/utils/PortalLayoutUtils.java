package com.mycompany.client.utils;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.menu.MenuButton;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.12.14
 * Time: 13:58
 * Утилиты для редактирвоания портала
 */
public class PortalLayoutUtils
{

    public static Canvas getBodyLayout(PortalLayout portalLayout,int ix)
    {
        Canvas member = portalLayout.getMember(ix);
        Canvas[] clds = member.getChildren();
        return clds[1];
    }

    public static Canvas getHeaderLayout(PortalLayout portalLayout,int ix)
    {
        Canvas member = portalLayout.getMember(ix);
        Canvas[] clds = member.getChildren();
        return clds[0];
    }

    public static MenuButton getMenuButton(PortalLayout portalLayout,int ix)
    {
        Canvas headerLayout = getHeaderLayout(portalLayout,ix);
        return (MenuButton) headerLayout.getChildren()[1];
    }

    public static MenuButton getMenuButton(Canvas headerLayout)
    {
        return (MenuButton) headerLayout.getChildren()[1];
    }

}
