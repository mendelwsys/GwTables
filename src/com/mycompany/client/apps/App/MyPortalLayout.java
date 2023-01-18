package com.mycompany.client.apps.App;

import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.MyHeaderControl;
import com.mycompany.client.utils.PortalLayoutUtils;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuButton;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.12.14
 * Time: 17:06
 * Портальная панель для всех остальных окон.
 */
public class MyPortalLayout extends PortalLayout
{
    public void destroy()
    {
        super.destroy(); //TODO проверить что у нас прекарщается опрос сервера, и необходимо явно очистить серверные ресурсы
    }

    public ToolStripButton getSwitchButton() {
        return switchButton;
    }

    public void setSwitchButton(ToolStripButton switchButton) {
        this.switchButton = switchButton;
    }

    private ToolStripButton switchButton;


    public void addPortlet(Portlet portlet, int colNum, int rowNum)
    {
        super.addPortlet(portlet,colNum,rowNum);

    }

    public MyPortalLayout()
    {
        this.setNumColumns(1);
        setColumnMenu();
    }

    public void setColumnMenu()
    {
        final MyPortalLayout pl=this;

        for (int ix=0;ix<pl.getNumColumns();ix++)
        {
            final Canvas headerLayout = PortalLayoutUtils.getHeaderLayout(this,ix);
            MenuButton mb= PortalLayoutUtils.getMenuButton(headerLayout);

            final GUIStateDesc guiStateDesc = App01.GUI_STATE_DESC;

            mb.setTitle(AppConst.NEW_COLUMN_OF_PORTAL_LAYOUT_HEADER);
            Menu menu = mb.getMenu();

            MenuItem[] items = menu.getItems();
            if (items.length>2)
            {
                setIxPortletLayout(ix, items);
                continue;
            }

            {
                final MenuItem item = items[0];
                item.setTitle(AppConst.DELETE_COLUMN_FROM_PORTAL_LAYOUT);
                item.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
                    @Override
                    public void onClick(MenuItemClickEvent event) {
                        pl.removeColumn(item.getAttributeAsInt("IX"));
                        setColumnMenu();

                        if (pl.getNumColumns()==0)
                        {
                            Layout mainLayout = guiStateDesc.getMainLayout();
                            mainLayout.setCanAcceptDrop(true);
                        }
                    }
                });
            }

