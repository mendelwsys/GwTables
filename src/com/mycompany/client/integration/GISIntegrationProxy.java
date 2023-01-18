package com.mycompany.client.integration;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Anton.Pozdnev on 27.01.2015.
 */
public class GISIntegrationProxy implements IGISIntegrationProxy {

    private static volatile IGISIntegrationProxy instance = null;
    static final Object lock = new Object();
    private List<ICallbackListener> listeners = null;
    private IRequestIdGenerator reqIdGenerator = null;

    private GISIntegrationProxy() {

        listeners = new ArrayList<ICallbackListener>();
        initAjax();
        reqIdGenerator = new RequestIdGenerator();
    }


    @Override
    public String positionOnObject(int predId) throws GISIntegrationException {
        String id = generateRequestId();
        try {

            positionOnObjectInternal(id, predId);
        } catch (Exception e) {
            // e.printStackTrace();
            throw new GISIntegrationException(e);
        }
        return id;
    }

    @Override
    public String getSelectedObjects() throws GISIntegrationException {
        String id = generateRequestId();
        try {
            getSelectedObjectsInternal(id);
        } catch (Exception e) {
            //  e.printStackTrace();
            throw new GISIntegrationException(e);
        }
        return id;
    }

    @Override
    public String getUserAuth() throws GISIntegrationException {
        String id = generateRequestId();
        try {
            getUserAuthInternal(id);
        } catch (Exception e) {
            //  e.printStackTrace();
            throw new GISIntegrationException(e);
        }
        return id;
    }

    @Override
    public String positionOnObject(String requestId, int predId) throws GISIntegrationException {
        try {

            positionOnObjectInternal(requestId, predId);
        } catch (Exception e) {
            // e.printStackTrace();
            throw new GISIntegrationException(e);
        }
        return requestId;
    }

    @Override
    public String getSelectedObjects(String requestId) throws GISIntegrationException {
        try {
            getSelectedObjectsInternal(requestId);
        } catch (Exception e) {
            //  e.printStackTrace();
            throw new GISIntegrationException(e);
        }
        return requestId;
    }

    @Override
    public String getUserAuth(String requestId) throws GISIntegrationException {
        try {
            getUserAuthInternal(requestId);
        } catch (Exception e) {
            //  e.printStackTrace();
            throw new GISIntegrationException(e);
        }
        return requestId;
    }


    @Override
    public String positionOnEvents(String requestId, String[] eventIds) throws GISIntegrationException {
        List<String []> al = splitArray(eventIds);
        try {
            JsArrayString arr = toJsArray((String [])al.get(0));
            JsArrayString arr2 = toJsArray((String [])al.get(1));
            positionOnEventsInternal(requestId, arr2,arr);
        } catch (Exception e) {
            // e.printStackTrace();
            throw new GISIntegrationException(e);
        }
        return requestId;
    }

    private List<String[]> splitArray(String[] eventIds) {
        List<String []> al = new ArrayList<String []>();
        String [] ids= new String [eventIds.length];
        String [] types =  new String [eventIds.length];
        for (int i=0;i<eventIds.length;i++)
        {
            String [] splitted = eventIds[i].split("##");
            ids [i] = splitted[0];
            types[i] = splitted[1];
        }

        al.add(ids);
        al.add(types);
        return al;
    }

    @Override
    public String positionOnEvents(String[] eventIds) throws GISIntegrationException {
        String id = generateRequestId();
        List<String []> al = splitArray(eventIds);
        try {

            JsArrayString arr = toJsArray((String [])al.get(0));
            JsArrayString arr2 = toJsArray((String [])al.get(1));
            // System.out.println(arr);
            positionOnEventsInternal(id,arr2, arr);
        } catch (Exception e) {
            // e.printStackTrace();
            throw new GISIntegrationException(e);
        }
        return id;
    }


    boolean selectedObjectsPollingRun = true;
    Timer polling = null;

    @Override
    public void runGetSelectedObjectsPolling() {
        selectedObjectsPollingRun = true;
        polling = new Timer() {


            @Override
            public void run() {
                try {
                    getSelectedObjects();
                } catch (GISIntegrationException e) {
                    e.printStackTrace();
                }
            }
        };
        polling.scheduleRepeating(1000);


    }

    @Override
    public void stopGetSelectedObjectsPolling() {
        if (polling != null) //TODO NEW // && polling.isRunning())
        {
            polling.cancel();
            polling = null;

        }


    }


