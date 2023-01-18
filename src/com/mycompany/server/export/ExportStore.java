package com.mycompany.server.export;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anton.Pozdnev on 31.03.2015.
 */
public class ExportStore {

    static final Map<String, Map<String, OutputStream>> exportStore = Collections.synchronizedMap(new HashMap<String, Map<String, OutputStream>>());


    public static void putExportRecord(String sessionId, String filename, OutputStream out) {
        Map<String, OutputStream> map = exportStore.get(sessionId);
        if (map == null) {
            map = Collections.synchronizedMap(new HashMap<String, OutputStream>());

            map.put(filename, out);

            exportStore.put(sessionId, map);
        } else {
            map.put(filename, out);

        }

    }

    public static OutputStream getExportRecord(String sessionId, String filename) {
        Map<String, OutputStream> m = exportStore.get(sessionId);
        if (m == null) return null;
        return m.get(filename);


    }


    public static void deleteAll(String sessionId) {
        exportStore.remove(sessionId);


    }


    public static void deleteExportRecord(String sessionId, String filename) {
        Map<String, OutputStream> m = exportStore.get(sessionId);
        if (m != null) m.remove(filename);


    }


    public static void clearStore() {
        exportStore.clear();


    }


}
