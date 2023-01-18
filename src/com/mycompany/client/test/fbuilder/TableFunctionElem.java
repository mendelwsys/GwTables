package com.mycompany.client.test.fbuilder;

import com.smartgwt.client.data.*;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.util.JSOHelper;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.03.15
 * Time: 17:05
 *  Элемент табличной функции, состоит из вычислительной функции над множеством,
 *  критерия, который определяет условие выдачи значения (значение м ожет быть любой объект)
 *  Элементы задается один за другим
 */
public class TableFunctionElem
{
    public FunctionDet getFunctionDet() {
        return functionDet;
    }

    public void setFunctionDet(FunctionDet functionDet) {
        this.functionDet = functionDet;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public Object getOutValue(Record[] records, Double defValue) {
        return outValue;
    }

    public void setOutValue(Object outValue) {
        this.outValue = outValue;
    }

    private FunctionDet functionDet;

    private Criteria criteria;
    private Object outValue;

    public TableFunctionElem(FunctionDet functionDet, String criteria, Object outValue) {
        this.functionDet = functionDet;

        if (criteria!=null && criteria.length()>0)
            this.criteria = new AdvancedCriteria(JSOHelper.eval(criteria));
        this.outValue = outValue;
    }


    public TableFunctionElem(FunctionDet functionDet, Criteria criteria, Object outValue) {
        this.functionDet = functionDet;
        this.criteria = criteria;
        this.outValue = outValue;
    }

}
