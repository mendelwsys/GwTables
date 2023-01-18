package com.mycompany.client.apps;

import com.mycompany.client.operations.IOperation;
import com.mycompany.client.operations.IOperationFactory;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.11.14
 * Time: 13:54
 * Нужен ли этот класс большой вопрос может обойтись TreeNode ?
 */
public class OperationNode extends TreeNode
    {

        public static final String OPERATION_ID = "OperationId";
        public static final String PARENT_OPERATION_ID = "ParentOperationId";
        public static final String NAME_NODE = "Name";
        public static final String ISBUILDIN="IsBuildIn";
        public static final String OPERATIONNUM = "Operation";


        public boolean isBuildIn() {
            final Boolean isBuildIn = this.getAttributeAsBoolean(ISBUILDIN);
            return (isBuildIn!=null && isBuildIn);
        }

        public void setBuildIn(boolean buildIn) {
            this.setAttribute(ISBUILDIN,buildIn);
        }


        public static Map id2Operation=new HashMap();

        public static int nextId=1;


        public static Object getOperation(Record node)
        {
            int opNumber=node.getAttributeAsInt(OperationNode.OPERATIONNUM);
            return id2Operation.get(opNumber);
        }

        public static Object removerOperation(Record node)
        {
            int opNumber=node.getAttributeAsInt(OperationNode.OPERATIONNUM);
            return id2Operation.remove(opNumber);
        }


        public static void addOperation(Record record,Object operation)
        {
            record.setAttribute(OPERATIONNUM,nextId);
            id2Operation.put(nextId,operation);
            nextId++;
        }

        {
            setBuildIn(true);
        }

        public OperationNode(IOperation operation,boolean isFolder)
        {
            this(operation);
            setIsFolder(isFolder);

        }

        public OperationNode(IOperation operation)
        {
            setAttribute(OPERATION_ID,(Integer) operation.getOperationId());
            setAttribute(PARENT_OPERATION_ID, (Integer) operation.getParentOperationId());
            setAttribute(NAME_NODE, operation.getViewName());
            setAttribute(SimpleOperation.ISFOLDER, operation.isViewAsFolder());
            //setAttribute(OPERATION,operation);
            addOperation(this,operation);
        }



        public OperationNode(IOperationFactory factory)
        {
            IOperation operation=factory.getOperation();
            setAttribute(OPERATION_ID, (Integer)operation.getOperationId());
            setAttribute(PARENT_OPERATION_ID, (Integer)operation.getParentOperationId());
            setAttribute(NAME_NODE, operation.getViewName());
            setAttribute(SimpleOperation.ISFOLDER, operation.isViewAsFolder());
            addOperation(this,factory);
        }

    }