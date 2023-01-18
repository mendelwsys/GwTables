package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.MyPortalLayout;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.utils.PortalLayoutUtils;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.menu.MenuButton;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 22.12.14
 * Time: 14:37
 *
 */
public class AddColumnOperation extends SimpleOperation
{
    public static final String IS_SHOW_MENU = "isShowMenu";
    public static final String COL_NUM = "ColNum";
    public static final String MENU_HEADER = "MenuHeader";
    public static final String ROW_NUM = "RowNum";
    public static final String ROW_OFFSET = "rowOffset";

    protected DescOperation descOperation;


    public String getMenuHeader() {
        return menuHeader;
    }

    public void setMenuHeader(String menuHeader) {
        this.menuHeader = menuHeader;
    }

    private String menuHeader;


    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    private int colNum;

//    public DescOperation getDescOperation()
//    {
//        return descOperation;
//    }
//
//    public void setDescOperation(DescOperation descOperation) {
//        this.descOperation = descOperation;
//    }

    public boolean isShowMenu() {
        return isShowMenu;
    }

    public void setShowMenu(boolean showMenu) {
        isShowMenu = showMenu;
    }

    private boolean isShowMenu;


    protected AddColumnOperation(){
    }

    protected AddColumnOperation(DescOperation descOperation){
        this.descOperation=descOperation;
    }

    @Override
    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new AddColumnOperation(descOperation);
    }

    public Canvas operate(Canvas _dragTarget, IOperationContext ctx)
    {
        MyPortalLayout pl=(MyPortalLayout)_dragTarget;

        int clNum=getColNum();
        if (pl.getNumColumns()<=clNum)
        {
            pl.addColumn(clNum);
            pl.setColumnMenu();

        }

        Canvas headerLayout = PortalLayoutUtils.getHeaderLayout(pl, clNum);
        headerLayout.setVisible(isShowMenu());
        if (getMenuHeader()!=null)
        {
            MenuButton mb= PortalLayoutUtils.getMenuButton(headerLayout);
            mb.setTitle(getMenuHeader());
        }

        int rowNum=0;
        MainProcessor processor = App01.GUI_STATE_DESC.getProcessor();
        List<OperationHolder> operationHolders = processor.preProcessAll(descOperation.getSubOperation());
        for (OperationHolder operationHolder : operationHolders)
        {
            Canvas portlet = processor.operateAll(_dragTarget, operationHolder, ctx);
            Object oRowNum;
            if ((oRowNum=operationHolder.getDescriptor().get(ROW_NUM))!=null)
            {
                if (oRowNum instanceof Integer)
                {
                    Object oRowOffset=operationHolder.getDescriptor().get(ROW_OFFSET);
                    if (oRowOffset!=null && oRowOffset instanceof Integer)
                        pl.addPortlet((Portlet)portlet,clNum,(Integer)oRowNum,(Integer)oRowOffset);
                    else
                        pl.addPortlet((Portlet)portlet,clNum,(Integer)oRowNum);
                }
                else
                {
                    pl.addPortlet((Portlet)portlet,clNum,rowNum);
                    rowNum++;
                }
            }
            else
            {
               pl.addPortlet((Portlet)portlet,clNum,rowNum);
               rowNum++;
            }
        }
        return _dragTarget;
    }


    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        AddColumnOperation addColumnOperation =(AddColumnOperation)super.createOperation(descOperation,operation);
        Integer colNum=(Integer)descOperation.get(COL_NUM);
        if (colNum!=null)
            addColumnOperation.setColNum(colNum);
        Boolean isShowMenu=(Boolean )descOperation.get(IS_SHOW_MENU);
        if (isShowMenu)
            addColumnOperation.setShowMenu(isShowMenu);

        addColumnOperation.setMenuHeader((String) descOperation.get(MENU_HEADER));

        return operation;
    }

    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        super.getDescOperation(descOperation);
        descOperation.put(COL_NUM, getColNum());
        descOperation.put(IS_SHOW_MENU, isShowMenu());
        descOperation.put(MENU_HEADER, getMenuHeader());
        return descOperation;
    }



}
