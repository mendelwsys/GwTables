package com.mwlib.tablo.servlets;

import com.mycompany.server.export.ExportStore;
//import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * Created by Anton.Pozdnev on 31.03.2015.
 */
public class ExportServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        OutputStream sot = null;
        try {
            //String sessionId = request.getSession().getId();
            String sessionId = request.getParameter("sid");
            String filename = request.getParameter("filename");
            String format = request.getParameter("format");
            OutputStream os = ExportStore.getExportRecord(sessionId, filename);
            if (format.equalsIgnoreCase("xls")) {
                response.setContentType("application/vnd.ms-excel");
                filename = (request.getHeader("user-agent").contains("MSIE") || (request.getHeader("user-agent").contains("Trident") && request.getHeader("user-agent").contains("rv:"))) ? URLEncoder.encode(filename, "utf-8") :
                        filename;
//                        MimeUtility.encodeWord(filename);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");

                sot = response.getOutputStream();
                sot.write(((ByteArrayOutputStream) os).toByteArray());

                ExportStore.deleteExportRecord(sessionId, filename);
            }


        } catch (Exception e) {


        } finally {
            if (sot != null) {

                sot.flush();
                sot.close();
            }

        }


    }
}