    private native String getUserAuthInternal(String reqId) throws JavaScriptException/*-{

        var success = false;
        var d1 = {"func":"getUserAuth","Id":reqId};
        var jqXHR = $wnd.$.ajax({
                data: d1,
                beforeSend: function (jqXHR)
                        {
                            jqXHR.myData = d1;
                         }
            }
        ).done(function (e) {
                //console.log("done");
                //console.log(e.responseText);
                success = true;
                clearTimeout(errorTimeout);
                //   console.log(e);
                $wnd.GISonSuccess(e);


                //console.log(e.status);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                success = true;
                clearTimeout(errorTimeout);
                //  console.log("fail");
                //  console.log(textStatus);
                //  console.log(errorThrown);
                //  console.log(jqXHR);

                $wnd.GISonFailure(jqXHR.myData, textStatus, errorThrown);

                // console.log(e);
                //   console.log(e.status);
            });

        var errorTimeout = setTimeout(function() {
            if (!success)
            {
                // Handle error accordingly
                $wnd.GISonFailure(jqXHR.myData, "Connection refused", "Connection Refused");
                //alert("!"+JSON.stringify(jqXHR.myData));
            }
        }, 3000);
    }-*/;

    @Override
    public void addCallbackListener(ICallbackListener listener) {

        if (!listeners.contains(listener)) listeners.add(listener);


    }

    @Override
    public void removeCallbackListener(ICallbackListener listener) {
        if (listeners.contains(listener)) listeners.remove(listener);


    }

    @Override
    public void setRequestIdGenerator(IRequestIdGenerator generator) {
        reqIdGenerator = generator;
    }


    public static IGISIntegrationProxy getInstance() throws GISIntegrationException {
        if (instance == null) {
            synchronized (lock) {
                instance = new GISIntegrationProxy();
            }
        }

        return instance;
        //return null;
    }


    //Не используется, оставил как пример реализации callbackов
    private native void initGISConnection() throws JavaScriptException/*-{
        (function ($) {
            try {
                $wnd.$.fn.initializeTabloCli = function (options) {
                    var defaultOptions = { dataType: "jsonp", url: "http://127.0.0.1:15789/", crossDomain: true, timeout: 3000 };
                    var error = "";
                    try {
                        var jqxhr = $wnd.$.ajax($wnd.$.extend({}, defaultOptions, options)).done(function (e) {
                            $.fn.invokeTabloCli = function (options) {
                                var jqxhr = $wnd.$.ajax($wnd.$.extend({}, defaultOptions, options)).done(function (e) {
                                    // console.log("invoke done");
                                    if (options.onsuccess) options.onsuccess(e);
                                    //  console.log(e);
                                }).fail(function (e) {
                                    //  console.log("invoke fail");
                                    if (options.onerror) options.onerror(e);
                                    //   console.log(e);
                                    throw e.status + " " + e.responseText;
                                });
                            };

                            if (options.onsuccess) options.onsuccess(e);

                        }).fail(function (e) {

                            //  console.log("init failed");

                            if (e) {
                                //   console.log(e);
                                //   console.log(e.status);
                                //  console.log(e.responseText);
                                error = "Connection Error 33";
                                throw "Connection error";
                            }
                            else {
                                // throw "Connection error";
                            }
                        });
                        if (error.length > 0) throw "Connection Error 44";

                    }
                    catch (e) {
                        // console.log("catched e");
                        throw e;
                    }

                }
            }
            catch (e2) {
                // console.log("catched e2");
                throw e2;

            }

        })($wnd.jQuery);

    }-*/;


    private native void positionOnObjectInternal(String reqId, int predId) throws JavaScriptException/*-{


        //  var defaultOptions = { dataType: "jsonp", url: "http://127.0.0.1:15789/", crossDomain: true, timeout: 3000 };
        //  var jqxhr = $wnd.$.ajax($wnd.$.extend({}, defaultOptions, {
        //           data: { PredId: predId }
        //        })
        //    );


        $wnd.$.ajax({
                data: { PredId: predId, Id: reqId}
            }
        ).done(function (e) {
                //console.log("done");
                //console.log(e.responseText);
                // console.log(e);
                $wnd.GISonSuccess(e);


                //console.log(e.status);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                //  console.log("fail");
                // console.log(textStatus);
                // console.log(errorThrown);
                //  console.log(jqXHR);

                $wnd.GISonFailure(jqXHR, textStatus, errorThrown);

                // console.log(e);
                //   console.log(e.status);
            });


    }-*/;


    private native void getSelectedObjectsInternal(String reqId) throws JavaScriptException/*-{


        // var defaultOptions = { dataType: "jsonp", url: "http://127.0.0.1:15789/", crossDomain: true, timeout: 3000 };
        // var jqxhr = $wnd.$.ajax($wnd.$.extend({}, defaultOptions, {
        //         data: { Func: getSelectedObjects }
        //     })
        //  );


        $wnd.$.ajax({
                data: { func: "getSelectedObjects", Id: reqId }
            }
        ).done(function (e) {
                // console.log("done");
                // console.log(e);

                $wnd.GISonSuccess(e);

                // console.log(e);
                // console.log(e.status);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                // console.log("fail");
                // console.log(textStatus);
                ////  console.log(errorThrown);
                //  console.log(jqXHR);

                $wnd.GISonFailure(jqXHR, textStatus, errorThrown);

                // console.log(jqXHR);
                //console.log(jqXHR.status);
            });


    }-*/;


