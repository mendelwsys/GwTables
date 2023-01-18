package com.mycompany.client.dojoChart;

import com.mycompany.common.Pair;
import com.smartgwt.client.data.Record;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.02.15
 * Time: 11:58
 * Определение значений для уровня иерархии
 */
public class ValDef
{
    /**
     * получить значение для визуализации
     * @param id - ид графика которым отображается значение текущего уровня
     * @return - строка для отображения
     */
    public  String getViewVal(Object id)
    {
        final IFormatValue formatValue = getFormatValue(id);
        Value value = val.get(id);
        return formatValue.formatValue(value);

//        Object value = getValue(id);
//        if (value==null)
//            value=0d;
//
//        Number resNumber;
//        final IFormatValue formatValue = getFormatValue(id);
//
//
//
//        if (ListGridFieldType.INTEGER.getValue().equals(formatValue.getFtype()))
//            resNumber= (int) Math.round(value);
//        else
//            resNumber=value;
//
//        String format=formatValue.getFormat();
//
//        if (format!=null )
//        {
//            try
//            {
//                return NumberFormat.getFormat(format).format(resNumber);
//            } catch (IllegalArgumentException e) {
//                //
//            }
//        }
//        return String.valueOf(resNumber);
    }

    public String getFullViewText()
    {
        return getFullViewText(this);
    }

    public String getFullViewText(ValDef def)
    {
        String viewText=def.getViewText();
        if (def.getParentDef()!=null)
        {
            String parentView=getFullViewText(def.getParentDef());
            if (parentView!=null && parentView.length()>0)
                return (viewText!=null && viewText.length()>0)?(parentView+" "+viewText):parentView;
        }
        return viewText;
    }


    public String getViewText() {
        return viewText;
    }

    String viewText;


    public IChartLevelDef getMetaDef() {
        return metaDefI;
    }

    private IChartLevelDef metaDefI;

    public Map<Object, ValDef> getChildDef() {
        return childDef;
    }

    private Map<Object,ValDef> childDef= new HashMap<Object,ValDef>();



    public ValDef getParentDef() {
        return parentDef;
    }

    private ValDef parentDef;

    public Object getGrpValue() {
        return grpValue;
    }

    private Object grpValue;


    public String getColor(Object id)
    {
        Value value=val.get(id);
        if (value!=null)
            return value.getColor();
        return null;
    }

    public void setColor(String color,Object id)
    {
        Value value=val.get(id);
        if (value!=null)
            value.setColor(color);
    }


    public ValDef(IChartLevelDef metaDefI,ValDef parentDef,Object grpValue,String viewText)
    {
        this.metaDefI = metaDefI;
        this.parentDef = parentDef;
        this.grpValue = grpValue;
        this.viewText=viewText;
    }

    public Object getMeanValue(Object id)
    {
        final Value value = val.get(id);
        if (value!=null)
            return value.getMean();
        return null;
    }

    public Value getValue(Object id)
    {
        return val.get(id);
    }

    public boolean containsKey(Object id)
    {
        return val.containsKey(id);
    }


    public IFormatValue getFormatValue(Object id)
    {
        final Value value = val.get(id);
        if (value!=null)
            return value.getFormatValue();
        return null;
    }


    public Value setVal(Object val,IFormatValue formatValue,Object id)
    {
        return this.val.put(id,new Value(val,formatValue));
    }

    public Value removeVal(Object id)
    {
        return this.val.remove(id);
    }


    private Map<Object,Value> val=new HashMap<Object,Value>();

    public Object getRecRef() {
        return recRef;
    }

//    public void setRecRef(Object recRef) {
//        this.recRef = recRef;
//    }

    private Object recRef; //Ссылка на идентификатор записи

    public boolean isRecalc() {
        return recalc;
    }

    public void setRecalc() {
        this.recalc = true;
    }

    private boolean recalc =true;

    /**
     *
     * @param record - запись которую нужно разложить
     * @param tailDefI - мно-во описателей
     * @param index - следующий в иерархии описатель
     */

    public void addRecord(Record record,IChartLevelDef[] tailDefI,int index)
    {
        if (metaDefI.isGrouping())
        {
            Object nextGroupVal = metaDefI.calcGrpValue(record);//получить значение определяющую группу текущего уровня
            ValDef valDef=childDef.get(nextGroupVal);
            if (valDef!=null)
                valDef.addRecord(record, tailDefI,index+1);//Группа есть добавляем
            else
            {
                String text= metaDefI.getGrpViewName(record);
//                if (colTextName!=null)
//                    text=record.getAttributeAsString(colTextName);
//                if (text==null)
//                    text="Прочие";

                childDef.put(nextGroupVal,valDef=new ValDef(tailDefI[index+1],this,nextGroupVal,text));//группы нет - создаем
                valDef.addRecord(record, tailDefI,index+1);
            }
        }
        else
            recRef=record.getAttributeAsObject(metaDefI.getColRefName());// на последнем этапе каждая группа содержит ровно одну запись, группировка должна сожержать одну запись


        recalc =true;
    }


    public List<Pair<String,Object>> getParentGroupsVal()
    {
        ValDef parentDef=this.getParentDef();
        List<Pair<String,Object>> rv;
        rv = new LinkedList<Pair<String,Object>>();
//TODO Здесь не доделанный метод который собирает всю иерархию снизу вверх, на само деле он должен быть
//TODO использован для отображения в таблице кортежей для данного уровня
//        if (parentDef!=null)
//             rv = parentDef.getParentGroupsVal();
//        else
//            rv = new LinkedList<Pair<String,Object>>();
//        {
//            if (this.getParentDef()!=null)
//                rv.add(new Pair<String,Object>(this.getParentDef().getMetaDef().getColName(),this.getGrpValue()));
//        }

        return rv;
    }

    public boolean removeRecord(Record record,IChartLevelDef[] tailDefI,int index)
    {
        if (record==null)
            return false;

        Object nextGroupVal = metaDefI.calcGrpValue(record);// record.getAttributeAsObject(metaDef.getColName());
        ValDef valDef=childDef.get(nextGroupVal);

        if (valDef!=null)
        {
            if (valDef.removeRecord(record, tailDefI,index))
                childDef.remove(nextGroupVal);
        }
        else if (index>= tailDefI.length)
        {
            if (recRef!=null && recRef.equals(record.getAttributeAsObject(metaDefI.getColRefName())))
                return true;
            else
                throw new RuntimeException("Error for tuple remove");
        }
        recalc =true;
        return childDef.isEmpty();
    }



    public void reCalcGroups(Map<Object,Record> recordMap)
    {
        final Map<Object, IGetRecordVal> mapGroupF = metaDefI.getGroupF();
        for (Object key : mapGroupF.keySet())
        {
            IGetRecordVal groupF=mapGroupF.get(key);
            final Object recordsVal = groupF.getRecordsVal(recordMap, this);

            if (recordsVal!=null)
            {
                final Value newValue = new Value(recordsVal,groupF.getFormatValue());
                Value preValue=val.put(key, newValue);
                if (preValue!=null)
                    newValue.setColor(preValue.getColor());
            }
            else
                this.removeVal(key);
        }
        recalc=false;
    }


    public void removeByGrpId(Object key)
    {
        removeVal(key);
        if (childDef!=null)
            for (ValDef valDef : childDef.values())
                valDef.removeByGrpId(key);
    }
}
