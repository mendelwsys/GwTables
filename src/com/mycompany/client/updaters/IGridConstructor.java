package com.mycompany.client.updaters;

import com.mycompany.client.utils.SetGridException;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.DataBoundComponent;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 04.09.14
 * Time: 12:21
 * Система обновления гридов
 */
public interface IGridConstructor extends IMetaTableConstructor
{
    void setDiagramDesc(Map mapDesc) throws SetGridException;
    DataBoundComponent getDataBoundComponent();
    void setDataBoundComponent(DataBoundComponent grid);
    void updateDataGrid(Record[] data, boolean resetAll) throws SetGridException;


}
