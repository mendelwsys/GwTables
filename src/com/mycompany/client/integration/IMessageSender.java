package com.mycompany.client.integration;

/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public interface IMessageSender extends IGISIntegrationProxy,IMessageManipulator{

    void setRequestGenerator(IRequestIdGenerator generator);
    IRequestIdGenerator getRequestIdGenerator();


}