    private native void positionOnEventsInternal(String reqId,JsArrayString eventTypes, JsArrayString eventIds) throws JavaScriptException/*-{
        //console.log(eventIds);


        var add = {
            "callback": "jQuery111205435312066692859_" + new Date().getTime(),
            "_": "" + new Date().getTime(),
            func: "positionOnEvents",
            Id: reqId
        };
        // console.log(add);
        //var arr = $wnd.JSON.stringify(eventIds);
        var events = {"events": []};

        for (var i = 0, l = eventTypes.length; i < l; i++) {
            events.events.push({
                "objectId": eventIds[i],
                "dataType": eventTypes[i]
            });
        }

        //  console.log(arr);
        //  var arr2 = {"eventIds": arr};
        var overall = $wnd.$.extend(events, add);
        //console.log(overall);
        var contentType = "application/x-www-form-urlencoded; charset=utf-8";
        //  if($wnd.window.XDomainRequest)
        //      contentType = "text/plain";
        $wnd.$.support.cors = true;


        if (window.XDomainRequest) {
            var xdr = new XDomainRequest();


            xdr.open("get", "http://127.0.0.1:15789/?callback=jQuery111205435312066692859_" + new Date().getTime() + "&_=" + new Date().getTime() + "&func=positionOnEvents&Id=12345&" + $wnd.$.param(events));

            xdr.ontimeout = function () {
                /// alert("ontimeout");
            };

            xdr.onerror = function () {
                // alert("error");
            };

            xdr.onload = function () {
                /// alert("onload");
            }
            xdr.send();
        }
        else {

            $wnd.$.ajax({
                url: "http://127.0.0.1:15789/",
                type: "POST",
                    dataType: "json",
                //    jsonp: false,
                contentType: contentType,

                data: overall
                    //       beforeSend: function (request)
                    //         {
                    //            request.setRequestHeader("Origin", "");
                    //           }

            }
        ).done(function (e) {


                $wnd.GISonSuccess(e);


            }).fail(function (jqXHR, textStatus, errorThrown) {


                $wnd.GISonFailure(jqXHR, textStatus, errorThrown);


            });
        }


    }-*/;


    private native void initAjax() throws JavaScriptException/*-{

        $wnd.$.ajaxSetup({
            url: "http://127.0.0.1:15789/",
            dataType: 'jsonp',
            // type:"POST",
            contentType: "text/javascript; charset=utf-8",
            //  contentType: "application/x-www-form-urlencoded;charset=UTF-8",
            timeout: 3000
            //  async:false

            //  beforeSend: function (request)
            //   {
            //       request.setRequestHeader("Access-Control-Allow-Origin", "127.0.0.1:8888");
            //   }


        });

        function sleep(ms) {
            ms += new Date().getTime();
            while (new Date() < ms) {
            }
        }

        var that = this;
        $wnd.GISonSuccess = $entry(function (e) {
            that.@com.mycompany.client.integration.GISIntegrationProxy::onSuccess(Lcom/google/gwt/core/client/JavaScriptObject;)(e)
        });
        $wnd.GISonFailure = $entry(function (jqXHR, textStatus, errorThrown) {
            that.@com.mycompany.client.integration.GISIntegrationProxy::onFailure(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Ljava/lang/String;)(jqXHR, textStatus, errorThrown)
        });


    }-*/;


    public void onSuccess(JavaScriptObject obj) {
        JSONObject json = new JSONObject(obj);
        //String requestId = json.get("Id").isString().stringValue();
       // System.out.println(obj);
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i).canHandleMessage(obj))
                listeners.get(i).onSuccess(obj);
        }
    }

    public void onFailure(JavaScriptObject response, String errorText, String error) {
        System.out.println(response);
        System.out.println(errorText);
        System.out.println(error);
        for (int i = 0; i < listeners.size(); i++) {
            if (listeners.get(i).canHandleMessage(response))
                listeners.get(i).onFailure(response, errorText, error);
        }

    }


    private String generateRequestId() {
        if (reqIdGenerator != null)
            return reqIdGenerator.generateRequestId("");
        else
            return null;
    }


    private JsArrayString toJsArray(String[] input) {
        JsArrayString jsArrayString = JsArrayString.createArray().cast();
        for (String s : input) {
            jsArrayString.push(s);
        }
        return jsArrayString;
    }
}
