package com.mycompany.client.test.fbuilder;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.FieldType;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.03.15
 * Time: 17:09
 * Табличная функция для задания
 */
public class TableFunction  implements IChannels
{

    static DataSource dataSource = new DataSource();
    static
    {
        dataSource.addField(new DataSourceField("value", FieldType.FLOAT));
    }

    List<TableFunctionElem> elements= new LinkedList<TableFunctionElem>();
    TableFunctionElem defValue;//Значение возвращаемое по умоляанию если не одно из условий не выполнено


    private Record[] r= new Record[]{new Record()};

    public TableFunction(List<TableFunctionElem> elements, TableFunctionElem defValue)
    {//TODO инициализация грид функция и текстовый ввод
        this.elements = elements;
        this.defValue = defValue;
    }

    @Override
    public Object transmit(Record[] records)
    {
        for (TableFunctionElem element : elements)
        {
            FunctionDet f = element.getFunctionDet();
            Double value=f.getValue(records);
            r[0].setAttribute("value", value);
            Record[] res = dataSource.applyFilter(r,element.getCriteria());
            if (res!=null && res.length>0)
                    return element.getOutValue(records, value);
        }

        Double value=null;
        FunctionDet f = defValue.getFunctionDet();
        if (f!=null)
            value=f.getValue(records);
        return defValue.getOutValue(records, value);
    }

    @Override
    public boolean isNeed2Recalculate() {
        return false;
    }
}
