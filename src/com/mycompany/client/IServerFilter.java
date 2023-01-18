package com.mycompany.client;

import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 28.11.14
 * Time: 17:54
 * Интерфейс серверного фильтра
 */
public interface IServerFilter
{
    Map<String,List<String>> append2Criteria(Criteria criteria,Map<String,List<String>> param2List);

    /**
     * @return получить текущий фильтр
     */
    AdvancedCriteria getCriteria();

    void setCriteria(AdvancedCriteria criteria);

    /**
     * Установить передаваемый параметр исходя из текущего фильтра
     * @param criteria - устанавливаемый текущим фильтром параметр
     */
    void set2Criteria(Criteria criteria);

    /**
     * Сбросить передаваемый параметр
     * @param criteria - сбрасываемый параметр
     */
    void reset2Criteria(Criteria criteria);
}
