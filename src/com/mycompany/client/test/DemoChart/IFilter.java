package com.mycompany.client.test.DemoChart;

import com.smartgwt.client.data.Record;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 05.03.15
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public interface IFilter
{
   boolean isAccept(Record record);
}
