package com.mwlib.tablo.tables;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 19.07.15
 * Time: 17:26
 * мониторинговая информация для администратора
 */
public class AdminInfo
{
    public int getSessions() {
        return sessions;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
    }

    public Map[] getTables() {
        return tables;
    }

    public void setTables(Map[] tables) {
        this.tables = tables;
    }

    int sessions;//Кол-во сессий
    Map[] tables;//Информация о таблицах
}
