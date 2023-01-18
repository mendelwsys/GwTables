package com.mycompany.client.test.myportalview;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 12:48
 *
 */
public class MyPortalRow extends HLayout
{
    public MyPortalRow()
    {
        final HLayout hLayout = this;

        hLayout.setCanAcceptDrop(true);

        hLayout.setMembersMargin(6);
        hLayout.setLayoutMargin(6);

        Canvas dropLineProperties = new Canvas();
        dropLineProperties.setBackgroundColor("aqua");
        hLayout.setDropLineProperties(dropLineProperties);


        hLayout.setShowDragPlaceHolder(true);

        Canvas placeHolderProperties = new Canvas();
        placeHolderProperties.setBorder("2px solid #8289A6");

        hLayout.setPlaceHolderProperties(placeHolderProperties);
        hLayout.setDropLineThickness(4);

        hLayout.setWidth100();

    }
}
