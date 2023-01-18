package com.mycompany.client.operations;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 07.11.14
 * Time: 17:00
 * Интерфейс клиентского фильтра, используемого в том числе и для входящих данных
 */
public interface ICliFilter
{
    void setCriteria(Criteria cr);
    Criteria getCriteria();
    Record[] filter(Record[] records);


}
