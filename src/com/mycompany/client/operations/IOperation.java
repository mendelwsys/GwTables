package com.mycompany.client.operations;

import com.mycompany.client.apps.App.ISyncHandler;
import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 06.06.14
 * Time: 16:31
 *
 */
public interface IOperation
        //extends Cloneable
{



    boolean isViewAsFolder();
    void setViewAsFolder(boolean viewAsFolder);

    void setDescMapParams(Map<String, DescOperation> descMapParams);

    Map<String, DescOperation> getDescMapParams();


    public static enum TypeOperation
    {
        NON,
        addEventPortlet,
        addData,
        addFilter,
        addClientFilter,
//        addServerFilter,
        addGroping,
        addMenu,
        addPage,
        addNode,
        addChart,
        addGISEventPositioning,
        addInformer,
        addExcelExport,
        addChartByTable,
        addStyleToTable,
        extendGridByPlace

    }

    int getParentOperationId();
    void setParentOperationId(int parentOperationId);
    int getOperationId();
    void setOperationId(int operationId);

    String getViewName();
    void setViewName(String viewName);

    String getFolderName();
    void setFolderName(String folderName);

    TypeOperation getTypeOperation();
    void setTypeOperation(TypeOperation operation);

    Canvas operate(Canvas dragTarget, IOperationContext ctx);

    ISyncHandler getSyncHandler();




    IOperationContext getOperationCtx();

    void setOperationCtx(IOperationContext ctx);


    Canvas getInputFrom(Canvas warnGrid);


    HeaderControl createHeaderControl(Canvas canvas, Window target);

    Canvas onRemove(Canvas warnGrid, Window target);



}
