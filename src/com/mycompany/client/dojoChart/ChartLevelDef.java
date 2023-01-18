package com.mycompany.client.dojoChart;

import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.OperatorId;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.01.15
 * Time: 16:26
 * Описатель диаграммы для создания иерархии
 */
public class ChartLevelDef extends BaseChartLevelDef
{
    private String colName; //колонка по которой была произведена группировка

    @Override
    public String getGrpViewName(Record record)
    {
        String text=record.getAttributeAsString(colTextName);
        if (text==null)
            text="Прочие";
        return text;
    }

    private String colTextName;//колонка которая содержит имя группы

    public ChartLevelDef(String colRefName, String colName, String colTextName)
    {
        super(colRefName,true);
        this.colName = colName;
        this.colTextName = colTextName;
    }

    @Override
    public Object calcGrpValue(Record record)
    {
        return record.getAttributeAsObject(colName);
    }



//TODO Закоментировано                       List<Pair<String, Object>> ll = grpDef.getParentDef().getParentGroupsVal();
//TODO Здесь не доделанный метод, который собирает всю иерархию снизу вверх, на само деле он должен быть
//TODO использован для генерации серверного критерия для запроса кортежей данного уровня.
//public void appendToCriterionList(AdvancedCriteria cr, ValDef grpDef)
//{
//                        for (Pair<String, Object> colName2groupDef : ll)
//                        {
//                            final Object second = colName2groupDef.second;
//                            if (second==null)
//                                cr.appendToCriterionList(new Criterion(colName2groupDef.first, OperatorId.IS_NULL)); //TODO Обработать это на сервере, сейчас не обрабатывается!!!!
//                            else if (second instanceof Integer)
//                                cr.appendToCriterionList(new Criterion(colName2groupDef.first, OperatorId.EQUALS,(Integer) second));
//                            else
//                                cr.appendToCriterionList(new Criterion(colName2groupDef.first, OperatorId.EQUALS,"'"+String.valueOf(second)+"'")); //TODO Это специфика данной группировки,!!! не общее решение!!!!
//
//                        }
//                        cr.appendToCriterionList(new Criterion(grpDef.getParentDef().getMetaDef().getColName(), OperatorId.EQUALS,0));
//    }

    @Override
    public void appendToCriterionList(AdvancedCriteria cr, ValDef grpDef)
    {//TODO Частный случай, поскольку набор кортежей опредляется вообще говоря значениями групперовок всей иерархии до текущего уровня включительно
        final Object grpValue = grpDef.getGrpValue();
        if (grpValue==null)
            cr.appendToCriterionList(new Criterion(colName, OperatorId.IS_NULL));
        else if (grpValue instanceof Integer)
            cr.appendToCriterionList(new Criterion(colName, OperatorId.EQUALS,(Integer) grpValue));
        else
        {
            String  grp_name=String.valueOf(grpValue);
            {
                if (grp_name.contains("_"))
                {
                    String[]  dor_kod2Serv =grp_name.split("_");
                    setDorKod(cr, dor_kod2Serv[0]);
                }
                else
                    setDorKod(cr, grp_name);
                cr.appendToCriterionList(new Criterion(colName, OperatorId.EQUALS,0));
            }
        }

    }

    private void setDorKod(AdvancedCriteria cr, String s) {
        try {
            int dor_kod=Integer.parseInt(s);
            if (dor_kod<0)
                dor_kod=-dor_kod;
            cr.appendToCriterionList(new Criterion(TablesTypes.DOR_CODE, OperatorId.EQUALS,dor_kod));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean isGrouping()
    {
        return colName!=null;
    }
//TODO Для удаления 04032015
//    public String getColName()
//    {
//        return colName;
//    }
//TODO Для удаления 04032015
//    public void setColName(String colName)
//    {
//        this.colName = colName;
//    }

}
