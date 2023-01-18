package com.mycompany.client.updaters;

import com.mycompany.client.utils.IDSRegister;
import com.mycompany.client.utils.SetGridException;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
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
public class MetaTableDSCallback extends  AMetaDSCallback
{
    private IMetaTableConstructor metaConstructor;

    public MetaTableDSCallback(String headerURL, final String dataURL, IDSRegister register, final IMetaTableConstructor metaConstructor)
    {
        super(headerURL, dataURL, register);
        register.registerDataSource(headerURL, true, null);
        this.dataURL = dataURL;
        this.register = register;
        this.metaConstructor = metaConstructor;

    }

    protected void setMetaData(DSResponse dsResponse) throws SetGridException
    {
        metaConstructor.setHeaderGrid(dsResponse.getData());
        DataSource dataSource= register.registerDataSource2(dataURL, false, metaConstructor.getAddIdDataSource());
        if (!dataSource.isCreated())
        {
            ListGridField[] fields = metaConstructor.getAllFields();
            for (ListGridField field : fields)
                if (
                        ListGridFieldType.DATETIME.equals(field.getType()) ||
                        ListGridFieldType.DATE.equals(field.getType()) ||
                        ListGridFieldType.TIME.equals(field.getType())
                        )
                {
                       dataSource.addField(new DataSourceField(field.getName(), FieldType.valueOf(field.getType().name())));
                }
        }
        metaConstructor.setMetaWasSet(true); //!!!Устанавливаем флаг и разрешаем запрашивать данные только после установки и решистрации источника данных
    }

}
