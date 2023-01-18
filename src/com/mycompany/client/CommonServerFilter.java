package com.mycompany.client;

import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.types.OperatorId;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 28.11.14
 * Time: 17:57
 *
 */
public class CommonServerFilter implements IServerFilter
{

    private String parameter;

    public CommonServerFilter(String parameter)
    {
        this.parameter = parameter;
    }

    @Override
    public void setCriteria(AdvancedCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public AdvancedCriteria getCriteria() {
        return criteria;
    }

    protected AdvancedCriteria criteria=new AdvancedCriteria();

    protected String translateOperation(OperatorId operatorId)
    {
        switch (operatorId)
        {
            case AND:
            case OR:
            case NOT:
            case BETWEEN:
                return operatorId.getValue();
            case EQUALS:
                return "=";
            case NOT_EQUAL:
                return "<>";
            case LESS_THAN:
                return "<";
            case LESS_OR_EQUAL:
                return "<=";
            case GREATER_THAN:
                return ">";
            case GREATER_OR_EQUAL:
                return ">=";
            case CONTAINS:
            case CONTAINS_PATTERN:
                return " like ";
            case NOT_CONTAINS:
                return " not like ";
            case IN_SET:
                return " IN ";
            case NOT_IN_SET:
                return " NOT IN ";
            case NOT_NULL:
                return " IS NOT NULL ";
            case IS_NULL:
                return " IS NULL ";
            default:
                throw new UnsupportedOperationException("Unsupported operator:"+operatorId);
        }
    }

    protected String getSQLStringPresentation(Criterion criteria)
    {
        StringBuilder bld=new StringBuilder("");
        Criterion[] crit = criteria.getCriteria();
        OperatorId operator = criteria.getOperator();
        if (crit==null || crit.length==0)
        {
            if (operator.equals(OperatorId.NOT_NULL) || operator.equals(OperatorId.IS_NULL))
                return criteria.getFieldName()+ translateOperation(operator);
            else
            if (operator.equals(OperatorId.IN_SET) || operator.equals(OperatorId.NOT_IN_SET))
            {
                String[] strs=criteria.getValueAsStringArray();
                for (int i = 0; i < strs.length; i++) {
                    if (i>0)
                        bld.append(",");
                    bld.append(strs[i]);
                }
                return criteria.getFieldName()+ translateOperation(operator) +"( "+bld.toString()+" )";
            }
            else if (operator.equals(OperatorId.BETWEEN))
            {
                String[] strs=criteria.getValueAsStringArray();
                return "( "+criteria.getFieldName()+ translateOperation(operator) +strs[0]+" AND "+strs[1]+" )";
            }
            else
                return criteria.getFieldName()+ translateOperation(operator) +criteria.getValueAsString();
        }



        for (int i = 0; i < crit.length; i++)
        {
            Criterion criterion = crit[i];
            if (i>0)
                bld=bld.append(translateOperation(operator));
            bld=bld.append(" ( ").append(getSQLStringPresentation(criterion)).append(" ) ");
        }
        return bld.toString();
    }


    @Override
    public Map<String,List<String>> append2Criteria(Criteria criteria,Map<String,List<String>> param2List)
    {
        Criterion[] subcriterion = this.criteria.getCriteria();
        if (subcriterion==null || subcriterion.length==0)
            return new HashMap<String,List<String>>();

        if (param2List==null)
            param2List = new HashMap<String,List<String>>();

        final String sqlStringPresentation = getSQLStringPresentation(this.criteria);
        List<String> params=param2List.get(parameter);
        if (params==null)
        {
            param2List.put(parameter, params = new LinkedList<String>());
            criteria.setAttribute(parameter, sqlStringPresentation);
            params.add(sqlStringPresentation);
        }
        else
        {
            params.add(sqlStringPresentation);
            criteria.setAttribute(parameter, params.toArray(new String[params.size()]));
        }
        return param2List;
    }

    public void reset2Criteria(Criteria criteria)
    {
        if (criteria.getAttribute(parameter)!=null)
            criteria.setAttribute(parameter, new String[0]);
    }

    public void set2Criteria(Criteria criteria)
    {
        Criterion[] subcriterion = this.criteria.getCriteria();
        if (subcriterion==null || subcriterion.length==0)
        {
            reset2Criteria(criteria);
            return;
        }
        criteria.setAttribute(parameter, getSQLStringPresentation(this.criteria));
    }

}
