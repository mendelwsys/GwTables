package com.mycompany.client.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 12.09.14
 * Time: 15:15
 *
 */
public class GridMetaProviderFactory implements IGridMetaProviderFactory
{
    private static IGridMetaProviderFactory instance =new GridMetaProviderFactory();
    private static IGridMetaProvider gridMetaProvider = new GridMetaProviderBase();
    private static IGridMetaProvider gridMetaProviderTest=new GridMetaProviderTest();
    public static IGridMetaProviderFactory getInstance(){
        return instance;
    }


    public IGridMetaProvider createGridMetaProvider(String[][] params)
    {
        Map<String, Object> iparams = new HashMap<String,Object>();
        for (String[] param : params)
            iparams.put(param[0],param[1]);
        return createGridMetaProvider(iparams);
    }

    @Override
    public IGridMetaProvider createGridMetaProvider(Map<String, Object> params)
    {

        Object o = params.get(IGridMetaProviderFactory.TEST_CLIENT);
        if (o !=null && (o.toString().equalsIgnoreCase("true") || Boolean.valueOf(true).equals(o)))
            return gridMetaProviderTest;
        else
            return gridMetaProvider;
    }
}
