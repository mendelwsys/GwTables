package com.mycompany.client.security;

import com.google.gwt.http.client.RequestException;

/**
 * Created by Anton.Pozdnev on 19.02.2015.
 */
public interface IAuthenticateCallback {

    void authenticate(String user,String password);
}
