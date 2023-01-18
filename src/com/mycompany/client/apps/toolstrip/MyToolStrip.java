package com.mycompany.client.apps.toolstrip;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 09.09.14
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class MyToolStrip extends ToolStrip
{
    public void animateShow(Canvas canvas, AnimationEffect effect, String endsAt, String startFrom)
    {
        animateShow(canvas,effect,endsAt,startFrom,new AnimationCallback() {
            @Override
            public void execute(boolean earlyFinish) {
            }
        });
    }

    public void animateHide(Canvas canvas, AnimationEffect effect, String endsAt, String startFrom)
    {
        animateHide(canvas,effect,endsAt,startFrom,new AnimationCallback() {
            @Override
            public void execute(boolean earlyFinish) {
            }
        });

    }

    public native void animateShow(Canvas canvas, AnimationEffect effect, String endsAt, String startFrom,AnimationCallback callback) /*-{
    var effectJsObj = {
        effect:     effect.@com.smartgwt.client.types.AnimationEffect::getValue()(),
        endsAt:     endsAt,
        startFrom:  startFrom};
    var canvasJsObj = canvas.@com.smartgwt.client.widgets.Canvas::getOrCreateJsObj()();
    canvasJsObj.animateShow(effectJsObj,
            $entry(function(earlyFinish) {
                    earlyFinish = earlyFinish === undefined ? false : earlyFinish;
                    if(callback != null) callback.@com.smartgwt.client.widgets.AnimationCallback::execute(Z)(earlyFinish);
                })
    );
}-*/;


    public native void animateHide(Canvas canvas, AnimationEffect effect, String endsAt, String startFrom,AnimationCallback callback) /*-{
    var effectJsObj = {
        effect:     effect.@com.smartgwt.client.types.AnimationEffect::getValue()(),
        endsAt:     endsAt,
        startFrom:  startFrom};
    var canvasJsObj = canvas.@com.smartgwt.client.widgets.Canvas::getOrCreateJsObj()();
    canvasJsObj.animateHide(effectJsObj,
    $entry(function(earlyFinish) {
            earlyFinish = earlyFinish === undefined ? false : earlyFinish;
            if(callback != null) callback.@com.smartgwt.client.widgets.AnimationCallback::execute(Z)(earlyFinish);
        })
    );
}-*/;


    public MyToolStrip() {

               //animateShow();
    }



    public MyToolStrip(JavaScriptObject jsObj) {
        super(jsObj);
    }
}
