package com.mycompany.client.utils;

import com.mycompany.client.updaters.IMetaTableConstructor;
import com.mycompany.client.updaters.MetaTableDSCallback;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 09.07.15
 * Time: 13:24
 * Инициализатор каллбэков для метаданных и данных по умолчанию
 */
abstract public class DSDefUpdaterInit
{
    public IDSRegister getRegister()
    {
        return new DSRegisterImpl();
    }

    public Pair<DSCallback, MyDSCallback> initUpdater(IMetaTableConstructor metaConstructor, String headerURL, String dataURL, String tblType, int period)
    {

        DSCallback headerCallBack = initMetaDataUpdater(metaConstructor,headerURL,dataURL,tblType);
        MyDSCallback dataCallBack = initDataUpdater(period);
        return new Pair<DSCallback, MyDSCallback>(headerCallBack,dataCallBack);
    }

    public DSCallback initMetaDataUpdater(IMetaTableConstructor metaConstructor,String headerURL, final String dataURL, String tblType)
    {
        final Criteria criteria = new Criteria(TablesTypes.TTYPE, tblType);
        metaConstructor.setAddIdDataSource("$" + criteria.getAttribute(TablesTypes.TTYPE));
        return new MetaTableDSCallback(headerURL,dataURL,getRegister(),metaConstructor);
    }

    abstract public MyDSCallback initDataUpdater(final int period);

}
