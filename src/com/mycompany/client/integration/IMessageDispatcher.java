package com.mycompany.client.integration;

/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public interface IMessageDispatcher extends IMessageSender/*,IMessageReceiver*/{



    IMessageSender getMessageSender();
    IMessageReceiver getMessageReceiver();
    IMessageReceivedHandler getMessageReceivedHandler();
    IIntegrationSettings getIntegrationSettings();
    void setIntegrationSettings(IIntegrationSettings settings);
    void setMessageSender(IMessageSender sender);
    void setMessageReceiver(IMessageReceiver receiver);
    void setMessageReceivedHandler(IMessageReceivedHandler handler);

}
