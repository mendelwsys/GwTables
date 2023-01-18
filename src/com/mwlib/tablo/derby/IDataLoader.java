package com.mwlib.tablo.derby;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.07.15
 * Time: 16:26
 * Интерфейс для загрузчиков данных для их консолидации
 */
public interface IDataLoader {
    Map[] getData() throws Exception;
}
