package com.mwlib.tablo.servlets;

import com.google.gson.GsonBuilder;
import com.mycompany.server.wsauthclient.AuthService;
import com.mycompany.server.wsauthclient.AuthServiceService;
import com.mycompany.server.wsauthclient.NsiException_Exception;
import com.mycompany.server.wsauthclient.UserInfo;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Created by Anton.Pozdnev on 24.02.2015.
 */
public class AuthorizationServlet extends HttpServlet {

    transient GsonBuilder jsonBuilder = null;
    AuthService service = null;

    @Override
    public void init() throws ServletException {
        super.init();
        jsonBuilder = new GsonBuilder();

        service = new AuthServiceService().getAuthService();

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String user = request.getParameter("user");
        String password = request.getParameter("password");
       // System.out.println("user=" + user);
      //  System.out.println("password=" + password);

        UserInfo ui = null;
        PrintWriter sout=null;
        response.setCharacterEncoding("UTF-8");
        try {
                 ui = service.login(user, password, 10);


                response.addCookie(new Cookie("user", "" + ui.getIdUser()));
                request.getSession().setAttribute("userInfo", ui);
                response.setStatus(HttpServletResponse.SC_OK);
              String jsonui= jsonBuilder.create().toJson(ui);
               sout= response.getWriter();
                sout.print(jsonui);


        } catch (NsiException_Exception e) {


            String jsonex= "{\"message\":\""+e.getMessage()+"\"}";

            sout= response.getWriter();

            sout.print(jsonex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        finally
        {
            if (sout!=null)
            {
                sout.flush();
                sout.close();

            }

        }

    }








}
