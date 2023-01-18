package com.mycompany.client.integration;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.integration.commands.ICommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public class MessageReceiver implements IMessageReceiver {

    IMessageReceivedHandler handler = null;
    Map<String, ICommand> acceptedMessageTypes = new HashMap<String, ICommand>();
    String componentId = "";


    @Override
    public boolean canHandleMessage(JavaScriptObject obj) {

        JSONObject jsobject = new JSONObject(obj);
        String id = jsobject.get("Id").isString().stringValue();
        if (id==null) return false;
        MessageIdentifier mi = MessageIdentifier.getMessageIdentifier(id);
        if (mi.getSenderId()==null||mi.getMethodId()==null) return false;
        if (mi.getSenderId().equalsIgnoreCase(componentId) && acceptedMessageTypes.get(mi.getMethodId()) != null)
            return true;


        return false;
    }

    @Override
    public void setAcceptedMessageTypes(Map<String, ICommand> c) {
        acceptedMessageTypes.putAll(c);
    }

    @Override
    public String getComponentId() {
        return componentId;
    }

    @Override
    public void setComponentId(String id) {
        this.componentId = id;
    }


    @Override
    public IMessageReceivedHandler getMessageReceivedHandler() {
        return handler;
    }

    @Override
    public void setMessageReceivedHandler(IMessageReceivedHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSuccess(JavaScriptObject obj) {
        JSONObject jsonObj = new JSONObject(obj);
        String id = jsonObj.get("Id").isString().stringValue();
        MessageIdentifier mi = MessageIdentifier.getMessageIdentifier(id);
        final String methodId = mi.getMethodId();
        acceptedMessageTypes.get(methodId).execute(jsonObj);

        if (IMessageTypes.MESSAGE_GET_SELECTED_OBJECTS.equals(methodId))
            {handler.onGetSelectedObjectsMessage(jsonObj);}
        else if (IMessageTypes.MESSAGE_GET_USER_AUTH.equals(methodId))
            {handler.onUserAuthMessage(App01.GUI_STATE_DESC.getUser());}
        else if (IMessageTypes.MESSAGE_POSITION_ON_EVENTS.equals(methodId))
            {handler.onPositionOnEventsMessage(jsonObj);}
        else if (IMessageTypes.MESSAGE_POSITION_ON_OBJECT.equals(methodId))
            {handler.onPositionOnObjectMessage(jsonObj);}

//        switch(methodId)
//        {
//            case IMessageTypes.MESSAGE_GET_SELECTED_OBJECTS:{handler.onGetSelectedObjectsMessage(jsonObj); break;}
//            case IMessageTypes.MESSAGE_GET_USER_AUTH:{handler.onUserAuthMessage(App01.GUI_STATE_DESC.getUser()); break;}
//            case IMessageTypes.MESSAGE_POSITION_ON_EVENTS:{handler.onPositionOnEventsMessage(jsonObj); break;}
//            case IMessageTypes.MESSAGE_POSITION_ON_OBJECT:{handler.onPositionOnObjectMessage(jsonObj);break;}
//        }



    }

    @Override
    public void onFailure(JavaScriptObject objm, String errorText, String error) {
handler.onError(null,objm,errorText,error);
    }
}
