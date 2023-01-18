package com.mycompany.client.dojoChart;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.02.15
 * Time: 13:21
 * Получить значение записи
 */
public interface IGetRecordVal
{
    Object getRecordsVal(Map<Object,Record> records,ValDef def);

    Object getIdVal();

    JavaScriptObject getCharTitle(ValDef def);

    String getViewVal(ValDef def);

    IFormatValue getFormatValue();

    void setBaseTitle(String baseTitle);

}
