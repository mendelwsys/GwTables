package com.mycompany.client.integration;

/**
 * Created by Anton.Pozdnev on 13.02.2015.
 */
public class MessageSender implements IMessageSender {

    IGISIntegrationProxy proxy = null;
    String componentId = "";
    IRequestIdGenerator generator=null;

    public MessageSender(String componentId,ICallbackListener defaultCallbackListener) {

        this.componentId = componentId;
        try {
            proxy = GISIntegrationProxy.getInstance();
            proxy.addCallbackListener(defaultCallbackListener);
            generator = new RequestIdGenerator();
        } catch (GISIntegrationException e) {
            System.out.println(e);
        }
    }


    @Override
    public String getComponentId() {
        return componentId;
    }

    @Override
    public void setComponentId(String id) {
        componentId = id;
    }

    @Override
    public String positionOnObject(int predId) throws GISIntegrationException {

        String requestId = generateRequestId(IMessageTypes.MESSAGE_POSITION_ON_OBJECT);

        return proxy.positionOnObject(requestId,predId);
    }

    @Override
    public String getSelectedObjects() throws GISIntegrationException {

        String requestId = generateRequestId(IMessageTypes.MESSAGE_GET_SELECTED_OBJECTS);
        return proxy.getSelectedObjects(requestId);
    }

    @Override
    public String getUserAuth() throws GISIntegrationException {
        String requestId = generateRequestId(IMessageTypes.MESSAGE_GET_USER_AUTH);
        return proxy.getUserAuth(requestId);
    }

    @Override
    public String positionOnObject(String requestId, int predId) throws GISIntegrationException {
        return proxy.positionOnObject(requestId, predId);
    }

    @Override
    public String getSelectedObjects(String requestId) throws GISIntegrationException {
        return proxy.getSelectedObjects(requestId);
    }

    @Override
    public String getUserAuth(String requestId) throws GISIntegrationException {
        return proxy.getUserAuth(requestId);
    }

    @Override
    public void addCallbackListener(ICallbackListener listener) {

    }

    @Override
    public void removeCallbackListener(ICallbackListener listener) {

    }

    @Override
    public void setRequestIdGenerator(IRequestIdGenerator generator) {
        proxy.setRequestIdGenerator(generator);
    }

    @Override
    public String positionOnEvents(String predId, String[] eventIds) throws GISIntegrationException {
        return proxy.positionOnEvents(predId,eventIds);
    }

    @Override
    public String positionOnEvents(String[] eventIds) throws GISIntegrationException {


        String requestId = generateRequestId(IMessageTypes.MESSAGE_POSITION_ON_EVENTS);
        return proxy.positionOnEvents(requestId,eventIds);
    }

    @Override
    public void runGetSelectedObjectsPolling() {
        proxy.runGetSelectedObjectsPolling();

    }

    @Override
    public void stopGetSelectedObjectsPolling() {
        proxy.stopGetSelectedObjectsPolling();
    }

    @Override
    public void setRequestGenerator(IRequestIdGenerator generator) {
        this.generator = generator;
    }

    @Override
    public IRequestIdGenerator getRequestIdGenerator() {
        return generator;
    }

    private synchronized String generateRequestId(String methodPostfix) {
        if (generator != null)
            return generator.generateRequestId(componentId)+"#"+methodPostfix;
        else
            return null;
    }
}
