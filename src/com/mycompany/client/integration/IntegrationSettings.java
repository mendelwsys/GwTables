package com.mycompany.client.integration;


import com.mycompany.client.integration.commands.ICommand;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public class IntegrationSettings implements IIntegrationSettings {
    Map<String,ICommand> acceptedMessages = new TreeMap<String,ICommand>();



    @Override
    public Map<String,ICommand> getAcceptMessageTypesCollection() {
        return acceptedMessages;
    }

    @Override
    public void addAcceptedMessageType(String messageType,ICommand command) {

    acceptedMessages.put(messageType,command);
    }

    @Override
    public void removeAcceptedMessageType(String messageType) {
        if (acceptedMessages.containsKey(messageType))
            acceptedMessages.remove(messageType);
    }
}
