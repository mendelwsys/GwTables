package com.mycompany.client.integration;


/**
 * Created by Anton.Pozdnev on 16.02.2015.
 */
public class MessageIdentifier {

    String senderId = "";
    String methodId = "";

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    String requestId = "";


    public static MessageIdentifier getMessageIdentifier(String messageId) {

        MessageIdentifier mi = new MessageIdentifier();


        String[] delimitedString = messageId.split("[#]");
        mi.senderId = delimitedString[0];
        mi.methodId = delimitedString[2];
        mi.requestId = delimitedString[1];


        return mi;
    }


}
