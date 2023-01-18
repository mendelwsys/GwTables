package com.mycompany.client.test.informer;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.apps.App.api.NewWarnInformer;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.viewer.DetailViewer;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 18.03.15
 * Time: 19:52
 *
 */
public class DetailViewerWithDesc extends DetailViewer
{

    public DescOperation getDescInformer()
    {
        descInformer.put(NewWarnInformer.CRD_LEFT,this.getLeft());
        descInformer.put(NewWarnInformer.CRD_TOP,this.getTop());

        return descInformer;
    }

    int intheight = 0;
    int intwidth = 0;

    public int getIntheight() {
        return intheight;
    }

    public void setIntheight(int intheight) {
        this.intheight = intheight;
    }

    public int getIntwidth() {
        return intwidth;
    }

    public void setIntwidth(int intwidth) {
        this.intwidth = intwidth;
    }
    public void setDescInformer(DescOperation descInformer) {
        this.descInformer = descInformer;
    }

    private DescOperation descInformer;

    public DetailViewerWithDesc() {
    }

    public DetailViewerWithDesc(JavaScriptObject jsObj) {
        super(jsObj);
        // final DetailViewerWithDesc obj = this;

    }



}
