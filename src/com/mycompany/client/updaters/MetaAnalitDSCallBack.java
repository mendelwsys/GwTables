package com.mycompany.client.updaters;

import com.mycompany.client.utils.IDSRegister;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.FieldException;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mycompany.common.analit2.IAnalisysDescImpl;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 13.03.15
 * Time: 13:41
 *
 */
abstract public class MetaAnalitDSCallBack  extends  AMetaDSCallback
{
    public MetaAnalitDSCallBack(String headerURL, final String dataURL, IDSRegister register) {
        super(headerURL, dataURL, register);
    }

    @Override
    protected void setMetaData(DSResponse dsResponse) throws SetGridException, FieldException
    {
        Record[] metaData = dsResponse.getData();
        if (metaData!=null && metaData.length==1)
        {
            IAnalisysDesc desc = new IAnalisysDescImpl(metaData[0].getJsObj());
            updateByDesc(desc);
        }
    }

    abstract protected void updateByDesc(IAnalisysDesc desc) throws FieldException;
//    {
//        manager.setIAnalisysDesc(desc);
//    }
}
