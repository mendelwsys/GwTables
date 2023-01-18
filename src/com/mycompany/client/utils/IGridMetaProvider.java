package com.mycompany.client.utils;

import com.mycompany.client.updaters.IMetaTableConstructor;
import com.mycompany.common.Pair;
import com.mycompany.client.updaters.IGridConstructor;
import com.smartgwt.client.data.DSCallback;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 12.09.14
 * Time: 14:55
 * Интерфейс для получения данных и конструирования грида
 */
public interface IGridMetaProvider extends IDSRegister
{
    Pair<DSCallback, MyDSCallback> initGrid2(IGridConstructor gridConstructor, String headerURL, String dataURL, int period);

    MyDSCallback initDataUpdater(IGridConstructor gridConstructor, int period);

    DSCallback initMetaDataUpdater(IMetaTableConstructor metaConstructor, String headerURL, String dataURL);
}
