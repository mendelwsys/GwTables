package com.mycompany.common.analit2;


import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.util.JSOHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 23.10.14
 * Time: 15:05
 *
 */
public class IAnalisysDescImpl extends IAnalisysDescBaseImpl {
    public IAnalisysDescImpl(JavaScriptObject obj)
    {
        grpX = GrpDef.convertArray(JSOHelper.getAttributeAsJavaScriptObjectArray(obj, "grpX"));


        JavaScriptObject jsObject = JSOHelper.getAttributeAsJavaScriptObject(obj, "colName2Def");
        if (jsObject != null) {
            String[] props = JSOHelper.getProperties(jsObject);
            if (props != null)
                for (String prop : props) {
                    JavaScriptObject jsObject1 = JSOHelper.getAttributeAsJavaScriptObject(jsObject, prop);
                    colName2Def.put(prop, new ColDef(jsObject1));
                }
        }

        nNodes = NNode2Ext.convertArray(JSOHelper.getAttributeAsJavaScriptObjectArray(obj, "nNodes"), null);
    }

    public IAnalisysDescImpl() {
    }

    public IAnalisysDescImpl(List<GrpDef> grpX, Map<String, ColDef> colName2Def, List<NNode2> nNodes) {
        super(grpX, colName2Def, nNodes);
    }
}
