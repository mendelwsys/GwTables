package com.mycompany.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.integration.*;
import com.mycompany.client.integration.commands.DefaultAuthUserCommand;
import com.mycompany.client.integration.commands.ICommand;
import com.mycompany.common.security.IUser;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;

import java.util.Date;

/**
 * Created by Anton.Pozdnev on 05.03.2015.
 */
public class Authorizer implements IMessageReceivedHandler, IAuthenticateCallback {

    public static final long COOKIE_EXPIRE_PERIOD = 1000L * 60L * 60L * 24L * 31L;
    private static final String USER_COOKIE_NAME = "user";
    static IMessageDispatcher dispatcher = null;
    static IAuthorizerListener listener=null;

    public Authorizer()
    {
        setupMessageDispatcher();

    }

    public static void setUserCookie(String userjson)
    {

        try {
            // SC.say("Insetcookie "+userjson);
            Cookies.setCookie(USER_COOKIE_NAME, userjson, new Date(new Date().getTime() + COOKIE_EXPIRE_PERIOD));
            //  SC.say("Cookie must be set");
        } catch (Exception e) {
            //e.printStackTrace();
            SC.warn(e.getMessage());
        }
    }


    public static String getUserCookie()
    {
        return Cookies.getCookie(USER_COOKIE_NAME);
    }


    public void authorize(IAuthorizerListener l, boolean ignoreDefaultUser)
    {
        try {
            listener=l;

            // String usercookie = getUserCookie();
            ///  if (usercookie!=null) {
            // GWT.log("registering user from cookie = "+usercookie);
            //      registerWSUser(usercookie);
            //     listener.onAuthorized();
            //  }
            ///  else
               if (!ignoreDefaultUser)
                dispatcher.getUserAuth();
                else
                {
                    createAuthWindow(this);
                    w.show();

                }
        } catch (GISIntegrationException e) {
            e.printStackTrace();
        }
    }

    private void setupMessageDispatcher() {
        dispatcher = new MessageDispatcher(this);
        IIntegrationSettings settings = setupSettings();
        dispatcher.setIntegrationSettings(settings);
        dispatcher.setMessageReceivedHandler(this);
    }

    private IIntegrationSettings setupSettings() {
        IIntegrationSettings settings = new IntegrationSettings();
        settings.addAcceptedMessageType(IMessageTypes.MESSAGE_GET_USER_AUTH, new DefaultAuthUserCommand());
        return settings;
    }


    @Override
    public void onUserAuthMessage(IUser u) {
        //final  Boolean  authorized = false;
        if (App01.GUI_STATE_DESC.getUser() == null ) {
            if (getUserCookie()==null) {
                createAuthWindow(this);
                w.show();
            }
            else
            {
                registerWSUser(getUserCookie());
                listener.onAuthorized();
            }
        } else {
            //  setUserCookie(u.getJSONRepresentation());
           listener.onAuthorized();
        }
    }

    @Override
    public void onPositionOnEventsMessage(JSONObject response) {

    }

    @Override
    public void onGetSelectedObjectsMessage(JSONObject response) {

    }

    @Override
    public void onPositionOnObjectMessage(JSONObject response) {

    }

    Window w = null;

    @Override
    public void onError(ICommand command, JavaScriptObject objm, String errorText, String error) {
        JSONObject obj = new JSONObject(objm);
        System.out.println(obj + " " + errorText + " " + error);
        if (App01.GUI_STATE_DESC.getUser() == null) {
            if (getUserCookie()==null) {
                createAuthWindow(this);
                w.show();
            }
            else
            {
                registerWSUser(getUserCookie());
                listener.onAuthorized();
            }
        }
    }


    private Window createAuthWindow(IAuthenticateCallback c) {
        if (w == null) {
            w = AuthWindow.createAuthWindowWithDropdown(this);
        }

        return w;

    }

    @Override
    public void authenticate(String user, String password) {
/// Авторизоваться через веб-сервер
        try {
            authenticateViaServlet(user, password);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    private void authenticateViaServlet(String user, String password) throws RequestException {
        String url = GWT.getHostPageBaseURL() + "transport/auth?";
        url += "user=" + user + "&password=" + password;
        // GWT.log(GWT.getModuleBaseURL());
        // GWT.log(GWT.getHostPageBaseURL());
        // GWT.log(GWT.getModuleBaseForStaticFiles());


        final RequestBuilder builder = new RequestBuilder(
                RequestBuilder.GET, url);
        Request response = builder.sendRequest(null, new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                if (response.getStatusCode() == 200) {
                    // GWT.log(response.getText());
                    try {
                        setUserCookie(response.getText());
                        registerWSUser(response.getText());
                        w.close();
                        w.destroy();
                        w = null;
                        listener.onAuthorized();
                    } catch (Exception e) {
                        SC.warn(e.getMessage());
                    }
                } else
                {
                    showError(response.getText());
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                ((AuthWindow) w).setError(AuthException.toAuthException(exception));
            }
        });

    }


    private static void registerWSUser(String text) {

        try {
            JSONValue v = JSONParser.parseStrict(text);

            App01.GUI_STATE_DESC.setUser(DefaultAuthUserCommand.convertFromJSONToUserPrincipal(v.isObject()));
        } catch (Exception e) {
            SC.warn(e.toString());
            GWT.log(e.getMessage());
        }
    }
    private void showError(String text) {
        JSONValue v = null;
        try {
            v = JSONParser.parseStrict(text);
        } catch (Exception e) {
            GWT.log(e.toString());
        }
        if (v == null) return;
        ((AuthWindow) w).setError(AuthException.toAuthException(v.isObject()));
    }






}
