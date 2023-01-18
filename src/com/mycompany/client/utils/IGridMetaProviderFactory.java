package com.mycompany.client.utils;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 12.09.14
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public interface IGridMetaProviderFactory
{
    public static final String TEST_CLIENT="TEST_CLIENT";
    IGridMetaProvider createGridMetaProvider(String[][] params);
    IGridMetaProvider createGridMetaProvider(Map<String, Object> params);

}
