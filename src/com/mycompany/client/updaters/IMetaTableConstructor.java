package com.mycompany.client.updaters;

import com.mycompany.client.utils.SetGridException;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridField;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 12.03.15
 * Time: 19:09
 * Установщик полей таблиц
 */
public interface IMetaTableConstructor
{

    public DataSource getFieldsMetaDS();
    public DataSource getFilterDS();

    public boolean isMetaWasSet();

    public void setMetaWasSet(boolean metaWasSet);

    ListGridField[] getAllFields();

    void setHeaderGrid(Record[] gridOptions) throws SetGridException;

    void setAddIdDataSource(String addDataUrlId);

    String getAddIdDataSource();
}
