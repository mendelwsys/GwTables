package com.mycompany.client.integration;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by Anton.Pozdnev on 06.02.2015.
 */
public class WindowUtils {


    public static JavaScriptObject openWindowInCoordinates(String url, String windowName, String params, int x, int y) {

        return openWindowInCoordinatesInternal(url, windowName, params, x, y);
    }

    private static native JavaScriptObject openWindowInCoordinatesInternal(String url, String windowName, String params, int x, int y)/*-{
        var screenX = params.indexOf('screenX') > -1;
        var left = params.indexOf('left') > -1;
        var top = params.indexOf('top') > -1;
        var screenY = params.indexOf('screenY') > -1;
        var position = '';
        if (screenX == false) position += ' , screenX=' + x;
        if (top == false) position += ' , top=' + y;
        if (screenY == false) position += ' , screenY=' + y;
        if (left == false) position += ' , left=' + x;

        var myWindow = window.open(url, windowName, params + position);
        return myWindow;
    }-*/;
}
