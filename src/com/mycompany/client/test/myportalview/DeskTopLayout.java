package com.mycompany.client.test.myportalview;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.09.14
 * Time: 13:10
 * Лайоут используемый для страничек десктопа
 */
public class DeskTopLayout extends HLayout
{
        public DeskTopLayout()
        {
            setMembersMargin(6);
        }

        public void addPortlet(Canvas portlet, int column,int row)
        {
            MyPortalRow portalRow = (MyPortalRow) getMember(column);
            portalRow.addMember(portlet,row);
        }


        public MyPortalColumn addColumn(String title)
        {
            MyPortalColumn rv;
            this.addMember(rv=new MyPortalColumn(title));
            return rv;
        }


        public Canvas getMember(int column, int row)
        {
            MyPortalRow portalRow = (MyPortalRow) getMember(column);
            return portalRow.getMember(row);
        }

        public MyPortalRow getPortalRow(int column)
        {
            return (MyPortalRow) getMember(column);
        }
}