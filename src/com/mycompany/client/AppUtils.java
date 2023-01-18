package com.mycompany.client;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Layout;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.11.14
 * Time: 19:31
 * Утилиты приложения
 */
public class AppUtils
{
    public static List<Canvas> collectCurrentLayout(final Layout mainLayout, final Collection<Canvas> notCollect) {
        List<Canvas> rv = new LinkedList<Canvas>();
        Canvas[] members = mainLayout.getMembers();
        for (Canvas member : members)
            if (!notCollect.contains(member))
                rv.add(member);
        return rv;
    }

    public static List<Canvas> collectCurrentVisibleLayout(final Layout mainLayout, final Collection<Canvas> notCollect) {
        List<Canvas> rv = new LinkedList<Canvas>();
        Canvas[] members = mainLayout.getMembers();
        for (Canvas member : members)
            if (!notCollect.contains(member) && member.isVisible())
                rv.add(member);
        return rv;
    }

}
