package com.mycompany.client.utils;

import com.mycompany.client.test.t0.TestDataSource;
import com.smartgwt.client.data.DataSource;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 12.09.14
 * Time: 19:26
 * провадер для выполнения тестов
 */
public class GridMetaProviderTest extends GridMetaProviderBase
{
    public DataSource getNewDataSource(String url,boolean meta)
    {
        if (meta)
            return new TestDataSource(url);
        else
        {
            TestDataSource testDataSource = new TestDataSource(url);
            testDataSource.setForHead(false);
            return testDataSource;
        }
    }

}
