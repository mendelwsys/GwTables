package com.mycompany.client.integration;

import com.mycompany.client.integration.commands.ICommand;

import java.util.Map;

/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public interface IMessageReceiver extends ICallbackListener,IMessageManipulator{




    void setAcceptedMessageTypes(Map<String,ICommand> c);
    IMessageReceivedHandler getMessageReceivedHandler();
    void setMessageReceivedHandler(IMessageReceivedHandler handler);


}
