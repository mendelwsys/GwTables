package com.mycompany.client.utils;

import com.mycompany.client.updaters.DataDSCallback;
import com.mycompany.client.updaters.IMetaTableConstructor;
import com.mycompany.client.updaters.MetaTableDSCallback;
import com.mycompany.common.Pair;
import com.mycompany.client.updaters.IGridConstructor;
import com.smartgwt.client.data.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 21.05.14
 * Time: 17:38
 * Реализация конструктора грида (конструктор получает мета-данные и данные с сервера и выполняет заполнение грида)
 */
public class GridMetaProviderBase extends DSRegisterImpl
        implements IGridMetaProvider
{

    @Override
    public Pair<DSCallback, MyDSCallback> initGrid2(final IGridConstructor gridConstructor, String headerURL, final String dataURL, int period)
    {

        gridConstructor.getDataBoundComponent().setAutoFetchData(false);//Не удалось скрыть приходится устанавливать этот флаг до того как запросить данные по хидерам

        DSCallback headerCallBack = initMetaDataUpdater(gridConstructor, headerURL, dataURL);

//        final DataSource dst = new DataSource();
//        dst.setClientOnly(true);
//        dst.addField(new DataSourceField("TESTDATE",FieldType.DATETIME));
        //Обработчик данных
        MyDSCallback dataCallBack = initDataUpdater(gridConstructor, period);
        return new Pair(headerCallBack,dataCallBack);
    }

    @Override
    public MyDSCallback initDataUpdater(final IGridConstructor gridConstructor, final int period) {
        return new DataDSCallback(period)
        {
                    @Override
                    protected void updateData(Record[] data, boolean resetAll) throws SetGridException
                    {
                        gridConstructor.updateDataGrid(data, resetAll);
                    }
        };

    }

    @Override
    public DSCallback initMetaDataUpdater(final IMetaTableConstructor metaConstructor, String headerURL, final String dataURL)
    {
        return new MetaTableDSCallback(headerURL,dataURL,this,metaConstructor);
    }


}
