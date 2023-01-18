package com.mycompany.common.analit2;


import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 18.10.14
 * Time: 13:02
 * Узел описателя отчета
 */
public class NNode2 implements Serializable
{
    public static final String NNodeType="NNode";
    public static final String NVALType="NVAL";


    public String getTitle()
    {
        return title;
    }

    public NNode2 getParent() {
        return parent;
    }

    public String getColId() {
        return colId;
    }

    public String getVal() {
        return val;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public void setColId(String colId) {
        this.colId = colId;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String title;
    private String val;
    private String colId;

    public String getTblName() {
        return tblName;
    }

    public void setTblName(String tblName) {
        this.tblName = tblName;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    private String noDrill;
    private String filter;
    private String tblName;

    public boolean isRotate() {
        return rotate;
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    private boolean rotate=false;

    public Integer getColN() {
        return colN;
    }

    public void setColN(Integer colN) {
        this.colN = colN;
    }

    private Integer colN;

    private NNode2[] nodes;

    transient private NNode2 parent;

    public String getType() {
        return type;
    }

    private String type;

    public void setParent(NNode2 parent) {
        this.parent = parent;
    }



    public NNode2[] getNodes() {
        return nodes;
    }

    public void setNodes(NNode2[] nodes) {
        this.nodes = nodes;
    }

    public NNode2(){}


    public NNode2(String title, String val, String colId,String type,Integer colN,Boolean rotate,String tblName,String filter)
    {
        this.title = title;
        this.val = val;
        this.colId = colId;
        this.type=type;
        this.colN =colN;
        this.tblName=tblName;
        this.filter=filter;
        if (rotate!=null)
            this.rotate=rotate;
    }

    public NNode2(String title, String val, String colId, String type, Integer colN, Boolean rotate, String tblName, NNode2[] nodes, String filter) {
        this(title, val, colId, type, colN, rotate, tblName, filter);
        this.nodes = nodes;
        for (NNode2 node : this.nodes)
            node.setParent(this);
    }


//    public static NNode2[] convertArray(Object[] objects)
//    {
//        NNode2[] rv = new NNode2[objects.length];
//        for (int i = 0, objectsLength = objects.length; i < objectsLength; i++)
//            rv[i]=new NNode2((JavaScriptObject)objects[i]);
//        return rv;
//    }

    public String getNoDrill() {
        return noDrill;
    }

    public void setNoDrill(String noDrill) {
        this.noDrill = noDrill;
    }

}
