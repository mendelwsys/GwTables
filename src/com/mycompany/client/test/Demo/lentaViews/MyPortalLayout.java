package com.mycompany.client.test.Demo.lentaViews;

import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.09.14
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */
 public class MyPortalLayout extends HLayout {
        public MyPortalLayout(int numColumns) {
            setMembersMargin(6);
            for (int i = 0; i < numColumns; i++) {
                addMember(new PortalColumn("Диспетчер "+i));
            }
        }

        public PortalColumn addPortlet(MyPortlet portlet)
        {
            // find the column with the fewest portlets
            int fewestPortlets = Integer.MAX_VALUE;
            PortalColumn fewestPortletsColumn = null;
            for (int i = 0; i < getMembers().length; i++) {
                int numPortlets = ((PortalColumn) getMember(i)).getMembers().length;
                if (numPortlets < fewestPortlets) {
                    fewestPortlets = numPortlets;
                    fewestPortletsColumn = (PortalColumn) getMember(i);
                }
            }
            fewestPortletsColumn.addMember(portlet);
            return fewestPortletsColumn;
        }
    }