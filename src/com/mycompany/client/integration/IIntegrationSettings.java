package com.mycompany.client.integration;


import com.mycompany.client.integration.commands.ICommand;

import java.util.Map;

/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public interface IIntegrationSettings {
    Map<String,ICommand> getAcceptMessageTypesCollection();
    void addAcceptedMessageType(String messageType,ICommand command);
void removeAcceptedMessageType(String messageType);




}
