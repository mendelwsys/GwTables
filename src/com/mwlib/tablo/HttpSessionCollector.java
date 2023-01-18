package com.mwlib.tablo;

import com.mycompany.server.export.ExportStore;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 13.10.14
 * Time: 14:14
 * удаляет сессии если это необходимо в соответсвии с принятыми бизнес правилами
 */
public class HttpSessionCollector implements HttpSessionListener {
    private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<String, HttpSession>();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        sessions.put(session.getId(), session);
    }


    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        String sessionId = event.getSession().getId();
        sessions.remove(sessionId);
        ExportStore.deleteAll(sessionId);
    }

    public static HttpSession find(String sessionId) {
        return sessions.get(sessionId);
    }

    public static HttpSession remove(String sessionId) {
        return sessions.remove(sessionId);
    }

    public static int getCountSessions()
    {
        return sessions.size();
    }

}