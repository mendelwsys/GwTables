package com.mycompany.client.utils;

import com.smartgwt.client.data.DataSource;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 13.03.15
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public interface IDSRegister
{
    DataSource registerDataSource2(String url, boolean meta, String addUrlName);

    String registerDataSource(String url, boolean meta, String addUrlName);
}
