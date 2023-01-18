package com.mycompany.client.test.fbuilder;


import com.smartgwt.client.data.Record;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.03.15
 * Time: 19:09
 * Отдать выходное значение канала
 */
public interface IChannels
{
    Object transmit(Record[] records);

    boolean isNeed2Recalculate();
}
