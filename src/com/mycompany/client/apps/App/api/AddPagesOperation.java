package com.mycompany.client.apps.App.api;

import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.SimpleNewPortlet;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 22.12.14
 * Time: 15:41
 * Операция для добавления страницы (Здесь должно быть описание названия, и иконка)
 */
public class AddPagesOperation extends SimpleNewPortlet
{
    private DescOperation descOperation;


    public static final String CURR_PAGE_NUM = "CurrPageNum";
    public static final String VIEW_TOOL_STRIP = "ViewToolStrip";
    private int currOrderPageNum;

    public boolean isViewToolStrip() {
        return viewToolStrip;
    }

    public void setViewToolStrip(boolean viewToolStrip) {
        this.viewToolStrip = viewToolStrip;
    }

    private boolean viewToolStrip;

    protected AddPagesOperation(){
    }

    protected AddPagesOperation(DescOperation descOperation){
        this.descOperation=descOperation;
    }

    @Override
    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new AddPagesOperation(descOperation);
    }

    public Canvas operate(Canvas _dragTarget, IOperationContext ctx)
    {


        GUIStateDesc state = App01.GUI_STATE_DESC;
        MainProcessor processor = state.getProcessor();

        Map<Integer,Integer> switchMapper=new HashMap<Integer,Integer>();

        final List<DescOperation> subOperations = descOperation.getSubOperation();
        for (DescOperation subOperation : subOperations)
        {
            OperationHolder addPageOperationHolder= processor.preProcessIt(subOperation);
            AddPageOperation addPageOperation = (AddPageOperation) addPageOperationHolder.getOperation();
            addPageOperation.operate(_dragTarget, null);
            switchMapper.put(addPageOperation.getPageOrderNum(),addPageOperation.getPageNum());
        }

        state.getThinStripCtrl().setVisible(!isViewToolStrip());
        state.getToolStrip().setVisible(isViewToolStrip());
        final Integer toPage = switchMapper.get(getCurrOrderPageNum());
        if (toPage!=null)
            state.switchPages(toPage);
        else
        {
            SC.say("Не найден индекс для переключения на "+getCurrOrderPageNum()+" страницу");//TODO Если сюда попадаем то !!!БАГА!!!!
        }

        return _dragTarget;
    }


    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        AddPagesOperation addPagesOperation =(AddPagesOperation)super.createOperation(descOperation,operation);
        Integer currPageNum=(Integer)descOperation.get(CURR_PAGE_NUM);
        if (currPageNum!=null)
            addPagesOperation.setCurrOrderPageNum(currPageNum);
        Boolean viewToolBar= (Boolean) descOperation.get(VIEW_TOOL_STRIP);
        if (viewToolBar!=null)
            addPagesOperation.setViewToolStrip(viewToolBar);
        return operation;
    }


    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        super.getDescOperation(descOperation);
        descOperation.put(CURR_PAGE_NUM, getCurrOrderPageNum());
        descOperation.put(VIEW_TOOL_STRIP, isViewToolStrip());
        return descOperation;
    }


    public int getCurrOrderPageNum() {
        return currOrderPageNum;
    }

    public void setCurrOrderPageNum(Integer currOrderPageNum) {
        this.currOrderPageNum = currOrderPageNum;
    }
}
