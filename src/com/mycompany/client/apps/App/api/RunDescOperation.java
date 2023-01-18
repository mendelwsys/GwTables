package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 18.12.14
 * Time: 15:00
 * Операция запуска дескриптора операций
 */
public class RunDescOperation extends SimpleOperation
{
   protected DescOperation descOperation;

   protected RunDescOperation(){
   }

   public RunDescOperation(DescOperation descOperation)
   {
       this.descOperation=super.getDescOperation(new DescOperation());
       this.descOperation.getSubOperation().add(descOperation);
   }

   public DescOperation getDescOperation(DescOperation descOperation)
   {
       descOperation=super.getDescOperation(descOperation);
       descOperation.getSubOperation().addAll(this.descOperation.getSubOperation());
       return this.descOperation=descOperation;
   }
   protected IOperation getEmptyObject(DescOperation descOperation)
   {
       final RunDescOperation rv = new RunDescOperation();
       rv.descOperation=descOperation;
       return rv;
   }

   public Canvas operate(Canvas dragTarget, IOperationContext ctx)
   {
        MainProcessor processor = App01.GUI_STATE_DESC.getProcessor();
        List<OperationHolder> operationHolders = processor.preProcessAll(descOperation.getSubOperation());
        return processor.operateAll(dragTarget, operationHolders, ctx);
   }
}
