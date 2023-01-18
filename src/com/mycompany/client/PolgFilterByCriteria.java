package com.mycompany.client;

import com.mycompany.client.apps.App.api.ExtendGridByPlaces;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.OperatorId;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 09.09.15
 * Time: 11:41
 * фильтр со специальной обработкой поля PLACE_CODE_NAME -
 * обработчик поля берет и ищет пересечение мно-ва кодов в фильтре и в поле PLACE_CODE_NAME, если мно-во содержит хотя
 * бы один элемент соответсвующий узел критерия выдает true. Узел может находится на любой вложенности дерева критериев фильтра.
 */
public class PolgFilterByCriteria extends  CliFilterByCriteria {
    public PolgFilterByCriteria(DataSource filterDS, Criteria criteria)
    {
        super(filterDS, criteria);
    }

    public Record[] filter(Record[] records)
    {
        if (!findPlaceCodeCri(this.getCriteria()))
            return super.filter(records);
        else
           return filterPlaceCodeCri(this.getCriteria(),records);
    }

//    private Record[] filter(Criteria criteria,Record[] records)
//    {
//        if (!findPlaceCodeCri(criteria))
//            return getFilterDS().applyFilter(records,criteria);
//        else
//           return filterPlaceCodeCri(this.getCriteria(),records);
//    }


    private Record[] filterPlaceCodeCri(Criteria criteria,Record[] records)
    {
        if (criteria.isAdvanced())
        {

            final AdvancedCriteria advancedCriteria;
            if (criteria instanceof AdvancedCriteria)
                advancedCriteria = (AdvancedCriteria)criteria;
            else
                advancedCriteria = criteria.asAdvancedCriteria();
            Criterion[] crit = advancedCriteria.getCriteria();
            OperatorId op = advancedCriteria.getOperator();

            if (op.equals(OperatorId.AND))
                return andFilter(records, crit);
            else if (op.equals(OperatorId.OR))
                return orFilter(records, crit);
            else
                return notFilter(records, crit);
        }
        else
             return getFilterDS().applyFilter(records,criteria);
    }


    private Record[] andFilter(Record[] records, Criterion[] crit)
    {
        List<Criterion> specCriterion=new LinkedList<Criterion>();
        for (Criterion criterion : crit)
        {
            if ((ExtendGridByPlaces.PLACE_CODE_NAME.equalsIgnoreCase(criterion.getFieldName()) && criterion.getOperator().equals(OperatorId.IN_SET)))
                specCriterion.add(criterion);
            else
                records=filterPlaceCodeCri(criterion,records);
        }

        Set<Record> rv= new HashSet<Record>();
        for (Criterion criterion : specCriterion)
        {
                String polgs=criterion.getValueAsString();
                String [] sVals= polgs.split(",");
                Set<String> vals=new HashSet<String>(Arrays.asList(sVals));
                for (Record inRecord : records)
                {
                    String sPlaces=inRecord.getAttribute(ExtendGridByPlaces.PLACE_CODE_NAME);
                    if (sPlaces==null || sPlaces.length()==0)
                        continue;

                    String[] recVals = sPlaces.split(",");
                    for (String recVal : recVals)
                        if (recVal.length()>0 && vals.contains(recVal))
                        {
                            rv.add(inRecord);
                            break;
                        }
                }
                records=rv.toArray(new Record[rv.size()]);
        }
        return records;
    }


    private Record[] orFilter(Record[] records, Criterion[] crit)
    {
        Set<Record> resCheck=new HashSet<Record>(Arrays.asList(records));
        resCheck.removeAll(Arrays.asList(notFilter(records,crit))) ;
        return resCheck.toArray(new Record[resCheck.size()]);
    }


    private Record[] notFilter(Record[] records, Criterion[] crit)
    {

        Set<Record> toCheck=new HashSet<Record>(Arrays.asList(records));//Мно-во не удовлетворяющее ни одному критерию

        List<Criterion> specCriterion=new LinkedList<Criterion>();
        for (Criterion criterion : crit)
        {
            if ((ExtendGridByPlaces.PLACE_CODE_NAME.equalsIgnoreCase(criterion.getFieldName()) && criterion.getOperator().equals(OperatorId.IN_SET)))
                specCriterion.add(criterion);
            else
            {
                    records=filterPlaceCodeCri(criterion,toCheck.toArray(new Record[toCheck.size()]));
                    toCheck.removeAll(Arrays.asList(records));
            }
        }

        for (Criterion criterion : specCriterion)
        {
                records=toCheck.toArray(new Record[toCheck.size()]);

                String polgs=criterion.getValueAsString();
                String [] sVals= polgs.split(",");
                Set<String> vals=new HashSet<String>(Arrays.asList(sVals));

                for (Record inRecord : records)
                {
                    String sPlaces=inRecord.getAttribute(ExtendGridByPlaces.PLACE_CODE_NAME);
                    if (sPlaces==null || sPlaces.length()==0)
                        continue;

                    String[] recVals = sPlaces.split(",");
                    for (String recVal : recVals)
                        if (recVal.length()>0 && vals.contains(recVal))
                        {
                            toCheck.remove(inRecord);
                            break;
                        }
                }
        }

        return toCheck.toArray(new Record[toCheck.size()]);
    }



    private boolean findPlaceCodeCri(Criteria criteria)
    {
        if (criteria.isAdvanced())
        {
            final AdvancedCriteria advancedCriteria;

            if (criteria instanceof AdvancedCriteria)
                advancedCriteria = (AdvancedCriteria)criteria;
            else
                advancedCriteria = criteria.asAdvancedCriteria();

            Criterion[] crit = advancedCriteria.getCriteria();
            for (Criterion criterion : crit)
            {
                if (
                        ((criterion.isAdvanced()) && findPlaceCodeCri(criterion))
                        ||
                        (ExtendGridByPlaces.PLACE_CODE_NAME.equalsIgnoreCase(criterion.getFieldName()) && criterion.getOperator().equals(OperatorId.IN_SET))
                   )
                   return true;
            }
        }
        return false;
    }

}
