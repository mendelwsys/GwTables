package com.mycompany.client.integration;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.mycompany.client.integration.commands.ICommand;
import com.mycompany.common.security.IUser;


/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public interface IMessageReceivedHandler {

    void onUserAuthMessage(IUser u);
    void onPositionOnEventsMessage(JSONObject response);
    void onGetSelectedObjectsMessage(JSONObject response);
    void onPositionOnObjectMessage(JSONObject response);
    void onError(ICommand command, JavaScriptObject objm, String errorText, String error);

}
