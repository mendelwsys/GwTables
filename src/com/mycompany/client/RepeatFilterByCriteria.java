package com.mycompany.client;

import com.smartgwt.client.data.*;
import com.smartgwt.client.types.OperatorId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Групповой фильтр для фильтрации повторений
 */
public class RepeatFilterByCriteria extends CliFilterByCriteria
{

    public Criteria getGrpCriteria()
    {
        return grpCriteria;
    }

    public void setGrpCriteria(Criteria grpCriteria) {
        this.grpCriteria = grpCriteria;
    }

    private Criteria grpCriteria;

    public RepeatFilterByCriteria(DataSource filterDS, Criteria criteria) {
        super(filterDS, criteria);
    }

//    public RepeatFilterByCriteria(DataSource filterDS) {
//        super(filterDS);
//    }



    @Override
    public Record[] filter(Record[] records)
    {
        int valF = 1;//Значение группировчной функции, в данном случае
        OperatorId operator=OperatorId.GREATER_THAN;

        try {
            Criteria grpCriteria = this.getGrpCriteria();
            if (grpCriteria!=null)
            {
                Criterion[] crit = ((AdvancedCriteria) grpCriteria).getCriteria();
                if (crit.length>0)
                {
                    operator = crit[0].getOperator();
                    String val=crit[0].getValueAsString();
                    valF=Integer.parseInt(val);
                }
            }
        }
        catch (NumberFormatException e)
        { //

        }


        Map<String,List<Record>> resRecords=new HashMap<String,List<Record>>();

        List<String> fnames= new LinkedList<String>();

        Criterion[] crit = ((AdvancedCriteria) this.getCriteria()).getCriteria();
        for (Criterion criterion : crit)
            fnames.add(criterion.getFieldName());

        String key="";
        for (Record record : records)
        {
            key="";
            for (String fname : fnames)
                key+=record.getAttribute(fname)+"#";
            List<Record> ll = resRecords.get(key);
            if (ll==null)
                resRecords.put(key, ll = new LinkedList<Record>());
            ll.add(record);
        }
        List<Record> resVal= new LinkedList<Record>();
        for (List<Record> recordList : resRecords.values())
        {
            switch (operator)
            {
                case GREATER_THAN:
                {
                    if (recordList.size()> valF)
                        resVal.addAll(recordList);
                    break;
                }
                case GREATER_OR_EQUAL:
                {
                    if (recordList.size()>= valF)
                        resVal.addAll(recordList);
                    break;
                }
                case LESS_THAN:
                {
                    if (recordList.size()< valF)
                        resVal.addAll(recordList);
                    break;
                }
                case LESS_OR_EQUAL:
                {
                    if (recordList.size()<= valF)
                        resVal.addAll(recordList);
                    break;
                }
                case EQUALS:
                {
                    if (recordList.size() == valF)
                        resVal.addAll(recordList);
                    break;
                }
                case NOT_EQUAL:
                {
                    if (recordList.size() != valF)
                        resVal.addAll(recordList);
                    break;
                }
            }
        }
        return resVal.toArray(new Record[resVal.size()]);
    }

}
