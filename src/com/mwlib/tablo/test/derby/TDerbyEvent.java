package com.mwlib.tablo.test.derby;

import com.mwlib.tablo.events.IEventOperation;
import com.mwlib.tablo.events.StorageExeption;

import java.sql.SQLException;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 02.09.14
 * Time: 15:23
 * Тестовая имплементация операций по ассоцимированию пользователя с событиями с помощью БД derby
 */
public class TDerbyEvent implements IEventOperation{


    public int addEvent2User(int userId, String eventId) throws StorageExeption
    {
        try {
            TestDerby.insertUser2Event(userId,eventId);
            return 0;
        } catch (SQLException e) {
            throw new StorageExeption(e);
        }
    }

    public int delEventFromUser(int userId, String eventId) throws StorageExeption
    {
        try {
            TestDerby.deleteEvents(userId, eventId);
            return 0;
        } catch (SQLException e) {
            throw new StorageExeption(e);
        }
    }

    public Set<String> getEventsByUser(int userId) throws StorageExeption
    {
        try {
            return TestDerby.selectEventsById(userId);
        } catch (SQLException e) {
            throw new StorageExeption(e);
        }
    }
}
