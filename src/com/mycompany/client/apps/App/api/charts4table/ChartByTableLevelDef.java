package com.mycompany.client.apps.App.api.charts4table;

import com.mycompany.client.dojoChart.BaseChartLevelDef;
import com.mycompany.client.dojoChart.ValDef;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.OperatorId;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.04.15
 * Time: 17:09
 * Объект уровня графика
 */
public class ChartByTableLevelDef extends BaseChartLevelDef
{

    private String grpFieldName;




    public ChartByTableLevelDef(String colRefName,String grpFieldName)
    {
        super(colRefName,true);
        this.grpFieldName=grpFieldName;
        allowEntrance=isGrouping();
    }

    public ChartByTableLevelDef(String colRefName,boolean allowEntrance,String grpFieldName) {
        super(colRefName,allowEntrance);
        this.grpFieldName=grpFieldName;
    }

    @Override
    public String getGrpViewName(Record record)
    {
        if (grpFieldName!=null)
            return record.getAttribute(grpFieldName);
        return null;
    }

    @Override
    public Object calcGrpValue(Record record)
    {
        if (grpFieldName!=null)
            return record.getAttributeAsObject(grpFieldName);
        return null;
    }

    @Override
    public void appendToCriterionList(AdvancedCriteria cr, ValDef grpDef)
    {
        {
            final ValDef parentDef = grpDef.getParentDef();
            if (parentDef !=null && parentDef.getParentDef()!=null)
                parentDef.getParentDef().getMetaDef().appendToCriterionList(cr,parentDef);
        }

        final Object grpValue = grpDef.getGrpValue();
        if (grpValue==null)
            cr.appendToCriterionList(new Criterion(grpFieldName, OperatorId.IS_NULL));
        else if (grpValue instanceof Float)
            cr.appendToCriterionList(new Criterion(grpFieldName, OperatorId.EQUALS,(Float) grpValue));
        else if (grpValue instanceof Long)
            cr.appendToCriterionList(new Criterion(grpFieldName, OperatorId.EQUALS,(Long) grpValue));
        else if (grpValue instanceof Integer)
            cr.appendToCriterionList(new Criterion(grpFieldName, OperatorId.EQUALS,(Integer) grpValue));
        else if (grpValue instanceof Date)
            cr.appendToCriterionList(new Criterion(grpFieldName, OperatorId.EQUALS,(Date) grpValue));
        else if (grpValue instanceof Boolean)
            cr.appendToCriterionList(new Criterion(grpFieldName, OperatorId.EQUALS,(Boolean) grpValue));
        else
            cr.appendToCriterionList(new Criterion(grpFieldName, OperatorId.EQUALS,String.valueOf(grpValue)));
    }

    @Override
    public boolean isGrouping()
    {
        return grpFieldName!=null;
    }

}
