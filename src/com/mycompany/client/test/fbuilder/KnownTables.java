package com.mycompany.client.test.fbuilder;

import com.mycompany.common.TablesTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 11.03.15
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
public class KnownTables
{
//TODO Перенести инициализацию на сервер
 static Map<String,String> tableNames = new HashMap<String,String>();
 static
    {
        tableNames.put(TablesTypes.WINDOWS,"Окна");
        tableNames.put(TablesTypes.WARNINGS,"Предупреждения");
        tableNames.put(TablesTypes.VIOLATIONS,"Нарушения");
        tableNames.put(TablesTypes.REFUSES,"Отказы");
        tableNames.put(TablesTypes.VIP_GID,"Отметки ГИД");
        tableNames.put(TablesTypes.LENTA,"Лента");
    }

  public static  Map<String,String> getTableMap()
  {
         return tableNames;
  }

}
