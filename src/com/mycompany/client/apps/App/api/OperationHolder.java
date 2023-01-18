package com.mycompany.client.apps.App.api;

import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 15.12.14
 * Time: 19:42
 * To change this template use File | Settings | File Templates.
 */
public class OperationHolder
{
    private IOperation operation;
    private DescOperation descriptor;

    public IOperation getOperation() {
        return operation;
    }

    public void setOperation(IOperation operation) {
        this.operation = operation;
    }

    public DescOperation getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(DescOperation descriptor) {
        this.descriptor = descriptor;
    }

    public List<OperationHolder> getSubHolders() {
        return subHolders;
    }

    public void setSubHolders(List<OperationHolder> subHolders) {
        this.subHolders = subHolders;
    }

    private List<OperationHolder> subHolders = new LinkedList<OperationHolder>();

    public OperationHolder(IOperation operation, DescOperation descriptor) {
        this.operation = operation;
        this.descriptor = descriptor;
    }

}
