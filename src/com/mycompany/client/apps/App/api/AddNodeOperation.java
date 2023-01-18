package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.NodesHolder;
import com.mycompany.client.apps.OperationNode;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 18.12.14
 * Time: 12:04
 * Добавляем операцию в дерево
 */
public class AddNodeOperation extends SimpleOperation
{
    protected DescOperation descOperation;

    protected AddNodeOperation(){
    }

    protected AddNodeOperation(DescOperation descOperation){
        this.descOperation=descOperation;
    }

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new AddNodeOperation(descOperation);
    }

    public Canvas operate(Canvas _dragTarget, IOperationContext ctx)
    {

        MainProcessor processor = App01.GUI_STATE_DESC.getProcessor();
        List<OperationHolder> operationHolder = processor.preProcessAll(descOperation.getSubOperation());
        for (OperationHolder holder : operationHolder)
        {
            OperationNode node = new OperationNode(holder.getOperation());
            NodesHolder.setTreeDs(App01.GUI_STATE_DESC.getMainTreeGrid().getDataSource(), node);//Добавление его в список узлов
        }

//       OperationNode node = new OperationNode
//       (
//        new SimpleNewPortlet(this.getOperationId(), this.getParentOperationId(), this.getViewName(), IOperation.TypeOperation.addEventPortlet)
//        {
//            public Canvas operate(Canvas dragTarget)
//            {
//
//                MainProcessor processor = App01.GUI_STATE_DESC.getProcessor();
//                List<OperationHolder> operationHolder = processor.preProcessAll(descOperation.getSubOperation());
//                return processor.operateAll(dragTarget, operationHolder);
//            }
//        }
//       ); //формирование нода

        return null;
    }

}
