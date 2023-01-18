package com.mycompany.client.updaters;

import com.mycompany.client.utils.IDSRegister;
import com.mycompany.client.utils.MyDSCallback;
import com.mycompany.client.utils.SetGridException;
import com.mycompany.common.FieldException;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGridField;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 13.03.15
 * Time: 11:22
 * Апдейтер метаданных
 */
abstract public class AMetaDSCallback extends MyDSCallback
{
    protected String dataURL;
    protected IDSRegister register;

    public AMetaDSCallback(String headerURL, final String dataURL, IDSRegister register)
    {
        if (register!=null)
            register.registerDataSource(headerURL, true, null);
        this.dataURL = dataURL;
        this.register = register;

    }

    public void execute(DSResponse response, Object rawData, DSRequest request)
    {
                try
                {

                    int status=response.getStatus();

                    if (status!= RPCResponse.STATUS_SUCCESS)
                    {
                        SC.say("ERROR HEADER GETTING: " + status + " " + response.getAttribute("httpResponseText"));
                        return;
                    }
                    setMetaData(response);
                }
                catch (SetGridException e)
                {
                    e.printStackTrace();  //TODO Что делать на это исключение
                }
                catch (Exception e)
                {
                    e.printStackTrace(); //TODO Что делать на это исключение
                }
            }

    abstract protected void setMetaData(DSResponse dsResponse) throws SetGridException, FieldException;
}
