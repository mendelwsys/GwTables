package com.mycompany.client.test.Demo;

import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 28.05.14
 * Time: 14:35
 *
 */
public class MyRecord extends TreeNode
{

    public MyRecord() {
    }

    public MyRecord(JavaScriptObject jsObj) {
        super(jsObj);
    }

//    public MyRecord(String name) {
//        super(name);
//    }
//
//    public MyRecord(String name, TreeNode... children) {
//        super(name, children);
//    }
//
//    public String[] getAddInfo() {
//        return addInfo;
//    }
//
//    public void setAddInfo(String[] addInfo) {
//        this.addInfo = addInfo;
//    }
//
//    private String[] addInfo;



//    public MyRecord() {
//    }
//
//    public MyRecord(JavaScriptObject jsObj) {
//        super(jsObj);
//    }

//    public MyRecord(Map recordProperties) {
//        super(recordProperties);
//    }

    public String getRowStyle() {
        return rowStyle;
    }

    public void setRowStyle(String rowStyle) {
        this.rowStyle = rowStyle;
        this.setAttribute(TablesTypes.ROW_STYLE,rowStyle);
    }

    private String rowStyle;
}
