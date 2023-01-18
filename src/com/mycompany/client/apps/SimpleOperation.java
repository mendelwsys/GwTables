package com.mycompany.client.apps;

import com.mycompany.client.ListGridWithDesc;
import com.mycompany.client.apps.App.ICopyObject;
import com.mycompany.client.apps.App.ISyncHandler;
import com.mycompany.client.apps.App.api.IOperationContext;
import com.mycompany.common.DescOperation;
import com.mycompany.client.apps.App.api.IDescProcessor;
import com.mycompany.client.operations.IOperation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:53
 * Элементарная операция
 */
public class SimpleOperation implements IOperation,ICopyObject,IDescProcessor
{
    private IOperationContext ctx;

    public IOperationContext getOperationCtx()
    {
        return ctx;
    }

    public void setOperationCtx(IOperationContext ctx)
    {
        this.ctx=ctx;
    }


    @Override
    public Map<String, DescOperation> getDescMapParams() {
        return descMapParams;
    }

    @Override
    public void setDescMapParams(Map<String, DescOperation> descMapParams) {
        this.descMapParams = descMapParams;
    }

    private Map<String,DescOperation> descMapParams=new HashMap<String,DescOperation>();

    private String folderName;

    public boolean isViewAsFolder() {
        return viewAsFolder;
    }

    public void setViewAsFolder(boolean viewAsFolder) {
        this.viewAsFolder = viewAsFolder;
    }

    private boolean viewAsFolder =false;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


    private int operationId;
    private int parentOperationId;

    private String viewName;
    private TypeOperation type=TypeOperation.NON;


    public SimpleOperation()
    {
        this(-1, -1, null, TypeOperation.NON);
    }



    public SimpleOperation(int operationId, int parentOperationId, String viewName, TypeOperation type)
    {

        this.operationId = operationId;
        this.parentOperationId = parentOperationId;
        this.viewName = viewName;
        this.type = type;
    }

    public int getParentOperationId() {
        return parentOperationId;
    }

    public void setParentOperationId(int parentOperationId)
    {
        this.parentOperationId=parentOperationId;
    }

    public void setOperationId(int operationId)
    {
        this.operationId=operationId;
    }


    public int getOperationId() {
        return operationId;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName=viewName;
    }

    public TypeOperation getTypeOperation() {
        return type;
    }

    public  void setTypeOperation(TypeOperation type)
    {
        this.type =type;
    }


    public ISyncHandler getSyncHandler()
    {
        return null;
    }

    public Canvas operate(Canvas dragTarget, IOperationContext ctx)
    {
        return null;
    }

    public Canvas getInputFrom(Canvas warnGrid) {
        return null;
    }

    public HeaderControl createHeaderControl(Canvas canvas, Window target)
    {
        return null;
    }

    public Canvas onRemove(Canvas warnGrid, Window target)
    {
        return null;
    }

    @Override
    public Object copy()
    {
        throw new UnsupportedOperationException("can't create copy of operation");
    }

    @Override
    public IOperation createOperation(DescOperation descOperation)
    {
        IOperation operation = getEmptyObject(descOperation);
        if (descOperation==null)
            return operation;

        String name = getNameOperation(operation);
        if (name.equals(descOperation.apiName))
            return createOperation(descOperation, operation);
        throw new UnsupportedOperationException("Can't create class "+descOperation.apiName+" with class "+name);
    }

    protected String getNameOperation(IOperation operation)
    {
        return operation.getClass().getName();
    }

    protected IOperation getEmptyObject(DescOperation descOperation)
    {
        return new SimpleOperation();
    }

    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation)
    {
        operation.setOperationId((Integer)descOperation.get(OPERATION_ID));
        operation.setParentOperationId((Integer) descOperation.get(PARENT_ID));
        operation.setViewName((String) descOperation.get(VIEW_NAME));
        operation.setFolderName((String) descOperation.get(FOLDER_NAME));
        operation.setTypeOperation(TypeOperation.valueOf((String) descOperation.get(TYPE_OPERATION)));
        final Boolean viewAsFolder1 = (Boolean) descOperation.get(ISFOLDER);
        operation.setViewAsFolder(viewAsFolder1!=null && viewAsFolder1);
        operation.setDescMapParams(descOperation.getDescMapParams());

        return operation;
    }

    @Override
    public DescOperation getDescOperation(DescOperation descOperation)
    {
        descOperation.apiName= getNameOperation(this);
        descOperation.put(OPERATION_ID, getOperationId());
        descOperation.put(PARENT_ID, getParentOperationId());
        descOperation.put(VIEW_NAME, getViewName());
        descOperation.put(TYPE_OPERATION, getTypeOperation().name());
        descOperation.put(FOLDER_NAME, getFolderName());
        descOperation.put(ISFOLDER, isViewAsFolder());
        descOperation.setDescMapParams(getDescMapParams());

        return descOperation;
    }


    protected static String OPERATION_ID="OPERATION_ID";
    protected static String PARENT_ID="PARENT_ID";
    public static String VIEW_NAME="VIEW_NAME";
    protected static String TYPE_OPERATION ="TYPE_OPERATION";
    protected static String FOLDER_NAME ="FOLDER_NAME";
    public static final String ISFOLDER = "isFolder";



    protected void setDescOperation(ListGridWithDesc grid)
    {
        List<DescOperation> subOperation = grid.getDescOperation().getSubOperation();
        subOperation.add(this.getDescOperation(new DescOperation()));
    }

    public int findIndexInDesc(List<DescOperation> subOperation)
    {
        return findIndexInDesc(subOperation,this.getClass());
    }


    public int findIndexInDesc(List<DescOperation> subOperation,Class apiClass)
    {
        String apiName=apiClass.getName();
        for (int i = 0, subOperationSize = subOperation.size(); i < subOperationSize; i++)
        {
            DescOperation descOperation = subOperation.get(i);
            if (apiName.equals(descOperation.apiName))
                return i;
        }
        return -1;
    }


}

