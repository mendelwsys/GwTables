package com.mycompany.client.apps.App.api;

import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.MyPortalLayout;
import com.mycompany.client.apps.App.SimpleNewPortlet;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 22.12.14
 * Time: 15:41
 * Операция для добавления страницы (Здесь должно быть описание названия, и иконка)
 */
public class AddPageOperation extends SimpleNewPortlet
{
    private DescOperation descOperation;
    public static final String PAGE_NUM = "PageNum";
    public static final String PAGE_ORD_NUM = "PageOrdNum";
    public static final String VIEW_TREE = "ViewTree";
    public static final String PAGE_TITLE = "PageTitle";
    public static final String PAGE_ICON = "PageIcon";


    public String getPageIcon() {
        return pageIcon;
    }

    public void setPageIcon(String pageIcon) {
        this.pageIcon = pageIcon;
    }

    String pageIcon;

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    String pageTitle;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    int pageNum;

    public int getPageOrderNum() {
        return pageOrderNum;
    }

    public void setPageOrderNum(int pageOrderNum) {
        this.pageOrderNum = pageOrderNum;
    }

    int pageOrderNum;

    public boolean isViewTree() {
        return viewTree;
    }

    public void setViewTree(boolean viewTree) {
        this.viewTree = viewTree;
    }

    boolean viewTree;

    protected AddPageOperation(){
    }

    protected AddPageOperation(DescOperation descOperation){
        this.descOperation=descOperation;
    }

    @Override
    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new AddPageOperation(descOperation);
    }

    public Canvas operate(Canvas _dragTarget, IOperationContext ctx)
    {

        GUIStateDesc state = App01.GUI_STATE_DESC;
        MainProcessor processor = state.getProcessor();

        MyPortalLayout newPage;
        int pages = state.getNumPages();

        Canvas treeView = MyPortalLayout.getTreeView(App01.GUI_STATE_DESC);
        if (treeView!=null)
            treeView.setVisible(isViewTree());

        if (pages<=getPageOrderNum())
        {
//Создать пустую страницу если требуется
            newPage = new MyPortalLayout();
            pageNum=MyPortalLayout.createEmptyPage(App01.GUI_STATE_DESC, newPage, treeView);
        }
        else
        {
            List<Canvas> canvases=state.getPageCanvas(state.getCurrentPage());
            br:
            {
                for (Canvas canvase : canvases)
                    if (canvase instanceof MyPortalLayout)
                    {
                        newPage= (MyPortalLayout) canvase;
                        break br;
                    }
                    newPage = new MyPortalLayout();
                    pageNum=MyPortalLayout.createEmptyPage(App01.GUI_STATE_DESC, newPage, treeView);
            }
        }
//Наполняем созданную страницу
        final List<DescOperation> subOperations = descOperation.getSubOperation();
        for (DescOperation subOperation : subOperations)
        {
            OperationHolder addColumnOperation= processor.preProcessIt(subOperation);
            addColumnOperation.getOperation().operate(newPage, null);
        }

        if (getPageTitle()!=null)
        {
            ToolStripButton switchButton = newPage.getSwitchButton();
            switchButton.setPrompt(getPageTitle());
        }

        if (getPageIcon()!=null)
        {
            ToolStripButton switchButton = newPage.getSwitchButton();
            switchButton.setIcon(getPageIcon());
        }




        return _dragTarget;
    }


    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        AddPageOperation addPageOperation =(AddPageOperation)super.createOperation(descOperation,operation);
        Integer pageNum=(Integer)descOperation.get(PAGE_NUM);
        if (pageNum!=null)
            addPageOperation.setPageNum(pageNum);

        Integer pageOrderedNum=(Integer)descOperation.get(PAGE_ORD_NUM);
        if (pageOrderedNum!=null)
            addPageOperation.setPageOrderNum(pageOrderedNum);

        Boolean viewTree= (Boolean) descOperation.get(VIEW_TREE);
        if (viewTree!=null)
            addPageOperation.setViewTree(viewTree);

        addPageOperation.setPageTitle((String)descOperation.get(PAGE_TITLE));
        addPageOperation.setPageIcon((String)descOperation.get(PAGE_ICON));

        return operation;
    }

    //                final


    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        super.getDescOperation(descOperation);
        descOperation.put(PAGE_NUM, getPageNum());
        descOperation.put(PAGE_ORD_NUM, getPageOrderNum());
        descOperation.put(VIEW_TREE, isViewTree());
        descOperation.put(PAGE_TITLE, getPageTitle());
        descOperation.put(PAGE_ICON, getPageIcon());
        return descOperation;
    }


}
