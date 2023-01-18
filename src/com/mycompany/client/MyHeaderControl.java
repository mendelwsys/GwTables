package com.mycompany.client;

import com.mycompany.client.operations.IOperation;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.Canvas;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 09.06.14
 * Time: 20:08
 *
 */
public class MyHeaderControl extends HeaderControl
{
    private IOperation operation;
    private Canvas grid;

    public Window getTarget() {
        return target;
    }

    public void setTarget(Window target) {
        this.target = target;
    }

    private Window target;


    public Canvas getGrid() {
        return grid;
    }

    public void setGrid(Canvas grid) {
        this.grid = grid;
    }

    public MyHeaderControl(HeaderIcon icon) {
        super(icon);
    }

    /**
     * Вызывается когда контрол удаляется с панели
     */
    public void onRemoveCtrl()
    {

    }
    public MyHeaderControl(HeaderIcon icon, ClickHandler clickHandler) {
        super(icon, clickHandler);
    }

    public IOperation getOperation() {
        return operation;
    }

    public void setOperation(IOperation operation) {
        this.operation = operation;
    }
}
