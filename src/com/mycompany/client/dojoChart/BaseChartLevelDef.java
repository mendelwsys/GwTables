package com.mycompany.client.dojoChart;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.OperatorId;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 26.01.15
 * Time: 16:26
 * Описатель диаграммы для создания иерархии
 */
abstract public class BaseChartLevelDef implements IChartLevelDef
{
    public String colRefName;//Имя колонки, которая содержит идентифкатор ID кортежа
    protected boolean allowEntrance;

    public BaseChartLevelDef(String colRefName,boolean allowEntrance)
    {
        this.colRefName = colRefName;
        this.allowEntrance=allowEntrance;
    }

    @Override
    public String getColRefName() {
        return colRefName;
    }

    @Override
    public Map<Object,IGetRecordVal> getGroupF() {
        return groupF;
    }

    @Override
    public IGetRecordVal removeGroupF(Object id) {
        return groupF.remove(id);
    }

    @Override
    public IGetRecordVal getGroupF(Object id) {
        return groupF.get(id);
    }

    @Override
    public void setGroupF(IGetRecordVal groupF) {
        this.groupF.put(groupF.getIdVal(),groupF);
    }

    Map<Object,IGetRecordVal> groupF= new HashMap<Object,IGetRecordVal>();

    @Override
    public JavaScriptObject getChartTitle(Object id, ValDef levelDef)
    {
        JavaScriptObject rv=null;
        IGetRecordVal recVal=groupF.get(id);
        if (recVal!=null)
            rv=recVal.getCharTitle(levelDef);
        return rv;
    }

    @Override
    public boolean isAllowEntrance()
    {
        return allowEntrance;
    }
}
