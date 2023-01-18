package com.mycompany.client.operations;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 06.06.14
 * Time: 16:31
 *
 */
public interface IOperationParam extends IOperation
        //extends Cloneable
{
    public List getParams();
    public void setParams(List params);

}
