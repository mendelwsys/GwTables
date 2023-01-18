package com.mycompany.client.test.myportalview;

import com.smartgwt.client.types.EdgeName;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuButton;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.09.14
 * Time: 13:09
 * Колонка портала
 */
public class MyPortalColumn extends VLayout
{
    public Button getHeaderButton() {
        return headerButton;
    }

    private Button headerButton;
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    private String header;
        public MyPortalColumn(String header)
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

                headerButton = new MenuButton(header,menu);
//                headerButton.setIcon("silk/application_side_expand.png");
                //headerButton.setAutoFit(true);

                newItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler()
                {
                    @Override
                    public void onClick(MenuItemClickEvent event)
                    {
                        headerButton.setVisible(false);
                    }
                });


                headerButton.setShowRollOver(true);
                headerButton.setShowDisabled(true);
                headerButton.setShowDown(true);
                headerButton.setWidth100();


                this.addMember(headerButton);
            }

            this.setShowEdges(true);
            this.setCanDragResize(true);

            this.setResizeFrom(EdgeName.R);

        }

    }