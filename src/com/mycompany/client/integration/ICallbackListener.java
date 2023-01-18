package com.mycompany.client.integration;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by Anton.Pozdnev on 28.01.2015.
 */
public interface ICallbackListener {

   void onSuccess(JavaScriptObject obj);

   void onFailure(JavaScriptObject objm,String errorText,String error);

    boolean canHandleMessage(JavaScriptObject obj);



}
