package com.mwlib.tablo.cache;

import com.mycompany.common.cache.INm2Ix;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 19.11.14
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public interface INm2IxEx extends INm2Ix
{
    /**
     * @return возвратить мапинг всех полей поля включая технические
     */
    Map<String, Integer> getAllColName2Ix();

}
