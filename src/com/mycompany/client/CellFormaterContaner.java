package com.mycompany.client;

import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 20.05.15
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */
public class CellFormaterContaner implements CellFormatter {

    private CellFormatter formater;
    private ListGrid grid;

    public CellFormaterContaner(CellFormatter formater,ListGrid grid)
    {
        this.formater = formater;
        this.grid = grid;
    }

    @Override
    public String format(Object value, ListGridRecord record, int rowNum, int colNum)
    {
        if (formater==null)
            return grid.getDefaultFormattedValue(record, rowNum, colNum);
        return formater.format(value,record,rowNum,colNum);
    }
}
