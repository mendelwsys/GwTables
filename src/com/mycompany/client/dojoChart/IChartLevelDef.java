package com.mycompany.client.dojoChart;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Record;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 04.03.15
 * Time: 15:12
 * Описатель уровня графика
 */
public interface IChartLevelDef {
    String getGrpViewName(Record record);

    Object calcGrpValue(Record record);

    void appendToCriterionList(AdvancedCriteria cr, ValDef grpDef);

    boolean isGrouping();

    String getColRefName();

    Map<Object,IGetRecordVal> getGroupF();

    IGetRecordVal removeGroupF(Object id);

    IGetRecordVal getGroupF(Object id);

    void setGroupF(IGetRecordVal groupF);

    JavaScriptObject getChartTitle(Object id, ValDef levelDef);

    boolean isAllowEntrance();
}
