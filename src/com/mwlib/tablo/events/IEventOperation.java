package com.mwlib.tablo.events;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 02.09.14
 * Time: 12:40
 * Интерфейс реализации назначения событий пользователям
 */
public interface IEventOperation
{
    /**
     * Взять событие на контроль пользователя
     * @param userId - идентифкатор пользователя
     * @param eventId - идентифкатор события.
     * @return - код завершения операции
     */
    int addEvent2User(int userId,String eventId) throws StorageExeption;
    int delEventFromUser(int userId,String eventId) throws StorageExeption;
    Set<String> getEventsByUser(int userId) throws StorageExeption;

}
