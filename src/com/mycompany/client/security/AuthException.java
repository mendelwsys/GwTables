package com.mycompany.client.security;

import com.google.gwt.json.client.JSONObject;


/**
 * Created by Anton.Pozdnev on 27.02.2015.
 */
public class AuthException extends Exception {
    public AuthException() {
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

//    public AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
//    }


    public static AuthException toAuthException(JSONObject obj)
    {
        AuthException auth =null;
       // GWT.log("A1");
            auth = new AuthException((""+obj.get("message")).replaceAll("[\"]",""));
       // GWT.log("A2");
       // JSONArray stacktrace = obj.get("stacktrace").isArray();
       // GWT.log("A3");
        return auth;
    }

    public static AuthException toAuthException(Throwable obj)
    {

        AuthException auth =null;
        auth = new AuthException(obj.getMessage());
        return auth;

    }

}
