package com.mwlib.tablo.db;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 13.07.15
 * Time: 14:55
 * Интерфейс апдейтера данных
 */
public interface ICascadeUpdater {
    void initStartParams();

    void performUpdate();

    String getUpdaterName();
}
