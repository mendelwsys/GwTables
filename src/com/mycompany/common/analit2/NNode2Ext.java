package com.mycompany.common.analit2;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.util.JSOHelper;

/**
 * Created by Anton.Pozdnev on 02.09.2015.
 */
public class NNode2Ext extends NNode2 {

    public NNode2Ext(String title, String val, String colId, String type, Integer colN, Boolean rotate, String tblName, String filter) {
        super(title, val, colId, type, colN, rotate, tblName, filter);
    }

    public NNode2Ext(String title, String val, String colId, String type, Integer colN, Boolean rotate, String tblName, NNode2[] nodes, String filter) {
        super(title, val, colId, type, colN, rotate, tblName, nodes, filter);
    }

    public NNode2Ext(JavaScriptObject jobj, NNode2 parent) {
        setTitle(JSOHelper.getAttribute(jobj, "title"));
        setVal(JSOHelper.getAttribute(jobj, "val"));

        setColId(JSOHelper.getAttribute(jobj, "colId"));
        setType(JSOHelper.getAttribute(jobj, "type"));
        setTblName(JSOHelper.getAttribute(jobj, "tblName"));
        setFilter(JSOHelper.getAttribute(jobj, "filter"));
        setNoDrill(JSOHelper.getAttribute(jobj, "noDrill"));

        setColN(JSOHelper.getAttributeAsInt(jobj, "colN"));
        setRotate(JSOHelper.getAttributeAsBoolean(jobj, "rotate"));
        setParent(parent);

        JavaScriptObject[] objs = JSOHelper.getAttributeAsJavaScriptObjectArray(jobj, "nodes");
        if (objs != null)
            setNodes(convertArray(objs, this));
    }

    public NNode2Ext() {
        super();
    }

    public static NNode2[] convertArray(JavaScriptObject[] objects, NNode2 parent) {
        if (objects == null)
            return null;
        NNode2[] rv = new NNode2[objects.length];
        for (int i = 0, objectsLength = objects.length; i < objectsLength; i++)
            rv[i] = new NNode2Ext(objects[i], parent);
        return rv;
    }
}