            final MenuItem item = items[1];
            {
                item.setTitle(AppConst.ADD_COLUMN_TO_PORTAL_LAYOUT);
                item.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
                    @Override
                    public void onClick(MenuItemClickEvent event)
                    {
                        pl.addColumn(item.getAttributeAsInt("IX")+1);
                        setColumnMenu();
                    }
                });
            }

            {
                final MenuItem newItem = new MenuItem(AppConst.CREATE_PAGE_FROM_COLUMN_PORTAL_LAYOUT);
                menu.addItem(newItem);
                newItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler()
                {
                    @Override
                    public void onClick(MenuItemClickEvent event)
                    {
                            final MyPortalLayout newPage = new MyPortalLayout();
                            int newPageIx = createEmptyPage(guiStateDesc, newPage,getTreeView(guiStateDesc));

                            //Собрать окно, удалив без уничтожения портлеты из старого окна и добавать в новое
                            final Integer iix = newItem.getAttributeAsInt("IX");
                            Portlet[][] parray = pl.getPortletArray()[iix];
                            for (Portlet[] portlets : parray)
                                for (int i = 0, portletsLength = portlets.length; i < portletsLength; i++)
                                {
                                    Portlet portlet = portlets[i];
                                    pl.removePortlet(portlet);
                                    newPage.addPortlet(portlet);
                                    if (i==0)
                                        newPage.getSwitchButton().setPrompt(portlet.getTitle());
                                }

                            pl.removeColumn(iix);//Удалить колонку из текущего лайоута
                            pl.setColumnMenu();//Переинициализировать меню

                            guiStateDesc.switchPages(newPageIx);
                    }
                });
            }

            {
                final MenuItem newItem = new MenuItem(AppConst.CUSTOM_COLUMN_PORTAL_LAYOUT);
                menu.addItem(newItem);
                newItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler()
                {
                    @Override
                    public void onClick(MenuItemClickEvent event)
                    {
                        Window wnd = OptionsViewers.createViewPortalColumnOptions(MyPortalLayout.this, newItem.getAttributeAsInt("IX"));
                        wnd.show();
                    }
                });

            }

            {
                MenuItem newItem = new MenuItem(AppConst.HIDE_MENU_ON_PORTAL_LAYOUT);
                menu.addItem(newItem);
                newItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler()
                {
                    @Override
                    public void onClick(MenuItemClickEvent event)
                    {
                        headerLayout.setVisible(false);
                        //pl.setShowColumnMenus(false);
                    }
                });
            }
            setIxPortletLayout(ix, menu.getItems());
        }
    }


    public static Canvas  getTreeView(GUIStateDesc guiStateDesc)
    {
        Canvas[] members = guiStateDesc.getMainLayout().getMembers();
        for (Canvas member : members)
        {
            String treeView=member.getID();
            if (treeView!=null && treeView.contains(AppConst.t_TREE_VIEW_INDICATOR))
                return  member;
        }
        return null;
    }


    public static int createEmptyPage(final GUIStateDesc guiStateDesc,final Canvas treeView)
    {
        return createEmptyPage(guiStateDesc,new MyPortalLayout(), treeView);
    }

    public static int createEmptyPage(final GUIStateDesc guiStateDesc,MyPortalLayout newPage,final Canvas treeView)
    {

        newPage.setWidth100();
        newPage.setHeight100();
        newPage.setCanResizeColumns(true);
//        newPage.setCanDragResize(true);
        newPage.setCanResizePortlets(true);

        ToolStripButton iconButton = new ToolStripButton();
        iconButton.setIcon(AppConst.TOOLSTRIPICONS_PREFIX+ AppConst.DEFAULT_ICON +AppConst.IMAGE_URL_SUFFIX);
        iconButton.setPrompt(AppConst.NEW_PAGE_PROMT);
        iconButton.setCanDrag(true);
        iconButton.setCanDrop(true);

        final ToolStrip toolStrip = guiStateDesc.getToolStrip();

        toolStrip.addButton(iconButton,guiStateDesc.getNumPages());

        newPage.setSwitchButton(iconButton);

        final List<Canvas> lc= new LinkedList<Canvas>();
        if (treeView!=null)
            lc.add(treeView);

        lc.add(newPage);

        final int currentPage=guiStateDesc.addPage(lc);
        guiStateDesc.add2NotSwitchToolsCanvas(currentPage,new Canvas[]{iconButton});

        iconButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event) {
                if (guiStateDesc.getCurrentPage() != currentPage)
                    guiStateDesc.switchPages(currentPage);
                else if (treeView != null) //Управление деревом виджетов
                    treeView.setVisible(!treeView.isVisible());
            }
        });
        //newPage.setColumnMenu(); Это не надо поскольку переинициализация осуществляется в конструкторе

        return currentPage;
    }

    private void setIxPortletLayout(int ix, MenuItem[] items) {
        for (int i = 0, itemsLength = items.length; i < itemsLength; i++)
            items[i].setAttribute("IX", ix);
    }


    public Canvas getDropPortlet(Canvas dragTarget, Integer colNum, Integer rowNum, Integer dropPosition)
        {
            if (dragTarget instanceof TreeGrid)
            {
                Record[] dragData = ((ListGrid) dragTarget).getDragData();
                return NodesHolder.layOutDragHandler(this, dragTarget, colNum, rowNum, dragData);
            }
            else if (dragTarget instanceof MyHeaderControl)
            {
                MyHeaderControl dragTarget1 = (MyHeaderControl) dragTarget;
                Canvas grid = dragTarget1.getGrid();
                return NodesHolder.onRemoveCtrlHandler(dragTarget1, grid);
            }
            else if (dragTarget instanceof Portlet)
                return super.getDropPortlet(dragTarget, colNum, rowNum, dropPosition);
            else
                return null;//По умолчанию не делаем ничего
        }




}