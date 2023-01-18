package com.mycompany.client.integration;

import com.google.gwt.json.client.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public class MessageDispatcher implements IMessageDispatcher {

    private List<String> messageIds = null;
    private List<JSONObject> messages = null;
    IMessageReceiver messageReceiver = null;
    IMessageSender messageSender = null;
    //  IMessageReceivedHandler handler = null;
    IIntegrationSettings settings = null;
    String componentId = "";

    public MessageDispatcher(IMessageReceiver messageReceiver, IMessageSender messageSender, IMessageReceivedHandler handler, String componentId) {
        this();
        this.messageReceiver = messageReceiver;
        this.messageSender = messageSender;
        //  this.handler = handler;
        this.componentId = componentId;
        messageReceiver.setMessageReceivedHandler(handler);
    }


    public MessageDispatcher(IMessageReceivedHandler messageHandler) {
        this();
        componentId = "mdisp" + new Date().getTime();
        messageReceiver = new MessageReceiver();
        messageReceiver.setComponentId(componentId);
        messageSender = new MessageSender(componentId, messageReceiver);
        messageReceiver.setMessageReceivedHandler(messageHandler);


    }


    private MessageDispatcher() {
        messageIds = new LinkedList<String>();
        messages = new LinkedList<JSONObject>();


    }

    @Override
    public IMessageSender getMessageSender() {
        return messageSender;
    }

    @Override
    public IMessageReceiver getMessageReceiver() {
        return messageReceiver;
    }


    @Override
    public IMessageReceivedHandler getMessageReceivedHandler() {
        return messageReceiver.getMessageReceivedHandler();
    }

    @Override
    public IIntegrationSettings getIntegrationSettings() {
        return settings;
    }

    @Override
    public void setIntegrationSettings(IIntegrationSettings settings) {
        this.settings = settings;
        messageReceiver.setAcceptedMessageTypes(settings.getAcceptMessageTypesCollection());
    }

    @Override
    public void setMessageSender(IMessageSender sender) {
        this.messageSender = sender;
    }

    @Override
    public void setMessageReceiver(IMessageReceiver receiver) {
        this.messageReceiver = receiver;
    }

    @Override
    public void setMessageReceivedHandler(IMessageReceivedHandler handler) {
        messageReceiver.setMessageReceivedHandler(handler);
    }

    @Override
    public String positionOnObject(int predId) throws GISIntegrationException {
        return messageSender.positionOnObject(predId);
    }

    @Override
    public String getSelectedObjects() throws GISIntegrationException {
        return messageSender.getSelectedObjects();
    }

    @Override
    public String getUserAuth() throws GISIntegrationException {
        return messageSender.getUserAuth();
    }

    @Override
    public String positionOnObject(String requestId, int predId) throws GISIntegrationException {
        return messageSender.positionOnObject(requestId, predId);
    }

    @Override
    public String getSelectedObjects(String requestId) throws GISIntegrationException {
        return messageSender.getSelectedObjects(requestId);
    }

    @Override
    public String getUserAuth(String requestId) throws GISIntegrationException {
        return messageSender.getUserAuth(requestId);
    }

    @Override
    public void addCallbackListener(ICallbackListener listener) {

    }

    @Override
    public void removeCallbackListener(ICallbackListener listener) {

    }

    @Override
    public void setRequestIdGenerator(IRequestIdGenerator generator) {
        messageSender.setRequestIdGenerator(generator);
    }

    @Override
    public String positionOnEvents(String predId, String[] eventIds) throws GISIntegrationException {
        return messageSender.positionOnEvents(predId, eventIds);
    }

    @Override
    public String positionOnEvents(String[] eventTypes) throws GISIntegrationException {
        return messageSender.positionOnEvents(eventTypes);
    }

    @Override
    public void runGetSelectedObjectsPolling() {
        messageSender.runGetSelectedObjectsPolling();
    }

    @Override
    public void stopGetSelectedObjectsPolling() {
        messageSender.stopGetSelectedObjectsPolling();

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
    public void setRequestGenerator(IRequestIdGenerator generator) {
        messageSender.setRequestIdGenerator(generator);
    }

    @Override
    public IRequestIdGenerator getRequestIdGenerator() {
        return messageSender.getRequestIdGenerator();
    }
}
