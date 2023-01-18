package com.mycompany.client.test.fbuilder;


import com.smartgwt.client.data.Record;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.03.15
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
public interface IAggregateCalculate
{
    Double getValue(Record[] records);
}
