package com.mycompany.client.utils;

import com.google.gwt.json.client.JSONArray;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.util.SC;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 13.03.15
 * Time: 11:42
 * Регистратор
 */
public class DSRegisterImpl implements IDSRegister
{
    public static final String TRANS = "trans";

    public String registerDataSource(String url, boolean meta, String addUrlName)
    {
//TODO Для удаления 13.03.2015
//        if (addUrlName==null)
//            addUrlName="";
//        String idDataSource = (url+addUrlName).replace(".", "_");
//        idDataSource = idDataSource.replace("/", "$");
//        DataSource dataSource;
//        if (DataSource.get(idDataSource)==null)
//        {
//            dataSource = getNewDataSource(url, meta);
//            dataSource.setID(idDataSource);
//            dataSource.setDataFormat(DSDataFormat.JSON);
//        }
//return idDataSource;
        DataSource dataSource=registerDataSource2(url,meta,addUrlName);
        return dataSource.getID();
    }

    public  DataSource registerDataSource2(String url, boolean meta, String addUrlName)
    {

        if (addUrlName==null)
            addUrlName="";
        String idDataSource = (url+addUrlName).replace(".", "_");
        idDataSource = idDataSource.replace("/", "$");
        DataSource dataSource;
        if ((dataSource=DataSource.get(idDataSource))==null)
        {
            dataSource = getNewDataSource(url, meta);
            dataSource.setID(idDataSource);
            dataSource.setDataFormat(DSDataFormat.JSON);
            dataSource.setUseStrictJSON(SC.isIE());  //TODO Протестировать с этим флагом (avoid for memory leak  in IE9) //TODO !!!Потом включить!!!!
        }
        return dataSource;
//        String idDataSource=registerDataSource(url,meta,addUrlName); //TODO Умник блять, здесь источник данных не должен создаваться что установит туда даты для трансляции!!!!
//        return DataSource.get(idDataSource);
    }

    public DataSource getNewDataSource(String url, boolean meta)
    {
        if (meta)
            return new DataSource(url);
        else
        {
            final DataSource dataSource = new DataSource(url)
            {
                protected void transformResponse(DSResponse dsResponse,
                         DSRequest dsRequest,
                         Object data)
                {
                    JSONArray trans = XMLTools.selectObjects(data, TRANS);
                    dsResponse.setAttribute(TRANS,trans.getJavaScriptObject());
                }
            };
            dataSource.setRecordXPath("tuples");
//            dataSource.addField(new DataSourceField("TESTDATE",FieldType.DATETIME));

            return dataSource;
        }
    }
}
