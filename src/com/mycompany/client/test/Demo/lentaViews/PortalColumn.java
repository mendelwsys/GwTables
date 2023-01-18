package com.mycompany.client.test.Demo.lentaViews;

import com.smartgwt.client.types.EdgeName;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuButton;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.09.14
 * Time: 13:09
 * To change this template use File | Settings | File Templates.
 */
public class PortalColumn extends VStack
{

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    private String header;
        public PortalColumn( String header)
        {

            this.header=header;
            // leave some space between portlets
            setMembersMargin(6);

            // enable predefined component animation
            setAnimateMembers(true);
            setAnimateMemberTime(300);

            // enable drop handling
            setCanAcceptDrop(true);

            // change appearance of drag placeholder and drop indicator
            setDropLineThickness(4);

            Canvas dropLineProperties = new Canvas();
            dropLineProperties.setBackgroundColor("aqua");
            setDropLineProperties(dropLineProperties);

            setShowDragPlaceHolder(true);

            Canvas placeHolderProperties = new Canvas();
            placeHolderProperties.setBorder("2px solid #8289A6");
            setPlaceHolderProperties(placeHolderProperties);

            {
                Menu menu = new Menu();
                menu.setShowShadow(true);
                menu.setShadowDepth(10);

                MenuItem newItem = new MenuItem("Скрыть заголовок");
                menu.setItems(newItem);

                final Button dispName = new MenuButton(header,menu);
//                dispName.setIcon("silk/application_side_expand.png");
                //dispName.setAutoFit(true);

                newItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler()
                {
                    @Override
                    public void onClick(MenuItemClickEvent event)
                    {
                        dispName.setVisible(false);
                    }
                });


                dispName.setShowRollOver(true);
                dispName.setShowDisabled(true);
                dispName.setShowDown(true);
                dispName.setWidth100();


                this.addMember(dispName);
            }


/*
            Canvas canvasTitle = new Canvas();
            canvasTitle.setHeight(18);
            canvasTitle.setWidth100();
            //canvasTitle.setAlign(Alignment.CENTER);
            canvasTitle.setContents("<b align=\"middle\">"+header+"</b>");

            this.addMember(canvasTitle);
*/
            this.setShowEdges(true);
            this.setCanDragResize(true);

            this.setResizeFrom(EdgeName.R);

        }

    }