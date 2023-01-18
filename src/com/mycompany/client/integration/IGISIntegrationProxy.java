package com.mycompany.client.integration;

/**
 * Created by Anton.Pozdnev on 27.01.2015.
 */
public interface IGISIntegrationProxy {


    String positionOnObject(int predId) throws GISIntegrationException;

    String getSelectedObjects() throws GISIntegrationException;

    String getUserAuth() throws GISIntegrationException;


    String positionOnObject(String requestId, int predId) throws GISIntegrationException;

    String getSelectedObjects(String requestId) throws GISIntegrationException;

    String getUserAuth(String requestId) throws GISIntegrationException;

    void addCallbackListener(ICallbackListener listener);

    void removeCallbackListener(ICallbackListener listener);

    void setRequestIdGenerator(IRequestIdGenerator generator);

    public String positionOnEvents(String predId, String[] eventIds) throws GISIntegrationException;

    public String positionOnEvents(String[] eventIds) throws GISIntegrationException;

    void runGetSelectedObjectsPolling();

    void stopGetSelectedObjectsPolling();


}
